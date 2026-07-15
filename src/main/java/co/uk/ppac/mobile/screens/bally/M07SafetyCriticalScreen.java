package co.uk.ppac.mobile.screens.bally;

import co.uk.ppac.core.base.BaseScreen;
import co.uk.ppac.mobile.locators.bally.M07SafetyCriticalLocators;
import io.appium.java_client.android.AndroidDriver;

/**
 * Screen Object M07 "Safety Critical Certifications" (chip "Pre-Deployment").
 * Gate Yes/No + 16 cert Yes/No; cert=Yes hiện ô Duration (EditText, đã verify live).
 * Chỉ tham chiếu {@link M07SafetyCriticalLocators}.
 *
 */
public class M07SafetyCriticalScreen extends BaseScreen {

    public M07SafetyCriticalScreen(AndroidDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(M07SafetyCriticalLocators.SCREEN_HEADING);
    }

    /** Gate "Are you subject to any medical restrictions?". */
    public void answerGate(boolean yes) {
        tap(yes ? M07SafetyCriticalLocators.GATE_YES : M07SafetyCriticalLocators.GATE_NO);
    }

    /** Trả lời 1 cert: {@code yes=true} chọn Yes (hiện Duration), ngược lại No. */
    public void answerCert(String cert, boolean yes) {
        tap(yes ? M07SafetyCriticalLocators.certYes(cert)
                : M07SafetyCriticalLocators.certNo(cert));
    }

    /** Nhập Duration (vd "2 years 3 months") cho cert đang = Yes. */
    public void enterDuration(String cert, String duration) {
        type(M07SafetyCriticalLocators.durationInput(cert), duration);
    }

    public boolean isDurationDisplayed(String cert) {
        return isDisplayed(M07SafetyCriticalLocators.durationLabel(cert));
    }

    /** Nhập "Other competencies" (hiện khi cert "Other" = Yes). */
    public void enterOtherCompetencies(String value) {
        type(M07SafetyCriticalLocators.OTHER_COMPETENCIES_INPUT, value);
    }

    public boolean isOtherCompetenciesDisplayed() {
        return isDisplayed(M07SafetyCriticalLocators.OTHER_COMPETENCIES_LABEL);
    }

    /** Hides IME then taps Next → sang M08 Lost & Stolen Sentinel Cards. */
    public M08LostStolenCardsScreen tapNext() {
        hideKeyboard();
        tap(M07SafetyCriticalLocators.NEXT_BUTTON);
        return new M08LostStolenCardsScreen(driver);
    }
}
