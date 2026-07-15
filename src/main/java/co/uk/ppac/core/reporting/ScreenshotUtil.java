package co.uk.ppac.core.reporting;

import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;

import java.io.ByteArrayInputStream;

/** Captures device screenshots and attaches them to the Allure report. */
public final class ScreenshotUtil {

    private ScreenshotUtil() {
    }

    /**
     * Captures the current screen and attaches it to the Allure report under
     * the given name. Safe to call with a {@code null} driver.
     */
    public static void attachScreenshot(AndroidDriver driver, String name) {
        if (driver == null) {
            return;
        }
        byte[] screenshot = driver.getScreenshotAs(OutputType.BYTES);
        Allure.addAttachment(name, "image/png", new ByteArrayInputStream(screenshot), "png");
    }
}
