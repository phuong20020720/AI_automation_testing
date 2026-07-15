package co.uk.ppac.web.tests.workermanagement;

import co.uk.ppac.core.driver.DriverFactory;
import co.uk.ppac.web.pages.WorkerListPage;
import co.uk.ppac.web.utils.WorkerSessionHelper;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/**
 * Base test cho module Worker Management — share 1 driver + login 1 lần
 * cho TOÀN suite (xuyên các class). Lý do: Flutter login pipeline UAT
 * bị backend rate-limit khi nhiều test class cùng login trong khoảng thời gian
 * ngắn. Dùng static shared driver + JVM shutdown hook để cleanup.
 *
 * Mỗi test method navigate lại về list URL ở `@BeforeMethod` để đảm bảo
 * test independence (state list được reset).
 */
public abstract class WorkerManagementBaseTest {

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
    protected WorkerListPage list;

    @BeforeClass(alwaysRun = true)
    public void classLogin() {
        if (SHARED_DRIVER == null) {
            WebDriver candidate = DriverFactory.create();
            try {
                WorkerSessionHelper.loginAsStgeo(candidate);
            } catch (RuntimeException loginFailure) {
                // Login thất bại (Flutter UAT rate-limit / timeout) — KHÔNG để driver "poisoned"
                // (non-null nhưng chưa login) làm skip toàn suite. Quit + để class sau retry sạch.
                try {
                    candidate.quit();
                } catch (Exception ignored) {
                }
                throw loginFailure;
            }
            SHARED_DRIVER = candidate;
        }
        driver = SHARED_DRIVER;
    }

    @BeforeMethod(alwaysRun = true)
    public void openListFresh() {
        // Clear filter state lưu trong sessionStorage/localStorage (theo SPA logic),
        // sau đó hard reload qua location.reload(true) để force re-render với clean state.
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        try {
            js.executeScript(
                    "try { Object.keys(sessionStorage).forEach(function(k){"
                            + " if (k && k.toLowerCase().indexOf('filter')>=0) sessionStorage.removeItem(k);"
                            + "}); } catch(e){}"
                            + "try { Object.keys(localStorage).forEach(function(k){"
                            + " if (k && k.toLowerCase().indexOf('filter')>=0) localStorage.removeItem(k);"
                            + "}); } catch(e){}");
        } catch (Exception ignored) {
        }
        String cleanUrl = "https://"
                + co.uk.ppac.core.config.ConfigReader.get("app.dashboardHost")
                + co.uk.ppac.core.config.ConfigReader.get("app.workerManagementPath");
        driver.get(cleanUrl);
        try { Thread.sleep(800); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
        list = new WorkerListPage(driver);
        list.waitForListReady(45);
    }
}
