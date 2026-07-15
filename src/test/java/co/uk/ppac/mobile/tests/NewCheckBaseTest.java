package co.uk.ppac.mobile.tests;

import co.uk.ppac.core.base.MobileBaseTest;
import co.uk.ppac.core.config.AppConfig;
import co.uk.ppac.mobile.data.Contractor;
import co.uk.ppac.mobile.screens.ContactPointScreen;
import co.uk.ppac.mobile.screens.HomeScreen;
import co.uk.ppac.mobile.screens.NewCheckScreen;
import co.uk.ppac.mobile.screens.SelectSiteScreen;
import co.uk.ppac.mobile.screens.WelcomeScreen;
import co.uk.ppac.core.utils.DataGenerator;
import co.uk.ppac.core.utils.TestDataReader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;

/**
 * Shared lifecycle and journey steps for the "New Check" submission flow.
 *
 * <p>Each concrete journey ({@link NewCheckNormalTest}, {@link NewCheckNoSkillCardTest})
 * shares the same first three steps - prefix → site → subcontractor → contact -
 * and only differs in what must come afterwards. Those shared steps live here so
 * the individual test classes stay focused on their distinct assertion.
 *
 * <p>Which steps a contractor goes through is decided by
 * {@code config.verificationFlows} in {@code input/Contractor/uat-db.contractors.json}
 * (override the path with {@code -Dcontractors.data.file=...}). Each journey looks
 * its contractor up by prefix, so name/site/subcontractor stay sourced from the
 * file - no hard-coded business data.
 */
public abstract class NewCheckBaseTest extends MobileBaseTest {

    protected static final String DATA_FILE_KEY = "contractors.data.file";
    protected static final String DEFAULT_DATA_FILE = "../input/Contractor/uat-db.contractors.json";

    protected NewCheckScreen newCheckScreen;

    @BeforeMethod(alwaysRun = true)
    public void openNewCheckTab() {
        new WelcomeScreen(driver).openEmailLogin().login(
                AppConfig.getRequired("test.user.email"),
                AppConfig.getRequired("test.user.password"));

        HomeScreen home = new HomeScreen(driver);
        Assert.assertTrue(home.isWelcomeDisplayed(),
                "Phải đăng nhập thành công trước khi mở tab New Check");

        home.openNewCheck();
        newCheckScreen = new NewCheckScreen(driver);
        Assert.assertTrue(newCheckScreen.isLoaded(),
                "Màn hình 'Enter company prefix' phải hiển thị sau khi mở tab New Check");
    }

    // ---------- Shared journey steps (prefix → site → subcontractor → contact) ----------

    /**
     * Drives steps 1-3 for the given contractor, asserting each transition. Both
     * journeys share these steps; they only differ in what must come after.
     */
    protected void completePrefixSiteAndContact(Contractor contractor) {
        // Step 1: prefix → suggestion → Continue
        newCheckScreen.enterPrefix(contractor.contractorPrefix());
        Assert.assertTrue(newCheckScreen.isContractorSuggestionDisplayed(contractor.contractorPrefix()),
                "Prefix '" + contractor.contractorPrefix() + "' phải hiện suggestion '"
                        + contractor.contractorName() + "'");
        newCheckScreen.selectContractorSuggestion(contractor.contractorPrefix());
        newCheckScreen.tapContinue();

        // Step 2: site + subcontractor (dò danh sách, chọn vị trí đầu tiên) → Continue
        SelectSiteScreen selectSite = new SelectSiteScreen(driver);
        Assert.assertTrue(selectSite.isLoaded(),
                "Sau Continue ở bước prefix, app PHẢI chuyển đến bước 'Select site location'");
        String site = selectSite.selectFirstSiteLocation();
        Assert.assertTrue(selectSite.isSiteSelected(site),
                "Field site phải hiển thị '" + site + "' sau khi chọn vị trí đầu tiên");
        String subcontractor = selectSite.selectFirstSubcontractor();
        Assert.assertTrue(selectSite.isSubcontractorSelected(subcontractor),
                "Field subcontractor phải hiển thị '" + subcontractor + "' sau khi chọn vị trí đầu tiên");
        selectSite.tapContinue();

        // Step 3: contact email → Continue
        ContactPointScreen contactPoint = new ContactPointScreen(driver);
        Assert.assertTrue(contactPoint.isLoaded(),
                "Sau Continue ở bước site, app PHẢI chuyển đến bước 'Contact point'");
        contactPoint.enterCompanyContactEmail(DataGenerator.generateEmail("newCheckContact"));
        contactPoint.tapContinue();
    }

    /** Loads the contractor with the given prefix from the UAT data file. */
    protected Contractor contractorByPrefix(String prefix) {
        String dataFile = AppConfig.get(DATA_FILE_KEY, DEFAULT_DATA_FILE);
        Contractor contractor = TestDataReader.readListFromPath(dataFile, Contractor.class).stream()
                .filter(c -> prefix.equals(c.contractorPrefix()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Không tìm thấy contractor prefix '" + prefix + "' trong " + dataFile));
        Assert.assertTrue(contractor.isFlowReady(),
                "Contractor '" + prefix + "' cần có cả site và subcontractor để chạy luồng New Check");
        return contractor;
    }
}
