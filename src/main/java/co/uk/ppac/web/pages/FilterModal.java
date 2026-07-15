package co.uk.ppac.web.pages;

import co.uk.ppac.core.base.BasePage;
import co.uk.ppac.web.locators.FilterLocators;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;

/**
 * Filter popover/dialog mở khi click button `Filters` ở list view.
 * UI thật (verified qua recon `05_filter_modal_elements.json`) có 5 fields visible:
 * Status (button chips), Site location (search input), Sub contractor (search input),
 * Start date, End date (cả 2 placeholder DD-MM-YYYY). Locators: see {@link FilterLocators}.
 *
 * Manual TC giả định Job position + Submission Date + Effective Date — KHÔNG khớp UI thật.
 */
public class FilterModal extends BasePage {

    public static final List<String> EXPECTED_VISIBLE_LABELS = List.of(
            "Status", "Site location", "Sub contractor", "Start date", "End date");

    public FilterModal(WebDriver driver) {
        super(driver);
    }

    public FilterModal waitForOpen() {
        new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(FilterLocators.DIALOG));
        return this;
    }

    public boolean isOpen() {
        return isPresent(FilterLocators.DIALOG);
    }

    public List<String> visibleFieldLabels() {
        return driver.findElements(FilterLocators.SECTION_LABELS_VISIBLE).stream()
                .map(WebElement::getText)
                .map(s -> s.replaceAll("\\s+", " ").trim())
                .filter(s -> !s.isBlank())
                .toList();
    }

    public List<String> statusChipLabels() {
        return driver.findElements(FilterLocators.STATUS_CHIPS).stream()
                .map(WebElement::getText)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }

    public FilterModal selectStatus(String statusLabel) {
        WebElement chip = driver.findElement(FilterLocators.statusChip(statusLabel));
        js().executeScript("arguments[0].click();", chip);
        return this;
    }

    public FilterModal typeStartDate(String value) {
        WebElement input = waitForVisible(FilterLocators.START_DATE_INPUT);
        input.clear();
        input.sendKeys(value);
        return this;
    }

    public FilterModal typeEndDate(String value) {
        WebElement input = waitForVisible(FilterLocators.END_DATE_INPUT);
        input.clear();
        input.sendKeys(value);
        return this;
    }

    public boolean hasField(String label) {
        return visibleFieldLabels().contains(label);
    }

    public WorkerListPage clickApply() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(FilterLocators.APPLY_BUTTON));
        js().executeScript("arguments[0].click();", btn);
        waitForClose();
        WorkerListPage list = new WorkerListPage(driver);
        list.waitForListReady();
        return list;
    }

    public WorkerListPage clickCancel() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(FilterLocators.CANCEL_BUTTON));
        js().executeScript("arguments[0].click();", btn);
        waitForClose();
        WorkerListPage list = new WorkerListPage(driver);
        list.waitForListReady();
        return list;
    }

    public FilterModal clickReset() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(FilterLocators.RESET_BUTTON));
        js().executeScript("arguments[0].click();", btn);
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        return this;
    }

    private void waitForClose() {
        new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.invisibilityOfElementLocated(FilterLocators.DIALOG));
    }

    /**
     * Helper cho M11 security test — thử type payload thẳng vào date input.
     * Trả về true nếu type được (input non-readonly). Browser thường strip non-date chars
     * cho input[type=date] nhưng UI ở đây là text input format DD-MM-YYYY nên có thể nhận.
     */
    public boolean tryTypeIntoFirstDateInput(String payload) {
        List<WebElement> inputs = driver.findElements(FilterLocators.START_DATE_INPUT);
        if (inputs.isEmpty()) {
            return false;
        }
        WebElement input = inputs.get(0);
        try {
            input.clear();
            input.sendKeys(payload);
            return true;
        } catch (org.openqa.selenium.InvalidElementStateException e) {
            return false;
        }
    }

    /** Apply nếu button enabled, ngược lại đóng modal. Dùng cho security test fallback. */
    public void applyIfPossible() {
        List<WebElement> applies = driver.findElements(FilterLocators.APPLY_BUTTON);
        if (!applies.isEmpty()) {
            String aria = applies.get(0).getAttribute("aria-disabled");
            if (aria == null || !"true".equalsIgnoreCase(aria)) {
                try {
                    js().executeScript("arguments[0].click();", applies.get(0));
                    return;
                } catch (Exception ignored) {
                }
            }
        }
        close();
    }

    public void close() {
        new org.openqa.selenium.interactions.Actions(driver)
                .sendKeys(org.openqa.selenium.Keys.ESCAPE).perform();
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
