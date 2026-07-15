package co.uk.ppac.mobile.locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Locators tập trung cho onboarding Document upload (SKELETON — chờ recon chi tiết
 * sau khi Selfie mock sẵn sàng).
 */
public final class DocumentUploadLocators {

    private DocumentUploadLocators() {
    }

    public static final By SCREEN_TITLE_CANDIDATES = AppiumBy.xpath(
            "//*[@text='Identity Documents' or @text='Right to Work' "
                    + "or @content-desc='Identity Documents' or @content-desc='Right to Work']");
    public static final By CONTINUE_BUTTON = AppiumBy.accessibilityId("Continue");
    public static final By SKIP_BUTTON = AppiumBy.accessibilityId("Skip for now");
    public static final By LOGOUT_ICON = AppiumBy.xpath(
            "//android.view.View[@clickable='true' and @bounds[contains(., '[979,')]]");
}
