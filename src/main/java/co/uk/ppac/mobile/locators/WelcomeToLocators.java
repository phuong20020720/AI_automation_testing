package co.uk.ppac.mobile.locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Locators tập trung cho "WELCOME TO" onboarding intro (post-OTP).
 */
public final class WelcomeToLocators {

    private WelcomeToLocators() {
    }

    public static final By TITLE_WELCOME_TO = AppiumBy.accessibilityId("WELCOME TO");
    public static final By SUBTITLE_IDENTITY = AppiumBy.accessibilityId("Secure Identity Verification");
    public static final By LETS_START_BUTTON = AppiumBy.accessibilityId("Let's start");
    public static final By LOGOUT_ICON = AppiumBy.xpath(
            "//android.view.View[@clickable='true' and @bounds[contains(., ',170]')]"
                    + "[position()=last() or @bounds[contains(., '[1016,')]]");
}
