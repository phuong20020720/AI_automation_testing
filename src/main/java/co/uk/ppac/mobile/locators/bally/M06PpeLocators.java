package co.uk.ppac.mobile.locators.bally;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * M06 "Personal Protective Equipment (PPE)"
 * (BALLYCOMMON-RAIL onboarding; chip stepper "PPE").
 *
 * <p>Trích THẬT từ {@code appium_get_page_source} của {@code com.ppac.app.sandbox}
 * trên emulator-5554 (Pixel7, Android 13), ngày 2026-06-16.
 *
 * <p>9 item PPE, mỗi item = label ({@code content-desc}) + 2 RadioButton
 * "Yes"/"No" (lặp mỗi item → neo theo label, giống M04). Item là sibling, radio
 * đứng ngay sau label.
 *
 */
public final class M06PpeLocators {

    private M06PpeLocators() {
    }

    /** 9 item PPE đúng thứ tự (content-desc THẬT). */
    public static final String[] ITEMS = {
            "Safety Shoes / Boots",
            "Bump / Hard Hat",
            "H.V. Vests",
            "H.V. Clothing",
            "Ear Protection",
            "Eye Protection",
            "Respiratory Equipment",
            "Overalls",
            "Gloves",
    };

    public static final By STEP_CHIP = AppiumBy.accessibilityId("PPE");

    /** Heading (content-desc 2 dòng "Personal Protective\nEquipment (PPE)"). */
    public static final By SCREEN_HEADING = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionStartsWith(\"Personal Protective\")");

    public static final By INTRO_TEXT = AppiumBy.accessibilityId(
            "Please indicate below which items of Personal Protective Equipment (PPE) you are in possession of.");

    public static final By NEXT_BUTTON = AppiumBy.accessibilityId("Next →");

    /** Nhãn item PPE. */
    public static By itemLabel(String item) {
        return AppiumBy.accessibilityId(item);
    }

    /** Radio "Yes" của item — radio đầu tiên sau nhãn item. */
    public static By itemYes(String item) {
        return AppiumBy.xpath("//android.view.View[@content-desc=\"" + item + "\"]"
                + "/following-sibling::android.widget.RadioButton[1]");
    }

    /** Radio "No" của item — radio thứ hai sau nhãn item. */
    public static By itemNo(String item) {
        return AppiumBy.xpath("//android.view.View[@content-desc=\"" + item + "\"]"
                + "/following-sibling::android.widget.RadioButton[2]");
    }
}
