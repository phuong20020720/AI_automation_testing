package co.uk.ppac.mobile.locators.bally;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * M09 "Declaration" (BALLYCOMMON-RAIL onboarding,
 * màn CUỐI; chip stepper "Declaration").
 *
 * <p>Trích THẬT từ {@code appium_get_page_source} của {@code com.ppac.app.sandbox}
 * trên emulator-5554 (Pixel7, Android 13), ngày 2026-06-16.
 *
 * <p>11 mục declaration, mỗi mục là 1 checkbox = {@code android.view.View
 * clickable=true}, content-desc = nguyên văn. Mục 11 chứa cả đoạn GDPR trong
 * ngoặc (KHÔNG phải mục info-only riêng — đúng TC_123). Nút cuối "Next →" thực
 * hiện SUBMIT onboarding (TC_125) và bị DISABLED khi chưa tick đủ (CR-01).
 *
 * <p><b>⚠️ Inspect dừng TRƯỚC khi submit — không tap "Next →" để tránh tạo
 * onboarding thật trên backend.</b>
 *
 */
public final class M09DeclarationLocators {

    private M09DeclarationLocators() {
    }

    /** 11 mục declaration đúng thứ tự (content-desc THẬT, nguyên văn). */
    public static final String[] DECLARATIONS = {
            "I declare that the contents of this application form are true.",
            "I authorise Ballycommon to contact my past employers for references.",
            "I am fit, well and able to undertake manual work on building, M & E, civil & railway engineering contracts.",
            "I authorise the deduction of equipment, services or training costs from my payments.",
            "I am not suffering from any occupational illness that could affect my ability to perform my duties.",
            "I have never been dismissed from any company for being under the influence of drugs or alcohol.",
            "I am willing to undertake a drugs and/or alcohol test at any time if requested by Ballycommon or the client.",
            "I will inform Ballycommon if I am taking any medication that may affect my ability to undertake my duties.",
            "I will inform Ballycommon of any hours worked for other employers and will comply with the relevant industry standards relating to working hours.",
            "I will inform Ballycommon of any changes to my medical condition or my fitness for work.",
            "I consent to Ballycommon sharing my personal data with their clients and other relevant third parties involved in the recruitment and compliance process. (Ballycommon will only share information that is necessary for assessing your suitability for roles, arranging placements or adhering to client compliance requests and will always handle your data in accordance with UK data-protection legislation).",
    };

    public static final By STEP_CHIP = AppiumBy.accessibilityId("Declaration");

    /** Heading "Declaration" (clickable=false để khỏi trùng chip stepper). */
    public static final By SCREEN_HEADING = AppiumBy.xpath(
            "//android.view.View[@content-desc=\"Declaration\" and @clickable=\"false\"]");

    /** Mục consent GDPR (mục 11) — tiện dùng riêng (TC_126). */
    public static final By CONSENT_GDPR = AppiumBy.accessibilityId(DECLARATIONS[10]);

    /** Nút Submit cuối (nhãn "Next →", disabled tới khi tick đủ — CR-01/TC_150). */
    public static final By SUBMIT_BUTTON = AppiumBy.accessibilityId("Next →");

    /** Checkbox declaration theo index 1..11. */
    public static By declarationCheckbox(int n) {
        return AppiumBy.accessibilityId(DECLARATIONS[n - 1]);
    }
}
