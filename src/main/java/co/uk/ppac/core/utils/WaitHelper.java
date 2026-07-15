package co.uk.ppac.core.utils;

import co.uk.ppac.core.config.AppConfig;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Explicit-wait helper. The framework never calls {@code Thread.sleep()};
 * every synchronisation point goes through one of these conditions.
 */
public class WaitHelper {

    private final WebDriverWait wait;

    public WaitHelper(AndroidDriver driver) {
        this.wait = new WebDriverWait(
                driver, Duration.ofSeconds(AppConfig.getInt("explicit.wait.seconds", 15)));
    }

    /** Waits until the element is rendered and visible, then returns it. */
    public WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Waits until the element is visible and enabled, then returns it. */
    public WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /** Waits until all matching elements are visible, then returns them in document order. */
    public List<WebElement> waitForAllVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    /** Waits until the element is no longer visible. */
    public boolean waitForInvisible(By locator) {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /** Waits until the element contains the expected text. */
    public boolean waitForText(By locator, String expectedText) {
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, expectedText));
    }
}
