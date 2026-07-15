package co.uk.ppac.mobile.screens.bally;

import co.uk.ppac.core.base.BaseScreen;
import co.uk.ppac.mobile.locators.bally.M04MedicalSelfCertLocators;
import io.appium.java_client.android.AndroidDriver;

/**
 * Screen Object M04 "Medical Self-Certification" (12 câu Yes/No + confirmation
 * checkbox khi all=NO). Chỉ tham chiếu {@link M04MedicalSelfCertLocators}.
 *
 * <p>{@code answerQuestion} giả định câu hỏi N đang hiển thị (test tự scroll tới
 * trước khi gọi — màn dài hơn 1 trang).
 *
 */
public class M04MedicalSelfCertScreen extends BaseScreen {

    public static final int QUESTION_COUNT = M04MedicalSelfCertLocators.QUESTIONS.length;

    public M04MedicalSelfCertScreen(AndroidDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(M04MedicalSelfCertLocators.SCREEN_HEADING);
    }

    /** Trả lời câu N (1..12): {@code yes=true} chọn Yes, ngược lại No. */
    public void answerQuestion(int n, boolean yes) {
        tap(yes ? M04MedicalSelfCertLocators.yesRadio(n)
                : M04MedicalSelfCertLocators.noRadio(n));
    }

    public boolean isQuestionDisplayed(int n) {
        return isDisplayed(M04MedicalSelfCertLocators.questionLabel(n));
    }

    /** Tick confirmation checkbox (chỉ hiện khi đã chọn NO cho cả 12 câu). */
    public void tickConfirmNoCheckbox() {
        tap(M04MedicalSelfCertLocators.CONFIRM_NO_CHECKBOX);
    }

    public boolean isConfirmNoCheckboxDisplayed() {
        return isDisplayed(M04MedicalSelfCertLocators.CONFIRM_NO_CHECKBOX);
    }

    /** Hides IME then taps Next → sang M05 Contract. */
    public M05ContractScreen tapNext() {
        hideKeyboard();
        tap(M04MedicalSelfCertLocators.NEXT_BUTTON);
        return new M05ContractScreen(driver);
    }
}
