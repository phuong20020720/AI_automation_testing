package co.uk.ppac.core.utils;

/**
 * Helper dùng chung cho các Locators class khi build locator động.
 */
public final class LocatorUtils {

    private LocatorUtils() {
    }

    /**
     * Build an XPath string literal that safely contains either single or double quotes
     * by using concat() when needed.
     */
    public static String xpathLiteral(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }
        StringBuilder sb = new StringBuilder("concat(");
        String[] parts = value.split("'");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                sb.append(", \"'\", ");
            }
            sb.append("'").append(parts[i]).append("'");
        }
        sb.append(")");
        return sb.toString();
    }
}
