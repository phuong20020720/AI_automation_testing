package co.uk.ppac.web.tests.workermanagement;

import co.uk.ppac.web.pages.SkillCardSection;
import co.uk.ppac.web.pages.WorkerProfilePage;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.Set;

/**
 * Phase 4 — M5 Skill Card (10 TC).
 *
 * RECON FINDING (phase4_profile_*): Dashboard role stgeo CHỈ view Skill Card —
 * KHÔNG có UI để chọn type / upload / replace / delete. Worker tự submit qua
 * mobile app khi onboarding. Vì vậy TC manual TC_043-052 (assumed editable form)
 * KHÔNG TESTABLE từ dashboard. Test class này:
 *
 * 1. Verify READ-ONLY contract: TC_043-046 (chọn type) → ASSERT NO type selector
 * 2. TC_047 (Other free text) → ASSERT label "Skill Card - Other" hiển thị nếu data có; KHÔNG có textarea/input
 * 3. TC_048 (Validation rỗng) → DISABLED (chỉ relevant trong submit form mobile)
 * 4. TC_049 (XSS Other) → ASSERT label rendered text-only (no script execution) nếu có data
 * 5. TC_050 (BVA max length) → DISABLED — không có input
 * 6. TC_051 (Not Applicable hiển thị reason + job role) → assertion structure
 * 7. TC_052 (Switch type clear) → DISABLED — không có way to switch
 */
public class WorkerSkillCardTest extends WorkerManagementBaseTest {

    private static final Set<String> KNOWN_SKILL_TYPES = Set.of(
            "CSCS", "CPCS", "CISRS", "NPORS", "Other", "Not Applicable");

    /** PPAC_WM_TC_043 / 044 / 045 / 046 (gộp) — Read-only contract: KHÔNG có type selector UI */
    @Test(groups = {"worker-management", "skill"})
    public void testSkillCardHasNoTypeSelector() {
        SkillCardSection skill = openProfileWithSkillSection();
        Assert.assertFalse(skill.hasTypeSelectorAffordance(),
                "Profile KHÔNG được có button/option để chọn skill type (CSCS/CPCS/CISRS/NPORS) — "
                        + "vi phạm read-only contract M5 (TC_043-046)");
    }

    /** PPAC_WM_TC_047 — Free text Other không có input editable từ dashboard */
    @Test(groups = {"worker-management", "skill"})
    public void testSkillCardOtherHasNoFreeTextInput() {
        SkillCardSection skill = openProfileWithSkillSection();
        Assert.assertFalse(skill.hasUploadAffordance(),
                "Profile KHÔNG được có input/textarea cho 'Other' description từ dashboard (read-only)");
    }

    /** PPAC_WM_TC_048 — DISABLED: validation rỗng chỉ relevant khi có submit form (mobile/onboarding). */
    @Test(groups = {"worker-management", "skill"}, enabled = false,
            description = "Disabled: dashboard không có submit form Skill Card. TC chỉ testable ở mobile app.")
    public void testSkillCardOtherEmptyValidation() {
        // Out of scope for dashboard automation.
    }

    /**
     * PPAC_WM_TC_049 — XSS payload trong free text Other (cross-site sanitization).
     * Nếu UAT có worker submitted "Other" với payload, label phải render escaped.
     * Hiện tại UAT 6 worker đầu không có "Other" → skip.
     */
    @Test(groups = {"worker-management", "skill", "security"})
    public void testSkillCardOtherLabelEscapesScriptIfPresent() {
        int rc = list.rowCount();
        for (int i = 0; i < Math.min(rc, 10); i++) {
            if (!list.rowHasFullProfileButton(i)) {
                continue;
            }
            WorkerProfilePage profile = list.clickFullProfileAt(i);
            SkillCardSection skill = profile.skillCard();
            if (skill.isPresent() && skill.skillType().equalsIgnoreCase("Other")) {
                String label = skill.label();
                Assert.assertFalse(label.toLowerCase().contains("<script"),
                        "Label Skill Card KHÔNG được chứa raw <script> tag (XSS) — actual: " + label);
                Assert.assertFalse(label.contains("onerror="),
                        "Label KHÔNG được chứa onerror handler — actual: " + label);
                profile.clickBack();
                return;
            }
            profile.clickBack();
        }
        throw new SkipException("UAT page 1 không có worker với Skill Card 'Other' — XSS path chưa testable");
    }

    /** PPAC_WM_TC_050 — DISABLED: max length input — không có input từ dashboard */
    @Test(groups = {"worker-management", "skill"}, enabled = false,
            description = "Disabled: không có input để test max length từ dashboard view")
    public void testSkillCardOtherMaxLength() {
        // Out of scope.
    }

    /** PPAC_WM_TC_051 — Not Applicable hiển thị đúng + reason text + job role */
    @Test(groups = {"worker-management", "skill", "smoke"})
    public void testSkillCardNotApplicableStructure() {
        SkillCardSection skill = findSkillSection(s -> s.isNotApplicable());
        if (skill == null) {
            throw new SkipException("UAT page 1 không có worker 'Not Applicable'");
        }
        Assert.assertTrue(skill.label().contains("Not Applicable"),
                "Label phải chứa 'Not Applicable' — actual: " + skill.label());
        Assert.assertTrue(skill.hasReasonText(),
                "Khi Not Applicable, phải có reason text 'The duties don't require a skill card'");
        Assert.assertTrue(skill.hasJobRoleLabel(),
                "Khi Not Applicable, phải hiển thị 'Job Role:' kèm role name");
    }

    /** PPAC_WM_TC_052 — DISABLED: không có way switch type từ dashboard */
    @Test(groups = {"worker-management", "skill"}, enabled = false,
            description = "Disabled: dashboard không có way switch skill type — chỉ view")
    public void testSkillCardTypeSwitchClearsData() {
        // Out of scope.
    }

    /** Bổ sung TC_M5_001 — Skill Card section LUÔN hiển thị label với prefix "Skill Card -" */
    @Test(groups = {"worker-management", "skill", "smoke"})
    public void testSkillCardLabelHasPrefix() {
        SkillCardSection skill = openProfileWithSkillSection();
        String label = skill.label();
        Assert.assertTrue(label.startsWith("Skill Card"),
                "Skill Card label phải bắt đầu bằng 'Skill Card' — actual: " + label);
        Assert.assertTrue(label.contains(" - "),
                "Label phải có format 'Skill Card - <Type>' — actual: " + label);
        String type = skill.skillType();
        Assert.assertTrue(KNOWN_SKILL_TYPES.stream().anyMatch(t -> t.equalsIgnoreCase(type)),
                "Skill type phải thuộc known set " + KNOWN_SKILL_TYPES + " — actual: '" + type + "'");
    }

    /** Bổ sung TC_M5_002 — Khi có skill type cụ thể (CSCS), thumbnail + fullscreen affordance phải có */
    @Test(groups = {"worker-management", "skill"})
    public void testSkillCardThumbnailHasFullscreenAffordance() {
        SkillCardSection skill = findSkillSection(s -> !s.isNotApplicable() && s.isPresent());
        if (skill == null) {
            throw new SkipException("Không tìm thấy worker có Skill Card với thumbnail (CSCS/CPCS/...)");
        }
        Assert.assertTrue(skill.hasFullscreenPreviewAffordance(),
                "Skill Card có thumbnail phải có 'Open in fullscreen' affordance — type=" + skill.skillType());
        String srcset = skill.thumbnailSrcset();
        Assert.assertTrue(srcset.contains("skillCard"),
                "Thumbnail srcset phải reference path 'skillCard-*' — actual: " + srcset);
    }

    // ───────────── helpers ─────────────

    private SkillCardSection openProfileWithSkillSection() {
        int rc = list.rowCount();
        for (int i = 0; i < Math.min(rc, 6); i++) {
            if (!list.rowHasFullProfileButton(i)) {
                continue;
            }
            WorkerProfilePage profile = list.clickFullProfileAt(i);
            SkillCardSection skill = profile.skillCard();
            if (skill.isPresent()) {
                return skill;
            }
            profile.clickBack();
        }
        throw new SkipException("Không có worker với Skill Card section trên page 1");
    }

    private SkillCardSection findSkillSection(java.util.function.Predicate<SkillCardSection> match) {
        int rc = list.rowCount();
        for (int i = 0; i < Math.min(rc, 10); i++) {
            if (!list.rowHasFullProfileButton(i)) {
                continue;
            }
            WorkerProfilePage profile = list.clickFullProfileAt(i);
            SkillCardSection skill = profile.skillCard();
            if (skill.isPresent() && match.test(skill)) {
                return skill;
            }
            profile.clickBack();
        }
        return null;
    }
}
