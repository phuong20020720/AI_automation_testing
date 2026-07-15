package co.uk.ppac.mobile.screens;
import co.uk.ppac.core.base.BaseScreen;

import co.uk.ppac.mobile.locators.WelcomeToLocators;
import io.appium.java_client.android.AndroidDriver;

/**
 * Screen Object for post-signup Step 5 — "WELCOME TO" onboarding intro.
 *
 * <p>Shown immediately after a successful OTP validate. Lists the 3-step
 * onboarding flow (Selfie → Documents → Skill card) with a single "Let's start"
 * call-to-action. No back arrow — this is terminal after OTP commit.
 *
 * <p>Top-right logout icon is present (see {@link LogoutConfirmDialog}).
 * Locators: see {@link WelcomeToLocators}.
 */
public class WelcomeToScreen extends BaseScreen {

    public WelcomeToScreen(AndroidDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(WelcomeToLocators.TITLE_WELCOME_TO);
    }

    public boolean hasSecureIdentitySubtitle() {
        return isDisplayed(WelcomeToLocators.SUBTITLE_IDENTITY);
    }

    /** Taps "Let's start" → navigate to Selfie instruction screen. */
    public SelfieInstructionScreen tapLetsStart() {
        tap(WelcomeToLocators.LETS_START_BUTTON);
        return new SelfieInstructionScreen(driver);
    }

    /** Opens the Confirm Log Out dialog by tapping top-right logout icon. */
    public LogoutConfirmDialog openLogoutDialog() {
        tap(WelcomeToLocators.LOGOUT_ICON);
        return new LogoutConfirmDialog(driver);
    }
}
