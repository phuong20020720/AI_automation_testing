package co.uk.ppac.web.locators;
import co.uk.ppac.core.utils.LocatorUtils;

import org.openqa.selenium.By;

/**
 * Locators tập trung cho Verifier Queue (Flutter Web CanvasKit, DOM qua flt-semantics).
 * Các locator động (theo nhãn status, contractor, page number...) là factory method.
 */
public final class VerifierQueueLocators {

    private VerifierQueueLocators() {
    }

    public static final By STATUS_FILTER_BUTTON =
            By.xpath("//flt-semantics[@role='button' and contains(normalize-space(.),'Select status')]");
    public static final By CONTRACTORS_BUTTON =
            By.xpath("//flt-semantics[@role='button' and contains(normalize-space(.),'Select Contractors')]");
    public static final By CLEAR_BUTTON =
            By.xpath("//flt-semantics[@role='button' and normalize-space(.)='Clear']");
    public static final By EXPIRY_REPORT_BUTTON =
            By.xpath("//flt-semantics[@role='button' and contains(normalize-space(.),'Expiry Report')]");

    // Search là <input> thật; aria-label đổi khi focus → prefix-match.
    public static final By SEARCH_INPUT = By.cssSelector("input[aria-label^='Search']");
    public static final By START_DATE_INPUT = By.cssSelector("input[aria-label='Start Date']");
    public static final By END_DATE_INPUT = By.cssSelector("input[aria-label='End date']");
    public static final By DATE_PICKER_DIALOG = By.cssSelector("flt-semantics[role='dialog']");
    public static final By ENTER_DATE_INPUT = By.cssSelector("input[aria-label^='Enter Date']");

    public static final By TABLE = By.cssSelector("flt-semantics[role='table']");
    public static final By COLUMN_HEADERS = By.cssSelector("flt-semantics[role='columnheader']");
    public static final By ROWS = By.cssSelector("flt-semantics[role='row']");
    public static final By CELLS = By.cssSelector("flt-semantics[role='cell']");
    public static final By MENU_ITEMS = By.cssSelector("flt-semantics[role='menuitem']");
    public static final By CHECKBOX_ITEMS = By.cssSelector("flt-semantics[role='checkbox']");

    public static final By PAGE_SIZE_BUTTON = By.xpath(
            "//flt-semantics[@role='button' and "
                    + "(normalize-space(.)='100' or normalize-space(.)='150' "
                    + "or normalize-space(.)='200' or normalize-space(.)='250')]");

    // Worker Detail Modal mở từ row (dùng để chờ modal xuất hiện sau khi tap row/cell).
    public static final By ALERT_DIALOG = By.cssSelector("flt-semantics[role='alertdialog']");

    // Material date-picker dialog controls.
    public static final By DATE_SWITCH_TO_INPUT = By.xpath(
            "//flt-semantics[@role='dialog']//flt-semantics[@role='button' and normalize-space(.)='Switch to input']");
    public static final By DATE_OK_BUTTON = By.xpath(
            "//flt-semantics[@role='dialog']//flt-semantics[@role='button' and normalize-space(.)='OK']");

    /** Menu item Status theo aria-label. */
    public static By statusMenuItem(String label) {
        return By.xpath(String.format(
                "//flt-semantics[@role='menuitem' and @aria-label=%s]", LocatorUtils.xpathLiteral(label)));
    }

    /** KPI badge theo nhãn (vd "Total", "Pending"). */
    public static By kpi(String label) {
        return By.xpath(String.format(
                "//flt-semantics[contains(normalize-space(.), %s)]", LocatorUtils.xpathLiteral(label + ":")));
    }

    /** Checkbox contractor theo tên (sibling trước là node tên). */
    public static By contractorCheckbox(String name) {
        return By.xpath("//flt-semantics[@role='checkbox']"
                + "[preceding-sibling::flt-semantics[1][normalize-space(.)=" + LocatorUtils.xpathLiteral(name) + "]]");
    }

    /** Semantic button theo text (dùng cho Select All/Clear All, pagination...). */
    public static By semanticButton(String text) {
        return By.xpath("//flt-semantics[@role='button' and normalize-space(.)="
                + LocatorUtils.xpathLiteral(text) + "]");
    }

    /** Nút số trang theo số (vd "2"). */
    public static By pageNumberButton(int page) {
        return By.xpath("//flt-semantics[@role='button' and normalize-space(.)='" + page + "']");
    }

    /** Menu item page-size theo số. */
    public static By pageSizeMenuItem(int size) {
        return By.xpath("//flt-semantics[@role='menuitem' and @aria-label='" + size + "']");
    }
}
