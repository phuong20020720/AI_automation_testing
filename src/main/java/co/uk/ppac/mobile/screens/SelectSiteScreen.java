package co.uk.ppac.mobile.screens;
import co.uk.ppac.core.base.BaseScreen;

import co.uk.ppac.mobile.locators.SelectSiteLocators;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Screen Object for the second step of the New Check flow: selecting a site
 * location and subcontractor for the contractor chosen at step 1.
 *
 * <p>Locators come from a live UI inspection of {@code com.ppac.app.sandbox}
 * v3.1.16. The site and subcontractor fields are each rendered as a clickable
 * {@code ImageView} carrying the same {@code content-desc} as the heading
 * above it (e.g. "Select site location"). Picking opens a modal bottom sheet
 * whose options are clickable {@code View}s addressed by their visible name.
 * See {@link SelectSiteLocators}.
 */
public class SelectSiteScreen extends BaseScreen {

    public SelectSiteScreen(AndroidDriver driver) {
        super(driver);
    }

    /** Returns true once the site/subcontractor selection step is displayed. */
    public boolean isLoaded() {
        return isDisplayed(SelectSiteLocators.SITE_LOCATION_HEADING);
    }

    /**
     * Opens the site-location modal picker and taps the option with the given
     * visible name. The picker is a bottom-sheet of clickable Views; each
     * option's {@code content-desc} matches its display text.
     */
    public void selectSiteLocation(String siteName) {
        tap(SelectSiteLocators.SITE_LOCATION_FIELD);
        tap(SelectSiteLocators.pickerOption(siteName));
    }

    /**
     * Opens the subcontractor modal picker and taps the option with the given
     * visible name.
     */
    public void selectSubcontractor(String subcontractorName) {
        tap(SelectSiteLocators.SUBCONTRACTOR_FIELD);
        tap(SelectSiteLocators.pickerOption(subcontractorName));
    }

    /**
     * Opens the site-location picker and taps the first option in the list,
     * returning the visible name chosen (so the test can assert and log it).
     */
    public String selectFirstSiteLocation() {
        tap(SelectSiteLocators.SITE_LOCATION_FIELD);
        return tapFirstPickerOption();
    }

    /**
     * Opens the subcontractor picker and taps the first option in the list,
     * returning the visible name chosen.
     */
    public String selectFirstSubcontractor() {
        tap(SelectSiteLocators.SUBCONTRACTOR_FIELD);
        return tapFirstPickerOption();
    }

    /** Taps the first option in the open bottom-sheet picker and returns its name. */
    private String tapFirstPickerOption() {
        List<WebElement> options = wait.waitForAllVisible(SelectSiteLocators.PICKER_OPTIONS);
        WebElement first = options.get(0);
        String name = first.getAttribute("content-desc");
        first.click();
        return name;
    }

    /**
     * Returns true if the site field currently displays the given name. Once
     * a value is picked the field's content-desc becomes that value, so we
     * locate by the chosen name instead of the placeholder.
     */
    public boolean isSiteSelected(String siteName) {
        return isDisplayed(SelectSiteLocators.selectedField(siteName));
    }

    public boolean isSubcontractorSelected(String subcontractorName) {
        return isDisplayed(SelectSiteLocators.selectedField(subcontractorName));
    }

    public void toggleCantFindSubcontractor() {
        tap(SelectSiteLocators.CANT_FIND_SUBCONTRACTOR_CHECKBOX);
    }

    public void tapContinue() {
        tap(SelectSiteLocators.CONTINUE_BUTTON);
    }
}
