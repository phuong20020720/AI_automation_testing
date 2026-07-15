package co.uk.ppac.mobile.screens.bally;

import co.uk.ppac.core.base.BaseScreen;
import co.uk.ppac.mobile.locators.bally.M09DeclarationLocators;
import io.appium.java_client.android.AndroidDriver;

/**
 * Screen Object M09 "Declaration" (màn CUỐI, 11 checkbox).
 * Chỉ tham chiếu {@link M09DeclarationLocators}.
 *
 * <p><b>⚠️ {@link #tapSubmit()} thực hiện SUBMIT onboarding thật (nút "Next →").
 * Chỉ gọi trong test chủ đích submit.</b>
 *
 */
public class M09DeclarationScreen extends BaseScreen {

    public static final int DECLARATION_COUNT = M09DeclarationLocators.DECLARATIONS.length;

    public M09DeclarationScreen(AndroidDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(M09DeclarationLocators.SCREEN_HEADING);
    }

    /** Tick mục declaration thứ n (1..11). Test tự scroll tới trước khi gọi. */
    public void tickDeclaration(int n) {
        tap(M09DeclarationLocators.declarationCheckbox(n));
    }

    /** Tick mục consent GDPR (mục 11). */
    public void tickConsentGdpr() {
        tap(M09DeclarationLocators.CONSENT_GDPR);
    }

    public boolean isSubmitEnabled() {
        return wait.waitForVisible(M09DeclarationLocators.SUBMIT_BUTTON).isEnabled();
    }

    /** ⚠️ SUBMIT onboarding (nút "Next →"). Chỉ gọi khi chủ đích submit. */
    public void tapSubmit() {
        hideKeyboard();
        tap(M09DeclarationLocators.SUBMIT_BUTTON);
    }
}
