package co.uk.ppac.web.recon;

import co.uk.ppac.core.config.ConfigReader;
import co.uk.ppac.core.driver.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Selenium-based DOM recon cho module Worker Management (PPAC v2).
 * Login bằng stgeo account (Flutter form) → navigate dashboard /en/worker-management
 * → snapshot DOM list, profile, delete modal → ghi ra
 * plans/automation/ppac_worker_management/recon/.
 */
public class WorkerManagementRecon {

    private static final Path OUT_DIR = Paths.get("..", "docs", "plans", "automation", "ppac_worker_management", "recon");

    public static void main(String[] args) throws Exception {
        Files.createDirectories(OUT_DIR);
        WebDriver driver = DriverFactory.create();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            driver.get(ConfigReader.get("app.baseUrl"));
            waitForFlutter(driver, js);

            click(driver, "Sign in with Email");
            for (int i = 0; i < 20 && driver.findElements(By.cssSelector("input[aria-label='Email Address']")).isEmpty(); i++) {
                sleep(500);
            }

            fillFlutterField(driver, "input[aria-label='Email Address']", ConfigReader.get("login.stgeo.email"));
            fillFlutterField(driver, "input[aria-label='Password']", ConfigReader.get("login.stgeo.password"));
            click(driver, "Sign In");

            String dashboardHost = ConfigReader.get("app.dashboardHost");
            for (int i = 0; i < 60; i++) {
                if (driver.getCurrentUrl().contains(dashboardHost) && !driver.getCurrentUrl().contains("/sign-in")) {
                    break;
                }
                sleep(1000);
            }
            sleep(3000);
            System.out.println("Logged-in URL: " + driver.getCurrentUrl());
            saveSnapshot(driver, "01_dashboard_landing");

            String wmUrl = "https://" + dashboardHost + ConfigReader.get("app.workerManagementPath");
            driver.get(wmUrl);
            sleep(4000);
            for (int i = 0; i < 30; i++) {
                Object ready = js.executeScript(
                        "return document.readyState === 'complete' && document.querySelectorAll('table, [role=table], [role=grid]').length > 0;");
                if (Boolean.TRUE.equals(ready)) {
                    break;
                }
                sleep(500);
            }
            sleep(2000);
            System.out.println("Worker Mgmt URL: " + driver.getCurrentUrl());
            saveSnapshot(driver, "02_worker_list");

            extractTestableElements(driver, js, "02_worker_list_elements");

            try {
                List<WebElement> profileButtons = driver.findElements(
                        By.xpath("//button[.//p[normalize-space(.)='View Full Profile']]"));
                if (!profileButtons.isEmpty()) {
                    js.executeScript("arguments[0].scrollIntoView({block:'center'}); arguments[0].click();",
                            profileButtons.get(0));
                    sleep(5000);
                    System.out.println("Profile URL: " + driver.getCurrentUrl());
                    saveSnapshot(driver, "03_worker_profile");
                    extractTestableElements(driver, js, "03_worker_profile_elements");
                    driver.navigate().back();
                    sleep(4000);
                } else {
                    System.out.println("No View Full Profile button found on list");
                }
            } catch (Exception e) {
                System.out.println("Profile recon error: " + e.getMessage());
            }

            try {
                List<WebElement> deleteButtons = driver.findElements(
                        By.xpath("//button[.//*[local-name()='svg' and contains(@class,'lucide-trash')]]"));
                if (!deleteButtons.isEmpty()) {
                    js.executeScript("arguments[0].scrollIntoView({block:'center'}); arguments[0].click();",
                            deleteButtons.get(0));
                    sleep(2500);
                    saveSnapshot(driver, "04_delete_modal");
                    extractTestableElements(driver, js, "04_delete_modal_elements");
                    List<WebElement> cancelBtn = driver.findElements(
                            By.xpath("//button[normalize-space(.)='Cancel']"));
                    if (!cancelBtn.isEmpty()) {
                        js.executeScript("arguments[0].click();", cancelBtn.get(0));
                        sleep(1500);
                    } else {
                        new org.openqa.selenium.interactions.Actions(driver).sendKeys(org.openqa.selenium.Keys.ESCAPE).perform();
                        sleep(1500);
                    }
                } else {
                    System.out.println("No delete (trash icon) button found on list");
                }
            } catch (Exception e) {
                System.out.println("Delete modal recon error: " + e.getMessage());
            }

            try {
                List<WebElement> filterBtns = driver.findElements(
                        By.xpath("//button[.//span[normalize-space(.)='Filters'] or normalize-space(.)='Filters']"));
                if (!filterBtns.isEmpty()) {
                    js.executeScript("arguments[0].scrollIntoView({block:'center'}); arguments[0].click();",
                            filterBtns.get(0));
                    sleep(2000);
                    saveSnapshot(driver, "05_filter_modal");
                    extractTestableElements(driver, js, "05_filter_modal_elements");
                } else {
                    System.out.println("No Filters button found");
                }
            } catch (Exception e) {
                System.out.println("Filter modal recon error: " + e.getMessage());
            }
        } finally {
            DriverFactory.quit();
        }
    }

    private static void waitForFlutter(WebDriver driver, JavascriptExecutor js) {
        for (int i = 0; i < 30 && driver.findElements(By.tagName("flt-glass-pane")).isEmpty(); i++) {
            sleep(500);
        }
        sleep(2500);
        try {
            WebElement placeholder = driver.findElement(By.tagName("flt-semantics-placeholder"));
            js.executeScript("arguments[0].click();", placeholder);
            sleep(800);
        } catch (NoSuchElementException ignored) {
        }
    }

    private static void fillFlutterField(WebDriver driver, String css, String value) {
        WebElement target = driver.findElement(By.cssSelector(css));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click(); arguments[0].focus();", target);
        sleep(400);
        try {
            WebElement editor = driver.findElement(By.cssSelector(
                    "flt-text-editing-host input, flt-text-editing-host textarea"));
            editor.clear();
            editor.sendKeys(value);
        } catch (NoSuchElementException e) {
            target.clear();
            target.sendKeys(value);
        }
    }

    private static void click(WebDriver driver, String label) {
        try {
            WebElement element = driver.findElement(By.xpath(
                    "//flt-semantics[@role='button' and normalize-space(.)='" + label + "']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            System.out.println("Clicked Flutter button: " + label);
        } catch (NoSuchElementException e) {
            System.out.println("Flutter button not found: " + label);
        }
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }

    private static void saveSnapshot(WebDriver driver, String name) throws Exception {
        Files.writeString(OUT_DIR.resolve(name + ".html"), driver.getPageSource());
        Files.writeString(OUT_DIR.resolve(name + "_meta.txt"),
                "URL: " + driver.getCurrentUrl() + "\nTitle: " + driver.getTitle() + "\n");
        System.out.println("Saved " + name);
    }

    /**
     * Trích tất cả elements có khả năng test: button, link, input, select, table, [role],
     * [data-testid], [aria-label], [aria-labelledby], v.v.
     */
    private static void extractTestableElements(WebDriver driver, JavascriptExecutor js, String name) throws Exception {
        String script = ""
                + "function attrs(el){var o={}; for(var i=0;i<el.attributes.length;i++){var a=el.attributes[i]; o[a.name]=a.value;} return o;}"
                + "function summarize(el){"
                + "  var rect = el.getBoundingClientRect();"
                + "  var text = (el.innerText||el.textContent||'').trim().slice(0,120);"
                + "  return {tag: el.tagName.toLowerCase(), text: text, visible: rect.width>0 && rect.height>0, attrs: attrs(el)};"
                + "}"
                + "var selectors = ['button','a','input','select','textarea','[role]','[data-testid]','[data-test]','[data-qa]','[aria-label]','[aria-labelledby]','table','th','td','[role=row]','[role=columnheader]','[role=cell]','[role=dialog]'];"
                + "var seen = new Set(); var out = [];"
                + "selectors.forEach(function(sel){"
                + "  document.querySelectorAll(sel).forEach(function(el){"
                + "    if(seen.has(el)) return; seen.add(el);"
                + "    out.push(summarize(el));"
                + "  });"
                + "});"
                + "return JSON.stringify(out, null, 2);";
        Object result = js.executeScript(script);
        Files.writeString(OUT_DIR.resolve(name + ".json"), result == null ? "[]" : result.toString());
        System.out.println("Saved " + name + ".json (" + (result == null ? 0 : result.toString().length()) + " chars)");
    }
}
