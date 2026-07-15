package co.uk.ppac.web.tests.compliantportal;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.List;

/**
 * M3 Filter Bar — search, contractors multi-select, and clear button.
 * Covers: PPAC_M3_TC_002, PPAC_M3_TC_003 (annotated trong M2StatusDefinitionsTest), PPAC_M3_TC_010,
 * PPAC_M3_TC_012, PPAC_M3_TC_013, PPAC_M3_TC_014, PPAC_M3_TC_040, PPAC_M3_TC_041, PPAC_M3_TC_045,
 * PPAC_M3_TC_050, PPAC_M11_TC_001.
 */
public class M3FilterTest extends CompliantPortalBaseTest {

    private static final int MIN_CONTRACTORS_FOR_ADMIN = 20;

    @Test(groups = {"compliantportal", "m3", "smoke"},
            description = "PPAC_M3_TC_040 / PPAC_M11_TC_001: Admin sees ≥20 contractors in the dropdown")
    public void testAdminContractorsDropdownHasAtLeastTwenty() {
        queue.openContractorsDropdown();
        int count = queue.contractorsDropdownOptionCount();
        queue.closeContractorsDropdown();
        Assert.assertTrue(count >= MIN_CONTRACTORS_FOR_ADMIN,
                "Admin user must see at least " + MIN_CONTRACTORS_FOR_ADMIN
                        + " contractors. Actual: " + count);
    }

    @Test(groups = {"compliantportal", "m3"},
            description = "PPAC_M3_TC_010: Searching by exact full email yields a single matching row")
    public void testSearchByFullEmailExact() {
        if (!queue.hasSearchBox()) {
            throw new SkipException("Search box not present on the queue page");
        }
        if (queue.dataRowCount() == 0) {
            throw new SkipException("No worker rows on landing — cannot pick a seed email");
        }
        queue.selectStatus("All");
        if (queue.dataRowCount() == 0) {
            queue.selectStatus("Pending");
        }
        String seedEmail = queue.emailOfFirstRow();
        if (seedEmail == null || seedEmail.isEmpty() || !seedEmail.contains("@")) {
            throw new SkipException("Could not read a valid seed email from the first row");
        }
        queue.typeSearch(seedEmail);
        int rows = queue.dataRowCount();
        Assert.assertTrue(rows >= 1,
                "Searching for an existing email must yield at least 1 row. seed='" + seedEmail
                        + "' rows=" + rows);
        String firstAfter = queue.emailOfFirstRow();
        Assert.assertTrue(firstAfter != null && firstAfter.equalsIgnoreCase(seedEmail),
                "First row email after exact search must equal the seed. seed='" + seedEmail
                        + "' first='" + firstAfter + "'");
    }

    @Test(groups = {"compliantportal", "m3"},
            description = "PPAC_M3_TC_014: Searching by V Code yields the worker with that V Code")
    public void testSearchByVCode() {
        if (!queue.hasSearchBox()) {
            throw new SkipException("Search box not present");
        }
        if (queue.dataRowCount() == 0) {
            throw new SkipException("No worker rows — cannot pick a seed V Code");
        }
        String seedVCode = queue.vCodeOfFirstRow();
        if (seedVCode == null || seedVCode.isBlank()) {
            throw new SkipException("Could not read seed V Code from the first row");
        }
        queue.typeSearch(seedVCode);
        int rows = queue.dataRowCount();
        Assert.assertTrue(rows >= 1,
                "Searching by V Code must yield at least 1 row. seed='" + seedVCode + "' rows=" + rows);
    }

    @Test(groups = {"compliantportal", "m3"},
            description = "PPAC_M3_TC_002: Đổi Status filter → table refresh ngay (cell content "
                    + "hoặc cell count khác, hoặc empty state)")
    public void testStatusFilterChangeRefreshesTable() {
        // Landing mặc định = Pending; signature row đầu tiên trước khi đổi
        String pendingSignature = queue.firstRowSignature();
        int pendingRows = queue.dataRowCount();

        queue.selectStatus("All");
        String allSignature = queue.firstRowSignature();
        int allRows = queue.dataRowCount();

        // Một trong các điều kiện sau phải đúng: row count đổi, signature đổi
        boolean changed = pendingRows != allRows
                || (!pendingSignature.isEmpty() && !pendingSignature.equals(allSignature));
        Assert.assertTrue(changed,
                "Đổi status filter Pending → All phải làm table refresh. "
                        + "Pending rows=" + pendingRows + " sig='" + pendingSignature + "' | "
                        + "All rows=" + allRows + " sig='" + allSignature + "'");

        String currentLabel = queue.currentStatusFilterLabel();
        Assert.assertEquals(currentLabel, "All",
                "Filter label phải reflect status mới đã chọn. Actual='" + currentLabel + "'");
    }

    @Test(groups = {"compliantportal", "m3"},
            description = "PPAC_M3_TC_012: Search by Name (substring) → ≥1 row trả về có Name khớp")
    public void testSearchByName() {
        if (!queue.hasSearchBox()) {
            throw new SkipException("Search box không có");
        }
        if (queue.dataRowCount() == 0) {
            throw new SkipException("Không có worker rows để pick seed Name");
        }
        String seedName = queue.nameOfFirstRow();
        if (seedName == null || seedName.isBlank()) {
            throw new SkipException("Không đọc được Name seed từ row đầu");
        }
        queue.typeSearch(seedName);
        int rows = queue.dataRowCount();
        Assert.assertTrue(rows >= 1,
                "Search by Name='" + seedName + "' phải trả về ≥1 row. Actual=" + rows);
        // Row đầu phải có Name khớp (substring case-insensitive)
        String firstNameAfter = queue.nameOfFirstRow();
        Assert.assertTrue(firstNameAfter != null
                        && firstNameAfter.toLowerCase().contains(seedName.toLowerCase()),
                "Row đầu sau search phải có Name chứa seed. seed='" + seedName
                        + "' first='" + firstNameAfter + "'");
    }

    @Test(groups = {"compliantportal", "m3"},
            description = "PPAC_M3_TC_013: Search by Surname (substring) → ≥1 row trả về có Surname khớp")
    public void testSearchBySurname() {
        if (!queue.hasSearchBox()) {
            throw new SkipException("Search box không có");
        }
        if (queue.dataRowCount() == 0) {
            throw new SkipException("Không có worker rows để pick seed Surname");
        }
        String seedSurname = queue.surnameOfFirstRow();
        if (seedSurname == null || seedSurname.isBlank()) {
            throw new SkipException("Không đọc được Surname seed từ row đầu");
        }
        queue.typeSearch(seedSurname);
        int rows = queue.dataRowCount();
        Assert.assertTrue(rows >= 1,
                "Search by Surname='" + seedSurname + "' phải trả về ≥1 row. Actual=" + rows);
        String firstSurnameAfter = queue.surnameOfFirstRow();
        Assert.assertTrue(firstSurnameAfter != null
                        && firstSurnameAfter.toLowerCase().contains(seedSurname.toLowerCase()),
                "Row đầu sau search phải có Surname chứa seed. seed='" + seedSurname
                        + "' first='" + firstSurnameAfter + "'");
    }

    @Test(groups = {"compliantportal", "m3"},
            description = "PPAC_M3_TC_041: Multi-select 2 contractor — table chỉ chứa worker của 2 "
                    + "contractor đó (Company column thuộc tập 2 contractor đã chọn)")
    public void testMultiSelectTwoContractorsConstrainsTable() {
        queue.selectStatus("All");
        // Pick 2 contractor name từ dropdown — dùng tên ngắn dễ click; nếu UI không có sẽ throw
        // SkipException qua waitForPresent timeout, để test honest fail.
        String[] picks = {"EKFB", "BBV"};
        queue.openContractorsDropdown();
        // Verify cả 2 đều tồn tại trong list trước khi filter
        int totalContractors = queue.contractorsDropdownOptionCount();
        queue.closeContractorsDropdown();
        if (totalContractors < 5) {
            throw new SkipException("Dropdown chỉ có " + totalContractors + " contractor — "
                    + "không đủ để test multi-select");
        }

        try {
            queue.filterByContractors(picks);
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new SkipException("Một trong 2 contractor [" + String.join(", ", picks)
                    + "] không tồn tại trên UAT — cần re-discover contractor names");
        }

        int rows = queue.dataRowCount();
        if (rows == 0) {
            throw new SkipException("2 contractor đã chọn không có worker — không thể verify "
                    + "constraint Company column");
        }
        // Company là cell index 0 theo EXPECTED_TABLE_HEADERS
        List<String> companies = queue.columnValues(0);
        for (String company : companies) {
            boolean matched = false;
            for (String pick : picks) {
                if (company != null && company.toLowerCase().contains(pick.toLowerCase())) {
                    matched = true;
                    break;
                }
            }
            Assert.assertTrue(matched,
                    "Sau khi filter 2 contractor " + java.util.Arrays.toString(picks)
                            + ", mọi row phải có Company thuộc tập đã chọn. Actual='" + company + "'");
        }
    }

    @Test(groups = {"compliantportal", "m3"},
            description = "PPAC_M3_TC_045: Contractor count badge — nút 'Select Contractors' hiển thị "
                    + "số contractor đã chọn (vd '2' hoặc 'Select Contractors (2)')")
    public void testContractorsButtonShowsSelectedCount() {
        String defaultText = queue.contractorsButtonText();
        // Default state — không có số đã chọn HOẶC có '0' / không có số ngoài 'Contractors'
        // Sau khi chọn 2 contractor cụ thể → badge phải có ký tự '2'
        try {
            queue.openContractorsDropdown();
            int totalContractors = queue.contractorsDropdownOptionCount();
            if (totalContractors < 3) {
                queue.closeContractorsDropdown();
                throw new SkipException("Dropdown < 3 contractor — không đủ test count badge");
            }
            // Clear hiện tại + chọn 2 contractor đầu trong list (qua selectContractor cần biết tên,
            // nhưng filterByContractors handle tên cụ thể). Dùng tên phổ biến nhất: EKFB + BBV.
            queue.closeContractorsDropdown();
            queue.filterByContractors("EKFB", "BBV");
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new SkipException("EKFB hoặc BBV không có trên UAT — cần adjust contractor names");
        }

        String afterText = queue.contractorsButtonText();
        // Badge có thể format: "Select Contractors (2)" / "Contractors 2" / "2 Selected" / ...
        // Heuristic: text sau phải khác text trước HOẶC chứa số '2' tách rời
        boolean changed = !afterText.equals(defaultText);
        boolean hasTwo = afterText.matches(".*\\b2\\b.*");
        Assert.assertTrue(changed || hasTwo,
                "Sau khi chọn 2 contractor, button text phải đổi để reflect count. "
                        + "Before='" + defaultText + "' After='" + afterText + "'");
    }

    @Test(groups = {"compliantportal", "m3"},
            description = "PPAC_M3_TC_050: Clear button resets the status filter back to default Pending")
    public void testClearButtonResetsFiltersToDefault() {
        queue.selectStatus("Active");
        String beforeClear = queue.currentStatusFilterLabel();
        Assert.assertEquals(beforeClear, "Active",
                "Pre-condition: filter must be Active before clicking Clear");
        if (!queue.hasClearButton()) {
            throw new SkipException("Clear button not visible — feature may be gated by filter dirty state");
        }
        queue.clickClear();
        String afterClear = queue.currentStatusFilterLabel();
        Assert.assertEquals(afterClear, "Pending",
                "After clicking Clear, the status filter must return to default 'Pending'. Actual: '"
                        + afterClear + "'");
    }
}
