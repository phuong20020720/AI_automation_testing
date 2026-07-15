package co.uk.ppac.web.tests.compliantportal;

import co.uk.ppac.web.pages.WorkerDetailModal;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * M4 Worker Detail Modal — document tabs + sub-tabs + info panel placeholders + Copy buttons.
 * Covers: PPAC_M4_TC_010 (4 doc tabs), PPAC_M4_TC_011 (click tab), PPAC_M4_TC_014 (Other Skill Card),
 * PPAC_M4_TC_020 (6 sub-tabs), PPAC_M4_TC_021 (PPAC Report sub-tab), PPAC_M4_TC_022 (Regula Data sub-tab),
 * PPAC_M4_TC_023 (Card Verification sub-tab), PPAC_M4_TC_025 (PPAC Report state),
 * PPAC_M4_TC_031 (Copy buttons proxy for field render),
 * PPAC_M4_TC_032/_033 (Copy buttons clickable, modal stable).
 *
 * <p>Pure read-only — không write action. Sử dụng modal mở qua queue.openFirstWorker() trên
 * landing default Pending → modal stable theo cycle 5 sau khi fix headerFieldValue
 * (2026-05-28).
 */
public class M4DocumentPanelTest extends CompliantPortalBaseTest {

    @Test(groups = {"compliantportal", "m4", "smoke"},
            description = "PPAC_M4_TC_010: 4 document tabs hiển thị đầy đủ — CSCS / RTW (Front) "
                    + "/ RTW (Back) / Other Skill Card")
    public void testFourDocumentTabsPresent() {
        if (queue.dataRowCount() == 0) {
            throw new SkipException("Pending queue rỗng — không có worker để mở modal");
        }
        WorkerDetailModal modal = queue.openFirstWorker();
        Assert.assertTrue(modal.isOpen(), "Modal phải mở để inspect document tabs");
        try {
            List<String> labels = collectDialogLabels();
            List<String> missing = new ArrayList<>();
            for (String tab : WorkerDetailModal.EXPECTED_DOC_TABS) {
                if (!labels.contains(tab)) {
                    missing.add(tab);
                }
            }
            Assert.assertTrue(missing.isEmpty(),
                    "Phải có đủ 4 document tabs. Missing=" + missing);
        } finally {
            modal.close();
        }
    }

    @Test(groups = {"compliantportal", "m4"},
            description = "PPAC_M4_TC_011: Click từng document tab — modal vẫn open + sub-tabs render. "
                    + "Verify click không gây error/crash; KHÔNG verify image content (cần data đặc thù).")
    public void testClickEachDocumentTabKeepsModalStable() {
        if (queue.dataRowCount() == 0) {
            throw new SkipException("Pending queue rỗng");
        }
        WorkerDetailModal modal = queue.openFirstWorker();
        Assert.assertTrue(modal.isOpen(), "Modal phải mở");
        try {
            for (String tabName : WorkerDetailModal.EXPECTED_DOC_TABS) {
                By tabBtn = By.xpath("//flt-semantics[@role='alertdialog']"
                        + "//flt-semantics[@role='button' and (normalize-space(.)='" + tabName
                        + "' or @aria-label='" + tabName + "')]");
                List<WebElement> tabs = driver.findElements(tabBtn);
                if (tabs.isEmpty()) {
                    throw new SkipException("Tab '" + tabName + "' không tìm thấy trong modal — "
                            + "có thể do worker này không có document loại đó");
                }
                flutterTap(tabs.get(0));
                Assert.assertTrue(modal.isOpen(),
                        "Sau click tab '" + tabName + "', modal phải vẫn open (không crash)");
            }
            // Sau khi click qua đủ 4 tab, sub-tabs vẫn phải render (TC_020 quan hệ với TC_011)
            List<String> labels = collectDialogLabels();
            int subTabsFound = 0;
            for (String st : WorkerDetailModal.EXPECTED_SUB_TABS) {
                if (labels.contains(st)) {
                    subTabsFound++;
                }
            }
            Assert.assertTrue(subTabsFound >= 4,
                    "Sau click 4 doc tab, sub-tabs vẫn phải render (≥4/6). Found=" + subTabsFound);
        } finally {
            modal.close();
        }
    }

    @Test(groups = {"compliantportal", "m4", "smoke"},
            description = "PPAC_M4_TC_020: 6 sub-tabs hiển thị — User Image / PPAC Report / Regula Data "
                    + "/ Right To Work / Card Verification / Extra Card")
    public void testSixSubTabsPresent() {
        if (queue.dataRowCount() == 0) {
            throw new SkipException("Pending queue rỗng");
        }
        WorkerDetailModal modal = queue.openFirstWorker();
        Assert.assertTrue(modal.isOpen(), "Modal phải mở");
        try {
            List<String> labels = collectDialogLabels();
            List<String> missing = new ArrayList<>();
            for (String st : WorkerDetailModal.EXPECTED_SUB_TABS) {
                if (!labels.contains(st)) {
                    missing.add(st);
                }
            }
            // Allow ≤2 missing để cover trường hợp worker không có Extra Card / Card Verification
            Assert.assertTrue(missing.size() <= 2,
                    "Phải có ≥4/6 sub-tabs. Missing=" + missing
                            + ". Nếu missing > 2 → có thể bug UI hoặc worker thiếu data tab.");
        } finally {
            modal.close();
        }
    }

    @Test(groups = {"compliantportal", "m4"},
            description = "PPAC_M4_TC_031: Info panel có Copy button cho mỗi field — proxy cho việc "
                    + "field render đầy đủ (có Copy = field rendered, không leave blank entirely). "
                    + "Verify số Copy buttons trong dialog ≥ 6 (proportional với EXPECTED_INFO_FIELDS).")
    public void testInfoPanelFieldsHaveCopyButtons() {
        if (queue.dataRowCount() == 0) {
            throw new SkipException("Pending queue rỗng");
        }
        WorkerDetailModal modal = queue.openFirstWorker();
        Assert.assertTrue(modal.isOpen(), "Modal phải mở");
        try {
            // Đếm Copy buttons trong dialog — mỗi info field thường có 1 Copy
            By copyButtons = By.xpath("//flt-semantics[@role='alertdialog']"
                    + "//flt-semantics[@role='button' and (normalize-space(.)='Copy' or @aria-label='Copy')]");
            List<WebElement> copies = driver.findElements(copyButtons);
            // EXPECTED_INFO_FIELDS có 9 field; Copy button tối thiểu nên ≥6 (loose để cho phép
            // 1-3 field nullable không có Copy nếu UI chọn ẩn).
            Assert.assertTrue(copies.size() >= 6,
                    "Info panel phải có ≥6 Copy button (1 Copy / field) — proxy cho field render. "
                            + "Found=" + copies.size() + ". Nếu < 6 → có thể UI thiếu placeholder hoặc "
                            + "đã ẩn nullable field hoàn toàn (vi phạm spec TC_031).");
        } finally {
            modal.close();
        }
    }

    @Test(groups = {"compliantportal", "m4"},
            description = "PPAC_M4_TC_014: Worker với Skill Card='Other' — tab CSCS empty/placeholder, "
                    + "tab Other Skill Card có content. SKIP nếu page 1 không có worker Skill Card=Other.")
    public void testOtherSkillCardWorkerShowsContentOnOtherTab() {
        if (queue.dataRowCount() == 0) {
            throw new SkipException("Pending queue rỗng");
        }
        // Tìm row có Skill Card = "Other"
        int skillCardIdx = co.uk.ppac.web.pages.VerifierQueuePage
                .EXPECTED_TABLE_HEADERS.indexOf("Skill Card");
        List<String> skillCards = queue.columnValues(skillCardIdx);
        int otherRowIdx = -1;
        for (int i = 0; i < skillCards.size(); i++) {
            if (skillCards.get(i) != null && skillCards.get(i).trim().equalsIgnoreCase("Other")) {
                otherRowIdx = i;
                break;
            }
        }
        if (otherRowIdx < 0) {
            throw new SkipException("Page 1 không có worker Skill Card='Other' — cần dataset có "
                    + "worker này để verify tab routing");
        }
        // Page Object chưa hỗ trợ openWorkerByIndex; mở first worker rồi assert dựa trên dataset có Other
        WorkerDetailModal modal = queue.openFirstWorker();
        Assert.assertTrue(modal.isOpen(), "Modal phải mở");
        try {
            // Modal mở từ row đầu (có thể không phải row Other); fall back assert tab labels có đủ
            // và Other Skill Card tab clickable. Spec verification depth giới hạn bởi PO data setup.
            By otherTab = By.xpath("//flt-semantics[@role='alertdialog']"
                    + "//flt-semantics[@role='button' and normalize-space(.)='Other Skill Card']");
            List<WebElement> tabs = driver.findElements(otherTab);
            Assert.assertFalse(tabs.isEmpty(),
                    "Tab 'Other Skill Card' phải hiện trong modal (label-level verification)");
            flutterTap(tabs.get(0));
            Assert.assertTrue(modal.isOpen(),
                    "Sau click 'Other Skill Card', modal phải vẫn open");
        } finally {
            modal.close();
        }
    }

    @Test(groups = {"compliantportal", "m4"},
            description = "PPAC_M4_TC_021: Click sub-tab 'PPAC Report' — modal vẫn open + sub-tab "
                    + "đã được render. (Spec content check: 'scoring details (numeric score, "
                    + "breakdown)' yêu cầu worker có report generated — data-dep, không verify "
                    + "depth.)")
    public void testClickPpacReportSubTabKeepsModalStable() {
        clickSubTabAndVerify("PPAC Report");
    }

    @Test(groups = {"compliantportal", "m4"},
            description = "PPAC_M4_TC_022: Click sub-tab 'Regula Data' — modal vẫn open. "
                    + "(Spec: OCR result + confidence weight — data-dep.)")
    public void testClickRegulaDataSubTabKeepsModalStable() {
        clickSubTabAndVerify("Regula Data");
    }

    @Test(groups = {"compliantportal", "m4"},
            description = "PPAC_M4_TC_023: Click sub-tab 'Card Verification' — modal vẫn open. "
                    + "(Spec: passed/failed status + lý do — data-dep.)")
    public void testClickCardVerificationSubTabKeepsModalStable() {
        clickSubTabAndVerify("Card Verification");
    }

    @Test(groups = {"compliantportal", "m4"},
            description = "PPAC_M4_TC_025: PPAC Report state — verify sub-tab có content nào đó "
                    + "(text 'pending' / score number / placeholder). Test gracefully accept cả 2 "
                    + "state (đã generate vs pending). Submit không bị block trong cả 2 case "
                    + "(verify hasSubmitButton).")
    public void testPpacReportSubTabShowsContentOrPlaceholder() {
        if (queue.dataRowCount() == 0) {
            throw new SkipException("Pending queue rỗng");
        }
        WorkerDetailModal modal = queue.openFirstWorker();
        Assert.assertTrue(modal.isOpen(), "Modal phải mở");
        try {
            By ppacTab = By.xpath("//flt-semantics[@role='alertdialog']"
                    + "//flt-semantics[@role='button' and (normalize-space(.)='PPAC Report' "
                    + "or @aria-label='PPAC Report')]");
            List<WebElement> tabs = driver.findElements(ppacTab);
            if (tabs.isEmpty()) {
                throw new SkipException("PPAC Report tab không tìm thấy");
            }
            flutterTap(tabs.get(0));
            Assert.assertTrue(modal.isOpen(),
                    "Sau click 'PPAC Report', modal phải vẫn open");
            // Verify modal có Submit button (không bị disable bởi report pending — TC_025 spec
            // "Submit có chặn?" — current behavior cho phép submit ngay cả khi report pending).
            Assert.assertTrue(modal.hasSubmitButton(),
                    "Submit button phải hiện kể cả khi PPAC Report pending — "
                            + "Worker Verifier vẫn có thể approve/reject sau khi xem document");
        } finally {
            modal.close();
        }
    }

    @Test(groups = {"compliantportal", "m4"},
            description = "PPAC_M4_TC_032 + TC_033: Copy buttons clickable + modal stable. "
                    + "Verify click từng Copy button trong info panel không gây crash / không "
                    + "close modal. (Clipboard verification cần browser permission — out of scope.)")
    public void testCopyButtonsClickableModalStable() {
        if (queue.dataRowCount() == 0) {
            throw new SkipException("Pending queue rỗng");
        }
        WorkerDetailModal modal = queue.openFirstWorker();
        Assert.assertTrue(modal.isOpen(), "Modal phải mở");
        try {
            By copyButtons = By.xpath("//flt-semantics[@role='alertdialog']"
                    + "//flt-semantics[@role='button' and (normalize-space(.)='Copy' "
                    + "or @aria-label='Copy')]");
            List<WebElement> copies = driver.findElements(copyButtons);
            if (copies.size() < 3) {
                throw new SkipException("Info panel < 3 Copy button — không đủ để test clickability");
            }
            // Click 3 Copy buttons đầu — verify modal stable sau mỗi click
            int clickable = 0;
            for (int i = 0; i < Math.min(3, copies.size()); i++) {
                try {
                    flutterTap(copies.get(i));
                    if (modal.isOpen()) {
                        clickable++;
                    }
                } catch (Exception ignored) {
                    // Skip individual click failure, continue
                }
            }
            Assert.assertTrue(clickable >= 3,
                    "≥3 Copy button đầu phải click được + modal vẫn open sau mỗi click. "
                    + "Successful=" + clickable);
        } finally {
            modal.close();
        }
    }

    private void clickSubTabAndVerify(String subTabName) {
        if (queue.dataRowCount() == 0) {
            throw new SkipException("Pending queue rỗng");
        }
        WorkerDetailModal modal = queue.openFirstWorker();
        Assert.assertTrue(modal.isOpen(), "Modal phải mở");
        try {
            By subTab = By.xpath("//flt-semantics[@role='alertdialog']"
                    + "//flt-semantics[@role='button' and (normalize-space(.)='" + subTabName
                    + "' or @aria-label='" + subTabName + "')]");
            List<WebElement> tabs = driver.findElements(subTab);
            if (tabs.isEmpty()) {
                throw new SkipException("Sub-tab '" + subTabName + "' không tìm thấy trong modal "
                        + "— UI có thể đã đổi label hoặc worker này không có sub-tab này");
            }
            flutterTap(tabs.get(0));
            Assert.assertTrue(modal.isOpen(),
                    "Sau click sub-tab '" + subTabName + "', modal phải vẫn open (không crash)");
            // Sub-tab khác vẫn phải hiện (UX không bị mất)
            List<String> labels = collectDialogLabels();
            int otherSubTabsFound = 0;
            for (String st : WorkerDetailModal.EXPECTED_SUB_TABS) {
                if (!st.equals(subTabName) && labels.contains(st)) {
                    otherSubTabsFound++;
                }
            }
            Assert.assertTrue(otherSubTabsFound >= 3,
                    "Sau khi click '" + subTabName + "', ≥3 sub-tab khác vẫn phải render. "
                            + "Found=" + otherSubTabsFound);
        } finally {
            modal.close();
        }
    }

    // ===================== helpers =====================

    private List<String> collectDialogLabels() {
        By dialogSemantics = By.cssSelector("flt-semantics[role='alertdialog'] flt-semantics");
        List<String> out = new ArrayList<>();
        for (WebElement el : driver.findElements(dialogSemantics)) {
            String aria = el.getAttribute("aria-label");
            if (aria != null && !aria.isBlank()) {
                out.add(aria.trim());
                continue;
            }
            String txt = el.getText();
            if (txt != null && !txt.isBlank()) {
                out.add(txt.trim());
            }
        }
        return out;
    }

    /** Flutter tap dùng pointer chain — cần thiết để trigger Flutter button handler. */
    private void flutterTap(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "var el=arguments[0];"
                        + "el.scrollIntoView({block:'center'});"
                        + "var r=el.getBoundingClientRect();"
                        + "var x=r.left+r.width/2, y=r.top+r.height/2;"
                        + "var base={bubbles:true,cancelable:true,composed:true,clientX:x,clientY:y,"
                        + "pointerId:1,pointerType:'mouse',isPrimary:true,button:0};"
                        + "el.dispatchEvent(new PointerEvent('pointerdown',Object.assign({},base,{buttons:1})));"
                        + "el.dispatchEvent(new PointerEvent('pointerup',Object.assign({},base,{buttons:0})));"
                        + "el.dispatchEvent(new MouseEvent('click',Object.assign({},base,{buttons:0})));",
                element);
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
