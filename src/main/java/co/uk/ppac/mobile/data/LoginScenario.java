package co.uk.ppac.mobile.data;

/**
 * One negative login scenario loaded from {@code test-data/login-scenarios.json}.
 *
 * @param scenario      human-readable description of the case
 * @param email         email input to enter
 * @param password      password input to enter
 * @param expectedError error message the app is expected to show
 */
public record LoginScenario(String scenario, String email, String password, String expectedError) {
}
