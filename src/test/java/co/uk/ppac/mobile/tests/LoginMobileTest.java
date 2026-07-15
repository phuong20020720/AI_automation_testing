package co.uk.ppac.mobile.tests;

import co.uk.ppac.core.base.MobileBaseTest;
import co.uk.ppac.core.config.AppConfig;
import co.uk.ppac.mobile.data.LoginScenario;
import co.uk.ppac.mobile.screens.HomeScreen;
import co.uk.ppac.mobile.screens.LoginScreen;
import co.uk.ppac.mobile.screens.WelcomeScreen;
import co.uk.ppac.core.utils.DataGenerator;
import co.uk.ppac.core.utils.TestDataReader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Login scenarios for the PPAC Sandbox mobile app.
 *
 * <p>Each test starts from a clean app state ({@code no.reset=false}), then
 * navigates welcome -&gt; "Log in" -&gt; "Log in with Email" to reach the login
 * form before exercising one scenario.
 *
 * <p>A rejected login is asserted via the login form still being displayed:
 * the app's error banner is not exposed to the accessibility tree (Flutter,
 * no Semantics), so its text cannot be read - see {@link LoginScreen}.
 */
public class LoginMobileTest extends MobileBaseTest {

    private LoginScreen loginScreen;

    @BeforeMethod(alwaysRun = true)
    public void openLoginForm() {
        loginScreen = new WelcomeScreen(driver).openEmailLogin();
        Assert.assertTrue(loginScreen.isLoaded(),
                "Màn hình Login phải hiển thị sau khi điều hướng từ Welcome");
    }

    @Test(groups = {"mobile", "smoke"},
            description = "Login succeeds with valid credentials and lands on the home screen")
    public void testLoginWithValidCredentials() {
        String email = AppConfig.getRequired("test.user.email");
        String password = AppConfig.getRequired("test.user.password");

        loginScreen.login(email, password);

        HomeScreen homeScreen = new HomeScreen(driver);
        Assert.assertTrue(homeScreen.isWelcomeDisplayed(),
                "Màn hình Home phải hiển thị sau khi đăng nhập thành công");
    }

    @Test(groups = {"mobile", "regression"},
            description = "Login is rejected for an account that does not exist")
    public void testLoginWithUnregisteredAccount() {
        String unregisteredEmail = DataGenerator.generateEmail("loginMobileUnregistered");

        loginScreen.login(unregisteredEmail, "ValidPass@123");

        Assert.assertTrue(loginScreen.isLoaded(),
                "Đăng nhập tài khoản không tồn tại phải bị từ chối — app vẫn ở màn hình Login");
    }

    @Test(dataProvider = "invalidLogins", groups = {"mobile", "regression"},
            description = "Login is rejected for invalid input")
    public void testLoginRejectsInvalidInput(LoginScenario scenario) {
        loginScreen.login(scenario.email(), scenario.password());

        Assert.assertTrue(loginScreen.isLoaded(),
                "Kịch bản '" + scenario.scenario() + "' phải bị từ chối — app vẫn ở màn hình Login "
                        + "(lỗi mong đợi: " + scenario.expectedError() + ")");
    }

    @DataProvider(name = "invalidLogins")
    public Object[][] invalidLogins() {
        List<LoginScenario> scenarios =
                TestDataReader.readList("login-scenarios.json", LoginScenario.class);
        return scenarios.stream()
                .map(scenario -> new Object[]{scenario})
                .toArray(Object[][]::new);
    }
}
