package co.uk.ppac.core.utils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Generates unique, traceable test data shared by web and mobile suites.
 *
 * <p>Every value follows {@code auto_testName_timestamp_random} so a record
 * seen later in a database or log traces straight back to the test that
 * created it, and tests running in parallel never collide.
 */
public final class DataGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALPHANUM = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final DateTimeFormatter TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private DataGenerator() {
    }

    public static String timestamp() {
        return LocalDateTime.now().format(TIMESTAMP);
    }

    public static String randomToken(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUM.charAt(RANDOM.nextInt(ALPHANUM.length())));
        }
        return sb.toString();
    }

    /** e.g. {@code auto_loginMobile_20260521_153012_a3f2@test.ppac.co.uk} */
    public static String generateEmail(String testName) {
        return "auto_" + sanitize(testName) + "_" + timestamp() + "_" + shortRandom() + "@test.ppac.co.uk";
    }

    /** e.g. {@code auto_loginMobile_20260521_153012_a3f2} */
    public static String generateUsername(String testName) {
        return "auto_" + sanitize(testName) + "_" + timestamp() + "_" + shortRandom();
    }

    public static String traceableEmail(String testName) {
        return String.format("auto_%s_%s_%s@yopmail.com",
                sanitize(testName), timestamp(), randomToken(4));
    }

    public static String nonExistentEmail(String testName) {
        return String.format("nonexistent_%s_%s_%s@yopmail.com",
                sanitize(testName), timestamp(), randomToken(4));
    }

    private static String shortRandom() {
        return UUID.randomUUID().toString().substring(0, 4);
    }

    private static String sanitize(String value) {
        return value.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
    }
}
