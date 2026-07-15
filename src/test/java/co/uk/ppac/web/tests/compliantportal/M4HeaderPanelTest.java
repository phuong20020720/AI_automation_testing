package co.uk.ppac.web.tests.compliantportal;

import co.uk.ppac.web.pages.WorkerDetailModal;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * M4 Worker Detail Modal — read-only header bar + info panel.
 * Covers: PPAC_M4_TC_001 (9 header fields), PPAC_M4_TC_030 (9 panel fields).
 */
public class M4HeaderPanelTest extends CompliantPortalBaseTest {

    @Test(groups = {"compliantportal", "m4", "smoke"},
            description = "PPAC_M4_TC_001: Modal header hiển thị đủ 9 field read-only "
                    + "(Company, Type, Email, DOB, V Code, Skill Card, Job Role, Status, Failed Reason)")
    public void testHeaderShowsNineReadOnlyFields() {
        if (queue.dataRowCount() == 0) {
            throw new SkipException("Không có worker rows để mở modal");
        }
        WorkerDetailModal modal = queue.openFirstWorker();
        Assert.assertTrue(modal.isOpen(), "Modal phải mở để inspect header");

        try {
            List<String> presentLabels = collectDialogSemanticLabels();
            List<String> missing = new ArrayList<>();
            for (String expected : WorkerDetailModal.EXPECTED_HEADER_FIELDS) {
                if (!presentLabels.contains(expected)) {
                    missing.add(expected);
                }
            }
            Assert.assertTrue(missing.isEmpty(),
                    "Header phải có đủ 9 field. Missing=" + missing
                            + " | Found semantics labels=" + presentLabels);
        } finally {
            modal.close();
        }
    }

    @Test(groups = {"compliantportal", "m4", "smoke"},
            description = "PPAC_M4_TC_030: Info panel hiển thị đủ 9 field "
                    + "(Name, Surname, Reusable Passport, Nationality, Site Location, Sub Contractor, "
                    + "NI Number, Share Code, Card Number)")
    public void testInfoPanelShowsNineFields() {
        if (queue.dataRowCount() == 0) {
            throw new SkipException("Không có worker rows để mở modal");
        }
        WorkerDetailModal modal = queue.openFirstWorker();
        Assert.assertTrue(modal.isOpen(), "Modal phải mở để inspect info panel");

        try {
            List<String> presentLabels = collectDialogSemanticLabels();
            List<String> missing = new ArrayList<>();
            for (String expected : WorkerDetailModal.EXPECTED_INFO_FIELDS) {
                if (!presentLabels.contains(expected)) {
                    missing.add(expected);
                }
            }
            Assert.assertTrue(missing.isEmpty(),
                    "Info panel phải có đủ 9 field. Missing=" + missing
                            + " | Found semantics labels=" + presentLabels);
        } finally {
            modal.close();
        }
    }

    /** Collect tất cả label / textContent từ semantic nodes trong alertdialog hiện tại. */
    private List<String> collectDialogSemanticLabels() {
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
}
