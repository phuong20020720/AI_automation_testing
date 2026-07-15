package co.uk.ppac.mobile.screens;
import co.uk.ppac.core.base.BaseScreen;

import co.uk.ppac.mobile.locators.ContactPointLocators;
import io.appium.java_client.android.AndroidDriver;

/**
 * Screen Object for step 3 of the New Check flow: "Contact point" — collects
 * a company contact email (Manager / HR / Director) that will receive the
 * Right-to-Work confirmation request.
 *
 * <p>Locators come from a live UI inspection of {@code com.ppac.app.sandbox}
 * v3.1.16. The screen has exactly one {@code EditText}, so a class-only XPath
 * is unique and stable. See {@link ContactPointLocators}.
 */
public class ContactPointScreen extends BaseScreen {

    public ContactPointScreen(AndroidDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(ContactPointLocators.SCREEN_HEADING);
    }

    public boolean isEmailLabelDisplayed() {
        return isDisplayed(ContactPointLocators.EMAIL_LABEL);
    }

    public void enterCompanyContactEmail(String email) {
        type(ContactPointLocators.EMAIL_INPUT, email);
    }

    public void tapContinue() {
        hideKeyboard();
        tap(ContactPointLocators.CONTINUE_BUTTON);
    }
}
