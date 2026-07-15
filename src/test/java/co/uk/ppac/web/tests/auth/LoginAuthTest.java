package co.uk.ppac.web.tests.auth;

import co.uk.ppac.core.base.WebBaseTest;
import co.uk.ppac.core.config.ConfigReader;
import co.uk.ppac.web.pages.DashboardPage;
import co.uk.ppac.web.pages.LoginPage;
import co.uk.ppac.core.utils.DataGenerator;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.Instant;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class LoginAuthTest extends WebBaseTest {

    @Test(description = "TC_011 — Login happy path không 2FA", groups = {"critical", "auth"})
    public void testLoginHappyPathRedirectsToDashboard() {
        LoginPage login = new LoginPage(driver).open(ConfigReader.get("app.baseUrl")).chooseEmailMethod();
        login.typeEmail(ConfigReader.get("login.test.email"));
        login.typePassword(ConfigReader.get("login.test.password"));
        login.submit();

        DashboardPage dashboard = new DashboardPage(driver);
        boolean reached = dashboard.waitForDashboard(20);

        assertTrue(reached,
                "Sau khi submit credential đúng, app phải redirect tới /dashboard hoặc hiển thị Logout button. "
                        + "Hiện tại URL=" + driver.getCurrentUrl());
    }

    @Test(description = "TC_012 — Password sai phải show generic error (anti-enumeration REQ-13)",
          groups = {"critical", "auth", "security"})
    public void testWrongPasswordShowsGenericError() {
        LoginPage login = new LoginPage(driver).open(ConfigReader.get("app.baseUrl")).chooseEmailMethod();
        login.typeEmail(ConfigReader.get("login.test.email"));
        login.typePassword("WrongPass@123");
        login.submit();
        sleep(2500);

        assertFalse(driver.getCurrentUrl().contains("dashboard"),
                "Password sai không được phép truy cập dashboard");
        assertTrue(login.isGenericInvalidCredentialsErrorShown(),
                "REQ-13 yêu cầu generic error 'Invalid email or password'. "
                        + "App đang hiển thị: '" + (login.isWrongPasswordErrorShown() ? login.wrongPasswordErrorText() : "(không có message generic)") + "' "
                        + "→ vi phạm anti-enumeration. Cần escalate cho security team.");
    }

    @Test(description = "TC_013 — Email không tồn tại — message giống TC_012 + timing đồng nhất",
          groups = {"critical", "auth", "security"})
    public void testNonExistentEmailShowsSameGenericError() {
        // Đo timing: TC_012 first
        LoginPage login = new LoginPage(driver).open(ConfigReader.get("app.baseUrl")).chooseEmailMethod();
        login.typeEmail(ConfigReader.get("login.test.email"));
        login.typePassword("WrongPass@123");
        Instant t0 = Instant.now();
        login.submit();
        sleep(2500);
        long wrongPasswordMs = Duration.between(t0, Instant.now()).toMillis();
        boolean wrongPasswordShownGenericError = login.isGenericInvalidCredentialsErrorShown();

        // Reset and try non-existent email
        driver.navigate().to(ConfigReader.get("app.baseUrl"));
        login = new LoginPage(driver);
        login.waitForFlutterReady();
        login.chooseEmailMethod();
        String nonExistent = DataGenerator.nonExistentEmail("auth");
        login.typeEmail(nonExistent);
        login.typePassword("AnyValidPass@1");
        Instant t1 = Instant.now();
        login.submit();
        sleep(2500);
        long nonExistentMs = Duration.between(t1, Instant.now()).toMillis();
        boolean nonExistentShownGenericError = login.isGenericInvalidCredentialsErrorShown();
        boolean nonExistentLeaks = login.isUserNotFoundErrorShown();

        assertFalse(nonExistentLeaks,
                "Email không tồn tại không được hiển thị 'User not found'/'No account' — vi phạm anti-enumeration");
        assertTrue(wrongPasswordShownGenericError && nonExistentShownGenericError,
                "Cả 2 case (sai password và email không tồn tại) phải show CHÍNH XÁC cùng generic error 'Invalid email or password'");
        assertTrue(Math.abs(wrongPasswordMs - nonExistentMs) <= 500,
                String.format("Timing chênh lệch quá lớn (%dms wrong-password vs %dms non-existent) — có thể bị timing attack",
                        wrongPasswordMs, nonExistentMs));
    }

    @Test(description = "TC_022 — URL phải kết thúc với /dashboard sau login", groups = {"auth", "redirect"})
    public void testRedirectsToDashboardPath() {
        LoginPage login = new LoginPage(driver).open(ConfigReader.get("app.baseUrl")).chooseEmailMethod();
        login.typeEmail(ConfigReader.get("login.test.email"));
        login.typePassword(ConfigReader.get("login.test.password"));
        login.submit();

        DashboardPage dashboard = new DashboardPage(driver);
        boolean reached = dashboard.waitForDashboard(20);

        assertTrue(reached, "Phải tới được dashboard sau login");
        assertTrue(driver.getCurrentUrl().contains(ConfigReader.get("app.dashboardPath")),
                "URL sau login phải chứa '" + ConfigReader.get("app.dashboardPath") + "', actual=" + driver.getCurrentUrl());
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
