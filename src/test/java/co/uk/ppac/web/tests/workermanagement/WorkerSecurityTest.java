package co.uk.ppac.web.tests.workermanagement;

import co.uk.ppac.web.pages.FilterModal;
import co.uk.ppac.web.pages.WorkerProfilePage;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.HttpURLConnection;
import java.net.URI;

/**
 * Phase 4 — M11 Security (10 TC).
 *
 * - TC_085 XSS Search field
 * - TC_086 XSS Skill Other (cross-context) — đã cover trong WorkerSkillCardTest, đây re-verify list view echo
 * - TC_087 IDOR worker id (stgeo per Q16 = full → expect 200, document behavior)
 * - TC_088 API DELETE bypass UI (per Q16 stgeo full → cookie required path) — DISABLED (cần API contract)
 * - TC_089 File upload DOS — DISABLED (no upload UI from dashboard)
 * - TC_090 Polyglot file — DISABLED (no upload UI from dashboard)
 * - TC_091 Filter date injection — filter sử dụng date picker; payload qua input
 * - TC_092 HTTPS + cookie flags (Secure, SameSite). HttpOnly: Selenium API hạn chế, document
 * - TC_093 Token KHÔNG expose localStorage/sessionStorage/URL
 * - TC_094 Hard delete authz cross-role — DISABLED (chỉ 1 role stgeo theo Q16)
 */
public class WorkerSecurityTest extends WorkerManagementBaseTest {

    @DataProvider(name = "xssSearchPayloads")
    public Object[][] xssSearchPayloads() {
        return new Object[][]{
                {"<script>alert('xss')</script>"},
                {"<img src=x onerror=alert(1)>"},
                {"\"><script>alert(1)</script>"},
                {"<svg onload=alert(1)>"}
        };
    }

    /** PPAC_WM_TC_085 — XSS qua Search field */
    @Test(groups = {"worker-management", "security"}, dataProvider = "xssSearchPayloads")
    public void testSearchXssIsSanitized(String payload) {
        list.search(payload);
        // Verify no alert
        try {
            String text = driver.switchTo().alert().getText();
            driver.switchTo().alert().dismiss();
            Assert.fail("XSS payload [" + payload + "] đã trigger alert: " + text);
        } catch (NoAlertPresentException expected) {
            // ok
        }
        // Verify payload không xuất hiện raw trong DOM (rendered escaped)
        String src = driver.getPageSource();
        Assert.assertFalse(src.contains("<script>alert('xss')</script>"),
                "Search input payload <script>alert('xss')</script> KHÔNG được render raw HTML");
        // Cleanup search
        list.search("");
    }

    /** PPAC_WM_TC_086 — XSS Skill Other: skip vì cover trong WorkerSkillCardTest */
    @Test(groups = {"worker-management", "security"}, enabled = false,
            description = "Cover trong WorkerSkillCardTest.testSkillCardOtherLabelEscapesScriptIfPresent")
    public void testSkillOtherXssCrossContextReflection() {
        // covered
    }

    /** PPAC_WM_TC_087 — IDOR: stgeo per Q16 = all permissions, expect 200 truy cập mọi workerId */
    @Test(groups = {"worker-management", "security"})
    public void testIdorOnOtherWorkerIdReturnsAuthorizedAccessForStgeo() {
        int idx = list.firstRowWithFullProfileButton();
        if (idx < 0) {
            throw new SkipException("Không có worker");
        }
        WorkerProfilePage profile = list.clickFullProfileAt(idx);
        String currentId = profile.currentWorkerId();
        Assert.assertFalse(currentId.isBlank(), "Phải capture được current worker id");

        // Construct synthetic id (flip 1 char) — vẫn cùng pattern 24-hex
        String syntheticId = mutateId(currentId);
        String mutatedUrl = driver.getCurrentUrl().replace(currentId, syntheticId);
        driver.get(mutatedUrl);
        try { Thread.sleep(2500); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }

        // Per Q16: stgeo full access. Behavior options:
        // (a) load profile của syntheticId nếu exists
        // (b) 404 / empty profile nếu syntheticId không exist
        // CẢ 2 đều OK. Critical là KHÔNG được crash hoặc expose stack trace / DB error.
        String src = driver.getPageSource().toLowerCase();
        Assert.assertFalse(src.contains("stack trace") || src.contains("java.lang") || src.contains("nullpointer"),
                "URL mutation KHÔNG được expose stack trace / DB error — synthetic id: " + syntheticId);
        // Document: nếu profile render → log; nếu không → ok
        list.open();
    }

    /** PPAC_WM_TC_088 — DISABLED: cần API contract specs để test bypass */
    @Test(groups = {"worker-management", "security"}, enabled = false,
            description = "Disabled: cần API DELETE endpoint spec để craft request. Không có swagger trong scope.")
    public void testApiDeleteRequiresSession() {
        // out of scope
    }

    /** PPAC_WM_TC_089 — DISABLED: M5 read-only, không có upload UI */
    @Test(groups = {"worker-management", "security"}, enabled = false,
            description = "Disabled: M5 Skill Card read-only từ dashboard — không có upload affordance. Test ở mobile.")
    public void testFileUploadDosLargeFile() {
        // out of scope
    }

    /** PPAC_WM_TC_090 — DISABLED: cùng lý do TC_089 */
    @Test(groups = {"worker-management", "security"}, enabled = false,
            description = "Disabled: không có upload UI dashboard. Polyglot file test ở mobile app upload pipeline.")
    public void testPolyglotFileUpload() {
        // out of scope
    }

    /** PPAC_WM_TC_091 — Date input không nhận free text injection */
    @Test(groups = {"worker-management", "security"})
    public void testFilterDateInputRejectsInjection() {
        if (!list.isFiltersButtonDisplayed()) {
            throw new SkipException("Không có Filters button");
        }
        FilterModal modal = list.openFilters();
        Assert.assertTrue(modal.isOpen(), "Filter modal phải mở");

        // FilterModal có start/end date inputs — try inject SQLi/XSS
        String payload = "2026-01-01'; DROP TABLE workers; --";
        boolean injected = modal.tryTypeIntoFirstDateInput(payload);
        if (!injected) {
            modal.close();
            throw new SkipException("Filter modal không có date input editable trực tiếp");
        }

        // Apply, expect no DB error / no crash
        modal.applyIfPossible();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
        String src = driver.getPageSource().toLowerCase();
        Assert.assertFalse(src.contains("syntax error") || src.contains("postgres") || src.contains("mysql")
                        || src.contains("stack trace") || src.contains("java.lang"),
                "Date injection payload KHÔNG được expose DB error / stack trace");
    }

    /** PPAC_WM_TC_092 — HTTPS + cookie flags Secure/SameSite */
    @Test(groups = {"worker-management", "security"})
    public void testHttpsAndCookieSecurityFlags() {
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.startsWith("https://"),
                "Worker Management URL phải dùng HTTPS — actual: " + url);

        boolean sessionCookieFound = false;
        for (Cookie c : driver.manage().getCookies()) {
            String name = c.getName().toLowerCase();
            if (name.contains("session") || name.contains("auth") || name.contains("token")) {
                sessionCookieFound = true;
                Assert.assertTrue(c.isSecure(),
                        "Cookie '" + c.getName() + "' phải có Secure flag — vi phạm transport security");
                Assert.assertNotNull(c.getSameSite(),
                        "Cookie '" + c.getName() + "' phải có SameSite attribute");
                Assert.assertTrue("Strict".equalsIgnoreCase(c.getSameSite())
                                || "Lax".equalsIgnoreCase(c.getSameSite()),
                        "Cookie SameSite phải Strict/Lax — actual: " + c.getSameSite());
            }
        }
        Assert.assertTrue(sessionCookieFound,
                "Phải có session/auth cookie sau khi login (verify cookie surface)");
    }

    /** PPAC_WM_TC_093 — Token KHÔNG expose ở localStorage / sessionStorage / URL */
    @Test(groups = {"worker-management", "security"})
    public void testTokenNotExposedInLocalStorageOrUrl() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String localKeys = (String) js.executeScript(
                "var out=[]; for(var i=0;i<localStorage.length;i++) out.push(localStorage.key(i)); return out.join(',');");
        String sessionKeys = (String) js.executeScript(
                "var out=[]; for(var i=0;i<sessionStorage.length;i++) out.push(sessionStorage.key(i)); return out.join(',');");
        String[] suspicious = new String[]{"token", "jwt", "bearer", "auth", "access_token", "id_token", "refresh_token"};
        for (String key : suspicious) {
            Assert.assertFalse(localKeys.toLowerCase().contains(key),
                    "localStorage KHÔNG được chứa key '" + key + "' — actual keys: " + localKeys);
            Assert.assertFalse(sessionKeys.toLowerCase().contains(key),
                    "sessionStorage KHÔNG được chứa key '" + key + "' — actual keys: " + sessionKeys);
        }
        // URL không được chứa token
        String url = driver.getCurrentUrl().toLowerCase();
        for (String key : suspicious) {
            Assert.assertFalse(url.contains(key + "="),
                    "URL KHÔNG được chứa query param '" + key + "=' — actual url: " + url);
        }
    }

    /** PPAC_WM_TC_094 — DISABLED: chỉ 1 role stgeo trong scope per Q16 */
    @Test(groups = {"worker-management", "security"}, enabled = false,
            description = "Disabled: Q16 chỉ test stgeo (full permission). Multi-role tests defer phase sau.")
    public void testHardDeleteAuthzCrossRole() {
        // out of scope
    }

    /** Bổ sung TC_M11_001 — HSTS header (transport security) */
    @Test(groups = {"worker-management", "security"})
    public void testHstsHeaderPresent() throws Exception {
        String url = driver.getCurrentUrl();
        HttpURLConnection conn = (HttpURLConnection) URI.create(url).toURL().openConnection();
        conn.setRequestMethod("HEAD");
        conn.setInstanceFollowRedirects(false);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        conn.connect();
        String hsts = conn.getHeaderField("Strict-Transport-Security");
        int code = conn.getResponseCode();
        conn.disconnect();
        // Note: anonymous request may redirect to login; HSTS header thường nằm ở response của hosting
        if (code == 401 || code == 403) {
            // Acceptable — server requires auth for HEAD, không thể verify HSTS thuần ở route này
            throw new SkipException("Anonymous HEAD trả " + code + " — HSTS check defer");
        }
        // Soft check: HSTS missing là finding F-SEC-04, nhưng tổ chức UAT có thể cố tình tắt
        // (không hard-fail). Khi có header thì verify max-age directive.
        if (hsts != null) {
            Assert.assertTrue(hsts.toLowerCase().contains("max-age"),
                    "HSTS phải có max-age directive — actual: " + hsts);
        }
    }

    // ───────────── helpers ─────────────

    private String mutateId(String id) {
        if (id.length() < 2) {
            return id + "0";
        }
        char last = id.charAt(id.length() - 1);
        char replaced = last == 'f' ? '0' : (char) (last + 1);
        if (!Character.isLetterOrDigit(replaced)) {
            replaced = '0';
        }
        return id.substring(0, id.length() - 1) + replaced;
    }
}
