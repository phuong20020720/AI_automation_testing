package co.uk.ppac.web.recon;

import co.uk.ppac.core.config.ConfigReader;
import co.uk.ppac.core.driver.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class LoginRecon {

    private static final Path OUT_DIR = Paths.get("..", "docs", "plans", "automation", "ppac_login", "recon");

    public static void main(String[] args) throws Exception {
        Files.createDirectories(OUT_DIR);
        WebDriver driver = DriverFactory.create();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        try {
            String baseUrl = ConfigReader.get("app.baseUrl");

            // === Snapshot 1: Method picker page ===
            driver.get(baseUrl);
            waitForBody(wait);
            waitForFlutterReady(driver, wait);
            enableFlutterSemantics(driver);
            sleep(1500);
            saveSnapshot(driver, "01_method_picker");
            saveSemanticsTree(driver, "01_method_picker_semantics");

            // === Snapshot 2: Email login form (after clicking "Sign in with Email") ===
            clickSemanticByText(driver, "Sign in with Email");
            sleep(2000);
            saveSnapshot(driver, "02_email_login_form");
            saveSemanticsTree(driver, "02_email_login_form_semantics");

            // === Snapshot 3: Validation errors after empty submit ===
            clickSemanticByText(driver, "Login", "Log In", "Sign In", "Submit");
            sleep(1500);
            saveSemanticsTree(driver, "03_empty_submit_errors_semantics");

            // === Snapshot 4: Wrong password error ===
            driver.get(baseUrl);
            waitForBody(wait);
            waitForFlutterReady(driver, wait);
            enableFlutterSemantics(driver);
            sleep(1500);
            clickSemanticByText(driver, "Sign in with Email");
            sleep(2000);
            fillFlutterTextField(driver, 0, ConfigReader.get("login.test.email"));
            fillFlutterTextField(driver, 1, "WrongPass@123");
            clickSemanticByText(driver, "Login", "Log In", "Sign In", "Submit");
            sleep(3500);
            saveSemanticsTree(driver, "04_wrong_password_semantics");

            // === Snapshot 5: Dashboard after happy login ===
            driver.get(baseUrl);
            waitForBody(wait);
            waitForFlutterReady(driver, wait);
            enableFlutterSemantics(driver);
            sleep(1500);
            clickSemanticByText(driver, "Sign in with Email");
            sleep(2000);
            fillFlutterTextField(driver, 0, ConfigReader.get("login.test.email"));
            fillFlutterTextField(driver, 1, ConfigReader.get("login.test.password"));
            clickSemanticByText(driver, "Login", "Log In", "Sign In", "Submit");
            sleep(6000);
            saveSnapshot(driver, "05_post_login");
            saveSemanticsTree(driver, "05_post_login_semantics");
            saveCookies(driver, "05_post_login_cookies");
            saveStorage(driver, "05_post_login_storage");
        } finally {
            DriverFactory.quit();
        }
        System.out.println("Recon complete. Output in: " + OUT_DIR.toAbsolutePath());
    }

    private static void waitForFlutterReady(WebDriver driver, WebDriverWait wait) {
        wait.until(d -> d.findElements(By.tagName("flt-glass-pane")).size() > 0);
        // give Flutter time to mount widget tree
        sleep(2500);
    }

    private static void enableFlutterSemantics(WebDriver driver) {
        try {
            WebElement placeholder = driver.findElement(By.tagName("flt-semantics-placeholder"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", placeholder);
        } catch (NoSuchElementException ignored) {
            // already enabled or placeholder absent
        }
        sleep(800);
    }

    private static void clickSemanticByText(WebDriver driver, String... labels) {
        for (String label : labels) {
            try {
                String xpath = String.format(
                        "//flt-semantics[@role='button' and normalize-space(.)='%s']", label);
                WebElement element = driver.findElement(By.xpath(xpath));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                System.out.println("Clicked semantic button: " + label);
                return;
            } catch (NoSuchElementException ignored) {
            }
        }
        System.out.println("No semantic button matched: " + String.join(", ", labels));
    }

    private static void fillFlutterTextField(WebDriver driver, int fieldIndex, String value) {
        // Flutter exposes one <input> at a time inside flt-text-editing-host while a TextField is focused.
        // Strategy: click the Nth role=textbox semantic, then type into the host's input element.
        try {
            String xpath = String.format("(//flt-semantics[@role='textbox' or @role='text-field'])[%d]", fieldIndex + 1);
            WebElement target;
            try {
                target = driver.findElement(By.xpath(xpath));
            } catch (NoSuchElementException e) {
                // Some Flutter versions expose textfields as <input> nodes inside flt-semantics
                String fallback = String.format("(//flt-semantics//input)[%d]", fieldIndex + 1);
                target = driver.findElement(By.xpath(fallback));
            }
            ((JavascriptExecutor) driver).executeScript("arguments[0].click(); arguments[0].focus();", target);
            sleep(500);
            try {
                WebElement input = driver.findElement(By.cssSelector("flt-text-editing-host input, flt-text-editing-host textarea"));
                input.clear();
                input.sendKeys(value);
            } catch (NoSuchElementException e) {
                // Fallback: use the semantic element itself if it's an input
                target.clear();
                target.sendKeys(value);
            }
            System.out.println("Filled field index " + fieldIndex + " with value of length " + value.length());
        } catch (NoSuchElementException e) {
            System.out.println("Could not fill text field index " + fieldIndex + ": " + e.getMessage());
        }
    }

    private static void saveSemanticsTree(WebDriver driver, String name) throws Exception {
        Object tree = ((JavascriptExecutor) driver).executeScript(
                "var host = document.querySelector('flt-semantics-host');"
                        + "return host ? host.outerHTML : 'flt-semantics-host not found';");
        Files.writeString(OUT_DIR.resolve(name + ".html"), tree == null ? "" : tree.toString());
    }

    private static void waitForBody(WebDriverWait wait) {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }

    private static void saveSnapshot(WebDriver driver, String name) throws Exception {
        Path htmlPath = OUT_DIR.resolve(name + ".html");
        String pageSource = driver.getPageSource();
        Files.writeString(htmlPath, pageSource);

        Path metaPath = OUT_DIR.resolve(name + "_meta.txt");
        StringBuilder sb = new StringBuilder();
        sb.append("URL: ").append(driver.getCurrentUrl()).append('\n');
        sb.append("Title: ").append(driver.getTitle()).append('\n');
        Files.writeString(metaPath, sb.toString());
        System.out.println("Saved " + htmlPath.toAbsolutePath() + " (" + pageSource.length() + " bytes)");
    }

    private static void saveCookies(WebDriver driver, String name) throws Exception {
        Set<Cookie> cookies = driver.manage().getCookies();
        StringBuilder sb = new StringBuilder();
        for (Cookie cookie : cookies) {
            sb.append("name=").append(cookie.getName())
              .append("; domain=").append(cookie.getDomain())
              .append("; path=").append(cookie.getPath())
              .append("; httpOnly=").append(cookie.isHttpOnly())
              .append("; secure=").append(cookie.isSecure())
              .append("; sameSite=").append(cookie.getSameSite())
              .append("; expiry=").append(cookie.getExpiry())
              .append('\n');
        }
        Files.writeString(OUT_DIR.resolve(name + ".txt"), sb.toString());
    }

    private static void saveStorage(WebDriver driver, String name) throws Exception {
        Map<String, Object> info = new LinkedHashMap<>();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        info.put("localStorage", js.executeScript(
                "var out={}; for (var i=0;i<localStorage.length;i++){var k=localStorage.key(i); out[k]=localStorage.getItem(k);} return out;"));
        info.put("sessionStorage", js.executeScript(
                "var out={}; for (var i=0;i<sessionStorage.length;i++){var k=sessionStorage.key(i); out[k]=sessionStorage.getItem(k);} return out;"));
        info.put("currentUrl", driver.getCurrentUrl());
        Files.writeString(OUT_DIR.resolve(name + ".txt"), info.toString());
    }

}
