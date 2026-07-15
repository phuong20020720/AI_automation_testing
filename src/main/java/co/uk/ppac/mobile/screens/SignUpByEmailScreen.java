package co.uk.ppac.mobile.screens;
import co.uk.ppac.core.base.BaseScreen;

import co.uk.ppac.mobile.locators.SignUpByEmailLocators;
import io.appium.java_client.android.AndroidDriver;

/**
 * Screen Object for Sign Up Step 2 — "Sign up by email".
 *
 * <p>Locators from MCP recon 2026-05-26 on {@code com.ppac.app.sandbox} v3.1.16
 * (Flutter / Compose UI). Single email EditText; Next button is full-width
 * bottom; validation error appears below the input as a separate Text node.
 * See {@link SignUpByEmailLocators}.
 *
 * <p>Note: error text "Please enter a valid email" is used by BOTH client format
 * validation AND server domain rejection (e.g., {@code @test.com}) — see
 * F-UX-NEW-1 in {@code plans/manual/ppac_mobile/sign_up_email_exploration.md}.
 */
public class SignUpByEmailScreen extends BaseScreen {

    public SignUpByEmailScreen(AndroidDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(SignUpByEmailLocators.SCREEN_HEADING);
    }

    public SignUpByEmailScreen enterEmail(String email) {
        type(SignUpByEmailLocators.EMAIL_INPUT, email);
        return this;
    }

    /** Returns true if the inline validation error text is visible. */
    public boolean hasValidationError() {
        return isDisplayed(SignUpByEmailLocators.VALIDATION_ERROR);
    }

    /**
     * Taps Next. If validation passes, the app navigates to Create password.
     * Caller must verify which screen is now displayed (this method does not
     * decide for the caller, since validation outcome depends on email value).
     */
    public CreatePasswordScreen tapNextExpectingPassword() {
        hideKeyboard();
        tap(SignUpByEmailLocators.NEXT_BUTTON);
        return new CreatePasswordScreen(driver);
    }

    /** Taps Next and stays on this screen (used for negative validation tests). */
    public SignUpByEmailScreen tapNextExpectingError() {
        hideKeyboard();
        tap(SignUpByEmailLocators.NEXT_BUTTON);
        return this;
    }
}
