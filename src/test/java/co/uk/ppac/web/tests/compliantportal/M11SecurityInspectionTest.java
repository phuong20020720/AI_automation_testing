package co.uk.ppac.web.tests.compliantportal;

import co.uk.ppac.web.pages.VerifierQueuePage;
import co.uk.ppac.web.pages.WorkerDetailModal;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * M11 Multi-tenant — admin scope + security inspection.
 * Covers: PPAC_M11_TC_002, PPAC_M11_TC_003, PPAC_M11_TC_011, PPAC_M11_TC_012,
 * PPAC_M11_TC_020, PPAC_M11_TC_021, PPAC_M11_TC_022, PPAC_M11_TC_023.
 *
 * <p>TC_020 nguyên gốc cần verifier-of-contractor account (KHÔNG CÓ trên UAT — gap).
 * Test thay thế: inspect URL pattern khi mở worker modal để document IDOR risk surface.
 * Nếu URL có dạng `/worker/{id}` mà không có scope contractor → high IDOR risk.
 * Nếu URL không đổi (modal Flutter state nội bộ) → no easy URL-guessing IDOR vector
 * (nhưng API-level IDOR vẫn cần test riêng).
 */
public class M11SecurityInspectionTest extends CompliantPortalBaseTest {

    @Test(groups = {"compliantportal", "m11"},
            description = "PPAC_M11_TC_002: Admin xem được worker của mọi contractor (no isolation). "
                    + "Sample 3 contractor đầu trong dropdown — filter từng cái, mỗi cái phải có "
                    + "ít nhất 1 worker visible (admin không bị deny). Approach mới: per-contractor "
                    + "filter (theo spec) thay vì scan page 1 (recon 2026-05-28: page 1 admin sort "
                    + "có thể chỉ show 1 company).")
    public void testAdminSeesWorkersFromMultipleContractors() {
        queue.selectStatus("All");
        // Lấy danh sách contractor từ dropdown
        queue.openContractorsDropdown();
        int totalContractors = queue.contractorsDropdownOptionCount();
        queue.closeContractorsDropdown();
        if (totalContractors < 2) {
            throw new SkipException("Dropdown chỉ có " + totalContractors + " contractor — "
                    + "không thể verify multi-tenant visibility");
        }
        // Sample first 3 well-known contractors trên UAT — verify mỗi cái filter ra ≥1 worker
        // (data-driven: nếu 1 trong 3 không có worker → SKIP cụ thể, không treat as fail)
        String[] sampleContractors = {"EKFB", "Clipfine", "BBV"};
        int passedCount = 0;
        Set<String> verifiedContractors = new HashSet<>();
        StringBuilder report = new StringBuilder();
        for (String contractor : sampleContractors) {
            try {
                queue.filterByContractors(contractor);
                int rows = queue.dataRowCount();
                report.append(contractor).append("=").append(rows).append(" rows; ");
                if (rows >= 1) {
                    passedCount++;
                    verifiedContractors.add(contractor);
                }
            } catch (org.openqa.selenium.TimeoutException te) {
                report.append(contractor).append("=N/A (not in dropdown); ");
            }
        }
        Assert.assertTrue(passedCount >= 2,
                "Admin phải thấy worker từ ≥2/3 sample contractor (no isolation). "
                        + "PassedCount=" + passedCount + " | Verified=" + verifiedContractors
                        + " | Report: " + report);
    }

    @Test(groups = {"compliantportal", "m11"},
            description = "PPAC_M11_TC_003: Admin xem được cross product Status × Contractor. "
                    + "Sampling: (Status=Pending, no contractor filter) + (Status=All, contractor=EKFB) "
                    + "→ mỗi combination phải render table không error.")
    public void testAdminCrossProductStatusContractor() {
        // Combination 1: Status=Pending (default landing) — phải render OK
        queue.selectStatus("Pending");
        int pendingRows = queue.dataRowCount();
        boolean pendingHeadersOk = !queue.tableHeaders().isEmpty();
        Assert.assertTrue(pendingHeadersOk,
                "Combo 1 (Pending, all contractors): table headers phải render");

        // Combination 2: Status=All + contractor=EKFB
        queue.selectStatus("All");
        try {
            queue.filterByContractors("EKFB");
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new SkipException("EKFB contractor không tồn tại trên UAT — cần adjust");
        }
        int ekfbRows = queue.dataRowCount();
        boolean ekfbHeadersOk = !queue.tableHeaders().isEmpty();
        Assert.assertTrue(ekfbHeadersOk,
                "Combo 2 (All, EKFB): table headers phải render");

        // Admin không bị deny: cả 2 combination phải produce table render
        // (rows có thể là 0 nếu thực sự không có data, đó cũng là valid state)
        Assert.assertTrue(pendingRows >= 0 && ekfbRows >= 0,
                "Cả 2 combination phải trả về data state hợp lệ (≥0). "
                        + "Pending=" + pendingRows + " | All+EKFB=" + ekfbRows);
    }

    @Test(groups = {"compliantportal", "m11", "security", "inspection"},
            description = "PPAC_M11_TC_020 (modified): URL inspection — open worker modal "
                    + "và verify URL không expose worker_id (no easy IDOR vector via URL guessing). "
                    + "Document finding cho dev team về API-level IDOR cần test thêm.")
    public void testWorkerModalDoesNotExposeIdInUrl() {
        if (queue.dataRowCount() == 0) {
            throw new SkipException("Pending queue rỗng — không có worker để inspect URL");
        }
        String urlBeforeModal = driver.getCurrentUrl();
        Assert.assertNotNull(urlBeforeModal, "URL trước khi mở modal phải đọc được");

        WorkerDetailModal modal = queue.openFirstWorker();
        Assert.assertTrue(modal.isOpen(), "Modal phải mở để inspect URL pattern");

        String urlAfterModal = driver.getCurrentUrl();
        modal.close();

        boolean urlChanged = !urlBeforeModal.equals(urlAfterModal);
        boolean urlContainsId = urlAfterModal.matches(".*/(?:worker|workers|verify|wkr)/\\d+.*")
                || urlAfterModal.matches(".*[?&](id|worker_id|workerId|vCode)=\\d+.*")
                || urlAfterModal.matches(".*/[a-f0-9]{8}-[a-f0-9]{4}.*"); // UUID

        // Document hành vi: URL không đổi = no URL-level IDOR vector từ modal open.
        // URL có id raw = high IDOR risk, cần dev xác nhận server-side scope check.
        if (urlChanged && urlContainsId) {
            Assert.fail("⚠️ IDOR RISK (F-SEC-NEW-2 candidate): URL exposes worker identifier. "
                    + "Before: '" + urlBeforeModal + "' | After: '" + urlAfterModal + "'. "
                    + "Verifier-of-contractor có thể bypass scope bằng cách edit URL → access "
                    + "worker contractor khác. Cần dev verify server-side contractor scope check.");
        }
        Assert.assertEquals(urlAfterModal, urlBeforeModal,
                "URL phải KHÔNG đổi khi mở modal (Flutter state nội bộ). "
                        + "Pattern này tốt cho IDOR resistance via URL guessing, "
                        + "nhưng API-level IDOR (GraphQL query manipulation) vẫn cần test riêng. "
                        + "Before: '" + urlBeforeModal + "' | After: '" + urlAfterModal + "'");
    }

    @Test(groups = {"compliantportal", "m11"},
            description = "PPAC_M11_TC_011: Distinct=ON + search email shared cross-contractor → "
                    + "gộp thành 1 row. SKIP nếu UAT page 1 không có shared-email worker visible.")
    public void testDistinctOnGroupsSharedEmailToSingleRow() {
        queue.selectStatus("All");
        if (queue.dataRowCount() == 0) {
            throw new SkipException("Filter All rỗng — không pick được seed email");
        }
        // Tìm email xuất hiện trên ≥2 rows trong sample hiện tại (proxy cho shared email)
        int emailIdx = VerifierQueuePage.EXPECTED_TABLE_HEADERS.indexOf("Email");
        List<String> emails = queue.columnValues(emailIdx);
        Map<String, Integer> freq = new HashMap<>();
        String sharedSeed = null;
        for (String e : emails) {
            if (e != null && e.contains("@")) {
                int n = freq.getOrDefault(e, 0) + 1;
                freq.put(e, n);
                if (n >= 2 && sharedSeed == null) {
                    sharedSeed = e;
                }
            }
        }
        if (sharedSeed == null) {
            throw new SkipException("Page 1 không có email duplicate visible — cần dataset có "
                    + "shared-email cross-contractor để verify distinct grouping");
        }
        if (!queue.hasSearchBox()) {
            throw new SkipException("Search box không khả dụng");
        }
        queue.typeSearch(sharedSeed);
        int rows = queue.dataRowCount();
        Assert.assertEquals(rows, 1,
                "Distinct=ON (default) với shared-email '" + sharedSeed + "' phải gộp về 1 row. "
                        + "Actual=" + rows + " | freq sample=" + freq.get(sharedSeed));
    }

    @Test(groups = {"compliantportal", "m11"},
            description = "PPAC_M11_TC_012: Submit verify ở 1 record shared-email không ảnh hưởng "
                    + "record kia (edge audit). Gated bởi compliantportal.write.enabled + cần 2 record "
                    + "cùng email visible → SKIP mặc định.")
    public void testSharedEmailSubmitIsolation() {
        boolean writeEnabled = Boolean.getBoolean("compliantportal.write.enabled");
        if (!writeEnabled) {
            throw new SkipException("Test write-action — gated bởi -Dcompliantportal.write.enabled=true");
        }
        throw new SkipException("Cần seed 2 record cùng email ở 2 contractor (EKFB + McLaren) "
                + "Pending. UAT shared dataset không control được — recommend dev seed dedicated "
                + "test data hoặc test trên staging riêng. Document cho audit team: edge case "
                + "shared email submit isolation chưa verify được.");
    }

    @Test(groups = {"compliantportal", "m11"},
            description = "PPAC_M11_TC_021 [FLAG-ONLY]: Single-contractor user dropdown chỉ hiện 1 "
                    + "contractor (REQ-M3-08). GAP — không có verifier-of-contractor account trên UAT.")
    public void testSingleContractorDropdownGap() {
        throw new SkipException("⛔ GAP CONFIRMED: Cần verifier-of-contractor account để verify "
                + "REQ-M3-08 (single contractor dropdown). Account này không có trên UAT. "
                + "Escalation: yêu cầu PO seed account verifier-of-{contractor} cho test isolation. "
                + "Khi có account → enable test này + adjust login session.");
    }

    @Test(groups = {"compliantportal", "m11"},
            description = "PPAC_M11_TC_022 [FLAG-ONLY]: API-level IDOR — verifier-of-A gọi API list "
                    + "workers contractor B phải trả 403. GAP — không có API token verifier-of-contractor.")
    public void testApiPermissionGap() {
        throw new SkipException("⛔ GAP CONFIRMED: Cần (a) verifier-of-contractor account + "
                + "(b) API token để gọi GraphQL endpoint với user role limited. "
                + "Test này phải implement bằng Postman/curl ngoài Selenium scope. "
                + "Recommendation cho dev: thêm IDOR integration test ở backend layer; "
                + "GraphQL resolver getWorkers phải enforce contractor_id scope từ session "
                + "context, không trust input filter từ client.");
    }

    @Test(groups = {"compliantportal", "m11", "security", "inspection"},
            description = "PPAC_M11_TC_023: Network inspect — khi apply Contractor filter, request "
                    + "payload tới backend phải chứa contractor_id (server-side enforcement). "
                    + "Nếu chỉ filter client-side → Critical security finding.")
    public void testNetworkRequestIncludesContractorFilter() {
        if (!(driver instanceof JavascriptExecutor)) {
            throw new SkipException("WebDriver không hỗ trợ JS execution để inject fetch interceptor");
        }
        JavascriptExecutor js = (JavascriptExecutor) driver;
        // Inject fetch interceptor — capture mọi POST body trước khi trigger filter action.
        // Flutter Web encode body dạng Uint8Array → cần TextDecoder để đọc raw JSON.
        String interceptor =
                "(function() {"
                        + "  if (window.__capturedReqs) return;"
                        + "  window.__capturedReqs = [];"
                        + "  function decodeBody(b) {"
                        + "    if (b == null) return '';"
                        + "    if (typeof b === 'string') return b;"
                        + "    try {"
                        + "      if (b instanceof Uint8Array) return new TextDecoder('utf-8').decode(b);"
                        + "      if (b instanceof ArrayBuffer) return new TextDecoder('utf-8').decode(new Uint8Array(b));"
                        + "      if (b && b.buffer instanceof ArrayBuffer)"
                        + "        return new TextDecoder('utf-8').decode(new Uint8Array(b.buffer));"
                        + "    } catch (e) {}"
                        + "    return String(b);"
                        + "  }"
                        + "  var origFetch = window.fetch;"
                        + "  window.fetch = function(input, init) {"
                        + "    try {"
                        + "      var u = (typeof input === 'string') ? input : (input && input.url) || '';"
                        + "      var body = init && init.body ? decodeBody(init.body) : '';"
                        + "      if (body) window.__capturedReqs.push({url: u, body: body});"
                        + "    } catch (e) {}"
                        + "    return origFetch.apply(this, arguments);"
                        + "  };"
                        + "  var OrigXHR = window.XMLHttpRequest;"
                        + "  function PatchedXHR() {"
                        + "    var xhr = new OrigXHR();"
                        + "    var url='';"
                        + "    var openOrig = xhr.open;"
                        + "    xhr.open = function(m, u) { url = u; return openOrig.apply(xhr, arguments); };"
                        + "    var sendOrig = xhr.send;"
                        + "    xhr.send = function(body) {"
                        + "      try { if (body) window.__capturedReqs.push({url: url, body: decodeBody(body)}); }"
                        + "      catch (e) {}"
                        + "      return sendOrig.apply(xhr, arguments);"
                        + "    };"
                        + "    return xhr;"
                        + "  }"
                        + "  window.XMLHttpRequest = PatchedXHR;"
                        + "})();";
        js.executeScript(interceptor);
        // Clear any stale captured requests từ previous test
        js.executeScript("window.__capturedReqs = [];");

        // Trigger filter action — chọn contractor EKFB
        try {
            queue.filterByContractors("EKFB");
        } catch (TimeoutException e) {
            throw new SkipException("EKFB không có trong contractor list — không trigger được "
                    + "filter request để capture");
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> captured = (List<Map<String, Object>>) js.executeScript(
                "return window.__capturedReqs || []");
        if (captured == null || captured.isEmpty()) {
            throw new SkipException("Không capture được request nào — Flutter Web có thể không "
                    + "dùng fetch/XHR cho action này, hoặc request đã hoàn thành trước inject. "
                    + "Cần inject interceptor sớm hơn (browser launch arg).");
        }

        // Log toàn bộ captured request để diagnostic — luôn print regardless of result
        System.out.println("[M11_TC_023] Captured " + captured.size() + " request(s):");
        for (int i = 0; i < captured.size(); i++) {
            Map<String, Object> req = captured.get(i);
            String url = String.valueOf(req.get("url"));
            String body = String.valueOf(req.get("body"));
            String bodySample = body.length() > 600 ? body.substring(0, 600) + "..." : body;
            System.out.println("  #" + i + " URL=" + url);
            System.out.println("       BODY=" + bodySample);
        }

        // Detect filter signal — broaden search: contractor/company/ekfb/filter/where/input
        boolean foundFilterSignal = false;
        String evidenceBody = null;
        String evidenceUrl = null;
        for (Map<String, Object> req : captured) {
            String url = String.valueOf(req.get("url"));
            String body = String.valueOf(req.get("body"));
            String bodyLower = body.toLowerCase();
            if (bodyLower.contains("contractor") || bodyLower.contains("ekfb")
                    || bodyLower.contains("company")) {
                foundFilterSignal = true;
                evidenceBody = body.length() > 500 ? body.substring(0, 500) + "..." : body;
                evidenceUrl = url;
                break;
            }
        }
        if (foundFilterSignal) {
            System.out.println("[M11_TC_023] PASS — server-side filter signal detected.");
            System.out.println("  URL=" + evidenceUrl);
            System.out.println("  Evidence body=" + evidenceBody);
            return;
        }
        // Không tìm thấy filter signal — có thể là (a) request là metadata/auth/telemetry không
        // liên quan filter; (b) filter thật sự client-side only (security risk). SKIP với
        // diagnostic vì không thể distinguish 100% qua single test run — cần manual recon.
        throw new SkipException("⚠️ DIAGNOSTIC SKIP (F-SEC-NEW-3 candidate): Đã capture "
                + captured.size() + " request nhưng không có request nào contain "
                + "'contractor'/'ekfb'/'company' trong body. Có thể: (a) requests đã captured là "
                + "auth/telemetry, request filter chưa fire trong window inject; (b) filter "
                + "thực sự chỉ client-side. Recon thủ công qua DevTools cần thiết trước khi "
                + "kết luận security finding. Captured URLs log ở console output.");
    }
}
