package co.uk.ppac.mobile.locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Locators tập trung cho onboarding Step 1/3 — "Selfie time!" instructions.
 * Toạ độ tap (logo reveal) giữ trong screen vì là hằng số gesture, không phải locator.
 */
public final class SelfieInstructionLocators {

    private SelfieInstructionLocators() {
    }

    public static final By SCREEN_TITLE = AppiumBy.accessibilityId("Selfie time!");
    public static final By OPEN_CAMERA_BUTTON = AppiumBy.accessibilityId("Open Camera");
    public static final By BACK_ARROW = AppiumBy.xpath(
            "//android.view.View[@clickable='true' and @bounds[starts-with(., '[42,')]]");
    public static final By LOGOUT_ICON = AppiumBy.xpath(
            "//android.view.View[@clickable='true' and @bounds[contains(., '[979,')]]");
    public static final By DEBUG_SKIP_BUTTON = AppiumBy.accessibilityId("DEBUG: Skip Selfie");
}
