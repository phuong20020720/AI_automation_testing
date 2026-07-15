package co.uk.ppac.mobile.screens;
import co.uk.ppac.core.base.BaseScreen;

import co.uk.ppac.mobile.locators.LoginLocators;
import io.appium.java_client.android.AndroidDriver;

/**
 * Screen Object for the PPAC Sandbox "Log in with email" form.
 *
 * <p>Locators were derived from a live UI inspection of {@code com.ppac.app.sandbox}
 * v3.1.16. The app is built with <b>Flutter</b>: the accessibility tree exposes
 * most widgets through {@code content-desc}, but the two text fields carry
 * neither a {@code content-desc} nor a {@code resource-id}, so they are located
 * by the {@code password} attribute - the only stable attribute that tells the
 * email field apart from the password field. See {@link LoginLocators}.
 *
 * <p><b>Known limitation:</b> the "incorrect email or password" error banner is
 * NOT present in the accessibility tree (Flutter renders it without a
 * {@code Semantics} node), so it cannot be located by Appium. A rejected login
 * is therefore verified by the form still being displayed ({@link #isLoaded()})
 * rather than by reading the banner text. To assert the banner text, ask the
 * app team to add a {@code Semantics} label, or use the Appium images plugin.
 *
 * <p>Entry point: this form is reached from the welcome screen via
 * "Log in" -&gt; "Log in with Email". That navigation belongs in a separate
 * WelcomeScreen object; this class represents the form itself.
 */
public class LoginScreen extends BaseScreen {

    public LoginScreen(AndroidDriver driver) {
        super(driver);
    }

    /** Returns true once the login form is displayed and ready for input. */
    public boolean isLoaded() {
        return isDisplayed(LoginLocators.SCREEN_HEADING);
    }

    public void enterEmail(String email) {
        type(LoginLocators.EMAIL_INPUT, email);
    }

    public void enterPassword(String password) {
        type(LoginLocators.PASSWORD_INPUT, password);
    }

    public void tapLogin() {
        tap(LoginLocators.LOGIN_BUTTON);
    }

    /** Performs a full login with the given credentials. */
    public void login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        hideKeyboard();
        tapLogin();
    }
}
