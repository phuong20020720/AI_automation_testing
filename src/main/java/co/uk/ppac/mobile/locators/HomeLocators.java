package co.uk.ppac.mobile.locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Locators tập trung cho Home ("Wallet") — bottom navigation 4 tab.
 * Tab là ImageView với content-desc chứa tên tab (vd "Wallet\nTab 2 of 4").
 */
public final class HomeLocators {

    private HomeLocators() {
    }

    public static final By NEW_CHECK_TAB = AppiumBy.xpath(
            "//android.widget.ImageView[contains(@content-desc,'New Check')]");
    public static final By WALLET_TAB = AppiumBy.xpath(
            "//android.widget.ImageView[contains(@content-desc,'Wallet')]");
    public static final By NOTIFICATION_TAB = AppiumBy.xpath(
            "//android.widget.ImageView[contains(@content-desc,'Notification')]");
    public static final By PROFILE_TAB = AppiumBy.xpath(
            "//android.widget.ImageView[contains(@content-desc,'Profile')]");
}
