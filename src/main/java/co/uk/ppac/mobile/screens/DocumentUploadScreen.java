package co.uk.ppac.mobile.screens;
import co.uk.ppac.core.base.BaseScreen;

import co.uk.ppac.mobile.locators.DocumentUploadLocators;
import io.appium.java_client.android.AndroidDriver;

/**
 * Screen Object for onboarding Step 2/3 — Document upload.
 *
 * <p><b>SKELETON</b> — locators and methods are placeholders based on the
 * {@code WELCOME TO} onboarding intro ("Provide your Identity or Right-to-work
 * documents"). This screen has NOT been explored in detail yet because it sits
 * behind Selfie verification which is gated until a mock is in place.
 * Locators: see {@link DocumentUploadLocators}.
 *
 * <p>Once Selfie mock is live (per {@code selfie_mock_design.md}), expand this
 * via live MCP recon following the same pattern as {@link SignUpByEmailScreen}.
 * Expected elements (educated guesses from PPAC web equivalent):
 * <ul>
 *   <li>Title "Identity documents" or "Right to Work"</li>
 *   <li>Document type picker (Passport / Driver License / National ID / etc.)</li>
 *   <li>Front-side capture button</li>
 *   <li>Back-side capture button (if applicable)</li>
 *   <li>"Continue" or "Next" button</li>
 *   <li>Top-right logout icon (consistent across onboarding)</li>
 * </ul>
 */
public class DocumentUploadScreen extends BaseScreen {

    public DocumentUploadScreen(AndroidDriver driver) {
        super(driver);
    }

    /**
     * Returns true when ANY known title candidate is visible. Use as a smoke
     * check after Selfie skip — refine with exact title once recon is done.
     */
    public boolean isLoaded() {
        return isDisplayed(DocumentUploadLocators.SCREEN_TITLE_CANDIDATES);
    }

    public boolean hasSkipOption() {
        return isDisplayed(DocumentUploadLocators.SKIP_BUTTON);
    }

    /** Placeholder — actual flow may require document type selection first. */
    public void tapContinue() {
        tap(DocumentUploadLocators.CONTINUE_BUTTON);
    }

    public LogoutConfirmDialog openLogoutDialog() {
        tap(DocumentUploadLocators.LOGOUT_ICON);
        return new LogoutConfirmDialog(driver);
    }
}
