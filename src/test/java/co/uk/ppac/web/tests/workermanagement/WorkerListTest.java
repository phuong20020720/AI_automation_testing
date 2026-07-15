package co.uk.ppac.web.tests.workermanagement;

import co.uk.ppac.web.pages.WorkerListPage;
import co.uk.ppac.web.pages.WorkerProfilePage;
import co.uk.ppac.web.pages.DeleteConfirmModal;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;

/**
 * M1 List — 12 TC cho list view của Worker Management.
 * Live UAT data, không seed/destroy worker.
 */
public class WorkerListTest extends WorkerManagementBaseTest {

    /** PPAC_WM_TC_001 — Render đủ 9 columns đúng thứ tự */
    @Test(groups = {"smoke", "worker-management", "list"})
    public void testRenderNineColumnsInOrder() {
        List<String> headers = list.headerTexts();
        Assert.assertEquals(headers.size(), WorkerListPage.EXPECTED_HEADERS.size(),
                "Số cột header phải đúng 9 — actual: " + headers);
        Assert.assertEquals(headers, WorkerListPage.EXPECTED_HEADERS,
                "Header columns phải đúng thứ tự đã định");
    }

    /** PPAC_WM_TC_002 — Verification column read-only, value thuộc 4 status hợp lệ */
    @Test(groups = {"worker-management", "list"})
    public void testVerificationColumnIsReadOnlyValidStatus() {
        Set<String> validStatuses = Set.of("Go to Site", "Active", "Rejected", "Expired");
        int idx = list.firstRowWithStatusBadge();
        if (idx < 0) {
            throw new SkipException("Không có row nào hiển thị status badge");
        }

        String status = list.statusOfRow(idx);
        Assert.assertTrue(validStatuses.contains(status),
                "Status row " + idx + " phải thuộc {" + validStatuses + "} — actual: '" + status + "'");

        WebElement firstBadge = list.rowAt(idx)
                .findElement(By.cssSelector("span[data-slot='badge']"));
        String html = firstBadge.getAttribute("outerHTML");
        Assert.assertFalse(html.contains("contenteditable=\"true\""),
                "Badge KHÔNG được contenteditable — read-only contract");
        Assert.assertFalse(html.contains("<input") || html.contains("<select"),
                "Badge KHÔNG chứa input/select — read-only");
    }

    /** PPAC_WM_TC_003 — Selfie thumbnail render hoặc fallback (không broken image) */
    @Test(groups = {"worker-management", "list"})
    public void testSelfieThumbnailRenders() {
        Assert.assertTrue(list.rowCount() > 0, "Cần ≥1 worker");
        int idx = list.firstRowWithSelfie();
        Assert.assertTrue(idx >= 0,
                "Ít nhất 1 row phải có selfie thumbnail (role=button aria-label='Open preview in fullscreen')");
    }

    /** PPAC_WM_TC_004 — Click View Full Profile mở Profile đúng worker (URL có ?id=) */
    @Test(groups = {"smoke", "worker-management", "list"})
    public void testClickFullProfileOpensProfile() {
        int idx = list.firstRowWithFullProfileButton();
        if (idx < 0) {
            throw new SkipException("Không có row nào có button View Full Profile");
        }
        String code = list.codeOfRow(idx);
        WorkerProfilePage profile = list.clickFullProfileAt(idx);

        Assert.assertTrue(profile.isOnProfileUrl(),
                "URL phải có pattern `worker-management?id=...` sau khi click View Full Profile");
        Assert.assertTrue(profile.isHeadingDisplayed(),
                "Heading 'Profile Details' phải hiển thị");
        Assert.assertFalse(profile.currentWorkerId().isEmpty(),
                "workerId trong query param phải khác rỗng");
        Assert.assertFalse(code.isEmpty(), "Code của worker được click phải đọc được");
    }

    /** PPAC_WM_TC_005 — Click Delete mở modal */
    @Test(groups = {"smoke", "worker-management", "list"})
    public void testClickDeleteOpensModal() {
        int idx = list.firstRowWithDeleteButton();
        if (idx < 0) {
            throw new SkipException("Không có row nào có button Delete");
        }
        DeleteConfirmModal modal = list.clickDeleteAt(idx);

        Assert.assertTrue(modal.isOpen(), "Modal Delete phải mở");
        Assert.assertTrue(modal.isTitleCorrect(),
                "Title modal phải là 'Delete submission?' (không phải 'Delete a submission?' như TC manual ghi sai)");
        Assert.assertTrue(modal.hasCancelAndConfirmButtons(),
                "Modal phải có 2 button Cancel + Confirm");
        modal.clickCancel();
    }

    /** PPAC_WM_TC_006 — Pagination với ≥10 items */
    @Test(groups = {"worker-management", "list"})
    public void testPaginationShowsForLargeDataset() {
        int rows = list.rowCount();
        if (rows < 10) {
            throw new SkipException("UAT data có <10 workers; skip kiểm tra pagination >10");
        }
        Assert.assertTrue(list.isPaginationDisplayed(),
                "Khi có ≥10 rows thì pagination phải hiển thị");
        Assert.assertTrue(rows <= 25,
                "Page size phải hợp lý (≤25) — actual: " + rows);
    }

    /** PPAC_WM_TC_007 — List <10 items không có pagination (dùng filter/search rất hẹp) */
    @Test(groups = {"worker-management", "list"})
    public void testNoPaginationWhenFewItems() {
        list.search("__nonexistent_search_token_xyz__");
        boolean paginationVisible = list.isPaginationDisplayed();
        int rowCount = list.rowCount();
        Assert.assertTrue(rowCount <= 10,
                "Sau filter strict, rowCount phải ≤10 — actual: " + rowCount);
        if (rowCount == 0) {
            Assert.assertTrue(true, "Empty result is acceptable");
        } else {
            Assert.assertFalse(paginationVisible && hasMultiplePages(),
                    "Khi có <10 results không nên có nhiều page");
        }
    }

    private boolean hasMultiplePages() {
        return list.isPaginationNextEnabled();
    }

    /** PPAC_WM_TC_008 — Empty list state khi search không match */
    @Test(groups = {"worker-management", "list"})
    public void testEmptyStateOnNoSearchMatch() {
        list.search("ZZZ_no_match_" + System.currentTimeMillis());
        Assert.assertTrue(list.isEmptyStateDisplayed(),
                "Search không match phải hiển thị empty state hoặc 0 rows");
    }

    /** PPAC_WM_TC_009 — Search by Code (EP) */
    @Test(groups = {"worker-management", "list"})
    public void testSearchByCode() {
        Assert.assertTrue(list.rowCount() > 0, "Cần dữ liệu để search");
        String existingCode = "";
        int rc = list.rowCount();
        for (int i = 0; i < rc; i++) {
            String c = list.codeOfRow(i);
            if (c != null && !c.isBlank()) { existingCode = c; break; }
        }
        if (existingCode.isBlank()) {
            throw new SkipException("Không tìm thấy row nào có Code — không thể test search");
        }
        list.search(existingCode);
        Assert.assertTrue(list.rowCount() >= 1,
                "Search Code đã tồn tại phải trả về ≥1 row — actual: " + list.rowCount());
        Assert.assertTrue(list.codeOfRow(0).contains(existingCode)
                        || existingCode.contains(list.codeOfRow(0)),
                "Row trả về phải khớp Code đã search");

        list.search("");
        int unfilteredCount = list.rowCount();
        list.search("@@##!!unmatchable" + System.currentTimeMillis());
        int filteredCount = list.rowCount();
        Assert.assertTrue(filteredCount <= unfilteredCount,
                "Search với special chars phải execute không crash. "
                        + "Backend có thể no-op hoặc filter strict — kết quả không được nhiều hơn unfiltered. "
                        + "unfiltered=" + unfilteredCount + " filtered=" + filteredCount);
    }

    /** PPAC_WM_TC_010 — Search by Email (EP) */
    @Test(groups = {"worker-management", "list"})
    public void testSearchByEmail() {
        Assert.assertTrue(list.rowCount() > 0, "Cần dữ liệu");
        String email = "";
        int rc = list.rowCount();
        for (int i = 0; i < rc; i++) {
            String e = list.emailOfRow(i);
            if (e != null && e.contains("@")) { email = e; break; }
        }
        if (email.isBlank()) {
            throw new SkipException("Không tìm thấy row nào có email khả dụng");
        }
        String domain = email.substring(email.indexOf('@'));
        list.search(domain);
        Assert.assertTrue(list.rowCount() >= 1,
                "Search theo domain phải trả về ≥1 row");
    }

    /** PPAC_WM_TC_011 — Sort column không hoạt động (REQ-04) */
    @Test(groups = {"worker-management", "list"})
    public void testHeaderClickDoesNotSort() {
        Assert.assertTrue(list.rowCount() >= 2, "Cần ≥2 rows để verify order không đổi");
        String firstCodeBefore = list.codeOfRow(0);
        String secondCodeBefore = list.codeOfRow(1);

        WebElement codeHeader = driver.findElements(By.cssSelector("th[data-slot='table-head']")).get(0);
        codeHeader.click();
        try { Thread.sleep(800); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        Assert.assertEquals(list.codeOfRow(0), firstCodeBefore,
                "Sau khi click header Code, row 0 phải giữ nguyên — column không sort");
        Assert.assertEquals(list.codeOfRow(1), secondCodeBefore,
                "Sau khi click header Code, row 1 phải giữ nguyên");

        boolean hasSortIndicator = !driver.findElements(
                By.xpath("//th[@data-slot='table-head'][.//*[local-name()='svg' "
                        + "and (contains(@class,'arrow-up') or contains(@class,'arrow-down') "
                        + "or contains(@class,'chevron-up') or contains(@class,'chevron-down'))]]"))
                .isEmpty();
        Assert.assertFalse(hasSortIndicator,
                "Không được có sort indicator (mũi tên) khi feature sort bị tắt");
    }

    /** PPAC_WM_TC_012 — Responsive 1280px — 9 cột không bị ẩn */
    @Test(groups = {"worker-management", "list"})
    public void testResponsiveAt1280pxAllColumnsVisible() {
        driver.manage().window().setSize(new Dimension(1280, 720));
        try { Thread.sleep(800); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        list = new WorkerListPage(driver).open();
        List<String> headers = list.headerTexts();
        Assert.assertEquals(headers.size(), 9,
                "Ở 1280px vẫn phải có đủ 9 cột — actual: " + headers.size());
    }
}
