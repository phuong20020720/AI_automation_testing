package co.uk.ppac.mobile.locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Locators tập trung cho Sign Up Step 2 — "Sign up by email".
 */
public final class SignUpByEmailLocators {

    private SignUpByEmailLocators() {
    }

    public static final By SCREEN_HEADING = AppiumBy.accessibilityId("Sign up by email");
    public static final By EMAIL_INPUT = AppiumBy.xpath("(//android.widget.EditText)[1]");
    public static final By NEXT_BUTTON = AppiumBy.accessibilityId("Next");
    public static final By VALIDATION_ERROR = AppiumBy.xpath(
            "//*[@text='Please enter a valid email' or @content-desc='Please enter a valid email']");
}
