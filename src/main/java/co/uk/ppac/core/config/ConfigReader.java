package co.uk.ppac.core.config;

/**
 * Web-side configuration accessor. Thin wrapper over {@link AppConfig} so web
 * and mobile share one resolution chain (-D &gt; env &gt; .env &gt;
 * config/environments &gt; config/mobile &gt; config/framework &gt; config.properties).
 *
 * <p>Unlike {@link AppConfig#get(String)} (which returns {@code null} when a key
 * is absent), these methods fail fast on a missing key - the historical web
 * contract that callers rely on.
 */
public final class ConfigReader {

    private ConfigReader() {
    }

    public static String get(String key) {
        return AppConfig.getRequired(key);
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
}
