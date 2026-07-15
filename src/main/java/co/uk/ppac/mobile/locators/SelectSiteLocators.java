package co.uk.ppac.mobile.locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Locators tập trung cho New Check Step 2 — chọn site location + subcontractor.
 */
public final class SelectSiteLocators {

    private SelectSiteLocators() {
    }

    public static final By SITE_LOCATION_HEADING = AppiumBy.xpath(
            "//android.view.View[@content-desc='Select site location']");
    public static final By SITE_LOCATION_FIELD = AppiumBy.xpath(
            "//android.widget.ImageView[@content-desc='Select site location']");
    public static final By SUBCONTRACTOR_FIELD = AppiumBy.xpath(
            "//android.widget.ImageView[@content-desc='Select subcontractor']");
    public static final By CANT_FIND_SUBCONTRACTOR_CHECKBOX =
            AppiumBy.accessibilityId("I can't find my subcontractor in the list");
    public static final By CONTINUE_BUTTON = AppiumBy.accessibilityId("Continue →");

    /**
     * Mọi option trong modal bottom-sheet picker theo thứ tự hiển thị (clickable
     * View có content-desc). Loại trừ lớp nền mờ "Scrim" (modal barrier - tap vào
     * sẽ đóng picker) và nút "Continue →" cùng nằm trong cây.
     */
    public static final By PICKER_OPTIONS = AppiumBy.xpath(
            "//android.view.View[@clickable='true' and @content-desc "
                    + "and @content-desc != 'Scrim' and @content-desc != 'Continue →']");

    /** Option trong modal bottom-sheet picker (clickable View theo content-desc). */
    public static By pickerOption(String name) {
        return AppiumBy.xpath(
                "//android.view.View[@clickable='true' and @content-desc='" + name + "']");
    }

    /** Field sau khi đã chọn giá trị — content-desc của ImageView đổi thành tên đã chọn. */
    public static By selectedField(String name) {
        return AppiumBy.xpath("//android.widget.ImageView[@content-desc='" + name + "']");
    }
}
