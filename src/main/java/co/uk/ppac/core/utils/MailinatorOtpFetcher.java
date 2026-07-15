package co.uk.ppac.core.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fetches a one-time OTP from a Mailinator public inbox via its REST API.
 *
 * <p>We switched here from Yopmail because Yopmail soft-blocks any fresh
 * browser session (returning {@code finrmail(-1,...)} stubs), and bypassing
 * it requires either a long-warmed Chrome profile or paid scraping
 * infrastructure. Mailinator's public API returns inbox + message bodies as
 * plain JSON with no authentication.
 *
 * <p>Resolution order on each poll:
 * <ol>
 *   <li>{@code -Dotp.code=XXXXXX} system property — manual override</li>
 *   <li>{@code target/current_signup_otp.txt} file — manual override via file</li>
 *   <li>Mailinator public REST API</li>
 * </ol>
 */
public final class MailinatorOtpFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailinatorOtpFetcher.class);
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final Pattern OTP_PATTERN = Pattern.compile("\\b(\\d{6})\\b");
    private static final String INBOX_URL = "https://api.mailinator.com/api/v2/domains/public/inboxes/";
    private static final int DEFAULT_TIMEOUT_SEC = 60;
    private static final int POLL_INTERVAL_MS = 4_000;

    public static final String OTP_OVERRIDE_PROPERTY = "otp.code";
    public static final java.nio.file.Path OTP_OVERRIDE_FILE =
            java.nio.file.Paths.get("target", "current_signup_otp.txt");

    private final HttpClient http;

    public MailinatorOtpFetcher() {
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public String fetchLatestOtp(String mailinatorInbox) {
        return fetchLatestOtp(mailinatorInbox, DEFAULT_TIMEOUT_SEC);
    }

    public String fetchLatestOtp(String mailinatorInbox, int timeoutSec) {
        String propOverride = System.getProperty(OTP_OVERRIDE_PROPERTY);
        if (propOverride != null && propOverride.matches("\\d{6}")) {
            LOGGER.info("Using OTP from -D{}={}", OTP_OVERRIDE_PROPERTY, propOverride);
            return propOverride;
        }

        long deadline = System.currentTimeMillis() + timeoutSec * 1000L;
        while (System.currentTimeMillis() < deadline) {
            String fileOtp = readOverrideFile();
            if (fileOtp != null) {
                return fileOtp;
            }
            try {
                String otp = tryApiFetch(mailinatorInbox);
                if (otp != null) {
                    return otp;
                }
            } catch (Exception e) {
                LOGGER.debug("Mailinator API fetch attempt failed: {}", e.getMessage());
            }
            try {
                Thread.sleep(POLL_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        throw new IllegalStateException("No OTP retrieved for inbox '" + mailinatorInbox + "' within "
                + timeoutSec + "s. Tried (1) -D" + OTP_OVERRIDE_PROPERTY + "=XXXXXX, "
                + "(2) file " + OTP_OVERRIDE_FILE.toAbsolutePath() + ", "
                + "(3) Mailinator public API. To unblock manually, open "
                + "https://www.mailinator.com/v4/public/inboxes.jsp?to=" + mailinatorInbox
                + " and write the OTP to " + OTP_OVERRIDE_FILE.toAbsolutePath() + " before rerun.");
    }

    private String readOverrideFile() {
        if (!java.nio.file.Files.exists(OTP_OVERRIDE_FILE)) {
            return null;
        }
        try {
            String content = java.nio.file.Files.readString(OTP_OVERRIDE_FILE).trim();
            if (!content.matches("\\d{6}")) {
                return null;
            }
            LOGGER.info("Using OTP from file '{}': {}", OTP_OVERRIDE_FILE, content);
            try { java.nio.file.Files.delete(OTP_OVERRIDE_FILE); } catch (Exception ignored) { }
            return content;
        } catch (Exception e) {
            LOGGER.warn("Failed to read OTP override file: {}", e.getMessage());
            return null;
        }
    }

    private String tryApiFetch(String inbox) throws Exception {
        HttpRequest listReq = HttpRequest.newBuilder()
                .uri(URI.create(INBOX_URL + inbox))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        HttpResponse<String> listResp = http.send(listReq, HttpResponse.BodyHandlers.ofString());
        if (listResp.statusCode() != 200) {
            LOGGER.debug("Mailinator inbox list returned {}", listResp.statusCode());
            return null;
        }
        JsonNode root = JSON.readTree(listResp.body());
        JsonNode msgs = root.path("msgs");
        if (!msgs.isArray() || msgs.isEmpty()) {
            LOGGER.info("Mailinator inbox '{}': 0 message(s)", inbox);
            return null;
        }

        JsonNode latest = null;
        long latestTime = Long.MIN_VALUE;
        for (JsonNode msg : msgs) {
            long t = msg.path("time").asLong(0);
            if (t > latestTime) {
                latestTime = t;
                latest = msg;
            }
        }
        String msgId = latest.path("id").asText();
        HttpRequest bodyReq = HttpRequest.newBuilder()
                .uri(URI.create(INBOX_URL + inbox + "/messages/" + msgId))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        HttpResponse<String> bodyResp = http.send(bodyReq, HttpResponse.BodyHandlers.ofString());
        if (bodyResp.statusCode() != 200) {
            LOGGER.debug("Mailinator message body returned {}", bodyResp.statusCode());
            return null;
        }
        JsonNode bodyRoot = JSON.readTree(bodyResp.body());
        StringBuilder allText = new StringBuilder();
        allText.append(bodyRoot.path("subject").asText("")).append('\n');
        for (JsonNode part : bodyRoot.path("parts")) {
            allText.append(part.path("body").asText("")).append('\n');
        }
        String visibleText = stripHtml(allText.toString());
        Matcher matcher = OTP_PATTERN.matcher(visibleText);
        if (!matcher.find()) {
            LOGGER.info("No 6-digit OTP in visible text of msg {} for inbox '{}'", msgId, inbox);
            return null;
        }
        String otp = matcher.group(1);
        LOGGER.info("Retrieved OTP {} for inbox '{}' (msg {})", otp, inbox, msgId);
        return otp;
    }

    private static String stripHtml(String html) {
        return html
                .replaceAll("(?is)<style[^>]*>.*?</style>", " ")
                .replaceAll("(?is)<script[^>]*>.*?</script>", " ")
                .replaceAll("(?s)<[^>]+>", " ");
    }
}
