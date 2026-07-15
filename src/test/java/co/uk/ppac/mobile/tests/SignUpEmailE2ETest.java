package co.uk.ppac.mobile.tests;

import co.uk.ppac.core.base.MobileBaseTest;
import co.uk.ppac.mobile.screens.CheckYourInboxScreen;
import co.uk.ppac.mobile.screens.CreatePasswordScreen;
import co.uk.ppac.mobile.screens.DocumentUploadScreen;
import co.uk.ppac.mobile.screens.SelfieInstructionScreen;
import co.uk.ppac.mobile.screens.SignUpByEmailScreen;
import co.uk.ppac.mobile.screens.WelcomeScreen;
import co.uk.ppac.mobile.screens.WelcomeToScreen;
import co.uk.ppac.core.utils.MailinatorOtpFetcher;
import co.uk.ppac.core.utils.SelfieMockHelper;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * M10 Sign Up by Email — E2E happy path.
 * Covers: PPAC_M10_TC_160 (E2E happy) and partial coverage of TC_001..TC_133.
 *
 * <p><b>Gating:</b>
 * <ul>
 *   <li>Write action — gated by {@code -Dmobile.write.enabled=true}; modifies
 *       UAT data (creates an account).</li>
 *   <li>Past-Selfie verification — gated by {@link SelfieMockHelper}; if no mock
 *       strategy is configured, test asserts up to "WELCOME TO" and skips the
 *       remainder.</li>
 * </ul>
 */
public class SignUpEmailE2ETest extends MobileBaseTest {

    private static final String WRITE_FLAG = "mobile.write.enabled";

    @Test(groups = {"mobile", "signup", "e2e"},
            description = "PPAC_M10_TC_160 / E2E happy: full sign-up reaches WELCOME TO; "
                    + "with Selfie mock, continues to Document upload")
    public void testSignUpHappyPath() {
        if (!"true".equalsIgnoreCase(System.getProperty(WRITE_FLAG, "false"))) {
            throw new SkipException("Sign-up E2E modifies UAT data — enable with -D"
                    + WRITE_FLAG + "=true.");
        }

        String mailinatorInbox = "qa_signup_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String email = mailinatorInbox + "@mailinator.com";
        String password = "StrongP@ss123";

        // --- Step 1: Welcome → accept terms → sign up with email ---
        WelcomeScreen welcome = new WelcomeScreen(driver);
        Assert.assertTrue(welcome.isLoaded(), "Welcome screen must be displayed at app launch");
        welcome.acceptTerms();
        SignUpByEmailScreen emailScreen = welcome.openEmailSignUp();
        Assert.assertTrue(emailScreen.isLoaded(),
                "After accepting terms + tap 'Sign up with Email', email form must show");

        // --- Step 2: Enter email → next ---
        emailScreen.enterEmail(email);
        CreatePasswordScreen passwordScreen = emailScreen.tapNextExpectingPassword();
        Assert.assertTrue(passwordScreen.isLoaded(),
                "After valid email + Next, Create password screen must show. "
                        + "If this fails, check email '" + email + "' was accepted by server "
                        + "(F-UX-NEW-1: error message is generic).");

        // --- Step 3: Set password → create account ---
        CheckYourInboxScreen inboxScreen = passwordScreen.createAccount(password);
        Assert.assertTrue(inboxScreen.isLoaded(),
                "After Create account, OTP screen must show. If returned to email "
                        + "screen instead, server rejected the email domain.");

        try {
            java.nio.file.Files.writeString(
                    java.nio.file.Paths.get("target", "current_signup_email.txt"),
                    email + "\n");
        } catch (java.io.IOException ignored) {
        }
        MailinatorOtpFetcher otpFetcher = new MailinatorOtpFetcher();
        String otp = otpFetcher.fetchLatestOtp(mailinatorInbox, 180);
        Assert.assertEquals(otp.length(), CheckYourInboxScreen.OTP_LENGTH,
                "Yopmail must return a 6-digit OTP");
        inboxScreen.enterOtp(otp);
        WelcomeToScreen welcomeToScreen = inboxScreen.tapValidateExpectingSuccess();
        Assert.assertTrue(welcomeToScreen.isLoaded(),
                "After Validate with correct OTP, WELCOME TO screen must show. "
                        + "If 'Invalid Code!' appears, the OTP may have expired (5-min window) "
                        + "or been invalidated by a prior Resend.");

        // --- Step 5: Onboarding intro → selfie ---
        SelfieInstructionScreen selfieInstruction = welcomeToScreen.tapLetsStart();
        Assert.assertTrue(selfieInstruction.isLoaded(),
                "After 'Let's start', Selfie instructions screen must show");

        // --- Step 6: Selfie skip via mock (if available) ---
        if (!SelfieMockHelper.isAnyMockAvailable()) {
            throw new SkipException(
                    "Sign-up E2E reached Selfie step but no mock strategy is configured. "
                            + "Set 'selfie.mock.backend.enabled=true' or "
                            + "'selfie.mock.debug.skip.enabled=true' to verify past-Selfie flow. "
                            + "Up to Selfie instructions — TC_001..TC_140 verified.");
        }
        if (!SelfieMockHelper.isEmailMockable(email)) {
            throw new SkipException("Email '" + email + "' is not in mock whitelist pattern '"
                    + SelfieMockHelper.emailWhitelistPattern() + "' — refusing to invoke mock "
                    + "for non-test account.");
        }
        DocumentUploadScreen documentScreen = selfieInstruction.skipSelfieForTest();
        Assert.assertTrue(documentScreen.isLoaded(),
                "After Selfie mock skip, Document upload screen must show. "
                        + "If this fails, the mock backend may have rejected the request, "
                        + "OR the DocumentUploadScreen locators are out of date (skeleton).");
    }
}
