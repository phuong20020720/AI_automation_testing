package co.uk.ppac.mobile.locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Locators tập trung cho New Check Step 3 — "Contact point" (company contact email).
 */
public final class ContactPointLocators {

    private ContactPointLocators() {
    }

    public static final By SCREEN_HEADING = AppiumBy.accessibilityId("Contact point");
    public static final By EMAIL_LABEL =
            AppiumBy.accessibilityId("Company contact email (Manager, HR, Director)");
    public static final By EMAIL_INPUT = AppiumBy.xpath("//android.widget.EditText");
    public static final By CONTINUE_BUTTON = AppiumBy.accessibilityId("Continue →");
}
