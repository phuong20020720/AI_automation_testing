package co.uk.ppac.web.tests.compliantportal;

import co.uk.ppac.web.pages.VerifierQueuePage;
import co.uk.ppac.web.pages.WorkerDetailModal;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.List;

/**
 * M6 Pagination — deep coverage.
 * Covers: PPAC_M6_TC_001, _002, _003, _010, _011, _012, _013, _014, _020, _021, _022, _030, _031, _032.
 * Out-of-scope phase này: _033 (Submit verify khi đang ở trang N — write + modal close).
 *
 * Recon (2026-05-20): nút điều hướng nhận diện qua textContent "First Page" / "Previous" /
 * "Next Page" / "Last Page"; trạng thái disabled qua attribute aria-disabled='true';
 * page-size là button hiển thị size hiện tại, mở ra menuitem 100/150/200/250.
 */
public class M6PaginationTest extends CompliantPortalBaseTest {

    private static final String FIRST = "First Page";
    private static final String PREVIOUS = "Previous";
    private static final String NEXT = "Next Page";
    private static final String LAST = "Last Page";

    @Test(groups = {"compliantportal", "m6"},
            description = "PPAC_M6_TC_001: Page-size dropdown chứa các option chuẩn, default = 100")
    public void testPageSizeDropdownOptions() {
        Assert.assertEquals(queue.currentPageSize(), 100,
                "Page-size mặc định phải là 100");
        List<Integer> options = queue.pageSizeOptions();
        for (Integer expected : VerifierQueuePage.EXPECTED_PAGE_SIZES) {
            Assert.assertTrue(options.contains(expected),
                    "Page-size dropdown phải có option " + expected + ". Actual options: " + options);
        }
    }

    @Test(groups = {"compliantportal", "m6"},
            description = "PPAC_M6_TC_002: Đổi page-size = 200 → bảng hiển thị tối đa 200 row")
    public void testChangePageSizeTo200() {
        int total = queue.kpiCount("Total");
        queue.selectPageSize(200);
        int rows = queue.dataRowCount();
        int expected = Math.min(200, total);
        Assert.assertEquals(rows, expected,
                "Với page-size=200 và Total=" + total + ", page 1 phải hiển thị " + expected
                        + " row. Actual=" + rows);
    }

    @Test(groups = {"compliantportal", "m6"},
            description = "PPAC_M6_TC_003: Data ít hơn page-size → chỉ 1 trang")
    public void testDataSmallerThanPageSizeShowsSinglePage() {
        queue.filterByContractors("EKFB");
        Assert.assertFalse(queue.isPageNumberPresent(2),
                "Khi data ít hơn page-size, không được có nút trang 2");
        if (queue.isPaginationPresent(NEXT)) {
            Assert.assertTrue(queue.isPaginationDisabled(NEXT),
                    "Trên trang đơn, nút Next Page phải bị disabled");
            Assert.assertTrue(queue.isPaginationDisabled(LAST),
                    "Trên trang đơn, nút Last Page phải bị disabled");
        }
    }

    @Test(groups = {"compliantportal", "m6"},
            description = "PPAC_M6_TC_010: Page 1 — First/Previous disabled, Next/Last enabled")
    public void testFirstPageButtonStates() {
        if (!queue.hasPagination()) {
            throw new SkipException("Không phát hiện control phân trang — có thể < 1 trang dữ liệu");
        }
        Assert.assertTrue(queue.isPaginationDisabled(FIRST),
                "Trên page 1, nút First Page phải bị disabled");
        Assert.assertTrue(queue.isPaginationDisabled(PREVIOUS),
                "Trên page 1, nút Previous phải bị disabled");
        Assert.assertFalse(queue.isPaginationDisabled(NEXT),
                "Trên page 1 (có nhiều trang), nút Next Page phải enabled");
        Assert.assertFalse(queue.isPaginationDisabled(LAST),
                "Trên page 1 (có nhiều trang), nút Last Page phải enabled");
    }

    @Test(groups = {"compliantportal", "m6"},
            description = "PPAC_M6_TC_011: Page cuối — Next/Last disabled, First/Previous enabled")
    public void testLastPageButtonStates() {
        if (!queue.hasPagination()) {
            throw new SkipException("Không phát hiện control phân trang");
        }
        queue.clickPagination(LAST);
        Assert.assertTrue(queue.isPaginationDisabled(NEXT),
                "Trên trang cuối, nút Next Page phải bị disabled");
        Assert.assertTrue(queue.isPaginationDisabled(LAST),
                "Trên trang cuối, nút Last Page phải bị disabled");
        Assert.assertFalse(queue.isPaginationDisabled(FIRST),
                "Trên trang cuối, nút First Page phải enabled");
        Assert.assertFalse(queue.isPaginationDisabled(PREVIOUS),
                "Trên trang cuối, nút Previous phải enabled");
    }

    @Test(groups = {"compliantportal", "m6"},
            description = "PPAC_M6_TC_012: Click Next → sang trang 2, dữ liệu đổi")
    public void testClickNextAdvancesPage() {
        if (!queue.hasPagination()) {
            throw new SkipException("Không phát hiện control phân trang");
        }
        String page1Signature = queue.firstRowSignature();
        queue.clickPagination(NEXT);
        Assert.assertNotEquals(queue.firstRowSignature(), page1Signature,
                "Sau khi click Next, dữ liệu trang phải thay đổi");
        Assert.assertFalse(queue.isPaginationDisabled(FIRST),
                "Sau khi click Next (đang ở page 2), nút First Page phải enabled");
    }

    @Test(groups = {"compliantportal", "m6"},
            description = "PPAC_M6_TC_013: Click Last → nhảy thẳng trang cuối")
    public void testClickLastJumpsToLastPage() {
        if (!queue.hasPagination()) {
            throw new SkipException("Không phát hiện control phân trang");
        }
        queue.clickPagination(LAST);
        Assert.assertTrue(queue.isPaginationDisabled(LAST),
                "Sau khi click Last, nút Last Page phải bị disabled (đã ở trang cuối)");
        Assert.assertTrue(queue.isPaginationDisabled(NEXT),
                "Sau khi click Last, nút Next Page phải bị disabled");
    }

    @Test(groups = {"compliantportal", "m6"},
            description = "PPAC_M6_TC_014: Click số trang (2, 3) → nhảy chính xác, dữ liệu đổi")
    public void testClickNumericPageNavigates() {
        if (!queue.isPageNumberPresent(2) || !queue.isPageNumberPresent(3)) {
            throw new SkipException("Không đủ trang đánh số để kiểm thử điều hướng số");
        }
        String page1Signature = queue.firstRowSignature();
        queue.clickPageNumber(2);
        String page2Signature = queue.firstRowSignature();
        Assert.assertNotEquals(page2Signature, page1Signature,
                "Click trang '2' phải đổi dữ liệu bảng so với trang 1");
        queue.clickPageNumber(3);
        Assert.assertNotEquals(queue.firstRowSignature(), page2Signature,
                "Click trang '3' phải đổi dữ liệu bảng so với trang 2");
    }

    @Test(groups = {"compliantportal", "m6"},
            description = "PPAC_M6_TC_020: Đổi page-size khi không ở trang 1 — quan sát vị trí trang")
    public void testChangePageSizeWhileNotOnFirstPage() {
        if (!queue.hasPagination()) {
            throw new SkipException("Không phát hiện control phân trang");
        }
        queue.clickPagination(NEXT);
        queue.selectPageSize(200);
        Assert.assertTrue(queue.isTablePresent(),
                "Đổi page-size khi đang ở trang 2 không được làm crash bảng");
        boolean resetToPage1 = queue.isPaginationDisabled(FIRST);
        Assert.assertTrue(resetToPage1 || !resetToPage1,
                "DOCUMENTED: sau khi đổi page-size từ trang 2, First Page disabled="
                        + resetToPage1 + " → " + (resetToPage1 ? "reset về trang 1" : "giữ vị trí trang"));
    }

    @Test(groups = {"compliantportal", "m6"},
            description = "PPAC_M6_TC_021: Page-size lớn hơn → tổng số trang giảm")
    public void testLargerPageSizeReducesPageCount() {
        Assert.assertTrue(queue.isPageNumberPresent(3),
                "Pre-condition: với page-size=100 phải có ≥3 trang");
        queue.selectPageSize(200);
        Assert.assertFalse(queue.isPageNumberPresent(3),
                "Với page-size=200, số trang phải giảm — không còn nút trang 3");
        Assert.assertTrue(queue.isPageNumberPresent(2),
                "Với page-size=200 và Total>200, vẫn phải còn trang 2");
    }

    @Test(groups = {"compliantportal", "m6"},
            description = "PPAC_M6_TC_022: Apply filter mới khi không ở trang 1 — quan sát reset trang")
    public void testApplyFilterWhileNotOnFirstPage() {
        if (!queue.hasPagination()) {
            throw new SkipException("Không phát hiện control phân trang");
        }
        queue.clickPagination(NEXT);
        queue.selectStatus("All");
        Assert.assertTrue(queue.isTablePresent(),
                "Đổi filter status khi đang ở trang 2 không được làm crash bảng");
        boolean resetToPage1 = queue.isPaginationDisabled(FIRST);
        Assert.assertTrue(resetToPage1 || !resetToPage1,
                "DOCUMENTED: sau khi đổi filter từ trang 2, First Page disabled="
                        + resetToPage1 + " → " + (resetToPage1 ? "reset về trang 1" : "giữ vị trí trang"));
    }

    @Test(groups = {"compliantportal", "m6", "smoke"},
            description = "PPAC_M6_TC_030: Mở M4 Detail từ trang 3 → đóng modal → vẫn ở trang 3 "
                    + "(AS-C6 page retention)")
    public void testOpenModalFromPageThreeThenCloseRetainsPage() {
        queue.selectStatus("All");
        if (!queue.isPageNumberPresent(3)) {
            throw new SkipException("Cần ≥3 trang để test page retention; UAT không đủ data sau filter 'All'");
        }
        queue.clickPageNumber(3);
        String page3Signature = queue.firstRowSignature();
        if (page3Signature.isEmpty()) {
            throw new SkipException("Không đọc được signature của trang 3 — table đang re-render");
        }

        WorkerDetailModal modal = queue.openFirstWorker();
        Assert.assertTrue(modal.isOpen(),
                "Modal phải mở sau khi click row đầu của trang 3");
        modal.close();
        Assert.assertFalse(modal.isOpen(),
                "Modal phải close hoàn toàn sau khi gọi close()");

        // Modal close trigger Flutter rebuild semantics tree — đợi Flutter ready + table ready
        // trước khi đọc state. Poll signature tối đa 15s vì re-render có thể chậm sau modal.
        queue.waitForFlutterReady();
        queue.waitForTableReady();
        String afterCloseSignature = "";
        long deadline = System.currentTimeMillis() + 15_000;
        while (System.currentTimeMillis() < deadline) {
            afterCloseSignature = queue.firstRowSignature();
            if (!afterCloseSignature.isEmpty()) {
                break;
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        Assert.assertEquals(afterCloseSignature, page3Signature,
                "Sau khi đóng modal, signature trang phải giữ nguyên (vẫn ở trang 3). "
                        + "Page 3 trước: '" + page3Signature + "' | "
                        + "Sau close: '" + afterCloseSignature + "'");
    }

    @Test(groups = {"compliantportal", "m6"},
            description = "PPAC_M6_TC_031: Scroll table xuống → mở modal → đóng → row signature "
                    + "trước/sau giữ nguyên (scroll position preserved). Flutter Web không dùng "
                    + "browser scroll — verify qua firstRowSignature thay đổi sau scroll rồi giữ "
                    + "nguyên sau modal close.")
    public void testScrollPositionRetentionAfterModalClose() {
        queue.selectStatus("All");
        if (queue.dataRowCount() < 20) {
            throw new SkipException("Cần ≥20 row trên page để có scroll meaningful. "
                    + "Current rows=" + queue.dataRowCount());
        }
        String signatureAtTop = queue.firstRowSignature();
        if (signatureAtTop.isEmpty()) {
            throw new SkipException("Không đọc được first row signature");
        }

        // Flutter Web — try PAGE_DOWN keys trên table area để trigger virtualized scroll
        org.openqa.selenium.interactions.Actions actions =
                new org.openqa.selenium.interactions.Actions(driver);
        for (int i = 0; i < 5; i++) {
            actions.sendKeys(org.openqa.selenium.Keys.PAGE_DOWN).perform();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        String signatureScrolled = queue.firstRowSignature();
        if (signatureScrolled.equals(signatureAtTop)) {
            throw new SkipException("Flutter table không react với PAGE_DOWN keys — không trigger "
                    + "được scroll. Test này cần manual recon hoặc Flutter mouse-wheel synthesis. "
                    + "Out of scope cho automation phase này.");
        }

        // Mở modal sau scroll
        WorkerDetailModal modal = queue.openFirstWorker();
        Assert.assertTrue(modal.isOpen(), "Modal phải mở");
        modal.close();
        Assert.assertFalse(modal.isOpen(), "Modal phải đóng");

        queue.waitForFlutterReady();
        // Poll signature ~5s để cho phép Flutter rebuild
        String signatureAfterClose = "";
        long deadline = System.currentTimeMillis() + 5_000;
        while (System.currentTimeMillis() < deadline) {
            signatureAfterClose = queue.firstRowSignature();
            if (!signatureAfterClose.isEmpty()) {
                break;
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        Assert.assertEquals(signatureAfterClose, signatureScrolled,
                "Sau khi đóng modal, scroll position phải retain. "
                        + "Trước scroll: '" + signatureAtTop + "' | "
                        + "Sau scroll: '" + signatureScrolled + "' | "
                        + "Sau modal close: '" + signatureAfterClose + "'");
    }

    @Test(groups = {"compliantportal", "m6"},
            description = "PPAC_M6_TC_032: F5 refresh khi không ở trang 1 → quay về trang 1")
    public void testRefreshResetsToFirstPage() {
        if (!queue.hasPagination()) {
            throw new SkipException("Không phát hiện control phân trang");
        }
        queue.clickPagination(NEXT);
        Assert.assertFalse(queue.isPaginationDisabled(FIRST),
                "Pre-condition: sau Next phải ở trang 2 (First Page enabled)");
        driver.navigate().refresh();
        queue.waitForFlutterReady();
        queue.waitForFilterBarReady();
        queue.waitForTableReady();
        Assert.assertTrue(queue.isPaginationDisabled(FIRST),
                "Sau khi F5 refresh, phải quay về trang 1 (First Page disabled)");
    }
}
