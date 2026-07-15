package co.uk.ppac.mobile.screens;
import co.uk.ppac.core.base.BaseScreen;

import co.uk.ppac.mobile.locators.CreatePasswordLocators;
import io.appium.java_client.android.AndroidDriver;

import java.util.List;

/**
 * Screen Object for Sign Up Step 3 — "Create password".
 *
 * <p>Two EditText fields (Password + Confirm) — Flutter masks both as
 * {@code password='true'}. The 5 password rules are Compose Text nodes that
 * update real-time (gray ⊘ → green ✅) — their {@code content-desc} stays the
 * same; the indicator icon's class/state is what changes. See
 * {@link CreatePasswordLocators}.
 *
 * <p>Recon 2026-05-26: Password = first password EditText, Confirm = second.
 */
public class CreatePasswordScreen extends BaseScreen {

    public static final List<String> EXPECTED_RULES = List.of(
            "at least 8 characters",
            "uppercase letter (A-Z)",
            "lowercase letter (a-z)",
            "number (0-9)",
            "special character (-@#$%*&_+=,?/!)"
    );

    public CreatePasswordScreen(AndroidDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(CreatePasswordLocators.SCREEN_HEADING);
    }

    public CreatePasswordScreen enterPassword(String password) {
        type(CreatePasswordLocators.PASSWORD_INPUT, password);
        return this;
    }

    public CreatePasswordScreen enterConfirmPassword(String password) {
        type(CreatePasswordLocators.CONFIRM_INPUT, password);
        return this;
    }

    /**
     * Returns true if the named rule is satisfied (green check). Rule lookup is
     * by exact content-desc match against {@link #EXPECTED_RULES}.
     *
     * <p>Note: Compose may render satisfied rules with a different sibling icon
     * node — caller may need to inspect siblings rather than the rule text node
     * itself. This method currently returns {@code true} if the rule text is
     * present at all; refine when rule satisfied-state attribute is identified.
     */
    public boolean isRuleSatisfied(String rule) {
        return isDisplayed(CreatePasswordLocators.rule(rule));
    }

    /**
     * Taps Create account. On success → navigate to OTP screen. On server
     * failure (email rejected) → navigate back to Email screen with error.
     * Caller must verify which screen is now displayed.
     */
    public CheckYourInboxScreen tapCreateAccount() {
        hideKeyboard();
        tap(CreatePasswordLocators.CREATE_ACCOUNT_BUTTON);
        return new CheckYourInboxScreen(driver);
    }

    /** Convenience: fill both fields and submit. */
    public CheckYourInboxScreen createAccount(String password) {
        enterPassword(password);
        enterConfirmPassword(password);
        return tapCreateAccount();
    }
}
