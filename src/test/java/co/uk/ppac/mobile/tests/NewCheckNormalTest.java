package co.uk.ppac.mobile.tests;

import co.uk.ppac.mobile.data.Contractor;
import co.uk.ppac.mobile.screens.SkillCardScreen;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * "New Check" normal journey: a contractor with {@code skillCardJourney=true}
 * ({@code ppactest}) must reach the skill card step after completing
 * prefix → site → subcontractor → contact.
 *
 * <p>Skill card itself is only asserted to appear - completing it requires
 * uploading a real document.
 */
public class NewCheckNormalTest extends NewCheckBaseTest {

    private static final String NORMAL_PREFIX = "ppactest";

    @Test(groups = {"mobile", "newcheck", "journey", "smoke"},
            description = "Normal contractor (skillCardJourney=true) completes prefix → site → "
                    + "subcontractor → contact and reaches the skill card step")
    public void testNormalJourneyReachesSkillCard() {
        Contractor contractor = contractorByPrefix(NORMAL_PREFIX);

        completePrefixSiteAndContact(contractor);

        SkillCardScreen skillCard = new SkillCardScreen(driver);
        Assert.assertTrue(skillCard.isLoaded(),
                "Contractor '" + contractor.contractorName() + "' có skillCardJourney=true → "
                        + "sau Contact point app PHẢI chuyển đến bước 'Skill card verification'");
    }
}
