package co.uk.ppac.mobile.screens.bally;

import co.uk.ppac.core.base.BaseScreen;
import co.uk.ppac.mobile.locators.bally.M05ContractLocators;
import io.appium.java_client.android.AndroidDriver;

/**
 * Screen Object M05 "Contract of Sentinel Scheme Sponsorship".
 * Chỉ tham chiếu {@link M05ContractLocators}.
 *
 * <p>Checkbox acceptance nằm cuối hợp đồng dài — test tự scroll tới trước khi
 * gọi {@link #acceptContract()}.
 *
 */
public class M05ContractScreen extends BaseScreen {

    public M05ContractScreen(AndroidDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(M05ContractLocators.SCREEN_HEADING);
    }

    /** Tick checkbox "I have read, and agree to be bound by…". */
    public void acceptContract() {
        tap(M05ContractLocators.ACCEPT_CHECKBOX);
    }

    /** Hides IME then taps Next → sang M06 PPE. */
    public M06PpeScreen tapNext() {
        hideKeyboard();
        tap(M05ContractLocators.NEXT_BUTTON);
        return new M06PpeScreen(driver);
    }
}
