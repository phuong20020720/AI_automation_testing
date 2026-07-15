# Requirements Document — DTSource RAIL Worker Onboarding

> **Hệ thống:** PPAC Mobile — Worker Onboarding
> **Module:** DTSource (`dt • source`) · Sector = **Rail** — journey 8 form
> **Nguồn design:** Figma "[NEW] PPAC New Design 2025" — file `ZzysHnguFvUu15zJyg6rjy`, section DTSource `13380:5796` (đã verify 2026-06-17)
> **Phương pháp:** AI-RBT (FULL) — ANALYZE design + đối chiếu Ballycommon Rail làm baseline
> **Ngày tạo:** 2026-06-17
> **QA Owner:** maya.do@ppac.co.uk
> **Baseline tham chiếu:** [[requirements_ballycommon_rail_onboarding]] — DTSource là một contractor mới dùng lại journey Rail, có khác biệt được liệt kê ở mục 9.

---

## 1. Tổng quan

Worker truy cập onboarding link, nhập **company prefix "DTSOURCE"** rồi chọn **Rail** (hoặc Construction — tài liệu này khoanh vùng **Rail**). Hệ thống thu thập thông tin worker qua chuỗi 8 form. Một phần dữ liệu nhân thân (**Surname, Email, Date of Birth**) đã được thu thập & validate ở bước **"Verify your identity"** trước đó nên hiển thị **pre-filled** ở form Your Details (ngoài scope test của tài liệu này).

Journey đầy đủ **8 form** (Rail):

```
Prefix (DTSOURCE) → Select Rail → Your Details → References → Medical Self-Certification
→ Contract of Sentinel Scheme Sponsorship → PPE → Safety Critical Certifications (tab "Pre-Deployment")
→ Lost & Stolen Sentinel Cards → Declaration (Privacy & Submit) → Submit
```

**Khác Ballycommon ở điểm cốt lõi:** DTSource **KHÔNG có field Payroll provider** (không phân nhánh CIS/PAYE) — mọi worker Rail đi cùng một journey 8 form.

## 2. Actor

- **Worker** (ứng viên đăng ký onboarding) — đã qua bước Verify identity (có sẵn Surname/Email/DOB).

## 3. Phụ thuộc (Dependencies)

- **Infinity API** — Consultant, Trade, Qualification, "Where did you hear about us?" (populate sheet chọn).
- **Address autocomplete service** — gợi ý Street address ("Type & select from suggestion").
- Worker Onboarding Service · Validation Service · Form Submission Service · Declaration Management Service.
- **Verify Identity Service** — cung cấp Surname/Email/DOB pre-filled.

---

## 4. Đặc tả màn hình (Design-grounded)

### 4.1 Prefix / Select Sector — *(mirror Ballycommon, chưa có screenshot DTSource — ⚠️ AMB-06)*
- Nhập company prefix "DTSOURCE" → chọn **Rail** / **Construction**. Tài liệu chọn **Rail**.

### 4.2 Your Details (tab "Onboarding Details") — `M02`
Tiêu đề **"Your Details"**. Stepper: Onboarding Details · References · Medical Self-Certification.

| Nhóm | Field | Loại | Ràng buộc |
|---|---|---|---|
| Personal | First name | text | **pre-filled** — ngoài scope |
| Personal | Surname | text | **pre-filled** — ngoài scope (lỗi minh hoạ: *"Please fill in this field"*) |
| Personal | **Consultant** | sheet single-select + Search + CheckCircle (Infinity API) | **required** |
| Personal | **Trade** | sheet **multi-select** (checkbox + Search + **Done**) → tag ✕ | **required** |
| Personal | **Qualification** | sheet **multi-select** ("Search your qualification" + Done) → tag ✕ | **required** |
| Personal | Email | text | **pre-filled** — ngoài scope (lỗi minh hoạ: *"Please enter a valid email address"*) |
| Personal | **Candidate's Mobile Phone** | text | **required**; phone format → *"Please enter a valid phone number"* |
| Address | **Street address** | autocomplete ("Type & select from suggestion") | **required** |
| Address | Address line 2 | text ("Door, Flat, House Name") | **OPTIONAL** |
| Address | **City** | text | **required** |
| Address | **ZIP / Postcode** | text | **required** |
| Personal | **National Insurance Number** | text | **required**; format `AA999999A` → *"Please enter the right format: 2 letters, 6 numbers, 1 letter (e.g. AA999999A)"* |
| Personal | **Sentinel Number** | text | **required** (Rail) |
| Personal | Date of Birth | date picker | **pre-filled từ Verify identity** — ngoài scope (gate 16+: *"Only individuals aged 16 or above are eligible to register."*) |
| Personal | **Where did you hear about us?** | sheet single-select + Search + CheckCircle | **required** |
| Next of Kin | **Next of Kin Name** | text | **required**; helper *"Please provide details of someone we can contact in case of emergency."* |
| Next of Kin | **Relationship to Candidate** | text (placeholder *"e.g. Parent, Spouse, Sibling"*) | **required** |
| Next of Kin | **Contact Phone Number** | text | **required**; phone format → *"Please enter a valid phone number"* |

→ nút **"Next →"**. **KHÔNG có field Payroll provider.**

### 4.3 References (tab "References") — `M03`
- Intro 3 đoạn (verbatim — khớp Ballycommon): *"You must provide the details of someone who is prepared to provide a reference for you."* · **(bold)** *"Please note: References provided must be a company reference from your previous contractor/employer supervisor or manager. No Agency references please."* · *"All references will be checked verbally. **Failure to provide legitimate reference information could delay approval of your application.**"*
- **Referee 1 & Referee 2** — mỗi khối **4 field GIỐNG NHAU, đều required**: **Name** · **Contact Number** · **Contractor/Company Name** · **Project/Site you worked on**.
- **KHÔNG có** Reference Type (Personal/Employer), **KHÔNG có** Relationship, **KHÔNG tách** First/Surname (khác Ballycommon).
- Lỗi: Name trống → *"Please fill in this field"*; phone sai → *"Please enter a valid phone number"*.

### 4.4 Medical Self-Certification (tab "Medical Self-Certification") — `M04`
- Intro railway (verbatim — khớp Ballycommon, xem mục 5).
- **12 câu hỏi Yes/No** (xem mục 5), **mỗi câu bắt buộc** → *"Please answer this question"*.
- **Toggle xác nhận** (switch — không phải checkbox): **"I confirm that I have selected 'NO' to all of the medical self-certification declarations above."** — chỉ hiện khi **TẤT CẢ 12 = NO** (BR-04); **ẩn nếu có ≥1 YES** (BR-05). Khi all-NO, chưa bật → *"Please confirm this to continue."*

### 4.5 Contract of Sentinel Scheme Sponsorship (tab "Sentinel Scheme Contract") — `M05`
- Hợp đồng cuộn, **5 section đánh số**: 1. Duties · 2. Candidate Responsibilities · 3. Primary Sponsor Responsibilities · 4. Misconduct · 5. Withdrawal of Sentinel Competence Cards; + section cuối **Sentinel Scheme Declaration**. Sponsor trong text = **"Dt Source"**.
- Checkbox bắt buộc: **"I have read, and agree to be bound by, the above Contract of Sentinel Scheme Sponsorship."** Chưa tick → *"Please review and confirm this to continue."*. Không có e-signature.

### 4.6 PPE (tab "PPE") — `M06`
- Intro (verbatim): *"Please indicate below which items of Personal Protective Equipment (PPE) you are in possession of."*
- **9 item Yes/No, mỗi item bắt buộc**: Safety Shoes / Boots · Bump / Hard Hat · H.V. Vests · H.V. Clothing · Ear Protection · Eye Protection · Respiratory Equipment · Overalls · Gloves. Bỏ trống → *"Please answer this question"*.

### 4.7 Safety Critical Certifications (tab **"Pre-Deployment"**) — `M07`
- ⚠️ Tab tên **"Pre-Deployment"**, tiêu đề màn = **"Safety Critical Certifications"** (AMB-04).
- Gate: **"Are you subject to any medical restrictions?"** (Yes/No, bắt buộc).
  - Gate = **Yes** → hiện field **"Please enter details of medical restrictions"** (placeholder *"Please give us more details."*, helper *"Please be as specific as possible"*) — **required**.
- Heading: **"Please provide below details of all competency restrictions"**.
- **16 cert Yes/No, mỗi cert bắt buộc**: PTS AC · PTS DCCR · AOD PO · AOD LXA · COSS · LKT/SW · Level A · IWA · PC · PS · ES · MC/CC · LB 3rd – R ST-i · DLR Track Awareness · PICOW · Other.
- Cert = **Yes** → hiện field **"[Cert] - Duration Held (Years & Months)"** (placeholder *"e.g. 2 years 3 months"*) — **required**, trống → *"Please enter the duration"*.
- **"Other" = Yes** → hiện field **"Other competencies"** (free text, placeholder *"Please give us more details."*).

### 4.8 Lost & Stolen Sentinel Cards (tab "Lost Sentinel Cards") — `M08`
- Tiêu đề: **"Lost & Stolen Sentinel Cards"**.
- Toggle bắt buộc: **"I confirm that I will pay £25 + VAT if my Sentinel card is lost or stolen to Ballycommon"** (🐞 **DEF-01** — sai tên công ty, phải là "Dt Source").

### 4.9 Declaration — "Privacy & Submit" (tab "Declaration") — `M09`
- Tiêu đề: **"Privacy & Submit"**. **4 toggle, TẤT CẢ bắt buộc**:
  1. **Declaration** — *"I hereby declare that all of the information entered on this form is true and accurate to the best of my knowledge and belief"*
  2. **Consent** — *"I agree that my personal data may be processed in accordance with the **DT Source Privacy Policy**"* (link gạch chân)
  3. **Marketing** — *"I agree that my personal data may be shared with selected partners for marketing purposes as described in the **DT Source Privacy Policy**"* (link gạch chân) — **bắt buộc** (đã chốt; lưu ý khác chuẩn GDPR opt-in — xem AMB-05)
  4. **Initial Terms** — *"I agree to the initial Terms and Work Finding Agreement"*
- Toggle bắt buộc chưa bật → *"Please review and confirm this to continue."*
- Nút **"Next →"** = hành động **Submit onboarding** (AMB-07).

---

## 5. Dữ liệu nội dung verbatim (Content reference)

**Medical Self-Certification — 12 câu hỏi:**
1. Do you have Diabetes needing insulin?
2. Do you suffer from Epilepsy or fits?
3. Have you ever had blackouts, recurrent dizziness or any condition which may cause sudden collapse or incapacity?
4. Do you get discomfort or pain in the chest or shortness of breath on exercise e.g. climbing a single flight of stairs?
5. Do you have difficulty in moving rapidly over short distances, including on slopes, stairs or rough ground?
6. Would you have difficulty in looking over your shoulder?
7. Do you have any difficulty with your eyesight (simple problems, needing glasses need not be included)?
8. Do you have any difficulty hearing normal conversations?
9. Are you taking any medication that is giving you dizziness or drowsiness?
10. Have you used drugs of abuse within the last 12 months?
11. Have you had any alcohol-related illness during the last 12 months?
12. Have you experienced any Hand/Arm problems from operating vibrating equipment?*

**Safety Critical — 16 cert:** PTS AC · PTS DCCR · AOD PO · AOD LXA · COSS · LKT/SW · Level A · IWA · PC · PS · ES · MC/CC · LB 3rd – R ST-i · DLR Track Awareness · PICOW · Other.

**PPE — 9 item:** Safety Shoes / Boots · Bump / Hard Hat · H.V. Vests · H.V. Clothing · Ear Protection · Eye Protection · Respiratory Equipment · Overalls · Gloves.

**Declaration — 4 toggle:** Declaration (truth) · Consent (data processing) · Marketing (data sharing) · Initial Terms (Terms and Work Finding Agreement).

---

## 6. Business Rules (DTSource Rail)

| ID | Rule |
|---|---|
| BR-01 | Worker nhập prefix DTSOURCE + chọn Rail trước khi vào journey |
| BR-02 | **KHÔNG có Payroll provider** — mọi worker Rail đi cùng journey 8 form |
| BR-03 | Sentinel Number bắt buộc (Rail) |
| BR-04/05 | Medical: toggle confirmation chỉ hiện khi **tất cả 12 = NO**; ẩn nếu **có ≥1 YES** |
| BR-06 | Medical: phải trả lời **đủ 12 câu** mới Next |
| BR-07 | References: đúng 2 referee, **mỗi referee đủ 4 field** (8 field bắt buộc) |
| BR-08 | Consultant/Trade/Qualification/"Where hear about us" lấy từ Infinity API |
| BR-09 | Safety Critical: gate=Yes → "details of medical restrictions" required; cert=Yes → "[Cert] - Duration Held" required |
| BR-10 | Contract phải được chấp nhận (checkbox) trước khi tiếp |
| BR-11 | Lost & Stolen: toggle £25+VAT phải bật |
| BR-12 | Declaration: **cả 4 toggle bắt buộc** (gồm Marketing) trước khi Submit |
| BR-13 | Surname/Email/DOB pre-filled từ Verify identity — không nhập lại ở Your Details |
| BR-14 | Worker phải ≥ 16 tuổi (kiểm ở bước Verify identity) |

## 7. Validation (in-scope DTSource Rail)

VAL-01 Your Details required (Consultant · Trade · Qualification · Candidate's Mobile Phone · Street address · City · Postcode · NIN · Sentinel · Where hear · Next of Kin Name/Relationship/Contact Phone) — Address line 2 **optional** · VAL-02 NIN format `AA999999A` · VAL-03 Phone format (Candidate's Mobile, Next of Kin, Referee) · VAL-04 References 8 field required · VAL-05 Medical đủ 12 câu + toggle khi all-NO · VAL-06 Contract checkbox · VAL-07 PPE đủ 9 item · VAL-08 Safety gate + Duration (conditional required) · VAL-09 Lost & Stolen toggle · VAL-10 Declaration 4 toggle.

---

## 8. Defects & Design Notes phát hiện

| ID | Loại | Mô tả | Ưu tiên |
|---|---|---|---|
| **DEF-01** | 🐞 Bug nội dung | M08 Lost & Stolen ghi *"…lost or stolen to **Ballycommon**"* — sai tên công ty, đúng phải **"Dt Source"** (Figma node `14739:14073`) | High |
| **DEF-02** | 🐞 Chính tả US→UK | M05 Contract mục Duties: *"…given by an **authorized** Dt Source representative…"* — phải **"authorised"** (UK). Kèm *"is first **sort**"* → nhiều khả năng "sought" (Figma `14739:14898`/`14829:8754`/`14829:8848`) | High |
| **DEF-03** | 🐞 Bug nội dung | M01 Prefix option hiển thị *"**BALLYCOMMON** - RAIL / - CONSTRUCTION"* — phải *"DTSOURCE - …"* (Figma `14739:19250`/`14739:19249`) | High |
| **DEF-04** | 🐞 Bug nội dung | M01 dialog onboarding dở: *"…existing onboarding for **Ballycommon - Rail**…"* — phải "Dt Source" | Medium |
| **DN-01** | Chính tả (chờ design update) | M02 *"**Next to** Kin Details"* → đúng *"Next **of** Kin"* | — |
| **DN-02** | Chính tả (chờ design update) | M02 *"**Adress"** (ô Street address state focus) → "Address" | — |
| **DN-03** | Chính tả (chờ design update) | M02 tag *"Paediatric (**Childen**)"* → "Children" | — |
| **DN-04** | Brand-casing nhất quán | Logo *"dt • source"* vs Contract *"Dt Source"* vs Declaration *"DT Source"* — nên thống nhất 1 dạng | Low |

> **Phân loại theo yêu cầu QA (2026-06-17):** DEF-01/03/04 = branding "Ballycommon" còn sót (rebrand chưa xong) → **do team Design phụ trách**, **KHÔNG tạo test case** (chỉ ghi nhận findings ở đây để báo Design). **DEF-02** (chính tả "authorise") có test case riêng **TC_153/TC_154** (vì QA yêu cầu kiểm tra spelling này).
> **DN-xx** là lỗi chính tả **chờ design update** → không viết TC dựa trên text sai; sau update verify lại (TC_151).

## 9. Khác biệt DTSource vs Ballycommon (đối chiếu baseline)

| Hạng mục | Ballycommon Rail | **DTSource Rail** |
|---|---|---|
| Payroll provider | Có (CWG-CIS / Workwell-PAYE) | ❌ **Không có** |
| Your Details — Surname/Email/DOB | nhập & validate tại form | **pre-filled** từ Verify identity (ngoài scope) |
| References — Type/Relationship | có Type (Personal/Employer) + Relationship + First/Surname + conditional fields | ❌ bỏ hết; chỉ **Name + Contact Number + Contractor/Company + Project/Site** (4 field cố định, luôn hiện) |
| Medical confirmation | checkbox vuông | **toggle switch** (logic giữ nguyên: all-NO → hiện) |
| Safety Critical | tab "Safety Critical Certifications" | tab đổi tên **"Pre-Deployment"** (nội dung như cũ) |
| Lost & Stolen | "…to Ballycommon" (đúng) | "…to Ballycommon" (🐞 **sai** — phải "Dt Source") |
| Declaration cuối | **11 mục** cam kết (fitness/drug/GDPR…) | **4 toggle Privacy & Submit** (Declaration/Consent/Marketing/Initial Terms) |

## 10. Bảng Ambiguity (đã chốt trong hội thoại — 2026-06-17)

| ID | Vấn đề | Quyết định |
|---|---|---|
| **AMB-01** | DTSource có Payroll provider? | **Không** (đã chốt) → bỏ toàn bộ TC payroll/journey branching |
| **AMB-02** | DOB nhập ở đâu | **Pre-filled từ "Verify your identity"** → ngoài scope Your Details |
| **AMB-03** | Surname/Email có test ở Your Details? | **Không** — pre-filled, đã validate ở bước trước |
| **AMB-04** | Tab "Pre-Deployment" là gì | **≡ Safety Critical Certifications** (tab đổi tên) |
| **AMB-05** | Marketing toggle required? | **Required** (đã chốt) — lưu ý lệch chuẩn GDPR opt-in, BA nên review |
| **AMB-06** | Text màn Prefix DTSource | Chưa có screenshot → TC M01 theo baseline Ballycommon, ⚠️ verify text "DTSOURCE - RAIL" |
| **AMB-07** | Nút cuối "Next →" hay "Submit" | UI nhãn **"Next →"**, hành vi = Submit onboarding |
| **AMB-08** | Figma node-id | Chờ Figma link → cột "Figma Ref" tạm dùng mã màn (M02…), backfill sau |
