package co.uk.ppac.web.recon;

import co.uk.ppac.core.config.ConfigReader;
import co.uk.ppac.core.driver.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class HappyLoginRecon {

    private static final Path OUT_DIR = Paths.get("..", "docs", "plans", "automation", "ppac_login", "recon");

    public static void main(String[] args) throws Exception {
        Files.createDirectories(OUT_DIR);
        WebDriver driver = DriverFactory.create();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            String baseUrl = ConfigReader.get("app.baseUrl");
            driver.get(baseUrl);
            // Wait for Flutter to mount
            for (int i = 0; i < 30; i++) {
                if (!driver.findElements(By.tagName("flt-glass-pane")).isEmpty()) {
                    break;
                }
                sleep(500);
            }
            sleep(3000);

            try {
                WebElement placeholder = driver.findElement(By.tagName("flt-semantics-placeholder"));
                js.executeScript("arguments[0].click();", placeholder);
            } catch (NoSuchElementException ignored) {
            }
            // Wait for semantics to populate
            for (int i = 0; i < 20; i++) {
                Object btn = js.executeScript(
                        "var nodes = document.querySelectorAll('flt-semantics[role=\"button\"]');"
                                + "for (var i = 0; i < nodes.length; i++) {"
                                + "  if ((nodes[i].textContent || '').trim() === 'Sign in with Email') return true;"
                                + "} return false;");
                if (Boolean.TRUE.equals(btn)) {
                    break;
                }
                sleep(500);
            }

            click(driver, "Sign in with Email");
            // Wait for email form to appear
            for (int i = 0; i < 20; i++) {
                if (!driver.findElements(By.cssSelector("input[aria-label='Email Address']")).isEmpty()) {
                    break;
                }
                sleep(500);
            }
            sleep(1000);

            fillFlutterField(driver, "input[aria-label='Email Address']", ConfigReader.get("login.test.email"));
            fillFlutterField(driver, "input[aria-label='Password']", ConfigReader.get("login.test.password"));

            click(driver, "Sign In");
            // Capture page every second for 15 seconds
            for (int i = 1; i <= 15; i++) {
                sleep(1000);
                String url = driver.getCurrentUrl();
                if (!url.endsWith("/") || url.contains("dashboard") || url.contains("home") || url.contains("verification")) {
                    System.out.println("URL changed at " + i + "s: " + url);
                    break;
                }
                System.out.println("t+" + i + "s url=" + url);
            }
            sleep(3000);

            saveSnapshot(driver, "06_happy_post_login");
            saveSemantics(driver, "06_happy_post_login_semantics");
            saveCookies(driver, "06_happy_post_login_cookies");
            saveStorage(driver, js, "06_happy_post_login_storage");
        } finally {
            DriverFactory.quit();
        }
    }

    private static void fillFlutterField(WebDriver driver, String semanticInputCss, String value) {
        WebElement target = driver.findElement(By.cssSelector(semanticInputCss));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click(); arguments[0].focus();", target);
        sleep(500);
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
            System.out.println("Clicked: " + label);
        } catch (NoSuchElementException e) {
            System.out.println("Button not found: " + label);
        }
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }

    private static void saveSnapshot(WebDriver driver, String name) throws Exception {
        Files.writeString(OUT_DIR.resolve(name + ".html"), driver.getPageSource());
        Files.writeString(OUT_DIR.resolve(name + "_meta.txt"),
                "URL: " + driver.getCurrentUrl() + "\nTitle: " + driver.getTitle() + "\n");
        System.out.println("Saved " + name + " URL=" + driver.getCurrentUrl());
    }

    private static void saveSemantics(WebDriver driver, String name) throws Exception {
        Object tree = ((JavascriptExecutor) driver).executeScript(
                "var h = document.querySelector('flt-semantics-host'); return h ? h.outerHTML : '';");
        Files.writeString(OUT_DIR.resolve(name + ".html"), tree == null ? "" : tree.toString());
    }

    private static void saveCookies(WebDriver driver, String name) throws Exception {
        Set<Cookie> cookies = driver.manage().getCookies();
        StringBuilder sb = new StringBuilder();
        for (Cookie c : cookies) {
            sb.append("name=").append(c.getName())
              .append("; domain=").append(c.getDomain())
              .append("; httpOnly=").append(c.isHttpOnly())
              .append("; secure=").append(c.isSecure())
              .append("; sameSite=").append(c.getSameSite())
              .append('\n');
        }
        Files.writeString(OUT_DIR.resolve(name + ".txt"), sb.toString());
    }

    private static void saveStorage(WebDriver driver, JavascriptExecutor js, String name) throws Exception {
        Object info = js.executeScript(
                "var ls={}, ss={};"
                        + "for(var i=0;i<localStorage.length;i++){var k=localStorage.key(i); ls[k]=localStorage.getItem(k);}"
                        + "for(var i=0;i<sessionStorage.length;i++){var k=sessionStorage.key(i); ss[k]=sessionStorage.getItem(k);}"
                        + "return JSON.stringify({url: location.href, localStorage: ls, sessionStorage: ss}, null, 2);");
        Files.writeString(OUT_DIR.resolve(name + ".json"), info == null ? "{}" : info.toString());
    }
}
