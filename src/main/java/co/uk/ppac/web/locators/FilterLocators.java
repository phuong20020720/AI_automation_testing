package co.uk.ppac.web.locators;
import co.uk.ppac.core.utils.LocatorUtils;

import org.openqa.selenium.By;

/**
 * Locators tập trung cho Filter popover/dialog mở từ button {@code Filters} ở list view.
 */
public final class FilterLocators {

    private FilterLocators() {
    }

    public static final By DIALOG = By.cssSelector("div[role='dialog'][data-slot='popover-content']");
    public static final By SECTION_LABELS_VISIBLE = By.xpath(
            "//div[@role='dialog']//section[not(contains(@class,'hidden'))]"
                    + "//p[not(ancestor::section[contains(@class,'hidden')])"
                    + " and string-length(normalize-space(.)) > 2"
                    + " and string-length(normalize-space(.)) < 30]");
    public static final By STATUS_CHIPS = By.xpath(
            "//div[@role='dialog']//section[.//p[normalize-space(.)='Status']]//button");
    public static final By START_DATE_INPUT = By.xpath(
            "(//div[@role='dialog']//input[@placeholder='DD-MM-YYYY'])[1]");
    public static final By END_DATE_INPUT = By.xpath(
            "(//div[@role='dialog']//input[@placeholder='DD-MM-YYYY'])[2]");
    public static final By APPLY_BUTTON = By.xpath(
            "//div[@role='dialog']//button[normalize-space(.)='Apply']");
    public static final By CANCEL_BUTTON = By.xpath(
            "//div[@role='dialog']//button[normalize-space(.)='Cancel']");
    public static final By RESET_BUTTON = By.xpath(
            "//div[@role='dialog']//button[normalize-space(.)='Reset all filters']");

    /** Chip Status cụ thể theo nhãn (dùng cho selectStatus). */
    public static By statusChip(String label) {
        return By.xpath(
                "//div[@role='dialog']//section[.//p[normalize-space(.)='Status']]"
                        + "//button[.//span[normalize-space(.)=" + LocatorUtils.xpathLiteral(label) + "]]");
    }
}
