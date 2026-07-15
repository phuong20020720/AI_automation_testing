package co.uk.ppac.core.listeners;

import java.io.IOException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sends a one-way message to Telegram. Bot token + chat id are resolved (highest
 * priority first) from:
 * <ol>
 *   <li>JVM system property {@code -DTELEGRAM_BOT_TOKEN} / {@code -DTELEGRAM_CHAT_ID}</li>
 *   <li>Environment variable {@code TELEGRAM_BOT_TOKEN} / {@code TELEGRAM_CHAT_ID}</li>
 *   <li>{@code .claude/telegram.local.json} found by walking up from the working dir</li>
 * </ol>
 * When no credentials are found the call is a no-op - tests never fail because of
 * a missing Telegram setup.
 */
final class TelegramNotifier {

    private static final Pattern BOT_TOKEN = Pattern.compile("\"botToken\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern CHAT_ID = Pattern.compile("\"chatId\"\\s*:\\s*\"?([^\",}\\s]+)\"?");

    private TelegramNotifier() {
    }

    static void send(String message) {
        String botToken = resolve("TELEGRAM_BOT_TOKEN", BOT_TOKEN);
        String chatId = resolve("TELEGRAM_CHAT_ID", CHAT_ID);

        if (isBlank(botToken) || isBlank(chatId)) {
            System.out.println("[Telegram] Chua cau hinh botToken/chatId - bo qua notify.");
            return;
        }

        try {
            String body = "chat_id=" + URLEncoder.encode(chatId, StandardCharsets.UTF_8)
                    + "&text=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.telegram.org/bot" + botToken + "/sendMessage"))
                    .timeout(Duration.ofSeconds(15))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("[Telegram] Da gui ket qua test.");
            } else {
                System.out.println("[Telegram] Gui that bai (HTTP " + response.statusCode() + "): " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("[Telegram] Gui that bai: " + e.getMessage());
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static String resolve(String key, Pattern jsonPattern) {
        String fromProperty = System.getProperty(key);
        if (!isBlank(fromProperty)) {
            return fromProperty.trim();
        }
        String fromEnv = System.getenv(key);
        if (!isBlank(fromEnv)) {
            return fromEnv.trim();
        }
        return fromJson(jsonPattern);
    }

    private static String fromJson(Pattern pattern) {
        Path config = locateConfig();
        if (config == null) {
            return null;
        }
        try {
            String content = Files.readString(config, StandardCharsets.UTF_8);
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        } catch (IOException e) {
            System.out.println("[Telegram] Khong doc duoc " + config + ": " + e.getMessage());
        }
        return null;
    }

    /** Walks up from the working directory looking for {@code .claude/telegram.local.json}. */
    private static Path locateConfig() {
        Path dir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        while (dir != null) {
            Path candidate = dir.resolve(".claude").resolve("telegram.local.json");
            if (Files.exists(candidate)) {
                return candidate;
            }
            dir = dir.getParent();
        }
        return null;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
