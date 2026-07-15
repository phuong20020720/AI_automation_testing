package co.uk.ppac.mobile.screens;
import co.uk.ppac.core.base.BaseScreen;

import co.uk.ppac.mobile.locators.LogoutConfirmLocators;
import io.appium.java_client.android.AndroidDriver;

/**
 * Modal dialog Object for "Confirm Log Out".
 *
 * <p>Triggered by the top-right logout icon on any onboarding screen
 * ({@link WelcomeToScreen}, {@link SelfieInstructionScreen}, etc.). Has a
 * warning paragraph + two buttons: "Log Out" (destructive primary) and
 * "Cancel" (outline). Locators: see {@link LogoutConfirmLocators}.
 */
public class LogoutConfirmDialog extends BaseScreen {

    public LogoutConfirmDialog(AndroidDriver driver) {
        super(driver);
    }

    public boolean isOpen() {
        return isDisplayed(LogoutConfirmLocators.DIALOG_TITLE);
    }

    /** Confirms logout → session cleared, navigate to Welcome screen fresh. */
    public WelcomeScreen tapLogOut() {
        tap(LogoutConfirmLocators.LOG_OUT_BUTTON);
        return new WelcomeScreen(driver);
    }

    /** Dismisses dialog, returns null — caller should keep reference to prior screen. */
    public void tapCancel() {
        tap(LogoutConfirmLocators.CANCEL_BUTTON);
    }
}
