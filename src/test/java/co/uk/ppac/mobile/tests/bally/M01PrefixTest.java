package co.uk.ppac.mobile.tests.bally;

import co.uk.ppac.mobile.screens.NewCheckScreen;
import co.uk.ppac.mobile.screens.bally.M02YourDetailsScreen;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * M01 — Prefix / Welcome (BALLYCOMMON-RAIL). Nguồn TC:
 * {@code knowledge-base/app/Testcase/ballycommon_rail_onboarding}.
 *
 * <p>Pre-Condition mỗi test: login → tab New Check → màn "Enter company prefix".
 * Bỏ TC_003/004 (cần dựng sẵn onboarding dở dang — tách batch riêng).
 */
public class M01PrefixTest extends BallyOnboardingBaseTest {

    private NewCheckScreen prefix;

    @BeforeMethod(alwaysRun = true)
    public void goToPrefixScreen() {
        prefix = openPrefixScreen();
    }

    @Test(groups = {"mobile", "bally", "m01"},
            description = "PPAC_PREFIX_TC_002 — màn Welcome hiển thị đúng tiêu đề/heading/mô tả")
    public void tc002_welcomeContent() {
        Assert.assertTrue(prefix.isWelcomeTitleDisplayed(),
                "Tiêu đề 'Welcome to PPAC, ...' phải hiển thị");
        Assert.assertTrue(prefix.isLoaded(),
                "Heading 'Enter company prefix' phải hiển thị");
        Assert.assertTrue(prefix.isHelpTextDisplayed(),
                "Mô tả 'We use this to identify your contractor or site.' phải hiển thị");
    }

    @Test(groups = {"mobile", "bally", "m01"},
            description = "PPAC_PREFIX_TC_006 — prefix không hợp lệ → không hiện gợi ý nào")
    public void tc006_invalidPrefixNoSuggestion() {
        prefix.typePrefix("INVALIDXYZ");
        Assert.assertFalse(prefix.isBallycommonRailDisplayed(),
                "Prefix không hợp lệ không được hiện gợi ý BALLYCOMMON");
    }

    @Test(groups = {"mobile", "bally", "m01"},
            description = "PPAC_PREFIX_TC_007 — gõ 'BALLY' → dropdown hiện đủ Construction + Rail")
    public void tc007_autocompleteShowsBothCompanies() {
        prefix.typePrefix(NewCheckScreen.BALLYCOMMON_PREFIX);
        // Dropdown gợi ý load async — chờ Rail (đảm bảo list đã populate) rồi mới
        // kiểm Construction, tránh false-negative do item render trễ.
        Assert.assertTrue(prefix.isBallycommonRailDisplayed(),
                "Phải hiện gợi ý 'Ballycommon - bally - Rail'");
        Assert.assertTrue(prefix.isBallycommonConstructionDisplayed(),
                "Phải hiện gợi ý 'Ballycommon - bally - Construction'");
    }

    @Test(groups = {"mobile", "bally", "m01"},
            description = "PPAC_PREFIX_TC_008 — chọn Rail → input điền + helper text hiện")
    public void tc008_selectRailFillsInputAndShowsHint() {
        prefix.typePrefix(NewCheckScreen.BALLYCOMMON_PREFIX);
        prefix.selectBallycommonRail();
        Assert.assertTrue(prefix.getEnteredPrefix().contains("Rail"),
                "Ô prefix phải được điền giá trị 'Ballycommon - bally - Rail'");
        Assert.assertTrue(prefix.isHintTextDisplayed(),
                "Helper text 'If you're unsure, ask your on-site manager.' phải hiển thị");
    }

    @Test(groups = {"mobile", "bally", "m01", "critical"},
            description = "PPAC_PREFIX_TC_001 — chọn Rail → Continue → vào form Your Details")
    public void tc001_selectRailContinueToYourDetails() {
        prefix.typePrefix(NewCheckScreen.BALLYCOMMON_PREFIX);
        prefix.selectBallycommonRail();
        prefix.tapContinue();

        // Tài khoản có thể đang có onboarding dở dang → popup "Continue onboarding?".
        // Bắt đầu mới để vào thẳng Your Details (form đầu).
        if (prefix.isContinueOnboardingPopupDisplayed()) {
            prefix.startNewOnboarding();
        }

        M02YourDetailsScreen details = new M02YourDetailsScreen(driver);
        Assert.assertTrue(details.isLoaded(),
                "Sau Continue phải điều hướng tới form Your Details");
    }
}
