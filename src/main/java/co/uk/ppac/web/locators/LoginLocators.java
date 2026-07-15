package co.uk.ppac.web.locators;

import org.openqa.selenium.By;

/**
 * Locators tập trung cho màn hình Login (method picker + email form + lỗi auth).
 * Tách khỏi {@code LoginPage} để dễ bảo trì khi UI thay đổi.
 */
public final class LoginLocators {

    private LoginLocators() {
    }

    public static final By METHOD_PICKER_GOOGLE_BUTTON =
            By.xpath("//flt-semantics[@role='button' and normalize-space(.)='Continue with Google']");
    public static final By METHOD_PICKER_MICROSOFT_BUTTON =
            By.xpath("//flt-semantics[@role='button' and normalize-space(.)='Continue with Microsoft']");
    public static final By METHOD_PICKER_EMAIL_BUTTON =
            By.xpath("//flt-semantics[@role='button' and normalize-space(.)='Sign in with Email']");
    public static final By METHOD_PICKER_CREATE_ACCOUNT_BUTTON =
            By.xpath("//flt-semantics[@role='button' and normalize-space(.)='Create Account']");
    public static final By METHOD_PICKER_LANGUAGE_BUTTON =
            By.xpath("//flt-semantics[@role='button' and normalize-space(.)='ENGLISH']");

    public static final By EMAIL_INPUT = By.cssSelector("input[aria-label='Email Address']");
    public static final By PASSWORD_INPUT = By.cssSelector("input[aria-label='Password']");
    public static final By SUBMIT_BUTTON =
            By.xpath("//flt-semantics[@role='button' and normalize-space(.)='Sign In']");
    public static final By FORGOT_PASSWORD_BUTTON =
            By.xpath("//flt-semantics[@role='button' and normalize-space(.)='FORGOT PASSWORD']");

    public static final By EMAIL_INVALID_INPUT =
            By.cssSelector("input[aria-label='Email Address'][aria-invalid='true']");
    public static final By PASSWORD_INVALID_INPUT =
            By.cssSelector("input[aria-label='Password'][aria-invalid='true']");
    public static final By EMPTY_EMAIL_ERROR =
            By.xpath("//flt-semantics//span[normalize-space(.)='Please enter an email']");
    public static final By EMPTY_PASSWORD_ERROR =
            By.xpath("//flt-semantics//span[normalize-space(.)='Please enter password']");
    public static final By WRONG_PASSWORD_ERROR =
            By.xpath("//flt-semantics//span[contains(normalize-space(.),'Wrong password')]");
    public static final By GENERIC_AUTH_ERROR_TEXTS =
            By.xpath("//flt-semantics//span[contains(translate(normalize-space(.), 'IE', 'ie'),'Wrong password. Try again or click on ‘FORGOT PASSWORD')"
                    + " or contains(translate(normalize-space(.), 'EI', 'ei'),'Wrong password. Try again or click on ‘FORGOT PASSWORDs')]");
    public static final By USER_NOT_FOUND_ERROR =
            By.xpath("//flt-semantics//span[contains(normalize-space(.),'User not found')"
                    + " or contains(normalize-space(.),'No account')"
                    + " or contains(normalize-space(.),'does not exist')]");
}
