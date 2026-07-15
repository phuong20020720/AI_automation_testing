package co.uk.ppac.web.utils;

import co.uk.ppac.core.config.ConfigReader;
import co.uk.ppac.web.pages.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Login Compliant Portal bằng admin account — chia sẻ session cho M2 test suite.
 * Admin role thấy mọi contractor; landing page = Verifier Queue (same host as login).
 */
public final class CompliantPortalSession {

    private static final By FILTER_BUTTON = By.xpath(
            "//flt-semantics[@role='button' and contains(normalize-space(.),'Select status')]");
    private static final By EMAIL_PICKER_BUTTON = By.xpath(
            "//flt-semantics[@role='button' and normalize-space(.)='Sign in with Email']");

    private CompliantPortalSession() {
    }

    public static void loginAsAdmin(WebDriver driver) {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(ConfigReader.get("app.baseUrl"));

        // Sau khi load + enable semantics, có 2 trạng thái: (a) đã ở landing (session cached),
        // (b) đang ở method picker (chưa login). Đợi 1 trong 2 element xuất hiện trước khi quyết định.
        long deadline = System.currentTimeMillis() + 30_000;
        while (System.currentTimeMillis() < deadline) {
            if (!driver.findElements(FILTER_BUTTON).isEmpty()) {
                return;
            }
            if (!driver.findElements(EMAIL_PICKER_BUTTON).isEmpty()) {
                break;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (driver.findElements(FILTER_BUTTON).isEmpty()) {
            loginPage.chooseEmailMethod()
                    .typeEmail(ConfigReader.get("compliantportal.admin.email"))
                    .typePassword(ConfigReader.get("compliantportal.admin.password"))
                    .submit();
            waitForLanding(driver);
        }
    }

    private static void waitForLanding(WebDriver driver) {
        long deadline = System.currentTimeMillis() + 90_000;
        By placeholder = By.tagName("flt-semantics-placeholder");
        while (System.currentTimeMillis() < deadline) {
            if (!driver.findElements(FILTER_BUTTON).isEmpty()) {
                return;
            }
            try {
                if (!driver.findElements(placeholder).isEmpty()) {
                    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                            "var p=document.querySelector('flt-semantics-placeholder'); if(p) p.click();");
                }
            } catch (Exception ignored) {
            }
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        // Fallback: 30s extra để cover UAT slow window — tổng 120s trước khi throw.
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(d -> !d.findElements(FILTER_BUTTON).isEmpty());
    }
}
