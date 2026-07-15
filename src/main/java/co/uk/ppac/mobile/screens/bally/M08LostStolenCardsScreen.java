package co.uk.ppac.mobile.screens.bally;

import co.uk.ppac.core.base.BaseScreen;
import co.uk.ppac.mobile.locators.bally.M08LostStolenCardsLocators;
import io.appium.java_client.android.AndroidDriver;

/**
 * Screen Object M08 "Lost & Stolen Sentinel Cards" (chip "Lost Sentinel Cards").
 * Chỉ tham chiếu {@link M08LostStolenCardsLocators}.
 *
 */
public class M08LostStolenCardsScreen extends BaseScreen {

    public M08LostStolenCardsScreen(AndroidDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(M08LostStolenCardsLocators.SCREEN_HEADING);
    }

    /** Tick checkbox xác nhận "I confirm that I will pay £25 + VAT…". */
    public void confirm() {
        tap(M08LostStolenCardsLocators.CONFIRM_CHECKBOX);
    }

    /** Hides IME then taps Next → sang M09 Declaration. */
    public M09DeclarationScreen tapNext() {
        hideKeyboard();
        tap(M08LostStolenCardsLocators.NEXT_BUTTON);
        return new M09DeclarationScreen(driver);
    }
}
