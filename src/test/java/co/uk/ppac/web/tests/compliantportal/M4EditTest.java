package co.uk.ppac.web.tests.compliantportal;

import co.uk.ppac.web.pages.WorkerDetailModal;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * M4 Worker Detail — Edit dialog write actions.
 * Covers: PPAC_M4_TC_050 (Edit NI Number valid UK format).
 *
 * <p>Tests gated bởi {@code -Dcompliantportal.write.enabled=true} vì modify shared UAT data.
 * Sau khi save, test best-effort restore original NI để giảm side-effect.
 */
public class M4EditTest extends CompliantPortalBaseTest {

    private static final String WRITE_FLAG = "compliantportal.write.enabled";
    private static final String VALID_UK_NI = "AB123456C";

    @BeforeMethod(alwaysRun = true)
    public void skipWhenWriteDisabled() {
        if (!"true".equalsIgnoreCase(System.getProperty(WRITE_FLAG, "false"))) {
            throw new SkipException("M4 Edit write-action test skipped by default on shared UAT. "
                    + "Enable with -D" + WRITE_FLAG + "=true.");
        }
        if (queue.dataRowCount() == 0) {
            throw new SkipException("Queue rỗng — không có worker để mở Edit");
        }
    }

    @Test(groups = {"compliantportal", "m4", "compliantportal-write"},
            description = "PPAC_M4_TC_050: Edit NI Number với valid UK format 'AB123456C' → save success")
    public void testEditValidUkNiNumber() {
        WorkerDetailModal modal = queue.openFirstWorker();
        Assert.assertTrue(modal.isOpen(), "Modal phải mở để bắt đầu Edit");
        if (modal.isEditDisabled()) {
            modal.close();
            throw new SkipException("Edit button disabled trên worker đầu — chọn worker khác hoặc skip");
        }

        modal.clickEdit();
        Assert.assertTrue(modal.isEditDialogOpen(),
                "Edit dialog phải mở sau khi click Edit (Save Changes button xuất hiện)");

        String originalNi = modal.readNiNumberInputValue();
        modal.fillNiNumber(VALID_UK_NI);
        // Bỏ assertion intermediate — Flutter text-editing-host sync async, value visible input
        // có thể chưa update tới khi Save Changes commit.

        modal.clickSaveChanges();
        Assert.assertFalse(modal.isEditDialogOpen(),
                "Edit dialog phải đóng sau Save Changes (success path = save thành công, không validation error)");

        // Best-effort cleanup: restore NI nếu original khác value test
        if (originalNi != null && !originalNi.equals(VALID_UK_NI)) {
            try {
                modal.clickEdit();
                modal.fillNiNumber(originalNi);
                modal.clickSaveChanges();
            } catch (Exception cleanupFailed) {
                System.err.println("[M4_TC_050 cleanup] Restore NI failed (best-effort): "
                        + cleanupFailed.getMessage());
            }
        }
        modal.close();
    }
}
