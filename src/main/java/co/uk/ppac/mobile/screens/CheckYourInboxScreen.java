package co.uk.ppac.mobile.screens;
import co.uk.ppac.core.base.BaseScreen;

import co.uk.ppac.mobile.locators.CheckYourInboxLocators;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Screen Object for Sign Up Step 4 — "Check your inbox" (OTP verification).
 *
 * <p>6 OTP boxes are Compose custom views with NO {@code android.widget.EditText}.
 * Each box's filled digit is exposed via {@code content-desc} (NOT {@code text}).
 * Boxes are positioned at y=842 on 1080x2400 reference; centres x: 105, 279, 453,
 * 627, 801, 975 — spacing ~174px. See {@link CheckYourInboxLocators}.
 *
 * <p>"Resend" link has a countdown phase ("Resend after Xs", disabled) and an
 * active phase ("Resend", clickable). Validate button is full-width centre.
 *
 * <p>See F-INFRA-1 + F-DOC-1 in {@code sign_up_email_exploration.md}.
 */
public class CheckYourInboxScreen extends BaseScreen {

    public static final int OTP_LENGTH = 6;
    /** Box centre X positions on 1080-wide reference viewport. */
    private static final int[] BOX_X = {105, 279, 453, 627, 801, 975};
    private static final int BOX_Y = 842;

    public CheckYourInboxScreen(AndroidDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(CheckYourInboxLocators.SCREEN_HEADING);
    }

    /**
     * Fills the 6-digit OTP. Taps each box explicitly and types one digit at a
     * time — auto-advance via single {@code sendKeys} can drop the last digit
     * on Compose custom OTP widgets (F-INFRA-1).
     */
    public CheckYourInboxScreen enterOtp(String code) {
        if (code == null || code.length() != OTP_LENGTH) {
            throw new IllegalArgumentException(
                    "OTP must be exactly " + OTP_LENGTH + " digits, got: "
                            + (code == null ? "null" : code.length()));
        }
        // Compose OTP custom widget: per-box tap + key press via Android KeyEvent.
        // Tested patterns:
        //   - "mobile: type" — unreliable, drops chars on Compose custom inputs
        //   - sendKeys on EditText — no EditText exists (Compose canvas)
        //   - pressKey(KEYCODE_N) — reliable across Compose versions ✓
        // Each focused box accepts a single digit via OS-level key inject.
        for (int i = 0; i < OTP_LENGTH; i++) {
            gestures.tapAt(BOX_X[i], BOX_Y);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            int digit = Character.digit(code.charAt(i), 10);
            // OS-level key inject via Android KeyEvent (KEYCODE_0..9)
            driver.pressKey(new io.appium.java_client.android.nativekey.KeyEvent(
                    digitKeyOf(digit)));
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return this;
    }

    private static io.appium.java_client.android.nativekey.AndroidKey digitKeyOf(int digit) {
        switch (digit) {
            case 0: return io.appium.java_client.android.nativekey.AndroidKey.DIGIT_0;
            case 1: return io.appium.java_client.android.nativekey.AndroidKey.DIGIT_1;
            case 2: return io.appium.java_client.android.nativekey.AndroidKey.DIGIT_2;
            case 3: return io.appium.java_client.android.nativekey.AndroidKey.DIGIT_3;
            case 4: return io.appium.java_client.android.nativekey.AndroidKey.DIGIT_4;
            case 5: return io.appium.java_client.android.nativekey.AndroidKey.DIGIT_5;
            case 6: return io.appium.java_client.android.nativekey.AndroidKey.DIGIT_6;
            case 7: return io.appium.java_client.android.nativekey.AndroidKey.DIGIT_7;
            case 8: return io.appium.java_client.android.nativekey.AndroidKey.DIGIT_8;
            case 9: return io.appium.java_client.android.nativekey.AndroidKey.DIGIT_9;
            default: throw new IllegalArgumentException("Not a digit 0-9: " + digit);
        }
    }

    /**
     * Reads the digit currently filled in the given box (0-indexed). Returns
     * empty string if box is empty.
     */
    public String readDigit(int boxIndex) {
        if (boxIndex < 0 || boxIndex >= OTP_LENGTH) {
            throw new IndexOutOfBoundsException("OTP box index out of range: " + boxIndex);
        }
        // Each box's filled digit is in content-desc on the box node. Locate the
        // box at the given X coordinate and read its content-desc.
        By boxLocator = CheckYourInboxLocators.otpBoxByCenterX(BOX_X[boxIndex]);
        try {
            WebElement box = wait.waitForVisible(boxLocator);
            String desc = box.getAttribute("content-desc");
            return desc == null ? "" : desc;
        } catch (RuntimeException e) {
            return "";
        }
    }

    /** Returns true if all 6 boxes have a digit filled. */
    public boolean isOtpComplete() {
        for (int i = 0; i < OTP_LENGTH; i++) {
            if (readDigit(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean hasInvalidCodeError() {
        return isDisplayed(CheckYourInboxLocators.INVALID_CODE_ERROR);
    }

    public boolean isResendActive() {
        return isDisplayed(CheckYourInboxLocators.RESEND_ACTIVE_LINK);
    }

    public CheckYourInboxScreen tapResend() {
        tap(CheckYourInboxLocators.RESEND_ACTIVE_LINK);
        return this;
    }

    public SignUpByEmailScreen tapEditMyEmail() {
        tap(CheckYourInboxLocators.EDIT_MY_EMAIL_LINK);
        return new SignUpByEmailScreen(driver);
    }

    /**
     * Taps Validate. On success → navigate to {@link WelcomeToScreen}.
     * On invalid OTP → stay here with error. Caller must verify.
     */
    public WelcomeToScreen tapValidateExpectingSuccess() {
        tap(CheckYourInboxLocators.VALIDATE_BUTTON);
        return new WelcomeToScreen(driver);
    }

    public CheckYourInboxScreen tapValidateExpectingError() {
        tap(CheckYourInboxLocators.VALIDATE_BUTTON);
        return this;
    }
}
