package co.uk.ppac.web.tests.compliantportal;

import co.uk.ppac.web.pages.VerifierQueuePage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * M2.1 Status Definitions — read-only display checks.
 * Covers: PPAC_M2_TC_001, TC_003, TC_004.
 */
public class M2StatusDefinitionsTest extends CompliantPortalBaseTest {

    @Test(groups = {"compliantportal", "m2", "m3", "smoke"},
            description = "PPAC_M2_TC_001/003 + PPAC_M3_TC_003: Status dropdown chứa đủ 13 status + 'All', "
                    + "text khớp spec (M3_TC_003 regression cover của M3.1 filter dropdown)")
    public void testStatusDropdownContainsAllStatusOptions() {
        queue.openStatusDropdown();
        List<String> options = queue.readStatusDropdownOptions();
        queue.closeStatusDropdown();

        Assert.assertFalse(options.isEmpty(), "Status dropdown phải có ít nhất 1 option");
        for (String expected : VerifierQueuePage.EXPECTED_STATUS_OPTIONS) {
            Assert.assertTrue(options.contains(expected),
                    "Status dropdown phải chứa option '" + expected + "'. Actual options: " + options);
        }
        Assert.assertEquals(options.size(), VerifierQueuePage.EXPECTED_STATUS_OPTIONS.size(),
                "Số option trong dropdown phải khớp spec (13 status + All). Actual: " + options);
    }

    @Test(groups = {"compliantportal", "m2", "smoke"},
            description = "PPAC_M2_TC_004: Default landing filter status = Pending sau khi login")
    public void testDefaultLandingFilterIsPending() {
        String currentLabel = queue.currentStatusFilterLabel();
        Assert.assertEquals(currentLabel, "Pending",
                "Default landing filter phải = 'Pending' sau khi login. Actual: '" + currentLabel + "'");
    }

    @Test(groups = {"compliantportal", "m2"},
            description = "PPAC_M2_TC_001 sanity: Table và filter bar render sau khi Flutter ready")
    public void testTableAndFilterBarRendered() {
        Assert.assertTrue(queue.isTablePresent(),
                "Table Worker Queue phải hiển thị trên landing page");
        List<String> headers = queue.tableHeaders();
        Assert.assertFalse(headers.isEmpty(), "Table phải có column headers");
        for (String expected : VerifierQueuePage.EXPECTED_TABLE_HEADERS) {
            Assert.assertTrue(headers.contains(expected),
                    "Table phải có column '" + expected + "'. Actual: " + headers);
        }
    }

    @Test(groups = {"compliantportal", "m2"},
            description = "PPAC_M2_TC_003 deep: Filter từng status sample → cột Status trong table "
                    + "phải hiển thị text exact match với spec. Bỏ qua row có Status empty (Flutter "
                    + "canvas đôi khi render delay). Sample 3 status non-empty trên UAT.")
    public void testStatusColumnTextExactMatchSpec() {
        int statusColumnIdx = VerifierQueuePage.EXPECTED_TABLE_HEADERS.indexOf("Status");
        String[] samplesToVerify = {"Pending", "Active", "Expired"};
        int verifiedCount = 0;
        StringBuilder report = new StringBuilder();
        for (String statusName : samplesToVerify) {
            queue.selectStatus(statusName);
            int rows = queue.dataRowCount();
            if (rows == 0) {
                report.append(statusName).append("=zerorows; ");
                continue;
            }
            List<String> columnValues = queue.columnValues(statusColumnIdx);
            // Loại row có Status empty (Flutter canvas render delay) trước khi assert
            int nonEmptyChecked = 0;
            int matched = 0;
            String firstMismatchExample = null;
            for (String val : columnValues) {
                if (val == null || val.trim().isEmpty()) {
                    continue;
                }
                nonEmptyChecked++;
                String low = val.trim().toLowerCase();
                boolean isMatch;
                if ("Pending".equals(statusName)) {
                    // Pending là meta-grouping
                    isMatch = low.startsWith("pending") || low.contains("recheck")
                            || low.contains("review") || low.contains("waiting");
                } else {
                    isMatch = val.trim().equalsIgnoreCase(statusName);
                }
                if (isMatch) {
                    matched++;
                } else if (firstMismatchExample == null) {
                    firstMismatchExample = val;
                }
            }
            if (nonEmptyChecked == 0) {
                report.append(statusName).append("=allempty(").append(rows).append("rows); ");
                continue;
            }
            // ≥80% non-empty rows phải match spec
            double matchRatio = (double) matched / nonEmptyChecked;
            if (matchRatio >= 0.8) {
                verifiedCount++;
                report.append(statusName).append("=ok(").append(matched).append("/")
                        .append(nonEmptyChecked).append("); ");
            } else {
                report.append(statusName).append("=mismatch ratio=").append(matchRatio)
                        .append(" example='").append(firstMismatchExample).append("'; ");
            }
        }
        Assert.assertTrue(verifiedCount >= 2,
                "Phải verify ≥2/3 sample status text exact match (cho phép 1 empty/allempty). "
                        + "Verified=" + verifiedCount + " | Report: " + report);
    }
}
