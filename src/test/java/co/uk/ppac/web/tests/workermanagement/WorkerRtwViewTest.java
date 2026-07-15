package co.uk.ppac.web.tests.workermanagement;

import co.uk.ppac.web.pages.WorkerProfilePage;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.Set;

/**
 * M4 Right to Work — view-only contract (4 TC).
 *
 * Lưu ý sai khác so với TC manual:
 * - UI thật chỉ hiển thị 1 RtW type per worker (dạng "Right To Work - ShareCode"),
 *   KHÔNG phải 5 slot riêng cho Passport / ID Card / Birth Certificate / Share code / NI document
 * - Document hiển thị qua iframe PDF với signed S3 URL
 */
public class WorkerRtwViewTest extends WorkerManagementBaseTest {

    private static final Set<String> KNOWN_RTW_TYPES = Set.of(
            "ShareCode", "Share Code", "Passport", "ID Card", "Birth Certificate",
            "NI document", "NI Document");

    /** PPAC_WM_TC_029 — Section Right To Work render với label + type */
    @Test(groups = {"worker-management", "rtw"})
    public void testRtwSectionDisplaysWithType() {
        int idx = list.firstRowWithFullProfileButton();
        if (idx < 0) {
            throw new SkipException("Không có worker để mở profile");
        }
        WorkerProfilePage profile = list.clickFullProfileAt(idx);
        if (!profile.isRtwSectionPresent()) {
            throw new SkipException("Worker này không có RtW section (data-dependent)");
        }
        String label = profile.rtwLabelText();
        Assert.assertTrue(label.startsWith("Right To Work"),
                "Label phải bắt đầu với 'Right To Work' — actual: " + label);
        Assert.assertTrue(label.contains(" - "),
                "Label phải có format 'Right To Work - <Type>' — actual: " + label);
        String type = label.substring(label.indexOf(" - ") + 3).trim();
        Assert.assertTrue(KNOWN_RTW_TYPES.stream().anyMatch(t -> t.equalsIgnoreCase(type)),
                "Type phải thuộc danh sách RtW types đã biết — actual: '" + type + "'");
    }

    /** PPAC_WM_TC_034 — Section RtW KHÔNG có Upload/Replace/Delete affordance */
    @Test(groups = {"worker-management", "rtw", "smoke"})
    public void testRtwSectionIsReadOnlyNoUploadAffordance() {
        int idx = list.firstRowWithFullProfileButton();
        if (idx < 0) {
            throw new SkipException("Không có worker để test");
        }
        WorkerProfilePage profile = list.clickFullProfileAt(idx);
        Assert.assertFalse(profile.hasUploadOrReplaceAffordance(),
                "Profile KHÔNG được có button Upload/Replace/Remove document hoặc input[type=file] "
                        + "— vi phạm read-only contract M4 (REQ-11)");
    }

    /** PPAC_WM_TC_036 — Empty state khi worker không có RtW data */
    @Test(groups = {"worker-management", "rtw"})
    public void testRtwEmptyStateWhenNoData() {
        int rc = list.rowCount();
        for (int i = 0; i < Math.min(rc, 5); i++) {
            if (!list.rowHasFullProfileButton(i)) {
                continue;
            }
            String code = list.codeOfRow(i);
            WorkerProfilePage profile = list.clickFullProfileAt(i);
            boolean hasRtw = profile.isRtwSectionPresent();
            boolean hasUploadAffordance = profile.hasUploadOrReplaceAffordance();
            profile.clickBack();
            if (!hasRtw) {
                Assert.assertFalse(hasUploadAffordance,
                        "Worker không có RtW (code=" + code + ") cũng KHÔNG được có upload affordance "
                                + "— vẫn read-only");
                return;
            }
        }
        throw new SkipException("5 worker đầu đều có RtW data — không test được empty state RtW");
    }

    /** PPAC_WM_TC_041 — F5 refresh Profile giữ RtW document state */
    @Test(groups = {"worker-management", "rtw"})
    public void testRtwDocumentPersistsAfterRefresh() {
        int idx = list.firstRowWithFullProfileButton();
        if (idx < 0) {
            throw new SkipException("Không có worker");
        }
        WorkerProfilePage profile = list.clickFullProfileAt(idx);
        if (!profile.isRtwSectionPresent()) {
            throw new SkipException("Worker không có RtW section");
        }
        String labelBefore = profile.rtwLabelText();
        String urlBefore = profile.rtwDocumentUrl();

        profile.refresh();
        Assert.assertEquals(profile.rtwLabelText(), labelBefore,
                "Sau refresh, label RtW phải giữ nguyên");
        if (!urlBefore.isBlank()) {
            String urlAfter = profile.rtwDocumentUrl();
            Assert.assertTrue(urlAfter.contains("documents-storage"),
                    "Document URL sau refresh vẫn phải là signed S3 URL — actual: " + urlAfter);
        }
    }
}
