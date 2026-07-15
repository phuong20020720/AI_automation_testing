package co.uk.ppac.web.tests.compliantportal;

import co.uk.ppac.web.pages.WorkerDetailModal;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

/**
 * M4.1 / M2.1 — Header Status đồng bộ với cột Status M1.
 * Covers: PPAC_M2_TC_005.
 */
public class M2HeaderSyncTest extends CompliantPortalBaseTest {

    @Test(groups = {"compliantportal", "m2"},
            description = "PPAC_M2_TC_005: Header Status M4 = cột Status M1 sau khi mở worker từ filter Active")
    public void testHeaderStatusMatchesListStatus() {
        queue.selectStatus("Active");
        if (queue.dataRowCount() == 0) {
            throw new SkipException("Filter Active không có worker trên UAT — không thể verify header sync");
        }
        String listStatus = queue.statusOfFirstRow();
        Assert.assertEquals(listStatus, "Active",
                "Sau khi filter Active, status cột M1 row đầu phải = 'Active'. Actual: '" + listStatus + "'");

        WorkerDetailModal modal = queue.openFirstWorker();
        Assert.assertTrue(modal.isOpen(), "Worker Detail Modal phải mở sau khi click row");

        String headerStatus = modal.headerStatusText();
        Assert.assertEquals(headerStatus, listStatus,
                "Header Status M4.1 phải khớp cột Status M1. List='" + listStatus
                        + "', Header='" + headerStatus + "'");
    }
}
