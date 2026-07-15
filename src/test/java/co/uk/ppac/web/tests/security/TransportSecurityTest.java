package co.uk.ppac.web.tests.security;

import co.uk.ppac.core.config.ConfigReader;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.util.Timeout;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * TC_034 — HTTP→HTTPS redirect + HSTS header check.
 * Uses Apache HttpClient because Selenium does not expose response headers directly.
 */
public class TransportSecurityTest {

    private static final RequestConfig NO_REDIRECT = RequestConfig.custom()
            .setRedirectsEnabled(false)
            .setResponseTimeout(Timeout.ofSeconds(15))
            .build();

    @Test(description = "TC_034 — HTTP redirect to HTTPS + HSTS header",
          groups = {"security", "transport"})
    public void testHttpRedirectsToHttpsWithHsts() throws Exception {
        URI httpsUri = URI.create(ConfigReader.get("app.baseUrl"));
        String host = httpsUri.getHost();
        URI httpUri = URI.create("http://" + host + "/");

        try (CloseableHttpClient client = HttpClientBuilder.create()
                .setDefaultRequestConfig(NO_REDIRECT)
                .build()) {

            HttpHead httpRequest = new HttpHead(httpUri);
            try (ClassicHttpResponse response = client.executeOpen(null, httpRequest, null)) {
                int code = response.getCode();
                assertTrue(code == 301 || code == 302 || code == 308,
                        "HTTP request phải redirect sang HTTPS với 301/302/308, actual=" + code);
                Header location = response.getFirstHeader("Location");
                assertNotNull(location, "Redirect response phải có Location header");
                assertTrue(location.getValue().toLowerCase().startsWith("https://"),
                        "Location header phải redirect sang HTTPS, actual=" + location.getValue());
            }

            HttpHead httpsRequest = new HttpHead(httpsUri);
            try (ClassicHttpResponse response = client.executeOpen(null, httpsRequest, null)) {
                Header hsts = response.getFirstHeader("Strict-Transport-Security");
                assertNotNull(hsts, "Response HTTPS phải có header Strict-Transport-Security (HSTS) — REQ-15");
                assertTrue(hsts.getValue().toLowerCase().contains("max-age"),
                        "HSTS header phải có 'max-age' directive, actual=" + hsts.getValue());
            }
        }
    }
}
