package co.uk.ppac.mobile.locators.bally;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * M04 "Medical Self-Certification" (BALLYCOMMON-RAIL
 * onboarding, bước 3 trên stepper).
 *
 * <p>Trích THẬT từ {@code appium_get_page_source} của {@code com.ppac.app.sandbox}
 * trên emulator-5554 (Pixel7, Android 13). Re-verify 2026-06-16: <b>12 câu</b>
 * (không phải 10) + confirmation checkbox khi all=NO.
 *
 * <p>Màn là bộ <b>12 câu hỏi Yes/No</b>. Mỗi câu là 4 sibling liền kề trong
 * {@code ScrollView}: {@code View "Question N"} → {@code View <nội dung>} →
 * {@code RadioButton content-desc="Yes"} → {@code RadioButton content-desc="No"}.
 * Vì "Yes"/"No" LẶP cho mọi câu (không unique), phải neo theo "Question N"
 * bằng {@code following-sibling} (relative, không xpath tuyệt đối).
 *
 * <p>Khi trả lời <b>tất cả 12 câu = NO</b> → hiện {@link #CONFIRM_NO_CHECKBOX}.
 */
public final class M04MedicalSelfCertLocators {

    private M04MedicalSelfCertLocators() {
    }

    /** Nội dung 12 câu hỏi (đúng thứ tự Q1..Q12) — trích THẬT từ content-desc. */
    public static final String[] QUESTIONS = {
            "Do you have Diabetes needing Insulin?",
            "Do you suffer from Epilepsy or fits?",
            "Have you ever had blackouts, recurrent dizziness or any condition, which may cause sudden collapse or incapacity?",
            "Do you get discomfort or pain in the chest or shortness of breath on exercise, e.g. climbing a single flight of stairs?",
            "Do you have difficulty in moving rapidly over short distances, including on slopes, steps or rough ground?",
            "Would you have difficulty in looking over either shoulder?",
            "Do you have any difficulty with your eyesight (simple problems, needing glasses need not be included)?",
            "Do you have any difficulty hearing normal conversations?",
            "Are you taking any medication that is giving you dizziness or drowsiness?",
            "Have you used drugs of abuse within the last 12 months?",
            "Have you had any alcohol-related illness during the last 12 months?",
            "Have you experienced any Hand/Arm problems from operating vibrating equipment?",
    };

    /** Confirmation checkbox (clickable View) — chỉ hiện khi tất cả 12 câu = NO. */
    public static final By CONFIRM_NO_CHECKBOX = AppiumBy.accessibilityId(
            "I confirm that I have selected 'NO' to all of the medical self-certification declarations above.");

    // ---- Stepper (accessibility id) ----
    public static final By STEP_ONBOARDING_DETAILS = AppiumBy.accessibilityId("Onboarding Details");
    public static final By STEP_REFERENCES         = AppiumBy.accessibilityId("References");
    public static final By STEP_MEDICAL            = AppiumBy.accessibilityId("Medical Self-Certification");

    /**
     * Heading "Medical Self-Certification". LƯU Ý trùng content-desc với chip
     * stepper (clickable=true); heading là clickable=false → lọc cho unique.
     */
    public static final By SCREEN_HEADING = AppiumBy.xpath(
            "//android.view.View[@content-desc=\"Medical Self-Certification\" and @clickable=\"false\"]");

    /** Đoạn intro (1 node nhiều đoạn) — match phần đầu cho bền. */
    public static final By INTRO_TEXT = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionStartsWith(\"Alertness and reasonable physical fitness\")");

    public static final By NEXT_BUTTON = AppiumBy.accessibilityId("Next →");

    // ---- Pattern theo câu hỏi (n = 1..10) ----

    /** Nhãn "Question N". */
    public static By questionLabel(int n) {
        return AppiumBy.accessibilityId("Question " + n);
    }

    /** Nội dung câu hỏi N (= QUESTIONS[n-1]) — định vị bằng accessibility id. */
    public static By questionText(int n) {
        return AppiumBy.accessibilityId(QUESTIONS[n - 1]);
    }

    /** Radio "Yes" của câu N — radio đầu tiên sau nhãn "Question N". */
    public static By yesRadio(int n) {
        return AppiumBy.xpath("//android.view.View[@content-desc=\"Question " + n + "\"]"
                + "/following-sibling::android.widget.RadioButton[1]");
    }

    /** Radio "No" của câu N — radio thứ hai sau nhãn "Question N". */
    public static By noRadio(int n) {
        return AppiumBy.xpath("//android.view.View[@content-desc=\"Question " + n + "\"]"
                + "/following-sibling::android.widget.RadioButton[2]");
    }
}
