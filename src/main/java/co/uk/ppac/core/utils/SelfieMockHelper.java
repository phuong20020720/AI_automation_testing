package co.uk.ppac.core.utils;

import co.uk.ppac.core.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralised access to Selfie mock configuration. Tests reading this helper
 * can branch on mock availability instead of skipping silently.
 *
 * <p>3 mock strategies (Phase 1 + 2 of {@code plans/automation/ppac_mobile/selfie_mock_design.md}):
 * <ul>
 *   <li><b>BACKEND</b> — server respects {@code PPAC_SELFIE_MOCK_ENABLED=true} env
 *       and returns canned success when user email matches whitelist pattern. Test
 *       proceeds through Selfie Camera Live screen via real (fake) camera capture
 *       and the backend mocks the Regula response.</li>
 *   <li><b>DEBUG_SKIP</b> — app debug build exposes a hidden skip UI on
 *       {@code SelfieInstructionScreen}; activated via triple-tap top-left logo
 *       (or similar gesture). Bypasses camera entirely.</li>
 *   <li><b>NONE</b> — no mock available. Selfie tests must skip or use real device.</li>
 * </ul>
 *
 * <p>Config keys (all optional, default to disabled):
 * <pre>
 * selfie.mock.backend.enabled     true|false   - backend feature flag deployed
 * selfie.mock.debug.skip.enabled  true|false   - app built with DEBUG_SKIP_SELFIE=true
 * selfie.mock.email.pattern       regex        - email whitelist (must match BACKEND deploy)
 * </pre>
 */
public final class SelfieMockHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SelfieMockHelper.class);

    public enum Strategy {
        BACKEND_FEATURE_FLAG,
        DEBUG_SKIP_BUTTON,
        NONE
    }

    private SelfieMockHelper() {
    }

    /** Returns the active mock strategy based on configuration. */
    public static Strategy activeStrategy() {
        if (AppConfig.getBoolean("selfie.mock.debug.skip.enabled", false)) {
            return Strategy.DEBUG_SKIP_BUTTON;
        }
        if (AppConfig.getBoolean("selfie.mock.backend.enabled", false)) {
            return Strategy.BACKEND_FEATURE_FLAG;
        }
        return Strategy.NONE;
    }

    /** True if any mock strategy is configured — tests can proceed past Selfie. */
    public static boolean isAnyMockAvailable() {
        return activeStrategy() != Strategy.NONE;
    }

    /**
     * Returns the configured email whitelist regex (must match backend deploy).
     * Default safe-guard: only emails ending in {@code @yopmail.com} are mockable.
     */
    public static String emailWhitelistPattern() {
        return AppConfig.get("selfie.mock.email.pattern", "^qa_signup_.*@yopmail\\.com$");
    }

    /**
     * Returns true if the given email matches the configured whitelist. Tests
     * SHOULD verify before generating accounts — production emails must never
     * trigger mock paths.
     */
    public static boolean isEmailMockable(String email) {
        if (email == null) {
            return false;
        }
        boolean matches = email.matches(emailWhitelistPattern());
        if (!matches) {
            LOGGER.warn("Email '{}' does not match mock whitelist '{}' — test will hit real Regula path",
                    email, emailWhitelistPattern());
        }
        return matches;
    }
}
