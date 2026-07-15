# PPAC Mobile — Keltbray Worker Onboarding (Test Cases)

> **Hệ thống:** PPAC Mobile — Worker Onboarding
> **Module:** Keltbray (`keltbray`) · **2 sector:** KRS (induction) + Keltbray (normal)
> **Nguồn requirements:** [[requirements_keltbray_onboarding]]
> **Phương pháp:** FULL RBT (AI-RBT 6 bước) — EP · BVA · Decision Table · State Transition · UI-state checks
> **Ngày tạo:** 2026-06-22 · **QA Owner:** harry.vo@ppac.co.uk
> **Tổng số TC:** 140 (16 module / 6 Part) — Critical ~12 · High ~74 · Medium ~48 · Low ~6.
> **Kỳ vọng cuối journey (cả 2 sector):** popup **"Your check has been submitted!"** = submission thành công.

---

## Phạm vi

**Trong scope:** 2 sector (KRS induction: CIS/Limited/Umbrella + Keltbray normal), tới bước Skill card verification → submission thành công.
**Ngoài scope (không sinh TC — theo QA):** required-status Select site location/subcontractor (AMB-N01), hành vi checkbox "I can't find my subcontractor" (AMB-N02), "Not Applicable" có submit được không (Q8). Pre-filled (Surname/Forenames/Email/DOB/Citizenship) chỉ verify hiển thị, không nhập lại. Chi tiết bước sau khi chọn Skill card type (vòng sau).

## Quy ước Test Data

- Traceable: `auto_kelt_<module>_<TCnum>_20260622`.
- Email pre-filled (ngoài scope nhập); khi cần: `auto_kelt_<TCnum>_20260622@yopmail.com`.
- **NIN** hợp lệ `AH123456L` (2 chữ + 6 số + 1 chữ); sai format `MIDORI`, thiếu số `AH12345L`.
- **Phone** hợp lệ `07700900111` (11 số); sai `8272930` (<11 số).
- **UTR** hợp lệ `1234567890` / `1234567890K`; sai `12345`.
- **Bank account** hợp lệ `12345678` (8 số); sai `1234567` (7 số).
- **Company Registration Number** hợp lệ `12345678` / `SC12345678`; sai `CHCJMX`.
- Mỗi TC độc lập; reset onboarding ở Pre-Condition (lưu ý BR-39: back/thoát → reset).

---

## Test Cases — Part 1

### PREFIX — Prefix / Select Sector (M01)

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_PREFIX_TC_001 | PREFIX | Medium | Nhập prefix keltbray → hiển thị 2 sector | Worker mở onboarding link, màn nhập company prefix | 1. Gõ "keltbray" vào ô company prefix<br>2. Quan sát danh sách gợi ý | prefix = keltbray | 1. Hiển thị gợi ý gồm **"Keltbray - KRS"** và **"Keltbray - Keltbray"** | Medium |
| PPAC_KELT_PREFIX_TC_002 | PREFIX | High | Chọn "Keltbray - KRS" → vào luồng induction | Đã gõ "keltbray", có gợi ý | 1. Chọn **"Keltbray - KRS"** | chọn KRS | 1. Điều hướng tới màn **Worker Payment Status** (luồng induction) | High |
| PPAC_KELT_PREFIX_TC_003 | PREFIX | High | Chọn "Keltbray - Keltbray" → vào luồng normal | Đã gõ "keltbray", có gợi ý | 1. Chọn **"Keltbray - Keltbray"**<br>2. Tap "Continue →" | chọn Keltbray | 1. Điều hướng tới màn **Select site location** (luồng normal, N02) | High |


### WPS — Worker Payment Status (M02)

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_WPS_TC_001 | WPS | High | Không chọn Worker Payment Status → lỗi required | Đã vào KRS, màn Worker Payment Status, dropdown trống | 1. Không chọn gì ở dropdown<br>2. Thử tiếp tục | (none) | 1. Field viền đỏ + lỗi đỏ **"Please select here"**; không tiếp tục được | High |
| PPAC_KELT_WPS_TC_002 | WPS | Medium | Sheet hiển thị đúng 3 option (single-select) | Màn Worker Payment Status | 1. Mở dropdown<br>2. Quan sát sheet "Worker Payment Status" | N/A | 1. Sheet có đúng 3 option: **CIS · Limited · Umbrella**; chọn 1 → đánh dấu CheckCircle xanh (single-select) | Medium |
| PPAC_KELT_WPS_TC_003 | WPS | High | Chọn CIS → hiện radio provider (Riddingtons/Industrial Labour) | Màn Worker Payment Status | 1. Chọn **CIS**<br>2. Quan sát dưới dropdown | CIS | 1. Dropdown thu gọn "CIS"; hiện **radio group provider**: Riddingtons + Industrial Labour; nút "Next →" | High |
| PPAC_KELT_WPS_TC_004 | WPS | High | CIS + chưa chọn provider → chặn Next | Đã chọn CIS, chưa chọn provider | 1. Không chọn provider<br>2. Tap "Next →" | CIS, provider = (none) | 1. Không điều hướng (BR-03: provider bắt buộc khi CIS) | High |
| PPAC_KELT_WPS_TC_005 | WPS | High | CIS + Riddingtons → Next → Welcome Riddingtons | Đã chọn CIS | 1. Chọn provider **Riddingtons**<br>2. Tap "Next →" | CIS + Riddingtons | 1. Điều hướng tới Welcome "Welcome to Riddingtons payroll services." | High |
| PPAC_KELT_WPS_TC_006 | WPS | High | CIS + Industrial Labour → Next → Welcome Industrial Labour | Đã chọn CIS | 1. Chọn provider **Industrial Labour**<br>2. Tap "Next →" | CIS + Industrial Labour | 1. Điều hướng tới Welcome "Welcome to Industrial Labour LTD payroll services." | High |
| PPAC_KELT_WPS_TC_007 | WPS | High | Chọn Limited → hiện text provider + section "Kindly provide one of the following" | Màn Worker Payment Status | 1. Chọn **Limited**<br>2. Quan sát dưới dropdown | Limited | 1. Hiện dòng text provider (cố định) + section "Kindly provide one of the following:" với 2 nút **Company Registration Number →** / **Company Trading Name →**<br>hiển thị "Industrial Labour"  | High |
| PPAC_KELT_WPS_TC_008 | WPS | High | Chọn Umbrella → radio provider Riddingtons (1 option) → Next → Welcome | Màn Worker Payment Status | 1. Chọn **Umbrella**<br>2. Chọn radio **Riddingtons**<br>3. Tap "Next →" | Umbrella + Riddingtons | 1. Hiện radio "Riddingtons" (1 option, selected); Next → Welcome "Welcome to Riddingtons payroll services." | High |
| PPAC_KELT_WPS_TC_009 | WPS | Medium | Đổi lựa chọn CIS → Limited → ẩn radio provider, hiện section company (State Transition) | Đã chọn CIS (radio provider hiển thị) | 1. Đổi dropdown sang **Limited**<br>2. Quan sát | CIS → Limited | 1. Radio provider (Riddingtons/Industrial Labour) ẩn; hiện section "Kindly provide one of the following" của Limited | Medium |
| PPAC_KELT_WPS_TC_010 | WPS | Low | Header hiển thị đúng | Màn Worker Payment Status | 1. Quan sát header | N/A | 1. Có logo **keltbray**, nút Back (`<`); tiêu đề xanh "Worker Payment Status" | Low |

### WELCOME — Màn Welcome theo provider (M03 / M03·UMB)

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_WELCOME_TC_001 | WELCOME | Low | Welcome Riddingtons (CIS/Umbrella) hiển thị đúng logo + heading | Đã chọn provider Riddingtons → màn Welcome | 1. Quan sát logo + heading | N/A | 1. Logo "Riddingtons Payroll"; heading xanh **"Welcome to Riddingtons payroll services."** | Low |
| PPAC_KELT_WELCOME_TC_002 | WELCOME | Low | Welcome Industrial Labour (CIS) hiển thị đúng logo + heading | Đã chọn CIS + Industrial Labour → màn Welcome | 1. Quan sát logo + heading | N/A | 1. Logo "IL — INDUSTRIAL LABOUR LTD"; heading **"Welcome to Industrial Labour LTD payroll services."** | Low |
| PPAC_KELT_WELCOME_TC_003 | WELCOME | Medium | Body Welcome verbatim (mọi provider) | Màn Welcome (provider bất kỳ) | 1. Đọc body | N/A | 1. Body đúng: "Please complete your full personal details in line with our client's requirements. You will be contacted by us directly to complete the contract services." + "Thank you." | Medium |
| PPAC_KELT_WELCOME_TC_004 | WELCOME | Medium | Next → Personal information | Màn Welcome | 1. Tap "Next →" | N/A | 1. Điều hướng tới form Personal information (tab Onboarding details) | Medium |
| PPAC_KELT_WELCOME_TC_005 | WELCOME | Low | Welcome Umbrella = Riddingtons | Đã chọn Umbrella + Riddingtons → Welcome | 1. Quan sát heading | N/A | 1. Heading "Welcome to Riddingtons payroll services." (Umbrella dùng Riddingtons) | Low |

### DEF — Regression / Defects (DN-04)

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_DEF_TC_001 | DEF | High | 🐞 DN-04 — text provider ở WPS Limited phải là Industrial Labour | Màn Worker Payment Status, đã chọn **Limited** | 1. Quan sát dòng text provider dưới dropdown Limited | N/A | Hiển thị provider = **"Industrial Labour"** (hiện đang hiển thị SAI "Payroll Provider is Riddingtons" → bug DN-04) | High |
| PPAC_KELT_DEF_TC_002 | DEF | Medium | Limited: Welcome = Industrial Labour LTD (provider thực) | Đã chọn Limited → qua company info → màn Welcome | 1. Quan sát heading Welcome | N/A | 1. Heading "Welcome to Industrial Labour LTD payroll services." → xác nhận provider thực của Limited = Industrial Labour (đối chiếu DEF_TC_001) | Medium |

---

> **Part 1 = 20 TC** (PREFIX 3 · WPS 10 · WELCOME 5 · DEF 2).

---

## Test Cases — Part 2

### PICIS — Personal information, nhánh CIS (M04)

> Pre-Condition chung: đã vào onboarding **CIS** (qua provider → Welcome), màn **Personal information** (tab Onboarding details); **Surname/Forenames/Email/DOB/Citizenship** pre-filled từ profile. Stepper 3 tab: Onboarding details (active) · Health Questionnaire · Declarations.

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_PICIS_TC_001 | PICIS | Critical | Nhập đầy đủ field hợp lệ → Next sang Health Questionnaire | Màn Personal information (CIS); các pre-filled có sẵn | 1. Upload passport<br>2. Nhập Address, City, Postcode<br>3. Nhập Candidate's Mobile Phone, NIN<br>4. Chọn Contract/Job start date, nhập Next of Kin + Tel, chọn Trade<br>5. Nhập UTR, Name of bank, Bank account, Sort code<br>6. Tap "Next →" | Phone=07700900111; NIN=AH123456L; Contract=01/01/2026; Next of Kin=John Smith; Tel=07700900222; UTR=1234567890; Bank acc=12345678; Sort=12-34-56; Roll=(trống) | 1. Mọi field nhận giá trị, không lỗi<br>2. Điều hướng tới tab **Health Questionnaire** | Critical |
| PPAC_KELT_PICIS_TC_002 | PICIS | High | Your passport bỏ trống → lỗi required, chặn Next | Màn Personal information, chưa upload passport | 1. Để trống "Your passport"<br>2. Điền field khác hợp lệ<br>3. Tap "Next →" | passport=(none) | 1. Lỗi **"Please upload your passport"**; không điều hướng | High |
| PPAC_KELT_PICIS_TC_003 | PICIS | High | Tap "Upload your passport" → điều hướng màn Regula | Màn Personal information | 1. Tap "+ Upload your passport" | N/A | 1. Điều hướng tới màn **Regula** (quét/verify hộ chiếu) — KHÁC component "Pick your source of document"; helper "Accepted file types: png, jpg, jpeg, doc, docx, pdf." | High |
| PPAC_KELT_PICIS_TC_004 | PICIS | Medium | Pre-filled hiển thị sẵn từ profile | Màn Personal information | 1. Quan sát Surname, Forenames, Email, Date of Birth, Citizenship | N/A | 1. 5 field hiển thị giá trị pre-filled (vd Surname/Forenames "Maya", DOB 28/05/2001, Citizenship "Vietnamese") — không cần nhập lại | Medium |
| PPAC_KELT_PICIS_TC_005 | PICIS | High | Field pre-filled KHÔNG cho chỉnh sửa/xóa (read-only) | Màn Personal information; Surname/Forenames/Email/DOB/Citizenship pre-filled từ profile | 1. Thử chỉnh sửa và xóa lần lượt từng field: Surname, Forenames, Email, Date of Birth, Citizenship | N/A | 1. **Không cho chỉnh sửa/xóa** — cả 5 field pre-filled ở trạng thái read-only; giá trị giữ nguyên từ profile | High |
| PPAC_KELT_PICIS_TC_006 | PICIS | High | Address bỏ trống → lỗi required | Màn Personal information | 1. Để trống Address<br>2. Tap "Next →" | Address=(empty) | 1. Hiển thị lỗi required tại Address; không điều hướng | High |
| PPAC_KELT_PICIS_TC_007 | PICIS | Medium | City / Postcode bỏ trống → lỗi required | Màn Personal information | 1. Để trống City và Postcode<br>2. Tap "Next →" | City=(empty); Postcode=(empty) | 1. Mỗi field (City, Postcode) hiển thị lỗi required; không điều hướng | Medium |
| PPAC_KELT_PICIS_TC_008 | PICIS | High | Candidate's Mobile Phone < 11 số → lỗi (BVA) | Màn Personal information | 1. Nhập Candidate's Mobile Phone < 11 số<br>2. Tap "Next →" | Phone=8272930 (7 số) | 1. Hiển thị **"either your phone number is invalid or use at least 11 digits"**; không điều hướng | High |
| PPAC_KELT_PICIS_TC_009 | PICIS | High | Candidate's Mobile Phone bỏ trống → lỗi required | Màn Personal information | 1. Để trống Candidate's Mobile Phone<br>2. Tap "Next →" | Phone=(empty) | 1. Hiển thị lỗi required tại Candidate's Mobile Phone; không điều hướng | High |
| PPAC_KELT_PICIS_TC_010 | PICIS | Medium | Candidate's Mobile Phone = 11 số → chấp nhận (BVA biên) | Màn Personal information | 1. Nhập Phone đúng 11 số<br>2. Rời focus | Phone=07700900111 (11 số) | 1. Không lỗi phone; field nhận giá trị | Medium |
| PPAC_KELT_PICIS_TC_011 | PICIS | High | NIN sai format → lỗi format verbatim | Màn Personal information | 1. Nhập NIN sai định dạng<br>2. Tap "Next →" | NIN=MIDORI | 1. Hiển thị **"Please enter the right format: 2 letters, 6 numbers, 1 letter (e.g. AA999999A)"**; không điều hướng | High |
| PPAC_KELT_PICIS_TC_012 | PICIS | Medium | NIN thiếu 1 số (boundary) → lỗi format (BVA) | Màn Personal information | 1. Nhập NIN thiếu 1 số (2 chữ + 5 số + 1 chữ)<br>2. Tap "Next →" | NIN=AH12345L | 1. Hiển thị lỗi format NIN | Medium |
| PPAC_KELT_PICIS_TC_013 | PICIS | High | NIN đúng format (2 chữ + 6 số + 1 chữ) → chấp nhận | Màn Personal information | 1. Nhập NIN đúng định dạng<br>2. Rời focus | NIN=AH123456L | 1. Không lỗi NIN; field nhận giá trị | High |
| PPAC_KELT_PICIS_TC_014 | PICIS | Medium | NIN bỏ trống → lỗi required | Màn Personal information | 1. Để trống NIN<br>2. Tap "Next →" | NIN=(empty) | 1. Hiển thị lỗi required tại NIN; không điều hướng | Medium |
| PPAC_KELT_PICIS_TC_015 | PICIS | Medium | Contract/Job start date bỏ trống → lỗi "Please enter here" | Màn Personal information | 1. Để trống Contract/Job start date<br>2. Tap "Next →" | Contract=(empty) | 1. Hiển thị **"Please enter here"** tại Contract/Job start date; không điều hướng | Medium |
| PPAC_KELT_PICIS_TC_016 | PICIS | Medium | Next of Kin bỏ trống → lỗi required | Màn Personal information | 1. Để trống Next of Kin<br>2. Tap "Next →" | Next of Kin=(empty) | 1. Hiển thị lỗi required tại Next of Kin; không điều hướng | Medium |
| PPAC_KELT_PICIS_TC_017 | PICIS | High | Tel (Next of Kin) sai định dạng → lỗi | Màn Personal information | 1. Nhập Tel (Next of Kin) không hợp lệ<br>2. Tap "Next →" | Tel=8272930 | 1. Hiển thị **"Please enter a valid phone number"**; không điều hướng | High |
| PPAC_KELT_PICIS_TC_018 | PICIS | Medium | Trade không chọn → lỗi required | Màn Personal information | 1. Bỏ trống Trade<br>2. Tap "Next →" | Trade=(none) | 1. Hiển thị lỗi required tại Trade; không điều hướng | Medium |
| PPAC_KELT_PICIS_TC_020 | PICIS | Medium | UTR đúng format → chấp nhận (EP) | Màn Personal information | 1. Nhập UTR đúng định dạng<br>2. Rời focus | UTR=1234567890K | 1. Không lỗi UTR; field nhận giá trị | Medium |
| PPAC_KELT_PICIS_TC_021 | PICIS | High | UTR sai format → lỗi format verbatim | Màn Personal information | 1. Nhập UTR sai định dạng<br>2. Tap "Next →" | UTR=12345 | 1. Hiển thị **"The UTR number can only be of the form - 1234567890 or 1234567890K"**; không điều hướng | High |
| PPAC_KELT_PICIS_TC_022 | PICIS | High | Bank account number = 8 số → chấp nhận | Màn Personal information | 1. Nhập Bank account đúng 8 số<br>2. Rời focus | Bank acc=12345678 | 1. Không lỗi; field nhận giá trị | High |
| PPAC_KELT_PICIS_TC_023 | PICIS | High | Bank account ≠ 8 số (boundary 7 số) → lỗi format | Màn Personal information | 1. Nhập Bank account 7 số<br>2. Tap "Next →" | Bank acc=1234567 | 1. Hiển thị **"the bank account number can only be of the form - 12345678 or 8 digits ex.12345678"**; không điều hướng | High |
| PPAC_KELT_PICIS_TC_024 | PICIS | Medium | Name of bank bỏ trống → lỗi "Please enter here" | Màn Personal information | 1. Để trống Name of bank<br>2. Tap "Next →" | Name of bank=(empty) | 1. Hiển thị **"Please enter here"** tại Name of bank; không điều hướng | Medium |
| PPAC_KELT_PICIS_TC_025 | PICIS | Medium | Sort code bỏ trống → lỗi "Please enter here" | Màn Personal information | 1. Để trống Sort code<br>2. Tap "Next →" | Sort code=(empty) | 1. Hiển thị **"Please enter here"** tại Sort code; không điều hướng | Medium |
| PPAC_KELT_PICIS_TC_026 | PICIS | Medium | Roll number để trống vẫn Next được (OPTIONAL) | Màn Personal information, các field bắt buộc hợp lệ, Roll number trống | 1. Để trống Roll number (if applicable)<br>2. Tap "Next →" | Roll number=(empty) | 1. KHÔNG báo lỗi tại Roll number; điều hướng sang Health Questionnaire (field optional) | Medium |
| PPAC_KELT_PICIS_TC_027 | PICIS | Medium | Có field UTR Number (đặc thù CIS) | Màn Personal information CIS | 1. Cuộn form tìm field UTR Number | N/A | 1. **CÓ** field "UTR Number" (đặc thù nhánh CIS — đối chiếu Umbrella không có) | Medium |
| PPAC_KELT_PICIS_TC_028 | PICIS | Medium | Nhiều field required trống cùng lúc → mỗi field báo lỗi | Màn Personal information trống (trừ pre-filled) | 1. Để trống toàn bộ field bắt buộc<br>2. Tap "Next →" | all empty | 1. Mỗi field bắt buộc hiển thị lỗi tương ứng; không điều hướng | Medium |

---

> **Part 2 = 27 TC** (PICIS; bỏ TC_019, giữ gap ID).

---

## Test Cases — Part 3

### LTDCO — Company info, nhánh Limited (M-LTD-01 / M-LTD-02)

> Pre-Condition chung: đã chọn **Limited** ở Worker Payment Status (provider = Industrial Labour — text WPS đang hiển thị sai "Riddingtons", xem DEF); màn hiển thị section **"Kindly provide one of the following:"** với 2 nút Company Registration Number / Company Trading Name. (Card MD + VAT giống nhau giữa M-LTD-01 và M-LTD-02.)

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_LTDCO_TC_001 | LTDCO | High | Bắt buộc chọn 1 trong 2 (one-of) | Màn "Kindly provide one of the following" | 1. Không chọn Company Registration Number / Company Trading Name<br>2. Thử tiếp tục | (none) | 1. Không tiếp tục được — bắt buộc chọn 1 trong 2 (one-of) | High |
| PPAC_KELT_LTDCO_TC_002 | LTDCO | Medium | Chọn "Company Registration Number" → màn M-LTD-01 | Màn "Kindly provide one of the following" | 1. Tap **"Company Registration Number →"** | N/A | 1. Điều hướng tới màn tiêu đề **"Company Registration Number"** (M-LTD-01) | Medium |
| PPAC_KELT_LTDCO_TC_003 | LTDCO | Medium | Chọn "Company Trading Name" → màn M-LTD-02 | Màn "Kindly provide one of the following" | 1. Tap **"Company Trading Name →"** | N/A | 1. Điều hướng tới màn tiêu đề **"Company Trading Name"**  | Medium |
| PPAC_KELT_LTDCO_TC_004 | LTDCO | High | M-LTD-01: Company Registration Number bỏ trống → lỗi | Màn Company Registration Number (M-LTD-01) | 1. Để trống field Company Registration Number<br>2. Tap "Next →" | (empty) | 1. Hiển thị **"Please enter here"**; không điều hướng | High |
| PPAC_KELT_LTDCO_TC_005 | LTDCO | High | M-LTD-01: Reg Number sai format → lỗi verbatim | Màn Company Registration Number | 1. Nhập sai định dạng<br>2. Tap "Next →" | CHCJMX | 1. Hiển thị **"The Company Registration Number can only be of the form – 12345678 or two letters followed by 8 digits ex. SC12345678"** | High |
| PPAC_KELT_LTDCO_TC_006 | LTDCO | High | M-LTD-01: Reg Number = 8 chữ số → chấp nhận (BVA/EP) | Màn Company Registration Number | 1. Nhập 8 chữ số<br>2. Rời focus | 12345678 | 1. Không lỗi; field nhận giá trị | High |
| PPAC_KELT_LTDCO_TC_007 | LTDCO | Medium | M-LTD-01: Reg Number = 2 chữ + 8 số → chấp nhận (EP) | Màn Company Registration Number | 1. Nhập 2 chữ cái + 8 số<br>2. Rời focus | SC12345678 | 1. Không lỗi; field nhận giá trị | Medium |
| PPAC_KELT_LTDCO_TC_008 | LTDCO | High | "Are you a Managing Director?" chưa chọn → lỗi | Màn company-info (M-LTD-01 hoặc M-LTD-02) | 1. Không chọn Yes/No ở "Are you a Managing Director?"<br>2. Tap "Next →" | (none) | 1. Hiển thị **"Please select here"**; không điều hướng | High |
| PPAC_KELT_LTDCO_TC_009 | LTDCO | High | MD = No → hiện field "Please enter your position" (State Transition) | Màn company-info | 1. Chọn **No** ở "Are you a Managing Director?"<br>2. Quan sát | MD=No | 1. Hiện field **"Please enter your position"** (placeholder "Please enter here") | High |
| PPAC_KELT_LTDCO_TC_010 | LTDCO | High | MD = No + position bỏ trống → lỗi | Màn company-info, MD=No (đã hiện field position) | 1. Để trống "Please enter your position"<br>2. Tap "Next →" | position=(empty) | 1. Hiển thị **"Please enter here"** tại field position; không điều hướng | High |
| PPAC_KELT_LTDCO_TC_011 | LTDCO | Medium | MD = Yes → ẩn field position (State Transition) | Màn company-info, MD đang = No (field position hiển thị) | 1. Đổi sang **Yes**<br>2. Quan sát | MD=Yes | 1. Field "Please enter your position" **ẩn**; không yêu cầu position | Medium |
| PPAC_KELT_LTDCO_TC_012 | LTDCO | High | "Are you VAT Registered?" chưa chọn → lỗi | Màn company-info | 1. Không chọn Yes/No ở "Are you VAT Registered?"<br>2. Tap "Next →" | VAT=(none) | 1. Hiển thị **"Please select here"**; không điều hướng | High |
| PPAC_KELT_LTDCO_TC_013 | LTDCO | High | M-LTD-02: Company Trading Name bỏ trống → lỗi | Màn Company Registration Name (M-LTD-02) | 1. Để trống Company Trading Name<br>2. Tap "Next →" | (empty) | 1. Hiển thị **"Please enter here"**; không điều hướng | High |
| PPAC_KELT_LTDCO_TC_014 | LTDCO | High | M-LTD-02: Trading Name nhập text bất kỳ → chấp nhận (KHÔNG validate format) | Màn Company Registration Name | 1. Nhập text bất kỳ không trống<br>2. Rời focus | ABC Builders Ltd | 1. Không lỗi; field chấp nhận **text bất kỳ** (không validate format reg number) | High |
| PPAC_KELT_LTDCO_TC_015 | LTDCO | High | Hoàn tất company-info (VAT=No) → Next → Welcome Industrial Labour LTD | Màn company-info, đã nhập Reg Number/Trading Name + MD + VAT | 1. Nhập đủ field hợp lệ, chọn VAT Registered = No<br>2. Tap "Next →" | Reg=12345678; MD=Yes; VAT=No | 1. Điều hướng tới Welcome **"Welcome to Industrial Labour LTD payroll services."** → Personal information bản Limited | High |

### PILTD — Personal information, nhánh Limited (M04·LTD)

> Pre-Condition chung: đã hoàn tất company-info + Welcome (Industrial Labour LTD); màn **Personal information** bản Limited. Pre-filled = **Surname/Forenames/Email** (read-only). Biến thể theo "Are you VAT Registered?": **No** → không có field VAT; **Yes** → thêm VAT Number + Your VAT Certificate.

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_PILTD_TC_001 | PILTD | Critical | Happy path VAT=No → Next sang Health Questionnaire | Đã vào PILTD, VAT Registered = No (không có field VAT) | 1. Upload passport<br>2. Nhập Address/City/Postcode, Phone<br>3. Nhập UTR, Company trading name, Company registration number, Name of bank, Bank account, Sort code, chọn Trade<br>4. Tap "Next →" | Phone=07700900111; UTR=1234567890; Bank acc=12345678; Sort=12-34-56; Roll=(trống) | 1. Mọi field hợp lệ, không lỗi<br>2. Điều hướng tới tab **Health Questionnaire** | Critical |
| PPAC_KELT_PILTD_TC_002 | PILTD | High | Your passport bỏ trống → "Please upload your passport" | Màn PILTD, chưa upload passport | 1. Để trống passport<br>2. Tap "Next →" | passport=(none) | 1. Hiển thị **"Please upload your passport"**; không điều hướng | High |
| PPAC_KELT_PILTD_TC_003 | PILTD | High | Field pre-filled (Surname/Forenames/Email) read-only | Màn PILTD | 1. Thử chỉnh sửa / xóa Surname, Forenames, Email | N/A | 1. **Không cho chỉnh sửa/xóa** — 3 field read-only, giữ nguyên từ profile | High |
| PPAC_KELT_PILTD_TC_004 | PILTD | Medium | Khác CIS — KHÔNG có NIN/DOB/Citizenship/Contract start/Next of Kin/Tel | Màn PILTD | 1. Cuộn toàn bộ form, tìm các field NIN, Date of Birth, Citizenship, Contract/Job start date, Next of Kin, Tel (Next of Kin) | N/A | 1. **KHÔNG có** 6 field này ở bản Limited (khác CIS) | Medium |
| PPAC_KELT_PILTD_TC_005 | PILTD | Medium | Có field Company trading name + Company registration number | Màn PILTD | 1. Cuộn form tìm 2 field Company | N/A | 1. **CÓ** "Company trading name" và "Company registration number" (đặc thù Limited) | Medium |
| PPAC_KELT_PILTD_TC_006 | PILTD | Medium | Company trading name pre-fill từ M-LTD-02 | Đã chọn path Company Trading Name ở company-info, nhập "ABC Builders Ltd" | 1. Vào PILTD, quan sát field "Company trading name" | đã nhập "ABC Builders Ltd" ở M-LTD-02 | 1. Field "Company trading name" **điền sẵn** = "ABC Builders Ltd" (pre-fill từ M-LTD-02) | Medium |
| PPAC_KELT_PILTD_TC_007 | PILTD | Medium | Company registration number pre-fill từ M-LTD-01 | Đã chọn path Company Registration Number ở company-info, nhập "SC12345678" | 1. Vào PILTD, quan sát field "Company registration number" | đã nhập "SC12345678" ở M-LTD-01 | 1. Field "Company registration number" **điền sẵn** = "SC12345678" (pre-fill từ M-LTD-01) | Medium |
| PPAC_KELT_PILTD_TC_008 | PILTD | High | VAT Registered = Yes → hiện VAT Number + Your VAT Certificate (Decision Table) | Đã chọn VAT Registered = **Yes** ở company-info | 1. Vào PILTD<br>2. Quan sát vùng sau UTR Number | VAT=Yes | 1. Hiện thêm **VAT Number** + **Your VAT Certificate** (upload) ngay sau UTR Number | High |
| PPAC_KELT_PILTD_TC_009 | PILTD | Medium | VAT Registered = No → KHÔNG có VAT Number/Certificate | Đã chọn VAT Registered = **No** ở company-info | 1. Vào PILTD<br>2. Quan sát vùng sau UTR Number | VAT=No | 1. **KHÔNG có** field VAT Number / Your VAT Certificate | Medium |
| PPAC_KELT_PILTD_TC_010 | PILTD | High | VAT=Yes — upload Your VAT Certificate qua component | Màn PILTD, VAT=Yes | 1. Tap "+ Upload document" ở Your VAT Certificate<br>2. Chọn nguồn → chụp/upload → Okay | VAT Certificate document | 1. Mở component "Pick your source of document" → upload thành công → card "VAT Certificate Document" (✓) hiển thị ở field | High |
| PPAC_KELT_PILTD_TC_011 | PILTD | Medium | Candidate's Mobile Phone < 11 số → lỗi (BVA) | Màn PILTD | 1. Nhập Phone < 11 số<br>2. Tap "Next →" | Phone=8272930 | 1. Hiển thị **"either your phone number is invalid or use at least 11 digits"**; không điều hướng | Medium |
| PPAC_KELT_PILTD_TC_012 | PILTD | Medium | UTR sai format → lỗi verbatim | Màn PILTD | 1. Nhập UTR sai định dạng<br>2. Tap "Next →" | UTR=12345 | 1. Hiển thị **"The UTR number can only be of the form - 1234567890 or 1234567890K"** | Medium |
| PPAC_KELT_PILTD_TC_013 | PILTD | Medium | Bank account ≠ 8 số (boundary 7 số) → lỗi | Màn PILTD | 1. Nhập Bank account 7 số<br>2. Tap "Next →" | Bank acc=1234567 | 1. Hiển thị **"the bank account number can only be of the form - 12345678 or 8 digits ex.12345678"** | Medium |
| PPAC_KELT_PILTD_TC_014 | PILTD | Medium | Name of bank / Sort code bỏ trống → "Please enter here" | Màn PILTD | 1. Để trống Name of bank và Sort code<br>2. Tap "Next →" | (empty) | 1. Mỗi field hiển thị **"Please enter here"**; không điều hướng | Medium |
| PPAC_KELT_PILTD_TC_015 | PILTD | Medium | Address / City / Postcode bỏ trống → lỗi required | Màn PILTD | 1. Để trống Address, City, Postcode<br>2. Tap "Next →" | (empty) | 1. Mỗi field hiển thị lỗi required; không điều hướng | Medium |
| PPAC_KELT_PILTD_TC_016 | PILTD | Medium | Trade không chọn → lỗi required | Màn PILTD | 1. Bỏ trống Trade<br>2. Tap "Next →" | Trade=(none) | 1. Hiển thị lỗi required tại Trade; không điều hướng | Medium |
| PPAC_KELT_PILTD_TC_017 | PILTD | Medium | Roll number để trống vẫn Next được (OPTIONAL) | Màn PILTD, field bắt buộc hợp lệ, Roll number trống | 1. Để trống Roll number<br>2. Tap "Next →" | Roll=(empty) | 1. KHÔNG báo lỗi Roll number; điều hướng sang Health Questionnaire | Medium |
| PPAC_KELT_PILTD_TC_018 | PILTD | High | Happy path VAT=Yes (đã upload VAT cert) → Next sang Health Questionnaire | Màn PILTD, VAT=Yes, đã upload VAT Certificate | 1. Điền đủ field hợp lệ + VAT Number<br>2. Tap "Next →" | VAT Number=GB123456789; các field hợp lệ | 1. Không lỗi; điều hướng tới tab **Health Questionnaire** | High |

---

> **Part 3 = 33 TC** (LTDCO 15 · PILTD 18).

---

## Test Cases — Part 4

### PIUMB — Personal information, nhánh Umbrella (M04·UMB)

> Pre-Condition chung: đã chọn **Umbrella** → provider Riddingtons (radio 1 option) → Welcome (Riddingtons); màn **Personal information** bản Umbrella. **= GIỐNG CIS nhưng BỎ field UTR Number.** Pre-filled = Surname/Forenames/Email/DOB/Citizenship (read-only). Các validation field giống PICIS — chỉ test đại diện.

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_PIUMB_TC_001 | PIUMB | Critical | Happy path → Next sang Health Questionnaire | Màn Personal information bản Umbrella | 1. Upload passport<br>2. Nhập Address/City/Postcode, Phone, NIN<br>3. Chọn Contract start date, Next of Kin + Tel, Trade<br>4. Nhập Name of bank, Bank account, Sort code<br>5. Tap "Next →" | Phone=07700900111; NIN=AH123456L; Bank acc=12345678; Sort=12-34-56 | 1. Mọi field hợp lệ, không lỗi<br>2. Điều hướng tới tab **Health Questionnaire** | Critical |
| PPAC_KELT_PIUMB_TC_002 | PIUMB | High | Your passport bỏ trống → "Please upload your passport" | Màn PIUMB, chưa upload passport | 1. Để trống passport<br>2. Tap "Next →" | passport=(none) | 1. Hiển thị **"Please upload your passport"**; không điều hướng | High |
| PPAC_KELT_PIUMB_TC_003 | PIUMB | High | Field pre-filled (cả 5) read-only | Màn PIUMB | 1. Thử chỉnh sửa / xóa Surname, Forenames, Email, Date of Birth, Citizenship | N/A | 1. **Không cho chỉnh sửa/xóa** — cả 5 field read-only | High |
| PPAC_KELT_PIUMB_TC_004 | PIUMB | Medium | KHÔNG có field UTR Number (khác CIS) | Màn PIUMB | 1. Cuộn toàn bộ form tìm field "UTR Number" | N/A | 1. **KHÔNG có** field UTR Number (đặc trưng Umbrella — khác CIS có UTR) | Medium |
| PPAC_KELT_PIUMB_TC_005 | PIUMB | Medium | KHÔNG có VAT / Company fields | Màn PIUMB | 1. Tìm field VAT Number, Your VAT Certificate, Company trading name, Company registration number | N/A | 1. **KHÔNG có** các field VAT/Company (chỉ Limited mới có) | Medium |
| PPAC_KELT_PIUMB_TC_006 | PIUMB | Medium | CÓ NIN/DOB/Citizenship/Contract/Next of Kin/Tel (giống CIS) | Màn PIUMB | 1. Cuộn form xác nhận có các field này | N/A | 1. **CÓ** National Insurance Number, Date of Birth, Citizenship, Contract/Job start date, Next of Kin, Tel (Next of Kin) — giống CIS | Medium |
| PPAC_KELT_PIUMB_TC_007 | PIUMB | High | NIN sai format → lỗi verbatim (đại diện) | Màn PIUMB | 1. Nhập NIN sai<br>2. Tap "Next →" | NIN=MIDORI | 1. Hiển thị **"Please enter the right format: 2 letters, 6 numbers, 1 letter (e.g. AA999999A)"** | High |
| PPAC_KELT_PIUMB_TC_008 | PIUMB | Medium | Candidate's Mobile Phone < 11 số → lỗi (đại diện) | Màn PIUMB | 1. Nhập Phone < 11 số<br>2. Tap "Next →" | Phone=8272930 | 1. Hiển thị **"either your phone number is invalid or use at least 11 digits"** | Medium |
| PPAC_KELT_PIUMB_TC_009 | PIUMB | Medium | Bank account ≠ 8 số → lỗi (đại diện) | Màn PIUMB | 1. Nhập Bank account 7 số<br>2. Tap "Next →" | Bank acc=1234567 | 1. Hiển thị **"the bank account number can only be of the form - 12345678 or 8 digits ex.12345678"** | Medium |
| PPAC_KELT_PIUMB_TC_010 | PIUMB | Medium | Name of bank / Sort code bỏ trống → "Please enter here" | Màn PIUMB | 1. Để trống Name of bank và Sort code<br>2. Tap "Next →" | (empty) | 1. Mỗi field hiển thị **"Please enter here"** | Medium |

### HQ — Health Questionnaire (M05) — *(áp dụng cả CIS / Limited / Umbrella)*

> Pre-Condition chung: đã hoàn tất Personal information, màn tab **Health Questionnaire**. 10 câu (Q1–8, 8.1, 8.2), mỗi câu radio **Yes/No bắt buộc**. Nội dung 10 câu verbatim xem requirements §4.5.

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_HQ_TC_001 | HQ | Critical | Trả lời đủ 10 câu (mix) + details hợp lệ → Next sang Declarations | Màn Health Questionnaire | 1. Trả lời 10 câu (vài câu Yes có nhập details, còn lại No)<br>2. Tap "Next →" | Q1=Yes details "Asthma - using inhaler"; còn lại No | 1. Không lỗi; điều hướng tới tab **Declarations** | Critical |
| PPAC_KELT_HQ_TC_002 | HQ | High | Bỏ trống ≥1 câu → "Please answer this question" | Màn Health Questionnaire | 1. Để trống 1 câu (vd Q2)<br>2. Trả lời các câu khác<br>3. Tap "Next →" | Q2=(chưa chọn) | 1. Hiển thị **"Please answer this question"** tại Q2; không điều hướng | High |
| PPAC_KELT_HQ_TC_003 | HQ | High | Chọn Yes → hiện field "Please enter details" (State Transition) | Màn Health Questionnaire | 1. Chọn **Yes** ở 1 câu<br>2. Quan sát | Q1=Yes | 1. Hiện field **"Please enter details"** (placeholder "Please enter here") dưới câu đó | High |
| PPAC_KELT_HQ_TC_004 | HQ | High | Yes + details bỏ trống → "Please give us the details here" | Màn Health Questionnaire, 1 câu = Yes (field details hiển thị) | 1. Để trống field details<br>2. Tap "Next →" | Q1=Yes, details=(empty) | 1. Hiển thị **"Please give us the details here"**; không điều hướng | High |
| PPAC_KELT_HQ_TC_005 | HQ | Medium | Chọn No → ẩn field details (State Transition) | Màn Health Questionnaire | 1. Chọn **No** ở 1 câu<br>2. Quan sát | Q1=No | 1. **Ẩn** field "Please enter details" của câu đó | Medium |
| PPAC_KELT_HQ_TC_006 | HQ | Medium | Đổi Yes → No sau khi đã hiện details → ẩn field (State Transition) | Màn Health Questionnaire, 1 câu = Yes (details hiển thị) | 1. Đổi câu đó sang **No**<br>2. Quan sát | Q1: Yes→No | 1. Field details ẩn (trạng thái reset) | Medium |
| PPAC_KELT_HQ_TC_007 | HQ | High | Tất cả 10 câu = No → Next sang Declarations | Màn Health Questionnaire | 1. Chọn No cho cả 10 câu<br>2. Tap "Next →" | all=No | 1. Không có field details; điều hướng tới Declarations | High |
| PPAC_KELT_HQ_TC_008 | HQ | Medium | Tất cả 10 câu = Yes + điền đủ details → Next | Màn Health Questionnaire | 1. Chọn Yes cả 10 câu<br>2. Nhập details mỗi câu<br>3. Tap "Next →" | all=Yes + details | 1. Mỗi câu hiện field details; điền đủ → điều hướng tới Declarations | Medium |
| PPAC_KELT_HQ_TC_009 | HQ | Medium | Tất cả câu chưa trả lời → mỗi câu báo lỗi | Màn Health Questionnaire trống | 1. Không trả lời câu nào<br>2. Tap "Next →" | all empty | 1. Mỗi câu (10 câu) hiển thị **"Please answer this question"** | Medium |
| PPAC_KELT_HQ_TC_010 | HQ | Medium | Hiển thị đủ 10 câu đúng nội dung/thứ tự + intro verbatim | Màn Health Questionnaire | 1. Quan sát intro + danh sách câu hỏi | N/A | 1. Intro "Details given are deemed relevant in the interest of your Health and Safety and allow the company to assess the risk to your health."<br>2. Đủ 10 câu Q1–8, 8.1, 8.2 đúng nội dung/thứ tự (xem requirements §4.5) | Medium |
| PPAC_KELT_HQ_TC_011 | HQ | Medium | Mỗi câu radio Yes/No mutually exclusive | Màn Health Questionnaire | 1. Chọn Yes 1 câu<br>2. Chọn No cùng câu đó | Q1: Yes→No | 1. Chỉ 1 lựa chọn active; chọn No bỏ chọn Yes | Medium |
| PPAC_KELT_HQ_TC_012 | HQ | Low | Health Questionnaire giống nhau ở CIS / Limited / Umbrella | Vào Health Questionnaire từ mỗi nhánh | 1. So sánh tab Health Questionnaire ở 3 nhánh | N/A | 1. Nội dung + hành vi 10 câu giống hệt ở cả 3 nhánh (AMB-31) | Low |

---

> **Part 4 = 22 TC** (PIUMB 10 · HQ 12).

---

## Test Cases — Part 5

### DECL — Declarations (M06) — *(áp dụng cả CIS / Limited / Umbrella)*

> Pre-Condition chung: đã hoàn tất Health Questionnaire, màn tab **Declarations**. 3 link policy + 1 checkbox xác nhận. Provider ở link #3 + text checkbox theo nhánh (CIS = provider đã chọn; **Limited = Industrial Labour**; Umbrella = Riddingtons).

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_DECL_TC_001 | DECL | High | Tick checkbox + Next → Skill card verification | Màn Declarations | 1. Tick checkbox xác nhận<br>2. Tap "Next →" | checkbox = ON | 1. Điều hướng tới **Skill card verification** (M07) | High |
| PPAC_KELT_DECL_TC_002 | DECL | High | Chưa tick checkbox + Next → lỗi | Màn Declarations, checkbox OFF | 1. Không tick checkbox<br>2. Tap "Next →" | checkbox = OFF | 1. Hiển thị lỗi đỏ **"Please review and confirm these declarations to continue."**; không điều hướng | High |
| PPAC_KELT_DECL_TC_003 | DECL | High | Link "Keltbray Privacy Policies" → mở trình duyệt ngoài | Màn Declarations | 1. Tap link **"Keltbray Privacy Policies"** | N/A | 1. Mở **trình duyệt ngoài (external browser)** tới trang Keltbray Privacy Policy [URL verify khi test] | High |
| PPAC_KELT_DECL_TC_004 | DECL | Medium | Link "Keltbray GDPR Privacy Policies" → mở trình duyệt ngoài | Màn Declarations | 1. Tap link **"Keltbray GDPR Privacy Policies"** | N/A | 1. Mở browser ngoài tới Keltbray GDPR Privacy Policy [URL verify] | Medium |
| PPAC_KELT_DECL_TC_005 | DECL | Medium | Link "{Provider} GDPR Privacy Policies" → mở trình duyệt ngoài | Màn Declarations | 1. Tap link **"{Provider} GDPR Privacy Policies"** (vd "Riddingtons GDPR Privacy Policies") | N/A | 1. Mở browser ngoài tới {Provider} GDPR Privacy Policy [URL verify] | Medium |
| PPAC_KELT_DECL_TC_006 | DECL | Medium | Link #3 + text checkbox đổi theo provider | Vào Declarations từ các nhánh khác nhau | 1. CIS·Riddingtons → quan sát<br>2. CIS·Industrial Labour → quan sát<br>3. Umbrella → quan sát | N/A | 1. Link #3 + tên trong checkbox đổi theo provider: Riddingtons / Industrial Labour ("…sent to Keltbray and {Provider} my payroll provider") | Medium |
| PPAC_KELT_DECL_TC_007 | DECL | Medium | Body + checkbox verbatim | Màn Declarations | 1. Đọc body + text checkbox | N/A | 1. Body "Please confirm you understand and agree with keltbray rules and policies:"<br>2. Checkbox "I can confirm that all the information is correct, and I understand my details will be sent to Keltbray and {Provider} my payroll provider" | Medium |

### SKILL — Skill card verification + Submit (M07)

> Pre-Condition chung: đã hoàn tất Declarations (tick + Next), màn **Skill card verification**.

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_SKILL_TC_001 | SKILL | High | Chưa chọn Skill card type → Continue disabled | Màn Skill card verification, chưa chọn type | 1. Quan sát nút Continue khi chưa chọn type | N/A | 1. Nút **"Continue →" disabled** (mờ); không bấm được | High |
| PPAC_KELT_SKILL_TC_002 | SKILL | High | Chọn Skill card type → Continue enabled | Màn Skill card verification | 1. Chọn 1 Skill card type<br>2. Quan sát nút Continue | type = SIA | 1. Nút **"Continue →" enabled** (đậm) sau khi chọn | High |
| PPAC_KELT_SKILL_TC_003 | SKILL | Critical | Chọn type + Continue → submission thành công (popup) | Màn Skill card verification, đã chọn type | 1. Chọn Skill card type<br>2. Tap "Continue →" | type = SIA | 1. Hiển thị **popup "Your check has been submitted!"** = submission thành công | Critical |
| PPAC_KELT_SKILL_TC_004 | SKILL | Medium | Body + label verbatim | Màn Skill card verification | 1. Đọc tiêu đề + body + label | N/A | 1. Tiêu đề "Skill card verification"; body "We need your skill card information to complete the compliance check."; label "Skill card type"; placeholder "Select skill card type" | Medium |


### DOC — Component Document Upload (Cmp-DOC) — *(vd Your VAT Certificate, Limited VAT=Yes)*

> Pre-Condition chung: ở field upload tài liệu (vd "Your VAT Certificate" — PILTD VAT=Yes). {DocType} = "VAT Certificate".

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_DOC_TC_001 | DOC | High | Bấm "+ Upload document" → sheet "Pick your source of document" | Field Your VAT Certificate, chưa upload | 1. Tap "+ Upload document" | N/A | 1. Mở bottom sheet **"Pick your source of document"** với 2 nút: **"Open camera"** + **"Upload from your device"** | High |
| PPAC_KELT_DOC_TC_002 | DOC | High | Open camera → màn "Capture your document" | Sheet "Pick your source of document" | 1. Tap "Open camera" | N/A | 1. Mở màn **"Capture your document"** + hướng dẫn "Position your VAT Certificate Document within the frame and ensure the details are clearly readable." + nút chụp | High |
| PPAC_KELT_DOC_TC_003 | DOC | High | Chụp → màn preview với Retake/Okay | Màn "Capture your document" | 1. Tap nút chụp | N/A | 1. Hiển thị màn preview **"VAT Certificate Document"** với ảnh vừa chụp + 2 nút **"Retake"** / **"Okay"** | High |
| PPAC_KELT_DOC_TC_004 | DOC | Medium | Retake → chụp lại | Màn preview (sau chụp) | 1. Tap "Retake" | N/A | 1. Quay lại màn camera để chụp lại | Medium |
| PPAC_KELT_DOC_TC_005 | DOC | High | Okay → quay về form, card đã upload hiển thị | Màn preview (sau chụp) | 1. Tap "Okay" | N/A | 1. Quay về form; field hiển thị **card "VAT Certificate Document"** (icon ✓ xanh) + icon thùng rác | High |
| PPAC_KELT_DOC_TC_006 | DOC | Medium | Upload from your device → mở file picker | Sheet "Pick your source of document" | 1. Tap "Upload from your device" | N/A | 1. Mở file picker của thiết bị (chọn file png/jpg/jpeg/doc/docx/pdf) | Medium |
| PPAC_KELT_DOC_TC_007 | DOC | Medium | Tap card đã upload → view + "Remove and Resubmit" | Field có card "VAT Certificate Document" đã upload | 1. Tap vào card | N/A | 1. Mở màn **"VAT Certificate Document"** xem ảnh + nút **"Remove and Resubmit"** | Medium |
| PPAC_KELT_DOC_TC_008 | DOC | Medium | "Remove and Resubmit" → xóa & chụp/upload lại | Màn view "VAT Certificate Document" | 1. Tap "Remove and Resubmit" | N/A | 1. Xóa tài liệu hiện tại, cho phép chụp/upload lại (quay về sheet/camera) | Medium |
| PPAC_KELT_DOC_TC_009 | DOC | Medium | Tap thùng rác → dialog "Remove VAT Certificate Document?" | Field có card đã upload | 1. Tap icon thùng rác trên card | N/A | 1. Hiển thị dialog **"Remove VAT Certificate Document?"** với 2 nút **"Yes, remove it!"** / **"Cancel"** | Medium |
| PPAC_KELT_DOC_TC_010 | DOC | Medium | Dialog xóa: "Yes, remove it!" xóa / "Cancel" giữ | Dialog "Remove VAT Certificate Document?" | 1. Tap "Yes, remove it!" (TH1) / "Cancel" (TH2) | N/A | 1. "Yes, remove it!" → xóa card, field trống lại<br>2. "Cancel" → đóng dialog, giữ nguyên file | Medium |

---

> **Part 5 = 21 TC** (DECL 7 · SKILL 4 · DOC 10).

---

## Test Cases — Part 6

### NSITE — Select site location & subcontractor (N02, luồng normal)

> Pre-Condition chung: đã chọn sector **"Keltbray - Keltbray"** (normal) → màn Select site location. *(Required-status location/subcontractor + hành vi checkbox = ngoài scope theo QA — không sinh TC validation.)*

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_NSITE_TC_001 | NSITE | High | Chọn location + subcontractor → Continue → Skill card | Màn Select site location | 1. Chọn Select site location<br>2. Chọn Select subcontractor<br>3. Tap "Continue →" | site + subcontractor (chọn bất kỳ) | 1. Điều hướng tới màn **Skill card verification** (N03) | High |
| PPAC_KELT_NSITE_TC_002 | NSITE | Low | Hiển thị 2 dropdown + checkbox + Continue | Màn Select site location | 1. Quan sát màn | N/A | 1. Có "Select site location", "Select subcontractor", checkbox "I can't find my subcontractor in the list", nút "Continue →", bottom nav 4 mục | Low |

### NSKILL — Skill card verification (N03, luồng normal)

> Pre-Condition chung: đã hoàn tất N02 → màn Skill card verification (normal).

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_NSKILL_TC_001 | NSKILL | High | Chưa chọn Skill card type → Continue disabled | Màn Skill card verification (normal) | 1. Quan sát nút Continue khi chưa chọn | N/A | 1. Nút **"Continue →" disabled** | High |
| PPAC_KELT_NSKILL_TC_002 | NSKILL | Critical | Chọn type + Continue → submission thành công (popup) | Màn Skill card verification (normal) | 1. Chọn Skill card type<br>2. Tap "Continue →" | type = SIA | 1. Hiển thị popup **"Your check has been submitted!"** | Critical |
| PPAC_KELT_NSKILL_TC_003 | NSKILL | Medium | Body + helper "Not Applicable" + label verbatim | Màn Skill card verification (normal) | 1. Đọc body, helper, label; mở dropdown | N/A | 1. Body "We need your skill card information to complete the compliance check."<br>2. Helper "If your trade/occupation does not require a skill card please scroll to Not Applicable."<br>3. Label "Skill card type:"; dropdown có option **"Not Applicable"** | Medium |

### NAV — Back / Reset & Navigation (BR-39)

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_NAV_TC_001 | NAV | High | Nhập dở → Back → quay lại → dữ liệu RESET (BR-39, khác DTSource) | Đang ở 1 màn (vd Personal information), đã nhập 1 phần | 1. Nhập 1 phần dữ liệu<br>2. Tap Back<br>3. Quay lại màn đó | dữ liệu một phần | 1. Dữ liệu đã nhập **bị RESET** (không lưu) — ⚠️ KHÁC DTSource (DTSource giữ data) | High |
| PPAC_KELT_NAV_TC_002 | NAV | High | Thoát app giữa journey → mở lại → KHÔNG resume | Đang giữa journey onboarding, đã nhập 1 phần | 1. Thoát app<br>2. Mở lại onboarding | onboarding dở dang | 1. **KHÔNG resume** — bắt đầu lại, dữ liệu trước đã mất (reset) | High |

### E2E — End-to-End (cả 2 sector → submission thành công)

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_E2E_TC_001 | E2E | Critical | E2E CIS · Riddingtons → submission thành công | Prefix keltbray → KRS | 1. WPS=CIS, provider=Riddingtons<br>2. Welcome → Personal info (đủ field hợp lệ)<br>3. Health Q (all No)<br>4. Declarations (tick)<br>5. Skill card type + Continue | NIN=AH123456L; Phone=07700900111; Bank=12345678; type=SIA | 1. Đi qua đúng thứ tự, không bị chặn<br>2. Popup **"Your check has been submitted!"** | Critical |
| PPAC_KELT_E2E_TC_002 | E2E | Critical | E2E CIS · Industrial Labour → submission thành công | Prefix keltbray → KRS | 1. WPS=CIS, provider=Industrial Labour<br>2. Welcome (Industrial Labour LTD) → Personal info<br>3. Health Q → Declarations → Skill card + Continue | (như E2E_TC_001) | 1. Popup **"Your check has been submitted!"** | Critical |
| PPAC_KELT_E2E_TC_003 | E2E | Critical | E2E Limited (VAT=No) → submission thành công | Prefix keltbray → KRS | 1. WPS=Limited → Company Registration Number (12345678) + MD=Yes + VAT=No<br>2. Welcome → Personal info Limited<br>3. Health Q → Declarations → Skill card + Continue | Reg=12345678; type=SIA | 1. Popup **"Your check has been submitted!"** | Critical |
| PPAC_KELT_E2E_TC_004 | E2E | Critical | E2E Limited (VAT=Yes, upload VAT cert) → submission thành công | Prefix keltbray → KRS | 1. WPS=Limited → Company Trading Name + MD=No (position) + VAT=Yes<br>2. Personal info Limited: nhập VAT Number + upload Your VAT Certificate<br>3. Health Q → Declarations → Skill card + Continue | Trading="ABC Builders Ltd"; VAT Number=GB123456789; type=SIA | 1. Popup **"Your check has been submitted!"** | Critical |
| PPAC_KELT_E2E_TC_005 | E2E | Critical | E2E Umbrella → submission thành công | Prefix keltbray → KRS | 1. WPS=Umbrella, provider=Riddingtons<br>2. Welcome → Personal info Umbrella (không UTR)<br>3. Health Q → Declarations → Skill card + Continue | NIN=AH123456L; Bank=12345678; type=SIA | 1. Popup **"Your check has been submitted!"** | Critical |
| PPAC_KELT_E2E_TC_006 | E2E | Critical | E2E Normal (Keltbray-Keltbray) → submission thành công | Prefix keltbray | 1. Chọn "Keltbray - Keltbray" → Continue<br>2. Select site location + subcontractor → Continue<br>3. Skill card type + Continue | site/subcontractor; type=SIA | 1. Đi qua 3 bước; popup **"Your check has been submitted!"** | Critical |

### E2E (Post-submission) — Admin portal / Client dashboard / Email

> ⚠️ **Mở rộng scope ngoài mobile app** (Admin portal · Client dashboard · Email). Các hệ thống này **chưa có trong requirements** (mobile-only) → chi tiết portal/nội dung email đánh dấu `[verify khi test]`. Pre-Condition chung: worker đã submit onboarding ở mobile thành công (popup "Your check has been submitted!").

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_E2E_TC_007 | E2E | High | Admin portal — submission đã đăng ký thành công | Worker vừa submit ở mobile (popup "Your check has been submitted!") | 1. Đăng nhập **Admin portal**<br>2. Tìm submission của worker vừa submit | worker = Maya (submission vừa tạo) | 1. Submission **xuất hiện** ở Admin portal, đăng ký thành công (status khởi tạo) | High |
| PPAC_KELT_E2E_TC_008 | E2E | High | Admin portal — đổi status submission sang "go to site" | Submission đã có ở Admin portal (E2E_TC_007) | 1. Mở submission<br>2. Đổi status sang **"go to site"**<br>3. Lưu | status = "go to site" | 1. Status cập nhật thành công = **"go to site"** | High |
| PPAC_KELT_E2E_TC_009 | E2E | High | Client dashboard — hiển thị submission sau khi status = "go to site" | Admin đã đổi status sang "go to site" (E2E_TC_008) | 1. Đăng nhập **Client dashboard**<br>2. Quan sát danh sách submission/worker | status = "go to site" | 1. Submission/worker **hiển thị** trên Client dashboard sau khi status = "go to site"  | High |
| PPAC_KELT_E2E_TC_010 | E2E | High | Email — gửi sau khi status = "go to site" | Admin đã đổi status sang "go to site" (E2E_TC_008) | 1. Kiểm tra hộp thư (worker / client)<br>2. Mở email | hộp thư worker/client | 1. **Nhận được email** thông báo sau khi status đổi sang "go to site" (người nhận + tiêu đề + nội dung + file PDF) | High |

---

> **Part 6 = 17 TC** (NSITE 2 · NSKILL 3 · NAV 2 · E2E 10 — gồm 4 case post-submission cross-system).

---

## Tổng kết Coverage

| Module | TC | Ghi chú |
|---|---|---|
| PREFIX | 3 | Chọn sector → routing 2 luồng |
| WPS | 10 | Payment status + rẽ nhánh provider (3 nhánh) |
| WELCOME | 5 | Welcome theo provider |
| DEF | 2 | DN-04 (bug provider Limited) |
| PICIS | 27 | Personal info CIS (required/format/error/read-only) |
| LTDCO | 15 | Company info Limited (one-of, format, MD→position, VAT) |
| PILTD | 18 | Personal info Limited (VAT=No/Yes, Company pre-fill) |
| PIUMB | 10 | Personal info Umbrella (= CIS − UTR) |
| HQ | 12 | Health Questionnaire (Decision Table + State Transition) |
| DECL | 7 | Declarations (checkbox + 3 link browser) |
| SKILL | 4 | Skill card + submit popup |
| DOC | 10 | Component upload tài liệu |
| NSITE | 2 | Normal: location/subcontractor |
| NSKILL | 3 | Normal: Skill card + submit |
| NAV | 2 | Back/reset (BR-39) |
| E2E | 10 | 5 nhánh KRS + normal (6) + post-submission admin/dashboard/email (4) |
| **Tổng** | **140** | Critical ~12 · High ~74 · Medium ~48 · Low ~6 |

> **Traceability:** mọi BR-01→39 + BR-N01→N03 + VAL-01→18 + VAL-N01→N03 đều có TC phủ. Ngoài scope (không sinh TC theo QA): AMB-N01/N02, Q8. Bug DN-04 → module DEF. Reset-on-back (BR-39) → module NAV. Format Excel: tương thích `scripts/export_testcases_to_excel.ps1`, **không cột Figma Ref**.
