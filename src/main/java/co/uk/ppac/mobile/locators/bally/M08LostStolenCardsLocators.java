package co.uk.ppac.mobile.locators.bally;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * M08 "Lost & Stolen Sentinel Cards"
 * (BALLYCOMMON-RAIL onboarding; chip stepper "Lost Sentinel Cards").
 *
 * <p>Trích THẬT từ {@code appium_get_page_source} của {@code com.ppac.app.sandbox}
 * trên emulator-5554 (Pixel7, Android 13), ngày 2026-06-16.
 *
 * <p>Màn ngắn: heading + 1 checkbox xác nhận (clickable View, content-desc =
 * nguyên văn, có ký hiệu £) + Next.
 *
 */
public final class M08LostStolenCardsLocators {

    private M08LostStolenCardsLocators() {
    }

    public static final By STEP_CHIP = AppiumBy.accessibilityId("Lost Sentinel Cards");

    /** Heading (content-desc 2 dòng "Lost & Stolen\nSentinel Cards"). */
    public static final By SCREEN_HEADING = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionStartsWith(\"Lost & Stolen\")");

    /** Checkbox xác nhận (clickable View) — content-desc nguyên văn (TC_112), có £. */
    public static final By CONFIRM_CHECKBOX = AppiumBy.accessibilityId(
            "I confirm that I will pay £25 + VAT if my Sentinel card is lost or stolen to Ballycommon");

    public static final By NEXT_BUTTON = AppiumBy.accessibilityId("Next →");
}
