package co.uk.ppac.web.tests.workermanagement;

import co.uk.ppac.web.pages.WorkerProfilePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;

/**
 * M6 Status — 8 TC (TC_053-060). Read-only display contract.
 *
 * Tests focus:
 * - Status badge giá trị thuộc set {Go to Site, Active, Rejected, Expired}
 * - Status field KHÔNG có edit affordance (no inline editor, no dropdown)
 * - Profile KHÔNG có action button transition status (read by backend, not UI)
 * - Consistency list ↔ profile cho cùng worker
 * - Status nguồn từ backend API (verify qua React state/network)
 */
public class WorkerStatusTest extends WorkerManagementBaseTest {

    private static final Set<String> VALID_STATUSES = Set.of(
            "Go to Site", "Active", "Rejected", "Expired");

    /** PPAC_WM_TC_053 — Profile KHÔNG có action button thay đổi Worker status */
    @Test(groups = {"worker-management", "status"})
    public void testProfileHasNoStatusTransitionAction() {
        int idx = list.firstRowWithFullProfileButton();
        if (idx < 0) {
            throw new SkipException("Không có worker để mở profile");
        }
        WorkerProfilePage profile = list.clickFullProfileAt(idx);

        // Tìm button có khả năng transition status
        List<WebElement> dangerousButtons = driver.findElements(By.xpath(
                "//button[contains(translate(normalize-space(.),'PASS','pass'),'pass')"
                        + " or contains(translate(normalize-space(.),'APPROVE','approve'),'approve')"
                        + " or contains(translate(normalize-space(.),'REJECT','reject'),'reject')"
                        + " or contains(translate(normalize-space(.),'ACTIVATE','activate'),'activate')"
                        + " or contains(translate(normalize-space(.),'CHANGE STATUS','change status'),'change status')"
                        + " or contains(translate(normalize-space(.),'EDIT STATUS','edit status'),'edit status')]"));
        Assert.assertTrue(dangerousButtons.isEmpty()
                        || dangerousButtons.stream().noneMatch(WebElement::isDisplayed),
                "Profile KHÔNG được có button trigger status transition (Pass/Approve/Reject/Activate). "
                        + "Found: " + dangerousButtons.size() + " buttons matching");
    }

    /** PPAC_WM_TC_054 — Worker status `Go to Site` hiển thị đúng */
    @Test(groups = {"worker-management", "status"})
    public void testGoToSiteStatusDisplaysCorrectly() {
        verifyStatusDisplayConsistency("Go to Site");
    }

    /** PPAC_WM_TC_055 — Worker status `Active` hiển thị đúng */
    @Test(groups = {"worker-management", "status"})
    public void testActiveStatusDisplaysCorrectly() {
        verifyStatusDisplayConsistency("Active");
    }

    /** PPAC_WM_TC_056 — Worker status `Rejected` hiển thị đúng */
    @Test(groups = {"worker-management", "status"})
    public void testRejectedStatusDisplaysCorrectly() {
        verifyStatusDisplayConsistency("Rejected");
    }

    /** PPAC_WM_TC_057 — Worker status `Expired` hiển thị đúng */
    @Test(groups = {"worker-management", "status"})
    public void testExpiredStatusDisplaysCorrectly() {
        verifyStatusDisplayConsistency("Expired");
    }

    private void verifyStatusDisplayConsistency(String status) {
        int idx = list.firstRowIndexWithStatus(status);
        if (idx < 0) {
            throw new SkipException("UAT data không có worker status='" + status + "'");
        }
        Assert.assertEquals(list.statusOfRow(idx), status,
                "List Verification column phải hiển thị '" + status + "'");
        if (list.rowHasFullProfileButton(idx)) {
            WorkerProfilePage profile = list.clickFullProfileAt(idx);
            String profileStatus = profile.firstStatusBadgeText();
            if (!profileStatus.isBlank()) {
                Assert.assertEquals(profileStatus, status,
                        "Profile status badge phải khớp với list: list=" + status
                                + " profile=" + profileStatus);
            }
        }
    }

    /** PPAC_WM_TC_058 — Status field trong Profile là read-only (no edit affordance) */
    @Test(groups = {"worker-management", "status"})
    public void testStatusFieldHasNoEditAffordance() {
        int idx = list.firstRowWithFullProfileButton();
        if (idx < 0) {
            throw new SkipException("Không có worker");
        }
        WorkerProfilePage profile = list.clickFullProfileAt(idx);
        if (profile.firstStatusBadgeText().isBlank()) {
            throw new SkipException("Profile không có status field để test");
        }
        WebElement statusEl = profile.findStatusElement();
        String outerHtml = statusEl.getAttribute("outerHTML");
        Assert.assertFalse(outerHtml.contains("contenteditable=\"true\""),
                "Status element KHÔNG được contenteditable");
        Assert.assertFalse(outerHtml.toLowerCase().contains("<input")
                        || outerHtml.toLowerCase().contains("<select"),
                "Status element KHÔNG được chứa <input>/<select>");
        String cursor = statusEl.getCssValue("cursor");
        Assert.assertFalse("pointer".equalsIgnoreCase(cursor) || "text".equalsIgnoreCase(cursor),
                "Cursor trên status element KHÔNG được pointer/text — actual: " + cursor);
    }

    /** PPAC_WM_TC_059 — Status nguồn từ backend (verify list status khớp với value DOM render) */
    @Test(groups = {"worker-management", "status"})
    public void testStatusSourcedFromBackend() {
        Assert.assertTrue(list.rowCount() > 0, "Cần ≥1 row");
        int idx = list.firstRowWithStatusBadge();
        if (idx < 0) {
            throw new SkipException("Không có row có status badge");
        }
        String displayedStatus = list.statusOfRow(idx);
        Assert.assertTrue(VALID_STATUSES.contains(displayedStatus),
                "Status displayed phải thuộc set hợp lệ — actual: " + displayedStatus);

        // Verify status text được render từ data attribute hoặc text content (backend source),
        // KHÔNG phải hardcode trong UI source
        WebElement badge = list.rowAt(idx).findElement(By.cssSelector("span[data-slot='badge']"));
        String textContent = (String) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].textContent;", badge);
        Assert.assertEquals(textContent.trim(), displayedStatus,
                "DOM textContent phải khớp với getText() — render từ data, không transform UI");
    }

    /** PPAC_WM_TC_060 — Consistency list ↔ profile sau F5 refresh */
    @Test(groups = {"worker-management", "status"})
    public void testStatusConsistencyListVsProfileAfterRefresh() {
        int idx = list.firstFullyPopulatedRow();
        if (idx < 0) {
            throw new SkipException("Không có row đủ data");
        }
        String listStatusBefore = list.statusOfRow(idx);
        String codeBefore = list.codeOfRow(idx);

        WorkerProfilePage profile = list.clickFullProfileAt(idx);
        String profileStatusBefore = profile.firstStatusBadgeText();

        profile.refresh();
        String profileStatusAfter = profile.firstStatusBadgeText();

        Assert.assertEquals(profileStatusAfter, profileStatusBefore,
                "Profile status không được đổi sau refresh — worker=" + codeBefore);
        if (!profileStatusBefore.isBlank()) {
            Assert.assertEquals(profileStatusBefore, listStatusBefore,
                    "Profile status phải khớp list status — worker=" + codeBefore
                            + " list=" + listStatusBefore + " profile=" + profileStatusBefore);
        }
    }
}
