package co.uk.ppac.web.tests.session;

import co.uk.ppac.core.base.WebBaseTest;
import co.uk.ppac.core.config.ConfigReader;
import co.uk.ppac.web.pages.DashboardPage;
import co.uk.ppac.web.pages.LoginPage;
import org.openqa.selenium.Cookie;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class LogoutTest extends WebBaseTest {

    private DashboardPage loginAndReachDashboard() {
        LoginPage login = new LoginPage(driver).open(ConfigReader.get("app.baseUrl")).chooseEmailMethod();
        login.typeEmail(ConfigReader.get("login.test.email"));
        login.typePassword(ConfigReader.get("login.test.password"));
        login.submit();
        DashboardPage dashboard = new DashboardPage(driver);
        if (!dashboard.waitForDashboard(20)) {
            throw new SkipException("Cannot reach dashboard — login probably failed. URL=" + driver.getCurrentUrl());
        }
        return dashboard;
    }

    @Test(description = "TC_028 — Logout phải invalidate session", groups = {"logout", "session"})
    public void testLogoutInvalidatesSession() {
        DashboardPage dashboard = loginAndReachDashboard();

        List<Cookie> sessionCookiesBefore = driver.manage().getCookies().stream()
                .filter(c -> !c.getName().startsWith("_ga"))
                .filter(c -> !c.getName().startsWith("_fw_"))
                .filter(c -> {
                    String n = c.getName().toLowerCase();
                    return n.contains("session") || n.contains("auth") || n.contains("token") || n.contains("jwt");
                })
                .filter(c -> !c.getName().equalsIgnoreCase("first_session"))
                .toList();
        assertFalse(sessionCookiesBefore.isEmpty(),
                "Trước logout phải có session cookie do app set. Cookies hiện có: "
                        + driver.manage().getCookies().stream().map(Cookie::getName).toList());

        dashboard.logout();
        sleep(2500);

        // Sau logout: cookie session phải bị xóa hoặc set expired
        List<Cookie> sessionCookiesAfter = driver.manage().getCookies().stream()
                .filter(c -> sessionCookiesBefore.stream().anyMatch(b -> b.getName().equals(c.getName())))
                .toList();
        assertTrue(sessionCookiesAfter.isEmpty(),
                "Sau logout, cookie session phải bị clear/expired. Còn lại: "
                        + sessionCookiesAfter.stream().map(Cookie::getName).toList());

        // Truy cập trực tiếp URL Dashboard phải redirect về Login
        driver.navigate().to(ConfigReader.get("app.baseUrl") + "dashboard");
        sleep(3000);
        assertFalse(new DashboardPage(driver).isOnDashboardUrl()
                        && new DashboardPage(driver).isLogoutButtonPresent(),
                "Sau logout, không được phép truy cập dashboard bằng URL trực tiếp");
    }

    @Test(description = "TC_029 — Browser back sau logout không hiển thị nội dung cached",
          groups = {"logout", "session", "security"})
    public void testBrowserBackAfterLogoutBlocksCachedContent() {
        DashboardPage dashboard = loginAndReachDashboard();
        String dashboardUrl = driver.getCurrentUrl();
        dashboard.logout();
        sleep(2500);

        // Bấm back
        driver.navigate().back();
        sleep(2500);

        // Yêu cầu: KHÔNG hiển thị nội dung Dashboard cached
        DashboardPage afterBack = new DashboardPage(driver);
        boolean stillLoggedIn = afterBack.isLogoutButtonPresent();
        assertFalse(stillLoggedIn,
                "REQ: Sau logout, browser back KHÔNG được hiển thị Dashboard cached (Cache-Control: no-store). "
                        + "Hiện tại Logout button vẫn hiện → vi phạm Compliance Issue (R-SEC-02)");
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
