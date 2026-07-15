package co.uk.ppac.web.pages;

import co.uk.ppac.core.base.BasePage;
import co.uk.ppac.web.locators.WorkerDetailLocators;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;

/**
 * Compliant Portal — M4 Worker Detail Modal (Flutter alertdialog).
 * Header bar: 9 read-only fields. Tabs: CSCS / RTW (Front) / RTW (Back) / Other Skill Card.
 * Sub-tabs: User Image / PPAC Report / Regula Data / Right To Work / Card Verification / Extra Card.
 * Info panel: 9 fields with Copy buttons. Submit + Reason + Edit + Update controls.
 * Locators: see {@link WorkerDetailLocators}.
 */
public class WorkerDetailModal extends BasePage {

    public static final List<String> EXPECTED_HEADER_FIELDS = List.of(
            "Company", "Type", "Email", "DOB", "V Code", "Skill Card",
            "Job Role", "Status", "Failed Reason");

    public static final List<String> EXPECTED_DOC_TABS = List.of(
            "CSCS", "RTW (Front)", "RTW (Back)", "Other Skill Card");

    public static final List<String> EXPECTED_SUB_TABS = List.of(
            "User Image", "PPAC Report", "Regula Data", "Right To Work",
            "Card Verification", "Extra Card");

    public static final List<String> EXPECTED_INFO_FIELDS = List.of(
            "Name", "Surname", "Reusable Passport", "Nationality", "Site Location",
            "Sub Contractor", "NI Number", "Share Code", "Card Number");

    public WorkerDetailModal(WebDriver driver) {
        super(driver);
    }

    public WorkerDetailModal waitForOpen() {
        new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.presenceOfElementLocated(WorkerDetailLocators.DIALOG));
        return this;
    }

    public boolean isOpen() {
        return isPresent(WorkerDetailLocators.DIALOG);
    }

    /** Header Status field value (read-only label trong header bar). Trả về aria-label hoặc text. */
    public String headerStatusText() {
        return headerFieldValue("Status");
    }

    public boolean isSubmitDisabled() {
        List<WebElement> nodes = driver.findElements(WorkerDetailLocators.SUBMIT_BUTTON);
        if (nodes.isEmpty()) {
            return true;
        }
        String disabled = nodes.get(0).getAttribute("aria-disabled");
        return "true".equalsIgnoreCase(disabled);
    }

    public boolean isEditDisabled() {
        List<WebElement> nodes = driver.findElements(WorkerDetailLocators.EDIT_BUTTON);
        if (nodes.isEmpty()) {
            return true;
        }
        String disabled = nodes.get(0).getAttribute("aria-disabled");
        return "true".equalsIgnoreCase(disabled);
    }

    public boolean hasSubmitButton() {
        return isPresent(WorkerDetailLocators.SUBMIT_BUTTON);
    }

    public boolean hasReasonButton() {
        return isPresent(WorkerDetailLocators.REASON_BUTTON);
    }

    public boolean hasUpdateButton() {
        return isPresent(WorkerDetailLocators.UPDATE_BUTTON);
    }

    public boolean hasDirectRejectOption() {
        return isPresent(WorkerDetailLocators.DIRECT_REJECT_BUTTON);
    }

    private String readLabel(WebElement el) {
        String aria = el.getAttribute("aria-label");
        if (aria != null && !aria.isBlank()) {
            return aria.trim();
        }
        String txt = el.getText();
        return txt == null ? "" : txt.trim();
    }

    /**
     * Read the value next to a given header label (e.g. "Email" → returns the email shown).
     *
     * <p>Header layout (Flutter alertdialog): 9 label nodes followed by 9 value nodes.
     * Position-based mapping: index của label trong EXPECTED_HEADER_FIELDS = index của value
     * trong block 9 value đầu tiên (sau khi loại bỏ label + empty nodes).
     *
     * <p>Recon 2026-05-28: thuật toán cũ (walk forward từ label) bị fail khi UAT layout
     * group labels-then-values — first non-label-non-empty sau label "Email" hóa ra là
     * Company-value (đứng đầu block values), không phải Email-value.
     */
    public String headerFieldValue(String label) {
        int targetIdx = EXPECTED_HEADER_FIELDS.indexOf(label);
        if (targetIdx < 0) {
            return "";
        }
        List<WebElement> nodes = driver.findElements(WorkerDetailLocators.DIALOG_SEMANTICS);
        // Tìm vị trí node "Company" — anchor đầu block header
        int companyLabelIdx = -1;
        for (int i = 0; i < nodes.size(); i++) {
            if ("Company".equals(readLabel(nodes.get(i)))) {
                companyLabelIdx = i;
                break;
            }
        }
        if (companyLabelIdx < 0) {
            return "";
        }
        // Bắt đầu từ Company label, skip qua các header labels + empty separators để tìm
        // block values. Collect tối đa 9 values (= EXPECTED_HEADER_FIELDS.size()).
        List<String> values = new java.util.ArrayList<>();
        boolean passedLabels = false;
        for (int j = companyLabelIdx + 1; j < nodes.size() && values.size() < EXPECTED_HEADER_FIELDS.size(); j++) {
            String v = readLabel(nodes.get(j));
            if (v.isEmpty()) {
                continue;
            }
            if (EXPECTED_HEADER_FIELDS.contains(v)) {
                // Đang đi qua block labels — đánh dấu đã thấy nhiều label rồi sẽ vào value block
                passedLabels = true;
                continue;
            }
            if (passedLabels) {
                values.add(v);
            }
        }
        return targetIdx < values.size() ? values.get(targetIdx) : "";
    }

    /**
     * Close the modal — thử ESC trước, sau đó click Close/X button nếu vẫn còn open.
     * Wait deterministically until dialog vắng mặt (tối đa 10s) thay vì sleep cố định.
     * Returns silently if already closed.
     */
    public void close() {
        if (!isOpen()) {
            return;
        }
        // Modal close icon (X) là Flutter canvas-rendered — KHÔNG có semantic node,
        // KHÔNG có DOM hit-test target, KHÔNG response ESC, KHÔNG dismiss qua glass-pane click.
        // Recon (Playwright MCP 2026-05-25): real trusted mouse click tại viewport
        // (1670, 25) ở viewport 1920×1080 đóng được modal.
        // → Dùng PointerInput với Origin.viewport() để generate real trusted events.
        // Thử nhiều offset từ right edge (scrollbar/zoom có thể lệch ±20px).
        Long innerWidth = (Long) js().executeScript("return window.innerWidth;");
        int width = innerWidth.intValue();
        // Recon Selenium (DPR=1.25, viewport 1540×736): X icon ở screenshot pixel (~1820, ~85)
        // → CSS pixel ≈ (1456, 68) — offset từ right ≈ 84, Y ≈ 68.
        // (Playwright recon ban đầu ở viewport 1920×1080 DPR=1 cho coords (1670, 25) — không scale linear).
        int[] xOffsets = {84, 90, 80, 75, 95, 70, 100, 65, 110, 120};
        int[] yPositions = {68, 75, 60, 80, 55, 85, 50, 90};
        for (int y : yPositions) {
            for (int xOff : xOffsets) {
                int closeX = width - xOff;
                org.openqa.selenium.interactions.PointerInput mouse =
                        new org.openqa.selenium.interactions.PointerInput(
                                org.openqa.selenium.interactions.PointerInput.Kind.MOUSE, "close-modal");
                org.openqa.selenium.interactions.Sequence seq =
                        new org.openqa.selenium.interactions.Sequence(mouse, 0)
                                .addAction(mouse.createPointerMove(java.time.Duration.ofMillis(50),
                                        org.openqa.selenium.interactions.PointerInput.Origin.viewport(),
                                        closeX, y))
                                .addAction(mouse.createPointerDown(
                                        org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT.asArg()))
                                .addAction(new org.openqa.selenium.interactions.Pause(mouse,
                                        java.time.Duration.ofMillis(80)))
                                .addAction(mouse.createPointerUp(
                                        org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT.asArg()));
                try {
                    ((org.openqa.selenium.interactions.Interactive) driver)
                            .perform(java.util.Collections.singletonList(seq));
                } catch (Exception ignored) {
                }
                if (waitUntilClosed(400)) {
                    return;
                }
            }
        }
    }

    private boolean waitUntilClosed(long timeoutMs) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (!isOpen()) {
                return true;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return !isOpen();
            }
        }
        return !isOpen();
    }

    /** Fill the reason textbox if present, then click Submit. Used for M2 transition tests. */
    public void submitWithReason(String reason) {
        if (reason != null && !reason.isEmpty()) {
            List<WebElement> inputs = driver.findElements(WorkerDetailLocators.REASON_TEXTBOX);
            if (!inputs.isEmpty()) {
                WebElement target = inputs.get(0);
                js().executeScript("arguments[0].click(); arguments[0].focus();", target);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                try {
                    WebElement editor = driver.findElement(FLUTTER_TEXT_EDITING_INPUT);
                    editor.clear();
                    editor.sendKeys(reason);
                } catch (org.openqa.selenium.NoSuchElementException e) {
                    target.clear();
                    target.sendKeys(reason);
                }
            }
        }
        WebElement submit = waitForPresent(WorkerDetailLocators.SUBMIT_BUTTON);
        js().executeScript("arguments[0].click();", submit);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ===================== Edit dialog (M4.4.1) =====================

    /** Click Edit button → Edit dialog xuất hiện với 5 input editable. */
    public WorkerDetailModal clickEdit() {
        WebElement edit = waitForPresent(WorkerDetailLocators.EDIT_BUTTON);
        // Flutter button: dùng pointer chain thay vì JS click thuần (xem F-RECON-2)
        js().executeScript(
                "var el=arguments[0];"
                        + "el.scrollIntoView({block:'center'});"
                        + "var r=el.getBoundingClientRect();"
                        + "var x=r.left+r.width/2, y=r.top+r.height/2;"
                        + "var base={bubbles:true,cancelable:true,composed:true,clientX:x,clientY:y,"
                        + "pointerId:1,pointerType:'mouse',isPrimary:true,button:0};"
                        + "el.dispatchEvent(new PointerEvent('pointerdown',Object.assign({},base,{buttons:1})));"
                        + "el.dispatchEvent(new PointerEvent('pointerup',Object.assign({},base,{buttons:0})));"
                        + "el.dispatchEvent(new MouseEvent('click',Object.assign({},base,{buttons:0})));",
                edit);
        // Wait Edit dialog ready — Save Changes button xuất hiện
        new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(WorkerDetailLocators.SAVE_CHANGES_BUTTON));
        return this;
    }

    public boolean isEditDialogOpen() {
        return isPresent(WorkerDetailLocators.SAVE_CHANGES_BUTTON);
    }

    /**
     * Fill NI Number input (input thứ 5/cuối trong Edit dialog, sau DOB).
     * Recon: 5 inputs theo thứ tự Name, Surname, Sub Contractor, DOB (aria-label='Date of Birth'), NI.
     */
    public WorkerDetailModal fillNiNumber(String value) {
        // JS click + BACK_SPACE × 40 trên empty NI gây Flutter close Edit dialog (recon 2026-05-25).
        // Pattern fix: dùng pointer chain proper (như flutterTap) để focus, rồi Actions.sendKeys
        // gửi keys vào active element (Flutter's text-editing-host) không cần element ref.
        WebElement niInput = locateNiNumberInput();
        js().executeScript(
                "var el=arguments[0];"
                        + "el.scrollIntoView({block:'center'});"
                        + "var r=el.getBoundingClientRect();"
                        + "var x=r.left+r.width/2, y=r.top+r.height/2;"
                        + "var base={bubbles:true,cancelable:true,composed:true,clientX:x,clientY:y,"
                        + "pointerId:1,pointerType:'mouse',isPrimary:true,button:0};"
                        + "el.dispatchEvent(new PointerEvent('pointerdown',Object.assign({},base,{buttons:1})));"
                        + "el.dispatchEvent(new PointerEvent('pointerup',Object.assign({},base,{buttons:0})));"
                        + "el.dispatchEvent(new MouseEvent('click',Object.assign({},base,{buttons:0})));",
                niInput);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // Type via Actions — không hold element ref, gửi key vào focused editor
        org.openqa.selenium.interactions.Actions actions =
                new org.openqa.selenium.interactions.Actions(driver);
        actions.sendKeys(org.openqa.selenium.Keys.chord(
                org.openqa.selenium.Keys.CONTROL, "a")).perform();
        actions.sendKeys(org.openqa.selenium.Keys.DELETE).perform();
        actions.sendKeys(value).perform();
        return this;
    }

    public String readNiNumberInputValue() {
        WebElement niInput = locateNiNumberInput();
        String v = niInput.getAttribute("value");
        return v == null ? "" : v;
    }

    private WebElement locateNiNumberInput() {
        List<WebElement> all = driver.findElements(WorkerDetailLocators.EDIT_INPUTS);
        // Filter ra DOB
        WebElement candidate = null;
        for (WebElement input : all) {
            String aria = input.getAttribute("aria-label");
            if (aria == null || !"Date of Birth".equals(aria)) {
                candidate = input; // keep last non-DOB input
            }
        }
        if (candidate == null) {
            throw new org.openqa.selenium.NoSuchElementException(
                    "NI Number input không tìm thấy — Edit dialog có thể chưa mở. "
                            + "Total inputs: " + all.size());
        }
        return candidate;
    }

    /** Click Save Changes → submit edit + close Edit dialog. */
    public WorkerDetailModal clickSaveChanges() {
        WebElement save = waitForPresent(WorkerDetailLocators.SAVE_CHANGES_BUTTON);
        js().executeScript(
                "var el=arguments[0];"
                        + "var r=el.getBoundingClientRect();"
                        + "var x=r.left+r.width/2, y=r.top+r.height/2;"
                        + "var base={bubbles:true,cancelable:true,composed:true,clientX:x,clientY:y,"
                        + "pointerId:1,pointerType:'mouse',isPrimary:true,button:0};"
                        + "el.dispatchEvent(new PointerEvent('pointerdown',Object.assign({},base,{buttons:1})));"
                        + "el.dispatchEvent(new PointerEvent('pointerup',Object.assign({},base,{buttons:0})));"
                        + "el.dispatchEvent(new MouseEvent('click',Object.assign({},base,{buttons:0})));",
                save);
        // Wait Edit dialog đóng (Save Changes button biến mất)
        new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.invisibilityOfElementLocated(WorkerDetailLocators.SAVE_CHANGES_BUTTON));
        return this;
    }

    /** Click Cancel để abort edit không lưu. */
    public WorkerDetailModal clickCancelEdit() {
        if (!isPresent(WorkerDetailLocators.CANCEL_EDIT_BUTTON)) {
            return this;
        }
        WebElement cancel = driver.findElement(WorkerDetailLocators.CANCEL_EDIT_BUTTON);
        js().executeScript(
                "var el=arguments[0];"
                        + "var r=el.getBoundingClientRect();"
                        + "var x=r.left+r.width/2, y=r.top+r.height/2;"
                        + "var base={bubbles:true,cancelable:true,composed:true,clientX:x,clientY:y,"
                        + "pointerId:1,pointerType:'mouse',isPrimary:true,button:0};"
                        + "el.dispatchEvent(new PointerEvent('pointerdown',Object.assign({},base,{buttons:1})));"
                        + "el.dispatchEvent(new PointerEvent('pointerup',Object.assign({},base,{buttons:0})));"
                        + "el.dispatchEvent(new MouseEvent('click',Object.assign({},base,{buttons:0})));",
                cancel);
        return this;
    }
}
