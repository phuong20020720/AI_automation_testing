package co.uk.ppac.web.recon;

import co.uk.ppac.core.driver.DriverFactory;
import co.uk.ppac.web.pages.WorkerListPage;
import co.uk.ppac.web.utils.WorkerSessionHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Phase 5 recon — M9 Open Inception + M10 UI/UX states.
 *
 * Mục tiêu:
 * - Tìm affordance "Open Inception" trên Profile (phase4 thấy buttons [P][PPAC Report][Delete this submission])
 * - Click thử "PPAC Report" + "P" → capture modal/dialog mở ra
 * - Capture empty list state (search no-result)
 * - Resize viewport 1280 / 1920 / 375 → đo body overflow
 *
 * Output: plans/automation/ppac_worker_management/recon/phase5_*.
 */
public class WorkerManagementPhase5Recon {

    private static final Path OUT_DIR = Paths.get("..", "docs", "plans", "automation", "ppac_worker_management", "recon");

    public static void main(String[] args) throws Exception {
        Files.createDirectories(OUT_DIR);
        WebDriver driver = DriverFactory.create();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            WorkerSessionHelper.loginAsStgeo(driver);
            WorkerListPage list = new WorkerListPage(driver).open();

            // ─── M9: Profile → button discovery ───
            int idx = list.firstRowWithFullProfileButton();
            if (idx >= 0) {
                list.clickFullProfileAt(idx);
                sleep(3000);
                Files.writeString(OUT_DIR.resolve("phase5_profile.html"), driver.getPageSource());

                StringBuilder sb = new StringBuilder();
                List<WebElement> buttons = driver.findElements(By.cssSelector("button, a[role='button'], [role='button']"));
                sb.append("Total interactive (button/role=button): ").append(buttons.size()).append("\n\n");
                for (int i = 0; i < buttons.size(); i++) {
                    WebElement b = buttons.get(i);
                    String text = b.getText().trim().replaceAll("\\s+", " ");
                    if (text.length() > 70) {
                        text = text.substring(0, 70);
                    }
                    String aria = nz(b.getAttribute("aria-label"));
                    String dataSlot = nz(b.getAttribute("data-slot"));
                    String lucide = "";
                    List<WebElement> svgs = b.findElements(By.cssSelector("svg"));
                    if (!svgs.isEmpty()) {
                        String cls = nz(svgs.get(0).getAttribute("class"));
                        for (String tok : cls.split("\\s+")) {
                            if (tok.startsWith("lucide-")) {
                                lucide = tok;
                                break;
                            }
                        }
                    }
                    sb.append(String.format("[%d] tag=%s visible=%s data-slot=%s svg=%s aria='%s' text='%s'%n",
                            i, b.getTagName(), b.isDisplayed(), dataSlot, lucide, aria, text));
                }
                Files.writeString(OUT_DIR.resolve("phase5_profile_buttons.txt"), sb.toString());
                System.out.println("Saved phase5_profile_buttons.txt (" + buttons.size() + " buttons)");

                // Try clicking "PPAC Report"
                clickAndCapture(driver, js,
                        "//button[contains(normalize-space(.),'PPAC Report') or contains(normalize-space(.),'Inception')]",
                        "phase5_after_ppac_report");

                // reload profile, try "P" button (icon-only?)
                driver.navigate().refresh();
                sleep(3000);
                List<WebElement> pButtons = driver.findElements(By.xpath(
                        "//button[normalize-space(.)='P']"));
                if (!pButtons.isEmpty()) {
                    js.executeScript("arguments[0].click();", pButtons.get(0));
                    sleep(2500);
                    Files.writeString(OUT_DIR.resolve("phase5_after_p_button.html"), driver.getPageSource());
                    System.out.println("Saved phase5_after_p_button.html");
                    pressEscape(driver);
                }
            } else {
                System.out.println("No worker with Full Profile button");
            }

            // ─── M10: empty state ───
            list = new WorkerListPage(driver).open();
            list.search("zzz_nonexistent_" + System.currentTimeMillis());
            sleep(2000);
            Files.writeString(OUT_DIR.resolve("phase5_empty_state.html"), driver.getPageSource());
            StringBuilder emptyMeta = new StringBuilder();
            emptyMeta.append("rowCount after no-result search: ")
                    .append(driver.findElements(By.cssSelector("tr[data-slot='table-row']")).size()).append("\n");
            emptyMeta.append("body text snippet: ").append(
                    js.executeScript("return document.body.innerText.slice(0,800);")).append("\n");
            Files.writeString(OUT_DIR.resolve("phase5_empty_state_meta.txt"), emptyMeta.toString());
            System.out.println("Saved phase5_empty_state");

            // ─── M10: viewport overflow ───
            list = new WorkerListPage(driver).open();
            StringBuilder vp = new StringBuilder();
            for (int[] size : new int[][] {{1920, 1080}, {1280, 720}, {375, 667}}) {
                driver.manage().window().setSize(new Dimension(size[0], size[1]));
                sleep(1500);
                Long scrollW = (Long) js.executeScript("return document.body.scrollWidth;");
                Long clientW = (Long) js.executeScript("return document.body.clientWidth;");
                Long innerW = (Long) js.executeScript("return window.innerWidth;");
                boolean tableVisible = !driver.findElements(By.cssSelector("table[data-slot='table']")).isEmpty();
                vp.append(String.format("viewport %dx%d → innerWidth=%d bodyScrollW=%d bodyClientW=%d overflow=%d tableVisible=%s%n",
                        size[0], size[1], innerW, scrollW, clientW, scrollW - clientW, tableVisible));
            }
            Files.writeString(OUT_DIR.resolve("phase5_viewport_overflow.txt"), vp.toString());
            System.out.println("Saved phase5_viewport_overflow.txt");
            System.out.println(vp);
        } finally {
            DriverFactory.quit();
        }
    }

    private static void clickAndCapture(WebDriver driver, JavascriptExecutor js, String xpath, String name) {
        try {
            List<WebElement> els = driver.findElements(By.xpath(xpath));
            if (els.isEmpty()) {
                System.out.println("No element for: " + name + " (xpath: " + xpath + ")");
                return;
            }
            js.executeScript("arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", els.get(0));
            sleep(3000);
            Files.writeString(OUT_DIR.resolve(name + ".html"), driver.getPageSource());
            // probe for dialog
            int dialogs = driver.findElements(By.cssSelector("div[role='dialog']")).size();
            String url = driver.getCurrentUrl();
            Files.writeString(OUT_DIR.resolve(name + "_meta.txt"),
                    "url: " + url + "\ndialogCount: " + dialogs + "\n");
            System.out.println("Saved " + name + " (dialogs=" + dialogs + ", url=" + url + ")");
            pressEscape(driver);
        } catch (Exception e) {
            System.out.println("clickAndCapture error for " + name + ": " + e.getMessage());
        }
    }

    private static void pressEscape(WebDriver driver) {
        try {
            new org.openqa.selenium.interactions.Actions(driver)
                    .sendKeys(org.openqa.selenium.Keys.ESCAPE).perform();
            sleep(1000);
        } catch (Exception ignored) {
        }
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
