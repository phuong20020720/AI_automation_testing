package co.uk.ppac.web.tests.validation;

import co.uk.ppac.core.base.WebBaseTest;
import co.uk.ppac.core.config.ConfigReader;
import co.uk.ppac.web.pages.LoginPage;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class LoginValidationTest extends WebBaseTest {

    @Test(description = "TC_005 — Bỏ trống Email, có Password", groups = {"validation"})
    public void testEmptyEmailShowsValidationError() {
        LoginPage login = new LoginPage(driver).open(ConfigReader.get("app.baseUrl")).chooseEmailMethod();
        login.typePassword(ConfigReader.get("login.test.password"));
        login.submit();

        assertTrue(login.isEmptyEmailErrorShown() || login.isEmailMarkedInvalid(),
                "Phải hiển thị error 'Please enter an email' hoặc đánh dấu Email aria-invalid khi để trống");
        assertFalse(driver.getCurrentUrl().contains("dashboard"),
                "Form không được submit nếu email trống — không được redirect Dashboard");
    }

    @Test(description = "TC_006 — Bỏ trống Password, có Email", groups = {"validation"})
    public void testEmptyPasswordShowsValidationError() {
        LoginPage login = new LoginPage(driver).open(ConfigReader.get("app.baseUrl")).chooseEmailMethod();
        login.typeEmail(ConfigReader.get("login.test.email"));
        login.submit();

        assertTrue(login.isEmptyPasswordErrorShown() || login.isPasswordMarkedInvalid(),
                "Phải hiển thị error 'Please enter password' hoặc đánh dấu Password aria-invalid khi để trống");
        assertFalse(driver.getCurrentUrl().contains("dashboard"),
                "Form không được submit nếu password trống — không được redirect Dashboard");
    }

    @DataProvider(name = "invalidEmails")
    public Object[][] invalidEmails() {
        return new Object[][]{
                {"abc"},
                {"abc@"},
                {"@abc.com"},
                {"abc@@a.com"},
                {"abc@a"},
                {"abc def@a.com"},
                {".abc@a.com"}
        };
    }

    @Test(description = "TC_007 — Email sai format không được chấp nhận",
          dataProvider = "invalidEmails", groups = {"validation"})
    public void testInvalidEmailFormatRejected(String invalidEmail) {
        LoginPage login = new LoginPage(driver).open(ConfigReader.get("app.baseUrl")).chooseEmailMethod();
        login.typeEmail(invalidEmail);
        login.typePassword(ConfigReader.get("login.test.password"));
        login.submit();

        assertTrue(login.isEmailMarkedInvalid() || login.isEmptyEmailErrorShown()
                        || driver.getPageSource().toLowerCase().contains("invalid email")
                        || driver.getPageSource().toLowerCase().contains("valid email"),
                String.format("Email '%s' phải bị reject với validation error rõ ràng", invalidEmail));
        assertFalse(driver.getCurrentUrl().contains("dashboard"),
                String.format("Email sai format '%s' không được phép submit thành công", invalidEmail));
    }
}
