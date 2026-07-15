package co.uk.ppac.web.locators;

import org.openqa.selenium.By;

/**
 * Locators tập trung cho Profile Details page ({@code /en/worker-management?id=<workerId>}).
 */
public final class WorkerProfileLocators {

    private WorkerProfileLocators() {
    }

    public static final By PROFILE_HEADING =
            By.xpath("//h6[normalize-space(.)='Profile Details']");
    public static final By BACK_BUTTON =
            By.xpath("//button[.//*[local-name()='svg' and contains(@class,'lucide-arrow-left')]]");
    public static final By STATUS_BADGE =
            By.cssSelector("span[data-slot='badge']");
    public static final By RTW_LABEL = By.xpath(
            "//p[starts-with(normalize-space(.),'Right To Work')]");
    public static final By RTW_IFRAME = By.cssSelector("iframe[src*='documents-storage']");

    /** Giá trị Status trong Profile: {@code <p>Status</p>} + sibling {@code <div>{value}</div>}. */
    public static final By PROFILE_STATUS_VALUE =
            By.xpath("//p[normalize-space(.)='Status']/following-sibling::div[1]");
    public static final By FILE_INPUT = By.cssSelector("input[type='file']");
    public static final By UPLOAD_REPLACE_BUTTONS = By.xpath(
            "//button[contains(translate(normalize-space(.),'UPLOAD','upload'),'upload')"
                    + " or contains(translate(normalize-space(.),'REPLACE','replace'),'replace')"
                    + " or contains(translate(normalize-space(.),'REMOVE DOCUMENT','remove document'),'remove document')]");
    public static final By INCEPTION_AFFORDANCE = By.xpath(
            "//button[contains(translate(normalize-space(.),'INCEPTION','inception'),'inception')"
                    + " or contains(translate(normalize-space(.),'AUDIT','audit'),'audit')"
                    + " or contains(translate(normalize-space(.),'HISTORY','history'),'history')"
                    + " or contains(translate(normalize-space(.),'TIMELINE','timeline'),'timeline')]"
                    + " | //*[@role='button' and (contains(translate(normalize-space(.),'INCEPTION','inception'),'inception')"
                    + " or contains(translate(normalize-space(.),'HISTORY','history'),'history'))]");
    public static final By PPAC_REPORT_BUTTON =
            By.xpath("//button[contains(normalize-space(.),'PPAC Report')]");
    public static final By ANY_IFRAME = By.cssSelector("iframe");
    public static final By FULLSCREEN_PREVIEW_AFFORDANCE = By.xpath(
            "//*[@role='button' and contains(@aria-label,'fullscreen') and contains(@aria-label,'Open')]");
    public static final By PREVIEW_OVERLAY = By.cssSelector(
            "div[role='dialog'], [data-slot='dialog-content'], .lightbox, [data-state='open']");
}
