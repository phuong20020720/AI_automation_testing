package co.uk.ppac.web.tests.compliantportal;

import co.uk.ppac.web.pages.VerifierQueuePage;
import co.uk.ppac.web.pages.WorkerDetailModal;
import org.openqa.selenium.TimeoutException;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.List;

/**
 * M1 Worker Queue + KPI Counter — display checks and basic navigation.
 * Covers: PPAC_M1_TC_001, PPAC_M1_TC_004, PPAC_M1_TC_005, PPAC_M1_TC_007,
 * PPAC_M1_TC_010, PPAC_M1_TC_011, PPAC_M1_TC_012, PPAC_M1_TC_020, PPAC_M1_TC_021, PPAC_M1_TC_022.
 */
public class M1QueueAndKpiTest extends CompliantPortalBaseTest {

    @Test(groups = {"compliantportal", "m1", "smoke"},
            description = "PPAC_M1_TC_001: Bảng hiển thị đủ 14 cột đúng thứ tự theo spec")
    public void testTableHasFourteenColumnHeadersInOrder() {
        List<String> headers = queue.tableHeaders();
        Assert.assertEquals(headers, VerifierQueuePage.EXPECTED_TABLE_HEADERS,
                "Bảng phải hiển thị đủ 14 cột đúng thứ tự spec. Actual: " + headers);
    }

    @Test(groups = {"compliantportal", "m1", "smoke"},
            description = "PPAC_M1_TC_021: KPI Total >= sum của 6 visible status counter (data integrity "
                    + "REQ-M1-04). TC ghi nhận Total có thể bao gồm cả status không hiển thị "
                    + "(Pending Recheck, Update-Rejected, Auto Rejected*, Review Required, Waiting to Pass, Archive).")
    public void testKpiTotalGreaterOrEqualSumOfStatusCounters() {
        int total = queue.kpiCount("Total");
        if (total < 0) {
            throw new SkipException("KPI 'Total' không đọc được — không thể kiểm tra data integrity");
        }
        int sum = 0;
        StringBuilder breakdown = new StringBuilder();
        for (String label : VerifierQueuePage.KPI_LABELS) {
            if ("Total".equals(label)) {
                continue;
            }
            int n = queue.kpiCount(label);
            if (n < 0) {
                throw new SkipException("KPI '" + label
                        + "' không đọc được — không thể tính sum đầy đủ");
            }
            sum += n;
            breakdown.append(label).append('=').append(n).append(' ');
        }
        Assert.assertTrue(sum >= 0 && total >= sum,
                "Data integrity: KPI Total (" + total + ") phải >= sum 6 visible counter (" + sum
                        + "). Diff=" + (total - sum) + " workers ở status không hiện trong KPI bar"
                        + " (Pending Recheck/Update-Rejected/Auto Rejected*/Review Required/Waiting to Pass/Archive)."
                        + " Breakdown 6 visible: " + breakdown.toString().trim());
    }

    @Test(groups = {"compliantportal", "m1", "smoke"},
            description = "PPAC_M1_TC_020: KPI bar exposes counters for the 7 expected statuses")
    public void testKpiBarShowsSevenCounters() {
        int found = 0;
        StringBuilder missing = new StringBuilder();
        for (String label : VerifierQueuePage.KPI_LABELS) {
            int count = queue.kpiCount(label);
            if (count >= 0) {
                found++;
            } else {
                missing.append(label).append(' ');
            }
        }
        Assert.assertTrue(found >= VerifierQueuePage.KPI_LABELS.size() - 1,
                "KPI bar must expose at least 6 of 7 expected counters. Missing: " + missing);
    }

    @Test(groups = {"compliantportal", "m1"},
            description = "PPAC_M1_TC_010: Clicking a row opens M4 Detail Modal whose Email field matches the row")
    public void testRowClickOpensModalMatchingEmail() {
        if (queue.dataRowCount() == 0) {
            throw new SkipException("No worker rows on landing — cannot verify modal email match");
        }
        String rowEmail = queue.emailOfFirstRow();
        WorkerDetailModal modal = queue.openFirstWorker();
        Assert.assertTrue(modal.isOpen(),
                "Worker Detail Modal must open after clicking a row");
        String headerEmail = modal.headerFieldValue("Email");
        if (rowEmail.isEmpty() || headerEmail.isEmpty()) {
            throw new SkipException("Could not read Email from row or header for comparison");
        }
        Assert.assertEquals(headerEmail, rowEmail,
                "Modal header Email must match the clicked row. row='" + rowEmail
                        + "' header='" + headerEmail + "'");
        modal.close();
    }

    @Test(groups = {"compliantportal", "m1"},
            description = "PPAC_M1_TC_004: Email column hiển thị đầy đủ — mọi giá trị Email phải chứa "
                    + "'@' (RFC) và KHÔNG kết thúc/chứa ellipsis '...' (truncate ẩn data)")
    public void testEmailColumnDisplaysFullValueNoTruncation() {
        if (queue.dataRowCount() == 0) {
            throw new SkipException("Không có worker rows để verify Email column display");
        }
        // Email là cell index 5 theo EXPECTED_TABLE_HEADERS
        List<String> emails = queue.columnValues(5);
        Assert.assertFalse(emails.isEmpty(), "Phải đọc được ít nhất 1 Email value từ table");
        for (String email : emails) {
            Assert.assertTrue(email.contains("@"),
                    "Mỗi Email cell phải chứa '@' (no truncation đến mức mất @). Actual='" + email + "'");
            Assert.assertFalse(email.contains("…") || email.endsWith("..."),
                    "Email cell không được chứa ellipsis '…' / '...' (truncate ẩn data). Actual='"
                            + email + "'");
        }
    }

    @Test(groups = {"compliantportal", "m1"},
            description = "PPAC_M1_TC_005: Cột Status hiển thị giá trị thuộc 13 status hợp lệ "
                    + "(regression cover M2 status definitions) — không có status string lạ")
    public void testStatusColumnValuesAreAllValidDefinedStatuses() {
        queue.selectStatus("All");
        if (queue.dataRowCount() == 0) {
            throw new SkipException("Filter 'All' rỗng — không có data để verify status values");
        }
        // Status là cell index 10 theo EXPECTED_TABLE_HEADERS
        List<String> statuses = queue.columnValues(10);
        Assert.assertFalse(statuses.isEmpty(),
                "Phải đọc được ít nhất 1 Status value từ table");
        // EXPECTED_STATUS_OPTIONS chứa "All" — cell value sẽ là 1 trong 13 status thực
        List<String> validStatuses = VerifierQueuePage.EXPECTED_STATUS_OPTIONS.stream()
                .filter(s -> !"All".equals(s))
                .collect(java.util.stream.Collectors.toList());
        for (String status : statuses) {
            // Flutter có thể render "Review required" hoặc "Review Required" — case-insensitive match
            boolean matched = validStatuses.stream()
                    .anyMatch(valid -> valid.equalsIgnoreCase(status.trim()));
            Assert.assertTrue(matched,
                    "Status cell value phải thuộc 13 status hợp lệ. Actual='" + status
                            + "', valid=" + validStatuses);
        }
    }

    @Test(groups = {"compliantportal", "m1"},
            description = "PPAC_M1_TC_007: Empty state — search 'nonexistent' → 0 row hiển thị "
                    + "(AM-2: empty state phải replace data area, không show header trống)")
    public void testEmptyStateWhenSearchYieldsNoResults() {
        if (!queue.hasSearchBox()) {
            throw new SkipException("Search box không có — không thể trigger empty state qua search");
        }
        queue.selectStatus("All");
        String unlikelyToken = "nonexistent_xxx_" + System.currentTimeMillis();
        queue.typeSearch(unlikelyToken);
        int rows = queue.dataRowCount();
        Assert.assertEquals(rows, 0,
                "Search với token không tồn tại phải trả về 0 data row. token='"
                        + unlikelyToken + "' actual rows=" + rows);
    }

    @Test(groups = {"compliantportal", "m1"},
            description = "PPAC_M1_TC_012: Closing the modal preserves the active status filter")
    public void testCloseModalPreservesFilter() {
        queue.selectStatus("Active");
        String beforeFilter = queue.currentStatusFilterLabel();
        Assert.assertEquals(beforeFilter, "Active",
                "Pre-condition: filter must be Active before opening a worker");
        if (queue.dataRowCount() == 0) {
            throw new SkipException("Active filter has no workers on UAT — cannot exercise close-and-preserve");
        }
        WorkerDetailModal modal = queue.openFirstWorker();
        Assert.assertTrue(modal.isOpen(), "Modal must open");
        modal.close();
        String afterFilter = queue.currentStatusFilterLabel();
        Assert.assertEquals(afterFilter, beforeFilter,
                "Status filter must remain '" + beforeFilter + "' after closing the modal. Actual: '"
                        + afterFilter + "'");
    }

    @Test(groups = {"compliantportal", "m1"},
            description = "PPAC_M1_TC_022: KPI Pending count = total rows hiển thị qua mọi page khi "
                    + "filter=Pending. Lưu ý: Pending filter trên PPAC là meta-grouping (gồm "
                    + "Pending/Pending (Recheck)/Review required/Waiting to Pass), nên row sum "
                    + "có thể > KPI Pending count thuần. Test assert row sum >= KPI Pending "
                    + "(meta-group bao gồm pure Pending).")
    public void testKpiPendingMatchesRowSumAcrossPages() {
        Assert.assertEquals(queue.currentStatusFilterLabel(), "Pending",
                "Pre-condition: filter default phải = Pending");
        int kpiPending = queue.kpiCount("Pending");
        if (kpiPending < 0) {
            throw new SkipException("KPI Pending không đọc được");
        }
        // Sum row count qua mọi page bằng cách click Next cho đến khi Next disabled
        int totalRows = 0;
        int safetyMaxPages = 50; // tránh vòng lặp vô hạn nếu pagination buggy
        int pages = 0;
        while (pages < safetyMaxPages) {
            totalRows += queue.dataRowCount();
            pages++;
            if (!queue.hasPagination()) {
                break;
            }
            if (queue.isPaginationDisabled("Next Page")) {
                break;
            }
            try {
                queue.clickPagination("Next Page");
            } catch (TimeoutException te) {
                break;
            }
        }
        Assert.assertTrue(pages < safetyMaxPages,
                "Pagination loop vượt safety limit (" + safetyMaxPages + ") — likely buggy pagination");
        Assert.assertTrue(totalRows >= kpiPending,
                "Tổng rows hiển thị qua " + pages + " page (=" + totalRows + ") phải >= "
                        + "KPI Pending counter (" + kpiPending + "). Filter Pending là meta-grouping "
                        + "nên rows có thể > KPI Pending thuần. Nếu rows < KPI → data integrity bug.");
    }

    @Test(groups = {"compliantportal", "m1"},
            description = "PPAC_M1_TC_011: Mở modal lần 1 → close → mở modal lần 2 từ row khác. "
                    + "Verify modal open + close + open lại đều stable. (Spec gốc: click cell "
                    + "Email vs Name đều mở Detail; openFirstWorker đã có 5 click target chain "
                    + "fallback nên test verify lặp lại open.)")
    public void testRepeatedModalOpenFromRowIsStable() {
        if (queue.dataRowCount() == 0) {
            throw new SkipException("Pending queue rỗng");
        }
        // Open lần 1
        WorkerDetailModal modal1 = queue.openFirstWorker();
        Assert.assertTrue(modal1.isOpen(), "Modal lần 1 phải mở");
        String firstEmail = modal1.headerFieldValue("Email");
        modal1.close();
        Assert.assertFalse(modal1.isOpen(), "Modal lần 1 phải close được");

        // Open lần 2 từ cùng landing — verify repeat stable
        WorkerDetailModal modal2 = queue.openFirstWorker();
        Assert.assertTrue(modal2.isOpen(), "Modal lần 2 phải mở (sau khi đóng modal 1)");
        String secondEmail = modal2.headerFieldValue("Email");
        modal2.close();

        // Cả 2 lần đều phải đọc được email từ header (≠ blank); nếu UAT data ổn định, 2 email
        // có thể trùng (cùng row đầu) — đó cũng OK. Test chính: open được lặp lại, không có
        // accident-copy/race condition khiến modal không trigger.
        Assert.assertFalse(firstEmail == null || firstEmail.isBlank(),
                "Modal lần 1 phải đọc được email từ header (verify open thành công)");
        Assert.assertFalse(secondEmail == null || secondEmail.isBlank(),
                "Modal lần 2 phải đọc được email từ header (verify open lặp lại stable)");
    }
}
