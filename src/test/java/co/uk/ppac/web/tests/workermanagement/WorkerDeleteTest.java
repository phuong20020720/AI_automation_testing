package co.uk.ppac.web.tests.workermanagement;

import co.uk.ppac.web.pages.DeleteConfirmModal;
import co.uk.ppac.web.pages.WorkerListPage;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

/**
 * M7 Delete — 7 TC. UAT là môi trường chia sẻ; KHÔNG thực sự confirm delete
 * worker production. TC_062, TC_064, TC_065, TC_066 SKIP — cần dedicated
 * test data seeding (qua API/fixture) trước khi enable.
 */
public class WorkerDeleteTest extends WorkerManagementBaseTest {

    /** PPAC_WM_TC_061 — Click Delete mở confirmation modal */
    @Test(groups = {"smoke", "worker-management", "delete"})
    public void testDeleteOpensConfirmationModal() {
        int idx = list.firstRowWithDeleteButton();
        if (idx < 0) {
            throw new SkipException("Không có row có button Delete");
        }
        DeleteConfirmModal modal = list.clickDeleteAt(idx);
        Assert.assertTrue(modal.isOpen(), "Modal Delete phải mở");
        Assert.assertTrue(modal.isTitleCorrect(),
                "Title phải là 'Delete submission?' (UI thật, khác manual TC ghi 'Delete a submission?')");
        Assert.assertTrue(modal.hasCancelAndConfirmButtons(),
                "Phải có 2 button Cancel + Confirm");
        modal.clickCancel();
    }

    /** PPAC_WM_TC_062 — Confirm delete (DESTRUCTIVE — skip trên UAT shared data) */
    @Test(groups = {"worker-management", "delete", "destructive"}, enabled = false,
          description = "Cần seed worker mới qua API trước khi enable — không destroy production data")
    public void testConfirmDeleteRemovesWorker() {
        throw new SkipException("DESTRUCTIVE — cần fixture seed worker. Sẽ enable khi có API tạo worker.");
    }

    /** PPAC_WM_TC_063 — Cancel delete → modal đóng, list không thay đổi */
    @Test(groups = {"worker-management", "delete"})
    public void testCancelDeleteKeepsList() {
        int idx = list.firstRowWithDeleteButton();
        if (idx < 0) {
            throw new SkipException("Không có row có button Delete");
        }
        int rowsBefore = list.rowCount();
        String codeBefore = list.codeOfRow(idx);

        DeleteConfirmModal modal = list.clickDeleteAt(idx);
        Assert.assertTrue(modal.isOpen(), "Modal mở");
        WorkerListPage afterCancel = modal.clickCancel();

        Assert.assertEquals(afterCancel.rowCount(), rowsBefore,
                "Cancel KHÔNG được thay đổi rowCount — before=" + rowsBefore);
        Assert.assertEquals(afterCancel.codeOfRow(idx), codeBefore,
                "Row tại idx=" + idx + " vẫn là worker cũ sau Cancel");
    }

    /** PPAC_WM_TC_064 — Hard delete verification (DESTRUCTIVE) */
    @Test(groups = {"worker-management", "delete", "destructive"}, enabled = false,
          description = "Verify hard delete — phụ thuộc TC_062. Cần seed + DB/API check.")
    public void testHardDeleteCannotRecover() {
        throw new SkipException("DESTRUCTIVE + cần API GET /workers/{id} verify 404");
    }

    /** PPAC_WM_TC_065 — Delete worker status Active (DESTRUCTIVE) */
    @Test(groups = {"worker-management", "delete", "destructive"}, enabled = false,
          description = "Cần seed worker Active dedicated trước khi enable")
    public void testDeleteActiveWorkerBehavior() {
        throw new SkipException("Cần seed Active worker. Behavior thực tế chưa confirmed.");
    }

    /** PPAC_WM_TC_066 — Concurrent delete 2 tabs (DESTRUCTIVE + cần 2 driver) */
    @Test(groups = {"worker-management", "delete", "destructive"}, enabled = false,
          description = "Cần 2 driver instance + seed worker; race condition test")
    public void testConcurrentDeleteRaceCondition() {
        throw new SkipException("Cần 2 driver + fixture; không thể chạy với single-driver pattern hiện tại");
    }

    /** PPAC_WM_TC_067 — Browser back sau delete: do TC_062 destructive nên skip */
    @Test(groups = {"worker-management", "delete", "destructive"}, enabled = false,
          description = "Phụ thuộc TC_062 — chưa enable")
    public void testBrowserBackAfterDelete() {
        throw new SkipException("Phụ thuộc TC_062 destructive");
    }
}
