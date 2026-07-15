# Requirements Document — Ballycommon RAIL Worker Onboarding (Workwell / PAYE)

> **Hệ thống:** PPAC Mobile — Worker Onboarding
> **Module:** Ballycommon · Sector = **Rail** · Payroll = **Workwell Contractor Solutions (PAYE)** — journey đầy đủ 8 form
> **Nguồn design:** Figma "[NEW] PPAC New Design 2025" — frame `BALLYCOMMON - RAIL` (`13116:4231`)
> **Nguồn requirements gốc:** `input/app/requirements/Ballycomon.md`
> **Phương pháp:** ANALYZE (chuẩn hóa requirements từ design + đối chiếu requirements gốc)
> **Ngày tạo:** 2026-06-02
> **QA Owner:** harry.vo@ppac.co.uk

---

## 1. Tổng quan

Worker truy cập onboarding link Ballycommon, nhập **company prefix "CORE - RAIL"** để vào sector Rail. Hệ thống thu thập thông tin worker qua chuỗi form, hiển thị form khác nhau theo **Sector** (Rail/Construction) và **Payroll Company** (CWG-CIS / Workwell-PAYE).

Tài liệu này khoanh vùng **Rail + Workwell (PAYE)** — journey đầy đủ **8 form**:

```
Prefix (CORE-RAIL) → Your Details → References → Medical Self-Certification
→ Contract of Sentinel Scheme Sponsorship → PPE → Safety Critical Certifications
→ Lost & Stolen Sentinel Cards → Declaration → Submit
```

## 2. Actor

- **Worker** (ứng viên đăng ký onboarding).

## 3. Phụ thuộc (Dependencies)

- Infinity API — Consultant, Trade, Qualification (populate dropdown).
- Worker Onboarding Service · Validation Service · Payroll Provider Configuration Service · Form Submission Service · Declaration Management Service.

---

## 4. Đặc tả màn hình (Design-grounded)

### 4.1 Prefix / Welcome — `13185:80335`
- Màn "Welcome to PPAC, [User]!" → "Enter Company Name".
- Nhập prefix → badge **"CORE - RAIL"** → nút **"Continue →"**.
- Nếu có onboarding dở dang: dialog **"Continue onboarding?"** — *"You have an existing onboarding for Ballycommon - Rail. Do you want to continue?"* → **"Continue"** (về màn đang dở) / **"Start a new onboarding!"** (làm lại từ đầu).

### 4.2 Your Details — `13258:107339`
| Nhóm | Field | Loại | Ràng buộc |
|---|---|---|---|
| Personal | First name, Surname | text | Surname required → *"Please fill in this field"* |
| Personal | Consultant | dropdown (Infinity API) | required → *"Please select from the list"* |
| Personal | Trade | dropdown (Infinity API) | — |
| Personal | Qualification | dropdown (Infinity API, **multi-select tag**) | — |
| Personal | Email | text | format → *"Please enter a valid email address"* |
| Personal | Candidate's Mobile Phone | text | — |
| Personal | National Insurance Number | text | format `AA999999A` → *"Please enter the right format: 2 letters, 6 numbers, 1 letter (e.g. AA999999A)"* |
| Personal | **Sentinel Number** | text | **Rail-only**; required (BR-03/VAL-03) — *xem AMB-01* |
| Personal | Date of Birth | date picker | ≥ 16 tuổi → *"Only individuals aged 16 or above are eligible to register."* |
| Personal | Where did you hear about us? | dropdown | design-only |
| Address | Address, City, Postcode | text | required |
| Next of Kin | First name, Surname, Relationship to Candidate, Contact Phone Number | text | phone format → *"Please enter a valid phone number"*; helper *"Please provide details of someone we can contact in case of emergency."* |
| Payroll | Please select your preferred payroll provider | dropdown | options: **CWG (Provider for CIS)** / **Workwell Contractor Solutions (Provider for PAYE)** — chọn Workwell |

→ nút **"Next →"**.

### 4.3 References — `13177:284822`
- Intro: *"You must provide the details of someone who is prepared to provide a reference for you. … No Agency references please. All references will be checked verbally…"*
- **Referee 1 & Referee 2** (đúng 2 — BR-08), mỗi referee: **Type** (dropdown: *"Personal referee"* / *"Employer referee"* — *xem AMB-02*), First Name, Surname, Contact Number, Relationship to Candidate.
- Lỗi: Type bỏ trống → *"Please select here"*; phone sai → *"Please enter a valid phone number"*.

### 4.4 Medical Self-Certification — `13185:32501`
- Intro railway: *"Alertness and reasonable physical fitness are essential for duties which may interact with moving trains…"*
- **12 câu hỏi Yes/No** (xem mục 5 — danh sách đầy đủ).
- Checkbox xác nhận **"I confirm that I have selected 'NO' to all of the medical self-certification declarations above."** — chỉ hiện khi **TẤT CẢ = NO** (BR-14); ẩn nếu **có ≥1 YES** (BR-15).
- Câu chưa trả lời → *"Please answer this question"* (EF-07 / VAL-09).

### 4.5 Contract of Sentinel Scheme Sponsorship — `13185:52062`
- Hợp đồng cuộn (Duties, Candidate/Sponsor Responsibilities, Misconduct, Withdrawal of Sentinel Competence Cards, Sentinel Scheme Declaration).
- Checkbox bắt buộc: **"I have read, and agree to be bound by, the above Contract of Sentinel Scheme Sponsorship."** (VAL-11). Không có e-signature.

### 4.6 PPE — `13185:52785`
- Intro: *"Please indicate below which items of Personal Protective Equipment (PPE) you are in possession of."*
- **9 item Yes/No**: Safety Shoes/Boots, Bump/Hard Hat, H.V. Vests, H.V. Clothing, Ear Protection, Eye Protection, Respiratory Equipment, Overalls, Gloves. Bỏ trống → *"Please answer this question"*.

### 4.7 Safety Critical Certifications — `13185:53196`
- Câu gate: **"Are you subject to any medical restrictions?"** (Yes/No).
- Danh sách chứng chỉ Yes/No: PTS AC, PTS DCCR, AOD PO, AOD LXA, COSS, LKT/SW, Level A, IWA, PC, PS, ES, MC/CC, LB 3rd – R ST-i, DLR Track Awareness, PICOW, Other.
- Khi điều kiện kích hoạt → hiện field **"… - Duration Held (Years & Months)"** (placeholder *"e.g. 2 years 3 months"*) + **"Other competencies"** (BR-16 / AF-05/06 — *xem AMB-03*).

### 4.8 Lost & Stolen Sentinel Cards — `13185:72971`
- Checkbox bắt buộc: **"I confirm that I will pay £25 + VAT if my Sentinel card is lost or stolen to Ballycommon"** (BR-17 / VAL-12).

### 4.9 Declaration (cuối) — `13359:8298`
- **12 mục declaration** (xem mục 5). Mục GDPR dài là đoạn thông tin, các mục còn lại là checkbox cam kết (BR-18 / VAL-13).
- Nút **"Next →"** (design) = hành động **Submit onboarding** (req) — *xem AMB-08*.

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
12. Have you experienced any Hand/Arm problems from operating vibrating equipment?

**Declaration — 12 mục:**
1. I declare that the contents of this application form are true.
2. I authorize Ballycommon to contact my past employers for references.
3. I am fit, well and able to undertake manual work on building, M & E, civil & railway engineering contracts.
4. I authorize the deduction of equipment, services or training costs from my payments.
5. I am not suffering from any occupational illness that could affect my ability to perform my duties.
6. I have never been dismissed from any company for being under the influence of drugs or alcohol.
7. I am willing to undertake a drugs and/or alcohol test at any time if requested by Ballycommon or the client.
8. I will inform Ballycommon if I am taking any medication that may affect my ability to undertake my duties.
9. I will inform Ballycommon of any hours worked for other employers and will comply with the relevant industry standards relating to working hours.
10. I will inform Ballycommon of any changes to my medical condition or my fitness for work.
11. *(Thông tin GDPR)* As part of our recruitment and compliance processes, Ballycommon may need to share your personal information with clients, payroll providers, and other authorised third-party partners…
12. I consent to Ballycommon sharing my personal data with their clients and other relevant third parties involved in the recruitment and compliance process…

---

## 6. Business Rules áp dụng (Rail + Workwell)

| ID | Rule |
|---|---|
| BR-01 | Worker phải chọn Sector (nhập prefix CORE-RAIL) trước khi tiếp tục |
| BR-02/03/04 | Rail hiển thị Sentinel Number; Sentinel Number bắt buộc khi Rail; Construction không hiển thị |
| BR-05 | Worker phải chọn Payroll Company |
| BR-07 | Workwell yêu cầu toàn bộ 8 form PAYE |
| BR-08/09 | Đúng 2 References; mỗi reference thuộc loại Personal/Employer |
| BR-10/11/12 | Consultant/Trade/Qualification lấy từ Infinity API |
| BR-13 | Medical Self-Cert yêu cầu trả lời tất cả câu hỏi |
| BR-14/15 | Confirmation checkbox chỉ hiện khi tất cả NO; ẩn nếu có ≥1 YES |
| BR-16 | Additional Pre-Deployment/Duration fields chỉ hiện khi điều kiện Safety Critical = YES |
| BR-17 | Lost Sentinel Card Declaration phải được chấp nhận |
| BR-18 | Final Declaration phải được chấp nhận trước khi submit |
| BR-19 | Worker phải ≥ 16 tuổi |

## 7. Validation

VAL-01 Your Details required · VAL-02 References required · VAL-03 Sentinel required (Rail) · VAL-04 Email format · VAL-05 NIN `AA999999A` · VAL-06 ≥16 tuổi · VAL-07 Referee Type required · VAL-08 Referee phone format · VAL-09 Medical đủ câu · VAL-10 Declaration checkbox · VAL-11 Contract checkbox · VAL-12 Lost Sentinel checkbox · VAL-13 Final Declaration checkbox.

---

## 8. Bảng đối chiếu Figma ↔ Requirements & Ambiguities

| ID | Requirements | Figma | Quyết định / Giả định (cần verify) |
|---|---|---|---|
| **AMB-01** | Sentinel Number **bắt buộc** (BR-03) | Field **không có dấu \*** trong design | **Giả định: required** (business rule thắng UI). Test cả empty→error. ⚠️ Verify với dev. |
| **AMB-02** | Reference type = Personal / **Company** | Figma: Personal referee / **Employer referee** | **Dùng nhãn Figma** cho UI check. ⚠️ Verify nhãn chuẩn. |
| **AMB-03** | Additional fields khi **Safety Critical = YES** | Gate là **"Are you subject to any medical restrictions?"** + cert list riêng | **Giả định: trả lời YES (gate) → hiện Duration fields**. ⚠️ Verify trigger chính xác. |
| **AMB-04** | MF-05 có "Pre-Deployment Form" | Không có màn tên "Pre-Deployment"; gần nhất = **Safety Critical Certifications** | **Giả định: Pre-Deployment ≡ Safety Critical Certifications**. |
| **AMB-05** | Req không liệt kê cert | Figma có 16 loại cert | Test theo nhóm (≥1, tất cả), không test từng dòng riêng. |
| **AMB-06** | CWG=3 form, Workwell=8 form | Frame RAIL = full Workwell | **Scope đã chốt: Rail + Workwell (8 form)**. |
| **AMB-07** | Req không nêu | "Where did you hear about us?", Qualification multi-select, DOB | Đưa vào scope (design-grounded). |
| **AMB-08** | Kết thúc = "Submit onboarding" | Nút Declaration = **"Next →"** | UI check theo nhãn "Next →"; hành vi = submit + lưu (AC-14). ⚠️ Verify nhãn nút cuối. |

> Các điểm ⚠️ là **gap cần dev/BA xác nhận**; test case đã thiết kế theo giả định an toàn nhất và đánh dấu để re-verify.
