package co.uk.ppac.mobile.locators.bally;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Locator cho <b>bottom-sheet "Select" dialog</b> dùng chung cho mọi
 * dropdown searchable của onboarding (Trade/Consultant/Where did you hear/Payroll
 * ở M02, Type ở M03…). Trích THẬT từ {@code appium_get_page_source}
 * ({@code com.ppac.app.sandbox}, emulator-5554, 2026-06-16).
 *
 * <p>Cấu trúc dialog ({@code pane-title="Dialog"}):
 * <ul>
 *   <li>Tiêu đề: {@code content-desc="Select <Field>"} (vd "Select Trade").</li>
 *   <li>Ô search: {@code android.widget.EditText} (hint "Search") — DUY NHẤT trong dialog.</li>
 *   <li>Option: View clickable, {@code content-desc} = nhãn option.</li>
 *   <li>Empty: {@code content-desc="No found"}.</li>
 *   <li>Nút {@code content-desc="Done"}; nền {@code content-desc="Scrim"} (tap để đóng).</li>
 * </ul>
 *
 * <p><b>⚠️ Lưu ý:</b> option list là DỮ LIỆU BACKEND. Trên sandbox/offline hiện tại
 * các dropdown (vd Trade) trả "No found" → CHƯA bắt được giá trị option thật.
 * {@link #optionByText(String)} là factory dùng khi đã biết nhãn option.
 *
 */
public final class SelectDialogLocators {

    private SelectDialogLocators() {
    }

    /** Tiêu đề dialog theo tên field, vd title("Trade") → "Select Trade". */
    public static By title(String fieldName) {
        return AppiumBy.accessibilityId("Select " + fieldName);
    }

    /**
     * Ô search TRONG dialog — neo theo hint "Search" để KHÔNG khớp nhầm EditText
     * khác trên form khi dialog chưa mở.
     */
    public static final By SEARCH_INPUT = AppiumBy.xpath("//android.widget.EditText[@hint=\"Search\"]");

    /** Option theo nhãn (content-desc) trong list. */
    public static By optionByText(String optionText) {
        return AppiumBy.accessibilityId(optionText);
    }

    /**
     * Option đầu tiên CHỨA chuỗi cho trước (content-desc) — dùng khi không biết
     * nguyên văn option (vd consultant là tên đầy đủ chứa từ khóa search).
     */
    public static By optionContaining(String text) {
        return AppiumBy.xpath(
                "(//android.view.View[@clickable=\"true\" and contains(@content-desc,\""
                        + text + "\")])[1]");
    }

    /**
     * Option ĐẦU TIÊN trong list (loại trừ tiêu đề "Select …", "Done", "Scrim").
     * Dùng khi không biết giá trị option, chỉ cần chọn 1 cái hợp lệ bất kỳ.
     */
    public static final By FIRST_OPTION = AppiumBy.xpath(
            "(//android.view.View[@clickable=\"true\" and string-length(@content-desc)>0"
                    + " and @content-desc!=\"Done\" and @content-desc!=\"Scrim\""
                    + " and not(starts-with(@content-desc,\"Select \"))])[1]");

    /** Trạng thái rỗng / không tìm thấy. */
    public static final By EMPTY_STATE = AppiumBy.accessibilityId("No found");

    public static final By DONE_BUTTON = AppiumBy.accessibilityId("Done");

    /** Nền mờ — tap để đóng dialog (dismissable). */
    public static final By SCRIM = AppiumBy.accessibilityId("Scrim");
}
