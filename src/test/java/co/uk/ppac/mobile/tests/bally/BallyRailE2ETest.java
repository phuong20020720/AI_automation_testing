package co.uk.ppac.mobile.tests.bally;

import co.uk.ppac.mobile.screens.bally.M03ReferencesScreen;
import co.uk.ppac.mobile.screens.bally.M04MedicalSelfCertScreen;
import co.uk.ppac.mobile.screens.bally.M05ContractScreen;
import co.uk.ppac.mobile.screens.bally.M06PpeScreen;
import co.uk.ppac.mobile.screens.bally.M07SafetyCriticalScreen;
import co.uk.ppac.mobile.screens.bally.M08LostStolenCardsScreen;
import co.uk.ppac.mobile.screens.bally.M09DeclarationScreen;
import co.uk.ppac.mobile.locators.bally.M03ReferencesLocators;
import co.uk.ppac.mobile.locators.bally.M06PpeLocators;
import co.uk.ppac.mobile.locators.bally.M07SafetyCriticalLocators;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC_130 — E2E happy path Rail + Workwell (PAYE): MỘT luồng liên tục, login 1 lần
 * (ở {@link BallyE2EBaseTest}) → điền + Next qua đủ 8 form (M02→M09) → Submit.
 *
 * <p>Dữ liệu điền lấy từ {@link BallyRailTestData}
 * ({@code testdata/bally-rail-e2e.properties}) — sửa giá trị không phải đụng code.
 * Ô đánh {@code skip=true} = app đã điền sẵn → bỏ qua (tránh nối chuỗi vào ô có sẵn).
 */
public class BallyRailE2ETest extends BallyE2EBaseTest {

    private final BallyRailTestData data = BallyRailTestData.load();

    @Test(groups = {"mobile", "bally", "e2e", "critical"},
            description = "PPAC_E2E_TC_130 — hoàn tất đủ 8 form Rail+Workwell → Submit thành công")
    public void tc130_railWorkwellHappyPath() {
        // ---------- M02 Your Details ----------
        fillM02();
        M03ReferencesScreen refs = details.tapNext();
        Assert.assertTrue(refs.isLoaded(), "Sau M02 Next phải tới References");

        // ---------- M03 References (2 referee) ----------
        for (int i = 1; i <= M03ReferencesLocators.REFEREE_COUNT; i++) {
            String p = "m03.referee" + i;
            refs.selectRefereeType(i, data.select(p + ".type"));
            refs.enterRefereeFirstName(i, data.value(p + ".firstName"));
            refs.enterRefereeSurname(i, data.value(p + ".surname"));
            refs.enterRefereeContactNumber(i, data.value(p + ".contactNumber"));
            refs.enterRefereeRelationship(i, data.value(p + ".relationship"));
        }
        M04MedicalSelfCertScreen medical = refs.tapNext();
        Assert.assertTrue(medical.isLoaded(), "Sau M03 Next phải tới Medical");

        // ---------- M04 Medical: 12 câu = No + tick confirmation ----------
        for (int q = 1; q <= M04MedicalSelfCertScreen.QUESTION_COUNT; q++) {
            medical.answerQuestion(q, false);
        }
        medical.tickConfirmNoCheckbox();
        M05ContractScreen contract = medical.tapNext();
        Assert.assertTrue(contract.isLoaded(), "Sau M04 Next phải tới Contract");

        // ---------- M05 Contract: tick acceptance ----------
        contract.acceptContract();
        M06PpeScreen ppe = contract.tapNext();
        Assert.assertTrue(ppe.isLoaded(), "Sau M05 Next phải tới PPE");

        // ---------- M06 PPE: 9 item = No ----------
        for (String item : M06PpeLocators.ITEMS) {
            ppe.answerItem(item, false);
        }
        M07SafetyCriticalScreen safety = ppe.tapNext();
        Assert.assertTrue(safety.isLoaded(), "Sau M06 Next phải tới Safety Critical");

        // ---------- M07 Safety Critical: gate = No + 16 cert = No ----------
        safety.answerGate(false);
        for (String cert : M07SafetyCriticalLocators.CERTS) {
            safety.answerCert(cert, false);
        }
        M08LostStolenCardsScreen lost = safety.tapNext();
        Assert.assertTrue(lost.isLoaded(), "Sau M07 Next phải tới Lost & Stolen");

        // ---------- M08 Lost & Stolen: tick £25+VAT ----------
        lost.confirm();
        M09DeclarationScreen decl = lost.tapNext();
        Assert.assertTrue(decl.isLoaded(), "Sau M08 Next phải tới Declaration");

        // ---------- M09 Declaration: tick đủ 11 mục → Submit ----------
        for (int d = 1; d <= M09DeclarationScreen.DECLARATION_COUNT; d++) {
            decl.tickDeclaration(d);
        }
        Assert.assertTrue(decl.isSubmitEnabled(),
                "Tick đủ 11 declaration → nút Submit phải enabled (CR-01)");
        decl.tapSubmit();
        // TODO(heal): assert màn xác nhận hoàn tất onboarding (locator chờ inspect).
    }

    /**
     * Điền M02 theo dữ liệu: text → fillByLabel, dropdown (FIRST) → selectFirstByLabel,
     * dropdown có giá trị → selectByLabel, Payroll → selectPayrollCompany (xử lý
     * occlusion). Ô {@code skip=true} bỏ qua.
     */
    private void fillM02() {
        // Dropdown bắt buộc còn trống.
        fillSelect("m02.consultant", "Consultant");
        fillSelect("m02.trade", "Trade");
        fillSelect("m02.qualification", "Qualification");
        // Text fields (skip nếu app đã điền sẵn).
        fillText("m02.candidateMobilePhone", "Candidate's Mobile Phone");
        fillText("m02.addressLine1", "Address Line 1");
        fillText("m02.addressLine2", "Address Line 2");
        fillText("m02.addressLine3", "Address Line 3");
        fillText("m02.townCity", "Town / City");
        fillSelect("m02.country", "Country");
        fillText("m02.postcode", "Postcode");
        fillText("m02.niNumber", "National Insurance Number");
        fillText("m02.sentinelNumber", "Sentinel Number");
        fillSelect("m02.hearAboutUs", "Where did you hear about us?");
        // Next of Kin (trống & bắt buộc).
        fillText("m02.nokRelationship", "Relationship to Candidate");
        // Contact Phone sát đáy (ngay trên Payroll) → scroll tới "Payroll Company"
        // để kéo ô input lên vùng giữa màn rồi mới nhập.
        if (!data.skip("m02.nokContactPhone") && data.value("m02.nokContactPhone") != null) {
            details.fillByLabelScrollTo("Payroll Company", "Contact Phone Number",
                    data.value("m02.nokContactPhone"));
        }
        // Payroll — field cuối; ô dropdown tách container bởi helper → method riêng.
        if (!data.skip("m02.payrollCompany")) {
            details.selectPayrollCompany(data.select("m02.payrollCompany"));
        }
    }

    /** DIAGNOSTIC tạm: dump page source hiện tại ra target/&lt;name&gt;.xml. */
    private void dumpSource(String name) {
        try {
            java.nio.file.Files.writeString(
                    java.nio.file.Path.of("target", name + ".xml"), driver.getPageSource());
        } catch (Exception e) {
            System.out.println("dumpSource failed: " + e.getMessage());
        }
    }

    /** Fill ô text theo data; bỏ qua nếu skip=true hoặc giá trị trống. */
    private void fillText(String field, String label) {
        if (data.skip(field)) {
            return;
        }
        String value = data.value(field);
        if (value != null) {
            details.fillByLabel(label, value);
        }
    }

    /** Chọn dropdown theo data; (FIRST) → option đầu, ngược lại chọn theo giá trị. */
    private void fillSelect(String field, String label) {
        if (data.skip(field)) {
            return;
        }
        String sel = data.select(field);
        if (sel == null) {
            return;
        }
        if (data.isFirst(sel)) {
            details.selectFirstByLabel(label);
        } else {
            details.selectByLabel(label, sel);
        }
    }
}
