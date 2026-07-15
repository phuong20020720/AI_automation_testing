package co.uk.ppac.web.locators;

import org.openqa.selenium.By;

/**
 * Locators tập trung cho Worker Detail Modal (Flutter alertdialog).
 * Các button scope trong {@code //flt-semantics[@role='alertdialog']} để tránh
 * match button ngoài modal.
 */
public final class WorkerDetailLocators {

    private WorkerDetailLocators() {
    }

    public static final By DIALOG = By.cssSelector("flt-semantics[role='alertdialog']");
    public static final By DIALOG_SEMANTICS = By.cssSelector("flt-semantics[role='alertdialog'] flt-semantics");
    public static final By SUBMIT_BUTTON =
            By.xpath("//flt-semantics[@role='alertdialog']//flt-semantics[@role='button'"
                    + " and (normalize-space(.)='Submit' or @aria-label='Submit')]");
    public static final By REASON_BUTTON =
            By.xpath("//flt-semantics[@role='alertdialog']//flt-semantics[@role='button'"
                    + " and (normalize-space(.)='If failed, reason' or @aria-label='If failed, reason')]");
    public static final By EDIT_BUTTON =
            By.xpath("//flt-semantics[@role='alertdialog']//flt-semantics[@role='button'"
                    + " and (normalize-space(.)='Edit' or @aria-label='Edit')]");
    public static final By UPDATE_BUTTON =
            By.xpath("//flt-semantics[@role='alertdialog']//flt-semantics[@role='button'"
                    + " and (normalize-space(.)='Update' or @aria-label='Update')]");
    public static final By SAVE_CHANGES_BUTTON =
            By.xpath("//flt-semantics[@role='button' and normalize-space(.)='Save Changes']");
    public static final By CANCEL_EDIT_BUTTON =
            By.xpath("//flt-semantics[@role='button' and normalize-space(.)='Cancel']");
    // Edit dialog text inputs: Name(0), Surname(1), Sub Contractor(2), DOB(3), NI Number(4).
    // DOB có aria-label="Date of Birth"; các input khác không có aria-label.
    public static final By EDIT_INPUTS = By.cssSelector("input[type='text']");

    public static final By DIRECT_REJECT_BUTTON = By.xpath(
            "//flt-semantics[@role='alertdialog']//flt-semantics[@role='button'"
                    + " and (normalize-space(.)='Reject' or normalize-space(.)='Direct Reject'"
                    + " or normalize-space(.)='Mark Rejected' or @aria-label='Reject'"
                    + " or @aria-label='Direct Reject' or @aria-label='Mark Rejected')]");
    public static final By REASON_TEXTBOX = By.xpath(
            "//flt-semantics[@role='alertdialog']//flt-semantics[@role='textbox']");
}
