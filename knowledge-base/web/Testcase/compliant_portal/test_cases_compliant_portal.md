
# PPAC Compliant Portal — Manual Test Cases (Full RBT — Deep Coverage)

> **Hệ thống:** PPAC v2 — Compliant Portal (Verifier-side, UK Compliance)
> **Môi trường:** UAT — `https://ppac-v2-web-uat.prod-verification.compliant101.co.uk/`
> **Tài khoản test:** `ppac.team101@gmail.com` / `Batam.tran123` (Admin role — thấy mọi contractor)
> **Tech stack:** Flutter Web (CanvasKit renderer)
> **Phương pháp:** AI-RBT (Risk-Based Testing) 6 bước — Coverage **Deep RBT**
> **Ngày tạo:** 2026-05-15
> **QA Owner:** harry.vo@ppac.co.uk
> **Tổng số TC:** 202 (Critical 32 / High 106 / Medium 55 / Low 5 + 4 flag-only gap)

---

## Tóm tắt phạm vi

**Trong scope (Deep RBT):**
- M1 Worker Verification Queue (display, KPI counter, navigation)
- M2 Status State Machine (13 status, transition matrix, decision table)
- M3 Filter Bar (Status / Search / Date range / Distinct / Contractors / Clear)
- M4 Worker Detail Modal (verification action, document tabs, edit, upload)
- M5 Expiry Report Export (XLSX, full DB scope)
- M6 Pagination (page-size, navigation, position retention)

**Trong scope (Light Inspection):**
- M11 Multi-tenant Isolation (giới hạn coverage do thiếu verifier-of-contractor account)

**Ngoài scope (Harry skip ở Bước 2):**
- M7 Notification Center
- M8 2-Step Verification setup
- M9 Logout / Session timeout
- M10 Authentication / Login / Forgot Password / Create Account

**Test design techniques áp dụng:**
- **Equivalence Partitioning (EP):** field input M4 + filter M3
- **Boundary Value Analysis (BVA):** date range, max length, page boundary
- **Decision Table:** M2 Submit logic + state transitions
- **State Transition:** M2 13-status workflow matrix
- **Pairwise:** filter combinations M3

---

## Risk Hot-Spots

| Module | Risk Level | Lý do | Test depth |
|---|---|---|---|
| M2 State Machine | 🔴 HIGH | Cross-cutting, 13 status, ảnh hưởng compliance UK | Decision Table + State Transition đầy đủ |
| M4 Worker Detail | 🔴 HIGH | Core verification flow, PII, action bất hồi tố | EP + BVA + Decision Table |
| M3 Filter Bar | 🔴 HIGH | Gateway tới mọi data, dedup logic | EP + Pairwise |
| M5 Expiry Report | 🟡 MEDIUM | Export PII full DB | BVA + Data Integrity |
| M11 Multi-tenant | 🟡 MEDIUM | Security limited do single account | Inspection only |
| M1 Worker Queue | 🟢 LOW-MED | Display, logic dồn về M2/M3 | Standard |
| M6 Pagination | 🟢 LOW | Standard control | Smoke + boundary |

---

## Gap Analysis (cần escalate)

| Gap | Severity | Hành động |
|---|---|---|
| Không có verifier-of-contractor account → không test isolation depth | High (security) | Flag trong report; xin account để test sau |
| Time-based auto-transition (Pending > X ngày → Auto Rejected) bị skip | Medium | Note flag-only ở TC_020/021/025 (M2) |
| Notification/email khi đổi status bị skip | Low | Out-of-scope per Q-A3 |
| Status revert (admin override) bị skip | Low | Out-of-scope per Q-A4 |
| Audit log Edit Worker Info chưa rõ | Medium | Link với F-COMP-02; verify ở TC_054 (M4) |
| AM-1 Search fuzzy/exact, AM-2 Empty state, AM-3 URL params | Low | Resolve bằng exploratory test khi execute |

---

## Test Data Convention

- Format: `auto_<module>_<TC_num>_<timestamp>` (theo `automation_rules.md`)
- Ví dụ: `auto_m2_010_20260515_A3F2@test.com`
- Mọi worker test phải sinh động (UUID/Timestamp), không hardcode

---

# Part 1 — M2 State Machine (32 TC)

## A. Status Definitions (5 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M2_TC_001 | M2.1 | High | Verify đủ 13 status trong Status filter dropdown | Đã login, ở landing page | 1. Click dropdown "Select status" | Dropdown hiển thị đúng 13 option: Active, Go to Site, Pending, Pending (Recheck), Update-Rejected, Rejected, Expired, Pending Info, Auto Rejected, Auto Rejected Archived, Review Required, Waiting to Pass, Archive (không thừa, không thiếu) | Critical | Status list = 13 |
| PPAC_M2_TC_002 | M2.1 | High | Verify mỗi status có badge màu duy nhất (visual distinction) | Có sẵn 1 worker mỗi status (13 worker) | 1. Filter từng status<br>2. Quan sát màu badge cột Status | 13 status có 13 màu/style badge phân biệt được bằng mắt thường | High | 13 worker mỗi status |
| PPAC_M2_TC_003 | M2.1 | High | Verify text status hiển thị giống hệt định nghĩa | Có worker mọi status | 1. Filter từng status<br>2. So sánh text cột Status với spec | Text hiển thị đúng case + dấu câu: "Active", "Go to Site", "Pending", "Pending (Recheck)", "Update-Rejected", "Rejected", "Expired", "Pending Info", "Auto Rejected", "Auto Rejected Archived", "Review Required", "Waiting to Pass", "Archive" | High | 13 worker |
| PPAC_M2_TC_004 | M2.1 | Medium | Verify default landing filter = Pending | Logout → login lại | 1. Login | Filter Status hiển thị "Pending" mặc định, KPI bar hiển thị Pending count | High | account admin |
| PPAC_M2_TC_005 | M2.1 | Medium | Verify status hiển thị trong M4 Header bar khớp cột Status M1 | Worker `worker_M2_005@test.com` status = Active | 1. Filter Active<br>2. Mở row worker này<br>3. Đọc Header Status field | Header Status M4.1 = "Active" (giống cột M1) | Medium | worker_M2_005 status=Active |

## B. Valid Transitions — Verifier-driven (10 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M2_TC_010 | M2.2 | High | Pending → Active (Submit reason rỗng = Approve) | Worker `auto_m2_010_<ts>@test.com` status=Pending, đầy đủ document đạt Regula | 1. Mở worker<br>2. Bỏ trống "If failed, reason"<br>3. Click Submit | Status worker đổi → Active. Quay lại queue thấy worker ở filter Active | Critical | worker status=Pending, regula=passed |
| PPAC_M2_TC_011 | M2.2 | High | Pending → Update-Rejected (Submit reason có nội dung — reject) | Worker `auto_m2_011_<ts>@test.com` status=Pending | 1. Mở worker<br>2. Nhập reason: "Document blurry, please re-upload CSCS card"<br>3. Click Submit | Status đổi → Update-Rejected. Cột Failed Reason hiển thị text vừa nhập | Critical | worker status=Pending |
| PPAC_M2_TC_012 | M2.2 | High | Pending → Pending Info (reason yêu cầu thông tin) | Worker `auto_m2_012_<ts>@test.com` status=Pending | 1. Mở worker<br>2. Nhập reason: "Need NI Number proof, please upload"<br>3. Submit | Status → Pending Info (resolves AM-4 — verify rule phân biệt với Update-Rejected) | Critical | worker status=Pending |
| PPAC_M2_TC_013 | M2.2 | High | Pending (Recheck) → Active | Worker `auto_m2_013_<ts>@test.com` status=Pending (Recheck) | 1. Mở worker<br>2. Reason rỗng<br>3. Submit | Status → Active | High | worker status=Pending (Recheck) |
| PPAC_M2_TC_014 | M2.2 | High | Pending (Recheck) → Update-Rejected (re-reject) | Worker `auto_m2_014_<ts>@test.com` status=Pending (Recheck) | 1. Mở worker<br>2. Reason: "Still invalid"<br>3. Submit | Status → Update-Rejected | High | worker status=Pending (Recheck) |
| PPAC_M2_TC_015 | M2.2 | Medium | Pending (Recheck) → Pending Info | Worker `auto_m2_015_<ts>@test.com` status=Pending (Recheck) | 1. Mở<br>2. Reason: "Need extra Share Code"<br>3. Submit | Status → Pending Info | Medium | worker status=Pending (Recheck) |
| PPAC_M2_TC_016 | M2.2 | High | Active → Go to Site (verifier mark on-site ready) | Worker status=Active, có UI action "Go to Site" | 1. Mở worker Active<br>2. Trigger action Go to Site (cần discover UI) | Status → Go to Site | High | worker status=Active |
| PPAC_M2_TC_017 | M2.2 | High | Go to Site → Active (worker confirmed on-site) | Worker status=Go to Site | 1. Mở<br>2. Trigger transition về Active | Status → Active | High | worker status=Go to Site |
| PPAC_M2_TC_018 | M2.2 | Medium | Active → Archive (verifier archives manually) | Worker status=Active | 1. Mở<br>2. Trigger archive (cần discover UI) | Status → Archive (terminal) | Medium | worker status=Active |
| PPAC_M2_TC_019 | M2.2 | Medium | Update-Rejected → Pending (Recheck) (worker re-uploads via worker app) | Worker status=Update-Rejected, simulate worker upload mới | 1. Trigger upload từ worker side (qua API/DB nếu cần)<br>2. Refresh queue | Status auto → Pending (Recheck) | Medium | worker status=Update-Rejected |

## C. Valid Transitions — System-driven (6 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M2_TC_020 | M2.3 | Medium | Pending → Auto Rejected (system rule trigger) — flag-only | Worker meet system rule cho Auto Reject | 1. Tạo worker meet rule<br>2. Quan sát status sau X hours/days | Status → Auto Rejected. Flag-only do skip time rules (Q-A2) | Low | sample worker từ DB |
| PPAC_M2_TC_021 | M2.3 | Low | Auto Rejected → Auto Rejected Archived (sau X ngày) — flag-only | Worker status=Auto Rejected | 1. Quan sát/wait | Status → Auto Rejected Archived (terminal). Flag-only | Low | worker status=Auto Rejected |
| PPAC_M2_TC_022 | M2.3 | High | Pending → Waiting to Pass (Regula chờ kết quả) | Worker mới upload, Regula đang processing | 1. Tạo worker mới<br>2. Submit document<br>3. Quan sát status trong khi Regula chạy | Status hiển thị Waiting to Pass khi Regula chưa trả kết quả | High | worker mới + Regula pending |
| PPAC_M2_TC_023 | M2.3 | High | Waiting to Pass → Pending (Recheck) (sau khi worker re-upload) | Worker status=Waiting to Pass | 1. Worker upload lại document<br>2. Refresh | Status → Pending (Recheck) | High | worker status=Waiting to Pass |
| PPAC_M2_TC_024 | M2.4 | High | Pending → Review Required (Regula confidence ∈ [0.5, 0.9]) | Worker upload document có Regula score borderline | 1. Trigger Regula với confidence trong khoảng<br>2. Quan sát status | Status → Review Required (test boundary 0.5 và 0.9: cả 2 đều trigger Review Required) | High | regula score = 0.5, 0.7, 0.9 |
| PPAC_M2_TC_025 | M2.3 | High | Active → Expired (D.O.V đã qua) | Worker status=Active có D.O.V = hôm qua | 1. Wait for system check (hoặc trigger nightly job)<br>2. Refresh queue | Status → Expired | High | worker status=Active, D.O.V = past |

## D. Forbidden / Invalid Transitions (5 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M2_TC_030 | M2.2 | Critical | KHÔNG cho phép Pending → Rejected direct | Worker status=Pending | 1. Mở<br>2. Tìm UI/API option để set Rejected trực tiếp | UI **không có** option direct Rejected. API call thẳng → 4xx/403 | Critical | worker status=Pending |
| PPAC_M2_TC_031 | M2.1 | High | Worker ở status Archive → Submit/Edit bị disable | Worker status=Archive | 1. Mở M4 Detail<br>2. Quan sát Submit + Edit button | Cả Submit + Edit disabled (terminal status) | High | worker status=Archive |
| PPAC_M2_TC_032 | M2.1 | High | Worker ở status Auto Rejected Archived → Submit/Edit disable | Worker status=Auto Rejected Archived | 1. Mở M4<br>2. Quan sát Submit + Edit | Cả 2 disable | High | worker status=Auto Rejected Archived |
| PPAC_M2_TC_033 | M2.2 | Medium | Whitespace-only reason không được coi là "reason có nội dung" | Worker status=Pending | 1. Mở<br>2. Nhập reason = "    " (4 spaces)<br>3. Submit | TBD: validate báo lỗi, hoặc treat như rỗng → Approve. Document actual behavior | Medium | reason="    " |
| PPAC_M2_TC_034 | M2.2 | High | Reason với ký tự đặc biệt (XSS/SQLi check) | Worker status=Pending | 1. Nhập reason `<script>alert(1)</script>;DROP TABLE workers;`<br>2. Submit<br>3. Reload + xem trong M1 Failed Reason cột | Text được escape, không execute script, không break query. Reason hiển thị literal text | High | reason=XSS payload |

## E. Worker Recheck Flow (E2E — 3 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M2_TC_040 | M2.2 | Critical | E2E: Pending → Pending Info → worker updates → Pending (Recheck) → Active | Worker `auto_m2_040_<ts>` status=Pending | 1. Verifier reject với reason "Need NI proof" → Pending Info<br>2. Worker upload NI doc (qua worker app/API)<br>3. Verifier mở lại worker<br>4. Submit reason rỗng | Status sequence: Pending → Pending Info → Pending (Recheck) → Active. Failed Reason cleared khi → Active | Critical | full E2E worker |
| PPAC_M2_TC_041 | M2.2 | Critical | E2E: Pending → Update-Rejected → worker re-upload → Pending (Recheck) → Active | Worker `auto_m2_041_<ts>` status=Pending | 1. Reject "Doc blurry" → Update-Rejected<br>2. Worker re-upload document<br>3. Verifier approve → Active | Status sequence đúng. D.O.V được set khi → Active | Critical | full E2E worker |
| PPAC_M2_TC_042 | M2.2 | High | Loop reject: Pending (Recheck) → Update-Rejected → Recheck → Update-Rejected (multiple cycles) | Worker `auto_m2_042_<ts>` status=Pending (Recheck) | 1. Reject lần 1<br>2. Worker re-upload<br>3. Reject lần 2<br>4. Worker re-upload<br>5. Reject lần 3 | Hệ thống cho phép loop không giới hạn HOẶC trigger Auto Rejected sau N attempts. Mỗi lần Reject lưu Failed Reason mới (overwrite hay append?) | High | E2E loop |

## F. Decision Table — Submit Action (4 TC)

**Decision Table matrix:**

| # | status_terminal | reason_empty | regula_passed | Expected |
|---|---|---|---|---|
| TC_050 | No | Yes | Yes | → Active |
| TC_051 | No | No (need info) | Yes | → Pending Info |
| TC_052 | No | No (reject) | Yes | → Update-Rejected |
| TC_053 | No | Any | No | Submit blocked |

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M2_TC_050 | M4.6.1 | Critical | DT-1: Pending + reason empty + Regula passed → Active | Worker status=Pending, Regula score > 0.9 | 1. Mở<br>2. Reason rỗng<br>3. Submit | Status → Active | Critical | regula > 0.9 |
| PPAC_M2_TC_051 | M4.6.2 | Critical | DT-2: Pending + reason "need info" + Regula passed → Pending Info | Worker status=Pending, Regula passed | 1. Mở<br>2. Reason: "Please upload Share Code letter"<br>3. Submit | Status → Pending Info | Critical | regula > 0.9 |
| PPAC_M2_TC_052 | M4.6.2 | Critical | DT-3: Pending + reason "reject" + Regula passed → Update-Rejected | Worker status=Pending, Regula passed | 1. Mở<br>2. Reason: "Card invalid, reject"<br>3. Submit | Status → Update-Rejected | Critical | regula > 0.9 |
| PPAC_M2_TC_053 | M4.3 | High | DT-4: Regula failed → Submit bị chặn (no bypass per AS-B3) | Worker, Regula API trả error | 1. Mở<br>2. Tab Regula Data hiển thị fail reason<br>3. Click Submit (any reason) | Submit disabled HOẶC pop error "Regula failed, cannot proceed". Status không đổi | High | regula API fail |

---

# Part 2 — M4 Worker Detail Modal (58 TC)

## A. M4.1 Header Info Bar (5 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M4_TC_001 | M4.1 | High | Header hiển thị đủ 9 field read-only | Worker bất kỳ đã mở | 1. Quan sát header bar | 9 field hiện đúng thứ tự: Company / Type / Email / DOB / V Code / Skill Card / Job Role / Status / Failed Reason | High | worker bất kỳ |
| PPAC_M4_TC_002 | M4.1 | Medium | Header field không cho edit (read-only) | Worker đã mở | 1. Click vào từng field header<br>2. Thử nhập text | Field không trở thành editable. Không có cursor input | Medium | worker bất kỳ |
| PPAC_M4_TC_003 | M4.1 | High | Header Status đồng bộ với cột Status M1 sau khi Submit | Worker status=Pending | 1. Submit Approve<br>2. Quan sát header Status | Header Status đổi ngay sang "Active" (không cần reload) | High | worker status=Pending |
| PPAC_M4_TC_004 | M4.1 | High | Header Failed Reason cập nhật ngay sau Submit reject | Worker status=Pending | 1. Reason "Test reject reason"<br>2. Submit | Header field "Failed Reason" hiển thị "Test reject reason" | High | reason="Test reject reason" |
| PPAC_M4_TC_005 | M4.1 | Medium | Email + V Code trong header có copy được (button role) | Worker bất kỳ | 1. Click Email field<br>2. Click V Code field | Cả 2 trigger copy clipboard hoặc action click — verify behavior thực tế | Medium | worker bất kỳ |

## B. M4.2 Document Tabs Navigation (5 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M4_TC_010 | M4.2 | High | Verify đủ 4 tab document: CSCS / RTW (Front) / RTW (Back) / Other Skill Card | Worker đã mở | 1. Quan sát tab bar | 4 tab hiện đúng thứ tự + label | High | worker bất kỳ |
| PPAC_M4_TC_011 | M4.2 | High | Click từng tab → load đúng nội dung document tương ứng | Worker có đủ 4 loại document | 1. Click CSCS<br>2. Click RTW Front<br>3. Click RTW Back<br>4. Click Other Skill Card | Mỗi tab hiển thị image đúng loại, sub-tab area refresh | High | worker đủ docs |
| PPAC_M4_TC_012 | M4.2 | Medium | Tab CSCS được active mặc định khi mở Detail | Worker mới mở | 1. Mở Detail | CSCS tab highlight (active state) | Medium | worker bất kỳ |
| PPAC_M4_TC_013 | M4.2 | Medium | Tab giữ active khi switch sub-tab khác | Đang ở CSCS tab | 1. Click sub-tab Regula Data<br>2. Quan sát top tab | Top tab vẫn highlight CSCS | Medium | worker bất kỳ |
| PPAC_M4_TC_014 | M4.2 | High | Worker Skill Card = "Other" — tab "Other Skill Card" có nội dung; CSCS empty/placeholder | Worker Skill Card=Other | 1. Mở<br>2. Tab CSCS<br>3. Tab Other Skill Card | CSCS hiển thị empty/placeholder. Other Skill Card hiển thị card image | High | worker Skill Card=Other |

## C. M4.3 Sub-tabs (8 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M4_TC_020 | M4.3 | High | Đủ 6 sub-tab: User Image / PPAC Report / Regula Data / Right To Work / Card Verification / Extra Card | Worker đã mở | 1. Quan sát sub-tab bar | 6 sub-tab hiện đủ + đúng label | High | worker bất kỳ |
| PPAC_M4_TC_021 | M4.3 | High | Sub-tab "PPAC Report" hiển thị scoring report | Worker đã có PPAC Report generated | 1. Click PPAC Report | Hiển thị scoring details (numeric score, breakdown) | High | worker có report |
| PPAC_M4_TC_022 | M4.3 | High | Sub-tab "Regula Data" hiển thị OCR result + confidence weight | Worker có Regula data | 1. Click Regula Data | Hiển thị field OCR (Name, DOB, NI từ document) + confidence weight | High | worker có Regula data |
| PPAC_M4_TC_023 | M4.3 | High | Sub-tab "Card Verification" hiển thị API verification result | Worker có CSCS card đã verify | 1. Click Card Verification | Hiển thị status verification (passed/failed) + lý do | High | worker có CSCS verified |
| PPAC_M4_TC_024 | M4.3 | Critical | Regula API fail → tab Regula Data hiển thị error message + Submit chặn | Worker có Regula error | 1. Click Regula Data<br>2. Quan sát<br>3. Click Submit | Tab hiển thị error reason. Submit chặn HOẶC báo error "Cannot proceed, Regula failed" | Critical | worker Regula error |
| PPAC_M4_TC_025 | M4.3 | High | PPAC Report chưa generate → tab hiển thị placeholder, Submit có chặn? | Worker mới, PPAC Report pending | 1. Click PPAC Report<br>2. Submit | Tab hiển thị "Report pending" hoặc spinner. Document actual Submit behavior | High | worker mới |
| PPAC_M4_TC_026 | M4.3 | Critical | Verifier KHÔNG bypass được Regula error qua manual input (per AS-B3) | Worker Regula failed | 1. Tìm cách bypass (manual approve/override) | Không có UI bypass. Submit/Approve disabled hoặc requires escalation flow | Critical | worker Regula failed |
| PPAC_M4_TC_027 | M4.3 | Medium | Sub-tab "Extra Card" hiển thị card phụ nếu worker có multiple cards | Worker Skill Card="Multiple" | 1. Click Extra Card | Hiển thị danh sách card thứ 2 trở đi | Medium | worker Skill Card=Multiple |

## D. M4.4 Worker Information Panel + Copy (8 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M4_TC_030 | M4.4 | High | Panel hiển thị đủ 9 field: Name, Surname, Reusable Passport, Nationality, Site Location, Sub Contractor, NI Number, Share Code, Card Number | Worker đã mở | 1. Quan sát panel right | 9 field hiện đúng + có label | High | worker bất kỳ |
| PPAC_M4_TC_031 | M4.4 | High | Field rỗng hiển thị placeholder "-" hoặc "N/A" | Worker NI Number = null | 1. Quan sát NI Number row | Hiển thị "-" hoặc "N/A" (không leave blank) | High | worker NI=null |
| PPAC_M4_TC_032 | M4.4 | High | Copy button cạnh Name copy raw value | Worker Name="Test" | 1. Click Copy cạnh Name<br>2. Paste vào notepad | Clipboard content = "Test" (exact match) | High | Name="Test" |
| PPAC_M4_TC_033 | M4.4 | High | Copy button cạnh NI Number copy đúng UK format | Worker NI="AB123456C" | 1. Click Copy NI<br>2. Paste | Clipboard = "AB123456C" (no spaces, no truncation) | High | NI="AB123456C" |
| PPAC_M4_TC_034 | M4.4 | High | Copy button cạnh Share Code | Worker Share Code="C04ABCD" | 1. Click Copy Share Code<br>2. Paste | Clipboard = "C04ABCD" | High | Share Code="C04ABCD" |
| PPAC_M4_TC_035 | M4.4 | High | Copy button cạnh Card Number | Worker có Card Number | 1. Click Copy Card Number<br>2. Paste | Clipboard = card number raw | High | worker có card number |
| PPAC_M4_TC_036 | M4.4 | Medium | Copy field rỗng → behavior (no-op / copy "-" / copy empty) | Field NI=null | 1. Click Copy<br>2. Paste | Document actual: clipboard rỗng / hoặc "-" | Medium | field=null |
| PPAC_M4_TC_037 | M4.4 | Low | Copy không hiện toast confirm? | Worker bất kỳ | 1. Click Copy | Có toast "Copied" hay silent? Document actual UX | Low | bất kỳ |

## E. M4.4.1 Edit Dialog — Field Validation (15 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M4_TC_040 | M4.4.1 | High | Click Edit → mở dialog với 7 field editable | Worker đã mở | 1. Click Edit | Hiện form Name/Surname/Nationality/Site Location/Sub Contractor/DOB/NI Number. KHÔNG có Email/Reusable Passport/Share Code/Card Number | High | worker bất kỳ |
| PPAC_M4_TC_041 | M4.4.1 | High | Edit Name happy path | Worker Name="Test" | 1. Edit<br>2. Đổi Name → "TestUpdated"<br>3. Save | Panel + header refresh "TestUpdated" | High | "TestUpdated" |
| PPAC_M4_TC_042 | M4.4.1 | High | Edit Name — empty → reject | Worker bất kỳ | 1. Edit<br>2. Clear Name<br>3. Save | Validation error "Name required". Không save | High | "" |
| PPAC_M4_TC_043 | M4.4.1 | Medium | Edit Name — boundary 255 chars (BVA) | Worker bất kỳ | 1. Edit<br>2. Name = 255 ký tự "a"<br>3. Save | Save thành công HOẶC reject với max-length error. Document actual limit | Medium | "a"×255 |
| PPAC_M4_TC_044 | M4.4.1 | Medium | Edit Name — Unicode (Vietnamese, Chinese, Arabic) | Worker bất kỳ | 1. Edit Name = "Nguyễn", "李", "محمد"<br>2. Save | Save thành công, hiển thị đúng (no mojibake) | Medium | Unicode names |
| PPAC_M4_TC_045 | M4.4.1 | High | Edit Name — special char (O'Brien, Smith-Jones, José) | Worker bất kỳ | 1. Edit Name = "O'Brien"<br>2. Save<br>3. Re-edit "Smith-Jones"<br>4. Save | Cả 2 valid, không escape sai | High | "O'Brien", "Smith-Jones" |
| PPAC_M4_TC_046 | M4.4.1 | High | Edit Name — XSS payload | Worker bất kỳ | 1. Edit Name = `<script>alert(1)</script>`<br>2. Save<br>3. Reload | Text escape, không execute. Hiển thị literal | High | XSS payload |
| PPAC_M4_TC_047 | M4.4.1 | High | Edit DOB — valid past date | Worker bất kỳ | 1. Edit DOB = 01-01-1990<br>2. Save | Save thành công | High | 01-01-1990 |
| PPAC_M4_TC_048 | M4.4.1 | High | Edit DOB — future date → reject | Worker bất kỳ | 1. Edit DOB = 01-01-2099<br>2. Save | Validation error "DOB cannot be future" | High | 01-01-2099 |
| PPAC_M4_TC_049 | M4.4.1 | High | Edit DOB — under 16 (UK child labor) → reject hoặc warning | Worker bất kỳ | 1. Edit DOB = today minus 15 years<br>2. Save | Validation hoặc warning "Worker under 16". Document compliance rule | High | today - 15y |
| PPAC_M4_TC_050 | M4.4.1 | Critical | Edit NI Number — valid UK format AB123456C | Worker bất kỳ | 1. Edit NI = "AB123456C"<br>2. Save | Save thành công | Critical | "AB123456C" |
| PPAC_M4_TC_051 | M4.4.1 | High | Edit NI Number — invalid format → reject | Worker bất kỳ | 1. Edit NI = "12345678", "ABCDEFGH", "AB12 3456 C" (with spaces)<br>2. Save từng cái | Reject với "Invalid NI format". Document UK NI rule (2 chữ + 6 số + 1 chữ A/B/C/D, prefix không được D/F/I/Q/U/V) | High | invalid NI |
| PPAC_M4_TC_052 | M4.4.1 | Medium | Edit Nationality — dropdown selection | Worker bất kỳ | 1. Edit<br>2. Mở dropdown Nationality<br>3. Chọn "Vietnamese" | Selection saved, hiển thị | Medium | "Vietnamese" |
| PPAC_M4_TC_053 | M4.4.1 | Medium | Edit Sub Contractor — dropdown vs text? | Worker bất kỳ | 1. Edit Sub Contractor<br>2. Quan sát input type | Document: dropdown từ master list HOẶC free text | Medium | TBD |
| PPAC_M4_TC_054 | M4.4.1 | High | Edit save → audit log entry created (related F-COMP-02) | Worker bất kỳ | 1. Edit Name<br>2. Save<br>3. Check audit log (qua DB/Notification feed M7) | Có entry: who/when/what changed | High | Edit action |

## F. M4.5 Document Upload (8 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M4_TC_060 | M4.5 | High | Upload image valid (.jpg) → ghi đè bản cũ | Worker có CSCS image cũ | 1. CSCS tab → Upload<br>2. Chọn `cscs_new.jpg` (1MB)<br>3. Submit upload | Image cũ bị overwrite (AS-B5). Hiển thị image mới | High | cscs_new.jpg 1MB |
| PPAC_M4_TC_061 | M4.5 | High | Upload .png hợp lệ | Worker bất kỳ | 1. Upload `cscs.png`<br>2. Quan sát | Hiển thị PNG | High | cscs.png |
| PPAC_M4_TC_062 | M4.5 | Medium | Upload PDF (multi-page document) | Worker bất kỳ | 1. Upload `rtw.pdf`<br>2. Tab RTW Front | Hiển thị PDF preview HOẶC chỉ trang đầu | Medium | rtw.pdf 5 pages |
| PPAC_M4_TC_063 | M4.5 | High | Upload file lớn 100MB (no limit per AS-B5) | Worker bất kỳ | 1. Upload `big.jpg` 100MB<br>2. Wait | Upload thành công. Verify performance + UX progress bar | High | 100MB file |
| PPAC_M4_TC_064 | M4.5 | High | Upload .exe / .zip — security risk | Worker bất kỳ | 1. Upload `malware.exe`<br>2. Quan sát | Document actual: nếu thành công → flag security risk (per AS-B5 = no limit) | High | malware.exe |
| PPAC_M4_TC_065 | M4.5 | Medium | Upload 0-byte file | Worker bất kỳ | 1. Upload `empty.jpg` 0 bytes<br>2. Submit | Document: thành công hay reject? | Medium | empty.jpg 0B |
| PPAC_M4_TC_066 | M4.5 | High | Upload network drop giữa chừng | Worker bất kỳ | 1. Upload file 50MB<br>2. Disconnect network<br>3. Reconnect | Hiển thị error, upload không partial save. Cho retry | High | 50MB file |
| PPAC_M4_TC_067 | M4.5 | High | Re-upload trigger transition Pending Info → Pending (Recheck) | Worker status=Pending Info | 1. Upload document mới<br>2. Refresh queue | Status auto đổi Pending Info → Pending (Recheck) | High | worker status=Pending Info |

## G. M4.6 Verification Action — Field Validation (5 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M4_TC_070 | M4.6 | High | Reason textbox max-length boundary | Worker status=Pending | 1. Nhập reason 500 ký tự<br>2. Submit | Document max length (TBD). Test ranh giới 499/500/501 | High | reason×500 |
| PPAC_M4_TC_071 | M4.6 | Medium | Reason multi-line (paragraphs) | Worker status=Pending | 1. Nhập reason có line break<br>2. Submit<br>3. Reload xem trong header Failed Reason | Line break giữ nguyên | Medium | multi-line text |
| PPAC_M4_TC_072 | M4.6 | High | Reason Unicode (Vietnamese tiếng có dấu) | Worker status=Pending | 1. Reason "Tài liệu chưa rõ, vui lòng tải lại"<br>2. Submit | Lưu + hiển thị đúng (no mojibake) | High | Unicode reason |
| PPAC_M4_TC_073 | M4.6 | Medium | Submit không có document upload nào → behavior | Worker mới chưa upload | 1. Mở<br>2. Reason rỗng<br>3. Submit | Document actual: cho approve hay block? | Medium | worker mới |
| PPAC_M4_TC_074 | M4.6 | High | Submit double-click (race condition) | Worker status=Pending | 1. Mở<br>2. Reason rỗng<br>3. Click Submit nhanh 2 lần | Chỉ 1 transition (no duplicate). Submit button disabled sau click 1 | High | double-click |

## H. M4.7 Info Button (1 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M4_TC_080 | M4.7 | Low | Click Info button → hiển thị tooltip/help text | Worker đã mở | 1. Click Info top-left | Hiện tooltip giải thích modal (TBD content) | Low | bất kỳ |

## I. M4.8 Concurrency — No Lock (3 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M4_TC_090 | M4.8 | High | 2 verifier mở cùng worker đồng thời (no lock per AS-B7) | Cần 2 admin account hoặc 2 browser session | 1. Tab A: mở worker X<br>2. Tab B: mở worker X<br>3. Quan sát có warning lock không | Cả 2 mở được, không lock notice | High | 2 sessions |
| PPAC_M4_TC_091 | M4.8 | Critical | 2 verifier Submit cùng worker — last-write-wins hay conflict error? | 2 session mở worker X status=Pending | 1. Tab A: reason="Approve" → Submit<br>2. Tab B (cùng lúc): reason="Reject XYZ" → Submit | Document actual: status cuối cùng là cái nào? Có warning "modified by other user" không? **Risk Critical cho compliance audit** | Critical | 2 sessions race |
| PPAC_M4_TC_092 | M4.8 | High | Tab B refresh sau Tab A submit — hiển thị status mới | Tab A submit Approve. Tab B chưa refresh, vẫn show Pending | 1. Tab B click Submit lại | Tab B nhận status mới (Active). Submit từ Tab B fail/show error "Already processed" | High | stale session |

---

# Part 3 — M3 Filter Bar (45 TC)

## A. M3.1 Status Dropdown (5 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M3_TC_001 | M3.1 | Critical | Default Status filter = Pending khi load landing | Logout → login lại | 1. Login<br>2. Quan sát Status dropdown | Hiển thị "Pending", table chỉ data Pending | Critical | account admin |
| PPAC_M3_TC_002 | M3.1 | High | Đổi Status filter — table refresh ngay | Đang ở Pending | 1. Click dropdown<br>2. Chọn "Active"<br>3. Quan sát | Table refresh, KPI Active count khớp | High | status=Active |
| PPAC_M3_TC_003 | M3.1 | High | Status dropdown chứa đủ 13 status (regression cover M2) | Bất kỳ | 1. Mở dropdown | 13 option | High | dropdown |
| PPAC_M3_TC_004 | M3.1 | Medium | Single-select — chọn status mới override status cũ (không multi) | Đang ở Active | 1. Chọn Pending | Filter chuyển Pending, không phải "Active+Pending" | Medium | status=Pending |
| PPAC_M3_TC_005 | M3.1 | High | Sau Submit verify worker → quay queue, filter Pending vẫn giữ (AS-C6) | Filter=Pending, mở 1 worker | 1. Submit Approve<br>2. Quay lại queue | Filter vẫn = Pending. Worker vừa approve biến mất khỏi list (vì status đổi → Active) | High | filter persistence return |

## B. M3.2 Search Textbox (8 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M3_TC_010 | M3.2 | Critical | Search by full Email exact | Có worker email="aashigaur.16@gmail.com" | 1. Search "aashigaur.16@gmail.com" | Table chỉ hiện worker đó | Critical | email exact |
| PPAC_M3_TC_011 | M3.2 | High | Search by partial Email — fuzzy verify (AM-1) | Email "aashigaur.16@gmail.com" tồn tại | 1. Search "aashigaur"<br>2. Quan sát | Document actual: fuzzy (return match) hay exact (return empty) | High | partial email |
| PPAC_M3_TC_012 | M3.2 | High | Search by Name | Worker Name="Test" | 1. Search "Test" | Tất cả worker Name="Test" hiện | High | Name="Test" |
| PPAC_M3_TC_013 | M3.2 | High | Search by Surname | Worker Surname="Singh" | 1. Search "Singh" | Worker Surname="Singh" hiện | High | Surname="Singh" |
| PPAC_M3_TC_014 | M3.2 | Critical | Search by V Code | Worker V Code="100064" | 1. Search "100064" | Đúng 1 worker | Critical | V Code="100064" |
| PPAC_M3_TC_015 | M3.2 | Medium | Search case-insensitive (Email "AASHIGAUR" vs "aashigaur") | Email "aashigaur.16" | 1. Search "AASHIGAUR.16" | Hiện worker (case-insensitive) HOẶC document case-sensitive | Medium | uppercase |
| PPAC_M3_TC_016 | M3.2 | Medium | Search empty string → return all (theo filter status) | Filter=Pending | 1. Search ""<br>2. Quan sát | Hiển thị toàn bộ Pending list | Medium | empty |
| PPAC_M3_TC_017 | M3.2 | High | Search SQLi/XSS payload | Bất kỳ | 1. Search `'; DROP TABLE workers; --`<br>2. Search `<script>alert(1)</script>` | Không break query, không execute. Empty result hoặc literal text | High | payloads |

## C. M3.3 Date Range (8 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M3_TC_020 | M3.3 | High | Date range valid → filter Created+Updated trong khoảng (AS-C2) | Có worker Created 2026-04-01, Updated 2026-05-01 | 1. Start=2026-04-01, End=2026-05-15<br>2. Quan sát | Worker hiện (vì Created hoặc Updated trong range) | High | range valid |
| PPAC_M3_TC_021 | M3.3 | High | Start Date = End Date (1 ngày) | Worker Created 2026-05-15 | 1. Start=End=2026-05-15 | Hiển thị worker created/updated đúng ngày đó | High | single day |
| PPAC_M3_TC_022 | M3.3 | High | Start Date > End Date (invalid) | Bất kỳ | 1. Start=2026-05-15, End=2026-05-01 | Error "Start must be before End" HOẶC empty result. Document actual | High | reverse range |
| PPAC_M3_TC_023 | M3.3 | Medium | Chỉ Start Date, End trống — open-ended | Bất kỳ | 1. Start=2026-04-01<br>2. End=blank | Filter từ 2026-04-01 đến nay | Medium | start only |
| PPAC_M3_TC_024 | M3.3 | Medium | Chỉ End Date, Start trống | Bất kỳ | 1. Start=blank, End=2026-05-15 | Filter từ đầu đến 2026-05-15 | Medium | end only |
| PPAC_M3_TC_025 | M3.3 | Medium | Future date | Bất kỳ | 1. Start=2026-12-31 | Empty result (không có worker tương lai) | Medium | future |
| PPAC_M3_TC_026 | M3.3 | Medium | Date format invalid (text "abc") | Bất kỳ | 1. Nhập "abc" vào Start Date | Validation block input HOẶC field reject | Medium | invalid text |
| PPAC_M3_TC_027 | M3.3 | High | Date filter timezone — boundary 2026-05-15 23:59 vs 2026-05-16 00:01 UK time | Worker Created 2026-05-15 23:59 UK | 1. Filter End=2026-05-15<br>2. Quan sát | Worker có hiện không? Document timezone behavior (UTC vs BST/GMT) | High | timezone boundary |

## D. M3.4 Distinct Toggle (4 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M3_TC_030 | M3.4 | Critical | Distinct=ON (default) — gộp duplicate theo email, giữ latest (AS-C3) | Có worker email "X@test.com" với 3 record (Created khác nhau) | 1. Bật Distinct (default đã ON)<br>2. Search "X@test.com" | Chỉ 1 row hiện, là record có Updated Time mới nhất | Critical | email duplicate |
| PPAC_M3_TC_031 | M3.4 | High | Distinct=OFF — hiển thị tất cả N records cùng email | Cùng setup TC_030 | 1. Tắt Distinct<br>2. Search "X@test.com" | 3 row hiện | High | toggle off |
| PPAC_M3_TC_032 | M3.4 | Medium | Distinct với Date range — latest trong range | Worker email "X@test.com" có records ngoài + trong range | 1. Distinct=ON<br>2. Date range filter | Latest record TRONG range (không phải global latest) — TBD behavior | Medium | distinct + date |
| PPAC_M3_TC_033 | M3.4 | Medium | KPI count thay đổi khi toggle Distinct | Có worker duplicate | 1. Distinct=ON: ghi nhớ Total<br>2. Distinct=OFF: ghi nhớ Total | Total OFF > Total ON (hoặc bằng nếu không có duplicate) | Medium | toggle compare |

## E. M3.5 Contractors Multi-select (6 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M3_TC_040 | M3.5 | Critical | Dropdown hiển thị đủ contractor list cho admin (≥20) | Admin đã login | 1. Click "Select Contractors" | Dropdown hiện ≥20 contractor: EKFB, Mace Construct, BML, McLaren, Mclaren 2, Core Group, BOUYGUES ES, J Coffey, Mace Dragados Curzon Street, Multiplex, Ballycommon, Berkeley (Induction), Berkeley Home Counties, BBVS, Morrisroe, Erith, Clipfine, Kingscote, KRS, Ppac Test, ... | Critical | admin |
| PPAC_M3_TC_041 | M3.5 | High | Multi-select 2 contractors → table chỉ data 2 contractor đó | Bất kỳ | 1. Chọn EKFB + McLaren<br>2. Apply | Table chỉ worker EKFB + McLaren | High | multi-select |
| PPAC_M3_TC_042 | M3.5 | High | "Select all" checkbox | Dropdown mở | 1. Click Select All | Tất cả contractor được tick | High | select all |
| PPAC_M3_TC_043 | M3.5 | Medium | Deselect all | Đã select all | 1. Bỏ tick Select All | Tất cả untick. Table behavior: empty hay full? Document | Medium | deselect all |
| PPAC_M3_TC_044 | M3.5 | Medium | Search trong dropdown contractor (nếu có search) | Dropdown mở | 1. Gõ "EKFB" trong search box dropdown | Filter contractor list | Medium | dropdown search |
| PPAC_M3_TC_045 | M3.5 | High | Contractor count badge — hiển thị số đã chọn | Đã chọn 3 | 1. Đóng dropdown | Hiển thị "3 selected" hoặc tag list | High | count badge |

## F. M3.6 Clear Button (3 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M3_TC_050 | M3.6 | Critical | Clear reset toàn bộ filter về default | Đã apply: Status=Active + Search="test" + Date range + Contractors=EKFB + Distinct=OFF | 1. Click Clear | Filter về: Status=Pending, Search="", Date blank, Contractors=all/none default, Distinct=ON | Critical | full filter |
| PPAC_M3_TC_051 | M3.6 | Medium | Clear khi không có filter active → no-op | Filter ở default | 1. Click Clear | No-op, không error | Medium | no filter |
| PPAC_M3_TC_052 | M3.6 | High | Clear → table refresh hiện default Pending list | Filter custom | 1. Clear | Table refresh ngay | High | clear table |

## G. Filter Combinations / Pairwise (8 TC)

**Pairwise design** — 4 filter dimensions × 2 levels:
- Status: Pending / Active
- Search: "" / "test"
- Date: blank / 2026-04 to 2026-05
- Contractor: All / EKFB only

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M3_TC_060 | M3.* | High | Pairwise 1: Status=Pending + Search="test" + Date=blank + Contractor=All | Bất kỳ | 1. Apply<br>2. Verify | Result đúng combo, KPI khớp | High | pairwise 1 |
| PPAC_M3_TC_061 | M3.* | High | Pairwise 2: Status=Pending + Search="" + Date=range + Contractor=EKFB | Bất kỳ | 1. Apply | Đúng | High | pairwise 2 |
| PPAC_M3_TC_062 | M3.* | High | Pairwise 3: Status=Active + Search="test" + Date=range + Contractor=EKFB | Bất kỳ | 1. Apply | Đúng | High | pairwise 3 |
| PPAC_M3_TC_063 | M3.* | High | Pairwise 4: Status=Active + Search="" + Date=blank + Contractor=All | Bất kỳ | 1. Apply | Đúng (= reset = default Active view) | High | pairwise 4 |
| PPAC_M3_TC_064 | M3.* | High | Filter combo cho 0 result → empty state UX (AM-2) | Bất kỳ | 1. Search "nonexistent_xxxxxx"<br>2. Quan sát | Empty state UI: text "No workers found" + icon. KPI=0 | High | 0 result |
| PPAC_M3_TC_065 | M3.* | Medium | Apply filter khi đang ở page 5 → reset về page 1 hay giữ? | Filter hiện tại, ở page 5 | 1. Đổi Status filter | Document: page reset về 1 hay giữ 5? | Medium | filter + page |
| PPAC_M3_TC_066 | M3.* | Medium | Apply filter trong khi M4 modal đang mở → behavior | M4 modal opened | 1. Đóng modal<br>2. Hoặc apply filter từ background | Document: filter được apply hay block khi modal? | Medium | modal + filter |
| PPAC_M3_TC_067 | M3.* | High | Filter + Distinct OFF + Multiple contractors → cross product correct | Cross-contractor worker (cùng email 2 contractor) | 1. Distinct=OFF<br>2. Contractors = cả 2<br>3. Search email | Hiện 2 row (1 mỗi contractor) | High | cross-contractor |

## H. Filter Persistence + URL (3 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M3_TC_070 | M3.* | High | Refresh trang → filter reset về default (AS-C5) | Filter custom | 1. F5 refresh | Filter về Pending default | High | refresh |
| PPAC_M3_TC_071 | M3.* | Medium | URL có query param phản ánh filter? (AM-3) | Filter Status=Active | 1. Quan sát URL bar | Document: URL có `?status=Active` hay không? Nếu có → bookmark được | Medium | URL inspection |
| PPAC_M3_TC_072 | M3.* | Medium | Logout → login lại → filter reset (AS-C5) | Filter custom | 1. Logout<br>2. Login lại | Filter = Pending default | Medium | logout reset |

---

# Part 4 — M1 Worker Queue (22 TC)

## A. Data Table Display (8 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M1_TC_001 | M1.1 | Critical | Bảng hiển thị đủ 14 cột đúng thứ tự | Landing | 1. Quan sát header bảng | Header: Company / Nationality / Type / Name / Surname / Email / DOB / V Code / Skill Card / Job Role / Status / D.O.V / Updated Time / Created Time | Critical | landing |
| PPAC_M1_TC_002 | M1.1 | Medium | Cột Type chỉ chứa giá trị enum: GB / EU / ROW | Filter Pending | 1. Quan sát cột Type | Tất cả value ∈ {GB, EU, ROW}, không có giá trị lạ | Medium | observed |
| PPAC_M1_TC_003 | M1.1 | Medium | Cột Skill Card chỉ chứa enum: CSCS / SIA / Other / Multiple / Not Applicable / "-" | Pending | 1. Quan sát cột Skill Card | Value trong tập enum cho phép | Medium | observed |
| PPAC_M1_TC_004 | M1.1 | High | Email cột — display đầy đủ, không truncate ẩn data | Worker email dài "very_long_email_address_for_testing@example.com" | 1. Quan sát cột Email | Email hiện đầy đủ HOẶC truncate có tooltip hover full | High | long email |
| PPAC_M1_TC_005 | M1.1 | High | Cột Status hiển thị đủ 13 status với badge màu (regression cover M2) | Có worker mọi status | 1. Filter từng status | Status badge đúng | High | 13 status |
| PPAC_M1_TC_006 | M1.1 | Medium | Cột Nationality — long text "Vietnamese" không break layout | Worker Vietnamese | 1. Quan sát cột Nationality | Layout không vỡ, không overlap cột khác | Medium | long nationality |
| PPAC_M1_TC_007 | M1.1 | High | Empty table state — hiển thị message "No workers" khi 0 result (AM-2) | Filter cho 0 result | 1. Search "nonexistent_xxx" | Empty state UI: text + icon, không show table header trống | High | 0 result |
| PPAC_M1_TC_008 | M1.1 | Medium | Loading state khi đang fetch data | Bất kỳ | 1. Refresh F5<br>2. Quan sát giai đoạn fetch | Loading spinner/skeleton hiển thị, không show "0 results" gây nhầm | Medium | loading |

## B. Row Click Navigation (3 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M1_TC_010 | M1.2 | Critical | Click row → mở M4 Detail Modal đúng worker | Worker đầu tiên Pending | 1. Click row 1<br>2. Quan sát Header bar M4 | Modal mở, header Email + V Code khớp row đã click | Critical | row click |
| PPAC_M1_TC_011 | M1.2 | High | Click row khác cột Email — Email là button (đã thấy semantic) — không trigger nhầm copy | Bất kỳ | 1. Click trên cell Email vs cell Name | Cả 2 đều mở M4 Detail (hoặc Email trigger copy + Name mở Detail — document) | High | click cell type |
| PPAC_M1_TC_012 | M1.2 | Critical | Đóng modal → quay queue giữ scroll position + filter (AS-C6) | Scroll xuống row 50, filter Pending | 1. Mở row 50<br>2. Đóng modal | Queue ở vị trí cũ (row 50 visible), filter vẫn Pending | Critical | scroll preservation |

## C. KPI Counter Bar (5 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M1_TC_020 | M1.3 | Critical | KPI bar hiển thị đủ 7 counter: Total, Pending, Pending Info, Active, Go to Site, Rejected, Expired | Landing | 1. Quan sát KPI bar | 7 counter hiện đúng label + giá trị số | Critical | KPI bar |
| PPAC_M1_TC_021 | M1.3 | Critical | Total = sum của 7 status count (data integrity REQ-M1-04) | Bất kỳ | 1. Đọc 7 counter<br>2. Tính tổng | Total = Pending + Pending Info + Active + Go to Site + Rejected + Expired (+ ?). Document actual formula nếu Total bao gồm cả status không hiện | Critical | sum check |
| PPAC_M1_TC_022 | M1.3 | High | KPI count khớp số rows hiển thị (sample check Pending=197) | Filter Pending, Distinct=ON, no other filter | 1. Đọc KPI Pending<br>2. Đếm rows qua mọi page | Số rows total = KPI Pending count | High | count match |
| PPAC_M1_TC_023 | M1.3 | High | KPI cập nhật sau Submit verify (Pending count -1, Active count +1) | KPI Pending=N, Active=M | 1. Mở 1 worker Pending → Approve<br>2. Quay queue<br>3. Đọc KPI | Pending=N-1, Active=M+1 | High | KPI delta |
| PPAC_M1_TC_024 | M1.3 | Medium | KPI cập nhật khi đổi filter Date range (chỉ count trong range) | Apply Date range | 1. Apply<br>2. Quan sát KPI | Counter đổi theo data filtered | Medium | filter KPI |

## D. Sort / Order / Loading (4 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M1_TC_030 | M1.1 | Medium | Default sort order — Updated Time DESC (newest first)? | Bất kỳ | 1. Quan sát cột Updated Time row 1 vs row 2 | Document default sort. Lý tưởng newest first | Medium | sort default |
| PPAC_M1_TC_031 | M1.1 | Medium | Click header cột → sort ASC/DESC (nếu có sortable) | Bất kỳ | 1. Click header "Created Time"<br>2. Quan sát | Document: sortable hay không. Nếu có → toggle ASC/DESC | Medium | sortable |
| PPAC_M1_TC_032 | M1.1 | Medium | Sort + Distinct + Filter combo — không nhầm record | Distinct=ON, sort by Updated | 1. Verify record latest hiển thị đúng record có Updated mới nhất (không phải Created) | Latest theo Updated Time | Medium | sort distinct |
| PPAC_M1_TC_033 | M1.1 | High | Sau Submit verify → row đó refresh trong table (status mới) HOẶC biến mất (nếu filter loại) | Filter=Pending, mở row Pending → Approve | 1. Đóng modal | Row biến mất khỏi list (vì đã thành Active). KPI Pending -1 | High | row update |

## E. Data Formatting (2 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M1_TC_040 | M1.1 | Medium | Date format consistent — DD-MM-YYYY (UK) cho DOB / D.O.V / Created / Updated | Bất kỳ | 1. Quan sát 4 cột date | Tất cả format DD-MM-YYYY (UK) hoặc consistent. KHÔNG mix US (MM-DD) và UK (DD-MM) | Medium | date format |
| PPAC_M1_TC_041 | M1.1 | Medium | DOB rỗng / null hiển thị "-" hoặc placeholder, không "01-01-1970" (epoch leak) | Worker DOB=null | 1. Quan sát cột DOB | Hiển thị "-" hoặc "N/A", không phải epoch date | Medium | null DOB |

---

# Part 5 — M5 Expiry Report (20 TC)

## A. Trigger / Download (4 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M5_TC_001 | M5.1 | Critical | Click "Expiry Report" button → trigger download | Bất kỳ | 1. Click "Expiry Report" | Browser download bắt đầu (notification download bar). Không error | Critical | trigger |
| PPAC_M5_TC_002 | M5.1 | Medium | Loading state khi đang generate report | Bất kỳ | 1. Click button<br>2. Quan sát button + UI | Có spinner/disabled state trên button trong khi generate, tránh double-click | Medium | loading state |
| PPAC_M5_TC_003 | M5.1 | High | Double-click button → chỉ 1 request (no duplicate download) | Bất kỳ | 1. Click 2 lần liên tiếp | Chỉ 1 file download (button disabled sau click 1) | High | double-click |
| PPAC_M5_TC_004 | M5.1 | Medium | Worker ở status terminal (Archive) — Expiry Report vẫn dùng được | Bất kỳ | 1. Click | Vẫn generate (Expiry Report không phụ thuộc worker đang xem) | Medium | independence |

## B. File Metadata (5 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M5_TC_010 | M5.2 | Critical | File extension = .xlsx (AS-D1) | Đã download | 1. Check tên file | Extension `.xlsx` (Excel) | Critical | file ext |
| PPAC_M5_TC_011 | M5.2 | High | Filename pattern document hoá (AM-5) | Đã download | 1. Đọc tên file | Document actual: VD `Expiry_Report_2026-05-15_HHmmss.xlsx`. Phải có **timestamp** để tránh ghi đè khi download nhiều lần | High | filename pattern |
| PPAC_M5_TC_012 | M5.2 | Critical | Encoding UTF-8 — mở file với ký tự non-ASCII đọc đúng (AS-D1) | Có worker tên Vietnamese "Nguyễn", Romanian "Țărănescu" | 1. Download<br>2. Mở Excel/LibreOffice<br>3. Search tên Unicode | Hiển thị đúng chữ có dấu, không mojibake | Critical | Unicode names |
| PPAC_M5_TC_013 | M5.2 | Medium | File mở được trong Excel + LibreOffice + Google Sheets | Đã download | 1. Mở 3 ứng dụng | Mở được, không corrupt | Medium | cross-app |
| PPAC_M5_TC_014 | M5.2 | Medium | File size hợp lý (không quá nhỏ < 1KB hoặc trống) | Đã download | 1. Check file size | Size > 1KB. Sample workers expired phải có data | Medium | size sanity |

## C. Data Scope Correctness (5 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M5_TC_020 | M5.3 | Critical | Report chỉ chứa worker `status = Expired` (AS-D4) | Có worker Expired + worker khác status | 1. Download<br>2. Check cột Status trong file | 100% rows có Status = "Expired". Không lẫn Active/Pending/etc. | Critical | status filter |
| PPAC_M5_TC_021 | M5.3 | Critical | Report luôn full DB (KHÔNG theo filter hiện tại) (AS-D2) | Apply filter: Contractor=EKFB only + Date range + Search | 1. Download Report | File chứa worker Expired của TẤT CẢ contractors, KHÔNG chỉ EKFB. Hoàn toàn ignore UI filter | Critical | filter ignored |
| PPAC_M5_TC_022 | M5.3 | High | Số rows file = số worker Expired trong DB (full count) | Bất kỳ | 1. Apply filter Status=Expired (chỉ để đếm) → KPI count<br>2. Tắt mọi filter khác (no contractor, no date, Distinct=OFF)<br>3. Đọc số worker Expired<br>4. Download report<br>5. So sánh row count | Row count file = số Expired full DB | High | count match |
| PPAC_M5_TC_023 | M5.3 | High | Distinct logic — file chứa duplicate (Distinct=OFF) hay deduped (Distinct=ON theo email)? | Bất kỳ | 1. Download<br>2. Check duplicate email trong file | Document actual: file dedup hay không. Lý tưởng có cờ rõ trong filename | High | dedup behavior |
| PPAC_M5_TC_024 | M5.3 | Medium | Worker Expired sau khi report download — không có trong file (snapshot tại moment download) | Worker A status=Active, sắp Expired | 1. Download report (A không có)<br>2. Wait/trigger A → Expired<br>3. Download lại | File 1: không có A. File 2: có A | Medium | snapshot timing |

## D. Data Integrity (4 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M5_TC_030 | M5.3 | High | Cột trong file (AM-5 — discover) document hoá | Đã download | 1. Mở file<br>2. List header columns | Document: header hiện có (kỳ vọng: Company, Name, Surname, Email, V Code, NI Number, DOB, D.O.V, Updated Time...) | High | columns inventory |
| PPAC_M5_TC_031 | M5.3 | High | PII protection — cột nhạy cảm (NI Number, DOB) có raw plain text? | Đã download có NI/DOB | 1. Check 2 cột | Document actual. Raw plain → flag F-SEC-NEW (export PII không mask). Nếu mask → OK | High | PII check |
| PPAC_M5_TC_032 | M5.3 | High | Date format trong file — ISO 8601 hoặc DD-MM-YYYY consistent | Đã download | 1. Check cột date | Format consistent, không mix. Excel hiển thị đúng (không as text "2026-05-15" thành "44331" serial number) | High | date format |
| PPAC_M5_TC_033 | M5.3 | Medium | Special char trong data (apostrophe, comma, newline trong field) — không break XLSX cell | Worker Surname có comma "Smith, Jr." | 1. Tạo worker với name có ký tự đặc biệt → Expired<br>2. Download<br>3. Check cell | Cell hiển thị đúng "Smith, Jr." trong 1 cell, không split thành 2 column | Medium | special chars |

## E. Error Handling + Performance (2 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M5_TC_040 | M5.* | Medium | Network drop khi đang download → error message + retry | Bất kỳ | 1. Click download<br>2. Disconnect network giữa chừng<br>3. Reconnect | Browser hiển thị error download. Click lại → retry thành công, file không partial | Medium | network drop |
| PPAC_M5_TC_041 | M5.* | High | Performance — full DB Expired (giả sử >10K rows) generate dưới 30s | DB lớn (cần test trên prod-like data nếu UAT có) | 1. Click<br>2. Đo thời gian từ click → download bắt đầu | < 30s. Document actual time để baseline cho regression | High | perf baseline |

---

# Part 6 — M6 Pagination (15 TC)

## A. Page-Size Dropdown (3 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M6_TC_001 | M6.1 | High | Page-size dropdown chứa đúng 3 option: 100 / 150 / 200 (AS-F1) | Bất kỳ | 1. Click dropdown page-size | 3 option hiện đúng, default = 100 | High | dropdown options |
| PPAC_M6_TC_002 | M6.1 | High | Đổi page-size = 200 → table refresh hiển thị 200 rows | Filter có > 200 worker | 1. Đổi sang 200<br>2. Đếm rows page 1 | 200 rows hiện | High | size 200 |
| PPAC_M6_TC_003 | M6.1 | Medium | Đổi page-size = 150 với data < 150 → hiện hết 1 page, không hiện page 2 | Filter có 100 worker (< 150) | 1. Đổi sang 150 | 100 rows hiện. Pagination chỉ có "1", Next/Last disabled | Medium | size > data |

## B. Navigation Buttons (5 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M6_TC_010 | M6.2 | Critical | Page 1: First + Previous disabled, Next + Last enabled | Đang ở page 1, có > 1 page | 1. Quan sát button states | First/Previous **disabled**. Next/Last **enabled** | Critical | page 1 |
| PPAC_M6_TC_011 | M6.2 | Critical | Page cuối: Next + Last disabled, First + Previous enabled | Filter Pending (3 page), click "3" | 1. Quan sát | Next/Last **disabled**. First/Previous **enabled** | Critical | last page |
| PPAC_M6_TC_012 | M6.2 | High | Click Next → tăng 1 page | Page 1 | 1. Click Next | Page 2 active, table refresh | High | next |
| PPAC_M6_TC_013 | M6.2 | High | Click Last → nhảy thẳng page cuối | Page 1, có 5 page | 1. Click Last | Page 5 active | High | last |
| PPAC_M6_TC_014 | M6.2 | High | Click numeric page (2, 3) — nhảy chính xác | Bất kỳ | 1. Click page "2"<br>2. Click page "3" | Active state đúng, table refresh đúng data | High | numeric page |

## C. Page-Size + Pagination Interaction (3 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M6_TC_020 | M6.3 | High | Đổi page-size khi đang ở page 3 → reset về page 1 hay giữ vị trí worker đang xem (AM-3) | Page-size=100, đang ở page 3 (worker thứ 200-300) | 1. Đổi page-size sang 200<br>2. Quan sát page hiện tại | Document actual: page reset về 1 (mất context) HAY recompute để giữ worker thứ 200-300 visible (better UX) | High | size + page |
| PPAC_M6_TC_021 | M6.3 | Medium | Page-size = 200 → tổng số page giảm đi (ví dụ 3 page khi 100 size → 2 page khi 200 size) | Filter có 250 worker, size=100 (3 page) | 1. Đổi size=200 | Pagination control hiển thị 2 page | Medium | size affects count |
| PPAC_M6_TC_022 | M6.3 | High | Apply filter mới → reset về page 1 (M3.* TC_065 cross) | Đang ở page 3 | 1. Đổi Status filter | Document: reset về 1 hay giữ 3? | High | filter resets page |

## D. Position Retention (4 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M6_TC_030 | M6.3 | Critical | Mở M4 Detail từ page 3 → đóng modal → vẫn ở page 3 (AS-C6) | Page-size=100, navigate page 3 | 1. Click row 5 page 3<br>2. Đóng modal | Page indicator vẫn ở 3, table hiện đúng data page 3 | Critical | page retention |
| PPAC_M6_TC_031 | M6.3 | High | Mở Detail → scroll page 3 → đóng modal → giữ scroll position | Page 3, scroll xuống row 80 | 1. Mở row 80<br>2. Đóng modal | Scroll vẫn ở row 80 (không reset top) | High | scroll retention |
| PPAC_M6_TC_032 | M6.3 | Medium | Refresh F5 ở page 3 → reset về page 1 (giả định no URL state) | Page 3 | 1. F5 | Quay lại page 1 (do AS-C5 filter không persist) | Medium | refresh resets |
| PPAC_M6_TC_033 | M6.3 | High | Submit verify worker từ page 3 → row biến mất, vẫn ở page 3, không nhảy page | Page 3, mở 1 worker Pending | 1. Submit Approve<br>2. Quay lại queue | Vẫn page 3, row đó biến mất, các row khác shift up | High | submit + page |

---

# Part 7 — M11 Multi-tenant (10 TC, Inspection Light)

## A. Admin Visibility (3 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M11_TC_001 | M11.1 | Critical | Admin thấy ≥20 contractor trong dropdown M3.5 (REQ-M11-01) | Login admin | 1. Click "Select Contractors"<br>2. Đếm | ≥20 contractor (EKFB, Mace, BML, McLaren, Mclaren 2, Core Group, BOUYGUES ES, J Coffey, Mace Dragados Curzon Street, Multiplex, Ballycommon, Berkeley Induction, Berkeley Home Counties, BBVS, Morrisroe, Erith, Clipfine, Kingscote, KRS, Ppac Test, ...) | Critical | admin login |
| PPAC_M11_TC_002 | M11.1 | High | Admin thấy worker của mọi contractor (no isolation cho admin role) | Bất kỳ | 1. Filter từng contractor riêng lẻ<br>2. Quan sát có data hay không | Mỗi contractor đều có ít nhất 1 worker visible (admin không bị deny) | High | filter each |
| PPAC_M11_TC_003 | M11.1 | High | Admin thấy worker mọi status × mọi contractor (cross-product) | Bất kỳ | 1. Status=Pending + Contractor=All → count<br>2. Status=Active + Contractor=All → count | KPI count khớp với DB. Không miss contractor nào | High | cross product |

## B. Cross-Contractor Data Behavior (3 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M11_TC_010 | M11 | Critical | Worker cùng email thuộc N contractor → N records riêng (REQ-M11-02 / AS-E3) | Cần worker test với cùng email ở 2 contractor (EKFB + McLaren) | 1. Distinct=OFF<br>2. Search email "shared@test.com"<br>3. Đếm rows | 2 rows hiện (1 mỗi contractor), Company cột khác nhau | Critical | shared email worker |
| PPAC_M11_TC_011 | M11 | High | Distinct=ON với worker shared email → gộp thành 1 row, giữ latest | Cùng setup TC_010 | 1. Distinct=ON<br>2. Search email | 1 row hiện, là record latest theo Updated Time. Cột Company hiển thị contractor nào? Document | High | distinct cross |
| PPAC_M11_TC_012 | M11 | High | Worker shared email — Submit verify ở contractor A có ảnh hưởng record contractor B không? | Worker shared email cùng email, 2 record (EKFB + McLaren) đều Pending | 1. Distinct=OFF<br>2. Mở record EKFB → Submit Approve<br>3. Quay queue<br>4. Mở record McLaren | Record McLaren vẫn Pending (independent records) HOẶC cả 2 đều thành Active (linked). Document — **edge case quan trọng cho audit** | High | shared submit |

## C. Inspection / Negative Flag-Only (4 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M11_TC_020 | M11 | Critical | **[FLAG-ONLY]** Verify URL/API direct access tới worker contractor B từ user verifier-of-A — bị deny | Cần verifier-of-contractor account (KHÔNG CÓ — flag) | 1. Note: gap AS-E2<br>2. Document scenario quan trọng nhưng không test được<br>3. Inspect URL pattern khi mở worker (xem có expose worker_id/contractor_id raw không) | **GAP CONFIRMED:** Không thể test depth, document trong report final. Inspect URL pattern: nếu URL có dạng `/worker/{id}` mà không có scope contractor → high risk IDOR | Critical (gap) | gap doc |
| PPAC_M11_TC_021 | M11 | High | **[FLAG-ONLY]** Verify single-contractor user dropdown chỉ hiện 1 contractor (REQ-M3-08) | Verifier-of-contractor account (KHÔNG CÓ) | 1. Note gap | **GAP CONFIRMED:** Cần account để test | High (gap) | gap doc |
| PPAC_M11_TC_022 | M11 | High | **[FLAG-ONLY]** API permission check — user verifier-of-A gọi API list workers contractor B trả 403 | Cần API token verifier-of-contractor + API tool (Postman) | 1. Note gap<br>2. Inspect Network tab xem có API endpoint nào expose | **GAP CONFIRMED:** Document API endpoint khi inspect Network tab — sẽ chuyển recommendation cho dev team add IDOR test sau | High (gap) | gap doc |
| PPAC_M11_TC_023 | M11 | High | **[INSPECTION]** Network inspect — request payload có chứa `contractor_id` filter rõ ràng không (server-side enforcement check) | Đã login admin | 1. Mở DevTools → Network<br>2. Apply filter Contractor=EKFB<br>3. Inspect request | Document: Request payload có `contractor_id=EKFB` (server enforces), HOẶC chỉ filter client-side. Nếu chỉ client-side → Critical security finding | High | network inspect |

---

# Recap & Next Steps

## Tổng kết bộ TC

| Part | Module | #TC | Critical | High | Medium | Low |
|---|---|---|---|---|---|---|
| 1 | M2 State Machine | 32 | 8 | 13 | 11 | 0 |
| 2 | M4 Worker Detail | 58 | 6 | 32 | 13 | 4 |
| 3 | M3 Filter Bar | 45 | 4 | 26 | 14 | 1 |
| 4 | M1 Worker Queue | 22 | 4 | 11 | 7 | 0 |
| 5 | M5 Expiry Report | 20 | 5 | 9 | 6 | 0 |
| 6 | M6 Pagination | 15 | 3 | 9 | 3 | 0 |
| 7 | M11 Multi-tenant | 10 | 2 | 6 | 1 | 0 |
| **TOTAL** | | **202** | **32** | **106** | **55** | **5** |

**Trong đó:** 4 TC ở Part 7 là **flag-only (gap)** do không có verifier-of-contractor account.

## Khuyến nghị thứ tự execute

1. **Smoke pass:** All Critical (32 TC) — verify happy paths + security boundaries trước
2. **Regression pass 1:** All High (106 TC) — chia theo module
3. **Coverage pass:** Medium + Low (60 TC)
4. **Gap escalation:** Báo cáo 4 flag-only TC + 6 gap khác cho PO/Security team

## Findings có thể phát sinh trong execute

- **F-SEC-NEW-1** (TC_031 M5): Expiry Report export PII raw plain text → vi phạm GDPR
- **F-SEC-NEW-2** (TC_023 M11): Filter contractor chỉ client-side → IDOR risk
- **F-SEC-NEW-3** (TC_064 M4): Upload .exe / no file type restriction → malware vector
- **F-COMP-NEW-1** (TC_054 M4): Edit Worker Info không có audit trail (link F-COMP-02)
- **F-COMP-NEW-2** (TC_091 M4): Concurrent Submit không có lock/conflict resolution → audit risk
