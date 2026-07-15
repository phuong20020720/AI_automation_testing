package co.uk.ppac.mobile.tests.bally;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Dữ liệu test cho luồng E2E BALLYCOMMON-RAIL (TC_130), đọc từ
 * {@code src/test/resources/testdata/bally-rail-e2e.properties}.
 *
 * <p>Tách dữ liệu khỏi code: muốn đổi giá trị điền form chỉ cần sửa file
 * properties, không phải build lại logic test. Quy ước key:
 * <ul>
 *   <li>{@code <field>.value}  — text gõ vào ô input</li>
 *   <li>{@code <field>.select} — text để chọn option dropdown; {@code (FIRST)} = chọn option đầu</li>
 *   <li>{@code <field>.skip=true} — bỏ qua ô (vd app đã điền sẵn)</li>
 * </ul>
 */
public final class BallyRailTestData {

    private static final String RESOURCE = "testdata/bally-rail-e2e.properties";
    private static final String FIRST_TOKEN = "(FIRST)";

    private final Properties props;

    private BallyRailTestData(Properties props) {
        this.props = props;
    }

    /** Nạp dữ liệu từ classpath; ném lỗi rõ ràng nếu thiếu file. */
    public static BallyRailTestData load() {
        Properties p = new Properties();
        try (InputStream in = BallyRailTestData.class.getClassLoader().getResourceAsStream(RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException("Không tìm thấy '" + RESOURCE + "' trên classpath");
            }
            p.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Không đọc được '" + RESOURCE + "'", e);
        }
        return new BallyRailTestData(p);
    }

    /** Giá trị text của {@code <field>.value}; {@code null} nếu trống/không có. */
    public String value(String field) {
        return trimToNull(props.getProperty(field + ".value"));
    }

    /** Giá trị chọn dropdown {@code <field>.select}; {@code null} nếu trống/không có. */
    public String select(String field) {
        return trimToNull(props.getProperty(field + ".select"));
    }

    /** {@code true} khi {@code <field>.skip=true} (app đã điền sẵn → không nhập lại). */
    public boolean skip(String field) {
        return Boolean.parseBoolean(props.getProperty(field + ".skip", "false"));
    }

    /** {@code true} khi giá trị select là {@code (FIRST)} → chọn option đầu tiên. */
    public boolean isFirst(String selectValue) {
        return FIRST_TOKEN.equalsIgnoreCase(selectValue);
    }

    private static String trimToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
