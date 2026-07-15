package co.uk.ppac.mobile.tests.bally;

import co.uk.ppac.core.base.MobileBaseTest;
import co.uk.ppac.core.config.AppConfig;
import co.uk.ppac.mobile.locators.HomeLocators;
import co.uk.ppac.mobile.locators.bally.M02YourDetailsLocators;
import co.uk.ppac.mobile.screens.HomeScreen;
import co.uk.ppac.mobile.screens.NewCheckScreen;
import co.uk.ppac.mobile.screens.WelcomeScreen;
import co.uk.ppac.mobile.screens.bally.M02YourDetailsScreen;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;

/**
 * Base cho các test BALLYCOMMON-RAIL onboarding: login + điều hướng chung.
 *
 * <p>Sau khi tap Login, app hiện splash rồi mới tới Home — thường lâu hơn
 * explicit-wait mặc định (15s). Ở đây chờ Wallet tab xuất hiện bằng một wait
 * dài riêng (qua hết splash) cho ổn định, không phụ thuộc timeout mặc định.
 */
public abstract class BallyOnboardingBaseTest extends MobileBaseTest {

    /** Wait dài cho bước login→Home (qua splash + load dữ liệu Home). */
    private static final Duration LOGIN_HOME_TIMEOUT = Duration.ofSeconds(120);

    /** Login bằng tài khoản test (fixture) và chờ Home (Wallet tab) hiển thị. */
    protected HomeScreen loginToHome() {
        new WelcomeScreen(driver).openEmailLogin().login(
                AppConfig.getRequired("test.user.email"),
                AppConfig.getRequired("test.user.password"));

        try {
            new WebDriverWait(driver, LOGIN_HOME_TIMEOUT)
                    .until(ExpectedConditions.visibilityOfElementLocated(HomeLocators.WALLET_TAB));
        } catch (TimeoutException e) {
            Assert.fail("Sau login không thấy Home/Wallet trong " + LOGIN_HOME_TIMEOUT.getSeconds()
                    + "s — kiểm tra login fixture hoặc tài khoản đang resume onboarding dở dang");
        }
        return new HomeScreen(driver);
    }

    /** Login → tab New Check → màn M01 "Enter company prefix". */
    protected NewCheckScreen openPrefixScreen() {
        loginToHome().openNewCheck();
        NewCheckScreen prefix = new NewCheckScreen(driver);
        Assert.assertTrue(prefix.isLoaded(),
                "Màn 'Enter company prefix' phải hiển thị sau khi mở tab New Check");
        return prefix;
    }

    /**
     * Login → New Check → M01 → chọn BALLYCOMMON-RAIL → Continue → màn M02
     * "Your Details". Nếu tài khoản có onboarding dở dang (popup) thì bắt đầu mới.
     */
    protected M02YourDetailsScreen openYourDetailsScreen() {
        NewCheckScreen prefix = openPrefixScreen();
        prefix.enterBallycommonRailAndContinue();
        if (prefix.isContinueOnboardingPopupDisplayed()) {
            prefix.startNewOnboarding();
        }
        // Sau Continue/Start-new có spinner + load dữ liệu → chờ kiên nhẫn (lâu hơn 15s).
        try {
            new WebDriverWait(driver, LOGIN_HOME_TIMEOUT).until(
                    ExpectedConditions.visibilityOfElementLocated(M02YourDetailsLocators.SCREEN_HEADING));
        } catch (TimeoutException e) {
            Assert.fail("Không vào được màn 'Your Details' sau Continue/Start-new trong "
                    + LOGIN_HOME_TIMEOUT.getSeconds() + "s");
        }
        return new M02YourDetailsScreen(driver);
    }
}
