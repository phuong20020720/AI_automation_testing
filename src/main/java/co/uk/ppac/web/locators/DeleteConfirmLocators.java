package co.uk.ppac.web.locators;

import org.openqa.selenium.By;

/**
 * Locators tập trung cho modal xác nhận delete (Radix Dialog). Title đúng UI thật:
 * "Delete submission?".
 */
public final class DeleteConfirmLocators {

    private DeleteConfirmLocators() {
    }

    public static final By DIALOG = By.cssSelector("div[role='dialog'][data-slot='dialog-content']");
    public static final By TITLE = By.xpath("//h5[normalize-space(.)='Delete submission?']");
    public static final By DESCRIPTION =
            By.cssSelector("div[role='dialog'] [data-slot='dialog-description']");
    public static final By CANCEL_BUTTON =
            By.xpath("//div[@role='dialog']//button[normalize-space(.)='Cancel']");
    public static final By CONFIRM_BUTTON =
            By.xpath("//div[@role='dialog']//button[normalize-space(.)='Confirm']");
}
