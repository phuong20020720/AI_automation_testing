package co.uk.ppac.mobile.locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Locators tập trung cho Sign Up Step 4 — "Check your inbox" (OTP).
 * 6 ô OTP là Compose custom view, định vị theo toạ độ bounds.
 */
public final class CheckYourInboxLocators {

    private CheckYourInboxLocators() {
    }

    public static final By SCREEN_HEADING = AppiumBy.accessibilityId("Check your inbox");
    public static final By VALIDATE_BUTTON = AppiumBy.accessibilityId("Validate");
    public static final By RESEND_ACTIVE_LINK = AppiumBy.accessibilityId("Resend");
    public static final By EDIT_MY_EMAIL_LINK = AppiumBy.accessibilityId("Edit my email");
    public static final By INVALID_CODE_ERROR = AppiumBy.xpath(
            "//*[@text='Invalid Code!' or @content-desc='Invalid Code!']");

    /**
     * Locator động cho 1 ô OTP, theo toạ độ tâm X của ô (trên reference 1080px).
     * Bounds bắt đầu tại {@code x - 87} (nửa bề rộng ô) và y cố định 755.
     */
    public static By otpBoxByCenterX(int centerX) {
        return AppiumBy.xpath(
                "//android.view.View[@bounds[contains(., '[" + (centerX - 87) + ",755]')]]");
    }
}
