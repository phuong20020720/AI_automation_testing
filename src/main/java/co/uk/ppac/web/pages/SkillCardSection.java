package co.uk.ppac.web.pages;

import co.uk.ppac.web.locators.SkillCardLocators;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * M5 Skill Card section trong Profile Details — READ-ONLY contract.
 *
 * Recon findings (phase4_profile_*):
 * - Label: "Skill Card - <Type>" (CSCS / CPCS / CISRS / NPORS / Other / Not Applicable)
 * - "Not Applicable" case: hiển thị "The duties don't require a skill card" + "Job Role: <role>"
 * - Thumbnail (khi có skill type): /_next/image proxy với S3 url backing
 *   (path chứa "skillCard-<type-lowercase>-<uid>...")
 * - "Open in fullscreen" affordance (role=button, aria-label="Open ... in fullscreen") → preview
 * - KHÔNG có input[type=file], button Upload/Replace/Delete document
 *
 * Đây là view-only display cho stgeo role; mọi TC manual giả định upload/select
 * không testable ở phía dashboard. Test class cần verify read-only contract +
 * label/structure hiển thị đúng cho từng worker. Locators: see {@link SkillCardLocators}.
 */
public class SkillCardSection {

    private final WebDriver driver;

    public SkillCardSection(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isPresent() {
        return !driver.findElements(SkillCardLocators.SKILL_LABEL).isEmpty();
    }

    public String label() {
        List<WebElement> els = driver.findElements(SkillCardLocators.SKILL_LABEL);
        return els.isEmpty() ? "" : els.get(0).getText().trim();
    }

    public boolean isNotApplicable() {
        return label().toLowerCase().contains("not applicable");
    }

    public String skillType() {
        String l = label();
        int sep = l.indexOf(" - ");
        return sep > 0 ? l.substring(sep + 3).trim() : "";
    }

    public boolean hasReasonText() {
        return !driver.findElements(SkillCardLocators.NOT_APPLICABLE_REASON).isEmpty();
    }

    public boolean hasJobRoleLabel() {
        return !driver.findElements(SkillCardLocators.JOB_ROLE_LABEL).isEmpty();
    }

    public boolean hasThumbnail() {
        return !driver.findElements(SkillCardLocators.THUMBNAIL_IMG).isEmpty();
    }

    public String thumbnailSrcset() {
        List<WebElement> imgs = driver.findElements(SkillCardLocators.THUMBNAIL_IMG);
        return imgs.isEmpty() ? "" : imgs.get(0).getAttribute("srcset");
    }

    public boolean hasFullscreenPreviewAffordance() {
        return !driver.findElements(SkillCardLocators.PREVIEW_BUTTON).isEmpty();
    }

    /**
     * Verify section không có upload/replace/remove document affordance.
     * Đây là read-only contract: tương tự M4 RtW (TC_034).
     */
    public boolean hasUploadAffordance() {
        if (!driver.findElements(SkillCardLocators.FILE_INPUT).isEmpty()) {
            return true;
        }
        return !driver.findElements(SkillCardLocators.UPLOAD_BUTTONS).isEmpty();
    }

    /**
     * Verify KHÔNG có UI để chọn skill type (CSCS/CPCS/CISRS/NPORS/Other).
     * Dashboard role là viewer — type được set ở mobile app khi worker submit.
     */
    public boolean hasTypeSelectorAffordance() {
        for (String t : List.of("CSCS", "CPCS", "CISRS", "NPORS")) {
            if (!driver.findElements(SkillCardLocators.typeSelector(t)).isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
