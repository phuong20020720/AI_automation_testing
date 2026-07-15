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

public class DashboardRecon {

    private static final Path OUT_DIR = Paths.get("..", "docs", "plans", "automation", "ppac_login", "recon");

    public static void main(String[] args) throws Exception {
        Files.createDirectories(OUT_DIR);
        WebDriver driver = DriverFactory.create();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            String baseUrl = ConfigReader.get("app.baseUrl");
            driver.get(baseUrl);
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

            click(driver, "Sign in with Email");
            for (int i = 0; i < 20 && driver.findElements(By.cssSelector("input[aria-label='Email Address']")).isEmpty(); i++) {
                sleep(500);
            }

            fillFlutter(driver, "input[aria-label='Email Address']", ConfigReader.get("login.test.email"));
            fillFlutter(driver, "input[aria-label='Password']", ConfigReader.get("login.test.password"));
            click(driver, "Sign In");

            // Wait until URL leaves login host AND is no longer transient sign-in
            for (int i = 0; i < 60; i++) {
                String url = driver.getCurrentUrl();
                System.out.println("t+" + i + "s url=" + url);
                if (url.contains("workforce-overview") || url.contains("/dashboard")
                        || (!url.contains("ppac-v2-web-uat") && !url.contains("/sign-in") && !url.contains("authCode="))) {
                    System.out.println("Reached final dashboard at t+" + i + "s");
                    break;
                }
                sleep(1000);
            }
            sleep(3000);

            saveSnapshot(driver, "07_dashboard");
            saveSemantics(driver, "07_dashboard_semantics");
            saveBodyPreview(driver, "07_dashboard_body_preview");
            saveCookies(driver, "07_dashboard_cookies");
            saveStorage(driver, js, "07_dashboard_storage");
            saveButtonsAndLinks(driver, "07_dashboard_clickables");
        } finally {
            DriverFactory.quit();
        }
    }

    private static void click(WebDriver driver, String label) {
        try {
            WebElement el = driver.findElement(By.xpath(
                    "//flt-semantics[@role='button' and normalize-space(.)='" + label + "']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        } catch (NoSuchElementException e) {
            System.out.println("Button not found: " + label);
        }
    }

    private static void fillFlutter(WebDriver driver, String css, String value) {
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

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private static void saveSnapshot(WebDriver driver, String name) throws Exception {
        Files.writeString(OUT_DIR.resolve(name + ".html"), driver.getPageSource());
        Files.writeString(OUT_DIR.resolve(name + "_meta.txt"),
                "URL: " + driver.getCurrentUrl() + "\nTitle: " + driver.getTitle() + "\n");
        System.out.println("Saved " + name + " at " + driver.getCurrentUrl());
    }

    private static void saveSemantics(WebDriver driver, String name) throws Exception {
        Object tree = ((JavascriptExecutor) driver).executeScript(
                "var h = document.querySelector('flt-semantics-host'); return h ? h.outerHTML : '(no flt-semantics-host)';");
        Files.writeString(OUT_DIR.resolve(name + ".html"), String.valueOf(tree));
    }

    private static void saveBodyPreview(WebDriver driver, String name) throws Exception {
        Object body = ((JavascriptExecutor) driver).executeScript(
                "var b = document.body; if (!b) return ''; var s = b.outerHTML; return s.length > 8000 ? s.slice(0, 8000) + '...[truncated]' : s;");
        Files.writeString(OUT_DIR.resolve(name + ".html"), String.valueOf(body));
    }

    private static void saveButtonsAndLinks(WebDriver driver, String name) throws Exception {
        Object info = ((JavascriptExecutor) driver).executeScript(
                "var sel = document.querySelectorAll('button, a, [role=\"button\"], flt-semantics[role=\"button\"]');"
                        + "var out = [];"
                        + "sel.forEach(function(el){"
                        + "  var rect = el.getBoundingClientRect();"
                        + "  if (rect.width === 0 && rect.height === 0) return;"
                        + "  out.push({"
                        + "    tag: el.tagName.toLowerCase(),"
                        + "    text: (el.innerText || el.textContent || '').trim().slice(0, 100),"
                        + "    id: el.getAttribute('id'),"
                        + "    classes: (el.getAttribute('class') || '').slice(0, 100),"
                        + "    role: el.getAttribute('role'),"
                        + "    ariaLabel: el.getAttribute('aria-label'),"
                        + "    dataTestid: el.getAttribute('data-testid') || el.getAttribute('data-test') || el.getAttribute('data-qa'),"
                        + "    href: el.tagName.toLowerCase() === 'a' ? el.getAttribute('href') : null"
                        + "  });"
                        + "});"
                        + "return JSON.stringify(out, null, 2);");
        Files.writeString(OUT_DIR.resolve(name + ".json"), String.valueOf(info));
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
        Files.writeString(OUT_DIR.resolve(name + ".json"), String.valueOf(info));
    }
}
