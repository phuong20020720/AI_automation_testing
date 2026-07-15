package co.uk.ppac.core.factory;

import co.uk.ppac.core.config.AppConfig;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;

/**
 * Builds Appium capabilities from configuration.
 *
 * <p>Only Android (UiAutomator2) is wired up today. To add iOS, introduce an
 * {@code XCUITestOptions} branch keyed off the {@code platform.name} property.
 */
public final class CapabilitiesManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CapabilitiesManager.class);
    private static final String ANDROID = "android";

    private CapabilitiesManager() {
    }

    /** Builds the Android (UiAutomator2) capabilities for a new session. */
    public static UiAutomator2Options buildAndroidOptions() {
        String platform = AppConfig.get("platform.name", ANDROID);
        if (!ANDROID.equalsIgnoreCase(platform)) {
            throw new UnsupportedOperationException(
                    "Unsupported platform '" + platform + "'. Only 'android' is configured.");
        }

        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName(AppConfig.get("device.name", "Android Emulator"))
                .setAutoGrantPermissions(AppConfig.getBoolean("auto.grant.permissions", true))
                .setNoReset(AppConfig.getBoolean("no.reset", true))
                .setFullReset(AppConfig.getBoolean("full.reset", false))
                .setNewCommandTimeout(
                        Duration.ofSeconds(AppConfig.getInt("new.command.timeout.seconds", 120)));

        String platformVersion = AppConfig.get("platform.version");
        if (platformVersion != null) {
            options.setPlatformVersion(platformVersion);
        }

        applyApplication(options);
        LOGGER.info("Android capabilities ready for device '{}'",
                options.getDeviceName().orElse("?"));
        return options;
    }

    private static void applyApplication(UiAutomator2Options options) {
        String appPath = AppConfig.get("app.path");
        if (appPath != null) {
            File apk = new File(appPath);
            if (!apk.isFile()) {
                throw new IllegalStateException(
                        "app.path is set but the APK was not found: " + apk.getAbsolutePath());
            }
            options.setApp(apk.getAbsolutePath());
            return;
        }

        String appPackage = AppConfig.get("app.package");
        String appActivity = AppConfig.get("app.activity");
        if (appPackage != null && appActivity != null) {
            options.setAppPackage(appPackage);
            options.setAppActivity(appActivity);
            return;
        }

        throw new IllegalStateException(
                "No application configured. Set 'app.path' (an APK to install) "
                        + "or both 'app.package' and 'app.activity'.");
    }
}
