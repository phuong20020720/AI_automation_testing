package co.uk.ppac.web.pages;

import co.uk.ppac.core.base.BasePage;
import co.uk.ppac.web.locators.DeleteConfirmLocators;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

/**
 * Modal xác nhận delete (Radix Dialog). Title đúng theo UI thật:
 * "Delete submission?" (TC manual viết "Delete a submission?" — sai khác).
 * Locators: see {@link DeleteConfirmLocators}.
 */
public class DeleteConfirmModal extends BasePage {

    public DeleteConfirmModal(WebDriver driver) {
        super(driver);
    }

    public DeleteConfirmModal waitForOpen() {
        new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(DeleteConfirmLocators.DIALOG));
        return this;
    }

    public boolean isOpen() {
        return isPresent(DeleteConfirmLocators.DIALOG);
    }

    public boolean isTitleCorrect() {
        return isPresent(DeleteConfirmLocators.TITLE);
    }

    public String descriptionText() {
        if (!isPresent(DeleteConfirmLocators.DESCRIPTION)) {
            return "";
        }
        return driver.findElement(DeleteConfirmLocators.DESCRIPTION).getText().trim();
    }

    public boolean hasCancelAndConfirmButtons() {
        return isPresent(DeleteConfirmLocators.CANCEL_BUTTON) && isPresent(DeleteConfirmLocators.CONFIRM_BUTTON);
    }

    public WorkerListPage clickCancel() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(DeleteConfirmLocators.CANCEL_BUTTON));
        js().executeScript("arguments[0].click();", btn);
        waitForClose();
        WorkerListPage list = new WorkerListPage(driver);
        list.waitForListReady();
        return list;
    }

    public WorkerListPage clickConfirm() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(DeleteConfirmLocators.CONFIRM_BUTTON));
        js().executeScript("arguments[0].click();", btn);
        waitForClose();
        WorkerListPage list = new WorkerListPage(driver);
        list.waitForListReady();
        return list;
    }

    private void waitForClose() {
        new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.invisibilityOfElementLocated(DeleteConfirmLocators.DIALOG));
    }
}
