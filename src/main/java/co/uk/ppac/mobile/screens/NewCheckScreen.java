package co.uk.ppac.mobile.screens;
import co.uk.ppac.core.base.BaseScreen;

import co.uk.ppac.mobile.locators.NewCheckLocators;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;

/**
 * Screen Object for the PPAC Sandbox "New Check" tab - specifically the first
 * step "Enter company prefix" where the user types a contractor/site prefix
 * (e.g. {@code macec} for Mace Construct) before the rest of the flow.
 *
 * <p>Locators come from a live UI inspection of {@code com.ppac.app.sandbox}
 * v3.1.16. As is typical for this Flutter app, the static labels expose a
 * {@code content-desc} we can match by accessibility id, but the prefix
 * {@code EditText} carries neither {@code content-desc} nor {@code resource-id}
 * - and is the only EditText on the screen, so a plain class XPath is unique
 * and stable. See {@link NewCheckLocators}.
 */
public class NewCheckScreen extends BaseScreen {

    public NewCheckScreen(AndroidDriver driver) {
        super(driver);
    }

    /** Returns true once the prefix-entry step is displayed. */
    public boolean isLoaded() {
        return isDisplayed(NewCheckLocators.SCREEN_HEADING);
    }

    public boolean isHelpTextDisplayed() {
        return isDisplayed(NewCheckLocators.HELP_TEXT);
    }

    /** Tiêu đề động "Welcome to PPAC, &lt;User&gt;!". */
    public boolean isWelcomeTitleDisplayed() {
        return isDisplayed(NewCheckLocators.WELCOME_TITLE);
    }

    /** Helper (icon ?) "If you're unsure, ask your on-site manager." */
    public boolean isHintTextDisplayed() {
        return isDisplayed(NewCheckLocators.HINT_TEXT);
    }

    public boolean isContinueDisplayed() {
        return isDisplayed(NewCheckLocators.CONTINUE_BUTTON);
    }

    /** Prefix BALLYCOMMON (gõ đủ "BALLY" mới hiện gợi ý Construction/Rail). */
    public static final String BALLYCOMMON_PREFIX = "BALLY";

    /** Focuses the prefix field, clears any existing text, then types the prefix. */
    public void enterPrefix(String prefix) {
        type(NewCheckLocators.PREFIX_INPUT, prefix);
    }

    /**
     * Gõ prefix vào ô company prefix bằng key events thật ({@code sendKeys}) để
     * kích hoạt autocomplete của Flutter. Dùng {@link #BALLYCOMMON_PREFIX} ("BALLY")
     * để hiện dropdown gợi ý BALLYCOMMON. Trả về {@code this} để chain.
     */
    public NewCheckScreen typePrefix(String prefix) {
        enterPrefix(prefix);
        return this;
    }

    /** Returns the current value of the prefix field. */
    public String getEnteredPrefix() {
        WebElement field = wait.waitForVisible(NewCheckLocators.PREFIX_INPUT);
        String value = field.getText();
        return value == null ? "" : value;
    }

    /**
     * Hides the IME (so it cannot occlude the button) and taps Continue. Use
     * after {@link #enterPrefix(String)}.
     */
    public void tapContinue() {
        hideKeyboard();
        tap(NewCheckLocators.CONTINUE_BUTTON);
    }

    /**
     * Returns true when the contractor suggestion matching the given prefix is
     * visible. The dropdown only appears after the user types a prefix that
     * resolves to a registered contractor; absence of the suggestion is itself
     * a signal that the prefix is unrecognised.
     */
    public boolean isContractorSuggestionDisplayed(String prefix) {
        return isDisplayed(NewCheckLocators.contractorSuggestion(prefix));
    }

    /**
     * Selects a contractor from the suggestion dropdown for the given prefix.
     * Required step before Continue activates: typing alone does not commit
     * the contractor selection.
     */
    public void selectContractorSuggestion(String prefix) {
        tap(NewCheckLocators.contractorSuggestion(prefix));
    }

    // ----------------------- BALLYCOMMON-RAIL -----------------------

    /** True khi gợi ý "Ballycommon - bally - Rail" hiển thị (sau khi gõ "BALLY"). */
    public boolean isBallycommonRailDisplayed() {
        return isDisplayed(NewCheckLocators.SUGGESTION_BALLYCOMMON_RAIL);
    }

    /** True khi gợi ý "Ballycommon - bally - Construction" hiển thị. */
    public boolean isBallycommonConstructionDisplayed() {
        return isDisplayed(NewCheckLocators.SUGGESTION_BALLYCOMMON_CONSTRUCTION);
    }

    /**
     * Chọn gợi ý "Ballycommon - bally - Rail". Yêu cầu đã gõ "BALLY" trước
     * (xem {@link #typePrefix(String)}) và dropdown đang hiển thị.
     */
    public void selectBallycommonRail() {
        tap(NewCheckLocators.SUGGESTION_BALLYCOMMON_RAIL);
    }

    /** Chọn gợi ý "Ballycommon - bally - Construction". */
    public void selectBallycommonConstruction() {
        tap(NewCheckLocators.SUGGESTION_BALLYCOMMON_CONSTRUCTION);
    }

    /**
     * Luồng đầy đủ M01 cho Rail: gõ "BALLY" → chọn "Ballycommon - bally - Rail"
     * → tap Continue (sang M02 Your Details).
     */
    public void enterBallycommonRailAndContinue() {
        typePrefix(BALLYCOMMON_PREFIX);
        selectBallycommonRail();
        tapContinue();
    }

    // -------------------- Popup "Continue your onboarding?" --------------------

    /** True khi popup "Continue your onboarding?" hiển thị (có onboarding dở dang). */
    public boolean isContinueOnboardingPopupDisplayed() {
        return isDisplayed(NewCheckLocators.CONTINUE_ONBOARDING_POPUP);
    }

    /** Tiếp tục onboarding đang dở (nút "Continue" trong popup). */
    public void continueExistingOnboarding() {
        tap(NewCheckLocators.POPUP_CONTINUE_BUTTON);
    }

    /** Bắt đầu onboarding mới — huỷ cái dở dang (TC_004). */
    public void startNewOnboarding() {
        tap(NewCheckLocators.POPUP_START_NEW_BUTTON);
    }
}
