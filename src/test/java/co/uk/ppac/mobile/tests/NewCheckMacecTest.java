package co.uk.ppac.mobile.tests;

import co.uk.ppac.core.base.MobileBaseTest;
import co.uk.ppac.core.config.AppConfig;
import co.uk.ppac.mobile.screens.ContactPointScreen;
import co.uk.ppac.mobile.screens.HomeScreen;
import co.uk.ppac.mobile.screens.NewCheckScreen;
import co.uk.ppac.mobile.screens.SelectSiteScreen;
import co.uk.ppac.mobile.screens.SkillCardScreen;
import co.uk.ppac.mobile.screens.WelcomeScreen;
import co.uk.ppac.core.utils.DataGenerator;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * "New Check" flow exercised with the company prefix {@code macec}, which the
 * PPAC Sandbox resolves to the contractor "Mace Construct".
 *
 * <p>The flow has at least four steps: (1) prefix entry → suggestion pick,
 * (2) site + subcontractor selection, (3) contact-point email, (4) skill card
 * verification. Tests cover steps 1-3 fully; step 4 only asserts the screen
 * appears, since completing it requires uploading a real document.
 */
public class NewCheckMacecTest extends MobileBaseTest {

    /** Company prefix under test - resolves to "Mace Construct" in the Sandbox. */
    private static final String MACEC_PREFIX = "macec";
    private static final String SITE_LOCATION = "Astra Zeneca";
    private static final String SUBCONTRACTOR = "ADDC";

    private NewCheckScreen newCheckScreen;

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

    // ---------- Step 1: company prefix ----------

    @Test(groups = {"mobile", "macec", "smoke"},
            description = "New Check step 1 shows the prefix prompt, help text and Continue button")
    public void testMacecPrefixEntryShowsPromptAndContinue() {
        Assert.assertTrue(newCheckScreen.isHelpTextDisplayed(),
                "Phải hiển thị help text giải thích mục đích của prefix");
        Assert.assertTrue(newCheckScreen.isContinueDisplayed(),
                "Nút Continue phải hiển thị trên màn hình prefix entry");
    }

    @Test(groups = {"mobile", "macec", "regression"},
            description = "Typing prefix 'macec' is accepted by the field")
    public void testMacecPrefixIsAcceptedByField() {
        newCheckScreen.enterPrefix(MACEC_PREFIX);
        Assert.assertEquals(newCheckScreen.getEnteredPrefix(), MACEC_PREFIX,
                "Field phải giữ đúng prefix '" + MACEC_PREFIX + "' sau khi nhập");
    }

    @Test(groups = {"mobile", "macec", "regression"},
            description = "Typing prefix 'macec' surfaces the 'Mace Construct - macec' suggestion")
    public void testMacecPrefixShowsContractorSuggestion() {
        newCheckScreen.enterPrefix(MACEC_PREFIX);
        Assert.assertTrue(newCheckScreen.isContractorSuggestionDisplayed(MACEC_PREFIX),
                "App phải hiện dropdown suggestion 'Mace Construct - " + MACEC_PREFIX + "' "
                        + "khi prefix khớp với contractor đã đăng ký");
    }

    @Test(groups = {"mobile", "macec", "regression"},
            description = "Without selecting the suggestion, tapping Continue must not progress")
    public void testMacecPrefixWithoutSelectionDoesNotProgress() {
        newCheckScreen.enterPrefix(MACEC_PREFIX);
        newCheckScreen.tapContinue();
        Assert.assertTrue(newCheckScreen.isLoaded(),
                "Continue chỉ activate sau khi chọn suggestion — "
                        + "nếu chỉ nhập prefix mà không chọn dropdown, màn hình prefix entry "
                        + "PHẢI vẫn hiển thị");
    }

    @Test(groups = {"mobile", "macec", "smoke"},
            description = "Selecting 'Mace Construct - macec' from the dropdown and tapping "
                    + "Continue advances to the Select Site step")
    public void testMacecHappyPathAdvancesToSelectSiteStep() {
        SelectSiteScreen selectSite = goToSelectSiteStep();
        Assert.assertTrue(selectSite.isLoaded(),
                "Sau khi chọn 'Mace Construct - " + MACEC_PREFIX + "' và tap Continue, "
                        + "app PHẢI chuyển đến bước 'Select site location'");
    }

    // ---------- Step 2: site + subcontractor ----------

    @Test(groups = {"mobile", "macec", "regression"},
            description = "Picking a site from the modal populates the site field")
    public void testMacecStep2SelectSitePopulatesField() {
        SelectSiteScreen selectSite = goToSelectSiteStep();
        selectSite.selectSiteLocation(SITE_LOCATION);
        Assert.assertTrue(selectSite.isSiteSelected(SITE_LOCATION),
                "Field 'Select site location' phải hiển thị '" + SITE_LOCATION
                        + "' sau khi chọn từ dropdown modal");
    }

    @Test(groups = {"mobile", "macec", "regression"},
            description = "Picking a subcontractor from the modal populates the subcontractor field")
    public void testMacecStep2SelectSubcontractorPopulatesField() {
        SelectSiteScreen selectSite = goToSelectSiteStep();
        selectSite.selectSiteLocation(SITE_LOCATION);
        selectSite.selectSubcontractor(SUBCONTRACTOR);
        Assert.assertTrue(selectSite.isSubcontractorSelected(SUBCONTRACTOR),
                "Field 'Select subcontractor' phải hiển thị '" + SUBCONTRACTOR
                        + "' sau khi chọn từ dropdown modal");
    }

    @Test(groups = {"mobile", "macec", "smoke"},
            description = "Step 2 Continue with both selections advances to the Contact point step")
    public void testMacecStep2ContinueAdvancesToContactPoint() {
        ContactPointScreen contactPoint = goToContactPointStep();
        Assert.assertTrue(contactPoint.isLoaded(),
                "Sau khi chọn site '" + SITE_LOCATION + "' + subcontractor '"
                        + SUBCONTRACTOR + "' và tap Continue, app PHẢI chuyển đến bước "
                        + "'Contact point'");
    }

    // ---------- Step 3: contact point ----------

    @Test(groups = {"mobile", "macec", "regression"},
            description = "Step 3 displays the company contact email label")
    public void testMacecStep3ShowsEmailLabel() {
        ContactPointScreen contactPoint = goToContactPointStep();
        Assert.assertTrue(contactPoint.isEmailLabelDisplayed(),
                "Bước Contact point phải hiển thị label 'Company contact email "
                        + "(Manager, HR, Director)'");
    }

    @Test(groups = {"mobile", "macec", "smoke"},
            description = "Step 3 Continue with a valid email advances to the Skill card step")
    public void testMacecStep3ContinueAdvancesToSkillCardStep() {
        ContactPointScreen contactPoint = goToContactPointStep();
        String contactEmail = DataGenerator.generateEmail("macecContact");
        contactPoint.enterCompanyContactEmail(contactEmail);
        contactPoint.tapContinue();

        SkillCardScreen skillCard = new SkillCardScreen(driver);
        Assert.assertTrue(skillCard.isLoaded(),
                "Sau khi nhập contact email '" + contactEmail + "' và tap Continue, "
                        + "app PHẢI chuyển đến bước 'Skill card verification'");
    }

    // ---------- Navigation helpers ----------

    /** Completes step 1 and returns the step-2 screen object. */
    private SelectSiteScreen goToSelectSiteStep() {
        newCheckScreen.enterPrefix(MACEC_PREFIX);
        newCheckScreen.selectContractorSuggestion(MACEC_PREFIX);
        newCheckScreen.tapContinue();
        return new SelectSiteScreen(driver);
    }

    /** Completes steps 1-2 and returns the step-3 screen object. */
    private ContactPointScreen goToContactPointStep() {
        SelectSiteScreen selectSite = goToSelectSiteStep();
        selectSite.selectSiteLocation(SITE_LOCATION);
        selectSite.selectSubcontractor(SUBCONTRACTOR);
        selectSite.tapContinue();
        return new ContactPointScreen(driver);
    }
}
