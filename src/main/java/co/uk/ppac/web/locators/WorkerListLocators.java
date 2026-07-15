package co.uk.ppac.web.locators;

import org.openqa.selenium.By;

/**
 * Locators tập trung cho Worker Management list view (React + Radix, table dùng {@code data-slot}).
 * Nhóm {@code ROW_*} là locator tương đối ({@code .//}) dùng với {@code row.findElement(...)}.
 */
public final class WorkerListLocators {

    private WorkerListLocators() {
    }

    public static final By TABLE = By.cssSelector("table[data-slot='table']");
    public static final By HEADER_CELLS = By.cssSelector("th[data-slot='table-head']");
    public static final By ROWS = By.cssSelector("tr[data-slot='table-row']");
    public static final By SEARCH_INPUT = By.cssSelector("input.search-input[placeholder='Search']");
    public static final By FILTERS_BUTTON =
            By.xpath("//button[.//span[normalize-space(.)='Filters']]");
    public static final By PAGINATION = By.cssSelector("nav[data-slot='pagination']");
    public static final By PAGINATION_NEXT =
            By.cssSelector("a[data-slot='pagination-link'][aria-label='Go to next page']");
    public static final By PAGINATION_PREV =
            By.cssSelector("a[data-slot='pagination-link'][aria-label='Go to previous page']");
    public static final By EMPTY_STATE_MARKER =
            By.xpath("//*[contains(translate(normalize-space(.), 'NORESULTS', 'noresults'),'no result')"
                    + " or contains(translate(normalize-space(.), 'NOWORKERS', 'noworkers'),'no worker')"
                    + " or contains(translate(normalize-space(.), 'NODATA', 'nodata'),'no data')]");

    // ===== Locator tương đối trong 1 row (dùng với row.findElement / findElements) =====
    public static final By ROW_CELLS = By.cssSelector("td[data-slot='table-cell']");
    public static final By ROW_STATUS_BADGE = By.cssSelector("span[data-slot='badge']");
    public static final By ROW_SELFIE_THUMBNAIL =
            By.xpath(".//div[@role='button' and @aria-label='Open preview in fullscreen']");
    public static final By ROW_FULL_PROFILE_BUTTON =
            By.xpath(".//button[.//p[normalize-space(.)='View Full Profile']]");
    public static final By ROW_DELETE_BUTTON =
            By.xpath(".//button[.//*[local-name()='svg' and contains(@class,'lucide-trash')]]");
}
