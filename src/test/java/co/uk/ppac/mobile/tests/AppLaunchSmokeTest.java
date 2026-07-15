package co.uk.ppac.mobile.tests;

import co.uk.ppac.core.base.MobileBaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Smoke test for the automation setup itself.
 *
 * <p>Verifies the full chain - framework -> Appium server -> emulator -> app
 * under test - by launching the PPAC Sandbox app and confirming it reaches the
 * foreground. It needs no credentials and no UI locators, so it is the first
 * thing to run when bringing the environment up.
 */
public class AppLaunchSmokeTest extends MobileBaseTest {

    private static final String APP_PACKAGE = "com.ppac.app.sandbox";

    @Test(groups = {"mobile", "smoke"},
            description = "PPAC Sandbox app launches and is the foreground app")
    public void testAppLaunchesToForeground() {
        Assert.assertTrue(driver.isAppInstalled(APP_PACKAGE),
                "App PPAC Sandbox phải được cài trên thiết bị");

        String currentPackage = driver.getCurrentPackage();
        Assert.assertEquals(currentPackage, APP_PACKAGE,
                "App PPAC Sandbox phải ở foreground sau khi Appium khởi tạo session");
    }
}
