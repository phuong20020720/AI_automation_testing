package co.uk.ppac.core.base;

import co.uk.ppac.core.utils.MobileGestures;
import co.uk.ppac.core.utils.WaitHelper;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Base class for every Screen Object. Holds the driver plus shared
 * synchronisation and interaction helpers.
 *
 * <p>Screen classes describe user-facing behaviour; they never contain
 * assertions - those belong in the test classes.
 */
public abstract class BaseScreen {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseScreen.class);

    protected final AndroidDriver driver;
    protected final WaitHelper wait;
    protected final MobileGestures gestures;

    protected BaseScreen(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WaitHelper(driver);
        this.gestures = new MobileGestures(driver);
    }

    /** Waits for the element to be clickable, then taps it. */
    protected void tap(By locator) {
        wait.waitForClickable(locator).click();
    }

    /**
     * Tap với timeout TÙY BIẾN (giây). Dùng cho phần tử tải chậm hơn explicit.wait
     * mặc định — vd option trong dialog dropdown lấy DỮ LIỆU BACKEND (Consultant/
     * Trade/Payroll), đôi khi >15s mới hiện.
     */
    protected void tapWhenReady(By locator, int seconds) {
        new WebDriverWait(driver, Duration.ofSeconds(seconds))
                .until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    /**
     * Waits for the field to be visible, focuses it, clears it, then types.
     *
     * <p>The {@code click()} is required for Flutter text fields: they only
     * accept input once focused (an active input connection exists).
     */
    protected void type(By locator, String text) {
        WebElement field = wait.waitForVisible(locator);
        field.click();
        field.clear();
        field.sendKeys(text);
    }

    /**
     * Nhập text vào ô <b>Flutter View</b> (không phải EditText thật).
     *
     * <p>App Flutter dựng ô nhập là {@code android.view.View} (không
     * {@code resource-id}); gọi {@code element.sendKeys()} thường đặt text vào
     * lớp native nhưng Flutter KHÔNG nhận onChange ⇒ giá trị "biến mất". Cách tin
     * cậy: tap để focus (Flutter mở input connection), rồi gửi <b>key events thật</b>
     * tới phần tử đang focus qua W3C Actions ({@link Actions#sendKeys}) — tương
     * đương Appium {@code setValue w3cActions}.
     *
     * <p>KHÔNG clear: dùng cho ô đang trống (vd Next of Kin) hoặc khi muốn nối tiếp.
     */
    protected void typeFlutter(By locator, String text) {
        WebElement field = wait.waitForVisible(locator);
        field.click(); // focus → Flutter mở input connection
        new Actions(driver).sendKeys(text).perform(); // key thật tới ô đang focus
    }

    /**
     * Hides the on-screen keyboard if shown. Best-effort: keyboard hiding can
     * be flaky on the emulator, so a failure here is logged, not thrown.
     */
    protected void hideKeyboard() {
        try {
            if (driver.isKeyboardShown()) {
                driver.hideKeyboard();
            }
        } catch (RuntimeException e) {
            LOGGER.warn("hideKeyboard() failed, continuing: {}", e.getMessage());
        }
    }

    /** Waits for the element to be visible, then returns its text. */
    protected String readText(By locator) {
        return wait.waitForVisible(locator).getText();
    }

    /** Returns true if the element becomes visible within the explicit-wait window. */
    protected boolean isDisplayed(By locator) {
        try {
            return wait.waitForVisible(locator).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Kiểm tra NHANH (không chờ) phần tử có TỒN TẠI trong cây hiện tại không.
     * Dùng {@code findElements} (trả ngay, list rỗng nếu không có) — hợp cho việc
     * dò trạng thái tức thời như "dialog còn mở không?".
     */
    protected boolean isPresentNow(By locator) {
        return !driver.findElements(locator).isEmpty();
    }
}
