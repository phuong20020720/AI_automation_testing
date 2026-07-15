package co.uk.ppac.web.pages;

import co.uk.ppac.core.base.BasePage;
import co.uk.ppac.core.config.ConfigReader;
import co.uk.ppac.web.locators.WorkerListLocators;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Trang Worker Management list view (`/en/worker-management`).
 * UI: React + Radix, table semantic dùng `data-slot`. Locators verified từ
 * recon snapshot tại `plans/automation/ppac_worker_management/recon/`.
 * Locators: see {@link WorkerListLocators}.
 */
public class WorkerListPage extends BasePage {

    public static final List<String> EXPECTED_HEADERS = List.of(
            "Code", "Subcontractor", "Citizenship", "First name Last name",
            "Email", "Verification", "Selfie", "Full profile", "Delete");

    public WorkerListPage(WebDriver driver) {
        super(driver);
    }

    public WorkerListPage open() {
        String url = "https://" + ConfigReader.get("app.dashboardHost")
                + ConfigReader.get("app.workerManagementPath");
        driver.get(url);
        try {
            waitForListReady(20);
        } catch (org.openqa.selenium.TimeoutException firstTimeout) {
            driver.navigate().refresh();
            waitForListReady(45);
        }
        return this;
    }

    public WorkerListPage waitForListReady() {
        return waitForListReady(30);
    }

    public WorkerListPage waitForListReady(int seconds) {
        new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(seconds))
                .until(d -> !d.findElements(WorkerListLocators.TABLE).isEmpty()
                        && !d.findElements(WorkerListLocators.HEADER_CELLS).isEmpty());
        return this;
    }

    public List<String> headerTexts() {
        return driver.findElements(WorkerListLocators.HEADER_CELLS).stream()
                .map(WebElement::getText)
                .map(s -> s.replaceAll("\\s+", " ").trim())
                .collect(Collectors.toList());
    }

    public int rowCount() {
        return driver.findElements(WorkerListLocators.ROWS).size();
    }

    public List<WebElement> rows() {
        return driver.findElements(WorkerListLocators.ROWS);
    }

    public WebElement rowAt(int index) {
        List<WebElement> all = rows();
        if (index < 0 || index >= all.size()) {
            throw new IndexOutOfBoundsException("Row index " + index + " out of range (rows=" + all.size() + ")");
        }
        return all.get(index);
    }

    public String codeOfRow(int index) {
        return cellText(index, 0);
    }

    public String emailOfRow(int index) {
        return cellText(index, 4).replace("\n", "");
    }

    public String statusOfRow(int index) {
        WebElement row = rowAt(index);
        List<WebElement> badges = row.findElements(WorkerListLocators.ROW_STATUS_BADGE);
        if (badges.isEmpty()) {
            return "";
        }
        return badges.get(0).getText().trim();
    }

    public boolean rowHasSelfieThumbnail(int index) {
        WebElement row = rowAt(index);
        return !row.findElements(WorkerListLocators.ROW_SELFIE_THUMBNAIL).isEmpty();
    }

    public boolean rowHasFullProfileButton(int index) {
        WebElement row = rowAt(index);
        return !row.findElements(WorkerListLocators.ROW_FULL_PROFILE_BUTTON).isEmpty();
    }

    public boolean rowHasDeleteButton(int index) {
        WebElement row = rowAt(index);
        return !row.findElements(WorkerListLocators.ROW_DELETE_BUTTON).isEmpty();
    }

    public boolean rowHasStatusBadge(int index) {
        return !rowAt(index).findElements(WorkerListLocators.ROW_STATUS_BADGE).isEmpty();
    }

    /** Trả về row index đầu tiên thỏa đủ điều kiện: có Full Profile + Delete + status + selfie. */
    public int firstFullyPopulatedRow() {
        int count = rowCount();
        for (int i = 0; i < count; i++) {
            if (rowHasFullProfileButton(i) && rowHasDeleteButton(i) && rowHasStatusBadge(i)) {
                return i;
            }
        }
        return -1;
    }

    public int firstRowWithFullProfileButton() {
        int count = rowCount();
        for (int i = 0; i < count; i++) {
            if (rowHasFullProfileButton(i)) {
                return i;
            }
        }
        return -1;
    }

    public int firstRowWithDeleteButton() {
        int count = rowCount();
        for (int i = 0; i < count; i++) {
            if (rowHasDeleteButton(i)) {
                return i;
            }
        }
        return -1;
    }

    public int firstRowWithStatusBadge() {
        int count = rowCount();
        for (int i = 0; i < count; i++) {
            if (rowHasStatusBadge(i)) {
                return i;
            }
        }
        return -1;
    }

    public int firstRowWithSelfie() {
        int count = rowCount();
        for (int i = 0; i < count; i++) {
            if (rowHasSelfieThumbnail(i)) {
                return i;
            }
        }
        return -1;
    }

    public int firstRowIndexWithStatus(String statusText) {
        int count = rowCount();
        for (int i = 0; i < count; i++) {
            if (statusText.equalsIgnoreCase(statusOfRow(i))) {
                return i;
            }
        }
        return -1;
    }

    private String cellText(int rowIdx, int colIdx) {
        List<WebElement> cells = rowAt(rowIdx).findElements(WorkerListLocators.ROW_CELLS);
        if (colIdx < 0 || colIdx >= cells.size()) {
            return "";
        }
        return cells.get(colIdx).getText().trim();
    }

    public WorkerProfilePage clickFullProfileAt(int rowIdx) {
        WebElement btn = rowAt(rowIdx).findElement(WorkerListLocators.ROW_FULL_PROFILE_BUTTON);
        js().executeScript("arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", btn);
        WorkerProfilePage profile = new WorkerProfilePage(driver);
        profile.waitForReady();
        return profile;
    }

    public DeleteConfirmModal clickDeleteAt(int rowIdx) {
        WebElement btn = rowAt(rowIdx).findElement(WorkerListLocators.ROW_DELETE_BUTTON);
        js().executeScript("arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", btn);
        DeleteConfirmModal modal = new DeleteConfirmModal(driver);
        modal.waitForOpen();
        return modal;
    }

    public WorkerListPage search(String query) {
        WebElement input = waitForVisible(WorkerListLocators.SEARCH_INPUT);
        input.clear();
        input.sendKeys(query);
        try { Thread.sleep(1500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        return this;
    }

    public boolean isFiltersButtonDisplayed() {
        return isPresent(WorkerListLocators.FILTERS_BUTTON);
    }

    public FilterModal openFilters() {
        WebElement btn = waitForVisible(WorkerListLocators.FILTERS_BUTTON);
        js().executeScript("arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", btn);
        FilterModal modal = new FilterModal(driver);
        modal.waitForOpen();
        return modal;
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    public boolean isPaginationDisplayed() {
        return isPresent(WorkerListLocators.PAGINATION);
    }

    public boolean isPaginationNextEnabled() {
        List<WebElement> next = driver.findElements(WorkerListLocators.PAGINATION_NEXT);
        if (next.isEmpty()) {
            return false;
        }
        String disabled = next.get(0).getAttribute("aria-disabled");
        return disabled == null || !"true".equalsIgnoreCase(disabled);
    }

    public WorkerListPage clickNextPage() {
        wait.until(ExpectedConditions.elementToBeClickable(WorkerListLocators.PAGINATION_NEXT));
        WebElement next = driver.findElement(WorkerListLocators.PAGINATION_NEXT);
        js().executeScript("arguments[0].click();", next);
        try { Thread.sleep(800); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        return this;
    }

    public boolean isEmptyStateDisplayed() {
        return rowCount() == 0
                || isPresent(WorkerListLocators.EMPTY_STATE_MARKER);
    }

    /** Trả về text của empty-state copy (vd "No data available"). Rỗng nếu không tìm thấy. */
    public String emptyStateText() {
        List<WebElement> markers = driver.findElements(WorkerListLocators.EMPTY_STATE_MARKER);
        if (markers.isEmpty()) {
            return "";
        }
        // Lấy element sâu nhất (text node trực tiếp) để tránh trả về cả body
        for (WebElement m : markers) {
            String t = m.getText().trim();
            if (!t.isBlank() && t.length() < 120) {
                return t;
            }
        }
        return markers.get(markers.size() - 1).getText().trim();
    }

    /** M10 — set viewport size + chờ relayout. */
    public WorkerListPage setViewport(int width, int height) {
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(width, height));
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        return this;
    }

    /** M10 — body horizontal overflow (px). 0 = không tràn ngang. */
    public long bodyHorizontalOverflow() {
        Long scrollW = (Long) js().executeScript("return document.body.scrollWidth;");
        Long clientW = (Long) js().executeScript("return document.body.clientWidth;");
        if (scrollW == null || clientW == null) {
            return 0;
        }
        return scrollW - clientW;
    }

    public boolean isTableVisible() {
        return isPresent(WorkerListLocators.TABLE);
    }

    public boolean isOnListUrl() {
        String url = driver.getCurrentUrl();
        return url.contains(ConfigReader.get("app.workerManagementPath"))
                && !url.contains("?id=");
    }
}
