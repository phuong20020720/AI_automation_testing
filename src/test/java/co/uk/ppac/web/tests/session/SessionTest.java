package co.uk.ppac.web.tests.session;

import co.uk.ppac.core.base.WebBaseTest;
import co.uk.ppac.core.config.ConfigReader;
import co.uk.ppac.web.pages.DashboardPage;
import co.uk.ppac.web.pages.LoginPage;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class SessionTest extends WebBaseTest {

    private void loginHappyPath() {
        LoginPage login = new LoginPage(driver).open(ConfigReader.get("app.baseUrl")).chooseEmailMethod();
        login.typeEmail(ConfigReader.get("login.test.email"));
        login.typePassword(ConfigReader.get("login.test.password"));
        login.submit();
        DashboardPage dashboard = new DashboardPage(driver);
        boolean reached = dashboard.waitForDashboard(20);
        if (!reached) {
            throw new SkipException("Cannot proceed: login did not reach dashboard. URL=" + driver.getCurrentUrl());
        }
    }

    @Test(description = "TC_025 — Cookie session phải có HttpOnly + Secure + SameSite (REQ-15)",
          groups = {"critical", "session", "security"})
    public void testSessionCookieFlags() {
        loginHappyPath();
        Set<Cookie> cookies = driver.manage().getCookies();

        // Loại trừ analytics/Freshchat — chỉ check cookie session/auth do app set
        // Auth cookie chính ở host dashboard (uat-client-dashboard.sandbox-compliant101.co.uk),
        // không phải host login. Tên cookie chứa 'session' hoặc 'auth' hoặc 'token'.
        List<Cookie> sessionCookies = cookies.stream()
                .filter(c -> !c.getName().startsWith("_ga"))
                .filter(c -> !c.getName().startsWith("_fw_"))
                .filter(c -> {
                    String n = c.getName().toLowerCase();
                    return n.contains("session") || n.contains("auth") || n.contains("token") || n.contains("jwt");
                })
                .filter(c -> !c.getName().equalsIgnoreCase("first_session"))
                .toList();

        assertFalse(sessionCookies.isEmpty(),
                "Phải có ít nhất 1 cookie session do app set (tên chứa 'session'/'auth'/'token'). "
                        + "Cookies hiện có: " + cookies.stream().map(Cookie::getName).toList());

        for (Cookie c : sessionCookies) {
            assertTrue(c.isHttpOnly(),
                    "REQ-15: Cookie '" + c.getName() + "' phải có HttpOnly=true (actual=false). "
                            + "→ Vi phạm bảo mật, cần escalate.");
            assertTrue(c.isSecure(),
                    "REQ-15: Cookie '" + c.getName() + "' phải có Secure=true");
            assertNotNull(c.getSameSite(),
                    "REQ-15: Cookie '" + c.getName() + "' phải có SameSite (Strict hoặc Lax)");
            assertTrue("Strict".equalsIgnoreCase(c.getSameSite()) || "Lax".equalsIgnoreCase(c.getSameSite()),
                    "REQ-15: SameSite của cookie '" + c.getName() + "' phải là Strict hoặc Lax (actual=" + c.getSameSite() + ")");
        }
    }

    @Test(description = "TC_027 — Auth token KHÔNG được nằm trong localStorage/sessionStorage/URL",
          groups = {"session", "security"})
    public void testTokenNotExposedInStorageOrUrl() {
        loginHappyPath();

        JavascriptExecutor js = (JavascriptExecutor) driver;
        Object localKeys = js.executeScript(
                "var keys = []; for (var i=0;i<localStorage.length;i++) keys.push(localStorage.key(i).toLowerCase()); return keys;");
        Object sessionKeys = js.executeScript(
                "var keys = []; for (var i=0;i<sessionStorage.length;i++) keys.push(sessionStorage.key(i).toLowerCase()); return keys;");
        Object localValues = js.executeScript(
                "var values = []; for (var i=0;i<localStorage.length;i++) values.push((localStorage.getItem(localStorage.key(i)) || '').toLowerCase()); return values;");

        String localKeysStr = String.valueOf(localKeys);
        String sessionKeysStr = String.valueOf(sessionKeys);
        String localValuesStr = String.valueOf(localValues);
        String url = driver.getCurrentUrl().toLowerCase();

        for (String suspect : List.of("token", "jwt", "auth", "bearer", "access_token", "id_token")) {
            assertFalse(localKeysStr.contains(suspect),
                    "localStorage key chứa '" + suspect + "' — token không được lưu trong localStorage");
            assertFalse(sessionKeysStr.contains(suspect),
                    "sessionStorage key chứa '" + suspect + "' — token không được lưu trong sessionStorage");
            assertFalse(localValuesStr.contains("eyj"),
                    "localStorage có value bắt đầu bằng 'eyJ' (JWT prefix) — JWT phải lưu trong cookie HttpOnly");
            assertFalse(url.contains(suspect + "="),
                    "URL chứa '" + suspect + "=' — token không được lộ trong URL query string");
        }
    }

}
