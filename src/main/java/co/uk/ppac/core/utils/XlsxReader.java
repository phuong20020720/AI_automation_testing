package co.uk.ppac.core.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class XlsxReader {

    private XlsxReader() {
    }

    /**
     * Đọc sheet đầu tiên thành list of maps (header → cell value as String).
     * Empty rows được skip. Cell value lấy theo `getStringCellValue`-equivalent
     * (numbers → toString, dates → ISO).
     */
    public static List<Map<String, String>> readFirstSheetAsMaps(File xlsxFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(xlsxFile);
             Workbook wb = new XSSFWorkbook(fis)) {
            Sheet sheet = wb.getSheetAt(0);
            List<Map<String, String>> rows = new ArrayList<>();
            if (sheet.getPhysicalNumberOfRows() < 2) {
                return rows;
            }
            Row headerRow = sheet.getRow(0);
            List<String> headers = new ArrayList<>();
            for (int c = 0; c < headerRow.getLastCellNum(); c++) {
                headers.add(cellAsString(headerRow.getCell(c)));
            }
            int last = sheet.getLastRowNum();
            for (int r = 1; r <= last; r++) {
                Row row = sheet.getRow(r);
                if (row == null) {
                    continue;
                }
                Map<String, String> rowMap = new HashMap<>();
                boolean anyValue = false;
                for (int c = 0; c < headers.size(); c++) {
                    String value = cellAsString(row.getCell(c));
                    rowMap.put(headers.get(c), value);
                    if (!value.isEmpty()) {
                        anyValue = true;
                    }
                }
                if (anyValue) {
                    rows.add(rowMap);
                }
            }
            return rows;
        }
    }

    public static List<String> readHeaders(File xlsxFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(xlsxFile);
             Workbook wb = new XSSFWorkbook(fis)) {
            Sheet sheet = wb.getSheetAt(0);
            List<String> headers = new ArrayList<>();
            if (sheet.getPhysicalNumberOfRows() < 1) {
                return headers;
            }
            Row headerRow = sheet.getRow(0);
            for (int c = 0; c < headerRow.getLastCellNum(); c++) {
                headers.add(cellAsString(headerRow.getCell(c)));
            }
            return headers;
        }
    }

    private static String cellAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toString();
                }
                double d = cell.getNumericCellValue();
                if (d == Math.floor(d) && !Double.isInfinite(d)) {
                    yield String.valueOf((long) d);
                }
                yield String.valueOf(d);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            default -> "";
        };
    }
}
