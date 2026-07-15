package co.uk.ppac.core.driver;

import co.uk.ppac.core.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
    /** Download directory shared cho session — tests download XLSX vào đây để parse. */
    public static final File DOWNLOAD_DIR =
            new File(System.getProperty("user.dir"), "target/downloads").getAbsoluteFile();

    private DriverFactory() {
    }

    public static WebDriver create() {
        if (!DOWNLOAD_DIR.exists()) {
            DOWNLOAD_DIR.mkdirs();
        }
        String browser = ConfigReader.get("browser.name").toLowerCase();
        boolean headless = ConfigReader.getBoolean("browser.headless");
        WebDriver driver = switch (browser) {
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                }
                yield new FirefoxDriver(options);
            }
            case "edge" -> {
                WebDriverManager.edgedriver().setup();
                EdgeOptions options = new EdgeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                yield new EdgeDriver(options);
            }
            default -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                options.addArguments("--disable-extensions", "--no-sandbox", "--disable-dev-shm-usage");
                Map<String, Object> prefs = new HashMap<>();
                prefs.put("download.default_directory", DOWNLOAD_DIR.getAbsolutePath());
                prefs.put("download.prompt_for_download", false);
                prefs.put("download.directory_upgrade", true);
                prefs.put("safebrowsing.enabled", true);
                options.setExperimentalOption("prefs", prefs);
                yield new ChromeDriver(options);
            }
        };
        driver.manage().window().setSize(new Dimension(
                ConfigReader.getInt("browser.viewport.width"),
                ConfigReader.getInt("browser.viewport.height")));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.getInt("timeout.pageLoad")));
        DRIVER.set(driver);
        return driver;
    }

    public static WebDriver get() {
        return DRIVER.get();
    }

    public static void quit() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }
}
