# Requirements Document — DTSource CONSTRUCTION Worker Onboarding

> **Hệ thống:** PPAC Mobile — Worker Onboarding
> **Module:** DTSource (`dt • source`) · Sector = **Construction** — journey **3 form**
> **Nguồn design:** Figma "[NEW] PPAC New Design 2025" — file `ZzysHnguFvUu15zJyg6rjy`, section DTSource `13380:5796` (đã verify 2026-06-17)
> **Phương pháp:** AI-RBT (FULL) — ANALYZE design + đối chiếu DTSource Rail làm baseline
> **Ngày tạo:** 2026-06-17
> **QA Owner:** maya.do@ppac.co.uk
> **Baseline tham chiếu:** [[requirements_dtsource_rail_onboarding]] — Construction là **subset** của Rail (bỏ các form gắn đường sắt/Sentinel).

---

## 1. Tổng quan

Worker nhập **company prefix "DTSOURCE"** rồi chọn **Construction**. Một phần dữ liệu nhân thân (**Surname, Email, Date of Birth**) đã thu thập & validate ở bước **"Verify your identity"** trước đó → hiển thị pre-filled ở Your Details (ngoài scope).

Journey Construction **chỉ 3 form** (stepper: Onboarding Details · References · Declaration):

```
Prefix (DTSOURCE) → Select Construction → Your Details → References → Declaration (Privacy & Submit) → Submit
```

**Khác Rail:** Construction **bỏ hết 5 form gắn đường sắt/Sentinel** — Medical Self-Certification, Contract of Sentinel Scheme Sponsorship, PPE, Safety Critical Certifications (Pre-Deployment), Lost & Stolen Sentinel Cards. Và **không có** field Payroll provider.

## 2. Actor

- **Worker** (ứng viên Construction) — đã qua Verify identity (có sẵn Surname/Email/DOB).

## 3. Phụ thuộc (Dependencies)

- **Infinity API** — Consultant, Trade, Qualification, "Where did you hear about us?".
- **Address autocomplete service** — gợi ý Street address.
- Worker Onboarding Service · Validation Service · Form Submission Service · Declaration Management Service · Verify Identity Service.

---

## 4. Đặc tả màn hình (Design-grounded)

### 4.1 Prefix / Select Sector — *(mirror Rail, ⚠️ AMB-06)*
- Nhập prefix "DTSOURCE" → chọn **Construction**.

### 4.2 Your Details (tab "Onboarding Details") — `M02`
Tiêu đề **"Your Details"**. **Giống Your Details của Rail NHƯNG bỏ field Sentinel Number** (rail-only).

| Nhóm | Field | Loại | Ràng buộc |
|---|---|---|---|
| Personal | First name | text | **pre-filled** — ngoài scope |
| Personal | Surname | text | **pre-filled** — ngoài scope |
| Personal | **Consultant** | sheet single-select + Search + CheckCircle (Infinity API) | **required** |
| Personal | **Trade** | sheet **multi-select** (checkbox + Search + Done) → tag ✕ | **required** |
| Personal | **Qualification** | sheet **multi-select** (Search + Done) → tag ✕ | **required** |
| Personal | Email | text | **pre-filled** — ngoài scope |
| Personal | **Candidate's Mobile Phone** | text | **required**; phone → *"Please enter a valid phone number"* |
| Address | **Street address** | autocomplete ("Type & select from suggestion") | **required** |
| Address | Address line 2 | text ("Door, Flat, House Name") | **OPTIONAL** |
| Address | **City** | text | **required** |
| Address | **ZIP / Postcode** | text | **required** |
| Personal | **National Insurance Number** | text | **required**; format `AA999999A` → *"Please enter the right format: 2 letters, 6 numbers, 1 letter (e.g. AA999999A)"* |
| Personal | Date of Birth | date picker | **pre-filled từ Verify identity** — ngoài scope |
| Personal | **Where did you hear about us?** | sheet single-select + Search + CheckCircle | **required** |
| Next of Kin | **Next of Kin Name** | text | **required**; helper *"Please provide details of someone we can contact in case of emergency."* |
| Next of Kin | **Relationship to Candidate** | text (placeholder *"e.g. Parent, Spouse, Sibling"*) | **required** |
| Next of Kin | **Contact Phone Number** | text | **required**; phone format → *"Please enter a valid phone number"* |

→ nút **"Next →"**. **KHÔNG có Sentinel Number, KHÔNG có Payroll provider.**

### 4.3 References (tab "References") — `M03`
- Intro 3 đoạn (verbatim — **giống Rail**): *"You must provide the details of someone who is prepared to provide a reference for you."* · **(bold)** *"Please note: References provided must be a company reference from your previous contractor/employer supervisor or manager. No Agency references please."* · *"All references will be checked verbally. **Failure to provide legitimate reference information could delay approval of your application.**"*
- **CHỈ 1 khối "Referee"** (khác Rail — Rail có 2 referee). 4 field **đều required**: **Name** · **Contact Number** · **Contractor/Company Name** · **Project/Site you worked on**.
- Lỗi: Name trống → *"Please fill in this field"*; phone sai → *"Please enter a valid phone number"*.

### 4.4 Declaration — "Privacy & Submit" (tab "Declaration") — `M09`
- **Giống hệt Rail.** Tiêu đề **"Privacy & Submit"**. **4 toggle, TẤT CẢ bắt buộc**:
  1. **Declaration** — *"I hereby declare that all of the information entered on this form is true and accurate to the best of my knowledge and belief"*
  2. **Consent** — *"I agree that my personal data may be processed in accordance with the **DT Source Privacy Policy**"* (link)
  3. **Marketing** — *"I agree that my personal data may be shared with selected partners for marketing purposes as described in the **DT Source Privacy Policy**"* (link) — **bắt buộc** (AMB-05)
  4. **Initial Terms** — *"I agree to the initial Terms and Work Finding Agreement"*
- Toggle bắt buộc chưa bật → *"Please review and confirm this to continue."*
- Nút **"Next →"** = **Submit onboarding** (AMB-07).

---

## 5. Business Rules (DTSource Construction)

| ID | Rule |
|---|---|
| BR-01 | Worker nhập prefix DTSOURCE + chọn Construction |
| BR-02 | **Không có Payroll provider** |
| BR-03 | **Không có Sentinel Number** (rail-only) |
| BR-04 | Journey chỉ 3 form: Your Details → References → Declaration |
| BR-05 | Consultant/Trade/Qualification/"Where hear" lấy từ Infinity API |
| BR-06 | References: **đúng 1 referee**, 4 field bắt buộc |
| BR-07 | Declaration: cả 4 toggle bắt buộc (gồm Marketing) trước Submit |
| BR-08 | Surname/Email/DOB pre-filled từ Verify identity |
| BR-09 | Worker ≥ 16 tuổi (kiểm ở Verify identity) |

## 6. Validation (in-scope)

VAL-01 Your Details required (Consultant · Trade · Qualification · Candidate's Mobile Phone · Street address · City · Postcode · NIN · Where hear · Next of Kin Name/Relationship/Contact Phone) — Address line 2 **optional**, **không có Sentinel** · VAL-02 NIN format `AA999999A` · VAL-03 Phone format · VAL-04 References 4 field required (1 referee) · VAL-05 Declaration 4 toggle.

---

## 7. Design Notes phát hiện

| ID | Loại | Mô tả |
|---|---|---|
| **Branding-01** | Design phụ trách (không TC) | M01 Prefix option hiển thị *"**BALLYCOMMON** - CONSTRUCTION"* — phải *"DTSOURCE - CONSTRUCTION"* (Figma `14739:19249`) |
| **Branding-02** | Design phụ trách (không TC) | M01 dialog onboarding dở dùng "Ballycommon" — phải "Dt Source" |
| **DN-01** | Chính tả (chờ design update) | M02 *"**Next to** Kin Details"* → *"Next **of** Kin"* |
| **DN-02** | Chính tả (chờ design update) | M02 tag *"Paediatric (**Childen**)"* → "Children" |
| **DN-03** | Brand-casing | Logo *"dt • source"* vs Declaration *"DT Source"* — thống nhất |

> Branding "Ballycommon" còn sót ở màn Prefix dùng chung → **do team Design phụ trách, KHÔNG tạo test case** (chỉ ghi nhận findings để báo Design). Declaration dùng "DT Source" đúng. Verify qua Figma `13380:5796` (2026-06-17).

## 8. Khác biệt Construction vs Rail (đối chiếu baseline)

| Hạng mục | DTSource Rail | **DTSource Construction** |
|---|---|---|
| Số form | 8 | **3** |
| Stepper | 8 tab | **Onboarding Details · References · Declaration** |
| Your Details — Sentinel Number | có | ❌ **không** |
| References | 2 Referee (8 field) | **1 Referee (4 field)** |
| Medical / Contract / PPE / Safety Critical / Lost & Stolen | có | ❌ **không** |
| Declaration | 4 toggle | **giống** |
| Payroll provider | không | không |

## 9. Bảng Ambiguity (đã chốt — 2026-06-17)

| ID | Vấn đề | Quyết định |
|---|---|---|
| **AMB-01** | Construction journey | **3 form** (Your Details → References → Declaration) |
| **AMB-02** | Consultant | **Có** (giống Rail) |
| **AMB-03** | Sentinel Number | **Không có** (rail-only) |
| **AMB-04** | References | **1 referee** (4 field, all required) — khác Rail |
| **AMB-05** | Declaration | **Giống Rail** (4 toggle, Marketing required) |
| **AMB-06** | Text màn Prefix DTSource | Chưa có screenshot → M01 theo baseline, ⚠️ verify "DTSOURCE - CONSTRUCTION" |
| **AMB-07** | Nút cuối "Next →" hay "Submit" | UI "Next →", hành vi = Submit |
| **AMB-08** | Figma node-id | Chờ Figma link → cột "Figma Ref" tạm dùng mã màn |
