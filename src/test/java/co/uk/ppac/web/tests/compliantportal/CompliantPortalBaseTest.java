package co.uk.ppac.web.tests.compliantportal;

import co.uk.ppac.core.config.ConfigReader;
import co.uk.ppac.core.driver.DriverFactory;
import co.uk.ppac.web.pages.VerifierQueuePage;
import co.uk.ppac.web.utils.CompliantPortalSession;
import org.openqa.selenium.WebDriver;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/**
 * Base test cho Compliant Portal (Verifier side) — share driver + login 1 lần cho cả suite.
 * Mỗi method navigate lại landing để reset filter state.
 */
public abstract class CompliantPortalBaseTest {

    private static WebDriver SHARED_DRIVER;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (SHARED_DRIVER != null) {
                try {
                    SHARED_DRIVER.quit();
                } catch (Exception ignored) {
                }
                SHARED_DRIVER = null;
            }
        }));
    }

    protected WebDriver driver;
    protected VerifierQueuePage queue;

    @BeforeClass(alwaysRun = true)
    public void classLogin() {
        if (SHARED_DRIVER == null) {
            RuntimeException lastFailure = null;
            // Login UAT (Flutter) đôi khi flaky — thử tối đa 2 lần trước khi bỏ cuộc.
            for (int attempt = 1; attempt <= 2 && SHARED_DRIVER == null; attempt++) {
                WebDriver candidate = DriverFactory.create();
                try {
                    CompliantPortalSession.loginAsAdmin(candidate);
                    SHARED_DRIVER = candidate;
                } catch (RuntimeException loginFailure) {
                    lastFailure = loginFailure;
                    try {
                        candidate.quit();
                    } catch (Exception ignored) {
                    }
                }
            }
            if (SHARED_DRIVER == null && lastFailure != null) {
                throw lastFailure;
            }
        }
        driver = SHARED_DRIVER;
    }

    @BeforeMethod(alwaysRun = true)
    public void openLandingFresh() {
        if (driver == null) {
            throw new SkipException("Skip: @BeforeClass classLogin failed — không có driver để chạy test method này.");
        }
        driver.get(ConfigReader.get("app.baseUrl"));
        queue = new VerifierQueuePage(driver);
        queue.waitForFlutterReady();
        queue.waitForFilterBarReady();
        queue.waitForTableReady();
    }
}
