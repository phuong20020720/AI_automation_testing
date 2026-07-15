package co.uk.ppac.mobile.screens;
import co.uk.ppac.core.base.BaseScreen;

import co.uk.ppac.mobile.locators.HomeLocators;
import io.appium.java_client.android.AndroidDriver;

/**
 * Screen Object for the PPAC Sandbox home screen - the "Wallet" dashboard shown
 * after a successful login. It carries a four-item bottom navigation bar:
 * New Check, Wallet, Notification, Profile.
 *
 * <p>Locators come from a live UI inspection of {@code com.ppac.app.sandbox}
 * v3.1.16. The bottom-nav items are ImageViews whose {@code content-desc}
 * includes the tab name (e.g. {@code "Wallet\nTab 2 of 4"}), so they are
 * matched with a {@code content-desc} 'contains' query. See {@link HomeLocators}.
 */
public class HomeScreen extends BaseScreen {

    public HomeScreen(AndroidDriver driver) {
        super(driver);
    }

    /**
     * Returns true once the home screen is displayed - i.e. login succeeded and
     * the bottom navigation bar is visible.
     */
    public boolean isWelcomeDisplayed() {
        return isDisplayed(HomeLocators.WALLET_TAB);
    }

    public void openNewCheck() {
        tap(HomeLocators.NEW_CHECK_TAB);
    }

    public void openWallet() {
        tap(HomeLocators.WALLET_TAB);
    }

    public void openNotifications() {
        tap(HomeLocators.NOTIFICATION_TAB);
    }

    public void openProfile() {
        tap(HomeLocators.PROFILE_TAB);
    }
}
