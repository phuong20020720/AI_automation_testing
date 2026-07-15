package co.uk.ppac.web.pages;

import co.uk.ppac.core.base.BasePage;
import co.uk.ppac.web.locators.LoginLocators;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends BasePage {

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open(String baseUrl) {
        driver.get(baseUrl);
        waitForFlutterReady();
        return this;
    }

    public LoginPage chooseEmailMethod() {
        wait.until(ExpectedConditions.presenceOfElementLocated(LoginLocators.METHOD_PICKER_EMAIL_BUTTON));
        clickSemanticButton("Sign in with Email");
        wait.until(ExpectedConditions.presenceOfElementLocated(LoginLocators.EMAIL_INPUT));
        return this;
    }

    public boolean isMethodPickerDisplayed() {
        return isPresent(LoginLocators.METHOD_PICKER_EMAIL_BUTTON)
                && isPresent(LoginLocators.METHOD_PICKER_GOOGLE_BUTTON)
                && isPresent(LoginLocators.METHOD_PICKER_MICROSOFT_BUTTON)
                && isPresent(LoginLocators.METHOD_PICKER_CREATE_ACCOUNT_BUTTON);
    }

    public boolean isLanguageToggleDisplayed() {
        return isPresent(LoginLocators.METHOD_PICKER_LANGUAGE_BUTTON);
    }

    public boolean isEmailFormDisplayed() {
        return isPresent(LoginLocators.EMAIL_INPUT)
                && isPresent(LoginLocators.PASSWORD_INPUT)
                && isPresent(LoginLocators.SUBMIT_BUTTON);
    }

    public boolean isForgotPasswordButtonDisplayed() {
        return isPresent(LoginLocators.FORGOT_PASSWORD_BUTTON);
    }

    public LoginPage typeEmail(String email) {
        fillFlutterField(LoginLocators.EMAIL_INPUT, email);
        return this;
    }

    public LoginPage typePassword(String password) {
        fillFlutterField(LoginLocators.PASSWORD_INPUT, password);
        return this;
    }

    public LoginPage submit() {
        clickSemanticButton("Sign In");
        return this;
    }

    public LoginPage clearEmail() {
        WebElement input = driver.findElement(LoginLocators.EMAIL_INPUT);
        js().executeScript("arguments[0].click(); arguments[0].focus();", input);
        try {
            WebElement editor = driver.findElement(FLUTTER_TEXT_EDITING_INPUT);
            editor.clear();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            input.clear();
        }
        return this;
    }

    public boolean isEmptyEmailErrorShown() {
        return isPresent(LoginLocators.EMPTY_EMAIL_ERROR);
    }

    public boolean isEmptyPasswordErrorShown() {
        return isPresent(LoginLocators.EMPTY_PASSWORD_ERROR);
    }

    public boolean isEmailMarkedInvalid() {
        return isPresent(LoginLocators.EMAIL_INVALID_INPUT);
    }

    public boolean isPasswordMarkedInvalid() {
        return isPresent(LoginLocators.PASSWORD_INVALID_INPUT);
    }

    public boolean isWrongPasswordErrorShown() {
        return isPresent(LoginLocators.WRONG_PASSWORD_ERROR);
    }

    public boolean isGenericInvalidCredentialsErrorShown() {
        return isPresent(LoginLocators.GENERIC_AUTH_ERROR_TEXTS);
    }

    public boolean isUserNotFoundErrorShown() {
        return isPresent(LoginLocators.USER_NOT_FOUND_ERROR);
    }

    public String wrongPasswordErrorText() {
        return text(LoginLocators.WRONG_PASSWORD_ERROR);
    }
}
