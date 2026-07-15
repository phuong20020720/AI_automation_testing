package co.uk.ppac.mobile.tests;

import co.uk.ppac.mobile.data.Contractor;
import co.uk.ppac.mobile.screens.SkillCardScreen;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * "New Check" no-skill-card journey: a contractor with {@code skillCardJourney=false}
 * ({@code jcb}) completes the first three steps but the flow must <em>not</em>
 * reach the skill card step.
 */
public class NewCheckNoSkillCardTest extends NewCheckBaseTest {

    private static final String NO_SKILL_CARD_PREFIX = "jcb";

    @Test(groups = {"mobile", "newcheck", "journey", "smoke"},
            description = "No-skill-card contractor (skillCardJourney=false) completes the first "
                    + "three steps but the flow must not reach the skill card step")
    public void testNoSkillCardJourneySkipsSkillCard() {
        Contractor contractor = contractorByPrefix(NO_SKILL_CARD_PREFIX);

        completePrefixSiteAndContact(contractor);

        SkillCardScreen skillCard = new SkillCardScreen(driver);
        Assert.assertFalse(skillCard.isLoaded(),
                "Contractor '" + contractor.contractorName() + "' có skillCardJourney=false → "
                        + "sau Contact point app KHÔNG được hiển thị bước 'Skill card verification'");
    }
}
