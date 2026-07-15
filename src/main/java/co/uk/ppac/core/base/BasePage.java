package co.uk.ppac.core.base;

import co.uk.ppac.core.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public abstract class BasePage {

    protected static final By FLUTTER_GLASS_PANE = By.tagName("flt-glass-pane");
    protected static final By FLUTTER_SEMANTICS_PLACEHOLDER = By.tagName("flt-semantics-placeholder");
    protected static final By FLUTTER_TEXT_EDITING_INPUT =
            By.cssSelector("flt-text-editing-host input, flt-text-editing-host textarea");

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("timeout.default")));
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForPresent(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    protected boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    protected String text(By locator) {
        return waitForPresent(locator).getText();
    }

    protected JavascriptExecutor js() {
        return (JavascriptExecutor) driver;
    }

    /**
     * Wait for Flutter to mount (flt-glass-pane present), then enable semantics tree
     * by clicking the accessibility placeholder. Subsequent DOM queries can use
     * <flt-semantics> elements.
     */
    public void waitForFlutterReady() {
        // Flutter CanvasKit mount + semantics tree đôi khi mất >60s trên UAT chậm.
        // 90s wait + poll placeholder click mỗi vòng — nếu placeholder render trễ,
        // single click sẽ miss và semantics tree không bao giờ enable.
        WebDriverWait flutterWait = new WebDriverWait(driver, Duration.ofSeconds(90));
        flutterWait.until(d -> !d.findElements(FLUTTER_GLASS_PANE).isEmpty());
        flutterWait.until(d -> {
            List<WebElement> placeholders = d.findElements(FLUTTER_SEMANTICS_PLACEHOLDER);
            if (!placeholders.isEmpty()) {
                try {
                    js().executeScript("arguments[0].click();", placeholders.get(0));
                } catch (Exception ignored) {
                }
            }
            List<WebElement> nodes = d.findElements(By.cssSelector("flt-semantics-host flt-semantics"));
            return nodes.size() > 5;
        });
    }

    /**
     * Click a Flutter semantic button by its visible text (exact match, normalized).
     */
    protected void clickSemanticButton(String text) {
        By locator = By.xpath(String.format(
                "//flt-semantics[@role='button' and normalize-space(.)=%s]", xpathLiteral(text)));
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        WebElement element = driver.findElement(locator);
        js().executeScript("arguments[0].click();", element);
    }

    /**
     * Build an XPath string literal that safely contains either single or double quotes.
     * Delegates to {@link co.uk.ppac.core.utils.LocatorUtils#xpathLiteral(String)}
     * so locator-building logic lives in one place.
     */
    protected static String xpathLiteral(String value) {
        return co.uk.ppac.core.utils.LocatorUtils.xpathLiteral(value);
    }

    /**
     * Type into a Flutter text field exposed as <input> in the semantics tree.
     * Flutter routes keystrokes through flt-text-editing-host so we must focus the
     * semantic input then send keys to the editing host. Falls back to the input
     * itself if the editing host isn't present (e.g. when Flutter has no active editor).
     */
    protected void fillFlutterField(By semanticInputLocator, String value) {
        WebElement target = waitForPresent(semanticInputLocator);
        js().executeScript("arguments[0].click(); arguments[0].focus();", target);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        try {
            WebElement editor = driver.findElement(FLUTTER_TEXT_EDITING_INPUT);
            editor.clear();
            editor.sendKeys(value);
        } catch (NoSuchElementException e) {
            target.clear();
            target.sendKeys(value);
        }
    }
}
