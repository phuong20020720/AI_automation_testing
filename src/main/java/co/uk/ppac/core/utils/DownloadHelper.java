package co.uk.ppac.core.utils;

import co.uk.ppac.core.driver.DriverFactory;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

public final class DownloadHelper {

    private DownloadHelper() {
    }

    /** Xóa toàn bộ file trong download dir để test cô lập. */
    public static void cleanDownloadDir() {
        File[] files = DriverFactory.DOWNLOAD_DIR.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }

    /**
     * Chờ đến khi xuất hiện file match prefix/suffix trong download dir và download xong
     * (không còn `.crdownload` partial). Trả về File hoặc Optional.empty nếu timeout.
     */
    public static Optional<File> waitForNewFile(String suffix, long timeoutMs) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        FileFilter completed = f -> f.isFile()
                && f.getName().toLowerCase().endsWith(suffix.toLowerCase())
                && !f.getName().endsWith(".crdownload");
        while (System.currentTimeMillis() < deadline) {
            File[] files = DriverFactory.DOWNLOAD_DIR.listFiles(completed);
            if (files != null && files.length > 0) {
                return Arrays.stream(files).max(Comparator.comparingLong(File::lastModified));
            }
            File[] partials = DriverFactory.DOWNLOAD_DIR.listFiles(
                    f -> f.getName().endsWith(".crdownload"));
            if (partials != null && partials.length > 0) {
                sleepQuiet(500);
                continue;
            }
            sleepQuiet(500);
        }
        return Optional.empty();
    }

    private static void sleepQuiet(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
