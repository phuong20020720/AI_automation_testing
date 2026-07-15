package co.uk.ppac.mobile.tests;

import co.uk.ppac.core.base.MobileBaseTest;
import co.uk.ppac.core.config.AppConfig;
import co.uk.ppac.mobile.screens.HomeScreen;
import co.uk.ppac.mobile.screens.LoginScreen;
import org.openqa.selenium.ScreenOrientation;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

/**
 * Home screen scenarios, including mobile-specific behaviour: screen rotation
 * and returning the app from the background.
 *
 * <p>TestNG runs the base-class {@code setUp()} before this class's
 * {@code login()}, so the driver exists by the time the login runs.
 */
public class HomeMobileTest extends MobileBaseTest {

    private HomeScreen homeScreen;

    @BeforeMethod(alwaysRun = true)
    public void login() {
        new LoginScreen(driver).login(
                AppConfig.getRequired("test.user.email"),
                AppConfig.getRequired("test.user.password"));
        homeScreen = new HomeScreen(driver);
        Assert.assertTrue(homeScreen.isWelcomeDisplayed(),
                "Phải đăng nhập thành công trước khi chạy test màn hình Home");
    }

    @Test(groups = {"mobile", "regression"},
            description = "Home content stays visible after rotating to landscape and back")
    public void testHomeSurvivesRotation() {
        driver.rotate(ScreenOrientation.LANDSCAPE);
        Assert.assertTrue(homeScreen.isWelcomeDisplayed(),
                "Nội dung Home phải còn hiển thị ở chế độ landscape");

        driver.rotate(ScreenOrientation.PORTRAIT);
        Assert.assertTrue(homeScreen.isWelcomeDisplayed(),
                "Nội dung Home phải còn hiển thị khi xoay lại portrait");
    }

    @Test(groups = {"mobile", "regression"},
            description = "App state is preserved after being sent to the background")
    public void testHomeSurvivesBackground() {
        driver.runAppInBackground(Duration.ofSeconds(5));
        Assert.assertTrue(homeScreen.isWelcomeDisplayed(),
                "Màn hình Home phải khôi phục đúng trạng thái sau khi app quay lại từ nền");
    }
}
