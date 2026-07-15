package co.uk.ppac.mobile.screens.bally;

import co.uk.ppac.core.base.BaseScreen;
import co.uk.ppac.mobile.locators.bally.M03ReferencesLocators;
import co.uk.ppac.mobile.locators.bally.SelectDialogLocators;
import io.appium.java_client.android.AndroidDriver;

/**
 * Screen Object M03 "References" (2 card: Referee 1 & Referee 2).
 * Chỉ tham chiếu {@link M03ReferencesLocators}. {@code refereeIdx} = 1 | 2.
 *
 */
public class M03ReferencesScreen extends BaseScreen {

    public M03ReferencesScreen(AndroidDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(M03ReferencesLocators.SCREEN_HEADING);
    }

    public void openRefereeTypeDropdown(int refereeIdx) {
        tap(M03ReferencesLocators.typeDropdown(refereeIdx));
    }

    /**
     * Mở dropdown Type của referee thứ idx rồi chọn option (vd "Personal referee").
     * Dùng {@code optionContaining} để khớp dù option có text phụ.
     */
    public void selectRefereeType(int refereeIdx, String typeText) {
        gestures.scrollToContentDesc("Please select your referee type");
        openRefereeTypeDropdown(refereeIdx);
        tapWhenReady(SelectDialogLocators.optionContaining(typeText), 40);
        if (isPresentNow(SelectDialogLocators.DONE_BUTTON)) {
            tap(SelectDialogLocators.DONE_BUTTON);
        }
    }

    public void enterRefereeFirstName(int refereeIdx, String value) {
        gestures.scrollToContentDesc("First Name");
        typeFlutter(M03ReferencesLocators.firstNameInput(refereeIdx), value);
    }

    public void enterRefereeSurname(int refereeIdx, String value) {
        typeFlutter(M03ReferencesLocators.surnameInput(refereeIdx), value);
    }

    public void enterRefereeContactNumber(int refereeIdx, String value) {
        typeFlutter(M03ReferencesLocators.contactNumberInput(refereeIdx), value);
    }

    public void enterRefereeRelationship(int refereeIdx, String value) {
        typeFlutter(M03ReferencesLocators.relationshipInput(refereeIdx), value);
    }

    /** Field điều kiện khi Type = Employer referee (đã verify live). */
    public void enterRefereeContractorCompany(int refereeIdx, String value) {
        typeFlutter(M03ReferencesLocators.contractorCompanyInput(refereeIdx), value);
    }

    public void enterRefereeProjectSite(int refereeIdx, String value) {
        typeFlutter(M03ReferencesLocators.projectSiteInput(refereeIdx), value);
    }

    /** Hides IME then taps Next → sang M04 Medical Self-Certification. */
    public M04MedicalSelfCertScreen tapNext() {
        hideKeyboard();
        tap(M03ReferencesLocators.NEXT_BUTTON);
        return new M04MedicalSelfCertScreen(driver);
    }
}
