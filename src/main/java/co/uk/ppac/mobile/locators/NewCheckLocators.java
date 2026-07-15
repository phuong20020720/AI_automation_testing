package co.uk.ppac.mobile.locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Locators tập trung cho New Check Step 1 / M01 — "Enter company prefix" (màn
 * Welcome onboarding). Đã gộp các locator inspect THẬT cho luồng BALLYCOMMON-RAIL.
 *
 * <p>App Flutter: nhãn tĩnh lộ qua {@code content-desc} (= accessibility id); ô
 * prefix là {@code EditText} duy nhất nên dùng class XPath.
 *
 * <p><b>Dropdown gợi ý:</b> CHỈ hiện sau khi gõ prefix vào {@link #PREFIX_INPUT}.
 * Với BALLYCOMMON phải gõ đủ <b>"BALLY"</b> (gõ "BALL" chưa đủ). Lưu ý dùng key
 * events thật (Appium {@code setValue} w3cActions) vì {@code setValue} thường
 * không kích hoạt autocomplete của Flutter. Khi đó dropdown hiện 2 item thật:
 * "Ballycommon - bally - Construction" và "Ballycommon - bally - Rail"
 * (định dạng "&lt;Company&gt; - &lt;prefix&gt; - &lt;Type&gt;").
 */
public final class NewCheckLocators {

    private NewCheckLocators() {
    }

    /** Tiêu đề động "Welcome to PPAC, &lt;User&gt;!" — match phần cố định. */
    public static final By WELCOME_TITLE = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionStartsWith(\"Welcome to PPAC,\")");

    public static final By SCREEN_HEADING = AppiumBy.accessibilityId("Enter company prefix");

    /** Mô tả dưới heading. */
    public static final By HELP_TEXT =
            AppiumBy.accessibilityId("We use this to identify your contractor or site.");

    /** Helper (icon ?) — dấu nháy cong U+2019 trong "you’re". */
    public static final By HINT_TEXT =
            AppiumBy.accessibilityId("If you’re unsure, ask your on-site manager.");

    public static final By PREFIX_INPUT = AppiumBy.xpath("//android.widget.EditText");
    public static final By CONTINUE_BUTTON = AppiumBy.accessibilityId("Continue →");

    // ---- Dropdown gợi ý BALLYCOMMON (hiện sau khi gõ "BALLY") ----
    public static final By SUGGESTION_BALLYCOMMON_CONSTRUCTION =
            AppiumBy.accessibilityId("Ballycommon - bally - Construction");
    public static final By SUGGESTION_BALLYCOMMON_RAIL =
            AppiumBy.accessibilityId("Ballycommon - bally - Rail");

    /**
     * Gợi ý contractor theo prefix ("&lt;Company&gt; - &lt;prefix&gt; - &lt;Type&gt;").
     * ⚠️ Với prefix có NHIỀU loại (vd "bally" → Construction + Rail) thì khớp NHIỀU
     * item — dùng {@link #contractorSuggestion(String, String)} để chỉ định Type.
     */
    public static By contractorSuggestion(String prefix) {
        return AppiumBy.xpath(
                "//android.view.View[@clickable='true' and "
                        + "contains(@content-desc,' - " + prefix + "')]");
    }

    /**
     * Gợi ý contractor duy nhất theo prefix + loại (vd "bally","Rail" →
     * "Ballycommon - bally - Rail"). Khớp đuôi " - &lt;prefix&gt; - &lt;type&gt;".
     */
    public static By contractorSuggestion(String prefix, String type) {
        return AppiumBy.xpath(
                "//android.view.View[@clickable='true' and "
                        + "contains(@content-desc,' - " + prefix + " - " + type + "')]");
    }

    // ---- Popup "Continue your onboarding?" (hiện khi đã có onboarding dở dang) ----
    // Bấm Continue ở màn prefix khi đang có onboarding pending → popup này.
    public static final By CONTINUE_ONBOARDING_POPUP = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionStartsWith(\"Continue your onboarding?\")");
    /** Nút tiếp tục onboarding đang dở. */
    public static final By POPUP_CONTINUE_BUTTON = AppiumBy.accessibilityId("Continue");
    /** Nút bắt đầu onboarding mới (huỷ cái dở dang) — TC_004. */
    public static final By POPUP_START_NEW_BUTTON = AppiumBy.accessibilityId("Start a new onboarding!");
}
