package co.uk.ppac.web.recon;

import co.uk.ppac.core.driver.DriverFactory;
import co.uk.ppac.web.pages.WorkerListPage;
import co.uk.ppac.web.utils.WorkerSessionHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Capture Profile của các workers thuộc các status khác nhau + tìm "Open Inception"
 * button. Save mọi button có text/aria-label/svg-class để locator discovery.
 */
public class InceptionRecon {

    private static final Path OUT_DIR = Paths.get("..", "docs", "plans", "automation", "ppac_worker_management", "recon");

    public static void main(String[] args) throws Exception {
        Files.createDirectories(OUT_DIR);
        WebDriver driver = DriverFactory.create();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            WorkerSessionHelper.loginAsStgeo(driver);
            WorkerListPage list = new WorkerListPage(driver).open();

            for (String status : new String[] {"Active", "Go to Site"}) {
                int idx = list.firstRowIndexWithStatus(status);
                if (idx < 0) {
                    System.out.println("No worker found with status=" + status);
                    continue;
                }
                System.out.println("Found row " + idx + " with status=" + status + " code=" + list.codeOfRow(idx));
                list.clickFullProfileAt(idx);
                Thread.sleep(3000);
                String safeName = status.replace(" ", "_");
                Files.writeString(OUT_DIR.resolve("10_profile_" + safeName + ".html"), driver.getPageSource());

                // Dump all buttons summary
                StringBuilder summary = new StringBuilder();
                List<WebElement> buttons = driver.findElements(By.cssSelector("button"));
                summary.append("Total buttons: ").append(buttons.size()).append("\n");
                for (int i = 0; i < buttons.size(); i++) {
                    WebElement b = buttons.get(i);
                    String text = b.getText().trim().replaceAll("\\s+", " ");
                    if (text.length() > 60) text = text.substring(0, 60);
                    String aria = b.getAttribute("aria-label");
                    String dataSlot = b.getAttribute("data-slot");
                    boolean visible = b.isDisplayed();
                    // Try to find a lucide svg inside
                    String lucide = "";
                    List<WebElement> svgs = b.findElements(By.cssSelector("svg.lucide"));
                    if (!svgs.isEmpty()) {
                        String cls = svgs.get(0).getAttribute("class");
                        for (String tok : cls.split("\\s+")) {
                            if (tok.startsWith("lucide-")) { lucide = tok; break; }
                        }
                    }
                    summary.append(String.format("[%d] visible=%s data-slot=%s svg=%s aria='%s' text='%s'%n",
                            i, visible, dataSlot, lucide, aria == null ? "" : aria, text));
                }
                Files.writeString(OUT_DIR.resolve("10_profile_" + safeName + "_buttons.txt"), summary.toString());
                System.out.println("Saved profile " + safeName + " (" + buttons.size() + " buttons)");

                // Also try clicking any button labeled "Open Inception" or with svg lucide-scan-line
                List<WebElement> possibleInception = driver.findElements(By.xpath(
                        "//button[contains(translate(normalize-space(.),'INCEPTION','inception'),'inception')]"));
                if (!possibleInception.isEmpty()) {
                    System.out.println("Found Inception button by text!");
                    js.executeScript("arguments[0].click();", possibleInception.get(0));
                    Thread.sleep(2500);
                    Files.writeString(OUT_DIR.resolve("11_inception_modal_" + safeName + ".html"), driver.getPageSource());
                    System.out.println("Saved inception modal for " + safeName);
                    break;
                } else {
                    System.out.println("No 'Inception' button by text for " + status);
                }
                driver.navigate().back();
                Thread.sleep(2000);
            }
        } finally {
            DriverFactory.quit();
        }
    }
}
