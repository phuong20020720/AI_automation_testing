package co.uk.ppac.web.tests.compliantportal;

import co.uk.ppac.web.pages.WorkerDetailModal;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * M2 Verifier-driven transitions — write actions. Tests are gated by
 * {@code -Dcompliantportal.write.enabled=true} because Submit modifies shared UAT data.
 * Covers: PPAC_M2_TC_010, PPAC_M2_TC_011, PPAC_M2_TC_012 AND equivalently
 * PPAC_M2_TC_050 (DT-1), PPAC_M2_TC_051 (DT-2), PPAC_M2_TC_052 (DT-3) — Decision Table
 * cases có cùng logic state-transition (Pending + reason variants + Regula passed pre-condition).
 *
 * <p>Strategy (pre-seed via UI): pick the first Pending worker (Regula passed implicitly
 * vì worker đã ở status Pending), perform the Submit action, then assert resulting status.
 * Tests are independent — each opens a fresh Pending worker.
 */
public class M2TransitionsTest extends CompliantPortalBaseTest {

    private static final String WRITE_FLAG = "compliantportal.write.enabled";

    @BeforeMethod(alwaysRun = true)
    public void skipWhenWriteDisabled() {
        if (!"true".equalsIgnoreCase(System.getProperty(WRITE_FLAG, "false"))) {
            throw new SkipException("M2 write-action tests skipped by default on shared UAT. "
                    + "Enable with -D" + WRITE_FLAG + "=true.");
        }
        queue.selectStatus("Pending");
        if (queue.dataRowCount() == 0) {
            throw new SkipException("Pending queue is empty — no seed worker to transition");
        }
    }

    @Test(groups = {"compliantportal", "m2", "compliantportal-write"},
            description = "PPAC_M2_TC_010 / PPAC_M2_TC_050 (DT-1): Pending + empty reason + Regula passed → Active")
    public void testPendingWithEmptyReasonBecomesActive() {
        WorkerDetailModal modal = queue.openFirstWorker();
        Assert.assertTrue(modal.isOpen(), "Worker Detail Modal must open");
        modal.submitWithReason("");
        String headerStatus = modal.headerStatusText();
        Assert.assertEquals(headerStatus, "Active",
                "Submitting Pending worker with empty reason must transition status to Active. Actual: '"
                        + headerStatus + "'");
    }

    @Test(groups = {"compliantportal", "m2", "compliantportal-write"},
            description = "PPAC_M2_TC_011 / PPAC_M2_TC_052 (DT-3): Pending + reject reason + Regula passed → Update-Rejected")
    public void testPendingWithRejectReasonBecomesUpdateRejected() {
        WorkerDetailModal modal = queue.openFirstWorker();
        Assert.assertTrue(modal.isOpen(), "Worker Detail Modal must open");
        modal.submitWithReason("auto-reject: document blurry, please re-upload CSCS card");
        String headerStatus = modal.headerStatusText();
        Assert.assertEquals(headerStatus, "Update-Rejected",
                "Submitting Pending worker with rejection reason must transition to Update-Rejected. Actual: '"
                        + headerStatus + "'");
    }

    @Test(groups = {"compliantportal", "m2", "compliantportal-write"},
            description = "PPAC_M2_TC_012 / PPAC_M2_TC_051 (DT-2): Pending + need-info reason + Regula passed → Pending Info")
    public void testPendingWithNeedInfoReasonBecomesPendingInfo() {
        WorkerDetailModal modal = queue.openFirstWorker();
        Assert.assertTrue(modal.isOpen(), "Worker Detail Modal must open");
        modal.submitWithReason("auto-info: need NI Number proof, please upload supporting document");
        String headerStatus = modal.headerStatusText();
        Assert.assertEquals(headerStatus, "Pending Info",
                "Submitting Pending worker with need-info reason must transition to Pending Info. Actual: '"
                        + headerStatus + "'");
    }
}
