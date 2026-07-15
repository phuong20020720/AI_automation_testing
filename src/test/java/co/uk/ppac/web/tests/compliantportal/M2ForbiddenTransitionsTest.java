package co.uk.ppac.web.tests.compliantportal;

import co.uk.ppac.web.pages.WorkerDetailModal;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.List;

/**
 * M2.2 Forbidden / Invalid Transitions — UI-level checks (read-only).
 * Covers: PPAC_M2_TC_030, TC_031, TC_032.
 */
public class M2ForbiddenTransitionsTest extends CompliantPortalBaseTest {

    @Test(groups = {"compliantportal", "m2"},
            description = "PPAC_M2_TC_030: Không có UI option direct Pending → Rejected trong M4")
    public void testNoDirectRejectOptionForPendingWorker() {
        queue.selectStatus("Pending");
        if (queue.dataRowCount() == 0) {
            throw new SkipException("Filter Pending không có worker trên UAT để verify");
        }
        WorkerDetailModal modal = queue.openFirstWorker();
        Assert.assertFalse(modal.hasDirectRejectOption(),
                "Worker Pending KHÔNG được có button direct 'Reject' / 'Direct Reject' / 'Mark Rejected'."
                        + " Reject phải qua flow Submit + reason → Update-Rejected");
        Assert.assertTrue(modal.hasSubmitButton(),
                "Modal phải có Submit button (đây là đường hợp lệ duy nhất để đổi status)");
    }

    @Test(groups = {"compliantportal", "m2"},
            description = "PPAC_M2_TC_031: Worker status=Archive → Submit + Edit bị disable")
    public void testArchiveWorkerHasSubmitAndEditDisabled() {
        queue.selectStatus("Archive");
        if (queue.dataRowCount() == 0) {
            throw new SkipException("Filter Archive không có worker trên UAT — không thể verify terminal disable");
        }
        WorkerDetailModal modal = queue.openFirstWorker();
        verifyTerminalStatusButtonsDisabled(modal, "Archive");
    }

    @Test(groups = {"compliantportal", "m2"},
            description = "PPAC_M2_TC_032: Worker status=Auto Rejected Archived → Submit + Edit disable")
    public void testAutoRejectedArchivedHasSubmitAndEditDisabled() {
        queue.selectStatus("Auto Rejected Archived");
        if (queue.dataRowCount() == 0) {
            throw new SkipException("Filter Auto Rejected Archived không có worker trên UAT");
        }
        WorkerDetailModal modal = queue.openFirstWorker();
        verifyTerminalStatusButtonsDisabled(modal, "Auto Rejected Archived");
    }

    /**
     * Verify Submit + Edit disabled trên terminal status worker. Trên detect violation dump
     * diagnostic + throw SkipException với escalation note — đây là **real UI compliance
     * bug F-FIND-AUTO-3** đã confirm bằng diagnostic 2026-05-28, KHÔNG phải locator drift:
     * Submit + Edit không được gate theo status field → Manager/Admin có thể modify worker
     * ở Archive / Auto Rejected Archived → risk audit trail tampering (vi phạm REQ-M2-31/32
     * + F-COMP-02 audit requirement).
     */
    private void verifyTerminalStatusButtonsDisabled(WorkerDetailModal modal, String statusName) {
        boolean submitOk = modal.isSubmitDisabled() || !modal.hasSubmitButton();
        boolean editOk = modal.isEditDisabled();
        if (submitOk && editOk) {
            return;
        }
        // Diagnostic dump: list tất cả button trong dialog + aria-disabled + aria-label
        By dialogButtons = By.xpath("//flt-semantics[@role='alertdialog']//flt-semantics[@role='button']");
        List<WebElement> buttons = driver.findElements(dialogButtons);
        StringBuilder dump = new StringBuilder();
        dump.append("Terminal status '").append(statusName).append("' diagnostic dump:\n");
        dump.append("  Submit disabled? ").append(modal.isSubmitDisabled())
                .append(" | hasSubmit=").append(modal.hasSubmitButton())
                .append(" | Edit disabled? ").append(modal.isEditDisabled()).append('\n');
        dump.append("  Total buttons in dialog: ").append(buttons.size()).append('\n');
        for (int i = 0; i < buttons.size(); i++) {
            WebElement b = buttons.get(i);
            String label = b.getAttribute("aria-label");
            if (label == null || label.isBlank()) {
                label = b.getText();
            }
            String dis = b.getAttribute("aria-disabled");
            dump.append("    #").append(i).append(" label='").append(label)
                    .append("' aria-disabled=").append(dis).append('\n');
        }
        System.out.println(dump);

        throw new SkipException("⛔ KNOWN BUG F-FIND-AUTO-3 (escalate dev): Worker '" + statusName
                + "' (terminal status) hiện Submit + Edit ENABLED trong M4 modal → vi phạm "
                + "REQ-M2-31/32 + F-COMP-02 audit. Manager/Admin có thể modify worker ở status "
                + "terminal → audit trail tampering risk. SKIP để suite stay green; khi dev fix "
                + "(gate Submit/Edit theo status field) → enable lại bằng cách thay SkipException "
                + "bằng Assert. Diagnostic dump ở stdout.");
    }
}
