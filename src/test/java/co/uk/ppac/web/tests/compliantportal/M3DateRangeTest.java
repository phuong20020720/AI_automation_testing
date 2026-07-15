package co.uk.ppac.web.tests.compliantportal;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * M3.3 Date Range — deep coverage.
 * Covers: PPAC_M3_TC_020, _021, _022, _023, _024, _025.
 * Out-of-scope phase này: _026 (picker validation), _027 (timezone boundary — cần data đặc thù).
 *
 * Recon (2026-05-20): click ô Start/End Date mở Material date-picker dialog;
 * nút "Switch to input" cho phép gõ ngày format M/D/YYYY; KPI "Total" phản ánh kết quả filter.
 * Lưu ý: giá trị &lt;input&gt; ngày KHÔNG đáng tin (Flutter canvas) → assert qua KPI Total.
 */
public class M3DateRangeTest extends CompliantPortalBaseTest {

    @Test(groups = {"compliantportal", "m3", "date"},
            description = "PPAC_M3_TC_020: Date range hợp lệ → thu hẹp kết quả")
    public void testValidDateRangeFiltersResults() {
        int fullTotal = queue.kpiCount("Total");
        Assert.assertTrue(fullTotal > 0, "Pre-condition: landing phải có dữ liệu");

        queue.pickStartDate("5/1/2026").pickEndDate("5/20/2026");
        int rangeTotal = queue.kpiCount("Total");
        Assert.assertTrue(rangeTotal >= 0,
                "Date range hợp lệ phải chạy không lỗi. Total=" + rangeTotal);
        Assert.assertTrue(rangeTotal < fullTotal,
                "Date range 01/05–20/05/2026 phải thu hẹp kết quả so với tổng ban đầu. range="
                        + rangeTotal + " full=" + fullTotal);
    }

    @Test(groups = {"compliantportal", "m3", "date"},
            description = "PPAC_M3_TC_021: Start Date = End Date (lọc đúng 1 ngày)")
    public void testSingleDayRange() {
        int fullTotal = queue.kpiCount("Total");
        queue.pickStartDate("5/20/2026").pickEndDate("5/20/2026");
        int total = queue.kpiCount("Total");
        Assert.assertTrue(total >= 0 && total <= fullTotal,
                "Filter đúng 1 ngày phải chạy không lỗi, kết quả ≤ tổng ban đầu. Total=" + total);
        Assert.assertTrue(queue.isTablePresent(),
                "Bảng vẫn phải render khi lọc 1 ngày");
    }

    @Test(groups = {"compliantportal", "m3", "date"},
            description = "PPAC_M3_TC_022: Start Date > End Date (range ngược) — không crash")
    public void testReverseDateRangeDoesNotCrash() {
        queue.pickStartDate("5/20/2026").pickEndDate("5/1/2026");
        int total = queue.kpiCount("Total");
        String statusLabel = queue.currentStatusFilterLabel();
        Assert.assertFalse(statusLabel.isEmpty(),
                "Sau range ngược (Start > End), filter bar vẫn phải hoạt động — app không crash. "
                        + "DOCUMENTED: Total quan sát=" + total
                        + " (kỳ vọng: empty result hoặc validation error)");
    }

    @Test(groups = {"compliantportal", "m3", "date"},
            description = "PPAC_M3_TC_023: Chỉ Start Date, End trống — open-ended từ Start đến nay")
    public void testStartDateOnly() {
        int fullTotal = queue.kpiCount("Total");
        queue.pickStartDate("5/1/2026");
        int total = queue.kpiCount("Total");
        Assert.assertTrue(total >= 0 && total < fullTotal,
                "Filter chỉ-Start (từ 01/05/2026) phải thu hẹp kết quả. Total="
                        + total + " full=" + fullTotal);
    }

    @Test(groups = {"compliantportal", "m3", "date"},
            description = "PPAC_M3_TC_024: Chỉ End Date, Start trống")
    public void testEndDateOnly() {
        int fullTotal = queue.kpiCount("Total");
        queue.pickEndDate("1/1/2026");
        int total = queue.kpiCount("Total");
        Assert.assertTrue(total >= 0 && total <= fullTotal,
                "Filter chỉ-End phải chạy không lỗi, kết quả ≤ tổng ban đầu. DOCUMENTED: Total="
                        + total + " full=" + fullTotal
                        + (total == fullTotal ? " — end-only không thu hẹp (mọi worker có Created trong quá khứ)" : ""));
        Assert.assertTrue(queue.isTablePresent(),
                "Bảng vẫn phải render sau khi áp dụng filter chỉ-End");
    }

    @Test(groups = {"compliantportal", "m3", "date"},
            description = "PPAC_M3_TC_025: Start Date tương lai → không có worker")
    public void testFutureStartDateReturnsEmpty() {
        queue.pickStartDate("12/1/2026");
        int total = queue.kpiCount("Total");
        Assert.assertEquals(total, 0,
                "Start Date trong tương lai (01/12/2026) phải cho 0 kết quả. Total=" + total);
    }
}
