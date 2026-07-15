package co.uk.ppac.mobile.locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Locators tập trung cho dialog "Confirm Log Out".
 */
public final class LogoutConfirmLocators {

    private LogoutConfirmLocators() {
    }

    public static final By DIALOG_TITLE = AppiumBy.accessibilityId("Confirm Log Out");
    public static final By LOG_OUT_BUTTON = AppiumBy.accessibilityId("Log Out");
    public static final By CANCEL_BUTTON = AppiumBy.accessibilityId("Cancel");
}
