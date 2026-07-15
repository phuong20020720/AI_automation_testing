package co.uk.ppac.core.utils;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.Map;

/**
 * Reusable mobile gestures. Scrolling and swiping are delegated to native
 * UiAutomator2 capabilities rather than fixed-coordinate guesses, so they stay
 * stable across screen sizes and layouts.
 */
public class MobileGestures {

    /** Swipe directions accepted by {@link #swipe(Direction)}. */
    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private final AndroidDriver driver;

    public MobileGestures(AndroidDriver driver) {
        this.driver = driver;
    }

    /**
     * Scrolls the first scrollable container until an element with the given
     * visible text is on screen, then returns it.
     */
    public WebElement scrollToText(String visibleText) {
        String uiSelector = "new UiScrollable(new UiSelector().scrollable(true))"
                + ".scrollIntoView(new UiSelector().text(\"" + visibleText + "\"))";
        return driver.findElement(AppiumBy.androidUIAutomator(uiSelector));
    }

    /**
     * Scrolls the first vertically-scrollable container until an element with the
     * given {@code content-desc} (accessibility id) is on screen, then returns it.
     * Dùng cho app Flutter: label là content-desc, không phải text.
     */
    public WebElement scrollToContentDesc(String contentDesc) {
        // Target ScrollView dọc cụ thể — màn onboarding có cả HorizontalScrollView
        // (stepper) khiến scrollable(true) bắt nhầm container ngang.
        String uiSelector = "new UiScrollable(new UiSelector().className(\"android.widget.ScrollView\"))"
                + ".scrollIntoView(new UiSelector().description(\"" + contentDesc + "\"))";
        return driver.findElement(AppiumBy.androidUIAutomator(uiSelector));
    }

    /**
     * Taps at the given absolute viewport coordinates. Use for Compose custom
     * views without semantic nodes (e.g., Terms toggle on Welcome screen).
     * Coordinates assume 1080x2400 reference - scales linearly to actual viewport.
     */
    public void tapAt(int x, int y) {
        Dimension size = driver.manage().window().getSize();
        int scaledX = (int) (x * (size.getWidth() / 1080.0));
        int scaledY = (int) (y * (size.getHeight() / 2400.0));
        Map<String, Object> args = new HashMap<>();
        args.put("x", scaledX);
        args.put("y", scaledY);
        driver.executeScript("mobile: clickGesture", args);
    }

    /** Swipes the screen in the given direction using the native swipe gesture. */
    public void swipe(Direction direction) {
        Dimension size = driver.manage().window().getSize();
        Map<String, Object> args = new HashMap<>();
        args.put("left", (int) (size.getWidth() * 0.1));
        args.put("top", (int) (size.getHeight() * 0.1));
        args.put("width", (int) (size.getWidth() * 0.8));
        args.put("height", (int) (size.getHeight() * 0.8));
        args.put("direction", direction.name().toLowerCase());
        args.put("percent", 0.75);
        driver.executeScript("mobile: swipeGesture", args);
    }
}
