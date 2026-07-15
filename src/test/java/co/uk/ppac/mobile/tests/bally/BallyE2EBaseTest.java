package co.uk.ppac.mobile.tests.bally;

import co.uk.ppac.core.config.AppConfig;
import co.uk.ppac.core.driver.AppiumDriverFactory;
import co.uk.ppac.core.reporting.ScreenshotUtil;
import co.uk.ppac.mobile.locators.HomeLocators;
import co.uk.ppac.mobile.locators.NewCheckLocators;
import co.uk.ppac.mobile.locators.bally.M02YourDetailsLocators;
import co.uk.ppac.mobile.screens.HomeScreen;
import co.uk.ppac.mobile.screens.NewCheckScreen;
import co.uk.ppac.mobile.screens.WelcomeScreen;
import co.uk.ppac.mobile.screens.bally.M02YourDetailsScreen;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

import java.time.Duration;

/**
 * Base cho luồng E2E BALLYCOMMON-RAIL: driver + login <b>1 LẦN cho cả class</b>
 * ({@code @BeforeClass}) thay vì mỗi test, rồi điều hướng liên tục M02→M09 trong
 * cùng 1 phiên — đúng luồng người dùng thật (và giảm flaky do re-login).
 *
 * <p>Khác {@link BallyOnboardingBaseTest} (per-method, dùng cho test rời từng màn).
 */
public abstract class BallyE2EBaseTest {

    private static final Duration STEP_TIMEOUT = Duration.ofSeconds(120);

    protected AndroidDriver driver;
    /** Màn M02 Your Details — điểm bắt đầu luồng điền (đã tới trong @BeforeClass). */
    protected M02YourDetailsScreen details;

    @BeforeClass(alwaysRun = true)
    public void setUpClassAndReachYourDetails() {
        driver = AppiumDriverFactory.createDriver();

        // 1) Login (qua splash → Home).
        new WelcomeScreen(driver).openEmailLogin().login(
                AppConfig.getRequired("test.user.email"),
                AppConfig.getRequired("test.user.password"));
        waitVisible(HomeLocators.WALLET_TAB, "Home/Wallet sau login");

        // 2) New Check → M01 prefix → BALLYCOMMON-RAIL → Continue.
        new HomeScreen(driver).openNewCheck();
        waitVisible(NewCheckLocators.SCREEN_HEADING, "màn 'Enter company prefix'");
        NewCheckScreen prefix = new NewCheckScreen(driver);
        prefix.enterBallycommonRailAndContinue();
        if (prefix.isContinueOnboardingPopupDisplayed()) {
            prefix.startNewOnboarding();
        }

        // 3) Tới M02 Your Details (qua spinner + load).
        waitVisible(M02YourDetailsLocators.SCREEN_HEADING, "màn 'Your Details'");
        details = new M02YourDetailsScreen(driver);
    }

    @AfterMethod(alwaysRun = true)
    public void captureOnFailure(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE && driver != null) {
            ScreenshotUtil.attachScreenshot(driver, "failure-" + result.getName());
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        AppiumDriverFactory.quitDriver();
    }

    private void waitVisible(By locator, String what) {
        new WebDriverWait(driver, STEP_TIMEOUT)
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
}
