package co.uk.ppac.mobile.locators.bally;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * M07 "Safety Critical Certifications"
 * (BALLYCOMMON-RAIL onboarding; chip stepper app gọi là <b>"Pre-Deployment"</b>).
 *
 * <p>Trích THẬT từ {@code appium_get_page_source} của {@code com.ppac.app.sandbox}
 * trên emulator-5554 (Pixel7, Android 13), ngày 2026-06-16.
 *
 * <p>Cấu trúc: 1 gate question (Yes/No) + 16 cert, mỗi cert = label
 * ({@code content-desc}) + 2 RadioButton "Yes"/"No". Yes/No lặp → neo theo label.
 *
 */
public final class M07SafetyCriticalLocators {

    private M07SafetyCriticalLocators() {
    }

    /** 16 cert đúng thứ tự (content-desc THẬT). LƯU Ý: "LB 3rd – R ST-I" dùng en-dash U+2013. */
    public static final String[] CERTS = {
            "PTS AC", "PTS DCCR", "AOD PO", "AOD LXA", "COSS", "LKT/SW",
            "Level A", "IWA", "PC", "PS", "ES", "MC/CC",
            "LB 3rd – R ST-I", "DLR Track Awareness", "PiCOW", "Other",
    };

    /** Chip stepper để vào màn này (app đặt tên "Pre-Deployment"). */
    public static final By STEP_CHIP = AppiumBy.accessibilityId("Pre-Deployment");

    /** Heading (content-desc 2 dòng "Safety Critical\nCertifications"). */
    public static final By SCREEN_HEADING = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionStartsWith(\"Safety Critical\")");

    // ---- Gate question ----
    public static final By GATE_QUESTION =
            AppiumBy.accessibilityId("Are you subject to any medical restrictions?");
    public static final By GATE_YES = gateYes();
    public static final By GATE_NO = gateNo();

    public static final By RESTRICTIONS_INTRO =
            AppiumBy.accessibilityId("Please provide below details of all competency restrictions");

    public static final By NEXT_BUTTON = AppiumBy.accessibilityId("Next →");

    // ---- Gate Yes/No (neo trên câu gate) ----
    public static By gateYes() {
        return AppiumBy.xpath("//android.view.View[@content-desc=\"Are you subject to any medical restrictions?\"]"
                + "/following-sibling::android.widget.RadioButton[1]");
    }

    public static By gateNo() {
        return AppiumBy.xpath("//android.view.View[@content-desc=\"Are you subject to any medical restrictions?\"]"
                + "/following-sibling::android.widget.RadioButton[2]");
    }

    // ---- Cert Yes/No (neo trên label cert) ----
    public static By certLabel(String cert) {
        return AppiumBy.accessibilityId(cert);
    }

    public static By certYes(String cert) {
        return AppiumBy.xpath("//android.view.View[@content-desc=\"" + cert + "\"]"
                + "/following-sibling::android.widget.RadioButton[1]");
    }

    public static By certNo(String cert) {
        return AppiumBy.xpath("//android.view.View[@content-desc=\"" + cert + "\"]"
                + "/following-sibling::android.widget.RadioButton[2]");
    }

    // ---- Field CÓ ĐIỀU KIỆN (đã VERIFY LIVE 2026-06-16, PTS AC=Yes) ----
    // Khi cert=Yes hiện ngay dưới cặp Yes/No của cert đó:
    //   - Nhãn (View): "<Cert> - Duration Held (Years & Months)"  (ký tự '&' thật)
    //   - Ô nhập: android.widget.EditText (KHÁC các field khác vốn là View),
    //     đứng NGAY SAU nhãn; placeholder "e.g. 2 years 3 months".

    /** Nhãn Duration của cert (chỉ hiện khi cert=Yes). */
    public static By durationLabel(String cert) {
        return AppiumBy.accessibilityId(cert + " - Duration Held (Years & Months)");
    }

    /** Ô nhập Duration (EditText) của cert — sibling kế tiếp của nhãn Duration. */
    public static By durationInput(String cert) {
        return AppiumBy.xpath("//android.view.View[@content-desc=\""
                + cert + " - Duration Held (Years & Months)\"]"
                + "/following-sibling::android.widget.EditText[1]");
    }

    // ---- "Other competencies" (đã VERIFY LIVE 2026-06-16, cert "Other"=Yes) ----
    // Hiện ngay dưới cặp Yes/No của cert "Other":
    //   - Nhãn (View): "Other competencies"
    //   - Ô nhập: android.widget.EditText (hint "Please enter here"), sibling kế tiếp.
    public static final By OTHER_COMPETENCIES_LABEL = AppiumBy.accessibilityId("Other competencies");

    public static final By OTHER_COMPETENCIES_INPUT = AppiumBy.xpath(
            "//android.view.View[@content-desc=\"Other competencies\"]"
                    + "/following-sibling::android.widget.EditText[1]");
}
