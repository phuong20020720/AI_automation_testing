package co.uk.ppac.mobile.locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Locators tập trung cho Welcome screen (màn hình đầu khi mở app).
 */
public final class WelcomeLocators {

    private WelcomeLocators() {
    }

    public static final By SIGN_UP_WITH_EMAIL_BUTTON = AppiumBy.accessibilityId("Sign up with Email");
    public static final By LOG_IN_LINK = AppiumBy.accessibilityId("Log in");
    public static final By LOG_IN_WITH_EMAIL_BUTTON = AppiumBy.accessibilityId("Log in with Email");
}
