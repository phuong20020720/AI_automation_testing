package co.uk.ppac.web.pages;

import co.uk.ppac.core.base.BasePage;
import co.uk.ppac.core.config.ConfigReader;
import co.uk.ppac.web.locators.WorkerProfileLocators;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

/**
 * Profile Details page mở qua nút "View Full Profile" — URL pattern
 * `/en/worker-management?id=<workerId>` (query param, không phải path).
 * Locators: see {@link WorkerProfileLocators}.
 */
public class WorkerProfilePage extends BasePage {

    public WorkerProfilePage(WebDriver driver) {
        super(driver);
    }

    public WorkerProfilePage waitForReady() {
        new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(20))
                .until(d -> !d.findElements(WorkerProfileLocators.PROFILE_HEADING).isEmpty()
                        && d.getCurrentUrl().contains("?id="));
        return this;
    }

    public boolean isHeadingDisplayed() {
        return isPresent(WorkerProfileLocators.PROFILE_HEADING);
    }

    public String currentWorkerId() {
        String url = driver.getCurrentUrl();
        int idx = url.indexOf("?id=");
        if (idx < 0) {
            return "";
        }
        String tail = url.substring(idx + 4);
        int amp = tail.indexOf('&');
        return amp >= 0 ? tail.substring(0, amp) : tail;
    }

    public boolean isOnProfileUrl() {
        String url = driver.getCurrentUrl();
        return url.contains(ConfigReader.get("app.workerManagementPath"))
                && url.contains("?id=");
    }

    /**
     * Profile dùng pattern `<p>Status</p>` + `<div>{value}</div>` (sibling), KHÔNG phải
     * `<span data-slot="badge">` như list. Fallback đọc cả 2 nếu structure đổi.
     *
     * Status load 2-phase: "Pending" placeholder hiển thị trước, sau khi API trả về
     * mới render giá trị thật. Wait đến khi giá trị thuộc set hợp lệ.
     */
    public String firstStatusBadgeText() {
        java.util.Set<String> finalStatuses = java.util.Set.of(
                "Go to Site", "Active", "Rejected", "Expired");
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(d -> {
                        String t = readStatusRaw(d);
                        return !t.isBlank() && finalStatuses.contains(t);
                    });
        } catch (org.openqa.selenium.TimeoutException ignored) {
        }
        return readStatusRaw(driver);
    }

    private String readStatusRaw(org.openqa.selenium.WebDriver d) {
        List<WebElement> profileStatus = d.findElements(WorkerProfileLocators.PROFILE_STATUS_VALUE);
        if (!profileStatus.isEmpty()) {
            String text = profileStatus.get(0).getText().trim();
            if (!text.isBlank()) {
                return text;
            }
        }
        List<WebElement> badges = d.findElements(WorkerProfileLocators.STATUS_BADGE);
        if (!badges.isEmpty()) {
            return badges.get(0).getText().trim();
        }
        return "";
    }

    public WebElement findStatusElement() {
        List<WebElement> profileStatus = driver.findElements(WorkerProfileLocators.PROFILE_STATUS_VALUE);
        if (!profileStatus.isEmpty()) {
            return profileStatus.get(0);
        }
        return driver.findElement(WorkerProfileLocators.STATUS_BADGE);
    }

    public WorkerListPage clickBack() {
        WebElement back = waitForVisible(WorkerProfileLocators.BACK_BUTTON);
        js().executeScript("arguments[0].click();", back);
        WorkerListPage list = new WorkerListPage(driver);
        list.waitForListReady();
        return list;
    }

    public WorkerProfilePage refresh() {
        driver.navigate().refresh();
        waitForReady();
        return this;
    }

    /**
     * UI thật chỉ hiển thị 1 RtW type per worker (không phải 5 slot riêng như TC manual).
     * Trả về text label dạng "Right To Work - ShareCode" / "Right To Work - Passport" / etc.
     * Wait đến khi label load đầy đủ "- <Type>" (async — text "Right To Work" hiển thị trước,
     * sau đó "- ShareCode" được append sau khi API trả về).
     */
    public String rtwLabelText() {
        if (!isPresent(WorkerProfileLocators.RTW_LABEL)) {
            return "";
        }
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(d -> {
                        List<WebElement> labels = d.findElements(WorkerProfileLocators.RTW_LABEL);
                        return !labels.isEmpty() && labels.get(0).getText().contains(" - ");
                    });
        } catch (org.openqa.selenium.TimeoutException ignored) {
        }
        return driver.findElement(WorkerProfileLocators.RTW_LABEL).getText().trim();
    }

    public boolean isRtwSectionPresent() {
        return isPresent(WorkerProfileLocators.RTW_LABEL);
    }

    public boolean isRtwDocumentRendered() {
        return isPresent(WorkerProfileLocators.RTW_IFRAME);
    }

    public String rtwDocumentUrl() {
        if (!isPresent(WorkerProfileLocators.RTW_IFRAME)) {
            return "";
        }
        return driver.findElement(WorkerProfileLocators.RTW_IFRAME).getAttribute("src");
    }

    /**
     * Kiểm tra section RtW KHÔNG có upload/replace/delete button — read-only contract.
     * Tìm trong toàn page (Profile) các affordance upload mà người dùng có thể trigger:
     * input[type=file], button text Upload/Replace/Remove/Delete liên quan document.
     */
    public boolean hasUploadOrReplaceAffordance() {
        if (!driver.findElements(WorkerProfileLocators.FILE_INPUT).isEmpty()) {
            return true;
        }
        return !driver.findElements(WorkerProfileLocators.UPLOAD_REPLACE_BUTTONS).isEmpty();
    }

    public SkillCardSection skillCard() {
        return new SkillCardSection(driver);
    }

    /**
     * M9 — Phase 5 recon xác nhận dashboard KHÔNG có "Open Inception" / audit history feature.
     * Helper này tìm bất kỳ affordance nào có thể là Inception (text Inception/Audit/History/Timeline).
     * Trả về true nếu tồn tại — kỳ vọng false (feature absent → liên quan F-COMP-02 no audit trail).
     */
    public boolean hasOpenInceptionAffordance() {
        return !driver.findElements(WorkerProfileLocators.INCEPTION_AFFORDANCE).isEmpty();
    }

    /**
     * M9 — "PPAC Report" button: affordance gần nhất với audit/report trên Profile (mở tab external).
     * Button nằm trong action area render async sau khi worker-detail API resolve —
     * waitForReady() chỉ chờ heading + URL nên cần wait riêng cho button này.
     */
    public boolean hasPpacReportButton() {
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(d -> !d.findElements(WorkerProfileLocators.PPAC_REPORT_BUTTON).isEmpty());
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    /** Số window/tab hiện mở — dùng verify "PPAC Report" mở tab mới (M9 fallback) hoặc multi-tab test. */
    public int windowCount() {
        return driver.getWindowHandles().size();
    }

    public int iframeCount() {
        return driver.findElements(WorkerProfileLocators.ANY_IFRAME).size();
    }

    /** Đếm affordance "Open ... in fullscreen" (đại diện preview viewer cho document/image). */
    public int fullscreenPreviewAffordanceCount() {
        return driver.findElements(WorkerProfileLocators.FULLSCREEN_PREVIEW_AFFORDANCE).size();
    }

    /** Mở preview viewer (click affordance đầu tiên), trả về element overlay nếu có. */
    public boolean openFirstPreview() {
        List<WebElement> btns = driver.findElements(WorkerProfileLocators.FULLSCREEN_PREVIEW_AFFORDANCE);
        if (btns.isEmpty()) {
            return false;
        }
        js().executeScript("arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", btns.get(0));
        try { Thread.sleep(1200); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
        return !driver.findElements(WorkerProfileLocators.PREVIEW_OVERLAY).isEmpty();
    }

    public void closeAnyOpenModal() {
        new org.openqa.selenium.interactions.Actions(driver)
                .sendKeys(org.openqa.selenium.Keys.ESCAPE).perform();
        try { Thread.sleep(500); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
    }

    /**
     * Trả về URL document RtW từ iframe nếu có (đã verified ở phase4 = signed S3 URL hoặc obfuscated path).
     * Filter ra reCAPTCHA iframe (có src chứa "recaptcha").
     */
    public String firstRtwIframeSrc() {
        List<WebElement> iframes = driver.findElements(WorkerProfileLocators.ANY_IFRAME);
        for (WebElement iframe : iframes) {
            String src = iframe.getAttribute("src");
            if (src != null && src.contains("documents-storage")) {
                return src;
            }
        }
        return "";
    }
}
