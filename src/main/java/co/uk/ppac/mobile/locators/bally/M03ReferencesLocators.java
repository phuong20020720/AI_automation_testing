package co.uk.ppac.mobile.locators.bally;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * M03 "References" (BALLYCOMMON-RAIL onboarding,
 * bước 2 trên stepper, sau M02 Your Details).
 *
 * <p>Trích THẬT từ {@code appium_get_page_source} của {@code com.ppac.app.sandbox}
 * trên emulator-5554 (Pixel7, Android 13). Re-verify 2026-06-16: màn có
 * <b>2 referee</b> (Referee 1 + Referee 2), KHÔNG phải 1.
 *
 * <p>Mỗi referee có cùng field (Type, First Name, Surname, Contact Number,
 * Relationship to Candidate) ⇒ các content-desc đó <b>LẶP cho cả 2 referee</b>,
 * KHÔNG unique. Phải neo theo THỨ TỰ XUẤT HIỆN: occurrence thứ {@code refereeIdx}
 * (1-based) = referee đó (Referee 1 đứng trước Referee 2 trong document order).
 * Dùng {@link #labelOf(int, String)} / {@link #fieldOf(int, String)}.
 *
 * <p>Field điều kiện khi Type = "Employer referee" (ĐÃ verify live 2026-06-16):
 * "Contractor or Company Name" + "Project or site you worked on" hiện ngay dưới
 * "Relationship to Candidate" của referee đó.
 */
public final class M03ReferencesLocators {

    private M03ReferencesLocators() {
    }

    public static final int REFEREE_COUNT = 2;

    /** Option dropdown Type (verify live): đúng 2 giá trị. */
    public static final String TYPE_PERSONAL_REFEREE = "Personal referee";
    public static final String TYPE_EMPLOYER_REFEREE = "Employer referee";

    // ---- Stepper (accessibility id) ----
    public static final By STEP_ONBOARDING_DETAILS = AppiumBy.accessibilityId("Onboarding Details");
    public static final By STEP_REFERENCES         = AppiumBy.accessibilityId("References");
    public static final By STEP_MEDICAL            = AppiumBy.accessibilityId("Medical Self-Certification");

    /**
     * Heading "References". LƯU Ý: content-desc "References" trùng với chip stepper
     * (clickable=true). Heading là bản clickable=false → lọc để unique.
     */
    public static final By SCREEN_HEADING =
            AppiumBy.xpath("//android.view.View[@content-desc=\"References\" and @clickable=\"false\"]");

    /** Đoạn intro (1 node content-desc nhiều đoạn) — match phần đầu cho bền. */
    public static final By INTRO_TEXT = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionStartsWith(\"You must provide the details of someone\")");

    public static final By REFEREE_1_HEADER = AppiumBy.accessibilityId("Referee 1");
    public static final By REFEREE_2_HEADER = AppiumBy.accessibilityId("Referee 2");

    public static final By NEXT_BUTTON = AppiumBy.accessibilityId("Next →");

    // ---- Per-referee (refereeIdx = 1 | 2) ----

    /** Nhãn (occurrence thứ refereeIdx của label lặp). */
    public static By labelOf(int refereeIdx, String label) {
        return AppiumBy.xpath(
                "(//android.view.View[@content-desc=\"" + label + "\"])[" + refereeIdx + "]");
    }

    /** Field (input/dropdown) ngay sau nhãn thứ refereeIdx. */
    public static By fieldOf(int refereeIdx, String label) {
        return AppiumBy.xpath(
                "(//android.view.View[@content-desc=\"" + label + "\"])[" + refereeIdx
                        + "]/following-sibling::android.view.View[1]");
    }

    /** Dropdown loại referee (placeholder "Please select your referee type" — của referee thứ idx). */
    public static By typeDropdown(int refereeIdx) {
        return labelOf(refereeIdx, "Please select your referee type");
    }

    public static By firstNameInput(int refereeIdx)    { return fieldOf(refereeIdx, "First Name"); }
    public static By surnameInput(int refereeIdx)      { return fieldOf(refereeIdx, "Surname"); }
    public static By contactNumberInput(int refereeIdx) { return fieldOf(refereeIdx, "Contact Number"); }
    public static By relationshipInput(int refereeIdx) { return fieldOf(refereeIdx, "Relationship to Candidate"); }

    // ---- Field điều kiện khi Type = Employer referee (ĐÃ verify live) ----
    public static By contractorCompanyInput(int refereeIdx) {
        return fieldOf(refereeIdx, "Contractor or Company Name");
    }

    public static By projectSiteInput(int refereeIdx) {
        return fieldOf(refereeIdx, "Project or site you worked on");
    }
}
