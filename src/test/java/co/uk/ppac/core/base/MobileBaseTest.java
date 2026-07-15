package co.uk.ppac.core.base;

import co.uk.ppac.core.driver.AppiumDriverFactory;
import co.uk.ppac.core.reporting.ScreenshotUtil;
import io.appium.java_client.android.AndroidDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Lifecycle parent for every mobile test. Each test method gets its own,
 * fully isolated Appium session - no state is shared between tests.
 */
public abstract class MobileBaseTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MobileBaseTest.class);

    protected AndroidDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        driver = AppiumDriverFactory.createDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            LOGGER.warn("Test '{}' failed - capturing screenshot", result.getName());
            ScreenshotUtil.attachScreenshot(driver, "failure-" + result.getName());
        }
        AppiumDriverFactory.quitDriver();
    }
}
