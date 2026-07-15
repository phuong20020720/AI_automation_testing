package co.uk.ppac.web.recon;

import co.uk.ppac.core.config.ConfigReader;
import co.uk.ppac.core.driver.DriverFactory;
import co.uk.ppac.web.utils.WorkerSessionHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Phase 4 recon — walk multiple worker profiles to map:
 * - M5 Skill Card section variations (CSCS / CPCS / CISRS / NPORS / Other / Not applicable)
 * - M4 RtW iframe + signed URL structure + viewer affordance
 * - Cookie / storage / token leak surface (M11)
 *
 * Output: plans/automation/ppac_worker_management/recon/phase4_*.{html,json,txt}.
 */
public class WorkerManagementPhase4Recon {

    private static final Path OUT_DIR = Paths.get("..", "docs", "plans", "automation", "ppac_worker_management", "recon");

    public static void main(String[] args) throws Exception {
        Files.createDirectories(OUT_DIR);
        WebDriver driver = DriverFactory.create();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            WorkerSessionHelper.loginAsStgeo(driver);
            String wmUrl = "https://" + ConfigReader.get("app.dashboardHost")
                    + ConfigReader.get("app.workerManagementPath");
            driver.get(wmUrl);
            sleep(4000);

            captureStorageAndCookies(driver, js, "phase4_session_surface");

            List<WebElement> profileButtons = driver.findElements(
                    By.xpath("//button[.//p[normalize-space(.)='View Full Profile']]"));
            System.out.println("Profile buttons on page 1: " + profileButtons.size());

            int sampleSize = Math.min(profileButtons.size(), 6);
            for (int i = 0; i < sampleSize; i++) {
                // Re-query because navigating back rebuilds the DOM.
                profileButtons = driver.findElements(
                        By.xpath("//button[.//p[normalize-space(.)='View Full Profile']]"));
                if (i >= profileButtons.size()) {
                    break;
                }
                js.executeScript("arguments[0].scrollIntoView({block:'center'}); arguments[0].click();",
                        profileButtons.get(i));
                sleep(4500);

                String suffix = "phase4_profile_" + String.format("%02d", i + 1);
                saveProfileSnapshot(driver, js, suffix);

                driver.navigate().back();
                sleep(3500);
            }

            // Try opening any iframe document fullscreen — locate viewer triggers.
            profileButtons = driver.findElements(
                    By.xpath("//button[.//p[normalize-space(.)='View Full Profile']]"));
            if (!profileButtons.isEmpty()) {
                js.executeScript("arguments[0].scrollIntoView({block:'center'}); arguments[0].click();",
                        profileButtons.get(0));
                sleep(4500);
                probeRtwViewer(driver, js);
            }
        } finally {
            DriverFactory.quit();
        }
    }

    private static void saveProfileSnapshot(WebDriver driver, JavascriptExecutor js, String name) throws Exception {
        Files.writeString(OUT_DIR.resolve(name + ".html"), driver.getPageSource());
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("url", driver.getCurrentUrl());

        // Skill Card structure probe
        Map<String, Object> skill = new LinkedHashMap<>();
        skill.put("hasNotApplicableHeader",
                !driver.findElements(By.xpath("//*[contains(normalize-space(.),'Skill Card - Not Applicable')]")).isEmpty());
        skill.put("hasSkillCardHeader",
                !driver.findElements(By.xpath("//*[contains(normalize-space(.),'Skill Card') and not(contains(normalize-space(.),'Not Applicable'))]")).isEmpty());
        for (String type : List.of("CSCS", "CPCS", "CISRS", "NPORS", "Other")) {
            skill.put("hasType_" + type,
                    !driver.findElements(By.xpath("//*[normalize-space(.)='" + type + "']")).isEmpty());
        }
        skill.put("hasFileInput", !driver.findElements(By.cssSelector("input[type='file']")).isEmpty());
        skill.put("buttonsCount", driver.findElements(By.tagName("button")).size());

        // RtW iframe probe
        List<WebElement> iframes = driver.findElements(By.cssSelector("iframe"));
        skill.put("iframeCount", iframes.size());
        if (!iframes.isEmpty()) {
            skill.put("iframeSrc0", iframes.get(0).getAttribute("src"));
        }

        // Rtw label
        List<WebElement> rtwLabels = driver.findElements(
                By.xpath("//p[starts-with(normalize-space(.),'Right To Work')]"));
        if (!rtwLabels.isEmpty()) {
            skill.put("rtwLabel", rtwLabels.get(0).getText());
        }

        // Buttons text
        List<WebElement> buttons = driver.findElements(By.tagName("button"));
        StringBuilder btnTexts = new StringBuilder();
        for (WebElement b : buttons) {
            String t = b.getText().trim().replaceAll("\\s+", " ");
            if (!t.isEmpty() && t.length() < 80) {
                btnTexts.append("[").append(t).append("] ");
            }
        }
        skill.put("buttonTexts", btnTexts.toString());

        meta.put("probe", skill);
        Files.writeString(OUT_DIR.resolve(name + "_probe.json"), jsonify(meta));
        System.out.println("Saved " + name + " (" + driver.getCurrentUrl() + ")");
    }

    private static void probeRtwViewer(WebDriver driver, JavascriptExecutor js) throws Exception {
        Map<String, Object> probe = new LinkedHashMap<>();
        probe.put("url", driver.getCurrentUrl());

        List<WebElement> iframes = driver.findElements(By.cssSelector("iframe"));
        probe.put("iframeCount", iframes.size());
        if (!iframes.isEmpty()) {
            String src = iframes.get(0).getAttribute("src");
            probe.put("iframeSrc", src);
            probe.put("iframeHasSignature", src != null && (src.contains("X-Amz-Signature") || src.contains("Signature=")));
            probe.put("iframeHasExpires", src != null && (src.contains("X-Amz-Expires") || src.contains("Expires=")));
        }

        // Locate any preview button (e.g. "Open preview in fullscreen") on profile
        List<WebElement> previewBtns = driver.findElements(
                By.xpath("//*[@role='button' and @aria-label='Open preview in fullscreen']"));
        probe.put("previewBtnCount", previewBtns.size());

        Files.writeString(OUT_DIR.resolve("phase4_rtw_viewer_probe.json"), jsonify(probe));
        Files.writeString(OUT_DIR.resolve("phase4_rtw_viewer.html"), driver.getPageSource());
        System.out.println("RtW viewer probe done: iframes=" + iframes.size());
    }

    private static void captureStorageAndCookies(WebDriver driver, JavascriptExecutor js, String name) throws Exception {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("url", driver.getCurrentUrl());
        data.put("cookies", driver.manage().getCookies().toString());

        Object localKeys = js.executeScript(
                "var k=[]; for(var i=0;i<localStorage.length;i++) k.push(localStorage.key(i)); return JSON.stringify(k);");
        Object sessionKeys = js.executeScript(
                "var k=[]; for(var i=0;i<sessionStorage.length;i++) k.push(sessionStorage.key(i)); return JSON.stringify(k);");
        data.put("localStorageKeys", String.valueOf(localKeys));
        data.put("sessionStorageKeys", String.valueOf(sessionKeys));

        // Capture values for keys that look like token/jwt/auth (without dumping huge blobs)
        Object suspiciousLocal = js.executeScript(
                "var out={}; for(var i=0;i<localStorage.length;i++){var k=localStorage.key(i); if(/token|auth|session|jwt|bearer/i.test(k)) out[k]=String(localStorage.getItem(k)).slice(0,120);} return JSON.stringify(out);");
        data.put("suspiciousLocalStorage", String.valueOf(suspiciousLocal));

        Files.writeString(OUT_DIR.resolve(name + ".txt"), jsonify(data));
        System.out.println("Saved storage/cookies surface");
    }

    private static String jsonify(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{\n");
        boolean first = true;
        for (Map.Entry<String, Object> e : map.entrySet()) {
            if (!first) {
                sb.append(",\n");
            }
            first = false;
            sb.append("  \"").append(e.getKey()).append("\": ");
            Object v = e.getValue();
            if (v instanceof Map) {
                sb.append(jsonify((Map<String, Object>) v).replace("\n", "\n  "));
            } else if (v == null) {
                sb.append("null");
            } else {
                sb.append("\"").append(String.valueOf(v).replace("\\", "\\\\").replace("\"", "\\\"")
                        .replace("\n", "\\n")).append("\"");
            }
        }
        sb.append("\n}");
        return sb.toString();
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
