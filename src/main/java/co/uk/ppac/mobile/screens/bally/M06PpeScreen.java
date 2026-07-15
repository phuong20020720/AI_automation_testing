package co.uk.ppac.mobile.screens.bally;

import co.uk.ppac.core.base.BaseScreen;
import co.uk.ppac.mobile.locators.bally.M06PpeLocators;
import io.appium.java_client.android.AndroidDriver;

/**
 * Screen Object M06 "PPE" (9 item Yes/No).
 * Chỉ tham chiếu {@link M06PpeLocators}.
 *
 */
public class M06PpeScreen extends BaseScreen {

    public M06PpeScreen(AndroidDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(M06PpeLocators.SCREEN_HEADING);
    }

    /** Trả lời 1 item PPE: {@code yes=true} chọn Yes, ngược lại No. */
    public void answerItem(String item, boolean yes) {
        tap(yes ? M06PpeLocators.itemYes(item) : M06PpeLocators.itemNo(item));
    }

    public boolean isItemDisplayed(String item) {
        return isDisplayed(M06PpeLocators.itemLabel(item));
    }

    /** Hides IME then taps Next → sang M07 Safety Critical. */
    public M07SafetyCriticalScreen tapNext() {
        hideKeyboard();
        tap(M06PpeLocators.NEXT_BUTTON);
        return new M07SafetyCriticalScreen(driver);
    }
}
