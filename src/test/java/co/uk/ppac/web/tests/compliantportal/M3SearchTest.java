package co.uk.ppac.web.tests.compliantportal;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

/**
 * M3.2 Search Textbox — deep coverage.
 * Covers: PPAC_M3_TC_011, _012, _013, _015, _016, _017.
 * (PPAC_M3_TC_010 exact-email & _014 V-Code đã nằm ở {@link M3FilterTest}.)
 *
 * Recon (2026-05-20): ô Search là thẻ &lt;input aria-label="Search"&gt; thật;
 * KPI "Total" phản ánh đúng tổng kết quả search (không bị giới hạn bởi page-size).
 */
public class M3SearchTest extends CompliantPortalBaseTest {

    @Test(groups = {"compliantportal", "m3", "search"},
            description = "PPAC_M3_TC_011: Search bằng partial Email — xác minh fuzzy/exact behavior")
    public void testSearchByPartialEmailIsFuzzy() {
        if (!queue.hasSearchBox()) {
            throw new SkipException("Search box không hiển thị trên trang queue");
        }
        String seedEmail = queue.emailOfFirstRow();
        if (seedEmail == null || !seedEmail.contains("@")) {
            throw new SkipException("Không đọc được email hợp lệ từ row đầu tiên");
        }
        String prefix = seedEmail.substring(0, seedEmail.indexOf('@'));
        if (prefix.length() > 6) {
            prefix = prefix.substring(0, 6);
        }
        queue.typeSearch(prefix);
        int total = queue.kpiCount("Total");
        Assert.assertTrue(total >= 1,
                "Search partial email '" + prefix + "' phải trả về ≥1 kết quả nếu app dùng fuzzy match. "
                        + "Total=" + total + " (nếu =0 → app dùng exact match, cần document)");
        Assert.assertTrue(queue.emailOfFirstRow().toLowerCase().contains(prefix.toLowerCase()),
                "Row đầu tiên phải chứa chuỗi đã search. prefix='" + prefix
                        + "' email='" + queue.emailOfFirstRow() + "'");
    }

    @Test(groups = {"compliantportal", "m3", "search"},
            description = "PPAC_M3_TC_012: Search theo Name")
    public void testSearchByName() {
        if (!queue.hasSearchBox()) {
            throw new SkipException("Search box không hiển thị");
        }
        String seedName = queue.nameOfFirstRow();
        if (seedName == null || seedName.isBlank() || "-".equals(seedName)) {
            throw new SkipException("Row đầu tiên không có Name hợp lệ để làm seed");
        }
        queue.typeSearch(seedName);
        int total = queue.kpiCount("Total");
        Assert.assertTrue(total >= 1,
                "Search theo Name '" + seedName + "' phải trả về ≥1 row. Total=" + total);
        Assert.assertTrue(queue.nameOfFirstRow().equalsIgnoreCase(seedName),
                "Row đầu tiên sau khi search Name phải khớp seed. seed='" + seedName
                        + "' actual='" + queue.nameOfFirstRow() + "'");
    }

    @Test(groups = {"compliantportal", "m3", "search"},
            description = "PPAC_M3_TC_013: Search theo Surname")
    public void testSearchBySurname() {
        if (!queue.hasSearchBox()) {
            throw new SkipException("Search box không hiển thị");
        }
        String seedSurname = queue.surnameOfFirstRow();
        if (seedSurname == null || seedSurname.isBlank() || "-".equals(seedSurname)) {
            throw new SkipException("Row đầu tiên không có Surname hợp lệ để làm seed");
        }
        queue.typeSearch(seedSurname);
        int total = queue.kpiCount("Total");
        Assert.assertTrue(total >= 1,
                "Search theo Surname '" + seedSurname + "' phải trả về ≥1 row. Total=" + total);
    }

    @Test(groups = {"compliantportal", "m3", "search"},
            description = "PPAC_M3_TC_015: Search case-insensitive (Email viết HOA)")
    public void testSearchIsCaseInsensitive() {
        if (!queue.hasSearchBox()) {
            throw new SkipException("Search box không hiển thị");
        }
        String seedEmail = queue.emailOfFirstRow();
        if (seedEmail == null || !seedEmail.contains("@")) {
            throw new SkipException("Không đọc được email hợp lệ từ row đầu tiên");
        }
        String token = seedEmail.substring(0, seedEmail.indexOf('@'));
        if (token.length() > 6) {
            token = token.substring(0, 6);
        }
        queue.typeSearch(token.toUpperCase());
        int total = queue.kpiCount("Total");
        Assert.assertTrue(total >= 1,
                "Search '" + token.toUpperCase() + "' (viết HOA) phải trả về ≥1 row nếu search "
                        + "case-insensitive. Total=" + total + " (nếu =0 → app case-sensitive, cần document)");
    }

    @Test(groups = {"compliantportal", "m3", "search"},
            description = "PPAC_M3_TC_016: Search rỗng → trả về toàn bộ list theo filter status")
    public void testEmptySearchReturnsFullList() {
        if (!queue.hasSearchBox()) {
            throw new SkipException("Search box không hiển thị");
        }
        int fullTotal = queue.kpiCount("Total");
        Assert.assertTrue(fullTotal > 0,
                "Pre-condition: landing phải có dữ liệu. Total=" + fullTotal);

        queue.typeSearch("aashigaur");
        int filteredTotal = queue.kpiCount("Total");
        Assert.assertTrue(filteredTotal <= fullTotal,
                "Sau khi search, Total phải ≤ Total ban đầu. filtered=" + filteredTotal
                        + " full=" + fullTotal);

        queue.clearSearch();
        int restoredTotal = queue.kpiCount("Total");
        Assert.assertEquals(restoredTotal, fullTotal,
                "Sau khi xóa search, Total phải trở về giá trị đầy đủ ban đầu. restored="
                        + restoredTotal + " full=" + fullTotal);
    }

    @Test(groups = {"compliantportal", "m3", "search", "security"},
            description = "PPAC_M3_TC_017: Search payload SQLi/XSS — không break query, không execute")
    public void testSearchRejectsSqliAndXssPayloads() {
        if (!queue.hasSearchBox()) {
            throw new SkipException("Search box không hiển thị");
        }
        int fullTotal = queue.kpiCount("Total");

        queue.typeSearch("'; DROP TABLE workers; --");
        int sqliTotal = queue.kpiCount("Total");
        Assert.assertEquals(sqliTotal, 0,
                "Payload SQLi phải được xử lý như literal text → 0 kết quả, query không bị break. Total="
                        + sqliTotal);

        queue.clearSearch();
        queue.typeSearch("<script>alert(1)</script>");
        // Test chạy tiếp được (không UnhandledAlertException) = không có alert popup → script KHÔNG execute.
        int xssTotal = queue.kpiCount("Total");
        Assert.assertTrue(xssTotal == 0 || xssTotal == fullTotal,
                "Payload XSS phải được vô hiệu hóa an toàn: khớp như literal text (0 kết quả) "
                        + "HOẶC bị strip thành chuỗi rỗng (" + fullTotal + " kết quả). "
                        + "Không có alert popup = script không execute. DOCUMENTED: Total=" + xssTotal);

        queue.clearSearch();
        Assert.assertEquals(queue.kpiCount("Total"), fullTotal,
                "Sau khi xóa payload, danh sách phải phục hồi đầy đủ — app không bị hỏng bởi injection");
    }
}
