package co.uk.ppac.mobile.screens;
import co.uk.ppac.core.base.BaseScreen;

import co.uk.ppac.mobile.locators.SkillCardLocators;
import io.appium.java_client.android.AndroidDriver;

/**
 * Screen Object for step 4 of the New Check flow: "Skill card verification".
 *
 * <p>Only the {@link #isLoaded()} assertion is wired up today — completing
 * this step requires either uploading a real skill card image (file picker)
 * or filling registration number + surname AND uploading the document. Both
 * paths sit beyond the scope of these tests, which assert progression up to
 * and including the screen's appearance. Locators: see {@link SkillCardLocators}.
 */
public class SkillCardScreen extends BaseScreen {

    public SkillCardScreen(AndroidDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(SkillCardLocators.SCREEN_HEADING);
    }
}
