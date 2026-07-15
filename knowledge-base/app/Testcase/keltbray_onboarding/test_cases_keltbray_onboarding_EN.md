# PPAC Mobile — Keltbray Worker Onboarding (Test Cases) — EN

> **System:** PPAC Mobile — Worker Onboarding
> **Module:** Keltbray (`keltbray`) · **2 sectors:** KRS (induction) + Keltbray (normal)
> **Requirements source:** [[requirements_keltbray_onboarding]]
> **Method:** FULL RBT (AI-RBT 6 steps) — EP · BVA · Decision Table · State Transition · UI-state checks
> **Created:** 2026-06-22 · **QA Owner:** harry.vo@ppac.co.uk
> **Total TC:** 140 (16 modules / 6 Parts) — Critical ~12 · High ~74 · Medium ~48 · Low ~6.
> **End-of-journey expectation (both sectors):** popup **"Your check has been submitted!"** = successful submission.

---

## Scope

**In scope:** 2 sectors (KRS induction: CIS/Limited/Umbrella + Keltbray normal), up to the Skill card verification step → successful submission.
**Out of scope (no TC generated — per QA):** required-status Select site location/subcontractor (AMB-N01), behavior of the "I can't find my subcontractor" checkbox (AMB-N02), whether "Not Applicable" can be submitted (Q8). Pre-filled (Surname/Forenames/Email/DOB/Citizenship) only verify display, do not re-enter. Step details after selecting Skill card type (next round).

## Test Data Conventions

- Traceable: `auto_kelt_<module>_<TCnum>_20260622`.
- Pre-filled Email (out of scope to enter); when needed: `auto_kelt_<TCnum>_20260622@yopmail.com`.
- **NIN** valid `AH123456L` (2 letters + 6 numbers + 1 letter); wrong format `MIDORI`, missing a number `AH12345L`.
- **Phone** valid `07700900111` (11 digits); wrong `8272930` (<11 digits).
- **UTR** valid `1234567890` / `1234567890K`; wrong `12345`.
- **Bank account** valid `12345678` (8 digits); wrong `1234567` (7 digits).
- **Company Registration Number** valid `12345678` / `SC12345678`; wrong `CHCJMX`.
- Each TC is independent; reset onboarding in Pre-Condition (note BR-39: back/exit → reset).

---

## Test Cases — Part 1

### PREFIX — Prefix / Select Sector (M01)

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_PREFIX_TC_001 | PREFIX | Medium | Enter prefix keltbray → display 2 sectors | Worker opens onboarding link, company prefix entry screen | 1. Type "keltbray" into the company prefix field<br>2. Observe the suggestion list | prefix = keltbray | 1. Suggestions display **"Keltbray - KRS"** and **"Keltbray - Keltbray"** | Medium |
| PPAC_KELT_PREFIX_TC_002 | PREFIX | High | Select "Keltbray - KRS" → enter induction flow | Typed "keltbray", suggestions shown | 1. Select **"Keltbray - KRS"** | select KRS | 1. Navigate to the **Worker Payment Status** screen (induction flow) | High |
| PPAC_KELT_PREFIX_TC_003 | PREFIX | High | Select "Keltbray - Keltbray" → enter normal flow | Typed "keltbray", suggestions shown | 1. Select **"Keltbray - Keltbray"**<br>2. Tap "Continue →" | select Keltbray | 1. Navigate to the **Select site location** screen (normal flow, N02) | High |


### WPS — Worker Payment Status (M02)

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_WPS_TC_001 | WPS | High | Do not select Worker Payment Status → required error | Entered KRS, Worker Payment Status screen, dropdown empty | 1. Do not select anything in the dropdown<br>2. Try to continue | (none) | 1. Field has red border + red error **"Please select here"**; cannot continue | High |
| PPAC_KELT_WPS_TC_002 | WPS | Medium | Sheet displays exactly 3 options (single-select) | Worker Payment Status screen | 1. Open the dropdown<br>2. Observe the "Worker Payment Status" sheet | N/A | 1. Sheet has exactly 3 options: **CIS · Limited · Umbrella**; selecting 1 → marked with green CheckCircle (single-select) | Medium |
| PPAC_KELT_WPS_TC_003 | WPS | High | Select CIS → show provider radio (Riddingtons/Industrial Labour) | Worker Payment Status screen | 1. Select **CIS**<br>2. Observe below the dropdown | CIS | 1. Dropdown collapses to "CIS"; shows **provider radio group**: Riddingtons + Industrial Labour; "Next →" button | High |
| PPAC_KELT_WPS_TC_004 | WPS | High | CIS + no provider selected → block Next | CIS selected, no provider selected | 1. Do not select a provider<br>2. Tap "Next →" | CIS, provider = (none) | 1. Does not navigate (BR-03: provider required when CIS) | High |
| PPAC_KELT_WPS_TC_005 | WPS | High | CIS + Riddingtons → Next → Welcome Riddingtons | CIS selected | 1. Select provider **Riddingtons**<br>2. Tap "Next →" | CIS + Riddingtons | 1. Navigate to Welcome "Welcome to Riddingtons payroll services." | High |
| PPAC_KELT_WPS_TC_006 | WPS | High | CIS + Industrial Labour → Next → Welcome Industrial Labour | CIS selected | 1. Select provider **Industrial Labour**<br>2. Tap "Next →" | CIS + Industrial Labour | 1. Navigate to Welcome "Welcome to Industrial Labour LTD payroll services." | High |
| PPAC_KELT_WPS_TC_007 | WPS | High | Select Limited → show provider text + "Kindly provide one of the following" section | Worker Payment Status screen | 1. Select **Limited**<br>2. Observe below the dropdown | Limited | 1. Shows provider text line (fixed) + "Kindly provide one of the following:" section with 2 buttons **Company Registration Number →** / **Company Trading Name →**<br>displays "Industrial Labour"  | High |
| PPAC_KELT_WPS_TC_008 | WPS | High | Select Umbrella → provider radio Riddingtons (1 option) → Next → Welcome | Worker Payment Status screen | 1. Select **Umbrella**<br>2. Select radio **Riddingtons**<br>3. Tap "Next →" | Umbrella + Riddingtons | 1. Shows radio "Riddingtons" (1 option, selected); Next → Welcome "Welcome to Riddingtons payroll services." | High |
| PPAC_KELT_WPS_TC_009 | WPS | Medium | Change selection CIS → Limited → hide provider radio, show company section (State Transition) | CIS selected (provider radio shown) | 1. Change dropdown to **Limited**<br>2. Observe | CIS → Limited | 1. Provider radio (Riddingtons/Industrial Labour) hidden; shows the "Kindly provide one of the following" section for Limited | Medium |
| PPAC_KELT_WPS_TC_010 | WPS | Low | Header displays correctly | Worker Payment Status screen | 1. Observe the header | N/A | 1. Has **keltbray** logo, Back button (`<`); green title "Worker Payment Status" | Low |

### WELCOME — Welcome screen by provider (M03 / M03·UMB)

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_WELCOME_TC_001 | WELCOME | Low | Welcome Riddingtons (CIS/Umbrella) displays correct logo + heading | Selected provider Riddingtons → Welcome screen | 1. Observe logo + heading | N/A | 1. Logo "Riddingtons Payroll"; green heading **"Welcome to Riddingtons payroll services."** | Low |
| PPAC_KELT_WELCOME_TC_002 | WELCOME | Low | Welcome Industrial Labour (CIS) displays correct logo + heading | Selected CIS + Industrial Labour → Welcome screen | 1. Observe logo + heading | N/A | 1. Logo "IL — INDUSTRIAL LABOUR LTD"; heading **"Welcome to Industrial Labour LTD payroll services."** | Low |
| PPAC_KELT_WELCOME_TC_003 | WELCOME | Medium | Welcome body verbatim (every provider) | Welcome screen (any provider) | 1. Read the body | N/A | 1. Body correct: "Please complete your full personal details in line with our client's requirements. You will be contacted by us directly to complete the contract services." + "Thank you." | Medium |
| PPAC_KELT_WELCOME_TC_004 | WELCOME | Medium | Next → Personal information | Welcome screen | 1. Tap "Next →" | N/A | 1. Navigate to the Personal information form (Onboarding details tab) | Medium |
| PPAC_KELT_WELCOME_TC_005 | WELCOME | Low | Welcome Umbrella = Riddingtons | Selected Umbrella + Riddingtons → Welcome | 1. Observe the heading | N/A | 1. Heading "Welcome to Riddingtons payroll services." (Umbrella uses Riddingtons) | Low |

### DEF — Regression / Defects (DN-04)

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_DEF_TC_001 | DEF | High | 🐞 DN-04 — provider text at WPS Limited must be Industrial Labour | Worker Payment Status screen, selected **Limited** | 1. Observe the provider text line below the Limited dropdown | N/A | Displays provider = **"Industrial Labour"** (currently displaying WRONG "Payroll Provider is Riddingtons" → bug DN-04) | High |
| PPAC_KELT_DEF_TC_002 | DEF | Medium | Limited: Welcome = Industrial Labour LTD (actual provider) | Selected Limited → through company info → Welcome screen | 1. Observe the Welcome heading | N/A | 1. Heading "Welcome to Industrial Labour LTD payroll services." → confirms the actual provider of Limited = Industrial Labour (cross-check DEF_TC_001) | Medium |

---

> **Part 1 = 20 TC** (PREFIX 3 · WPS 10 · WELCOME 5 · DEF 2).

---

## Test Cases — Part 2

### PICIS — Personal information, CIS branch (M04)

> Common Pre-Condition: entered onboarding **CIS** (via provider → Welcome), **Personal information** screen (Onboarding details tab); **Surname/Forenames/Email/DOB/Citizenship** pre-filled from profile. Stepper 3 tabs: Onboarding details (active) · Health Questionnaire · Declarations.

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_PICIS_TC_001 | PICIS | Critical | Enter all valid fields → Next to Health Questionnaire | Personal information screen (CIS); pre-filled values available | 1. Upload passport<br>2. Enter Address, City, Postcode<br>3. Enter Candidate's Mobile Phone, NIN<br>4. Select Contract/Job start date, enter Next of Kin + Tel, select Trade<br>5. Enter UTR, Name of bank, Bank account, Sort code<br>6. Tap "Next →" | Phone=07700900111; NIN=AH123456L; Contract=01/01/2026; Next of Kin=John Smith; Tel=07700900222; UTR=1234567890; Bank acc=12345678; Sort=12-34-56; Roll=(empty) | 1. All fields accept values, no errors<br>2. Navigate to the **Health Questionnaire** tab | Critical |
| PPAC_KELT_PICIS_TC_002 | PICIS | High | Your passport left empty → required error, block Next | Personal information screen, passport not uploaded | 1. Leave "Your passport" empty<br>2. Fill other fields validly<br>3. Tap "Next →" | passport=(none) | 1. Error **"Please upload your passport"**; does not navigate | High |
| PPAC_KELT_PICIS_TC_003 | PICIS | High | Tap "Upload your passport" → navigate to Regula screen | Personal information screen | 1. Tap "+ Upload your passport" | N/A | 1. Navigate to the **Regula** screen (scan/verify passport) — DIFFERENT from the "Pick your source of document" component; helper "Accepted file types: png, jpg, jpeg, doc, docx, pdf." | High |
| PPAC_KELT_PICIS_TC_004 | PICIS | Medium | Pre-filled values displayed from profile | Personal information screen | 1. Observe Surname, Forenames, Email, Date of Birth, Citizenship | N/A | 1. 5 fields display pre-filled values (e.g. Surname/Forenames "Maya", DOB 28/05/2001, Citizenship "Vietnamese") — no need to re-enter | Medium |
| PPAC_KELT_PICIS_TC_005 | PICIS | High | Pre-filled fields CANNOT be edited/deleted (read-only) | Personal information screen; Surname/Forenames/Email/DOB/Citizenship pre-filled from profile | 1. Try to edit and delete each field in turn: Surname, Forenames, Email, Date of Birth, Citizenship | N/A | 1. **Cannot edit/delete** — all 5 pre-filled fields are read-only; values remain from profile | High |
| PPAC_KELT_PICIS_TC_006 | PICIS | High | Address left empty → required error | Personal information screen | 1. Leave Address empty<br>2. Tap "Next →" | Address=(empty) | 1. Display required error at Address; does not navigate | High |
| PPAC_KELT_PICIS_TC_007 | PICIS | Medium | City / Postcode left empty → required error | Personal information screen | 1. Leave City and Postcode empty<br>2. Tap "Next →" | City=(empty); Postcode=(empty) | 1. Each field (City, Postcode) displays a required error; does not navigate | Medium |
| PPAC_KELT_PICIS_TC_008 | PICIS | High | Candidate's Mobile Phone < 11 digits → error (BVA) | Personal information screen | 1. Enter Candidate's Mobile Phone < 11 digits<br>2. Tap "Next →" | Phone=8272930 (7 digits) | 1. Display **"either your phone number is invalid or use at least 11 digits"**; does not navigate | High |
| PPAC_KELT_PICIS_TC_009 | PICIS | High | Candidate's Mobile Phone left empty → required error | Personal information screen | 1. Leave Candidate's Mobile Phone empty<br>2. Tap "Next →" | Phone=(empty) | 1. Display required error at Candidate's Mobile Phone; does not navigate | High |
| PPAC_KELT_PICIS_TC_010 | PICIS | Medium | Candidate's Mobile Phone = 11 digits → accepted (BVA boundary) | Personal information screen | 1. Enter Phone with exactly 11 digits<br>2. Leave focus | Phone=07700900111 (11 digits) | 1. No phone error; field accepts the value | Medium |
| PPAC_KELT_PICIS_TC_011 | PICIS | High | NIN wrong format → verbatim format error | Personal information screen | 1. Enter NIN with wrong format<br>2. Tap "Next →" | NIN=MIDORI | 1. Display **"Please enter the right format: 2 letters, 6 numbers, 1 letter (e.g. AA999999A)"**; does not navigate | High |
| PPAC_KELT_PICIS_TC_012 | PICIS | Medium | NIN missing 1 number (boundary) → format error (BVA) | Personal information screen | 1. Enter NIN missing 1 number (2 letters + 5 numbers + 1 letter)<br>2. Tap "Next →" | NIN=AH12345L | 1. Display NIN format error | Medium |
| PPAC_KELT_PICIS_TC_013 | PICIS | High | NIN correct format (2 letters + 6 numbers + 1 letter) → accepted | Personal information screen | 1. Enter NIN with correct format<br>2. Leave focus | NIN=AH123456L | 1. No NIN error; field accepts the value | High |
| PPAC_KELT_PICIS_TC_014 | PICIS | Medium | NIN left empty → required error | Personal information screen | 1. Leave NIN empty<br>2. Tap "Next →" | NIN=(empty) | 1. Display required error at NIN; does not navigate | Medium |
| PPAC_KELT_PICIS_TC_015 | PICIS | Medium | Contract/Job start date left empty → error "Please enter here" | Personal information screen | 1. Leave Contract/Job start date empty<br>2. Tap "Next →" | Contract=(empty) | 1. Display **"Please enter here"** at Contract/Job start date; does not navigate | Medium |
| PPAC_KELT_PICIS_TC_016 | PICIS | Medium | Next of Kin left empty → required error | Personal information screen | 1. Leave Next of Kin empty<br>2. Tap "Next →" | Next of Kin=(empty) | 1. Display required error at Next of Kin; does not navigate | Medium |
| PPAC_KELT_PICIS_TC_017 | PICIS | High | Tel (Next of Kin) wrong format → error | Personal information screen | 1. Enter an invalid Tel (Next of Kin)<br>2. Tap "Next →" | Tel=8272930 | 1. Display **"Please enter a valid phone number"**; does not navigate | High |
| PPAC_KELT_PICIS_TC_018 | PICIS | Medium | Trade not selected → required error | Personal information screen | 1. Leave Trade empty<br>2. Tap "Next →" | Trade=(none) | 1. Display required error at Trade; does not navigate | Medium |
| PPAC_KELT_PICIS_TC_020 | PICIS | Medium | UTR correct format → accepted (EP) | Personal information screen | 1. Enter UTR with correct format<br>2. Leave focus | UTR=1234567890K | 1. No UTR error; field accepts the value | Medium |
| PPAC_KELT_PICIS_TC_021 | PICIS | High | UTR wrong format → verbatim format error | Personal information screen | 1. Enter UTR with wrong format<br>2. Tap "Next →" | UTR=12345 | 1. Display **"The UTR number can only be of the form - 1234567890 or 1234567890K"**; does not navigate | High |
| PPAC_KELT_PICIS_TC_022 | PICIS | High | Bank account number = 8 digits → accepted | Personal information screen | 1. Enter Bank account with exactly 8 digits<br>2. Leave focus | Bank acc=12345678 | 1. No error; field accepts the value | High |
| PPAC_KELT_PICIS_TC_023 | PICIS | High | Bank account ≠ 8 digits (boundary 7 digits) → format error | Personal information screen | 1. Enter Bank account with 7 digits<br>2. Tap "Next →" | Bank acc=1234567 | 1. Display **"the bank account number can only be of the form - 12345678 or 8 digits ex.12345678"**; does not navigate | High |
| PPAC_KELT_PICIS_TC_024 | PICIS | Medium | Name of bank left empty → error "Please enter here" | Personal information screen | 1. Leave Name of bank empty<br>2. Tap "Next →" | Name of bank=(empty) | 1. Display **"Please enter here"** at Name of bank; does not navigate | Medium |
| PPAC_KELT_PICIS_TC_025 | PICIS | Medium | Sort code left empty → error "Please enter here" | Personal information screen | 1. Leave Sort code empty<br>2. Tap "Next →" | Sort code=(empty) | 1. Display **"Please enter here"** at Sort code; does not navigate | Medium |
| PPAC_KELT_PICIS_TC_026 | PICIS | Medium | Roll number left empty still allows Next (OPTIONAL) | Personal information screen, required fields valid, Roll number empty | 1. Leave Roll number (if applicable) empty<br>2. Tap "Next →" | Roll number=(empty) | 1. NO error at Roll number; navigate to Health Questionnaire (optional field) | Medium |
| PPAC_KELT_PICIS_TC_027 | PICIS | Medium | Has UTR Number field (CIS-specific) | Personal information screen CIS | 1. Scroll the form to find the UTR Number field | N/A | 1. **HAS** the "UTR Number" field (specific to the CIS branch — cross-check Umbrella does not have it) | Medium |
| PPAC_KELT_PICIS_TC_028 | PICIS | Medium | Multiple required fields empty at once → each field reports an error | Personal information screen empty (except pre-filled) | 1. Leave all required fields empty<br>2. Tap "Next →" | all empty | 1. Each required field displays its corresponding error; does not navigate | Medium |

---

> **Part 2 = 27 TC** (PICIS; TC_019 dropped, ID gap kept).

---

## Test Cases — Part 3

### LTDCO — Company info, Limited branch (M-LTD-01 / M-LTD-02)

> Common Pre-Condition: selected **Limited** in Worker Payment Status (provider = Industrial Labour — WPS text currently displaying wrong "Riddingtons", see DEF); screen displays the **"Kindly provide one of the following:"** section with 2 buttons Company Registration Number / Company Trading Name. (MD + VAT cards are the same between M-LTD-01 and M-LTD-02.)

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_LTDCO_TC_001 | LTDCO | High | Must select 1 of 2 (one-of) | "Kindly provide one of the following" screen | 1. Do not select Company Registration Number / Company Trading Name<br>2. Try to continue | (none) | 1. Cannot continue — must select 1 of 2 (one-of) | High |
| PPAC_KELT_LTDCO_TC_002 | LTDCO | Medium | Select "Company Registration Number" → M-LTD-01 screen | "Kindly provide one of the following" screen | 1. Tap **"Company Registration Number →"** | N/A | 1. Navigate to the screen titled **"Company Registration Number"** (M-LTD-01) | Medium |
| PPAC_KELT_LTDCO_TC_003 | LTDCO | Medium | Select "Company Trading Name" → M-LTD-02 screen | "Kindly provide one of the following" screen | 1. Tap **"Company Trading Name →"** | N/A | 1. Navigate to the screen titled **"Company Trading Name"**  | Medium |
| PPAC_KELT_LTDCO_TC_004 | LTDCO | High | M-LTD-01: Company Registration Number left empty → error | Company Registration Number screen (M-LTD-01) | 1. Leave the Company Registration Number field empty<br>2. Tap "Next →" | (empty) | 1. Display **"Please enter here"**; does not navigate | High |
| PPAC_KELT_LTDCO_TC_005 | LTDCO | High | M-LTD-01: Reg Number wrong format → verbatim error | Company Registration Number screen | 1. Enter wrong format<br>2. Tap "Next →" | CHCJMX | 1. Display **"The Company Registration Number can only be of the form – 12345678 or two letters followed by 8 digits ex. SC12345678"** | High |
| PPAC_KELT_LTDCO_TC_006 | LTDCO | High | M-LTD-01: Reg Number = 8 digits → accepted (BVA/EP) | Company Registration Number screen | 1. Enter 8 digits<br>2. Leave focus | 12345678 | 1. No error; field accepts the value | High |
| PPAC_KELT_LTDCO_TC_007 | LTDCO | Medium | M-LTD-01: Reg Number = 2 letters + 8 digits → accepted (EP) | Company Registration Number screen | 1. Enter 2 letters + 8 digits<br>2. Leave focus | SC12345678 | 1. No error; field accepts the value | Medium |
| PPAC_KELT_LTDCO_TC_008 | LTDCO | High | "Are you a Managing Director?" not selected → error | Company-info screen (M-LTD-01 or M-LTD-02) | 1. Do not select Yes/No at "Are you a Managing Director?"<br>2. Tap "Next →" | (none) | 1. Display **"Please select here"**; does not navigate | High |
| PPAC_KELT_LTDCO_TC_009 | LTDCO | High | MD = No → show "Please enter your position" field (State Transition) | Company-info screen | 1. Select **No** at "Are you a Managing Director?"<br>2. Observe | MD=No | 1. Show the **"Please enter your position"** field (placeholder "Please enter here") | High |
| PPAC_KELT_LTDCO_TC_010 | LTDCO | High | MD = No + position left empty → error | Company-info screen, MD=No (position field shown) | 1. Leave "Please enter your position" empty<br>2. Tap "Next →" | position=(empty) | 1. Display **"Please enter here"** at the position field; does not navigate | High |
| PPAC_KELT_LTDCO_TC_011 | LTDCO | Medium | MD = Yes → hide position field (State Transition) | Company-info screen, MD currently = No (position field shown) | 1. Change to **Yes**<br>2. Observe | MD=Yes | 1. "Please enter your position" field **hidden**; position not required | Medium |
| PPAC_KELT_LTDCO_TC_012 | LTDCO | High | "Are you VAT Registered?" not selected → error | Company-info screen | 1. Do not select Yes/No at "Are you VAT Registered?"<br>2. Tap "Next →" | VAT=(none) | 1. Display **"Please select here"**; does not navigate | High |
| PPAC_KELT_LTDCO_TC_013 | LTDCO | High | M-LTD-02: Company Trading Name left empty → error | Company Registration Name screen (M-LTD-02) | 1. Leave Company Trading Name empty<br>2. Tap "Next →" | (empty) | 1. Display **"Please enter here"**; does not navigate | High |
| PPAC_KELT_LTDCO_TC_014 | LTDCO | High | M-LTD-02: Trading Name accepts any text → accepted (NO format validation) | Company Registration Name screen | 1. Enter any non-empty text<br>2. Leave focus | ABC Builders Ltd | 1. No error; field accepts **any text** (does not validate reg number format) | High |
| PPAC_KELT_LTDCO_TC_015 | LTDCO | High | Complete company-info (VAT=No) → Next → Welcome Industrial Labour LTD | Company-info screen, entered Reg Number/Trading Name + MD + VAT | 1. Enter all valid fields, select VAT Registered = No<br>2. Tap "Next →" | Reg=12345678; MD=Yes; VAT=No | 1. Navigate to Welcome **"Welcome to Industrial Labour LTD payroll services."** → Personal information Limited version | High |

### PILTD — Personal information, Limited branch (M04·LTD)

> Common Pre-Condition: completed company-info + Welcome (Industrial Labour LTD); **Personal information** Limited version screen. Pre-filled = **Surname/Forenames/Email** (read-only). Variant by "Are you VAT Registered?": **No** → no VAT field; **Yes** → adds VAT Number + Your VAT Certificate.

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_PILTD_TC_001 | PILTD | Critical | Happy path VAT=No → Next to Health Questionnaire | Entered PILTD, VAT Registered = No (no VAT field) | 1. Upload passport<br>2. Enter Address/City/Postcode, Phone<br>3. Enter UTR, Company trading name, Company registration number, Name of bank, Bank account, Sort code, select Trade<br>4. Tap "Next →" | Phone=07700900111; UTR=1234567890; Bank acc=12345678; Sort=12-34-56; Roll=(empty) | 1. All fields valid, no errors<br>2. Navigate to the **Health Questionnaire** tab | Critical |
| PPAC_KELT_PILTD_TC_002 | PILTD | High | Your passport left empty → "Please upload your passport" | PILTD screen, passport not uploaded | 1. Leave passport empty<br>2. Tap "Next →" | passport=(none) | 1. Display **"Please upload your passport"**; does not navigate | High |
| PPAC_KELT_PILTD_TC_003 | PILTD | High | Pre-filled fields (Surname/Forenames/Email) read-only | PILTD screen | 1. Try to edit / delete Surname, Forenames, Email | N/A | 1. **Cannot edit/delete** — 3 read-only fields, kept from profile | High |
| PPAC_KELT_PILTD_TC_004 | PILTD | Medium | Different from CIS — NO NIN/DOB/Citizenship/Contract start/Next of Kin/Tel | PILTD screen | 1. Scroll the entire form, look for the NIN, Date of Birth, Citizenship, Contract/Job start date, Next of Kin, Tel (Next of Kin) fields | N/A | 1. **No** 6 of these fields in the Limited version (different from CIS) | Medium |
| PPAC_KELT_PILTD_TC_005 | PILTD | Medium | Has Company trading name + Company registration number fields | PILTD screen | 1. Scroll the form to find the 2 Company fields | N/A | 1. **HAS** "Company trading name" and "Company registration number" (Limited-specific) | Medium |
| PPAC_KELT_PILTD_TC_006 | PILTD | Medium | Company trading name pre-filled from M-LTD-02 | Selected Company Trading Name path in company-info, entered "ABC Builders Ltd" | 1. Enter PILTD, observe the "Company trading name" field | entered "ABC Builders Ltd" at M-LTD-02 | 1. The "Company trading name" field is **pre-filled** = "ABC Builders Ltd" (pre-fill from M-LTD-02) | Medium |
| PPAC_KELT_PILTD_TC_007 | PILTD | Medium | Company registration number pre-filled from M-LTD-01 | Selected Company Registration Number path in company-info, entered "SC12345678" | 1. Enter PILTD, observe the "Company registration number" field | entered "SC12345678" at M-LTD-01 | 1. The "Company registration number" field is **pre-filled** = "SC12345678" (pre-fill from M-LTD-01) | Medium |
| PPAC_KELT_PILTD_TC_008 | PILTD | High | VAT Registered = Yes → show VAT Number + Your VAT Certificate (Decision Table) | Selected VAT Registered = **Yes** in company-info | 1. Enter PILTD<br>2. Observe the area after UTR Number | VAT=Yes | 1. Additionally shows **VAT Number** + **Your VAT Certificate** (upload) right after UTR Number | High |
| PPAC_KELT_PILTD_TC_009 | PILTD | Medium | VAT Registered = No → NO VAT Number/Certificate | Selected VAT Registered = **No** in company-info | 1. Enter PILTD<br>2. Observe the area after UTR Number | VAT=No | 1. **No** VAT Number / Your VAT Certificate field | Medium |
| PPAC_KELT_PILTD_TC_010 | PILTD | High | VAT=Yes — upload Your VAT Certificate via component | PILTD screen, VAT=Yes | 1. Tap "+ Upload document" at Your VAT Certificate<br>2. Select source → capture/upload → Okay | VAT Certificate document | 1. Opens the "Pick your source of document" component → upload successful → card "VAT Certificate Document" (✓) displayed in the field | High |
| PPAC_KELT_PILTD_TC_011 | PILTD | Medium | Candidate's Mobile Phone < 11 digits → error (BVA) | PILTD screen | 1. Enter Phone < 11 digits<br>2. Tap "Next →" | Phone=8272930 | 1. Display **"either your phone number is invalid or use at least 11 digits"**; does not navigate | Medium |
| PPAC_KELT_PILTD_TC_012 | PILTD | Medium | UTR wrong format → verbatim error | PILTD screen | 1. Enter UTR with wrong format<br>2. Tap "Next →" | UTR=12345 | 1. Display **"The UTR number can only be of the form - 1234567890 or 1234567890K"** | Medium |
| PPAC_KELT_PILTD_TC_013 | PILTD | Medium | Bank account ≠ 8 digits (boundary 7 digits) → error | PILTD screen | 1. Enter Bank account with 7 digits<br>2. Tap "Next →" | Bank acc=1234567 | 1. Display **"the bank account number can only be of the form - 12345678 or 8 digits ex.12345678"** | Medium |
| PPAC_KELT_PILTD_TC_014 | PILTD | Medium | Name of bank / Sort code left empty → "Please enter here" | PILTD screen | 1. Leave Name of bank and Sort code empty<br>2. Tap "Next →" | (empty) | 1. Each field displays **"Please enter here"**; does not navigate | Medium |
| PPAC_KELT_PILTD_TC_015 | PILTD | Medium | Address / City / Postcode left empty → required error | PILTD screen | 1. Leave Address, City, Postcode empty<br>2. Tap "Next →" | (empty) | 1. Each field displays a required error; does not navigate | Medium |
| PPAC_KELT_PILTD_TC_016 | PILTD | Medium | Trade not selected → required error | PILTD screen | 1. Leave Trade empty<br>2. Tap "Next →" | Trade=(none) | 1. Display required error at Trade; does not navigate | Medium |
| PPAC_KELT_PILTD_TC_017 | PILTD | Medium | Roll number left empty still allows Next (OPTIONAL) | PILTD screen, required fields valid, Roll number empty | 1. Leave Roll number empty<br>2. Tap "Next →" | Roll=(empty) | 1. NO Roll number error; navigate to Health Questionnaire | Medium |
| PPAC_KELT_PILTD_TC_018 | PILTD | High | Happy path VAT=Yes (VAT cert uploaded) → Next to Health Questionnaire | PILTD screen, VAT=Yes, VAT Certificate uploaded | 1. Fill all valid fields + VAT Number<br>2. Tap "Next →" | VAT Number=GB123456789; all fields valid | 1. No error; navigate to the **Health Questionnaire** tab | High |

---

> **Part 3 = 33 TC** (LTDCO 15 · PILTD 18).

---

## Test Cases — Part 4

### PIUMB — Personal information, Umbrella branch (M04·UMB)

> Common Pre-Condition: selected **Umbrella** → provider Riddingtons (radio 1 option) → Welcome (Riddingtons); **Personal information** Umbrella version screen. **= SAME AS CIS but DROPS the UTR Number field.** Pre-filled = Surname/Forenames/Email/DOB/Citizenship (read-only). Field validations same as PICIS — only representative tests.

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_PIUMB_TC_001 | PIUMB | Critical | Happy path → Next to Health Questionnaire | Personal information Umbrella version screen | 1. Upload passport<br>2. Enter Address/City/Postcode, Phone, NIN<br>3. Select Contract start date, Next of Kin + Tel, Trade<br>4. Enter Name of bank, Bank account, Sort code<br>5. Tap "Next →" | Phone=07700900111; NIN=AH123456L; Bank acc=12345678; Sort=12-34-56 | 1. All fields valid, no errors<br>2. Navigate to the **Health Questionnaire** tab | Critical |
| PPAC_KELT_PIUMB_TC_002 | PIUMB | High | Your passport left empty → "Please upload your passport" | PIUMB screen, passport not uploaded | 1. Leave passport empty<br>2. Tap "Next →" | passport=(none) | 1. Display **"Please upload your passport"**; does not navigate | High |
| PPAC_KELT_PIUMB_TC_003 | PIUMB | High | Pre-filled fields (all 5) read-only | PIUMB screen | 1. Try to edit / delete Surname, Forenames, Email, Date of Birth, Citizenship | N/A | 1. **Cannot edit/delete** — all 5 fields read-only | High |
| PPAC_KELT_PIUMB_TC_004 | PIUMB | Medium | NO UTR Number field (different from CIS) | PIUMB screen | 1. Scroll the entire form to find the "UTR Number" field | N/A | 1. **No** UTR Number field (characteristic of Umbrella — different from CIS which has UTR) | Medium |
| PPAC_KELT_PIUMB_TC_005 | PIUMB | Medium | NO VAT / Company fields | PIUMB screen | 1. Look for VAT Number, Your VAT Certificate, Company trading name, Company registration number fields | N/A | 1. **No** VAT/Company fields (only Limited has them) | Medium |
| PPAC_KELT_PIUMB_TC_006 | PIUMB | Medium | HAS NIN/DOB/Citizenship/Contract/Next of Kin/Tel (same as CIS) | PIUMB screen | 1. Scroll the form to confirm these fields are present | N/A | 1. **HAS** National Insurance Number, Date of Birth, Citizenship, Contract/Job start date, Next of Kin, Tel (Next of Kin) — same as CIS | Medium |
| PPAC_KELT_PIUMB_TC_007 | PIUMB | High | NIN wrong format → verbatim error (representative) | PIUMB screen | 1. Enter wrong NIN<br>2. Tap "Next →" | NIN=MIDORI | 1. Display **"Please enter the right format: 2 letters, 6 numbers, 1 letter (e.g. AA999999A)"** | High |
| PPAC_KELT_PIUMB_TC_008 | PIUMB | Medium | Candidate's Mobile Phone < 11 digits → error (representative) | PIUMB screen | 1. Enter Phone < 11 digits<br>2. Tap "Next →" | Phone=8272930 | 1. Display **"either your phone number is invalid or use at least 11 digits"** | Medium |
| PPAC_KELT_PIUMB_TC_009 | PIUMB | Medium | Bank account ≠ 8 digits → error (representative) | PIUMB screen | 1. Enter Bank account with 7 digits<br>2. Tap "Next →" | Bank acc=1234567 | 1. Display **"the bank account number can only be of the form - 12345678 or 8 digits ex.12345678"** | Medium |
| PPAC_KELT_PIUMB_TC_010 | PIUMB | Medium | Name of bank / Sort code left empty → "Please enter here" | PIUMB screen | 1. Leave Name of bank and Sort code empty<br>2. Tap "Next →" | (empty) | 1. Each field displays **"Please enter here"** | Medium |

### HQ — Health Questionnaire (M05) — *(applies to CIS / Limited / Umbrella)*

> Common Pre-Condition: completed Personal information, on the **Health Questionnaire** tab screen. 10 questions (Q1–8, 8.1, 8.2), each question a **required Yes/No** radio. Verbatim content of the 10 questions see requirements §4.5.

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_HQ_TC_001 | HQ | Critical | Answer all 10 questions (mix) + valid details → Next to Declarations | Health Questionnaire screen | 1. Answer 10 questions (some Yes with details entered, rest No)<br>2. Tap "Next →" | Q1=Yes details "Asthma - using inhaler"; rest No | 1. No error; navigate to the **Declarations** tab | Critical |
| PPAC_KELT_HQ_TC_002 | HQ | High | Leave ≥1 question empty → "Please answer this question" | Health Questionnaire screen | 1. Leave 1 question empty (e.g. Q2)<br>2. Answer the other questions<br>3. Tap "Next →" | Q2=(not selected) | 1. Display **"Please answer this question"** at Q2; does not navigate | High |
| PPAC_KELT_HQ_TC_003 | HQ | High | Select Yes → show "Please enter details" field (State Transition) | Health Questionnaire screen | 1. Select **Yes** on 1 question<br>2. Observe | Q1=Yes | 1. Show the **"Please enter details"** field (placeholder "Please enter here") below that question | High |
| PPAC_KELT_HQ_TC_004 | HQ | High | Yes + details left empty → "Please give us the details here" | Health Questionnaire screen, 1 question = Yes (details field shown) | 1. Leave the details field empty<br>2. Tap "Next →" | Q1=Yes, details=(empty) | 1. Display **"Please give us the details here"**; does not navigate | High |
| PPAC_KELT_HQ_TC_005 | HQ | Medium | Select No → hide details field (State Transition) | Health Questionnaire screen | 1. Select **No** on 1 question<br>2. Observe | Q1=No | 1. **Hide** the "Please enter details" field of that question | Medium |
| PPAC_KELT_HQ_TC_006 | HQ | Medium | Change Yes → No after details shown → hide field (State Transition) | Health Questionnaire screen, 1 question = Yes (details shown) | 1. Change that question to **No**<br>2. Observe | Q1: Yes→No | 1. Details field hidden (state reset) | Medium |
| PPAC_KELT_HQ_TC_007 | HQ | High | All 10 questions = No → Next to Declarations | Health Questionnaire screen | 1. Select No for all 10 questions<br>2. Tap "Next →" | all=No | 1. No details field; navigate to Declarations | High |
| PPAC_KELT_HQ_TC_008 | HQ | Medium | All 10 questions = Yes + all details filled → Next | Health Questionnaire screen | 1. Select Yes for all 10 questions<br>2. Enter details for each question<br>3. Tap "Next →" | all=Yes + details | 1. Each question shows a details field; filling all → navigate to Declarations | Medium |
| PPAC_KELT_HQ_TC_009 | HQ | Medium | All questions unanswered → each question reports an error | Health Questionnaire screen empty | 1. Do not answer any question<br>2. Tap "Next →" | all empty | 1. Each question (10 questions) displays **"Please answer this question"** | Medium |
| PPAC_KELT_HQ_TC_010 | HQ | Medium | Display all 10 questions with correct content/order + intro verbatim | Health Questionnaire screen | 1. Observe the intro + question list | N/A | 1. Intro "Details given are deemed relevant in the interest of your Health and Safety and allow the company to assess the risk to your health."<br>2. All 10 questions Q1–8, 8.1, 8.2 with correct content/order (see requirements §4.5) | Medium |
| PPAC_KELT_HQ_TC_011 | HQ | Medium | Each question Yes/No radio mutually exclusive | Health Questionnaire screen | 1. Select Yes on 1 question<br>2. Select No on the same question | Q1: Yes→No | 1. Only 1 choice active; selecting No deselects Yes | Medium |
| PPAC_KELT_HQ_TC_012 | HQ | Low | Health Questionnaire identical across CIS / Limited / Umbrella | Enter Health Questionnaire from each branch | 1. Compare the Health Questionnaire tab across the 3 branches | N/A | 1. Content + behavior of the 10 questions identical across all 3 branches (AMB-31) | Low |

---

> **Part 4 = 22 TC** (PIUMB 10 · HQ 12).

---

## Test Cases — Part 5

### DECL — Declarations (M06) — *(applies to CIS / Limited / Umbrella)*

> Common Pre-Condition: completed Health Questionnaire, on the **Declarations** tab screen. 3 policy links + 1 confirmation checkbox. Provider in link #3 + checkbox text by branch (CIS = selected provider; **Limited = Industrial Labour**; Umbrella = Riddingtons).

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_DECL_TC_001 | DECL | High | Tick checkbox + Next → Skill card verification | Declarations screen | 1. Tick the confirmation checkbox<br>2. Tap "Next →" | checkbox = ON | 1. Navigate to **Skill card verification** (M07) | High |
| PPAC_KELT_DECL_TC_002 | DECL | High | Checkbox not ticked + Next → error | Declarations screen, checkbox OFF | 1. Do not tick the checkbox<br>2. Tap "Next →" | checkbox = OFF | 1. Display red error **"Please review and confirm these declarations to continue."**; does not navigate | High |
| PPAC_KELT_DECL_TC_003 | DECL | High | Link "Keltbray Privacy Policies" → open external browser | Declarations screen | 1. Tap the link **"Keltbray Privacy Policies"** | N/A | 1. Open **external browser** to the Keltbray Privacy Policy page [URL verify during test] | High |
| PPAC_KELT_DECL_TC_004 | DECL | Medium | Link "Keltbray GDPR Privacy Policies" → open external browser | Declarations screen | 1. Tap the link **"Keltbray GDPR Privacy Policies"** | N/A | 1. Open external browser to Keltbray GDPR Privacy Policy [URL verify] | Medium |
| PPAC_KELT_DECL_TC_005 | DECL | Medium | Link "{Provider} GDPR Privacy Policies" → open external browser | Declarations screen | 1. Tap the link **"{Provider} GDPR Privacy Policies"** (e.g. "Riddingtons GDPR Privacy Policies") | N/A | 1. Open external browser to {Provider} GDPR Privacy Policy [URL verify] | Medium |
| PPAC_KELT_DECL_TC_006 | DECL | Medium | Link #3 + checkbox text change by provider | Enter Declarations from different branches | 1. CIS·Riddingtons → observe<br>2. CIS·Industrial Labour → observe<br>3. Umbrella → observe | N/A | 1. Link #3 + name in checkbox change by provider: Riddingtons / Industrial Labour ("…sent to Keltbray and {Provider} my payroll provider") | Medium |
| PPAC_KELT_DECL_TC_007 | DECL | Medium | Body + checkbox verbatim | Declarations screen | 1. Read the body + checkbox text | N/A | 1. Body "Please confirm you understand and agree with keltbray rules and policies:"<br>2. Checkbox "I can confirm that all the information is correct, and I understand my details will be sent to Keltbray and {Provider} my payroll provider" | Medium |

### SKILL — Skill card verification + Submit (M07)

> Common Pre-Condition: completed Declarations (tick + Next), on the **Skill card verification** screen.

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_SKILL_TC_001 | SKILL | High | No Skill card type selected → Continue disabled | Skill card verification screen, no type selected | 1. Observe the Continue button when no type is selected | N/A | 1. **"Continue →" button disabled** (greyed out); cannot be tapped | High |
| PPAC_KELT_SKILL_TC_002 | SKILL | High | Select Skill card type → Continue enabled | Skill card verification screen | 1. Select 1 Skill card type<br>2. Observe the Continue button | type = SIA | 1. **"Continue →" button enabled** (bold) after selection | High |
| PPAC_KELT_SKILL_TC_003 | SKILL | Critical | Select type + Continue → successful submission (popup) | Skill card verification screen, type selected | 1. Select Skill card type<br>2. Tap "Continue →" | type = SIA | 1. Display **popup "Your check has been submitted!"** = successful submission | Critical |
| PPAC_KELT_SKILL_TC_004 | SKILL | Medium | Body + label verbatim | Skill card verification screen | 1. Read the title + body + label | N/A | 1. Title "Skill card verification"; body "We need your skill card information to complete the compliance check."; label "Skill card type"; placeholder "Select skill card type" | Medium |


### DOC — Component Document Upload (Cmp-DOC) — *(e.g. Your VAT Certificate, Limited VAT=Yes)*

> Common Pre-Condition: at a document upload field (e.g. "Your VAT Certificate" — PILTD VAT=Yes). {DocType} = "VAT Certificate".

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_DOC_TC_001 | DOC | High | Tap "+ Upload document" → sheet "Pick your source of document" | Your VAT Certificate field, not uploaded | 1. Tap "+ Upload document" | N/A | 1. Open bottom sheet **"Pick your source of document"** with 2 buttons: **"Open camera"** + **"Upload from your device"** | High |
| PPAC_KELT_DOC_TC_002 | DOC | High | Open camera → "Capture your document" screen | "Pick your source of document" sheet | 1. Tap "Open camera" | N/A | 1. Open the **"Capture your document"** screen + instruction "Position your VAT Certificate Document within the frame and ensure the details are clearly readable." + capture button | High |
| PPAC_KELT_DOC_TC_003 | DOC | High | Capture → preview screen with Retake/Okay | "Capture your document" screen | 1. Tap the capture button | N/A | 1. Display the preview screen **"VAT Certificate Document"** with the captured image + 2 buttons **"Retake"** / **"Okay"** | High |
| PPAC_KELT_DOC_TC_004 | DOC | Medium | Retake → re-capture | Preview screen (after capture) | 1. Tap "Retake" | N/A | 1. Return to the camera screen to re-capture | Medium |
| PPAC_KELT_DOC_TC_005 | DOC | High | Okay → return to form, uploaded card displayed | Preview screen (after capture) | 1. Tap "Okay" | N/A | 1. Return to the form; field displays **card "VAT Certificate Document"** (green ✓ icon) + trash icon | High |
| PPAC_KELT_DOC_TC_006 | DOC | Medium | Upload from your device → open file picker | "Pick your source of document" sheet | 1. Tap "Upload from your device" | N/A | 1. Open the device's file picker (select png/jpg/jpeg/doc/docx/pdf file) | Medium |
| PPAC_KELT_DOC_TC_007 | DOC | Medium | Tap uploaded card → view + "Remove and Resubmit" | Field has uploaded card "VAT Certificate Document" | 1. Tap the card | N/A | 1. Open the **"VAT Certificate Document"** screen to view the image + **"Remove and Resubmit"** button | Medium |
| PPAC_KELT_DOC_TC_008 | DOC | Medium | "Remove and Resubmit" → delete & re-capture/upload | "VAT Certificate Document" view screen | 1. Tap "Remove and Resubmit" | N/A | 1. Delete the current document, allow re-capture/upload (return to sheet/camera) | Medium |
| PPAC_KELT_DOC_TC_009 | DOC | Medium | Tap trash → dialog "Remove VAT Certificate Document?" | Field has an uploaded card | 1. Tap the trash icon on the card | N/A | 1. Display dialog **"Remove VAT Certificate Document?"** with 2 buttons **"Yes, remove it!"** / **"Cancel"** | Medium |
| PPAC_KELT_DOC_TC_010 | DOC | Medium | Delete dialog: "Yes, remove it!" deletes / "Cancel" keeps | "Remove VAT Certificate Document?" dialog | 1. Tap "Yes, remove it!" (Case 1) / "Cancel" (Case 2) | N/A | 1. "Yes, remove it!" → delete card, field empty again<br>2. "Cancel" → close dialog, keep the file | Medium |

---

> **Part 5 = 21 TC** (DECL 7 · SKILL 4 · DOC 10).

---

## Test Cases — Part 6

### NSITE — Select site location & subcontractor (N02, normal flow)

> Common Pre-Condition: selected the sector **"Keltbray - Keltbray"** (normal) → Select site location screen. *(Required-status location/subcontractor + checkbox behavior = out of scope per QA — no validation TC generated.)*

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_NSITE_TC_001 | NSITE | High | Select location + subcontractor → Continue → Skill card | Select site location screen | 1. Select Select site location<br>2. Select Select subcontractor<br>3. Tap "Continue →" | site + subcontractor (any selection) | 1. Navigate to the **Skill card verification** screen (N03) | High |
| PPAC_KELT_NSITE_TC_002 | NSITE | Low | Display 2 dropdowns + checkbox + Continue | Select site location screen | 1. Observe the screen | N/A | 1. Has "Select site location", "Select subcontractor", checkbox "I can't find my subcontractor in the list", "Continue →" button, bottom nav 4 items | Low |

### NSKILL — Skill card verification (N03, normal flow)

> Common Pre-Condition: completed N02 → Skill card verification screen (normal).

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_NSKILL_TC_001 | NSKILL | High | No Skill card type selected → Continue disabled | Skill card verification screen (normal) | 1. Observe the Continue button when nothing is selected | N/A | 1. **"Continue →" button disabled** | High |
| PPAC_KELT_NSKILL_TC_002 | NSKILL | Critical | Select type + Continue → successful submission (popup) | Skill card verification screen (normal) | 1. Select Skill card type<br>2. Tap "Continue →" | type = SIA | 1. Display popup **"Your check has been submitted!"** | Critical |
| PPAC_KELT_NSKILL_TC_003 | NSKILL | Medium | Body + helper "Not Applicable" + label verbatim | Skill card verification screen (normal) | 1. Read the body, helper, label; open the dropdown | N/A | 1. Body "We need your skill card information to complete the compliance check."<br>2. Helper "If your trade/occupation does not require a skill card please scroll to Not Applicable."<br>3. Label "Skill card type:"; dropdown has option **"Not Applicable"** | Medium |

### NAV — Back / Reset & Navigation (BR-39)

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_NAV_TC_001 | NAV | High | Partial entry → Back → return → data RESET (BR-39, different from DTSource) | On a screen (e.g. Personal information), entered partial data | 1. Enter part of the data<br>2. Tap Back<br>3. Return to that screen | partial data | 1. Entered data is **RESET** (not saved) — ⚠️ DIFFERENT from DTSource (DTSource keeps data) | High |
| PPAC_KELT_NAV_TC_002 | NAV | High | Exit app mid-journey → reopen → does NOT resume | Mid onboarding journey, entered part | 1. Exit the app<br>2. Reopen onboarding | incomplete onboarding | 1. **Does NOT resume** — starts over, previous data lost (reset) | High |

### E2E — End-to-End (both sectors → successful submission)

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_E2E_TC_001 | E2E | Critical | E2E CIS · Riddingtons → successful submission | Prefix keltbray → KRS | 1. WPS=CIS, provider=Riddingtons<br>2. Welcome → Personal info (all valid fields)<br>3. Health Q (all No)<br>4. Declarations (tick)<br>5. Skill card type + Continue | NIN=AH123456L; Phone=07700900111; Bank=12345678; type=SIA | 1. Goes through the correct order, not blocked<br>2. Popup **"Your check has been submitted!"** | Critical |
| PPAC_KELT_E2E_TC_002 | E2E | Critical | E2E CIS · Industrial Labour → successful submission | Prefix keltbray → KRS | 1. WPS=CIS, provider=Industrial Labour<br>2. Welcome (Industrial Labour LTD) → Personal info<br>3. Health Q → Declarations → Skill card + Continue | (same as E2E_TC_001) | 1. Popup **"Your check has been submitted!"** | Critical |
| PPAC_KELT_E2E_TC_003 | E2E | Critical | E2E Limited (VAT=No) → successful submission | Prefix keltbray → KRS | 1. WPS=Limited → Company Registration Number (12345678) + MD=Yes + VAT=No<br>2. Welcome → Personal info Limited<br>3. Health Q → Declarations → Skill card + Continue | Reg=12345678; type=SIA | 1. Popup **"Your check has been submitted!"** | Critical |
| PPAC_KELT_E2E_TC_004 | E2E | Critical | E2E Limited (VAT=Yes, upload VAT cert) → successful submission | Prefix keltbray → KRS | 1. WPS=Limited → Company Trading Name + MD=No (position) + VAT=Yes<br>2. Personal info Limited: enter VAT Number + upload Your VAT Certificate<br>3. Health Q → Declarations → Skill card + Continue | Trading="ABC Builders Ltd"; VAT Number=GB123456789; type=SIA | 1. Popup **"Your check has been submitted!"** | Critical |
| PPAC_KELT_E2E_TC_005 | E2E | Critical | E2E Umbrella → successful submission | Prefix keltbray → KRS | 1. WPS=Umbrella, provider=Riddingtons<br>2. Welcome → Personal info Umbrella (no UTR)<br>3. Health Q → Declarations → Skill card + Continue | NIN=AH123456L; Bank=12345678; type=SIA | 1. Popup **"Your check has been submitted!"** | Critical |
| PPAC_KELT_E2E_TC_006 | E2E | Critical | E2E Normal (Keltbray-Keltbray) → successful submission | Prefix keltbray | 1. Select "Keltbray - Keltbray" → Continue<br>2. Select site location + subcontractor → Continue<br>3. Skill card type + Continue | site/subcontractor; type=SIA | 1. Goes through 3 steps; popup **"Your check has been submitted!"** | Critical |

### E2E (Post-submission) — Admin portal / Client dashboard / Email

> ⚠️ **Scope extension beyond the mobile app** (Admin portal · Client dashboard · Email). These systems are **not yet in requirements** (mobile-only) → portal details/email content marked `[verify during test]`. Common Pre-Condition: worker has successfully submitted onboarding on mobile (popup "Your check has been submitted!").

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_KELT_E2E_TC_007 | E2E | High | Admin portal — submission registered successfully | Worker just submitted on mobile (popup "Your check has been submitted!") | 1. Log in to the **Admin portal**<br>2. Find the submission of the worker who just submitted | worker = Maya (newly created submission) | 1. Submission **appears** in the Admin portal, registered successfully (initial status) | High |
| PPAC_KELT_E2E_TC_008 | E2E | High | Admin portal — change submission status to "go to site" | Submission already in the Admin portal (E2E_TC_007) | 1. Open the submission<br>2. Change status to **"go to site"**<br>3. Save | status = "go to site" | 1. Status updated successfully = **"go to site"** | High |
| PPAC_KELT_E2E_TC_009 | E2E | High | Client dashboard — display submission after status = "go to site" | Admin changed status to "go to site" (E2E_TC_008) | 1. Log in to the **Client dashboard**<br>2. Observe the submission/worker list | status = "go to site" | 1. Submission/worker **displayed** on the Client dashboard after status = "go to site"  | High |
| PPAC_KELT_E2E_TC_010 | E2E | High | Email — sent after status = "go to site" | Admin changed status to "go to site" (E2E_TC_008) | 1. Check the inbox (worker / client)<br>2. Open the email | worker/client inbox | 1. **Receive a notification email** after status changes to "go to site" (recipient + subject + content + PDF file) | High |

---

> **Part 6 = 17 TC** (NSITE 2 · NSKILL 3 · NAV 2 · E2E 10 — including 4 post-submission cross-system cases).

---

## Coverage Summary

| Module | TC | Note |
|---|---|---|
| PREFIX | 3 | Select sector → routing 2 flows |
| WPS | 10 | Payment status + provider branching (3 branches) |
| WELCOME | 5 | Welcome by provider |
| DEF | 2 | DN-04 (Limited provider bug) |
| PICIS | 27 | Personal info CIS (required/format/error/read-only) |
| LTDCO | 15 | Company info Limited (one-of, format, MD→position, VAT) |
| PILTD | 18 | Personal info Limited (VAT=No/Yes, Company pre-fill) |
| PIUMB | 10 | Personal info Umbrella (= CIS − UTR) |
| HQ | 12 | Health Questionnaire (Decision Table + State Transition) |
| DECL | 7 | Declarations (checkbox + 3 browser links) |
| SKILL | 4 | Skill card + submit popup |
| DOC | 10 | Component document upload |
| NSITE | 2 | Normal: location/subcontractor |
| NSKILL | 3 | Normal: Skill card + submit |
| NAV | 2 | Back/reset (BR-39) |
| E2E | 10 | 5 KRS branches + normal (6) + post-submission admin/dashboard/email (4) |
| **Total** | **140** | Critical ~12 · High ~74 · Medium ~48 · Low ~6 |

> **Traceability:** every BR-01→39 + BR-N01→N03 + VAL-01→18 + VAL-N01→N03 has a covering TC. Out of scope (no TC generated per QA): AMB-N01/N02, Q8. Bug DN-04 → DEF module. Reset-on-back (BR-39) → NAV module. Excel format: compatible with `scripts/export_testcases_to_excel.ps1`, **no Figma Ref column**.
