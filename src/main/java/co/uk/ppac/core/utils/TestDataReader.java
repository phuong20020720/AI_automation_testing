package co.uk.ppac.core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/** Reads external JSON test data (under {@code test-data/}) into typed objects. */
public final class TestDataReader {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String DATA_DIR = "test-data";

    private TestDataReader() {
    }

    /** Reads a JSON array from {@code test-data/<fileName>} into a list of the given type. */
    public static <T> List<T> readList(String fileName, Class<T> type) {
        return readListFromPath(Paths.get(DATA_DIR, fileName), type);
    }

    /**
     * Reads a JSON array from an arbitrary path (relative to the working dir or
     * absolute) into a list of the given type. Use for data sources that live
     * outside {@code test-data/}, such as the shared UAT contractor dump.
     */
    public static <T> List<T> readListFromPath(String filePath, Class<T> type) {
        return readListFromPath(Paths.get(filePath), type);
    }

    private static <T> List<T> readListFromPath(Path path, Class<T> type) {
        try (InputStream stream = Files.newInputStream(path)) {
            return MAPPER.readValue(stream,
                    MAPPER.getTypeFactory().constructCollectionType(List.class, type));
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to read test data: " + path, e);
        }
    }
}
