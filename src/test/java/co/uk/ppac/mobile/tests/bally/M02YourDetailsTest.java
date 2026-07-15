package co.uk.ppac.mobile.tests.bally;

import co.uk.ppac.mobile.screens.bally.M02YourDetailsScreen;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * M02 — Your Details (BALLYCOMMON-RAIL). Nguồn TC:
 * {@code knowledge-base/app/Testcase/ballycommon_rail_onboarding}.
 *
 * <p>Pre-Condition: login → New Check → chọn BALLYCOMMON-RAIL → Continue → Your Details.
 *
 * <p>Lưu ý: lỗi validation của app Flutter KHÔNG nằm trong accessibility tree
 * (không có Semantics) nên không đọc được text lỗi — TC validation được kiểm
 * bằng "không điều hướng" (vẫn ở Your Details), giống cách xử lý ở LoginScreen.
 */
public class M02YourDetailsTest extends BallyOnboardingBaseTest {

    private M02YourDetailsScreen details;

    @BeforeMethod(alwaysRun = true)
    public void goToYourDetails() {
        details = openYourDetailsScreen();
    }

    @Test(groups = {"mobile", "bally", "m02"},
            description = "PPAC_DETAILS_TC_011 — đủ các nhóm field (Personal / Address / Next of Kin / Payroll)")
    public void tc011_formStructure() {
        // Personal
        Assert.assertTrue(details.scrollToLabel("First name"), "Thiếu field 'First name'");
        Assert.assertTrue(details.scrollToLabel("Email"), "Thiếu field 'Email'");
        Assert.assertTrue(details.scrollToLabel("Candidate's Mobile Phone"),
                "Thiếu field 'Candidate's Mobile Phone'");
        // Address
        Assert.assertTrue(details.scrollToLabel("Address Line 1"), "Thiếu nhóm Address");
        Assert.assertTrue(details.scrollToLabel("Postcode"), "Thiếu field 'Postcode'");
        // Next of Kin
        Assert.assertTrue(details.scrollToLabel("Relationship to Candidate"),
                "Thiếu nhóm Next of Kin (Relationship to Candidate)");
        Assert.assertTrue(details.scrollToLabel("Contact Phone Number"),
                "Thiếu field 'Contact Phone Number'");
        // Payroll
        Assert.assertTrue(details.scrollToLabel("Payroll Company"), "Thiếu field 'Payroll Company'");
    }

    @Test(groups = {"mobile", "bally", "m02"},
            description = "PPAC_DETAILS_TC_021 — Sentinel Number hiển thị vì Sector = Rail")
    public void tc021_sentinelNumberPresentForRail() {
        Assert.assertTrue(details.scrollToLabel("Sentinel Number"),
                "Field 'Sentinel Number' phải hiển thị ở luồng Rail");
    }

    @Test(groups = {"mobile", "bally", "m02"},
            description = "PPAC_DETAILS_TC_014 — bỏ trống toàn bộ → tap Next bị chặn (vẫn ở Your Details)")
    public void tc014_emptyFormBlocksNext() {
        details.tapNext();
        Assert.assertTrue(details.isLoaded(),
                "Form trống → Next phải bị chặn; app vẫn ở màn Your Details");
    }
}
