package co.uk.ppac.mobile.locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Locators tập trung cho Sign Up Step 3 — "Create password".
 * Hai EditText đều bị Flutter mask {@code password='true'} → phân biệt bằng index.
 */
public final class CreatePasswordLocators {

    private CreatePasswordLocators() {
    }

    public static final By SCREEN_HEADING = AppiumBy.accessibilityId("Create password");
    public static final By PASSWORD_INPUT = AppiumBy.xpath("(//android.widget.EditText[@password='true'])[1]");
    public static final By CONFIRM_INPUT = AppiumBy.xpath("(//android.widget.EditText[@password='true'])[2]");
    public static final By CREATE_ACCOUNT_BUTTON = AppiumBy.accessibilityId("Create account");

    /** Locator động cho 1 dòng rule password (định vị theo content-desc đúng bằng text rule). */
    public static By rule(String ruleText) {
        return AppiumBy.accessibilityId(ruleText);
    }
}
