package co.uk.ppac.web.locators;

import org.openqa.selenium.By;

/**
 * Locators tập trung cho Skill Card section trong Profile Details (READ-ONLY contract).
 */
public final class SkillCardLocators {

    private SkillCardLocators() {
    }

    public static final By SKILL_LABEL = By.xpath(
            "//p[starts-with(normalize-space(.),'Skill Card')]");
    public static final By NOT_APPLICABLE_REASON = By.xpath(
            "//p[contains(normalize-space(.),'duties don') or contains(normalize-space(.),'no skill card')]");
    public static final By JOB_ROLE_LABEL = By.xpath(
            "//p[normalize-space(.)='Job Role:']");
    public static final By PREVIEW_BUTTON = By.xpath(
            "//div[@role='button' and contains(@aria-label,'fullscreen') and contains(@aria-label,'Open')]");
    public static final By THUMBNAIL_IMG = By.cssSelector("img[srcset*='skillCard']");
    public static final By FILE_INPUT = By.cssSelector("input[type='file']");
    public static final By UPLOAD_BUTTONS = By.xpath(
            "//button[contains(translate(normalize-space(.),'UPLOAD','upload'),'upload')"
                    + " or contains(translate(normalize-space(.),'REPLACE','replace'),'replace')"
                    + " or contains(translate(normalize-space(.),'CHANGE SKILL','change skill'),'change skill')]");

    /** Affordance chọn skill type (CSCS/CPCS/...) — kỳ vọng KHÔNG tồn tại (read-only). */
    public static By typeSelector(String type) {
        return By.xpath(
                "//button[normalize-space(.)='" + type + "']"
                        + " | //*[@role='radio' and normalize-space(.)='" + type + "']"
                        + " | //*[@role='option' and normalize-space(.)='" + type + "']");
    }
}
