package co.uk.ppac.mobile.screens;
import co.uk.ppac.core.base.BaseScreen;

import co.uk.ppac.mobile.locators.WelcomeLocators;
import io.appium.java_client.android.AndroidDriver;

/**
 * Screen Object for the PPAC Sandbox welcome screen - the first screen shown
 * when the app launches. From here the user reaches the email login form via
 * "Log in" -&gt; "Log in with Email".
 *
 * <p>Locators come from a live UI inspection of {@code com.ppac.app.sandbox}
 * v3.1.16 (Flutter app; widgets are exposed through {@code content-desc}).
 * See {@link WelcomeLocators}.
 */
public class WelcomeScreen extends BaseScreen {

    public WelcomeScreen(AndroidDriver driver) {
        super(driver);
    }

    /** Returns true once the welcome screen is displayed. */
    public boolean isLoaded() {
        return isDisplayed(WelcomeLocators.SIGN_UP_WITH_EMAIL_BUTTON);
    }

    /**
     * Toggles the Terms acceptance switch ON. Required before any signup
     * navigation - the app blocks "Sign up with Email" silently when OFF.
     * Returns this for chaining.
     */
    public WelcomeScreen acceptTerms() {
        // Toggle sits left of consent paragraph. Compose custom view - no semantic
        // node. Use viewport-relative tap at (~190, 1395) on a 1080x2400 device,
        // OR find consent paragraph bounds and offset left.
        gestures.tapAt(190, 1395);
        return this;
    }

    /**
     * Navigates to the email signup form. Requires {@link #acceptTerms()} to
     * have been called first - the app silently ignores this tap when terms
     * are OFF. Returns the {@link SignUpByEmailScreen} now on display.
     */
    public SignUpByEmailScreen openEmailSignUp() {
        tap(WelcomeLocators.SIGN_UP_WITH_EMAIL_BUTTON);
        return new SignUpByEmailScreen(driver);
    }

    /**
     * Navigates to the email login form: taps "Log in", then "Log in with
     * Email". Returns the {@link LoginScreen} now on display.
     */
    public LoginScreen openEmailLogin() {
        tap(WelcomeLocators.LOG_IN_LINK);
        tap(WelcomeLocators.LOG_IN_WITH_EMAIL_BUTTON);
        return new LoginScreen(driver);
    }
}
