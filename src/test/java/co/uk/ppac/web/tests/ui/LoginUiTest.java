package co.uk.ppac.web.tests.ui;

import co.uk.ppac.core.base.WebBaseTest;
import co.uk.ppac.web.pages.LoginPage;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class LoginUiTest extends WebBaseTest {

    @Test(description = "TC_001 — Login page render đầy đủ elements", groups = {"smoke"})
    public void testLoginPageRendersCoreElements() {
        LoginPage login = new LoginPage(driver);
        login.waitForFlutterReady();

        assertTrue(login.isMethodPickerDisplayed(),
                "Trang Login phải hiển thị method picker (Google, Microsoft, Email, Create Account)");
        assertTrue(login.isLanguageToggleDisplayed(),
                "Phải hiển thị nút chọn ngôn ngữ ENGLISH");

        login.chooseEmailMethod();

        assertTrue(login.isEmailFormDisplayed(),
                "Sau khi chọn email method, phải hiển thị Email input + Password input + Submit button");
        assertTrue(login.isForgotPasswordButtonDisplayed(),
                "Phải hiển thị link FORGOT PASSWORD");
        assertFalse(driver.getPageSource().contains("Remember me"),
                "Theo spec, KHÔNG được có 'Remember me' (out of scope)");
    }
}
