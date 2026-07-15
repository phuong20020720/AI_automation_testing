package co.uk.ppac.web.tests.security;

import co.uk.ppac.core.base.WebBaseTest;
import co.uk.ppac.core.config.ConfigReader;
import co.uk.ppac.web.pages.DashboardPage;
import co.uk.ppac.web.pages.LoginPage;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class SecurityTest extends WebBaseTest {

    @DataProvider(name = "sqlInjectionPayloads")
    public Object[][] sqlInjectionPayloads() {
        return new Object[][]{
                {"admin' OR '1'='1", "anything"},
                {"' OR 1=1 --", "' OR 1=1 --"},
                {"admin'/*", "*/--"},
                {"'; DROP TABLE users; --", "x"}
        };
    }

    @Test(description = "TC_032 — SQL Injection không được bypass auth",
          dataProvider = "sqlInjectionPayloads", groups = {"critical", "security"})
    public void testSqlInjectionCannotBypassAuth(String emailPayload, String passwordPayload) {
        LoginPage login = new LoginPage(driver).open(ConfigReader.get("app.baseUrl")).chooseEmailMethod();
        login.typeEmail(emailPayload);
        login.typePassword(passwordPayload);
        login.submit();
        sleep(3000);

        DashboardPage dashboard = new DashboardPage(driver);
        assertFalse(dashboard.isOnDashboardUrl(),
                "SQLi payload [" + emailPayload + "] không được bypass auth — phải reject");

        String pageSource = driver.getPageSource().toLowerCase();
        assertFalse(pageSource.contains("syntax error")
                        || pageSource.contains("sql")
                        || pageSource.contains("postgresql")
                        || pageSource.contains("mysql")
                        || pageSource.contains("jdbc"),
                "Response không được expose database error message khi gặp SQLi payload");
    }

    @DataProvider(name = "xssPayloads")
    public Object[][] xssPayloads() {
        return new Object[][]{
                {"<script>alert('xss')</script>"},
                {"\"><img src=x onerror=alert(1)>"},
                {"<svg onload=alert(1)>"},
                {"javascript:alert(1)"}
        };
    }

    @Test(description = "TC_033 — XSS payload phải được escape, không trigger script",
          dataProvider = "xssPayloads", groups = {"critical", "security"})
    public void testXssPayloadIsEscaped(String xssPayload) {
        LoginPage login = new LoginPage(driver).open(ConfigReader.get("app.baseUrl")).chooseEmailMethod();
        login.typeEmail(xssPayload);
        login.typePassword("Test@123");
        login.submit();
        sleep(2500);

        // Không có alert popup nào được phép xuất hiện
        try {
            String alertText = driver.switchTo().alert().getText();
            driver.switchTo().alert().dismiss();
            assertTrue(false, "XSS payload [" + xssPayload + "] đã trigger alert: " + alertText);
        } catch (org.openqa.selenium.NoAlertPresentException expected) {
            // Đúng: không alert
        }

        String pageSource = driver.getPageSource();
        assertFalse(pageSource.contains(xssPayload),
                "XSS payload [" + xssPayload + "] không được phép echo back nguyên văn — phải HTML-encoded");
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
