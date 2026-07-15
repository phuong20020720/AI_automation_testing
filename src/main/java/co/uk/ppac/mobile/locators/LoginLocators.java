package co.uk.ppac.mobile.locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Locators tập trung cho màn hình "Log in with email" (Flutter, com.ppac.app.sandbox).
 * Hai text field không có content-desc/resource-id nên định vị qua attribute {@code password}.
 */
public final class LoginLocators {

    private LoginLocators() {
    }

    public static final By SCREEN_HEADING = AppiumBy.accessibilityId("Log in with email");
    public static final By EMAIL_INPUT = AppiumBy.xpath("//android.widget.EditText[@password='false']");
    public static final By PASSWORD_INPUT = AppiumBy.xpath("//android.widget.EditText[@password='true']");
    public static final By LOGIN_BUTTON = AppiumBy.accessibilityId("Log in");
}
