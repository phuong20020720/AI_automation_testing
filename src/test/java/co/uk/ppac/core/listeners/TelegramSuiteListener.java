package co.uk.ppac.core.listeners;

import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Sends a Telegram summary at the end of every TestNG suite run. Registered
 * automatically via {@code META-INF/services/org.testng.ITestNGListener}, so it
 * fires for any {@code testng-*.xml} without editing the suite files.
 *
 * <p>The project label shown in the message defaults to "PPAC Automation" and
 * can be overridden per run with {@code -Dproject.label=...} (the web/mobile
 * Maven profiles set it to "Selenium Web" / "Appium App").
 */
public class TelegramSuiteListener implements ISuiteListener {

    private static final String PROJECT = System.getProperty("project.label", "PPAC Automation");
    private static final int MAX_FAILED_NAMES = 15;

    @Override
    public void onFinish(ISuite suite) {
        // Allow a runner script to silence the listener (-Dtelegram.notify=false)
        // when it sends its own, richer message with the report file path.
        if ("false".equalsIgnoreCase(System.getProperty("telegram.notify"))) {
            return;
        }

        int passed = 0;
        int failed = 0;
        int skipped = 0;
        long durationMs = 0;
        Set<String> failedNames = new LinkedHashSet<>();

        for (ISuiteResult result : suite.getResults().values()) {
            ITestContext context = result.getTestContext();
            passed += context.getPassedTests().size();
            failed += context.getFailedTests().size();
            skipped += context.getSkippedTests().size();
            durationMs += context.getEndDate().getTime() - context.getStartDate().getTime();
            collectFailedNames(context.getFailedTests(), failedNames);
        }

        int total = passed + failed + skipped;
        String icon = failed > 0 ? "❌" : "✅"; // ❌ / ✅
        StringBuilder message = new StringBuilder();
        message.append("EM YÊU ƠI ❤️\n");
        message.append("Anh vừa chạy xong test ").append(PROJECT).append(" cho em rồi nè! ").append(icon).append("\n\n");
        message.append("📊 Suite: ").append(suite.getName()).append("\n");
        message.append("✅ PASS: ").append(passed).append("\n");
        message.append("❌ FAIL: ").append(failed).append("\n");
        message.append("⏭️ SKIP: ").append(skipped).append("\n");
        message.append("🔢 Tổng: ").append(total).append("\n");
        message.append("⏱️ Thời gian: ").append(formatDuration(durationMs)).append("\n");

        if (!failedNames.isEmpty()) {
            message.append("\n🔴 Test FAIL:\n");
            int shown = 0;
            for (String name : failedNames) {
                if (shown++ >= MAX_FAILED_NAMES) {
                    message.append("  ... và ").append(failedNames.size() - MAX_FAILED_NAMES).append(" test khác\n");
                    break;
                }
                message.append("  • ").append(name).append("\n");
            }
        }

        TelegramNotifier.send(message.toString());
    }

    private static void collectFailedNames(IResultMap failedTests, Set<String> target) {
        for (ITestNGMethod method : failedTests.getAllMethods()) {
            target.add(method.getTestClass().getRealClass().getSimpleName() + "." + method.getMethodName());
        }
    }

    private static String formatDuration(long millis) {
        long totalSeconds = millis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return minutes > 0 ? minutes + "m " + seconds + "s" : seconds + "s";
    }
}
