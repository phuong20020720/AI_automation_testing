# PPAC Mobile — DTSource CONSTRUCTION Worker Onboarding

> **Hệ thống:** PPAC Mobile — Worker Onboarding
> **Module:** DTSource (`dt • source`) · Sector = **Construction** — 3 form
> **Nguồn design (Figma):** "[NEW] PPAC New Design 2025" — file `ZzysHnguFvUu15zJyg6rjy`, section DTSource `13380:5796`. Cột "Figma Ref" = node-id frame từng màn.
> **Nguồn requirements:** [[requirements_dtsource_construction_onboarding]]
> **Phương pháp:** RBT — EP · BVA · State Transition · UI-state checks
> **Ngày tạo:** 2026-06-17
> **QA Owner:** maya.do@ppac.co.uk
> **Tổng số TC:** 48 (Critical 6 / High 19 / Medium 18 / Low 5)
> **Baseline:** [[test_cases_dtsource_rail_onboarding]] — Construction là subset của Rail (3 form). Xem "Khác biệt vs Rail" trong requirements doc.

---

## Phạm vi

**Trong scope — DTSource + Construction, 3 form:**
- M02 Your Details · M03 References · M09 Declaration (Privacy & Submit) · M10 E2E

**Ngoài scope:**
- **Surname · Email · Date of Birth** — pre-filled từ "Verify your identity".
- Payroll provider (không có) · Sentinel Number (rail-only, không có).
- Các form rail: Medical · Contract · PPE · Safety Critical · Lost & Stolen (Construction **không có**).
- Màn Prefix DTSource (chưa có screenshot — M01 tối thiểu, ⚠️ verify).

**Kỹ thuật:** EP (NIN, phone) · BVA (NIN số ký tự) · State Transition (multi-form flow, conditional fields) · UI-state (label/placeholder/error verbatim).

## Quy ước Test Data

- Traceable: `auto_dtscon_<module>_<TCnum>_20260617` (vd `auto_dtscon_details_010_20260617`).
- NIN hợp lệ mẫu: `AB123456C` (format `AA999999A`).
- Mỗi TC độc lập; reset onboarding ở Pre-Condition.

## Ghi chú (xem requirements §7, §9)

- **DN-01/02** lỗi chính tả M02 ("Next to Kin"/"Childen") — branding/spelling do team Design phụ trách, **KHÔNG tạo test case**.
- **AMB-05** Marketing toggle = required (lệch chuẩn GDPR opt-in, BA review — TC_123).
- **Branding "Ballycommon" còn sót ở màn Prefix dùng chung (option/dialog) → do Design phụ trách, KHÔNG tạo test case kiểm tra branding. Test cases chỉ verify CHỨC NĂNG.**
- ✅ Đã backfill node-id thật từ Figma `13380:5796`. Construction Your Details = `14739:17824`; References = `14739:16223` (frame 1-referee — ⚠️ verify đúng biến thể).

---

## Test Cases

### M01 — Prefix / Select Sector *(⚠️ AMB-06)*

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority | Figma Ref |
|-------|--------|---------------|---------------|------------|-----------|-----------------|----------|-----------|
| PPAC_DTSC_PREFIX_TC_001 | M01 | Nhập prefix DTSOURCE → chọn Construction → vào onboarding | Worker mở onboarding link, màn nhập company prefix | 1. Gõ "DTSOURCE"<br>2. Chọn "DTSOURCE - CONSTRUCTION"<br>3. Tap "Continue →" | prefix = DTSOURCE; chọn = DTSOURCE - CONSTRUCTION | 1. Vào journey Construction; điều hướng tới Your Details (⚠️ verify text option qua Figma) | Critical | 14739:19117 |
| PPAC_DTSC_PREFIX_TC_002 | M01 | Gợi ý có cả Rail & Construction — chọn Construction | Màn nhập prefix, đã gõ "DTSOURCE" | 1. Quan sát gợi ý<br>2. Chọn "DTSOURCE - CONSTRUCTION" | prefix = DTSOURCE | 1. Gợi ý hiển thị cả "DTSOURCE - RAIL" và "DTSOURCE - CONSTRUCTION"; chọn Construction → flow Construction (⚠️ verify) | Medium | 14739:19117 |

### M02 — Your Details (tab "Onboarding Details")

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority | Figma Ref |
|-------|--------|---------------|---------------|------------|-----------|-----------------|----------|-----------|
| PPAC_DTSC_DETAILS_TC_010 | M02 | Nhập đầy đủ Your Details hợp lệ (Construction) → sang References | Đã vào onboarding Construction, màn Your Details; Surname/Email/DOB pre-filled | 1. Chọn Consultant<br>2. Chọn ≥1 Trade, ≥1 Qualification<br>3. Nhập Candidate's Mobile Phone hợp lệ<br>4. Chọn Street address, nhập City + ZIP/Postcode<br>5. Nhập NIN<br>6. Chọn "Where did you hear about us?"<br>7. Nhập Next of Kin Name + Relationship + Contact Phone<br>8. Tap "Next →" | NIN = AB123456C; Phone = 07700900111; Next of Kin Phone = 07700900222 | 1. Tất cả field nhận giá trị, không lỗi<br>2. Điều hướng tới References | Critical | 14739:17824 |
| PPAC_DTSC_DETAILS_TC_011 | M02 | Hiển thị đúng cấu trúc nhóm + pre-filled (không có Sentinel) | Màn Your Details | 1. Quan sát nhóm Personal / Address / Next of Kin<br>2. Tìm field Sentinel Number<br>3. Quan sát Surname/Email/DOB | N/A | 1. Đủ nhóm; **KHÔNG có field Sentinel Number** (Construction); helper Next of Kin hiển thị; Surname/Email/DOB pre-filled sẵn | Medium | 14739:17824 |
| PPAC_DTSC_DETAILS_TC_012 | M02 | Consultant không chọn → lỗi required | Màn Your Details, field khác hợp lệ | 1. Bỏ trống Consultant<br>2. Tap "Next →" | Consultant = (none) | 1. Hiển thị lỗi required tại Consultant; không điều hướng | High | 14739:17824 |
| PPAC_DTSC_DETAILS_TC_013 | M02 | Consultant sheet — single-select + Search + CheckCircle | Màn Your Details | 1. Mở dropdown Consultant<br>2. Quan sát sheet + Search<br>3. Chọn 1 item | keyword tùy | 1. Sheet "Select Consultant" có Search; chọn 1 → CheckCircle xanh (single-select) | High | 14739:17824 |
| PPAC_DTSC_DETAILS_TC_014 | M02 | Trade không chọn → lỗi required | Màn Your Details | 1. Bỏ trống Trade<br>2. Tap "Next →" | Trade = (none) | 1. Hiển thị lỗi required tại Trade; không điều hướng | High | 14739:17824 |
| PPAC_DTSC_DETAILS_TC_015 | M02 | Trade multi-select sheet (checkbox + Search + Done) → tag ✕ | Màn Your Details | 1. Mở sheet "Select Trade"<br>2. Tick "A-level", "Abrasive Wheel"<br>3. Tap "Done"<br>4. Bỏ 1 tag bằng ✕ | chọn A-level, Abrasive Wheel | 1. Sheet multi-select; "Done" xác nhận; tag có ✕ ở field Trade; xóa tag được | Medium | 14739:17824 |
| PPAC_DTSC_DETAILS_TC_016 | M02 | Qualification không chọn → lỗi required | Màn Your Details | 1. Bỏ trống Qualification<br>2. Tap "Next →" | Qualification = (none) | 1. Hiển thị lỗi required tại Qualification; không điều hướng | High | 14739:17824 |
| PPAC_DTSC_DETAILS_TC_017 | M02 | Qualification multi-select sheet → tag ✕ | Màn Your Details | 1. Mở sheet "Select your qualification"<br>2. Tick 2+ option<br>3. Tap "Done" | chọn 2 option | 1. Multi-select; tag có ✕ ở field Qualification; bỏ tag được | Medium | 14739:17824 |
| PPAC_DTSC_DETAILS_TC_018 | M02 | Candidate's Mobile Phone sai định dạng → lỗi | Màn Your Details, field khác hợp lệ | 1. Nhập Phone sai<br>2. Tap "Next →" | Phone = 8272930 | 1. Hiển thị "Please enter a valid phone number"; không điều hướng | High | 14739:17824 |
| PPAC_DTSC_DETAILS_TC_019 | M02 | Candidate's Mobile Phone để trống → lỗi required | Màn Your Details | 1. Để trống Phone<br>2. Tap "Next →" | Phone = (empty) | 1. Hiển thị lỗi required tại Candidate's Mobile Phone; không điều hướng | High | 14739:17824 |
| PPAC_DTSC_DETAILS_TC_020 | M02 | Street address autocomplete — gõ → gợi ý → chọn | Màn Your Details | 1. Gõ "Syra"<br>2. Quan sát dropdown gợi ý<br>3. Chọn 1 gợi ý | gõ = "Syra" | 1. Focus viền xanh; dropdown gợi ý (vd "Syracuse, Connecticut") kèm ✓; chọn 1 → điền vào ô | High | 14739:17824 |
| PPAC_DTSC_DETAILS_TC_021 | M02 | Street address để trống → lỗi required | Màn Your Details | 1. Để trống Street address<br>2. Tap "Next →" | Street address = (empty) | 1. Hiển thị lỗi required; không điều hướng | High | 14739:17824 |
| PPAC_DTSC_DETAILS_TC_022 | M02 | Address line 2 OPTIONAL — để trống vẫn Next được | Màn Your Details, field bắt buộc hợp lệ, Address line 2 trống | 1. Để trống Address line 2<br>2. Tap "Next →" | Address line 2 = (empty) | 1. KHÔNG báo lỗi tại Address line 2; điều hướng sang References (field optional) | Medium | 14739:17824 |
| PPAC_DTSC_DETAILS_TC_023 | M02 | City để trống → lỗi required | Màn Your Details | 1. Để trống City<br>2. Tap "Next →" | City = (empty) | 1. Hiển thị lỗi required tại City; không điều hướng | Medium | 14739:17824 |
| PPAC_DTSC_DETAILS_TC_024 | M02 | ZIP / Postcode để trống → lỗi required | Màn Your Details | 1. Để trống ZIP/Postcode<br>2. Tap "Next →" | Postcode = (empty) | 1. Hiển thị lỗi required tại ZIP/Postcode; không điều hướng | Medium | 14739:17824 |
| PPAC_DTSC_DETAILS_TC_025 | M02 | NIN sai format → lỗi format | Màn Your Details | 1. Nhập NIN sai<br>2. Tap "Next →" | NIN = MIDORI | 1. Hiển thị "Please enter the right format: 2 letters, 6 numbers, 1 letter (e.g. AA999999A)"; không điều hướng | High | 14739:17824 |
| PPAC_DTSC_DETAILS_TC_026 | M02 | NIN đúng format AA999999A → chấp nhận | Màn Your Details | 1. Nhập NIN đúng<br>2. Rời focus | NIN = AB123456C | 1. Không lỗi NIN; field nhận giá trị | High | 14739:17824 |
| PPAC_DTSC_DETAILS_TC_027 | M02 | NIN sai số ký tự (boundary) → lỗi | Màn Your Details | 1. Nhập NIN thiếu 1 số (AB12345C)<br>2. Tap "Next →" | NIN = AB12345C | 1. Hiển thị lỗi format NIN | Medium | 14739:17824 |
| PPAC_DTSC_DETAILS_TC_028 | M02 | "Where did you hear about us?" không chọn → lỗi required | Màn Your Details | 1. Bỏ trống "Where did you hear about us?"<br>2. Tap "Next →" | = (none) | 1. Hiển thị lỗi required; không điều hướng | Medium | 14739:17824 |
| PPAC_DTSC_DETAILS_TC_029 | M02 | "Where did you hear about us?" sheet single-select + Search + CheckCircle | Màn Your Details | 1. Mở dropdown<br>2. Quan sát sheet + Search<br>3. Chọn 1 option | option bất kỳ | 1. Sheet có Search; chọn 1 → CheckCircle xanh (single-select) | Low | 14739:17824 |
| PPAC_DTSC_DETAILS_TC_030 | M02 | Next of Kin Name để trống → lỗi required | Màn Your Details | 1. Để trống Next of Kin Name<br>2. Tap "Next →" | Next of Kin Name = (empty) | 1. Hiển thị "Please fill in this field" tại Next of Kin Name; không điều hướng | High | 14739:17824 |
| PPAC_DTSC_DETAILS_TC_031 | M02 | Relationship to Candidate để trống → lỗi required (placeholder gợi ý) | Màn Your Details | 1. Quan sát placeholder<br>2. Để trống Relationship<br>3. Tap "Next →" | Relationship = (empty) | 1. Placeholder "e.g. Parent, Spouse, Sibling"<br>2. Để trống → lỗi required; không điều hướng | Medium | 14739:17824 |
| PPAC_DTSC_DETAILS_TC_032 | M02 | Next of Kin Contact Phone sai định dạng → lỗi | Màn Your Details | 1. Nhập Contact Phone sai<br>2. Tap "Next →" | Contact Phone = 8272930 | 1. Hiển thị "Please enter a valid phone number"; không điều hướng | High | 14739:17824 |
| PPAC_DTSC_DETAILS_TC_033 | M02 | Nhiều field bắt buộc trống cùng lúc → hiển thị tất cả lỗi | Màn Your Details trống (trừ pre-filled) | 1. Để trống toàn bộ field bắt buộc<br>2. Tap "Next →" | all empty | 1. Mỗi field bắt buộc hiển thị lỗi tương ứng; không điều hướng | Medium | 14739:17824 |
| PPAC_DTSC_DETAILS_TC_034 | M02 | Verify KHÔNG có Sentinel Number & KHÔNG có Payroll (khác Rail) | Màn Your Details | 1. Cuộn toàn bộ form tìm "Sentinel Number" và "Payroll" | N/A | 1. KHÔNG có field "Sentinel Number" (BR-03) và KHÔNG có "Payroll provider" (BR-02) | Medium | 14739:17824 |
| PPAC_DTSC_DETAILS_TC_035 | M02 | Surname/Email/DOB pre-filled — Next được mà không sửa | Màn Your Details, 3 field có sẵn giá trị | 1. Không sửa Surname/Email/DOB<br>2. Điền các field bắt buộc còn lại hợp lệ<br>3. Tap "Next →" | giữ nguyên pre-filled | 1. Cho phép Next; 3 field pre-filled không yêu cầu nhập lại | Medium | 14739:17824 |

### M03 — References (1 Referee)

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority | Figma Ref |
|-------|--------|---------------|---------------|------------|-----------|-----------------|----------|-----------|
| PPAC_DTSC_REF_TC_040 | M03 | Hoàn thành 1 Referee đủ 4 field hợp lệ → sang Declaration | Đã hoàn thành Your Details, màn References | 1. Nhập Name, Contact Number, Contractor/Company Name, Project/Site<br>2. Tap "Next →" | Name = John Smith; Contact = 07700900001; Contractor/Company = ABC Build Ltd; Project = ABC Construction Site | 1. Cả 4 field nhận giá trị, không lỗi<br>2. Điều hướng tới Declaration (Privacy & Submit) | Critical | 14739:16223 |
| PPAC_DTSC_REF_TC_041 | M03 | Hiển thị intro 3 đoạn verbatim + đúng 1 khối Referee × 4 field | Màn References | 1. Quan sát intro và khối Referee | N/A | 1. Intro đủ 3 đoạn verbatim (xem requirements §4.3)<br>2. Có **đúng 1 khối "Referee"** với 4 field: Name, Contact Number, Contractor/Company Name, Project/Site you worked on | Medium | 14739:16223 |
| PPAC_DTSC_REF_TC_042 | M03 | Name trống → lỗi required | Màn References | 1. Bỏ trống Name<br>2. Tap "Next →" | Name = (empty) | 1. Hiển thị "Please fill in this field" tại Name; không điều hướng | High | 14739:16223 |
| PPAC_DTSC_REF_TC_043 | M03 | Contact Number sai định dạng → lỗi | Màn References | 1. Nhập Contact Number không hợp lệ<br>2. Tap "Next →" | Contact = 8272930 | 1. Hiển thị "Please enter a valid phone number" | High | 14739:16223 |
| PPAC_DTSC_REF_TC_044 | M03 | Contractor/Company Name trống → lỗi required | Màn References | 1. Bỏ trống Contractor/Company Name<br>2. Tap "Next →" | Contractor/Company = (empty) | 1. Hiển thị lỗi required tại Contractor/Company Name; không điều hướng | High | 14739:16223 |
| PPAC_DTSC_REF_TC_045 | M03 | Project/Site trống → lỗi required | Màn References | 1. Bỏ trống Project/Site you worked on<br>2. Tap "Next →" | Project/Site = (empty) | 1. Hiển thị lỗi required tại Project/Site; không điều hướng | High | 14739:16223 |
| PPAC_DTSC_REF_TC_046 | M03 | Bỏ trống toàn bộ 4 field → tất cả báo lỗi | Màn References trống | 1. Không nhập gì<br>2. Tap "Next →" | all empty | 1. Cả 4 field hiển thị lỗi required; không điều hướng | Medium | 14739:16223 |
| PPAC_DTSC_REF_TC_047 | M03 | Verify CHỈ 1 Referee (khác Rail có 2) | Màn References | 1. Cuộn toàn bộ màn References | N/A | 1. **Chỉ 1 khối "Referee"** (không có "Referee 1"/"Referee 2"); không có khối referee thứ 2 | Medium | 14739:16223 |

### M09 — Declaration (Privacy & Submit) — *giống Rail*

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority | Figma Ref |
|-------|--------|---------------|---------------|------------|-----------|-----------------|----------|-----------|
| PPAC_DTSC_DECL_TC_120 | M09 | Bật đủ 4 toggle + Submit → lưu onboarding thành công | Đã qua References, màn Declaration (Privacy & Submit) | 1. Bật toggle Declaration<br>2. Bật Consent<br>3. Bật Marketing<br>4. Bật Initial Terms<br>5. Tap "Next →" (Submit) | cả 4 toggle ON | 1. Submit thành công; lưu onboarding (màn xác nhận hoàn tất) | Critical | 14739:13757 |
| PPAC_DTSC_DECL_TC_121 | M09 | Thiếu ≥1 toggle bắt buộc → không submit được | Màn Declaration, 1 toggle OFF | 1. Bật 3 toggle, để 1 toggle OFF<br>2. Tap "Next →" | 1 toggle OFF | 1. Không submit; "Please review and confirm this to continue." dưới toggle chưa bật | Critical | 14739:13757 |
| PPAC_DTSC_DECL_TC_122 | M09 | Hiển thị đủ 4 section đúng nội dung verbatim | Màn Declaration | 1. Quan sát 4 section | N/A | 1. Đủ 4 section verbatim: Declaration ("I hereby declare that all of the information entered on this form is true and accurate to the best of my knowledge and belief"), Consent ("I agree that my personal data may be processed in accordance with the DT Source Privacy Policy"), Marketing ("I agree that my personal data may be shared with selected partners for marketing purposes as described in the DT Source Privacy Policy"), Initial Terms ("I agree to the initial Terms and Work Finding Agreement") | Medium | 14739:13757 |
| PPAC_DTSC_DECL_TC_123 | M09 | Marketing toggle BẮT BUỘC (AMB-05) — để trống Marketing → chặn Submit | Màn Declaration, 3 toggle khác ON, Marketing OFF | 1. Bật Declaration + Consent + Initial Terms<br>2. Để Marketing OFF<br>3. Tap "Next →" | Marketing OFF | 1. Không submit; "Please review and confirm this to continue." dưới Marketing (⚠️ lệch chuẩn GDPR opt-in, BA review) | High | 14739:13757 |
| PPAC_DTSC_DECL_TC_124 | M09 | Link "DT Source Privacy Policy" (Consent) mở được policy | Màn Declaration | 1. Tap link "DT Source Privacy Policy" ở Consent | N/A | 1. Mở/điều hướng tới Privacy Policy (link gạch chân tappable) | Medium | 14739:13757 |
| PPAC_DTSC_DECL_TC_125 | M09 | Link "DT Source Privacy Policy" (Marketing) mở được policy | Màn Declaration | 1. Tap link ở Marketing | N/A | 1. Mở/điều hướng tới Privacy Policy | Low | 14739:13757 |
| PPAC_DTSC_DECL_TC_126 | M09 | Initial Terms toggle text đúng verbatim | Màn Declaration | 1. Đọc text Initial Terms | N/A | 1. Text đúng "I agree to the initial Terms and Work Finding Agreement" | Low | 14739:13757 |
| PPAC_DTSC_DECL_TC_127 | M09 | Để trống toàn bộ 4 toggle → chặn submit | Màn Declaration trống | 1. Không bật toggle nào<br>2. Tap "Next →" | all OFF | 1. Không submit; lỗi yêu cầu bật các toggle bắt buộc | Low | 14739:13757 |
| PPAC_DTSC_DECL_TC_128 | M09 | Nút cuối "Next →" thực hiện Submit (AMB-07) | Màn Declaration, cả 4 toggle ON | 1. Quan sát nhãn nút<br>2. Tap nút | N/A | 1. Nút "Next →" (req gọi "Submit"); tap thực hiện submit onboarding | High | 14739:13757 |
| PPAC_DTSC_DECL_TC_129 | M09 | Mỗi toggle bật/tắt độc lập | Màn Declaration | 1. Bật/tắt lần lượt từng toggle | N/A | 1. Mỗi toggle thay đổi độc lập, không ảnh hưởng toggle khác | Low | 14739:13757 |

### M10 — End-to-End (Construction)

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority | Figma Ref |
|-------|--------|---------------|---------------|------------|-----------|-----------------|----------|-----------|
| PPAC_DTSC_E2E_TC_140 | M10 | E2E happy path Construction — hoàn tất 3 form → submit thành công | Worker đã qua Verify identity, prefix DTSOURCE-CONSTRUCTION | 1. Your Details: điền đủ field bắt buộc (Consultant/Trade/Qualification/Phone/Address/City/Postcode/NIN/Where hear/Next of Kin)<br>2. References: 1 referee × 4 field<br>3. Declaration: bật đủ 4 toggle + Submit | NIN = AB123456C; Phone = 07700900111 | 1. Đi qua đúng 3 form không bị chặn<br>2. Submit thành công; dữ liệu onboarding được lưu | Critical | 13380:5796 |
| PPAC_DTSC_E2E_TC_141 | M10 | E2E negative — dừng giữa chừng rồi quay lại (resume) | Worker đã hoàn thành Your Details rồi thoát app | 1. Mở lại onboarding, nhập prefix DTSOURCE-CONSTRUCTION<br>2. Quan sát hành vi resume<br>3. Tiếp tục onboarding | onboarding pending tại References | 1. ⚠️ Verify: có dialog "Continue onboarding?" như Ballycommon/Rail không; nếu có → quay lại đúng màn dở dang, giữ dữ liệu | High | 13380:5796 |

---

## Tổng kết coverage

| Module | TC range | Số TC | Ghi chú |
|--------|----------|-------|---------|
| M01 Prefix | TC_001–002 | 2 | ⚠️ inferred, verify Figma |
| M02 Your Details | TC_010–035 | 26 | giống Rail **bỏ Sentinel Number**; Address line 2 optional |
| M03 References | TC_040–047 | 8 | **1 Referee × 4 field** (khác Rail 2 referee) |
| M09 Declaration | TC_120–129 | 10 | giống Rail (4 toggle, Marketing required) |
| M10 E2E | TC_140–141 | 2 | happy path 3 form + resume |
| **Tổng** | | **48** | Critical 6 / High 19 / Medium 18 / Low 5 |

> Node-id thật từ Figma `13380:5796` (2026-06-17). Branding "Ballycommon" còn sót ở Prefix do Design phụ trách — **không** đưa vào test case. Còn verify: hành vi resume (TC_141), đúng biến thể References Construction (`14739:16223` — frame 1-referee).
