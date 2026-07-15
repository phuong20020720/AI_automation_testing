package co.uk.ppac.core.driver;
import co.uk.ppac.core.factory.CapabilitiesManager;

import co.uk.ppac.core.config.AppConfig;
import io.appium.java_client.android.AndroidDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Creates and tracks one {@link AndroidDriver} per thread, so the suite can run
 * tests in parallel without sharing driver state between them.
 */
public final class AppiumDriverFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppiumDriverFactory.class);
    private static final ThreadLocal<AndroidDriver> DRIVER = new ThreadLocal<>();

    private AppiumDriverFactory() {
    }

    /** Starts a new Appium session for the current thread (or returns the existing one). */
    public static AndroidDriver createDriver() {
        AndroidDriver existing = DRIVER.get();
        if (existing != null) {
            return existing;
        }
        URL serverUrl = resolveServerUrl();
        AndroidDriver driver = new AndroidDriver(serverUrl, CapabilitiesManager.buildAndroidOptions());
        DRIVER.set(driver);
        LOGGER.info("Appium session started against {}", serverUrl);
        return driver;
    }

    /** Returns the driver bound to the current thread. */
    public static AndroidDriver getDriver() {
        AndroidDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException(
                    "No Appium driver on this thread. Call createDriver() in test setup first.");
        }
        return driver;
    }

    /** Ends the current thread's Appium session and clears the binding. */
    public static void quitDriver() {
        AndroidDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
            LOGGER.info("Appium session closed");
        }
    }

    private static URL resolveServerUrl() {
        String url = AppConfig.get("appium.server.url", "http://127.0.0.1:4723/");
        try {
            return new URI(url).toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new IllegalStateException("Invalid appium.server.url: " + url, e);
        }
    }
}
