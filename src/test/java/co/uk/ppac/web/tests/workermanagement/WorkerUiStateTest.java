package co.uk.ppac.web.tests.workermanagement;

import co.uk.ppac.core.config.ConfigReader;
import co.uk.ppac.web.pages.WorkerListPage;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Phase 5 — M10 UI/UX & States (7 TC).
 *
 * RECON FINDING (phase5_viewport_overflow.txt):
 * - viewport 1920 / 1280 / 375 đều overflow=0, table visible
 * - empty state copy = "No data available"
 *
 * TC_079 (error state khi API 500) cần network intercept (CDP/BiDi) — disabled,
 * không nằm trong scope Selenium 4.18 cơ bản.
 */
public class WorkerUiStateTest extends WorkerManagementBaseTest {

    /** Reset viewport về desktop default sau mỗi test (selenium_rules: 1920x1080 debug). */
    @AfterMethod(alwaysRun = true)
    public void resetViewport() {
        try {
            driver.manage().window().setSize(new org.openqa.selenium.Dimension(1920, 1080));
        } catch (Exception ignored) {
        }
    }

    /**
     * PPAC_WM_TC_078 — Loading state: page fetch list KHÔNG để white screen vĩnh viễn.
     * Selenium khó bắt skeleton transient → verify: sau navigate, list render thành công
     * (loading hoàn tất, không treo). Best-effort check skeleton/spinner nếu kịp.
     */
    @Test(groups = {"worker-management", "ui-state"})
    public void testListLoadingCompletesToRenderedTable() {
        String cleanUrl = "https://" + ConfigReader.get("app.dashboardHost")
                + ConfigReader.get("app.workerManagementPath");
        driver.get(cleanUrl);
        // Quan trọng: list phải render xong (loading kết thúc, không treo white screen)
        WorkerListPage freshList = new WorkerListPage(driver);
        freshList.waitForListReady(45);
        Assert.assertTrue(freshList.isTableVisible(),
                "Sau loading, table phải render — KHÔNG được treo white screen vĩnh viễn");
    }

    /**
     * PPAC_WM_TC_079 — Error state khi API fail.
     * DISABLED — cần network intercept (CDP Network.enable + Fetch.failRequest) để mock 500;
     * ngoài scope Selenium 4.18 cơ bản. Defer khi có BiDi/proxy setup.
     */
    @Test(groups = {"worker-management", "ui-state"}, enabled = false,
            description = "Disabled: cần CDP/BiDi network intercept để mock API 500 — ngoài scope hiện tại")
    public void testListErrorStateWhenApiFails() {
        // Not testable without network mocking.
    }

    /** PPAC_WM_TC_080 — Empty list copy hiển thị khi search no-result */
    @Test(groups = {"worker-management", "ui-state", "smoke"})
    public void testEmptyListStateCopy() {
        list.search("zzz_no_such_worker_" + System.currentTimeMillis());
        Assert.assertTrue(list.isEmptyStateDisplayed(),
                "Search không match phải hiển thị empty state (rowCount=0 hoặc marker copy)");
        String copy = list.emptyStateText();
        Assert.assertFalse(copy.isBlank(),
                "Empty state phải có copy text thân thiện — KHÔNG được blank screen");
        // Recon xác nhận copy = "No data available"
        Assert.assertTrue(copy.toLowerCase().contains("no data")
                        || copy.toLowerCase().contains("no result")
                        || copy.toLowerCase().contains("no worker"),
                "Empty state copy phải mô tả rõ không có data — actual: '" + copy + "'");
        list.search("");
    }

    /** PPAC_WM_TC_081 — Desktop 1280px: 9 cột không overflow ngang toàn trang */
    @Test(groups = {"worker-management", "ui-state"})
    public void testNoHorizontalOverflowAt1280() {
        list.setViewport(1280, 720);
        Assert.assertTrue(list.isTableVisible(),
                "Tại 1280px, table vẫn phải visible");
        long overflow = list.bodyHorizontalOverflow();
        Assert.assertTrue(overflow <= 5,
                "Tại 1280px, body KHÔNG được overflow ngang toàn trang (table container có thể scroll nội bộ) "
                        + "— actual overflow=" + overflow + "px");
    }

    /** PPAC_WM_TC_082 — Desktop 1920px: layout cân đối, không overflow */
    @Test(groups = {"worker-management", "ui-state"})
    public void testLayoutBalancedAt1920() {
        list.setViewport(1920, 1080);
        Assert.assertTrue(list.isTableVisible(),
                "Tại 1920px, table phải visible");
        long overflow = list.bodyHorizontalOverflow();
        Assert.assertTrue(overflow <= 5,
                "Tại 1920px, body KHÔNG được overflow ngang — actual overflow=" + overflow + "px");
    }

    /**
     * PPAC_WM_TC_083 — Mobile 375px out-of-spec (Q22).
     * Document behavior thực tế: recon cho thấy Chrome min width ~500px, table vẫn visible,
     * overflow=0. Assert KHÔNG fail nghiêm trọng (crash / blank).
     */
    @Test(groups = {"worker-management", "ui-state"})
    public void testMobile375OutOfSpecGraceful() {
        list.setViewport(375, 667);
        // Q22: out-of-spec → không yêu cầu layout hoàn hảo, chỉ cần KHÔNG crash/blank
        boolean tableOrEmptyVisible = list.isTableVisible() || list.isEmptyStateDisplayed();
        Assert.assertTrue(tableOrEmptyVisible,
                "Tại 375px (out-of-spec), trang KHÔNG được blank/crash — table hoặc empty state vẫn render");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Long bodyHeight = (Long) js.executeScript("return document.body.scrollHeight;");
        Assert.assertTrue(bodyHeight != null && bodyHeight > 100,
                "Body phải có content (scrollHeight > 100px) — KHÔNG được blank screen tại 375px");
    }

    /** PPAC_WM_TC_084 — Multi-tab session sharing: tab mới cùng browser dùng chung session */
    @Test(groups = {"worker-management", "ui-state"})
    public void testMultiTabSessionSharing() {
        String original = driver.getWindowHandle();
        String cleanUrl = "https://" + ConfigReader.get("app.dashboardHost")
                + ConfigReader.get("app.workerManagementPath");
        try {
            ((JavascriptExecutor) driver).executeScript("window.open(arguments[0], '_blank');", cleanUrl);
            List<String> handles = driver.getWindowHandles().stream()
                    .filter(h -> !h.equals(original)).toList();
            Assert.assertFalse(handles.isEmpty(), "Tab mới phải được mở");

            driver.switchTo().window(handles.get(0));
            WorkerListPage tab2List = new WorkerListPage(driver);
            tab2List.waitForListReady(45);
            Assert.assertTrue(tab2List.isTableVisible(),
                    "Tab 2 phải dùng chung session đã login — list render mà KHÔNG cần login lại");
            Assert.assertFalse(driver.getCurrentUrl().contains("/sign-in"),
                    "Tab 2 KHÔNG được redirect về /sign-in — session phải share giữa tabs");
        } finally {
            // Cleanup: đóng các tab phụ, switch về tab gốc
            for (String h : driver.getWindowHandles()) {
                if (!h.equals(original)) {
                    driver.switchTo().window(h);
                    driver.close();
                }
            }
            driver.switchTo().window(original);
        }
    }
}
