# Automation Generation Progress — Ballycommon RAIL Onboarding

Nguồn TC: `knowledge-base/app/Testcase/ballycommon_rail_onboarding/test_cases_ballycommon_rail_onboarding.md` (118 TC).
Tech stack: **Appium + Java + TestNG + POM** (theo project).

- [x] Bước 1: Phân tích test cases
- [x] Bước 2: Khảo sát UI (đã inspect live M01–M09 ở các phiên trước → có locator THẬT)
- [x] Bước 3: Thiết kế POM (đã có `NewCheckScreen` + `screens/bally/M02..M09Screen` + locators)
- [ ] Bước 4: Chuẩn bị test data
- [ ] Bước 5: Sinh automation scripts  ← **ĐANG CHỜ CHỐT SCOPE (xem mismatch)**
- [ ] Bước 6: Chạy test + Auto-heal

## ✅ ĐÃ RE-VERIFY (2026-06-16) — App KHỚP TC doc; phân tích "mismatch" ban đầu của tôi SAI

> **Nguyên nhân:** các lần inspect nhanh trước CUỘN KHÔNG HẾT → sót nội dung dưới màn. Re-verify kỹ cho thấy app khớp TC doc.

| Vùng | Kết luận SAI (lần đầu) | THỰC TẾ sau re-verify |
|---|---|---|
| M02 fields | "thiếu Address/Email/Qualification/Candidate Mobile" | ✅ CÓ ĐỦ: Qualification, Email, Candidate's Mobile Phone, Address Line 1/2/3, Town/City, Country, Postcode (sót do scroll) |
| M03 referees | "chỉ 1 Referee" | ✅ CÓ Referee 2 (sót do scroll) |
| M04 câu hỏi | "10 câu, no checkbox" | ✅ ĐỦ 12 câu + confirmation checkbox "I confirm that I have selected 'NO' to all of the medical self-certification declarations above." (sót Q11/Q12 + checkbox do scroll) |

**Khác biệt THẬT còn lại (nhỏ):**
- M01 copy: heading "Enter company prefix" (TC ghi "Enter Company Name"); dropdown "Ballycommon - bally - Rail" (TC ghi "BALLYCOMMON - RAIL"); helper "ask your on-site manager" (TC "ask your manager on-site"). → cần đối chiếu Figma xem app hay TC đúng.
- Dropdown M02/M03 (Consultant/Trade/Type/Payroll…) là bottom-sheet "Select" dialog, **option load từ backend** — RỖNG ("No found") trên sandbox/offline hiện tại → chưa chọn được (vấn đề DATA, không phải field thiếu).

## ⚠️ HỆ QUẢ: POM M02/M03/M04 đã sinh bị THIẾU — phải RE-INSPECT + REGENERATE

Vì inspect trước sót, các file đã sinh ở `src/.../bally/` CHƯA đủ:
- **M02YourDetailsLocators/Screen**: THIẾU Qualification, Email, Candidate's Mobile Phone, Address Line 1/2/3, Town/City, Country, Postcode.
- **M03ReferencesLocators/Screen**: THIẾU toàn bộ Referee 2 + field điều kiện Employer (Contractor/Company Name, Project or site).
- **M04MedicalSelfCertLocators/Screen**: mảng QUESTIONS chỉ 10 (đúng 12); THIẾU confirmation checkbox.

→ **Cần re-inspect đầy đủ M02/M03/M04 → cập nhật locators/screens → mới sinh test.**
M05–M09 + M01 (trừ copy) đã đúng.

### ✅ ĐÃ SỬA POM (2026-06-16) — compile OK
- **M04**: QUESTIONS → 12 câu; thêm `CONFIRM_NO_CHECKBOX` + `tickConfirmNoCheckbox()`/`isConfirmNoCheckboxDisplayed()`.
- **M03**: thêm Referee 2; refactor sang per-referee `typeDropdown(idx)`/`firstNameInput(idx)`/… (label lặp → neo occurrence thứ idx); thêm field điều kiện Employer `contractorCompanyInput(idx)`/`projectSiteInput(idx)` (CHƯA verify live).
- **M02**: thêm 9 field — Qualification, Email, Candidate's Mobile Phone, Address Line 1/2/3, Town/City, Country, Postcode + method tương ứng.
- `mvn compile` toàn project: **OK**.
- Còn pending: option THẬT của dropdown (backend rỗng sandbox); field điều kiện Employer M03 (Type backend); copy M01 vs Figma.

### ✅ DATA ĐÃ CÓ — verify thêm (2026-06-16, đợt 2)
- Dropdown **đã load option** (backend có data). Type referee = **"Personal referee" / "Employer referee"** (đúng TC_043) → đã thêm hằng `TYPE_PERSONAL_REFEREE`/`TYPE_EMPLOYER_REFEREE`.
- **Field điều kiện Employer M03 ĐÃ verify live**: chọn "Employer referee" → hiện "Contractor or Company Name" + "Project or site you worked on" (đúng TC_052). Đã bỏ nhãn "CHƯA verify".
- Cơ chế chọn dropdown (Select dialog: search → option → list) hoạt động với data ⇒ **automation được giải chặn** cho TC phụ thuộc dropdown.
- **Copy M01**: app "Enter company prefix" là CHUẨN → đã sửa TC doc (heading, mô tả "We use this to identify your contractor or site.", helper "ask your on-site manager.").

**TRẠNG THÁI:** POM M01–M09 đã khớp app & build OK. Sẵn sàng Bước 4–6 (test data + sinh test + chạy).

### Bước 5–6 — M01 (đang xử lý, 2026-06-16)
- Đã sinh `src/test/.../tests/bally/M01PrefixTest.java` (6 TC: 002/006/007/008/001) + base `BallyOnboardingBaseTest` (login retry kiên nhẫn). **test-compile OK.**
- ⚠️ **Chạy M01 FAIL ở `@BeforeMethod` login** (`home.isWelcomeDisplayed()` = false sau cả retry 60s) — GIỐNG flaky của `LoginMobileTest` sẵn có. Root cause (chẩn đoán): sau tap Login app qua splash rồi mới tới Home, và/hoặc tài khoản đang có onboarding dở dang → app không hiện Home/Wallet trong thời gian chờ.
- **Heal:** fixture chờ Wallet tab bằng WebDriverWait dài (qua splash) thay retry 15s. Kết quả: login→Home OK, **các TC chạy được**. Cả 5 TC M01 PASS khi login thành công (chứng minh: 1 lần chạy 4/5 pass + `tc007` đảo thứ tự assert (chờ Rail trước, async) → PASS riêng BUILD SUCCESS).
- ⚠️ **Còn flaky MÔI TRƯỜNG:** emulator chậm, MobileBaseTest tạo driver mới + re-login MỖI test (~90–120s/login) → đôi khi 1 `@BeforeMethod` quá timeout (đã thấy 117s). Đã nới timeout 90s→120s. Đây là giới hạn môi trường (không phải lỗi code), giảm bằng: emulator nhanh hơn / clear onboarding dở dang / thêm TestNG retryAnalyzer cho infra flaky.
- **TODO M01:** TC_003/004 (popup "Continue onboarding?") cần dựng state onboarding dở dang — batch riêng.

### Bước 5–6 — M02 ✅ (2026-06-16) — 3/3 PASS (BUILD SUCCESS)
- `M02YourDetailsTest`: TC_011 (cấu trúc form đủ nhóm Personal/Address/Next of Kin/Payroll), TC_021 (Sentinel Number hiện vì Rail), TC_014 (form trống → Next bị chặn, vẫn ở Your Details). **Tests run: 3, Failures: 0.**
- Heal tái sử dụng cho M03–M09:
  - `BallyOnboardingBaseTest.openYourDetailsScreen()` — login → New Check → BALLY → Rail → Continue → (Start a new onboarding nếu popup) → chờ "Your Details" 120s.
  - `MobileGestures.scrollToContentDesc()` — scroll theo content-desc, target `android.widget.ScrollView` (tránh bắt nhầm HorizontalScrollView của stepper). `M02YourDetailsScreen.scrollToLabel()`.
  - Validation error Flutter KHÔNG ở a11y tree → TC validation kiểm bằng "không điều hướng" (vẫn ở màn).
- ⚠️ Vẫn flaky login môi trường (mỗi test re-login ~75–120s); đã ổn với timeout 120s.

### 🔄 ĐỔI HƯỚNG (theo yêu cầu user): luồng E2E LIÊN TỤC (TC_130)
User làm rõ: KHÔNG test rời từng màn + re-login. Phải là **1 luồng**: login 1 lần → nhập "bally" → chọn Rail → **điền + Next liên tục M02→M09** (điền → next → điền → next…). Lợi: chỉ 1 login (hết flaky re-login).
- Đã thêm helper điền vào `M02YourDetailsScreen`: `fillByLabel()`, `selectByLabel()` (scroll tới field rồi nhập/chọn dropdown), `scrollToLabel()`.
- **CẦN cho E2E (chưa có):**
  1. Giá trị option THẬT của dropdown M02 (Consultant/Trade/Qualification/Country/Where/Payroll=Workwell) + M03 Type — backend data (giờ đã có data, capture live khi build).
  2. Cơ chế **DOB picker** (wheel custom, TC_024/025) — chưa inspect.
  3. Test `BallyRailE2ETest`: @BeforeMethod login+reach M02; @Test fill M02→Next→M03→…→M09; chốt M09 (tick 11 declaration) — **CẦN QUYẾT: có tap Submit thật không** (TC_130 = submit success, tạo onboarding thật).
- M04–M09 fill đã có sẵn screen methods (answerQuestion all No + tickConfirmNoCheckbox; acceptContract; answerItem; answerGate No + answerCert No; confirm; tickDeclaration ×11).
- Test rời M01/M02 hiện có vẫn giữ (validate màn), nhưng E2E là mục tiêu chính tiếp theo.

### Bước 6 — E2E `BallyRailE2ETest` (đang run→heal, 2026-06-16)
Quyết định: **CÓ Submit thật** ở M09 (TC_130 đầy đủ).
- Đã viết `BallyRailE2ETest` (login 1 lần → fill M02→M09 → Submit). Compile OK.
- **Tiến triển qua run→heal:**
  - ✅ Login 1 lần + tới M02 chạy được (khi không gặp flaky).
  - ✅ `fillByLabel` điền được ô TEXT M02 (First name/Surname… — field là View vẫn sendKeys được).
  - 🔧 Dropdown: option backend là tên đầy đủ → thêm `SelectDialogLocators.optionContaining()` + `selectByLabel(label, query)` search rồi tap option CHỨA query (chưa kịp verify do flaky/login).
- **CÒN PHẢI LÀM:**
  1. ⚠️ **Login/nav FLAKY (~50%)** — blocker chính; mỗi run là 1 canh bạc. Cần: emulator nhanh/ổn hơn HOẶC login 1 lần dùng lại cho cả suite (đổi MobileBaseTest tạo driver per-class thay per-method) HOẶC retry.
  2. **DOB picker** (wheel) — chưa xử lý; M02 Next có thể chặn nếu DOB required.
  3. Verify live: option dropdown thật (Consultant chứa "Guy"? Trade "Abrasive Wheel"? Payroll "Workwell"); màn xác nhận sau Submit (assert cuối).
  4. M03 fill referee 2 cần scroll (M03 screen chưa có scrollToLabel) + chọn Type qua dialog.

### ✅ (a) PER-CLASS LOGIN — XONG & HIỆU QUẢ (2026-06-16)
- `BallyE2EBaseTest`: `@BeforeClass` createDriver + login + tới M02 **1 LẦN/class** (không re-login mỗi test); `@AfterClass` quit; `@AfterMethod` chụp screenshot khi fail. `BallyRailE2ETest` extends nó.
- Đã xác nhận login 1 lần chạy được nhiều run liên tiếp.
- (b) DOB & nhiều field **đã điền sẵn** (seed) — vd Email pre-fill "ppacd3@yopmail.com". KHÔNG re-fill text.

### 🔧 E2E — blocker per-field còn lại (cần phiên inspect tập trung)
Qua run→heal + screenshot lỗi:
- ✅ Consultant: `selectFirstByLabel()` (mở → tap option đầu) chạy.
- ❌ **Payroll dropdown KHÔNG mở** khi gọi `selectByLabel("Payroll Company",…)` → `SEARCH_INPUT="//android.widget.EditText"` khớp NHẦM EditText khác trên form, "Workwell" bị gõ vào sai ô. FIX: (1) `fieldAfterLabel("Payroll Company")` chưa trỏ đúng phần tử clickable mở dialog — verify locator Payroll; (2) scope SEARCH_INPUT trong dialog (`pane-title="Dialog"`), không lấy EditText toàn màn.
- ⚠️ **Text-field Flutter (View) sendKeys KHÔNG ổn định** (Candidate's Mobile Phone trống sau "fill") — nhưng form pre-fill nên có thể chỉ cần xử lý dropdown.
- ⚠️ **Address Line 1 = autocomplete** ("Start typing address") — cần gõ + chọn gợi ý nếu phải điền.
- **Hướng tiếp:** inspect M02 live (đã login sẵn) để: xác định field nào required-trống vs pre-fill; sửa locator mở dropdown Payroll + search-input dialog-scoped; rồi chain M03→M09 (đã có screen methods).

## Test Cases Inventory
| Module | TC | Số lượng | Trạng thái khớp app |
|---|---|---|---|
| M01 Prefix | TC_001–008 | 8 | Lệch (label/dropdown) |
| M02 Your Details | TC_010–038 | ~28 | Lệch nặng (thiếu field + dropdown rỗng) |
| M03 References | TC_040–054 | 15 | Lệch nặng (1 referee) |
| M04 Medical | TC_060–072 | 13 | Lệch (10 vs 12 câu, no checkbox) |
| M05 Contract | TC_080–084 | 5 | ✅ Khớp |
| M06 PPE | TC_090–097 | 8 | ✅ Khớp |
| M07 Safety Critical | TC_100–109 | 10 | ✅ Khớp (1 field chờ verify) |
| M08 Lost & Stolen | TC_110–113 | 4 | ✅ Khớp |
| M09 Declaration | TC_120–127, 150–157 | 16 | ✅ Khớp |
| M10 E2E | TC_130–131 | 2 | Chặn (phụ thuộc M01–M04 + dropdown) |

### ✅ FIX & PHÁT HIỆN (2026-06-16, đợt inspect Payroll qua MCP)
- ✅ **@BeforeClass nav ổn hơn**: chờ màn prefix bằng wait dài 120s (`NewCheckLocators.SCREEN_HEADING`) thay `isLoaded()` 15s → login 1 lần + nav tới M02 chạy được.
- ✅ **Payroll dropdown FIX**: helper text "Please select your preferred payroll provider" CHÈN GIỮA label và ô dropdown → `fieldAfterLabel[1]` trỏ nhầm helper (clk=false). Thêm `dropdownAfterLabel()` = following-sibling CLICKABLE đầu tiên (verify MCP mở đúng dialog). `selectByLabel`/`selectFirstByLabel` dùng nó + chọn option trực tiếp (bỏ search-input fragile).
- ✅ **Option PAYE thật** = "**Workweek** Contractor Solutions (Provider for PAYE)" (app typo "Workweek", không phải "Workwell"); còn "CWG (Provider for CIS)". E2E chọn contains "PAYE".
- ✅ Consultant: `selectFirstByLabel` (option đầu) chạy.
- ⚠️ "First name"/"Surname" content-desc TRÙNG (Personal + Next of Kin đều có) → không unique.
- ❌ **Blocker M02 Next còn lại:**
  1. **Payroll dropdown bị nút Next che** khi scroll tới label → cần scroll quá label (vd tới helper "Please select your preferred payroll provider") để ô dropdown lên trên Next.
  2. **Next of Kin (First name/Surname/Relationship/Contact Phone) TRỐNG & required**, là text-field Flutter (View) → **sendKeys không ổn định**. Cần cách nhập tin cậy (mobile:type / actions) HOẶC pre-seed onboarding đầy đủ.
