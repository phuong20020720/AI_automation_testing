package co.uk.ppac.mobile.screens;
import co.uk.ppac.core.base.BaseScreen;

import co.uk.ppac.mobile.locators.SelfieInstructionLocators;
import co.uk.ppac.core.utils.SelfieMockHelper;
import io.appium.java_client.android.AndroidDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Screen Object for onboarding Step 1/3 — "Selfie time!" instructions.
 *
 * <p>Pre-camera instructions screen: 3 prerequisite bullets (Good lighting,
 * No safety gear, Camera at eye level) + 2 example images (bad/good) + "Open
 * Camera" CTA. The live camera screen that opens after this CANNOT be
 * automated on Android emulator — face detection requires real face.
 * Locators: see {@link SelfieInstructionLocators}.
 *
 * <p>See F-DOC-3 in {@code sign_up_email_exploration.md} and full strategy in
 * {@code plans/automation/ppac_mobile/selfie_mock_design.md}.
 */
public class SelfieInstructionScreen extends BaseScreen {

    private static final Logger LOGGER = LoggerFactory.getLogger(SelfieInstructionScreen.class);

    // Debug-only skip UI exposed by app debug build with BuildConfig.DEBUG_SKIP_SELFIE=true.
    // Reveal gesture: triple-tap top-left ppac logo area.
    private static final int LOGO_TAP_X = 108;
    private static final int LOGO_TAP_Y = 200;

    public SelfieInstructionScreen(AndroidDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(SelfieInstructionLocators.SCREEN_TITLE);
    }

    /**
     * Taps "Open Camera". On real device → navigate to Selfie Camera Live.
     * On emulator → camera permission may auto-grant, then live camera shows
     * fake test pattern (cannot detect face → flow blocked).
     */
    public void tapOpenCamera() {
        tap(SelfieInstructionLocators.OPEN_CAMERA_BUTTON);
    }

    /**
     * Bypasses Selfie verification using whichever mock strategy is configured.
     * Returns {@link DocumentUploadScreen} (next onboarding step) on success.
     *
     * <p>Strategies (priority order):
     * <ol>
     *   <li><b>DEBUG_SKIP_BUTTON</b> — triple-tap logo to reveal debug skip UI,
     *       then tap. Requires app built with {@code DEBUG_SKIP_SELFIE=true}.</li>
     *   <li><b>BACKEND_FEATURE_FLAG</b> — tap Open Camera, then let backend
     *       mock service auto-accept the captured frame (fake camera pattern).
     *       Requires server with {@code PPAC_SELFIE_MOCK_ENABLED=true} and
     *       user email matching the whitelist pattern.</li>
     *   <li><b>NONE</b> — throw {@link UnsupportedOperationException}. Test
     *       should be skipped at fixture level via
     *       {@link SelfieMockHelper#isAnyMockAvailable()}.</li>
     * </ol>
     */
    public DocumentUploadScreen skipSelfieForTest() {
        SelfieMockHelper.Strategy strategy = SelfieMockHelper.activeStrategy();
        LOGGER.info("Selfie skip via strategy: {}", strategy);
        switch (strategy) {
            case DEBUG_SKIP_BUTTON -> {
                // Reveal hidden skip UI via triple-tap on ppac logo
                gestures.tapAt(LOGO_TAP_X, LOGO_TAP_Y);
                gestures.tapAt(LOGO_TAP_X, LOGO_TAP_Y);
                gestures.tapAt(LOGO_TAP_X, LOGO_TAP_Y);
                tap(SelfieInstructionLocators.DEBUG_SKIP_BUTTON);
                return new DocumentUploadScreen(driver);
            }
            case BACKEND_FEATURE_FLAG -> {
                // Backend will auto-accept the emulator camera capture
                tap(SelfieInstructionLocators.OPEN_CAMERA_BUTTON);
                // Wait for backend mock response + navigate to next step
                // (Document upload screen identified by its title)
                return new DocumentUploadScreen(driver);
            }
            case NONE -> throw new UnsupportedOperationException(
                    "No Selfie mock strategy configured. Set 'selfie.mock.debug.skip.enabled=true' OR "
                            + "'selfie.mock.backend.enabled=true' (with matching whitelist email) "
                            + "to enable. See plans/automation/ppac_mobile/selfie_mock_design.md.");
            default -> throw new IllegalStateException("Unknown Selfie mock strategy: " + strategy);
        }
    }

    /** Returns to {@link WelcomeToScreen}. */
    public WelcomeToScreen tapBack() {
        tap(SelfieInstructionLocators.BACK_ARROW);
        return new WelcomeToScreen(driver);
    }

    public LogoutConfirmDialog openLogoutDialog() {
        tap(SelfieInstructionLocators.LOGOUT_ICON);
        return new LogoutConfirmDialog(driver);
    }
}
