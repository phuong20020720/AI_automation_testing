package co.uk.ppac.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Centralised, read-only access to framework configuration for web and mobile.
 *
 * <p>Resolution order for any key (highest priority first):
 * <ol>
 *   <li>JVM system property ({@code -Dkey=value})</li>
 *   <li>Environment variable ({@code KEY_NAME} - dots become underscores, upper-cased)</li>
 *   <li>{@code .env} file in the project root (the only place for secrets)</li>
 *   <li>{@code config/environments/<env>.properties} - env from {@code -Denv} / {@code ENV}, default {@code uat}</li>
 *   <li>{@code config/mobile/<platform>.properties} - platform from {@code platform.name}, default {@code android}</li>
 *   <li>{@code config/framework.properties}</li>
 *   <li>{@code config.properties} on the classpath (legacy fallback)</li>
 * </ol>
 *
 * <p>Sensitive values (credentials) must come from an environment variable or
 * the git-ignored {@code .env} file - never from any committed {@code config/} file.
 */
public final class AppConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

    private static final String LEGACY_CONFIG_FILE = "config.properties";
    private static final String ENV_FILE = ".env";
    private static final Path CONFIG_DIR = Paths.get("config");
    private static final String DEFAULT_ENV = "uat";
    private static final String DEFAULT_PLATFORM = "android";

    // Each layer, loaded once; consulted in priority order by get().
    private static final Map<String, String> ENV_FILE_VALUES = new HashMap<>();
    private static final Properties ENVIRONMENT_PROPERTIES = new Properties();
    private static final Properties PLATFORM_PROPERTIES = new Properties();
    private static final Properties FRAMEWORK_PROPERTIES = new Properties();
    private static final Properties LEGACY_PROPERTIES = new Properties();

    static {
        loadClasspath(LEGACY_CONFIG_FILE, LEGACY_PROPERTIES);
        loadEnvFile();
        loadFile(CONFIG_DIR.resolve("framework.properties"), FRAMEWORK_PROPERTIES);

        String env = resolveLayerName("env", "ENV", DEFAULT_ENV, null);
        loadFile(CONFIG_DIR.resolve("environments").resolve(env + ".properties"), ENVIRONMENT_PROPERTIES);

        String platform = resolveLayerName("platform.name", "PLATFORM_NAME", DEFAULT_PLATFORM,
                FRAMEWORK_PROPERTIES.getProperty("platform.name"));
        loadFile(CONFIG_DIR.resolve("mobile").resolve(platform + ".properties"), PLATFORM_PROPERTIES);
    }

    private AppConfig() {
    }

    /** Returns the configured value, or {@code null} when the key is not set. */
    public static String get(String key) {
        return get(key, null);
    }

    /** Returns the configured value, or {@code defaultValue} when the key is not set. */
    public static String get(String key, String defaultValue) {
        String systemProperty = System.getProperty(key);
        if (isPresent(systemProperty)) {
            return systemProperty.trim();
        }
        String envValue = System.getenv(toEnvKey(key));
        if (isPresent(envValue)) {
            return envValue.trim();
        }
        String envFileValue = ENV_FILE_VALUES.get(toEnvKey(key));
        if (isPresent(envFileValue)) {
            return envFileValue.trim();
        }
        for (Properties layer : new Properties[]{
                ENVIRONMENT_PROPERTIES, PLATFORM_PROPERTIES, FRAMEWORK_PROPERTIES, LEGACY_PROPERTIES}) {
            String value = layer.getProperty(key);
            if (isPresent(value)) {
                return value.trim();
            }
        }
        return defaultValue;
    }

    /** Returns the configured value, or fails fast with a clear message when missing. */
    public static String getRequired(String key) {
        String value = get(key);
        if (!isPresent(value)) {
            throw new IllegalStateException(
                    "Required configuration '" + key + "' is missing. Set it via -D" + key
                            + ", the " + toEnvKey(key) + " environment variable, or the .env file.");
        }
        return value;
    }

    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        if (!isPresent(value)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            LOGGER.warn("Config '{}' is not a valid integer ('{}'); using default {}",
                    key, value, defaultValue);
            return defaultValue;
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        return isPresent(value) ? Boolean.parseBoolean(value) : defaultValue;
    }

    /** Resolves an env/platform selector from -D, env var, .env, an optional file default, then a hard default. */
    private static String resolveLayerName(String key, String envVar, String hardDefault, String fileDefault) {
        String value = System.getProperty(key);
        if (!isPresent(value)) {
            value = System.getenv(envVar);
        }
        if (!isPresent(value)) {
            value = ENV_FILE_VALUES.get(envVar);
        }
        if (!isPresent(value)) {
            value = fileDefault;
        }
        return isPresent(value) ? value.trim() : hardDefault;
    }

    private static String toEnvKey(String key) {
        return key.toUpperCase().replace('.', '_');
    }

    private static boolean isPresent(String value) {
        return value != null && !value.isBlank();
    }

    private static void loadClasspath(String resource, Properties target) {
        try (InputStream stream = AppConfig.class.getClassLoader().getResourceAsStream(resource)) {
            if (stream == null) {
                return;
            }
            target.load(stream);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read " + resource, e);
        }
    }

    private static void loadFile(Path path, Properties target) {
        if (!Files.exists(path)) {
            return;
        }
        try (InputStream stream = Files.newInputStream(path)) {
            target.load(stream);
            LOGGER.info("Loaded {} value(s) from {}", target.size(), path);
        } catch (IOException e) {
            LOGGER.warn("Unable to read {}: {}", path, e.getMessage());
        }
    }

    private static void loadEnvFile() {
        Path envPath = Paths.get(ENV_FILE);
        if (!Files.exists(envPath)) {
            return;
        }
        try {
            for (String line : Files.readAllLines(envPath)) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                int separator = trimmed.indexOf('=');
                if (separator <= 0) {
                    continue;
                }
                ENV_FILE_VALUES.put(
                        trimmed.substring(0, separator).trim(),
                        trimmed.substring(separator + 1).trim());
            }
            LOGGER.info("Loaded {} value(s) from .env", ENV_FILE_VALUES.size());
        } catch (IOException e) {
            LOGGER.warn("Unable to read .env file: {}", e.getMessage());
        }
    }
}
