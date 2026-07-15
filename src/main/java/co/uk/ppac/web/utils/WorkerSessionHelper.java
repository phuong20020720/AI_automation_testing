package co.uk.ppac.web.utils;

import co.uk.ppac.core.config.ConfigReader;
import co.uk.ppac.web.pages.LoginPage;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

/**
 * Login bằng tài khoản `stgeo` (full permission) để vào module Worker Management.
 * Tách riêng để các test class tái sử dụng — không lặp logic login Flutter.
 */
public final class WorkerSessionHelper {

    private WorkerSessionHelper() {
    }

    public static void loginAsStgeo(WebDriver driver) {
        org.openqa.selenium.TimeoutException last = null;
        for (int attempt = 1; attempt <= 2; attempt++) {
            try {
                new LoginPage(driver)
                        .open(ConfigReader.get("app.baseUrl"))
                        .chooseEmailMethod()
                        .typeEmail(ConfigReader.get("login.stgeo.email"))
                        .typePassword(ConfigReader.get("login.stgeo.password"))
                        .submit();
                waitForDashboard(driver);
                return;
            } catch (org.openqa.selenium.TimeoutException e) {
                last = e;
                if (attempt == 2) {
                    throw e;
                }
                try { Thread.sleep(3000); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
            }
        }
        if (last != null) {
            throw last;
        }
    }

    private static void waitForDashboard(WebDriver driver) {
        String dashboardHost = ConfigReader.get("app.dashboardHost");
        new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(90))
                .until(d -> {
                    String url = d.getCurrentUrl();
                    return url.contains(dashboardHost) && !url.contains("/sign-in");
                });
        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(20))
                .until(d -> "complete".equals(((org.openqa.selenium.JavascriptExecutor) d)
                        .executeScript("return document.readyState")));
    }

}
