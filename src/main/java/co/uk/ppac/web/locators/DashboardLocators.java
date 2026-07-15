package co.uk.ppac.web.locators;

import org.openqa.selenium.By;

/**
 * Locators tập trung cho Dashboard (React/Radix UI) — avatar + menu logout.
 */
public final class DashboardLocators {

    private DashboardLocators() {
    }

    public static final By USER_AVATAR_BUTTON = By.xpath(
            "//button[starts-with(@id,'radix-') and string-length(normalize-space(.))=1]");
    public static final By LOGOUT_MENU_ITEM = By.xpath(
            "//*[("
                    + "translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='logout'"
                    + " or translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='sign out'"
                    + " or translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='log out'"
                    + ") and (self::button or self::a or @role='menuitem' or @role='button')]");
}
