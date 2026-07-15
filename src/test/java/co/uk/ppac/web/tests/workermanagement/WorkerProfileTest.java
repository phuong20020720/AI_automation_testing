package co.uk.ppac.web.tests.workermanagement;

import co.uk.ppac.web.pages.WorkerListPage;
import co.uk.ppac.web.pages.WorkerProfilePage;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.Set;

/** M3 Profile — 6 TC. */
public class WorkerProfileTest extends WorkerManagementBaseTest {

    /** PPAC_WM_TC_023 — Mở Profile Details từ list */
    @Test(groups = {"smoke", "worker-management", "profile"})
    public void testOpenProfileFromList() {
        int idx = list.firstRowWithFullProfileButton();
        if (idx < 0) {
            throw new SkipException("Không có row có button View Full Profile");
        }
        WorkerProfilePage profile = list.clickFullProfileAt(idx);
        Assert.assertTrue(profile.isHeadingDisplayed(),
                "Heading 'Profile Details' phải hiển thị");
        Assert.assertTrue(profile.isOnProfileUrl(),
                "URL phải chứa `?id=` query param");
    }

    /** PPAC_WM_TC_024 — Render fields trong Profile (best-effort) */
    @Test(groups = {"worker-management", "profile"})
    public void testProfileRendersWithoutError() {
        int idx = list.firstRowWithFullProfileButton();
        if (idx < 0) {
            throw new SkipException("Không có row có button View Full Profile");
        }
        WorkerProfilePage profile = list.clickFullProfileAt(idx);
        Assert.assertTrue(profile.isHeadingDisplayed(),
                "Profile phải render với heading");

        Long bodyTextLength = (Long) ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("return document.body.innerText.length;");
        Assert.assertTrue(bodyTextLength != null && bodyTextLength > 200,
                "Profile body phải có nội dung (text > 200 chars) — actual: " + bodyTextLength);
    }

    /** PPAC_WM_TC_025 — Status display đúng (giá trị thuộc 4 status hợp lệ) */
    @Test(groups = {"worker-management", "profile"})
    public void testStatusDisplayInProfile() {
        int idx = list.firstFullyPopulatedRow();
        if (idx < 0) {
            throw new SkipException("Không có row có status badge + Full Profile button");
        }
        String listStatus = list.statusOfRow(idx);
        WorkerProfilePage profile = list.clickFullProfileAt(idx);
        String profileStatus = profile.firstStatusBadgeText();

        Set<String> valid = Set.of("Go to Site", "Active", "Rejected", "Expired");
        Assert.assertTrue(valid.contains(listStatus),
                "Status ở list phải hợp lệ — actual: " + listStatus);
        if (!profileStatus.isBlank()) {
            Assert.assertTrue(valid.contains(profileStatus),
                    "Status ở profile phải hợp lệ — actual: " + profileStatus);
        }
    }

    /** PPAC_WM_TC_026 — Back navigation về list */
    @Test(groups = {"worker-management", "profile"})
    public void testBackButtonReturnsToList() {
        int idx = list.firstRowWithFullProfileButton();
        if (idx < 0) {
            throw new SkipException("Không có row có button View Full Profile");
        }
        WorkerProfilePage profile = list.clickFullProfileAt(idx);
        WorkerListPage backToList = profile.clickBack();
        Assert.assertTrue(backToList.isOnListUrl(),
                "Click Back phải quay về URL list — current: " + driver.getCurrentUrl());
        Assert.assertTrue(backToList.rowCount() > 0,
                "List re-render với rows sau khi back");
    }

    /** PPAC_WM_TC_027 — Long content overflow (best-effort: không crash khi mở profile) */
    @Test(groups = {"worker-management", "profile"})
    public void testLongContentDoesNotBreakLayout() {
        int idx = list.firstRowWithFullProfileButton();
        if (idx < 0) {
            throw new SkipException("Không có row có button View Full Profile");
        }
        WorkerProfilePage profile = list.clickFullProfileAt(idx);
        Long horizontalScroll = (Long) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "return document.documentElement.scrollWidth - document.documentElement.clientWidth;");
        Assert.assertTrue(profile.isHeadingDisplayed(),
                "Profile vẫn render dù content có dài");
        Assert.assertTrue(horizontalScroll <= 50,
                "Toàn trang KHÔNG được horizontal scroll quá 50px (1920 viewport) — actual: " + horizontalScroll);
    }

    /** PPAC_WM_TC_028 — Refresh Profile giữ state */
    @Test(groups = {"worker-management", "profile"})
    public void testRefreshProfileKeepsState() {
        int idx = list.firstRowWithFullProfileButton();
        if (idx < 0) {
            throw new SkipException("Không có row có button View Full Profile");
        }
        WorkerProfilePage profile = list.clickFullProfileAt(idx);
        String idBefore = profile.currentWorkerId();
        profile.refresh();
        Assert.assertEquals(profile.currentWorkerId(), idBefore,
                "Sau refresh, workerId trong URL phải giữ nguyên");
        Assert.assertTrue(profile.isHeadingDisplayed(),
                "Heading vẫn hiển thị sau refresh");
    }
}
