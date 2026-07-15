package co.uk.ppac.mobile.locators.bally;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * M05 "Contract of Sentinel Scheme Sponsorship"
 * (BALLYCOMMON-RAIL onboarding; chip stepper app gọi là "Sentinel Scheme Contract").
 *
 * <p>Trích THẬT từ {@code appium_get_page_source} của {@code com.ppac.app.sandbox}
 * trên emulator-5554 (Pixel7, Android 13), ngày 2026-06-16.
 *
 * <p>Màn là hợp đồng dài (nhiều section đánh số) + 1 checkbox acceptance ở cuối.
 * Checkbox là {@code android.view.View clickable=true} (KHÔNG phải native CheckBox),
 * content-desc = nguyên văn câu acceptance.
 *
 * <p>Điều hướng: tap chip stepper "Sentinel Scheme Contract" nhảy thẳng được
 * (không cần hoàn tất Medical). Stepper là HorizontalScrollView — swipe ngang để
 * lộ các chip sau (PPE, Safety Critical, Lost & Stolen, Declaration).
 *
 */
public final class M05ContractLocators {

    private M05ContractLocators() {
    }

    /** Chip stepper để vào màn này. */
    public static final By STEP_CHIP = AppiumBy.accessibilityId("Sentinel Scheme Contract");

    /** Heading (content-desc 2 dòng "Contract of Sentinel\nScheme Sponsorship") — match phần đầu. */
    public static final By SCREEN_HEADING = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionStartsWith(\"Contract of Sentinel\")");

    // ---- Section headers (accessibility id — giá trị THẬT đã quan sát) ----
    public static final By SECTION_DUTIES               = AppiumBy.accessibilityId("1. Duties");
    public static final By SECTION_MISCONDUCT           = AppiumBy.accessibilityId("4. Misconduct");
    public static final By SECTION_SENTINEL_DECLARATION = AppiumBy.accessibilityId("Sentinel Scheme Declaration");
    // Ghi chú: các section khác (2. Candidate Responsibilities, 3. Primary Sponsor
    // Responsibilities, 5. Withdrawal of Sentinel Competence Cards) hiển thị giữa
    // chừng — locate bằng accessibility id tương tự khi cần.

    /** Checkbox acceptance (clickable View) — content-desc = nguyên văn (TC_083). */
    public static final By ACCEPT_CHECKBOX = AppiumBy.accessibilityId(
            "I have read, and agree to be bound by, the above Contract of Sentinel Scheme Sponsorship.");

    public static final By NEXT_BUTTON = AppiumBy.accessibilityId("Next →");
}
