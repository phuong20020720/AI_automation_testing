package co.uk.ppac.web.pages;

import co.uk.ppac.core.base.BasePage;
import co.uk.ppac.core.config.ConfigReader;
import co.uk.ppac.web.locators.DashboardLocators;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;

/**
 * Dashboard page after successful login. Locators are best-effort because the
 * recon could not capture a logged-in session — they will be hardened during
 * Step 6 auto-heal once the first happy-path login succeeds. See
 * {@link DashboardLocators}.
 */
public class DashboardPage extends BasePage {

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    public boolean isOnDashboardUrl() {
        String url = driver.getCurrentUrl();
        return url.contains(ConfigReader.get("app.dashboardHost"))
                || url.contains(ConfigReader.get("app.dashboardPath"));
    }

    public boolean waitForDashboard(int timeoutSeconds) {
        String dashboardHost = ConfigReader.get("app.dashboardHost");
        String dashboardPath = ConfigReader.get("app.dashboardPath");
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                    .until(d -> {
                        String url = d.getCurrentUrl();
                        boolean inTransientSignIn = url.contains("/sign-in") || url.contains("authCode=");
                        boolean reachedFinal = url.contains(dashboardPath)
                                || (url.contains(dashboardHost) && !inTransientSignIn);
                        return reachedFinal;
                    });
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public boolean isLogoutButtonPresent() {
        return isPresent(DashboardLocators.USER_AVATAR_BUTTON);
    }

    public LoginPage logout() {
        wait.until(ExpectedConditions.elementToBeClickable(DashboardLocators.USER_AVATAR_BUTTON));
        List<org.openqa.selenium.WebElement> avatars = driver.findElements(DashboardLocators.USER_AVATAR_BUTTON);
        if (avatars.isEmpty()) {
            throw new IllegalStateException("Không tìm thấy user avatar button — không thể mở menu logout");
        }
        js().executeScript("arguments[0].click();", avatars.get(0));
        try { Thread.sleep(800); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
        wait.until(ExpectedConditions.presenceOfElementLocated(DashboardLocators.LOGOUT_MENU_ITEM));
        js().executeScript("arguments[0].click();", driver.findElement(DashboardLocators.LOGOUT_MENU_ITEM));
        return new LoginPage(driver);
    }
}
