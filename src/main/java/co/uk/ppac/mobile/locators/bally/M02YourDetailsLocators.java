package co.uk.ppac.mobile.locators.bally;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * M02 "Your Details" (BALLYCOMMON-RAIL onboarding,
 * bước ngay sau M01 prefix).
 *
 * <p>Trích THẬT từ {@code appium_get_page_source} của {@code com.ppac.app.sandbox}
 * trên emulator-5554 (Pixel7, Android 13), ngày 2026-06-15. App Flutter:
 * <ul>
 *   <li><b>Label</b> lộ qua {@code content-desc} (= accessibility id) và là UNIQUE.</li>
 *   <li><b>Ô input</b> là {@code android.view.View} mang {@code text=&lt;giá trị&gt;},
 *       KHÔNG có {@code content-desc}/{@code resource-id}.</li>
 *   <li><b>Dropdown</b> là {@code android.view.View clickable=true} với
 *       {@code content-desc="Please select here"} — DÙNG CHUNG cho mọi dropdown
 *       (Consultant/Trade/Where did you hear/Payroll) ⇒ KHÔNG unique.</li>
 * </ul>
 *
 * <p>Trong {@code ScrollView}, label và field của nó là <b>sibling liền kề</b>
 * (field đứng ngay sau label). Vì vậy ô input & dropdown được định vị bằng
 * <b>following-sibling neo trên label</b> — relative xpath ổn định (KHÔNG dùng
 * xpath tuyệt đối theo index, không {@code Thread.sleep}).
 *
 */
public final class M02YourDetailsLocators {

    private M02YourDetailsLocators() {
    }

    // ---- Stepper (HorizontalScrollView, accessibility id unique) ----
    public static final By STEP_ONBOARDING_DETAILS = AppiumBy.accessibilityId("Onboarding Details");
    public static final By STEP_REFERENCES         = AppiumBy.accessibilityId("References");
    public static final By STEP_MEDICAL            = AppiumBy.accessibilityId("Medical Self-Certification");

    // ---- Heading & section headers (accessibility id) ----
    public static final By SCREEN_HEADING     = AppiumBy.accessibilityId("Your Details");
    /** App text thật là "Next to Kin Details" (giữ nguyên — nghi typo cho "Next of Kin"). */
    public static final By SECTION_NEXT_OF_KIN = AppiumBy.accessibilityId("Next to Kin Details");

    // ---- Labels (accessibility id, đều unique). Re-verify 2026-06-16: ĐỦ field. ----
    // Thứ tự thật: First name, Surname, Consultant, Trade, Qualification, Email,
    // Candidate's Mobile Phone, Address Line 1/2/3, Town/City, Country, Postcode,
    // National Insurance Number, Sentinel Number, Date of birth,
    // Where did you hear about us?, [Next to Kin] Relationship, Contact Phone, Payroll.
    public static final By LBL_FIRST_NAME               = AppiumBy.accessibilityId("First name");
    public static final By LBL_SURNAME                  = AppiumBy.accessibilityId("Surname");
    public static final By LBL_CONSULTANT               = AppiumBy.accessibilityId("Consultant");
    public static final By LBL_TRADE                    = AppiumBy.accessibilityId("Trade");
    public static final By LBL_QUALIFICATION            = AppiumBy.accessibilityId("Qualification");
    public static final By LBL_EMAIL                    = AppiumBy.accessibilityId("Email");
    public static final By LBL_CANDIDATE_MOBILE_PHONE   = AppiumBy.accessibilityId("Candidate's Mobile Phone");
    public static final By LBL_ADDRESS_LINE_1           = AppiumBy.accessibilityId("Address Line 1");
    public static final By LBL_ADDRESS_LINE_2           = AppiumBy.accessibilityId("Address Line 2");
    public static final By LBL_ADDRESS_LINE_3           = AppiumBy.accessibilityId("Address Line 3");
    public static final By LBL_TOWN_CITY                = AppiumBy.accessibilityId("Town / City");
    public static final By LBL_COUNTRY                  = AppiumBy.accessibilityId("Country");
    public static final By LBL_POSTCODE                 = AppiumBy.accessibilityId("Postcode");
    public static final By LBL_NI_NUMBER                = AppiumBy.accessibilityId("National Insurance Number");
    public static final By LBL_SENTINEL_NUMBER          = AppiumBy.accessibilityId("Sentinel Number"); // Rail-specific
    public static final By LBL_DATE_OF_BIRTH            = AppiumBy.accessibilityId("Date of birth");
    public static final By LBL_HEAR_ABOUT_US            = AppiumBy.accessibilityId("Where did you hear about us?");
    public static final By LBL_RELATIONSHIP_TO_CANDIDATE = AppiumBy.accessibilityId("Relationship to Candidate");
    public static final By LBL_CONTACT_PHONE_NUMBER     = AppiumBy.accessibilityId("Contact Phone Number");
    public static final By LBL_PAYROLL_COMPANY          = AppiumBy.accessibilityId("Payroll Company");

    /** Helper dưới Payroll Company. */
    public static final By PAYROLL_HELP_TEXT =
            AppiumBy.accessibilityId("Please select your preferred payroll provider");

    // ---- Field (input/dropdown/picker) neo theo label = sibling kế tiếp ----
    // Input có giá trị (First name="Kiều Phương", Surname="Đo"); các ô trống không
    // hiện text. Dropdown hiển thị "Please select here"; DOB hiển thị "20/07/2002".

    public static final By FIRST_NAME_INPUT      = fieldAfterLabel("First name");
    public static final By SURNAME_INPUT         = fieldAfterLabel("Surname");
    public static final By CONSULTANT_DROPDOWN   = fieldAfterLabel("Consultant");
    public static final By TRADE_DROPDOWN        = fieldAfterLabel("Trade");
    public static final By QUALIFICATION_DROPDOWN = fieldAfterLabel("Qualification"); // multi-select
    public static final By EMAIL_INPUT           = fieldAfterLabel("Email");
    public static final By CANDIDATE_MOBILE_PHONE_INPUT = fieldAfterLabel("Candidate's Mobile Phone");
    public static final By ADDRESS_LINE_1_INPUT  = fieldAfterLabel("Address Line 1");
    public static final By ADDRESS_LINE_2_INPUT  = fieldAfterLabel("Address Line 2");
    public static final By ADDRESS_LINE_3_INPUT  = fieldAfterLabel("Address Line 3");
    public static final By TOWN_CITY_INPUT       = fieldAfterLabel("Town / City");
    public static final By COUNTRY_FIELD         = fieldAfterLabel("Country");
    public static final By POSTCODE_INPUT        = fieldAfterLabel("Postcode");
    public static final By NI_NUMBER_INPUT       = fieldAfterLabel("National Insurance Number");
    public static final By SENTINEL_NUMBER_INPUT = fieldAfterLabel("Sentinel Number");
    public static final By DOB_PICKER            = fieldAfterLabel("Date of birth");
    public static final By HEAR_ABOUT_US_DROPDOWN = fieldAfterLabel("Where did you hear about us?");
    public static final By RELATIONSHIP_FIELD    = fieldAfterLabel("Relationship to Candidate");
    public static final By CONTACT_PHONE_INPUT   = fieldAfterLabel("Contact Phone Number");
    public static final By PAYROLL_COMPANY_DROPDOWN = fieldAfterLabel("Payroll Company");

    // ---- Nút ----
    public static final By NEXT_BUTTON = AppiumBy.accessibilityId("Next →");

    /**
     * Field (input/dropdown/picker) đứng NGAY SAU label cùng tên. Neo trên
     * content-desc của label (ổn định) thay vì index tuyệt đối.
     */
    public static By fieldAfterLabel(String label) {
        return AppiumBy.xpath(
                "//android.view.View[@content-desc=\"" + label + "\"]"
                        + "/following-sibling::android.view.View[1]");
    }

    /**
     * Phần tử mở DROPDOWN = sibling CLICKABLE đầu tiên sau label. Bền hơn
     * {@link #fieldAfterLabel(String)} vì một số dropdown (vd Payroll Company) có
     * helper text (clickable=false) CHÈN GIỮA label và ô dropdown.
     */
    public static By dropdownAfterLabel(String label) {
        return AppiumBy.xpath(
                "//android.view.View[@content-desc=\"" + label + "\"]"
                        + "/following-sibling::android.view.View[@clickable=\"true\"][1]");
    }

    /**
     * Ô dropdown ("Please select here") đứng sau label trong DOCUMENT ORDER — KHÔNG
     * yêu cầu là sibling. Dùng cho dropdown bị tách container bởi helper text (vd
     * Payroll Company: label → helper → [container khác] → ô dropdown). {@code following::}
     * lấy ô "Please select here" clickable đầu tiên sau label đó.
     */
    public static By dropdownFollowingLabel(String label) {
        return AppiumBy.xpath(
                "//android.view.View[@content-desc=\"" + label + "\"]"
                        + "/following::android.view.View[@content-desc=\"Please select here\""
                        + " and @clickable=\"true\"][1]");
    }

    /** Chip bước onboarding theo tên (Onboarding Details / References / Medical Self-Certification). */
    public static By stepByName(String name) {
        return AppiumBy.accessibilityId(name);
    }
}
