# Requirements Document — Keltbray Worker Onboarding

> **Hệ thống:** PPAC Mobile — Worker Onboarding
> **Module:** Keltbray (`keltbray`) · **2 sector:** KRS (luồng induction) + Keltbray (luồng normal)
> **Nguồn design:** Figma (screenshot do QA cung cấp, mô tả từng màn) — verify trực tiếp trên UI thật
> **Phương pháp:** AI-RBT (FULL) — ANALYZE design từng màn; luồng KRS là journey HOÀN TOÀN MỚI (không tái dùng baseline Rail/Construction)
> **Ngày tạo:** 2026-06-22
> **QA Owner:** maya.do@ppac.co.uk
> **Trạng thái:** ✅ **Hoàn chỉnh cả 2 sector tới Skill card verification (kỳ vọng cuối = submission thành công).**
> - **Sector KRS (induction)** — §4, §4B, §4C: 3 nhánh CIS (2 provider) · Limited (Industrial Labour, company-info + VAT — WPS text "Riddingtons" là 🐞 DN-04) · Umbrella (Riddingtons, Personal info = CIS − UTR).
> - **Sector Keltbray (normal)** — §4D: 3 bước gọn (location/subcontractor → Skill card → submit).
> - ✅ **Submission thành công** = popup **"Your check has been submitted!"** (cả 2 sector). 🔜 Còn nhỏ (không chặn): Q8 "Not Applicable" có submit được không, Q9 Back/resume, URL link (khi test thật).

---

## 0. Tiến độ thu thập màn hình

| # | Màn hình | Trạng thái |
|---|---|---|
| M01 | Prefix / Select Sector | ✅ đã có |
| M02 | Worker Payment Status (CIS / Limited / Umbrella) + chọn provider | ✅ đã có |
| M03 | Welcome — nhánh CIS (Riddingtons & Industrial Labour) | ✅ đã có (cả 2 provider) |
| M04 | Personal information (tab "Onboarding details") — nhánh CIS | ✅ đã có |
| M05 | Health Questionnaire (tab 2) — 10 câu (Q1–8, 8.1, 8.2) | ✅ đã có |
| M06 | Declarations (tab 3) — 3 link + checkbox | ✅ đã có |
| M07 | Skill card verification (Next từ M06) — **🔚 giới hạn scope** | ✅ đã có |
| Cmp-DOC | Component Document Upload (source sheet → capture → preview → card → view/remove) | ✅ đã có đầy đủ |
| M02·LTD | Worker Payment Status = **Limited** (provider + "Kindly provide one of the following") | ✅ đã có |
| M-LTD-01 | Company Registration Number (nhánh Limited) | ✅ đã có |
| M-LTD-02 | Company Trading Name / "Company Registration Name" (nhánh Limited) | ✅ đã có |
| M04·LTD | Personal information — **bản Limited** (VAT=No & VAT=Yes) | ✅ đã có cả 2 biến thể |
| M05/M06 (Limited) | Health Questionnaire & Declarations — **giống CIS** | ✅ xác nhận giống CIS |
| M02·UMB | Worker Payment Status = **Umbrella** (radio "Riddingtons") | ✅ đã có |
| M03·UMB | Welcome — Umbrella (Riddingtons) | ✅ đã có |
| M04·UMB | Personal information — **bản Umbrella** (= CIS − UTR Number) | ✅ đã có |
| M05/M06 (Umbrella) | Health Questionnaire & Declarations — **giống CIS** | ✅ xác nhận |
| **— SECTOR KELTBRAY (NORMAL) —** | | |
| N01 | Prefix → chọn "Keltbray - Keltbray" → Continue | ✅ đã có |
| N02 | Select site location + Select subcontractor (+ "I can't find my subcontractor") | ✅ đã có |
| N03 | Skill card verification → Continue → submission thành công | ✅ đã có |
| — | Chi tiết Skill card sau bước verification (option, submit cuối) | 🔜 QA cập nhật sau |

---

## 1. Tổng quan

Worker truy cập onboarding link, nhập **company prefix "keltbray"** rồi chọn sector. Hệ thống Keltbray có **2 sector / 2 loại luồng** (cả 2 đặc tả trong tài liệu này):
- **Keltbray - KRS** = luồng **induction** (dài) → **§4 / §4B / §4C** (3 nhánh CIS/Limited/Umbrella).
- **Keltbray - Keltbray** = luồng **normal** (gọn 3 bước) → **§4D**.

Sau khi chọn KRS, màn đầu tiên là **Worker Payment Status** — worker chọn loại hình thanh toán: **CIS / Limited / Umbrella**. Lựa chọn này **rẽ nhánh journey** (đã verify với nhánh CIS): chọn **CIS** → hiện thêm radio chọn **provider** (Riddingtons / Industrial Labour) → mỗi provider có màn **Welcome** riêng → rồi vào form Personal Details.

Journey đầy đủ KRS (induction) — 3 nhánh theo Worker Payment Status:

```
Prefix (keltbray) → Select Sector (KRS) → Worker Payment Status
   ├─ CIS → Provider (Riddingtons | Industrial Labour) → Welcome (theo provider)
   │         → [Onboarding details: Personal information] → [Health Questionnaire] → [Declarations]
   │         → Skill card verification  🔚 (scope tài liệu dừng tại đây)
   ├─ Limited → Provider = Industrial Labour (text; WPS hiển thị sai "Riddingtons" — DN-04) → "Kindly provide one of the following":
   │              ├─ Company Registration Number → [Reg Number(format) + MD(No→position) + VAT]
   │              └─ Company Trading Name        → [Trading Name(any) + MD(No→position) + VAT]
   │            → Welcome "Industrial Labour LTD" (dù WPS="Riddingtons" — DN-04) → Personal information (bản Limited):
   │                ├─ VAT Registered = No  → form không có field VAT
   │                └─ VAT Registered = Yes → form thêm VAT Number + Your VAT Certificate (upload)
   │            → Health Questionnaire (giống CIS) → Declarations (giống CIS) → Skill card verification
   └─ Umbrella → Provider radio "Riddingtons" (1 option) → Welcome (Riddingtons)
                → Personal information (bản Umbrella = CIS − UTR Number)
                → Health Questionnaire → Declarations → Skill card verification (đuôi chung; AMB-31 verify)
```

> **Lưu ý nhánh provider:** Riddingtons & Industrial Labour **dùng chung** journey CIS; provider chỉ đổi (a) Welcome (logo+heading), (b) text/link #3 ở Declarations.

> **Stepper (sau Welcome):** 3 tab — **Onboarding details** · **Health Questionnaire** · **Declarations**.

## 2. Actor

- **Worker** (ứng viên đăng ký onboarding cho Keltbray — cả 2 sector: KRS induction & Keltbray normal).

## 3. Phụ thuộc (Dependencies)

- **Profile Service / Verify Identity** — cung cấp pre-filled: Surname, Forenames, Email, Date of Birth, Citizenship.
- **Trade** lookup (sheet single-select — nguồn API ⚠️ verify, tham chiếu Infinity API như DTSource).
- **File upload service** — "Your passport" (png, jpg, jpeg, doc, docx, pdf).
- **Regula** — SDK quét/verify hộ chiếu (field "Your passport").
- **File upload service** — VAT Certificate, các tài liệu khác (component "Pick your source of document").
- Worker Onboarding Service · Validation Service · Form Submission Service.

---

## 4. Đặc tả màn hình (Design-grounded)

### 4.1 Prefix / Select Sector — `M01`
- Body text: *"We use this to identify your contractor or site."*
- Helper (icon ❓): *"If you're unsure, ask your on-site manager."*
- Ô nhập **company prefix** (text). Worker gõ `keltbray`.
- Dropdown gợi ý hiển thị **2 sector**:
  - **"Keltbray - KRS"**
  - **"Keltbray - Keltbray"**
- Chọn **"Keltbray - KRS"** → vào journey KRS (màn Worker Payment Status).

### 4.2 Worker Payment Status — `M02`
- Header: logo **`keltbray`**, nút **Back** (`<`), icon **chuông thông báo** góc phải.
- Tiêu đề (chữ xanh): **"Worker Payment Status"**.
- Field: dropdown placeholder **"Please select here"** (chevron ▾) — **required**.
  - Bỏ trống / chưa chọn → viền đỏ + lỗi đỏ **"Please select here"** (hiển thị dưới field).
- Mở dropdown → sheet **"Worker Payment Status"** với **3 option** (single-select, đánh dấu CheckCircle xanh khi chọn):
  - **CIS**
  - **Limited**
  - **Umbrella**
- **Logic điều kiện — chọn CIS:** sau khi chọn CIS, dropdown thu gọn (hiển thị "CIS") và **hiện thêm radio group provider** (single-select, radio tròn xanh khi chọn):
  - **Riddingtons**
  - **Industrial Labour**
  - → nút **"Next →"** (gradient xanh) ở cuối màn.
- ✅ **AMB-03 Resolved:** Error text đúng là **"Please select here"** (placeholder = error copy).
- ✅ **AMB-05 Resolved:** Radio group provider **KHÔNG có** label/tiêu đề riêng.
- ✅ **AMB-06 Resolved:** Limited → §4B (provider Industrial Labour + company-info); Umbrella → §4C (provider Riddingtons radio 1 option).

### 4.3 Welcome — nhánh CIS (theo provider) — `M03`
- Header: nút **Back** (`<`).
- **Logo provider** (thay đổi theo lựa chọn) + **Heading provider** (chữ xanh).
- **Body (verbatim — GIỐNG NHAU cho cả 2 provider):**
  > *"Please complete your full personal details in line with our client's requirements. You will be contacted by us directly to complete the contract services."*
  >
  > *"Thank you."*
- Nút **"Next →"** → điều hướng tới form **Personal information** (`M04`, tab Onboarding details).

| Provider (chọn ở M02) | Logo | Heading |
|---|---|---|
| **Riddingtons** | "Riddingtons Payroll" | **"Welcome to Riddingtons payroll services."** |
| **Industrial Labour** | "IL — INDUSTRIAL LABOUR LTD" | **"Welcome to Industrial Labour LTD payroll services."** |

> ✅ **Chốt:** Sau Welcome, **cả 2 provider điều hướng về cùng form Personal information (CIS)** và cùng journey (Health Questionnaire → Declarations). Provider chỉ ảnh hưởng: (a) logo + heading màn Welcome, (b) text/link #3 ở Declarations.

### 4.4 Personal information (tab "Onboarding details") — `M04` *(nhánh CIS)*
- Header: logo **`keltbray`**, nút Back (`<`).
- **Stepper 3 tab:** **Onboarding details** (active) · **Health Questionnaire** · **Declarations**.
- Tiêu đề màn (chữ xanh): **"Personal information"**.

| Nhóm | Field | Loại | Ràng buộc |
|---|---|---|---|
| Passport | **Your passport** | Upload ("+ Upload your passport") | **required**; bấm upload → điều hướng tới màn **Regula** (quét/verify hộ chiếu) — KHÁC component "Pick your source of document"; helper *"Accepted file types: png, jpg, jpeg, doc, docx, pdf."* |
| Personal | **Surname** | text | **pre-filled từ profile** (vd "Maya") — **read-only** (không chỉnh sửa/xóa) |
| Personal | **Forenames** | text | **pre-filled từ profile** — **read-only** (không chỉnh sửa/xóa) |
| Address | **Address** | text (placeholder "Please enter here") | **required** |
| Address | **City** | text | **required** |
| Address | **Postcode** | text | **required** |
| Personal | **Candidate's Mobile Phone** | text/phone | **required**; **≥ 11 digits**; lỗi *"either your phone number is invalid or use at least 11 digits"* |
| Personal | **National Insurance Number** | text | **required**; format **2 ký tự + 6 số + 1 ký tự** = `AA999999A` (vd `AH123456L`); lỗi *"Please enter the right format: 2 letters, 6 numbers, 1 letter (e.g. AA999999A)"* |
| Personal | **Date of Birth** | date picker | **pre-filled từ profile** (vd 28/05/2001) — **read-only** |
| Personal | **Citizenship** | dropdown (có cờ) | **pre-filled từ profile** (vd "Vietnamese") — **read-only** |
| Personal | **Contract/Job start date** | date (placeholder "DD/MM/YYYY") | **required** (bỏ trống → "Please enter here") |
| Next of Kin | **Next of Kin** | text | **required** |
| Next of Kin | **Tel (Next of Kin)** | text/phone | **required**; lỗi *"Please enter a valid phone number"* |
| Personal | **Trade** | dropdown ("Please select here") | **required** (sheet single-select — ⚠️ verify) |
| Personal | **Email** | text | **pre-filled từ profile** — **read-only** (không chỉnh sửa/xóa) |
| Payment (CIS) | **UTR Number** | text | format **10 chữ số** hoặc **10 chữ số + "K"** (vd `1234567890` / `1234567890K`); lỗi *"The UTR number can only be of the form - 1234567890 or 1234567890K"* |
| Payment (CIS) | **Name of bank** | text | **required** (bỏ trống → "Please enter here") |
| Payment (CIS) | **Bank account number** | text | format **đúng 8 chữ số** (vd `12345678`); lỗi *"the bank account number can only be of the form - 12345678 or 8 digits ex.12345678"* |
| Payment (CIS) | **Sort code** | text | **required** (bỏ trống → "Please enter here") |
| Payment (CIS) | **Roll number (if applicable)** | text | **OPTIONAL** (nhãn "if applicable") |

→ nút **"Next →"** (gradient xanh) → tab **Health Questionnaire** (§4.5).

- ✅ **AMB-08 Resolved:** "Your passport" **required**; bấm upload → điều hướng màn **Regula** (quét hộ chiếu).
- ✅ **AMB-09 Resolved:** NIN format **2 ký tự + 6 số + 1 ký tự** (`AA999999A`, vd `AH123456L`).
- ✅ **AMB-10 Resolved:** Contract/Job start date · Name of bank · Sort code đều **required** (bỏ trống → "Please enter here").
- ⚠️ **AMB-11:** Block **UTR/Name of bank/Bank account/Sort code/Roll number** là đặc thù **CIS** (worker tự kê khai thuế). Nhánh **Limited / Umbrella** có cùng block này không — chờ screenshot.
- ✅ **AMB-12 Resolved:** Form Personal information **giống nhau** giữa 2 provider — cả Riddingtons & Industrial Labour dùng chung journey CIS (xác nhận bởi QA).

### 4.5 Health Questionnaire (tab "Health Questionnaire") — `M05`
> Áp dụng cho **cả nhánh CIS và Limited** (✅ giống nhau).
- Tab thứ 2 trên stepper (sau "Onboarding details").
- Tiêu đề (chữ xanh): **"Health Questionnaire"**.
- Intro (verbatim): *"Details given are deemed relevant in the interest of your Health and Safety and allow the company to assess the risk to your health."*
- Gồm **10 câu** (Question 1–8, 8.1, 8.2). Mỗi câu là **radio Yes / No bắt buộc**.
- **Pattern (State Transition + validation) — áp dụng cho MỌI câu (đã xác nhận):**
  - Chưa chọn Yes/No → lỗi *"Please answer this question"* (thấy ở Q2).
  - Chọn **Yes** → **luôn** hiện field **"Please enter details"** (placeholder "Please enter here"); bỏ trống → lỗi *"Please give us the details here"*.
  - Chọn **No** → **ẩn** field details.
- → nút **"Next →"** ở cuối tab → tab **Declarations**.

**Nội dung 10 câu (verbatim):**

| Q | Nội dung |
|---|---|
| **Question 1** | "Do you suffer from any of the following illnesses? If so, please specify any prescribed medication used to control your illness: Heart Disease / Asthma / Diabetes / Epilepsy / Nervous Diseases. If the answer is yes to the above, has your illness caused you to be absent from work for more than seven days at any one time?" |
| **Question 2** | "Are you taking medication for the previously mentioned illnesses? If so, please specify." |
| **Question 3** | "Have you had to give up any previous employment for any medical reasons? If yes, please give details." |
| **Question 4** | "Are you partially sighted? Do you wear spectacles or contact lenses? Please specify." |
| **Question 5** | "Are you deaf or partially deaf? Please specify. If yes, then do you wear a hearing aid?" |
| **Question 6** | "Have you knowingly been exposed during your working life to and received any medical treatment for: Lead / Asbestos / Radiation / Noise / Dust / Fumes / Chemical / Harmful substances or associated environments / Other. Please specify." |
| **Question 7** | "If you have been exposed to any of the above, has this led to any illness or industrial injury? If yes, has industrial compensation been sought? Please give details." |
| **Question 8** | "Do you have any outstanding medical problems?" |
| **Question 8.1** | "Are you restricted from carrying out any duties covered by your terms & conditions of employment due to medical reasons?" |
| **Question 8.2** | "Have you ever had any of the following: Heart Trouble or Blood Pressure / Fainting attacks, Blackouts or Fits / Sciatica, Lumbago or any Back Trouble / Skin Trouble or Dermatitis / Chest Problems, Asthma, Bronchitis or Rheumatism / Any Nervous or Mental Problems / A Fear of Heights / A Fear of Confined Spaces. If yes, please specify." |

- ⚠️ **AMB-13:** ✅ Resolved — tổng **10 câu** (Q1–8, 8.1, 8.2).
- ⚠️ **AMB-14:** ✅ Resolved — **mọi câu** chọn Yes → hiện hộp "Please enter details"; chọn No → ẩn (áp dụng cho tất cả 10 câu, kể cả Q8).

### 4.6 Component Document Upload (chụp/upload tài liệu) — `Cmp-DOC`
> ✅ **AMB-15 Resolved:** đây là **component dùng chung** kích hoạt khi bấm **"+ Upload document"** (vd field **"Your VAT Certificate"** — Limited VAT=Yes). Tên tài liệu thay đổi động theo ngữ cảnh = **`{DocType}`** (vd "VAT Certificate"). Cũng dùng cho tài liệu khác (vd "National Insurance Document").

**Luồng đầy đủ:**

1. **Bottom sheet "Pick your source of document"** — 2 lựa chọn:
   - Nút **"Open camera"** (xanh) → màn chụp (bước 2).
   - Nút **"Upload from your device"** (outline) → mở file picker thiết bị.
2. **Màn chụp "Capture your document"** (khi Open camera):
   - Tiêu đề **"Capture your document"**; hướng dẫn *"Position your {DocType} Document within the frame and ensure the details are clearly readable."* (vd "…your VAT Certificate Document…") — lặp làm overlay.
   - Camera viewfinder + khung xanh + nút **chụp tròn xanh**.
3. **Màn preview "{DocType} Document"** (sau khi chụp):
   - Hiển thị ảnh vừa chụp + 2 nút: **"Retake"** (chụp lại) · **"Okay"** (xác nhận → quay về form, file đã đính kèm).
4. **Trạng thái đã upload (trên form):** field hiển thị **card "{DocType} Document"** (icon ✓ xanh) + **icon thùng rác đỏ** (xoá).
5. **View lại** (tap vào card đã upload): màn **"{DocType} Document"** xem ảnh + nút **"Remove and Resubmit"** (xoá & chụp/upload lại).
6. **Xoá nhanh** (tap icon thùng rác): dialog **"Remove {DocType} Document?"** + 2 nút: **"Yes, remove it!"** (xoá) · **"Cancel"** (huỷ).

- **Accepted file types:** "Your VAT Certificate" = **"png, jpg, jpeg, doc, docx, pdf"** (✅ xác nhận). ℹ️ Màn khác (vd National Insurance Document — ngoài scope) ghi "doc, docx, pdf" → khác tập, nhưng nằm ngoài tài liệu này.

### 4.7 Declarations (tab "Declarations") — `M06`
> Áp dụng cho **cả nhánh CIS và Limited** (✅ **giống nhau** — cùng 3 link + checkbox + validation). Provider ở link #3 + text checkbox: CIS = provider đã chọn (Riddingtons/Industrial Labour); **Limited** = theo provider của nhánh (lưu ý mâu thuẫn WPS "Riddingtons" vs Welcome "Industrial Labour LTD" — **DN-04**).
- Header: logo **`keltbray`**, nút Back (`<`), chuông thông báo.
- **Stepper 3 tab:** Onboarding details (done) · Health Questionnaire (done) · **Declarations** (active).
- Tiêu đề (chữ xanh): **"Declarations"**.
- Body (verbatim): *"Please confirm you understand and agree with keltbray rules and policies:"*
- **3 link policy** (chữ xanh gạch chân, tappable):
  1. **"Keltbray Privacy Policies"**
  2. **"Keltbray GDPR Privacy Policies"**
  3. **"{Provider} GDPR Privacy Policies"** — provider-dependent (vd "Riddingtons GDPR Privacy Policies")
- **Checkbox bắt buộc** (vuông bo góc; tick = nền đen ✓):
  > *"I can confirm that all the information is correct, and I understand my details will be sent to Keltbray and {Provider} my payroll provider"* (vd "…Keltbray and Riddingtons my payroll provider")
  - Chưa tick + tap Next → lỗi đỏ *"Please review and confirm these declarations to continue."*
- → nút **"Next →"** (sau khi tick) → điều hướng tới **Skill card verification** (`M07`). *(Không phải màn submit/success — onboarding tiếp tục sang bước compliance check.)*

**Yêu cầu kiểm thử link (QA: "check các link"):** mỗi trong 3 link phải **tap được** và **mở trình duyệt ngoài (external browser)** tới policy tương ứng (Keltbray Privacy · Keltbray GDPR · {Provider} GDPR). ✅ **AMB-18 Resolved:** mở **browser ngoài** (URL đích cụ thể cung cấp khi verify thật).

- ✅ **AMB-16 Resolved:** Nhánh **Industrial Labour** chỉ **đổi text** (link #3 = "Industrial Labour GDPR Privacy Policies"; checkbox "…Keltbray and Industrial Labour my payroll provider"). Mọi thứ còn lại **giống hệt** — cả 2 provider dùng chung journey CIS.
- ✅ **AMB-17 Resolved:** "Next →" ở Declarations → điều hướng **Skill card verification** (M07), không phải success screen.

### 4.8 Skill card verification — `M07` *(giới hạn scope tài liệu lần này)*
- Header: logo **`ppac`**, nút Back (`<`), chuông thông báo.
- Tiêu đề: **"Skill card verification"**.
- Body (verbatim): *"We need your skill card information to complete the compliance check."*
- Label **"Skill card type"** + dropdown placeholder **"Select skill card type"** (chevron ▾) — **required** (vd option **"SIA"**).
- Nút **"Continue →"**: **disabled** (mờ xanh nhạt) khi chưa chọn Skill card type; **enabled** sau khi chọn.
- **Bottom navigation** (4 mục): **New Check** (➕ active) · **Wallet** · **Notification** · **Profile**.
- ✅ **Submission thành công (kỳ vọng cuối E2E):** sau khi chọn Skill card type + Continue → hiện **popup/dialog** với tiêu đề (chữ xanh) **"Your check has been submitted!"** → đây là dấu hiệu xác nhận submission thành công (assert cho test E2E).
- 🔚 **Phạm vi tài liệu dừng tại đây** (theo QA): chi tiết các bước Skill card (danh sách type đầy đủ, nhập chi tiết) **cập nhật vòng sau**.
- ✅ **AMB-19 Resolved:** Danh sách "Skill card type" **không cần quan tâm** — chỉ cần tới bước này, chọn type, Continue → expect popup **"Your check has been submitted!"**.

---

## 4B. Nhánh Limited (Worker Payment Status = Limited)

### 4.9 Worker Payment Status — nhánh Limited — `M02·LTD`
- Chọn **"Limited"** ở dropdown Worker Payment Status → hiển thị **dòng text cố định** (không phải radio chọn) về provider.
  - ✅ **Provider của Limited = Industrial Labour** (tự gán, không cho chọn).
  - 🐞 **DN-04 (UI bug, High):** dòng text hiện đang hiển thị **SAI** = *"Payroll Provider is Riddingtons"* — đúng phải là **Industrial Labour** (khớp Welcome "Industrial Labour LTD"). Cần sửa text WPS.
- Section tiêu đề (chữ xanh) **"Kindly provide one of the following:"** với **2 nút/card** (có mũi tên →):
  - **"Company Registration Number →"**
  - **"Company Trading Name →"**
- Chọn 1 trong 2 → màn nhập tương ứng (M-LTD-01 hoặc M-LTD-02).
- ✅ **AMB-21 Resolved:** Chọn 1 trong 2 (Company Registration Number / Company Trading Name) là **bắt buộc** (one-of).

### 4.10 Company Registration Number (nhánh Limited) — `M-LTD-01`
- Header: logo **`keltbray`**, nút Back (`<`), chuông thông báo.
- Tiêu đề (chữ xanh): **"Company Registration Number"**.
- **Card 1 — Company Registration Number:** label **"Company Registration Number"** + input (placeholder "Please enter here").
  - **Required** — bỏ trống → lỗi *"Please enter here"*.
  - **Format:** **`12345678`** (8 chữ số) **hoặc** **2 chữ cái + 8 chữ số** (vd `SC12345678`); nhập sai (vd `CHCJMX`) → lỗi *"The Company Registration Number can only be of the form – 12345678 or two letters followed by 8 digits ex. SC12345678"*.
- **Card 2 — "Are you a Managing Director?"** — radio **Yes / No** (**required**; chưa chọn → lỗi *"Please select here"*).
  - Chọn **No** → hiện field **"Please enter your position"** (placeholder "Please enter here", **required**, bỏ trống → *"Please enter here"*). Chọn **Yes** → ẩn field.
- **Card 3 — "Are you VAT Registered?"** — radio **Yes / No** (**required**; chưa chọn → lỗi *"Please select here"*).
- → nút **"Next →"**.
- ℹ️ M-LTD-01 và M-LTD-02 có cấu trúc giống nhau (cùng "Are you a Managing Director?" + "Are you VAT Registered?"); chỉ khác **Card 1** (Reg Number có format vs Trading Name text bất kỳ) và tiêu đề màn.

### 4.11 Company Trading Name — tiêu đề "Company Registration Name" (nhánh Limited) — `M-LTD-02`
- Header: logo **`keltbray`**, nút Back (`<`), chuông thông báo.
- Tiêu đề (chữ xanh): **"Company Registration Name"** *(tiêu đề màn khác field — field "Company Trading Name" là đúng theo design, xem DN-01 đã chốt)*.
- **Card 1 — Company Trading Name:** label **"Company Trading Name"** + input (placeholder "Please enter here").
  - **Required** — nhập **bất kỳ text nào, miễn không để trống** là hợp lệ (🔧 **KHÔNG validate format**); bỏ trống → lỗi *"Please enter here"*.
  - 🔧 (Đính chính) field này **KHÔNG** dùng format `12345678`/`SC12345678` — format đó thuộc **Company Registration Number** (M-LTD-01). Xem DN-03 (đã thu hồi).
- **Card 2 — "Are you a Managing Director?"** — radio **Yes / No**.
  - Chọn **No** → hiện field **"Please enter your position"** (placeholder "Please enter here", **required**, lỗi *"Please enter here"*). Chọn **Yes** → ẩn field.
- **Card 3 — "Are you VAT Registered?"** — radio **Yes / No**; chưa chọn → lỗi *"Please select here"* (**required**).
- → nút **"Next →"** → màn **Welcome = "Welcome to Industrial Labour LTD payroll services."** (provider Limited = Industrial Labour; WPS hiển thị sai "Riddingtons" — DN-04) → **Personal information bản Limited** (§4.12) → Health Questionnaire → Declarations → Skill card verification (đuôi chung).

> **M-LTD-01 vs M-LTD-02 (bản hiện hành):** cả 2 đều dùng **"Are you a Managing Director?"** + **"Are you VAT Registered?"** (đồng nhất) — DN-02 đã thu hồi. Chỉ khác **Card 1**: Reg Number (có format) vs Trading Name (text bất kỳ) và tiêu đề màn.

### 4.12 Personal information — bản Limited (tab "Onboarding details") — `M04·LTD`
- Header: logo **`keltbray`**, nút Back (`<`). **Stepper 3 tab** y như CIS: Onboarding details (active) · Health Questionnaire · Declarations.
- Tiêu đề (chữ xanh): **"Personal information"**.
- **Routing — 2 biến thể theo "Are you VAT Registered?":**
  - **= No** → Personal information bản Limited **không có** field VAT (mô tả bảng dưới).
  - **= Yes** → Personal information bản Limited **có thêm 2 field**: **VAT Number** + **Your VAT Certificate** (upload), chèn ngay sau UTR Number. (✅ AMB-24 resolved.)
- **Your passport** ở bản Limited là **required** — bỏ trống → lỗi *"Please upload your passport"* (✅ giải quyết AMB-08 cho nhánh Limited).

**Field (theo thứ tự trên màn — bản VAT=No):**

| Nhóm | Field | Loại | Ràng buộc |
|---|---|---|---|
| Passport | **Your passport** | Upload ("+ Upload your passport") | **required** — bỏ trống lỗi "Please upload your passport"; bấm upload → màn **Regula**; helper "Accepted file types: png, jpg, jpeg, doc, docx, pdf." |
| Personal | **Surname** | text | **pre-filled từ profile** (vd "Maya") |
| Personal | **Forenames** | text | **pre-filled từ profile**; lỗi minh hoạ "Please fill in this field" |
| Address | **Address** | text | **required** |
| Address | **City** | text | **required** |
| Address | **Postcode** | text | **required** |
| Personal | **Candidate's Mobile Phone** | text/phone | **required**; ≥ 11 digits (lỗi "either your phone number is invalid or use at least 11 digits") |
| Personal | **Trade** | dropdown ("Please select here") | **required** |
| Personal | **Email** | text | **pre-filled từ profile**; lỗi minh hoạ "Please enter a valid email address" |
| Payment | **UTR Number** | text | format `1234567890` / `1234567890K` |
| **VAT (chỉ VAT=Yes)** | **VAT Number** | text | chỉ hiện khi **VAT Registered = Yes** |
| **VAT (chỉ VAT=Yes)** | **Your VAT Certificate** | Upload ("+ Upload document") | chỉ hiện khi **VAT Registered = Yes**; helper "Accepted file types: png, jpg, jpeg, doc, docx, pdf." |
| Company | **Company trading name** | text | **pre-filled** từ "Company Trading Name" nhập ở M-LTD-02 (khi chọn path Company Trading Name) |
| Company | **Company registration number** | text | format `12345678` / `SC12345678`; **pre-fill** từ M-LTD-01 (khi chọn path Company Registration Number) |
| Payment | **Name of bank** | text | **required** (bỏ trống → "Please enter here") |
| Payment | **Bank account number** | text | đúng 8 chữ số |
| Payment | **Sort code** | text | **required** (bỏ trống → "Please enter here") |
| Payment | **Roll number (if applicable)** | text | **OPTIONAL** |

> **VAT=No vs VAT=Yes:** bản **VAT=Yes** chèn thêm **VAT Number** + **Your VAT Certificate** (upload) ngay sau **UTR Number**; mọi field khác giống nhau.

→ nút **"Next →"** → tab Health Questionnaire (✅ giống CIS — AMB-26).

**So sánh Personal information — CIS (M04) vs Limited (M04·LTD):**

| Field | CIS (M04) | Limited (M04·LTD) |
|---|---|---|
| National Insurance Number | ✅ có | ❌ **bỏ** |
| Date of Birth | ✅ có (pre-filled) | ❌ **bỏ** |
| Citizenship | ✅ có (pre-filled) | ❌ **bỏ** |
| Contract/Job start date | ✅ có | ❌ **bỏ** |
| Next of Kin | ✅ có | ❌ **bỏ** |
| Tel (Next of Kin) | ✅ có | ❌ **bỏ** |
| Company trading name | ❌ không | ✅ **thêm** |
| Company registration number | ❌ không | ✅ **thêm** |
| Pre-filled | Surname/Forenames/Email/DOB/Citizenship | **Surname/Forenames/Email** (chỉ 3) |
| Chung 2 bản | passport, Surname, Forenames, Address, City, Postcode, Candidate's Mobile Phone, Trade, Email, UTR, Name of bank, Bank account number, Sort code, Roll number | (giống) |

- ✅ **Pre-fill liên màn (đính chính):** field **"Company trading name"** (M04·LTD) được **điền sẵn** từ giá trị **"Company Trading Name"** nhập ở M-LTD-02 (path Company Trading Name). Tương tự path Company Registration Number → pre-fill "Company registration number".
- ⚠️ **AMB-25:** Required-status các field Limited (Name of bank, Sort code) — chờ verify.
- ✅ **AMB-26 Resolved:** Tab Health Questionnaire & Declarations ở Limited **giống CIS**.
- ✅ **AMB-27 Resolved:** Field "Company trading name" (M04·LTD) **pre-filled** từ M-LTD-02.

---

## 4C. Nhánh Umbrella (Worker Payment Status = Umbrella)

### 4.13 Worker Payment Status — nhánh Umbrella — `M02·UMB`
- Chọn **"Umbrella"** ở dropdown → hiện **radio group provider**: **Riddingtons** (chỉ **1 option**, selected radio tròn xanh).
- → nút **"Next →"** → màn Welcome (Riddingtons).
- ℹ️ **Pattern provider 3 nhánh khác nhau:** CIS = radio 2 option (Riddingtons/Industrial Labour) · **Limited = text cố định (provider = Industrial Labour; đang hiển thị sai "Riddingtons" — DN-04)** · **Umbrella = radio 1 option (Riddingtons)**.

### 4.14 Welcome — nhánh Umbrella — `M03·UMB`
- = **"Welcome to Riddingtons payroll services."** (logo Riddingtons Payroll; body giống các Welcome khác). → nút **"Next →"** → Personal information bản Umbrella.

### 4.15 Personal information — bản Umbrella (tab "Onboarding details") — `M04·UMB`
- Stepper 3 tab y như CIS: Onboarding details (active) · Health Questionnaire · Declarations. Tiêu đề **"Personal information"**.
- **= GIỐNG bản CIS (M04) nhưng BỎ field "UTR Number"** — mọi field khác y hệt CIS.

**Field (theo thứ tự):** Your passport · Surname (pre-filled) · Forenames (pre-filled) · Address · City · Postcode · Candidate's Mobile Phone (≥11) · National Insurance Number · Date of Birth (pre-filled) · Citizenship (pre-filled) · Contract/Job start date · Next of Kin · Tel (Next of Kin) · Trade · Email (pre-filled) · Name of bank · Bank account number (8 số) · Sort code · Roll number (if applicable) (OPTIONAL).

- **Pre-filled:** Surname/Forenames/Email/DOB/Citizenship (5 field — giống CIS).
- **KHÔNG có:** UTR Number, VAT Number/Certificate, Company trading name/registration number.
- → nút **"Next →"** → Health Questionnaire → Declarations → Skill card verification *(đuôi chung — ✅ AMB-31: Health Q & Declarations Umbrella **giống CIS**; provider Declarations = Riddingtons)*.

**So sánh Personal information 3 nhánh:**

| Field đặc thù | CIS (M04) | Limited (M04·LTD) | Umbrella (M04·UMB) |
|---|---|---|---|
| UTR Number | ✅ | ✅ | ❌ **bỏ** |
| VAT Number / Your VAT Certificate | ❌ | ✅ (khi VAT=Yes) | ❌ |
| Company trading name / registration number | ❌ | ✅ | ❌ |
| NIN · DOB · Citizenship · Contract start · Next of Kin · Tel | ✅ | ❌ (bỏ) | ✅ |
| Name of bank · Bank account · Sort code · Roll number | ✅ | ✅ | ✅ |

---

## 4D. Sector Keltbray — luồng NORMAL

> Sector **"Keltbray - Keltbray"** = luồng **normal**, KHÁC hẳn KRS (induction). Chỉ **3 bước**: chọn location/subcontractor → Skill card → submit. **KHÔNG** có Worker Payment Status / Personal information / Health Questionnaire / Declarations.

```
Prefix (keltbray) → chọn "Keltbray - Keltbray" → Continue
  → N02: Select site location + Select subcontractor (+ "I can't find my subcontractor in the list") → Continue
  → N03: Skill card verification (chọn Skill card type) → Continue
  → ✅ tạo submission thành công
```

### 4.16 Prefix / Select Sector (normal) — `N01`
- Giống `M01`: nhập prefix `keltbray` → gợi ý 2 sector. Chọn **"Keltbray - Keltbray"** → **Continue** → màn N02.

### 4.17 Select site location & subcontractor — `N02`
- Header: logo **`ppac`**, nút Back (`<`), logo **`keltbray`**. Có **bottom nav** (New Check active · Wallet · Notification [badge] · Profile) + bong bóng hỗ trợ (chat) góc phải.
- Field **"Select site location"** — dropdown placeholder **"Select site location"** (chevron ▾). *(required? ⚠️ AMB-N01)*
- Field **"Select subcontractor"** — dropdown placeholder **"Select subcontractor"** (chevron ▾). *(required? ⚠️ AMB-N01)*
- **Checkbox:** **"I can't find my subcontractor in the list"** — *(tick → bỏ qua/nhập tay subcontractor? ⚠️ AMB-N02)*.
- → nút **"Continue →"** → màn Skill card verification (N03).

### 4.18 Skill card verification (normal) — `N03`
- Header: logo **`ppac`**, nút Back (`<`).
- Tiêu đề (chữ xanh): **"Skill card verification"**.
- Body (verbatim): *"We need your skill card information to complete the compliance check."*
- Helper (icon ❓, verbatim): *"If your trade/occupation does not require a skill card please scroll to Not Applicable."*
- Label **"Skill card type:"** + dropdown placeholder **"Select skill card type"** (chevron ▾) — **required**; có option **"Not Applicable"** (cuộn xuống) cho trade không cần skill card.
- Nút **"Continue →"**: **disabled** khi chưa chọn; **enabled** sau khi chọn.
- → **Continue** → ✅ **submission thành công** = hiện **popup "Your check has been submitted!"** (giống M07).
- ℹ️ So với KRS `M07`: normal có thêm **helper "Not Applicable"** + label có dấu hai chấm "Skill card type:".

- ⏸ **AMB-N01 / AMB-N02:** Required-status location/subcontractor + hành vi checkbox "I can't find my subcontractor" → **không cần quan tâm** (theo QA).

**Khác biệt KRS (induction) vs Keltbray (normal):**

| Hạng mục | KRS (induction) | Keltbray (normal) |
|---|---|---|
| Số bước | Dài (payment → provider → Welcome → Personal info → Health Q → Declarations → Skill card) | **3 bước** (location/subcontractor → Skill card → submit) |
| Worker Payment Status / Personal info / Health Q / Declarations | ✅ Có | ❌ **Không** |
| Select site location / subcontractor | ❌ Không | ✅ **Có** (N02) |
| Skill card verification | M07 | N03 — thêm helper "Not Applicable" |
| Kỳ vọng cuối | Submission thành công | Submission thành công |

---

## 5. Dữ liệu nội dung verbatim (Content reference)

**Welcome screen body (cả 2 provider):**
> "Please complete your full personal details in line with our client's requirements. You will be contacted by us directly to complete the contract services."
> "Thank you."

**Error / format messages (M04 Personal information):**
| Field | Message verbatim |
|---|---|
| (text field bắt buộc trống — minh hoạ Figma) | "Please fill in this field" — ⚠️ Forenames là **read-only** nên không trigger; copy cho field editable chờ verify |
| Contract/Job start date · Name of bank · Sort code (và field Limited) | "Please enter here" |
| National Insurance Number (sai format) | "Please enter the right format: 2 letters, 6 numbers, 1 letter (e.g. AA999999A)" |
| Candidate's Mobile Phone | "either your phone number is invalid or use at least 11 digits" |
| Tel (Next of Kin) | "Please enter a valid phone number" |
| Email | "Please enter a valid email address" |
| UTR Number | "The UTR number can only be of the form - 1234567890 or 1234567890K" |
| Bank account number | "the bank account number can only be of the form - 12345678 or 8 digits ex.12345678" |
| Worker Payment Status (M02) | "Please select here" |
| Health Questionnaire — chưa chọn Yes/No | "Please answer this question" |
| Health Questionnaire — details (khi Yes) | "Please give us the details here" |
| Declarations — chưa tick checkbox | "Please review and confirm these declarations to continue." |

**Health Questionnaire — intro + 10 câu (verbatim):** xem bảng đầy đủ tại §4.5 (intro "Details given are deemed relevant…"; Q1–Q8, Q8.1, Q8.2).

**Component Document Upload (verbatim):**
- Source sheet: "Pick your source of document" · "Open camera" · "Upload from your device"
- Capture: "Capture your document" · "Position your VAT Certificate Document within the frame and ensure the details are clearly readable."
- Preview: tiêu đề "VAT Certificate Document" · "Retake" · "Okay"
- Card đã upload: "VAT Certificate Document" (✓ + icon thùng rác)
- View lại: "VAT Certificate Document" · "Remove and Resubmit"
- Dialog xoá: "Remove VAT Certificate Document?" · "Yes, remove it!" · "Cancel"
- (Tên "VAT Certificate" thay đổi động theo {DocType})

**Declarations (verbatim):**
- Body: "Please confirm you understand and agree with keltbray rules and policies:"
- Links: "Keltbray Privacy Policies" · "Keltbray GDPR Privacy Policies" · "{Provider} GDPR Privacy Policies" (vd "Riddingtons GDPR Privacy Policies")
- Checkbox: "I can confirm that all the information is correct, and I understand my details will be sent to Keltbray and {Provider} my payroll provider" (vd "…Keltbray and Riddingtons my payroll provider")

**Skill card verification (verbatim):** "Skill card verification" · "We need your skill card information to complete the compliance check." · label "Skill card type" · placeholder "Select skill card type" (vd option "SIA") · nút "Continue →" · bottom nav "New Check / Wallet / Notification / Profile".

**Submission thành công (verbatim):** popup **"Your check has been submitted!"** (sau Continue ở Skill card verification — áp dụng cả 2 sector).

**Nhánh Limited (verbatim):**
- Worker Payment Status = Limited: "Payroll Provider is Riddingtons" (text đang hiển thị — **sai theo DN-04**, đúng phải "Industrial Labour")
- Section: "Kindly provide one of the following:" · nút "Company Registration Number →" · nút "Company Trading Name →"
- M-LTD-01 (title "Company Registration Number"): label "Company Registration Number" · "Are you a Managing Director?" (Yes/No) → "Please enter your position" · "Are you VAT Registered?" (Yes/No)
- M-LTD-02 (title "Company Registration Name"): label "Company Trading Name" · "Are you a Managing Director?" (Yes/No) → "Please enter your position" · "Are you VAT Registered?" (Yes/No)
- Error Company Registration Number (sai format): "The Company Registration Number can only be of the form – 12345678 or two letters followed by 8 digits ex. SC12345678"
- Error required (field text trống): "Please enter here" · Error radio chưa chọn (MD / VAT): "Please select here"

**Personal information bản Limited (verbatim bổ sung):**
- Passport required (bỏ trống): "Please upload your passport"
- VAT=Yes thêm: label "VAT Number" · "Your VAT Certificate" + nút "+ Upload document" + helper "Accepted file types: png, jpg, jpeg, doc, docx, pdf."

**Nhánh Umbrella (verbatim):**
- Worker Payment Status = Umbrella → radio "Riddingtons" (1 option) → Welcome "Welcome to Riddingtons payroll services."
- Personal information bản Umbrella = giống CIS, **không có field "UTR Number"**.

**Sector Keltbray — Normal (verbatim):**
- N02: label "Select site location" · "Select subcontractor" · checkbox "I can't find my subcontractor in the list" · nút "Continue →"
- N03: "Skill card verification" · "We need your skill card information to complete the compliance check." · helper "If your trade/occupation does not require a skill card please scroll to Not Applicable." · label "Skill card type:" · placeholder "Select skill card type" · option "Not Applicable" · nút "Continue →"

**Định dạng (format rules):**
- **Candidate's Mobile Phone:** tối thiểu **11 chữ số**.
- **UTR Number:** `1234567890` (10 chữ số) **hoặc** `1234567890K` (10 chữ số + hậu tố "K").
- **Bank account number:** đúng **8 chữ số** (vd `12345678`).
- **Company Registration Number:** `12345678` (8 chữ số) **hoặc** 2 chữ cái + 8 chữ số (vd `SC12345678`).

## 6. Business Rules (Keltbray — cả 2 sector)

> BR-01 → BR-38 = sector **KRS (induction)**. BR-N01 → BR-N03 = sector **Keltbray (normal)**.

| ID | Rule |
|---|---|
| BR-01 | Worker nhập prefix **keltbray** + chọn sector **KRS** trước khi vào journey |
| BR-02 | Worker Payment Status **bắt buộc chọn** (CIS / Limited / Umbrella) — bỏ trống → lỗi "Please select here" |
| BR-03 | Chọn **CIS** → bắt buộc chọn tiếp **provider** (Riddingtons / Industrial Labour) mới Next được |
| BR-04 | Mỗi provider có màn **Welcome** riêng (logo + heading theo provider; body text giống nhau) trước khi vào Personal Details — Riddingtons & Industrial Labour LTD |
| BR-05 | Sau Welcome: journey 3 tab — **Onboarding details** (Personal information) → **Health Questionnaire** → **Declarations** |
| BR-06 | **Surname, Forenames, Email, Date of Birth, Citizenship** lấy **pre-filled từ profile** worker — **read-only, KHÔNG cho chỉnh sửa/xóa** (áp dụng cả 3 nhánh CIS/Limited/Umbrella) |
| BR-07 | **Candidate's Mobile Phone** ≥ 11 chữ số |
| BR-08 | **UTR Number** dạng `1234567890` hoặc `1234567890K` |
| BR-09 | **Bank account number** đúng 8 chữ số |
| BR-10 | **Roll number** là optional ("if applicable"); các field thanh toán (UTR/Bank/Sort code) đặc thù **CIS** |
| BR-11 | Health Questionnaire: **10 câu** (Q1–8, 8.1, 8.2), mỗi câu **Yes/No bắt buộc**; chưa chọn → "Please answer this question" |
| BR-12 | Health Questionnaire: **mọi câu** chọn **Yes** → hiện field "Please enter details" required (lỗi "Please give us the details here"); **No** → ẩn field |
| BR-13 | Declarations: **checkbox xác nhận bắt buộc** tick mới Submit (lỗi "Please review and confirm these declarations to continue.") |
| BR-14 | Declarations: link thứ 3 + tên trong text checkbox **đổi theo provider** ({Provider} GDPR Privacy Policies; "…sent to Keltbray and {Provider}…") |
| BR-15 | Declarations: 3 link policy phải tap được & mở đúng (Keltbray Privacy · Keltbray GDPR · {Provider} GDPR) |
| BR-16 | Cả 2 provider (Riddingtons/Industrial Labour) **dùng chung journey CIS** (Personal information → Health Questionnaire → Declarations); provider chỉ đổi Welcome + text Declarations |
| BR-17 | Declarations "Next →" (sau khi tick) → **Skill card verification** (compliance check), không phải submit/success |
| BR-18 | Skill card verification: **Skill card type bắt buộc**; nút "Continue →" disabled tới khi chọn |
| BR-19 | **Nhánh Limited:** provider tự gán = **Industrial Labour** (WPS đang hiển thị sai "Riddingtons" — DN-04) → section "Kindly provide one of the following": **Company Registration Number** hoặc **Company Trading Name** (one-of) |
| BR-20 | Limited: **Company Registration Number** required + format `12345678` (8 số) hoặc 2 chữ cái + 8 số (vd `SC12345678`) — lỗi "The Company Registration Number can only be of the form – …" |
| BR-21 | Limited (cả 2 màn): "Are you a Managing Director?" **required** (chưa chọn → "Please select here"); = **No** → field **"Please enter your position"** required |
| BR-22 | Limited: "Are you VAT Registered?" bắt buộc chọn (lỗi "Please select here"); **Company Trading Name required, nhập text bất kỳ không trống** (không validate format) |
| BR-23 | Limited: provider = **Industrial Labour** (WPS text hiển thị sai "Riddingtons" — DN-04); sau company info → **Welcome "Industrial Labour LTD"** → **Personal information bản Limited** |
| BR-24 | Limited: "Are you VAT Registered?" = **No** → Personal information bản Limited (Yes → khác, AMB-24) |
| BR-25 | Personal information bản Limited **KHÁC CIS**: bỏ NIN/DOB/Citizenship/Contract start/Next of Kin/Tel; **thêm** Company trading name + Company registration number; pre-filled chỉ Surname/Forenames/Email |
| BR-26 | Limited: field **"Company trading name"** (M04·LTD) **pre-fill** từ "Company Trading Name" nhập ở M-LTD-02; "Company registration number" pre-fill từ M-LTD-01 (theo path đã chọn) |
| BR-27 | Limited: **VAT Registered = Yes** → Personal information thêm **VAT Number** + **Your VAT Certificate** (upload); = No → không có 2 field này |
| BR-28 | Limited: **Your passport** là **required** (lỗi "Please upload your passport") |
| BR-29 | Component upload tài liệu: bấm "+ Upload document" → sheet "Pick your source of document" (Open camera / Upload from your device) → chụp → preview (Retake/Okay) → card đã upload (✓ + thùng rác) |
| BR-30 | Tài liệu đã upload: tap card → view + "Remove and Resubmit"; tap thùng rác → dialog "Remove {DocType} Document?" (Yes, remove it! / Cancel) |
| BR-31 | **Nhánh Umbrella:** chọn Umbrella → radio provider **Riddingtons** (1 option) → Welcome (Riddingtons) → Personal information |
| BR-32 | Personal information bản **Umbrella = bản CIS nhưng BỎ field UTR Number** (giữ NIN/DOB/Citizenship/Next of Kin/Tel; không có VAT/Company fields) |
| BR-33 | **Provider 3 nhánh:** CIS = radio 2 option (Riddingtons/Industrial Labour) · Limited = text cố định = **Industrial Labour** (hiện hiển thị sai "Riddingtons" — DN-04) · Umbrella = radio 1 option (Riddingtons) |
| BR-34 | **National Insurance Number** format **2 ký tự + 6 số + 1 ký tự** (`AA999999A`, vd `AH123456L`); lỗi "Please enter the right format: 2 letters, 6 numbers, 1 letter (e.g. AA999999A)" |
| BR-35 | **Your passport** required; bấm "+ Upload your passport" → điều hướng màn **Regula** (quét hộ chiếu) — khác component upload tài liệu khác |
| BR-36 | **3 link policy** ở Declarations mở **trình duyệt ngoài** (external browser) |
| BR-37 | **Required + "Please enter here":** Contract/Job start date · Name of bank · Sort code (cả CIS & Limited) bỏ trống → "Please enter here" |
| BR-38 | **Kỳ vọng cuối journey (cả 2 sector)** = sau Skill card verification + Continue → **popup "Your check has been submitted!"** (= submission thành công) |
| BR-39 | **Back / thoát rồi quay lại** (cả 2 sector) → dữ liệu đã nhập **bị RESET, KHÔNG lưu / không resume** — ⚠️ **KHÁC DTSource** (DTSource giữ data) |
| **BR-N01** | **Sector Keltbray (normal):** chọn "Keltbray - Keltbray" → Continue → luồng **3 bước** (location/subcontractor → Skill card → submit); KHÔNG có Payment/Personal info/Health Q/Declarations |
| **BR-N02** | Normal N02: chọn **site location** + **subcontractor**; checkbox "I can't find my subcontractor in the list" cho trường hợp không tìm thấy (⚠️ AMB-N02) |
| **BR-N03** | Normal N03: **Skill card type bắt buộc** ("Continue →" disabled tới khi chọn); có option **"Not Applicable"**; hoàn tất → **submission thành công** |
| BR-… | *(QA cập nhật sau — chi tiết Skill card sau bước verification cả 2 sector)* |

## 7. Validation (in-scope — cả 2 sector)

**Sector Keltbray (normal):** VAL-N01 Select site location / Select subcontractor (required-status ⚠️ AMB-N01) · VAL-N02 Skill card type required (Continue disabled tới khi chọn) · VAL-N03 E2E: hoàn tất 3 bước → submission thành công.

**Sector KRS (induction):**

VAL-01 Worker Payment Status required (CIS/Limited/Umbrella) · VAL-02 Provider required khi chọn CIS (Riddingtons/Industrial Labour) · VAL-03 Personal information required (Address, City, Postcode, Candidate's Mobile Phone, NIN, Contract start date, Next of Kin, Tel Next of Kin, Trade) — Roll number **optional**; Surname/Forenames/Email/DOB/Citizenship pre-filled · VAL-04 Candidate's Mobile Phone ≥ 11 digits · VAL-05 Tel (Next of Kin) phone format · VAL-06 Email format · VAL-07 UTR format (10 digits hoặc 10+K) · VAL-08 Bank account number = 8 digits · VAL-09 Declarations checkbox bắt buộc tick (lỗi "Please review and confirm these declarations to continue.") · VAL-10 3 link policy tap mở đúng đích · VAL-11 Skill card type required (Continue → disabled tới khi chọn) · **VAL-12 (Limited)** Company Registration Number format (`12345678` / `SC12345678`) · VAL-13 (Limited) Company Trading Name required · VAL-14 (Limited) MD=No → "Please enter your position" required · VAL-15 (Limited) "Are you VAT registered?" required (lỗi "Please select here") · **VAL-16 (Umbrella)** Personal information = giống CIS nhưng **không có UTR Number** (NIN/DOB/Citizenship/Next of Kin/Tel vẫn required như CIS) · **VAL-17** NIN format `AA999999A` (lỗi "Please enter the right format: 2 letters, 6 numbers, 1 letter (e.g. AA999999A)") · **VAL-18** E2E cả 2 sector: hoàn tất → popup "Your check has been submitted!"

---

## 8. Defects & Design Notes phát hiện

| ID | Loại | Mô tả | Ưu tiên |
|---|---|---|---|
| ~~DN-01~~ | ✅ Chốt — không phải bug | M-LTD-02 field **"Company Trading Name"** là **đúng theo design** (tiêu đề màn "Company Registration Name" chỉ là tên màn) | — |
| ~~DN-02~~ | ❌ **Đã thu hồi** | Bản hiện hành: cả M-LTD-01 & M-LTD-02 đều dùng "Are you a Managing Director?" + "Are you VAT Registered?" (đồng nhất) — không còn lệch copy | — |
| **DN-04** | 🐞 UI bug (Limited) | Worker Payment Status hiển thị **SAI** dòng *"Payroll Provider is Riddingtons"*. ✅ **Provider đúng của Limited = Industrial Labour** (khớp Welcome "Industrial Labour LTD"). → Cần **sửa text WPS** thành Industrial Labour | High |
| ~~DN-03~~ | ❌ **Đã thu hồi** (kết luận sai) | Trước nghi field **Company Trading Name** validate format reg number. **Đính chính (QA):** field này nhập **text bất kỳ không trống**, KHÔNG validate format. Format `12345678`/`SC12345678` thuộc field **Company Registration Number** (M-LTD-01). Giá trị Company Trading Name → pre-fill sang "Company trading name" ở M04·LTD | — |

## 9. Khác biệt Keltbray KRS vs baseline (DTSource / Ballycommon)

| Hạng mục | DTSource / Ballycommon | **Keltbray KRS** |
|---|---|---|
| Sector | Rail / Construction | **KRS / Keltbray** |
| Payment/Payroll | DTSource: không có · Ballycommon: CIS/PAYE | **Worker Payment Status: CIS / Limited / Umbrella** (màn riêng đầu journey), CIS → chọn tiếp provider |
| Provider | — | **Riddingtons / Industrial Labour** (nhánh CIS), mỗi provider có màn Welcome riêng |
| Tab/stepper sau Welcome | DTSource Rail: 8 form tuần tự | **3 tab**: Onboarding details · Health Questionnaire · Declarations |
| Pre-filled | DTSource: Surname/Email/DOB (Verify identity) | **Surname/Forenames/Email/DOB/Citizenship** (profile) |
| Field thanh toán | DTSource: không | **UTR / Name of bank / Bank account / Sort code / Roll number** (block CIS) |
| Upload | DTSource: không | **"Your passport"** upload (png/jpg/jpeg/doc/docx/pdf) |
| Declaration cuối | DTSource: 4 toggle Privacy & Submit | **1 checkbox** xác nhận + **3 link policy** (Keltbray Privacy/GDPR + {Provider} GDPR), text đổi theo provider |
| Nhánh Limited | — | Thêm bước **company info** (Company Registration Number / Trading Name + Managing Director + VAT registered) trước Welcome |
| 3 nhánh payment | — | **CIS** (UTR + 2 provider) · **Limited** (Industrial Labour, company info + VAT) · **Umbrella** (Riddingtons, Personal info = CIS − UTR) |
| Sector normal | — | Keltbray-Keltbray = luồng 3 bước (location/subcontractor → Skill card → submit) — §4D |
| **Back / resume** | DTSource: **giữ data** (resume dở dang) | Keltbray: **RESET** — thoát/back là mất dữ liệu (BR-39) |

## 10. Bảng Ambiguity (chờ chốt)

| ID | Vấn đề | Trạng thái |
|---|---|---|
| **AMB-02** | Worker Payment Status có rẽ nhánh journey theo CIS/Limited/Umbrella không | ✅ **Có** — đã verify nhánh CIS (→ provider → Welcome) |
| **AMB-03** | Error copy màn Worker Payment Status (placeholder = error = "Please select here"?) | ✅ **Đúng** — error = "Please select here" |
| **AMB-04** | Sector thứ 2 "Keltbray - Keltbray" | ✅ **Đã gộp vào tài liệu này** — luồng **normal** (§4D); KRS = luồng induction |
| **AMB-05** | Tên/label của radio group provider (Riddingtons / Industrial Labour) | ✅ **KHÔNG có** label/tiêu đề nhóm |
| **AMB-06** | Nhánh **Limited** / **Umbrella** hiện gì sau khi chọn | ✅ **Resolved** — Limited §4B, Umbrella §4C |
| **AMB-07** | Nhánh CIS → **Industrial Labour** có màn Welcome riêng không | ✅ **Có** — heading "Welcome to Industrial Labour LTD payroll services.", body giống Riddingtons |
| **AMB-08** | "Your passport" upload — required + cơ chế | ✅ **Required**; bấm upload → điều hướng màn **Regula** (quét hộ chiếu) |
| **AMB-09** | Format & error của National Insurance Number | ✅ **2 ký tự + 6 số + 1 ký tự** (`AA999999A`, vd `AH123456L`) |
| **AMB-10** | Required-status M04 (Contract start date, Name of bank, Sort code, passport) | ✅ **Đều required** (Contract/Name of bank/Sort code bỏ trống → "Please enter here"; passport required) |
| **AMB-11** | Block thanh toán (UTR/Bank/Sort/Roll) có ở nhánh Limited/Umbrella không | 🔜 QA cập nhật sau |
| **AMB-12** | Form Personal information có giống nhau giữa 2 provider không | ✅ **Giống nhau** — cả 2 provider dùng chung journey CIS |
| **AMB-13** | Health Questionnaire tổng cộng bao nhiêu câu | ✅ **10 câu** (Q1–8, 8.1, 8.2) |
| **AMB-14** | Câu nào hiện field "Please enter details" khi Yes (Q8 không có chữ specify?) | ✅ **Mọi câu** Yes → hiện hộp details; No → ẩn |
| **AMB-15** | "Capture your document" (VAT Certificate) xuất hiện ở bước nào | ✅ **Resolved** — component upload khi bấm "+ Upload document" (vd Your VAT Certificate); xem §4.6 |
| **AMB-16** | Declarations: link/text checkbox đổi theo provider (Riddingtons ↔ Industrial Labour) | ✅ **Chỉ đổi text**, vẫn chung journey CIS |
| **AMB-17** | Màn sau "Next →" ở Declarations | ✅ **Skill card verification** (M07), không phải success screen |
| **AMB-18** | 3 link policy mở in-app hay browser ngoài | ✅ **Browser ngoài** (URL đích cụ thể verify khi test thật) |
| **AMB-19** | Danh sách giá trị "Skill card type" (option dropdown M07) | ✅ **Không cần** — tới bước này rồi expect **submission thành công** |
| **AMB-20** | Limited có provider **Riddingtons** không, hay Industrial Labour | ✅ **Industrial Labour** (WPS đang hiển thị sai text "Riddingtons" — DN-04) |
| **AMB-21** | Limited: chọn "one of the following" (Reg Number/Trading Name) có bắt buộc? | ✅ **Bắt buộc** (one-of) |
| **AMB-22** | Limited: sau Welcome có vào Personal information như CIS không | ✅ **Có** vào Personal information nhưng **KHÁC bản CIS** — field cụ thể 🔜 chờ screenshot |
| **AMB-23** | Field "Company Trading Name" có dùng error format "company registration number" | ❌ **Đính chính: KHÔNG** — Company Trading Name nhập text bất kỳ không trống; format thuộc Company Registration Number (DN-03 thu hồi) |
| **AMB-24** | Limited: "Are you VAT Registered?" = **Yes** điều hướng đi đâu | ✅ **Personal information bản Limited** + thêm VAT Number & Your VAT Certificate (upload) |
| **AMB-25** | Required-status field Limited Personal info (Name of bank, Sort code) | ✅ **Required** (bỏ trống → "Please enter here") |
| **AMB-26** | Tab Health Questionnaire & Declarations ở Limited có giống CIS không | ✅ **Giống CIS** |
| **AMB-27** | Field "Company trading name" (M04·LTD) có pre-filled không hay nhập mới | ✅ **Pre-filled** từ M-LTD-02 Company Trading Name |
| **AMB-28** | Accepted file types (VAT Certificate) | ✅ **png, jpg, jpeg, doc, docx, pdf** (màn National Insurance Document "doc/docx/pdf" — ngoài scope) |
| **AMB-29** | Limited: màn **Welcome** là Riddingtons hay Industrial Labour LTD | ✅ **Welcome = "Industrial Labour LTD"** (đúng); WPS text "Riddingtons" là **UI bug DN-04** |
| **AMB-30** | Limited: **Declarations** giống CIS không | ✅ **Giống CIS**; provider = Industrial Labour; verbatim tên trong link/checkbox **không cần quan tâm** (QA) |
| **AMB-31** | Umbrella: Health Questionnaire & Declarations có giống CIS không | ✅ **Giống CIS** (provider Declarations = Riddingtons) |
| **AMB-N01** | Normal: required-status Select site location / Select subcontractor | ⏸ **không cần quan tâm** (QA) |
| **AMB-N02** | Normal: hành vi checkbox "I can't find my subcontractor in the list" | ⏸ **không cần quan tâm** (QA) |

---

## 11. Câu hỏi chờ chốt (Open items)

> ✅ **Đã chốt (2026-06-22):** gần như toàn bộ AMB (xem §10). Submission thành công = popup **"Your check has been submitted!"**. NIN error đã có verbatim. Provider Limited = Industrial Labour.

### Còn lại (nhỏ, không chặn requirements)
| # | Nội dung | Trạng thái |
|---|---|---|
| Q8 | Chọn "Not Applicable" ở Skill card type → submission chấp nhận không skill card? | ⏸ **không cần quan tâm** (QA) |
| Q9 | Back / thoát rồi quay lại → dữ liệu giữ hay reset? | ✅ **RESET** (khác DTSource) — BR-39 |
| Q10 | URL đích thật của 3 link policy (mở browser ngoài) | 🔜 QA tự thêm khi có link |
| — | Chi tiết các bước **sau** khi chọn Skill card type (vd field "Your skill card…") | 🔜 vòng sau |

### Bug → báo Design/BA
| # | Nội dung | Ưu tiên |
|---|---|---|
| **DN-04** | Limited: Worker Payment Status hiển thị **SAI** *"Payroll Provider is Riddingtons"* — provider đúng = **Industrial Labour** (khớp Welcome "Industrial Labour LTD"). Cần sửa text WPS | **High** |
