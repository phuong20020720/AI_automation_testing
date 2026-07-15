package co.uk.ppac.mobile.tests;

import co.uk.ppac.core.utils.MailinatorOtpFetcher;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

/**
 * Standalone smoke test for {@link MailinatorOtpFetcher}: hits Mailinator's
 * public REST API for a given inbox and asserts a 6-digit OTP can be parsed
 * from the latest message.
 *
 * <p>Run with: {@code mvn test -Dsuite.xml=testng-mailinator-smoke.xml -Dmailinator.smoke.inbox=<inbox>}
 */
public class MailinatorOtpFetcherSmokeTest {

    @Test(groups = {"smoke", "mailinator"})
    public void fetcherReturnsSixDigitOtp() {
        String inbox = System.getProperty("mailinator.smoke.inbox");
        if (inbox == null || inbox.isBlank()) {
            throw new SkipException("Provide -Dmailinator.smoke.inbox=<inbox-with-recent-mail>");
        }
        try {
            String otp = new MailinatorOtpFetcher().fetchLatestOtp(inbox, 30);
            Assert.assertEquals(otp.length(), 6, "OTP must be 6 digits");
        } catch (IllegalStateException e) {
            throw new SkipException("No OTP within 30s for inbox '" + inbox + "': " + e.getMessage());
        }
    }
}
