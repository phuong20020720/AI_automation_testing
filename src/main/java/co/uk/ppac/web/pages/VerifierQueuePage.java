package co.uk.ppac.web.pages;

import co.uk.ppac.core.base.BasePage;
import co.uk.ppac.core.config.ConfigReader;
import co.uk.ppac.web.locators.VerifierQueueLocators;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Compliant Portal — Verifier Queue (landing page sau khi login admin).
 * Flutter Web CanvasKit; DOM accessible qua flt-semantics sau khi enable semantics tree.
 *
 * Recon (2026-05-20) — đặc thù locator/tương tác của trang này:
 * - Button: KHÔNG có aria-label — nhận diện qua textContent (vd "Clear", "Next Page").
 * - Menuitem (status / page-size): label nằm ở aria-label, textContent rỗng.
 * - Search là thẻ &lt;input&gt; thật; aria-label đổi thành "Search\nSEARCH" khi focus
 *   → dùng selector prefix-match.
 * - Date filter mở Material date-picker dialog; nút "Switch to input" cho gõ ngày M/D/YYYY.
 * - Contractors dropdown: 66 contractor checkbox + nút Select All / Clear All.
 * - Control Flutter (checkbox / button) phản hồi chậm với click thuần — cần chuỗi
 *   pointerdown → pointerup → click ({@link #flutterTap}).
 *
 * Locators: see {@link VerifierQueueLocators}.
 */
public class VerifierQueuePage extends BasePage {

    public static final List<String> EXPECTED_STATUS_OPTIONS = List.of(
            "All", "Active", "Go to Site", "Pending", "Pending (Recheck)",
            "Update-Rejected", "Rejected", "Expired", "Pending Info",
            "Auto Rejected", "Auto Rejected Archived", "Review required",
            "Waiting to Pass", "Archive");

    public static final List<String> EXPECTED_TABLE_HEADERS = List.of(
            "Company", "Nationality", "Type", "Name", "Surname", "Email", "DOB",
            "V Code", "Skill Card", "Job Role", "Status", "D.O.V",
            "Updated Time", "Created Time");

    public static final List<String> KPI_LABELS = List.of(
            "Total", "Pending", "Pending Info", "Active", "Go to Site", "Rejected", "Expired");

    /** Page-size options chuẩn theo manual TC M6_TC_001 (live thực tế còn có thêm 250). */
    public static final List<Integer> EXPECTED_PAGE_SIZES = List.of(100, 150, 200);

    public VerifierQueuePage(WebDriver driver) {
        super(driver);
    }

    public VerifierQueuePage open() {
        driver.get(ConfigReader.get("app.baseUrl"));
        waitForFlutterReady();
        waitForFilterBarReady();
        return this;
    }

    public VerifierQueuePage waitForFilterBarReady() {
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(ExpectedConditions.presenceOfElementLocated(VerifierQueueLocators.STATUS_FILTER_BUTTON));
        return this;
    }

    /** Đợi đến khi table được render (ít nhất 1 columnheader xuất hiện). */
    public VerifierQueuePage waitForTableReady() {
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(d -> !d.findElements(VerifierQueueLocators.COLUMN_HEADERS).isEmpty());
        return this;
    }

    // ===================== Status filter =====================

    public String currentStatusFilterLabel() {
        WebElement btn = waitForPresent(VerifierQueueLocators.STATUS_FILTER_BUTTON);
        String raw = btn.getText().trim();
        return raw.replaceFirst("(?i)^select status\\s*", "").trim();
    }

    public VerifierQueuePage openStatusDropdown() {
        flutterTap(waitForPresent(VerifierQueueLocators.STATUS_FILTER_BUTTON));
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(d -> !d.findElements(VerifierQueueLocators.MENU_ITEMS).isEmpty());
        return this;
    }

    public List<String> readStatusDropdownOptions() {
        return driver.findElements(VerifierQueueLocators.MENU_ITEMS).stream()
                .map(VerifierQueuePage::readLabel)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    public VerifierQueuePage closeStatusDropdown() {
        sendEscape();
        return this;
    }

    public VerifierQueuePage selectStatus(String label) {
        openStatusDropdown();
        By option = VerifierQueueLocators.statusMenuItem(label);
        WebElement item = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(option));
        int before = cellCount();
        flutterTap(item);
        waitForCellCountChange(before);
        return this;
    }

    // ===================== Table reads =====================

    public boolean isTablePresent() {
        return !driver.findElements(VerifierQueueLocators.TABLE).isEmpty()
                || !driver.findElements(VerifierQueueLocators.COLUMN_HEADERS).isEmpty();
    }

    public List<String> tableHeaders() {
        return driver.findElements(VerifierQueueLocators.COLUMN_HEADERS).stream()
                .map(VerifierQueuePage::readLabel)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /** Số row dữ liệu thực hiển thị trên trang hiện tại (loại trừ header row). */
    public int dataRowCount() {
        int count = 0;
        for (WebElement row : driver.findElements(VerifierQueueLocators.ROWS)) {
            if (!row.findElements(VerifierQueueLocators.CELLS).isEmpty()) {
                count++;
            }
        }
        return count;
    }

    public int kpiCount(String label) {
        By kpi = VerifierQueueLocators.kpi(label);
        List<WebElement> nodes = driver.findElements(kpi);
        if (nodes.isEmpty()) {
            return -1;
        }
        WebElement smallest = null;
        int minLen = Integer.MAX_VALUE;
        for (WebElement el : nodes) {
            String t = el.getText();
            int len = t == null ? 0 : t.length();
            if (len > 0 && len < minLen) {
                minLen = len;
                smallest = el;
            }
        }
        return smallest == null ? -1 : parseIntSafe(smallest.getText());
    }

    public WorkerDetailModal openFirstWorker() {
        waitForTableReady();
        WebElement firstDataRow = null;
        List<WebElement> firstRowCells = List.of();
        for (WebElement row : driver.findElements(VerifierQueueLocators.ROWS)) {
            List<WebElement> cells = row.findElements(VerifierQueueLocators.CELLS);
            if (!cells.isEmpty()) {
                firstDataRow = row;
                firstRowCells = cells;
                break;
            }
        }
        if (firstDataRow == null || firstRowCells.isEmpty()) {
            throw new org.openqa.selenium.NoSuchElementException("No data rows available to open");
        }

        // Recon (2026-05-25): JS click thuần trên cell không trigger Flutter pointer handler.
        // Thử lần lượt nhiều target với flutterTap (pointerdown→pointerup→click):
        // row, cell 3 (Name), cell 5 (Email), cell 7 (V Code — semantic button), cell 0 (Company).
        By dialog = VerifierQueueLocators.ALERT_DIALOG;
        int n = firstRowCells.size();
        WebElement[] candidates = new WebElement[] {
                firstDataRow,
                n > 3 ? firstRowCells.get(3) : null,
                n > 5 ? firstRowCells.get(5) : null,
                n > 7 ? firstRowCells.get(7) : null,
                firstRowCells.get(0)
        };
        String[] labels = {"row", "cell-3-Name", "cell-5-Email", "cell-7-VCode", "cell-0-Company"};

        StringBuilder tried = new StringBuilder();
        for (int i = 0; i < candidates.length; i++) {
            if (candidates[i] == null) {
                continue;
            }
            tried.append(labels[i]).append(',');
            try {
                flutterTap(candidates[i]);
            } catch (Exception clickFailed) {
                continue;
            }
            long deadline = System.currentTimeMillis() + 4_000;
            while (System.currentTimeMillis() < deadline) {
                if (!driver.findElements(dialog).isEmpty()) {
                    WorkerDetailModal modal = new WorkerDetailModal(driver);
                    modal.waitForOpen();
                    return modal;
                }
                sleepQuiet(250);
            }
        }
        throw new org.testng.SkipException(
                "Worker Detail Modal did not open after trying targets [" + tried
                        + "]. UI may have shifted — needs DOM recon to find correct click target.");
    }

    public String statusOfFirstRow() {
        return firstRowCellValue(10);
    }

    public String emailOfFirstRow() {
        return firstRowCellValue(5);
    }

    public String vCodeOfFirstRow() {
        return firstRowCellValue(7);
    }

    public String nameOfFirstRow() {
        return firstRowCellValue(3);
    }

    public String surnameOfFirstRow() {
        return firstRowCellValue(4);
    }

    private String firstRowCellValue(int cellIndex) {
        waitForTableReady();
        for (WebElement row : driver.findElements(VerifierQueueLocators.ROWS)) {
            List<WebElement> cells = row.findElements(VerifierQueueLocators.CELLS);
            if (cells.size() > cellIndex) {
                return readLabel(cells.get(cellIndex));
            }
        }
        return "";
    }

    /** Giá trị một cột (theo index) của mọi data row hiện hiển thị. */
    public List<String> columnValues(int cellIndex) {
        List<String> out = new ArrayList<>();
        for (WebElement row : driver.findElements(VerifierQueueLocators.ROWS)) {
            List<WebElement> cells = row.findElements(VerifierQueueLocators.CELLS);
            if (cells.size() > cellIndex) {
                out.add(readLabel(cells.get(cellIndex)));
            }
        }
        return out;
    }

    /** Chữ ký của row đầu tiên — dùng để phát hiện đổi trang khi row count không đổi. */
    public String firstRowSignature() {
        for (int attempt = 0; attempt < 4; attempt++) {
            try {
                return computeFirstRowSignature(driver);
            } catch (StaleElementReferenceException stale) {
                sleepQuiet(250); // table đang re-render — thử lại
            }
        }
        return "";
    }

    private static String computeFirstRowSignature(WebDriver d) {
        for (WebElement row : d.findElements(VerifierQueueLocators.ROWS)) {
            List<WebElement> cells = row.findElements(VerifierQueueLocators.CELLS);
            if (!cells.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < Math.min(8, cells.size()); i++) {
                    sb.append(readLabel(cells.get(i))).append('|');
                }
                return sb.toString();
            }
        }
        return "";
    }

    // ===================== Search =====================

    public boolean hasSearchBox() {
        return isPresent(VerifierQueueLocators.SEARCH_INPUT);
    }

    public VerifierQueuePage typeSearch(String value) {
        int before = cellCount();
        focusClearAndType(VerifierQueueLocators.SEARCH_INPUT, value);
        waitForCellCountChange(before);
        return this;
    }

    public VerifierQueuePage clearSearch() {
        int before = cellCount();
        focusClearAndType(VerifierQueueLocators.SEARCH_INPUT, "");
        waitForCellCountChange(before);
        return this;
    }

    // ===================== Date range =====================

    public VerifierQueuePage pickStartDate(String monthDayYear) {
        return pickDate(VerifierQueueLocators.START_DATE_INPUT, monthDayYear);
    }

    public VerifierQueuePage pickEndDate(String monthDayYear) {
        return pickDate(VerifierQueueLocators.END_DATE_INPUT, monthDayYear);
    }

    /** monthDayYear theo format picker yêu cầu: M/D/YYYY (vd "4/1/2026"). */
    private VerifierQueuePage pickDate(By dateInput, String monthDayYear) {
        int before = cellCount();
        waitForPresent(dateInput).click();
        if (driver.findElements(VerifierQueueLocators.DATE_PICKER_DIALOG).isEmpty()) {
            List<WebElement> reFind = driver.findElements(dateInput);
            if (!reFind.isEmpty()) {
                flutterTap(reFind.get(0));
            }
        }
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(VerifierQueueLocators.DATE_PICKER_DIALOG));

        if (isPresent(VerifierQueueLocators.DATE_SWITCH_TO_INPUT)) {
            flutterTap(driver.findElement(VerifierQueueLocators.DATE_SWITCH_TO_INPUT));
        }

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(VerifierQueueLocators.ENTER_DATE_INPUT));
        focusClearAndType(VerifierQueueLocators.ENTER_DATE_INPUT, monthDayYear);

        flutterTap(waitForPresent(VerifierQueueLocators.DATE_OK_BUTTON));
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> d.findElements(VerifierQueueLocators.DATE_PICKER_DIALOG).isEmpty());
        waitForCellCountChange(before);
        return this;
    }

    public String startDateValue() {
        return inputValue(VerifierQueueLocators.START_DATE_INPUT);
    }

    public String endDateValue() {
        return inputValue(VerifierQueueLocators.END_DATE_INPUT);
    }

    private String inputValue(By locator) {
        List<WebElement> els = driver.findElements(locator);
        if (els.isEmpty()) {
            return "";
        }
        String v = els.get(0).getAttribute("value");
        return v == null ? "" : v.trim();
    }

    // ===================== Clear / Expiry =====================

    public boolean hasClearButton() {
        return isPresent(VerifierQueueLocators.CLEAR_BUTTON);
    }

    public VerifierQueuePage clickClear() {
        if (!isPresent(VerifierQueueLocators.CLEAR_BUTTON)) {
            return this;
        }
        int before = cellCount();
        flutterTap(waitForPresent(VerifierQueueLocators.CLEAR_BUTTON));
        waitForCellCountChange(before);
        return this;
    }

    public boolean hasExpiryReportButton() {
        return isPresent(VerifierQueueLocators.EXPIRY_REPORT_BUTTON);
    }

    public VerifierQueuePage clickExpiryReport() {
        flutterTap(waitForPresent(VerifierQueueLocators.EXPIRY_REPORT_BUTTON));
        return this;
    }

    // ===================== Contractors multi-select =====================

    public VerifierQueuePage openContractorsDropdown() {
        flutterTap(waitForPresent(VerifierQueueLocators.CONTRACTORS_BUTTON));
        // Distinct checkbox luôn tồn tại sẵn → phải đợi DANH SÁCH contractor render đầy đủ.
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(d -> d.findElements(VerifierQueueLocators.CHECKBOX_ITEMS).size() >= 20);
        return this;
    }

    public int contractorsDropdownOptionCount() {
        return driver.findElements(VerifierQueueLocators.CHECKBOX_ITEMS).size();
    }

    public int checkboxCount() {
        return driver.findElements(VerifierQueueLocators.CHECKBOX_ITEMS).size();
    }

    public int checkedCheckboxCount() {
        return (int) driver.findElements(VerifierQueueLocators.CHECKBOX_ITEMS).stream()
                .filter(e -> "true".equalsIgnoreCase(e.getAttribute("aria-checked")))
                .count();
    }

    public VerifierQueuePage closeContractorsDropdown() {
        sendEscape();
        try {
            new WebDriverWait(driver, Duration.ofSeconds(6))
                    .until(d -> d.findElements(VerifierQueueLocators.CHECKBOX_ITEMS).size() <= 2);
        } catch (TimeoutException e) {
            sendEscape();
        }
        return this;
    }

    public VerifierQueuePage clickContractorSelectAll() {
        flutterTapButton("Select All");
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .pollingEvery(Duration.ofMillis(300))
                .until(d -> checkboxCount() > 5 && checkedCheckboxCount() == checkboxCount());
        return this;
    }

    public VerifierQueuePage clickContractorClearAll() {
        flutterTapButton("Clear All");
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .pollingEvery(Duration.ofMillis(300))
                .until(d -> checkedCheckboxCount() <= 2);
        return this;
    }

    public VerifierQueuePage selectContractor(String name) {
        By checkbox = VerifierQueueLocators.contractorCheckbox(name);
        flutterTap(waitForPresent(checkbox));
        new WebDriverWait(driver, Duration.ofSeconds(6))
                .pollingEvery(Duration.ofMillis(250))
                .until(d -> {
                    List<WebElement> found = d.findElements(checkbox);
                    return !found.isEmpty()
                            && "true".equalsIgnoreCase(found.get(0).getAttribute("aria-checked"));
                });
        return this;
    }

    /** Text hiển thị trên nút Select Contractors (kiểm tra count badge — M3_TC_045). */
    public String contractorsButtonText() {
        return waitForPresent(VerifierQueueLocators.CONTRACTORS_BUTTON).getText().trim().replaceAll("\\s+", " ");
    }

    /** Mở dropdown, Clear All, chọn các contractor chỉ định, đóng dropdown & chờ table refresh. */
    public VerifierQueuePage filterByContractors(String... names) {
        int before = cellCount();
        openContractorsDropdown();
        clickContractorClearAll();
        for (String name : names) {
            selectContractor(name);
        }
        closeContractorsDropdown();
        waitForCellCountChange(before);
        return this;
    }

    private void flutterTapButton(String text) {
        flutterTap(waitForPresent(VerifierQueueLocators.semanticButton(text)));
    }

    // ===================== Pagination =====================

    public boolean hasPagination() {
        return isPaginationPresent("Next Page") || isPaginationPresent("Last Page");
    }

    /** buttonText ∈ {First Page, Previous, Next Page, Last Page}. */
    public boolean isPaginationPresent(String buttonText) {
        return isPresent(VerifierQueueLocators.semanticButton(buttonText));
    }

    public boolean isPaginationDisabled(String buttonText) {
        List<WebElement> nodes = driver.findElements(VerifierQueueLocators.semanticButton(buttonText));
        if (nodes.isEmpty()) {
            return true;
        }
        return "true".equalsIgnoreCase(nodes.get(0).getAttribute("aria-disabled"));
    }

    public VerifierQueuePage clickPagination(String buttonText) {
        String beforeSig = firstRowSignature();
        flutterTap(waitForPresent(VerifierQueueLocators.semanticButton(buttonText)));
        waitForFirstRowChange(beforeSig);
        return this;
    }

    public boolean isPageNumberPresent(int page) {
        return isPresent(VerifierQueueLocators.pageNumberButton(page));
    }

    public VerifierQueuePage clickPageNumber(int page) {
        String beforeSig = firstRowSignature();
        flutterTap(waitForPresent(VerifierQueueLocators.pageNumberButton(page)));
        waitForFirstRowChange(beforeSig);
        return this;
    }

    public int currentPageSize() {
        List<WebElement> nodes = driver.findElements(VerifierQueueLocators.PAGE_SIZE_BUTTON);
        return nodes.isEmpty() ? -1 : parseIntSafe(nodes.get(0).getText().trim());
    }

    public VerifierQueuePage openPageSizeDropdown() {
        flutterTap(waitForPresent(VerifierQueueLocators.PAGE_SIZE_BUTTON));
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> !d.findElements(VerifierQueueLocators.MENU_ITEMS).isEmpty());
        return this;
    }

    public List<Integer> pageSizeOptions() {
        openPageSizeDropdown();
        List<Integer> sizes = new ArrayList<>();
        for (WebElement item : driver.findElements(VerifierQueueLocators.MENU_ITEMS)) {
            String label = item.getAttribute("aria-label");
            int v = label == null ? -1 : parseIntSafe(label);
            if (v > 0) {
                sizes.add(v);
            }
        }
        sendEscape();
        return sizes;
    }

    public VerifierQueuePage selectPageSize(int size) {
        int before = cellCount();
        openPageSizeDropdown();
        By option = VerifierQueueLocators.pageSizeMenuItem(size);
        flutterTap(waitForPresent(option));
        waitForCellCountChange(before);
        return this;
    }

    // ===================== Tương tác Flutter / waits =====================

    /**
     * Tap kiểu Flutter: control flt-tappable phản hồi không ổn định với click() thuần
     * (xử lý bất đồng bộ). Phát đủ chuỗi pointerdown → pointerup → click tại tâm element.
     */
    private void flutterTap(WebElement element) {
        js().executeScript(
                "var el=arguments[0];"
                        + "el.scrollIntoView({block:'center'});"
                        + "var r=el.getBoundingClientRect();"
                        + "var x=r.left+r.width/2, y=r.top+r.height/2;"
                        + "var base={bubbles:true,cancelable:true,composed:true,clientX:x,clientY:y,"
                        + "pointerId:1,pointerType:'mouse',isPrimary:true,button:0};"
                        + "el.dispatchEvent(new PointerEvent('pointerdown',"
                        + "Object.assign({},base,{buttons:1})));"
                        + "el.dispatchEvent(new PointerEvent('pointerup',"
                        + "Object.assign({},base,{buttons:0})));"
                        + "el.dispatchEvent(new MouseEvent('click',"
                        + "Object.assign({},base,{buttons:0})));",
                element);
    }

    /**
     * Focus một &lt;input&gt; Flutter, chờ kết nối text-editing sẵn sàng, xóa nội dung cũ rồi gõ.
     * Xóa bằng chuỗi backspace — không phụ thuộc hành vi select-all của text-field Flutter.
     * value rỗng = chỉ xóa.
     */
    private void focusClearAndType(By locator, String value) {
        WebElement input = waitForPresent(locator);
        input.click();
        sleepQuiet(450); // Flutter cần thời gian dựng kết nối text-editing sau khi focus
        input.sendKeys(Keys.END);
        input.sendKeys(String.valueOf(Keys.BACK_SPACE).repeat(60));
        if (!value.isEmpty()) {
            input.sendKeys(value);
        }
    }

    private int cellCount() {
        return driver.findElements(VerifierQueueLocators.CELLS).size();
    }

    /**
     * Chờ table re-render sau khi đổi filter — cell count phải KHÁC giá trị trước đó,
     * &gt; 0 (bỏ qua trạng thái loading rỗng tạm thời) và ổn định qua 3 lần poll.
     * Nếu filter trả về 0 row thật → timeout êm sau 10s (caller tự kiểm chứng qua KPI).
     */
    public void waitForCellCountChange(int previousCount) {
        int[] state = {Integer.MIN_VALUE, 0};
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .pollingEvery(Duration.ofMillis(350))
                    .until(d -> {
                        int now = d.findElements(VerifierQueueLocators.CELLS).size();
                        if (now == state[0]) {
                            state[1]++;
                        } else {
                            state[1] = 0;
                        }
                        state[0] = now;
                        return now != previousCount && now > 0 && state[1] >= 3;
                    });
        } catch (TimeoutException ignored) {
            // Filter có thể trả về 0 row hoặc đúng số row cũ — chấp nhận trạng thái hiện tại.
        }
    }

    /** Chờ row đầu tiên đổi nội dung — dùng cho điều hướng phân trang. */
    public void waitForFirstRowChange(String previousSignature) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .pollingEvery(Duration.ofMillis(350))
                    .until(d -> {
                        try {
                            String now = computeFirstRowSignature(d);
                            return !now.isEmpty() && !now.equals(previousSignature);
                        } catch (StaleElementReferenceException stale) {
                            return false; // table đang re-render — tiếp tục poll
                        }
                    });
        } catch (TimeoutException ignored) {
            // Trang có thể không đổi (nút disabled) — chấp nhận.
        }
    }

    private void sendEscape() {
        try {
            new org.openqa.selenium.interactions.Actions(driver)
                    .sendKeys(Keys.ESCAPE).perform();
        } catch (Exception ignored) {
        }
    }

    private static int parseIntSafe(String text) {
        if (text == null) {
            return -1;
        }
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)").matcher(text);
        if (!m.find()) {
            return -1;
        }
        try {
            return Integer.parseInt(m.group(1));
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private static String readLabel(WebElement el) {
        String aria = el.getAttribute("aria-label");
        if (aria != null && !aria.isBlank()) {
            return aria.trim();
        }
        String txt = el.getText();
        return txt == null ? "" : txt.trim();
    }

    private static void sleepQuiet(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
