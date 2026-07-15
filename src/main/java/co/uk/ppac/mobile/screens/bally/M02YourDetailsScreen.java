package co.uk.ppac.mobile.screens.bally;

import co.uk.ppac.core.base.BaseScreen;
import co.uk.ppac.mobile.locators.bally.M02YourDetailsLocators;
import co.uk.ppac.mobile.locators.bally.SelectDialogLocators;
import io.appium.java_client.android.AndroidDriver;

/**
 * Screen Object M02 "Your Details" (BALLYCOMMON-RAIL onboarding).
 * Chỉ tham chiếu {@link M02YourDetailsLocators}; không chứa assertion.
 *
 * <p>Dropdown (Consultant/Trade/Where did you hear/Payroll) chỉ có phương thức
 * MỞ dropdown — việc chọn option cần locator của list khi mở (chưa inspect).
 *
 */
public class M02YourDetailsScreen extends BaseScreen {

    public M02YourDetailsScreen(AndroidDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(M02YourDetailsLocators.SCREEN_HEADING);
    }

    /**
     * Scroll form tới label (content-desc) cho trước. Trả về true nếu tìm thấy
     * (kể cả khi đã hiển thị sẵn); false nếu không có trong form.
     */
    public boolean scrollToLabel(String labelContentDesc) {
        try {
            gestures.scrollToContentDesc(labelContentDesc);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    // ---- Fill helpers cho luồng E2E (scroll tới field rồi nhập/chọn) ----

    /**
     * Scroll tới label rồi nhập giá trị vào ô ngay sau label đó.
     * Dùng {@link #typeFlutter} (key events thật) vì ô là Flutter View — sendKeys
     * thường không vào (giá trị "biến mất").
     */
    public void fillByLabel(String label, String value) {
        hideKeyboard(); // tránh keyboard che field phía dưới khi scroll
        scrollToLabel(label);
        typeFlutter(M02YourDetailsLocators.fieldAfterLabel(label), value);
    }

    /**
     * Như {@link #fillByLabel} nhưng dùng cho ô text nằm sát ĐÁY form: scroll tới
     * chính label của nó đôi khi để ô input dưới fold (Flutter không expose →
     * không "visible"). Scroll tới {@code lowerAnchor} (một label NẰM DƯỚI field)
     * để kéo field lên vùng giữa màn rồi nhập.
     */
    public void fillByLabelScrollTo(String lowerAnchor, String label, String value) {
        hideKeyboard();
        scrollToLabel(lowerAnchor);
        typeFlutter(M02YourDetailsLocators.fieldAfterLabel(label), value);
    }

    /**
     * Scroll tới label, mở dropdown (ô sau label), gõ {@code query} vào search rồi
     * chọn option ĐẦU TIÊN chứa {@code query}. Dùng cho Consultant/Trade/Qualification/
     * Where/Payroll/Country (bottom-sheet dialog; option là dữ liệu backend).
     */
    public void selectByLabel(String label, String optionQuery) {
        hideKeyboard();
        scrollToLabel(label);
        tap(M02YourDetailsLocators.dropdownAfterLabel(label));
        tapWhenReady(SelectDialogLocators.optionContaining(optionQuery), 40);
        confirmDialogIfOpen();
    }

    /**
     * Đóng dialog "Select" nếu còn mở: dropdown MULTI-SELECT (vd Trade, Qualification)
     * chỉ TÍCH option khi tap, không tự đóng → phải tap "Done". Dropdown single-select
     * tự đóng nên "Done" biến mất → bỏ qua (best-effort, không chờ).
     */
    private void confirmDialogIfOpen() {
        if (isPresentNow(SelectDialogLocators.DONE_BUTTON)) {
            tap(SelectDialogLocators.DONE_BUTTON);
        }
    }

    /**
     * Mở dropdown rồi chọn OPTION ĐẦU TIÊN — dùng khi chỉ cần 1 giá trị hợp lệ
     * bất kỳ (vd Consultant), không phụ thuộc giá trị cụ thể.
     */
    public void selectFirstByLabel(String label) {
        hideKeyboard();
        scrollToLabel(label);
        tap(M02YourDetailsLocators.dropdownAfterLabel(label));
        tapWhenReady(SelectDialogLocators.FIRST_OPTION, 40);
        confirmDialogIfOpen();
    }

    /**
     * Chọn Payroll Company — field CUỐI form nên ô dropdown bị nút "Next →" cố định
     * che khi chỉ scroll tới label. Khắc phục: scroll tới <b>helper text</b> (nằm
     * ngay TRÊN ô dropdown) để ô trồi lên trên Next rồi mới tap.
     */
    public void selectPayrollCompany(String optionQuery) {
        hideKeyboard();
        // Render vùng Payroll (helper "Please select your preferred payroll provider").
        scrollToLabel("Please select your preferred payroll provider");
        // Ô dropdown Payroll là "Please select here". Ở ĐÁY form (các dropdown skip phía
        // trên đã scroll khỏi tầm → Flutter không render) nên đây là "Please select here"
        // DUY NHẤT còn render: scroll hẳn vào view rồi click trực tiếp element trả về
        // (tránh race "node off-screen không vào a11y tree" của tap theo locator).
        gestures.scrollToContentDesc("Please select here").click();
        tapWhenReady(SelectDialogLocators.optionContaining(optionQuery), 40);
        confirmDialogIfOpen();
    }

    // ---- Personal ----
    public void enterFirstName(String value) {
        type(M02YourDetailsLocators.FIRST_NAME_INPUT, value);
    }

    public void enterSurname(String value) {
        type(M02YourDetailsLocators.SURNAME_INPUT, value);
    }

    public void openConsultantDropdown() {
        tap(M02YourDetailsLocators.CONSULTANT_DROPDOWN);
    }

    public void openTradeDropdown() {
        tap(M02YourDetailsLocators.TRADE_DROPDOWN);
    }

    /** Qualification — multi-select dropdown. */
    public void openQualificationDropdown() {
        tap(M02YourDetailsLocators.QUALIFICATION_DROPDOWN);
    }

    public void enterEmail(String value) {
        type(M02YourDetailsLocators.EMAIL_INPUT, value);
    }

    public void enterCandidateMobilePhone(String value) {
        type(M02YourDetailsLocators.CANDIDATE_MOBILE_PHONE_INPUT, value);
    }

    // ---- Address ----
    public void enterAddressLine1(String value) {
        type(M02YourDetailsLocators.ADDRESS_LINE_1_INPUT, value);
    }

    public void enterAddressLine2(String value) {
        type(M02YourDetailsLocators.ADDRESS_LINE_2_INPUT, value);
    }

    public void enterAddressLine3(String value) {
        type(M02YourDetailsLocators.ADDRESS_LINE_3_INPUT, value);
    }

    public void enterTownCity(String value) {
        type(M02YourDetailsLocators.TOWN_CITY_INPUT, value);
    }

    /** Country — tap để mở (có thể là dropdown). */
    public void openCountry() {
        tap(M02YourDetailsLocators.COUNTRY_FIELD);
    }

    public void enterPostcode(String value) {
        type(M02YourDetailsLocators.POSTCODE_INPUT, value);
    }

    public void enterNiNumber(String value) {
        type(M02YourDetailsLocators.NI_NUMBER_INPUT, value);
    }

    /** Sentinel Number — chỉ có ở luồng Rail. */
    public void enterSentinelNumber(String value) {
        type(M02YourDetailsLocators.SENTINEL_NUMBER_INPUT, value);
    }

    public void openDateOfBirthPicker() {
        tap(M02YourDetailsLocators.DOB_PICKER);
    }

    public void openHearAboutUsDropdown() {
        tap(M02YourDetailsLocators.HEAR_ABOUT_US_DROPDOWN);
    }

    // ---- Next to Kin Details ----
    public void enterRelationship(String value) {
        type(M02YourDetailsLocators.RELATIONSHIP_FIELD, value);
    }

    public void enterContactPhone(String value) {
        type(M02YourDetailsLocators.CONTACT_PHONE_INPUT, value);
    }

    // ---- Payroll ----
    public void openPayrollCompanyDropdown() {
        tap(M02YourDetailsLocators.PAYROLL_COMPANY_DROPDOWN);
    }

    // ---- Bottom-sheet "Select" dialog (sau khi mở 1 dropdown) ----
    // Lưu ý: option list là dữ liệu backend — rỗng ("No found") trên sandbox hiện tại.

    /** Trong dialog đang mở: gõ search rồi chọn option theo nhãn. */
    public void searchAndSelectOption(String query, String optionText) {
        type(SelectDialogLocators.SEARCH_INPUT, query);
        tap(SelectDialogLocators.optionByText(optionText));
    }

    /** Chọn option theo nhãn (không search). */
    public void selectOption(String optionText) {
        tap(SelectDialogLocators.optionByText(optionText));
    }

    public void tapDialogDone() {
        tap(SelectDialogLocators.DONE_BUTTON);
    }

    public void dismissDialog() {
        tap(SelectDialogLocators.SCRIM);
    }

    /** Hides IME then taps Next → sang M03 References. */
    public M03ReferencesScreen tapNext() {
        hideKeyboard();
        tap(M02YourDetailsLocators.NEXT_BUTTON);
        return new M03ReferencesScreen(driver);
    }
}
