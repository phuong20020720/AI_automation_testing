# PPAC Mobile — DTSource RAIL Worker Onboarding

> **Hệ thống:** PPAC Mobile — Worker Onboarding
> **Module:** DTSource (`dt • source`) · Sector = **Rail** — 8 form
> **Nguồn design (Figma):** "[NEW] PPAC New Design 2025" — file `ZzysHnguFvUu15zJyg6rjy`, section DTSource `13380:5796`. Cột "Figma Ref" = node-id frame từng màn.
> **Nguồn requirements:** [[requirements_dtsource_rail_onboarding]]
> **Phương pháp:** RBT — EP · BVA · Decision Table · State Transition · UI-state checks
> **Ngày tạo:** 2026-06-17
> **QA Owner:** maya.do@ppac.co.uk
> **Tổng số TC:** 95 (Critical 10 / High 34 / Medium 32 / Low 19)
> **Baseline:** đối chiếu [[requirements_ballycommon_rail_onboarding]] — xem mục "Khác biệt vs Ballycommon" trong requirements doc.

---

## Phạm vi

**Trong scope — DTSource + Rail, 8 form:**
- M02 Your Details · M03 References · M04 Medical Self-Certification · M05 Contract of Sentinel Scheme Sponsorship
- M06 PPE · M07 Safety Critical Certifications (tab "Pre-Deployment") · M08 Lost & Stolen Sentinel Cards · M09 Declaration (Privacy & Submit) · M10 E2E

**Ngoài scope:**
- **Surname · Email · Date of Birth** — pre-filled từ bước "Verify your identity" trước đó (đã validate ở bước đó).
- Payroll provider (DTSource **không có**).
- Sector Construction; màn Prefix DTSource (chưa có screenshot — M01 chỉ liệt kê tối thiểu, ⚠️ verify).

**Kỹ thuật thiết kế:** EP (NIN, phone) · BVA (NIN số ký tự) · Decision Table (Medical all-NO/any-YES + toggle; Safety gate + cert) · State Transition (multi-form flow, conditional fields, toggle hiện/ẩn) · UI-state (label/placeholder/error verbatim từ design).

## Quy ước Test Data

- Traceable: `auto_dtsrail_<module>_<TCnum>_20260617` (vd `auto_dtsrail_details_010_20260617`).
- Email: pre-filled (ngoài scope) — khi cần ghi: `auto_dtsrail_<TCnum>_20260617@yopmail.com`.
- NIN hợp lệ mẫu: `AB123456C` (format `AA999999A`).
- Mỗi TC độc lập; reset onboarding ở Pre-Condition.


---

## Test Cases

### M01 — Prefix / Select Sector 

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority | Figma Ref |
|-------|--------|---------------|---------------|------------|-----------|-----------------|----------|-----------|
| PPAC_DTS_PREFIX_TC_001 | M01 | Nhập prefix DTSOURCE → chọn Rail → vào onboarding Rail | Worker mở onboarding link, màn nhập company prefix | 1. Gõ "DTSOURCE" vào ô company name<br>2. Chọn "DTSOURCE - RAIL" từ gợi ý<br>3. Tap "Continue →" | prefix = DTSOURCE; chọn = DTSOURCE - RAIL | 1. Vào journey Rail; điều hướng tới form Your Details | Critical | 14739:19117 |
| PPAC_DTS_PREFIX_TC_002 | M01 | Có cả Rail & Construction trong gợi ý — chọn Rail | Màn nhập prefix, đã gõ "DTSOURCE" | 1. Quan sát danh sách gợi ý<br>2. Chọn "DTSOURCE - RAIL" | prefix = DTSOURCE | 1. Gợi ý hiển thị cả "DTSOURCE - RAIL" và "DTSOURCE - CONSTRUCTION"; chọn Rail → vào flow Rail  | Medium | 14739:19117 |

### M02 — Your Details (tab "Onboarding Details")

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority | Figma Ref |
|-------|--------|---------------|---------------|------------|-----------|-----------------|----------|-----------|
| PPAC_DTS_DETAILS_TC_010 | M02 | Nhập đầy đủ Your Details hợp lệ (Rail) → sang References | Đã vào onboarding Rail, màn Your Details; Surname/Email/DOB pre-filled | 1. Chọn Consultant<br>2. Chọn ≥1 Trade, ≥1 Qualification<br>3. Nhập Candidate's Mobile Phone hợp lệ<br>4. Chọn Street address từ gợi ý, nhập City + ZIP/Postcode<br>5. Nhập NIN + Sentinel Number<br>6. Chọn "Where did you hear about us?"<br>7. Nhập Next of Kin Name + Relationship + Contact Phone hợp lệ<br>8. Tap "Next →" | NIN = AB123456C; Sentinel = SEN20260617; Phone = 07700900111; Next of Kin Phone = 07700900222 | 1. Tất cả field nhận giá trị, không lỗi<br>2. Điều hướng tới form References | Critical | 14739:16526 |
| PPAC_DTS_DETAILS_TC_011 | M02 | Hiển thị đúng cấu trúc nhóm + giá trị pre-filled | Màn Your Details | 1. Quan sát nhóm Personal / Address / Next of Kin<br>2. Quan sát Surname/Email/DOB | N/A | 1. Đủ nhóm; Sentinel Number hiển thị (Rail); helper Next of Kin "Please provide details of someone we can contact in case of emergency."<br>2. Surname/Email/DOB hiển thị **giá trị pre-filled** sẵn (không cần nhập lại) | Medium | 14739:16526 |
| PPAC_DTS_DETAILS_TC_012 | M02 | Consultant không chọn → lỗi required | Màn Your Details, các field khác hợp lệ | 1. Bỏ trống Consultant<br>2. Tap "Next →" | Consultant = (none) | 1. Hiển thị lỗi required tại Consultant; không điều hướng | High | 14739:16526 |
| PPAC_DTS_DETAILS_TC_013 | M02 | Consultant sheet — single-select + Search + CheckCircle (Infinity API) | Màn Your Details | 1. Mở dropdown Consultant<br>2. Quan sát sheet "Select Consultant" + ô Search<br>3. Gõ từ khóa, chọn 1 item | keyword tùy | 1. Sheet "Select Consultant" mở, có ô "Search", danh sách từ API; chọn 1 item → đánh dấu **CheckCircle** xanh (single-select) | High | 14739:16526 |
| PPAC_DTS_DETAILS_TC_014 | M02 | Trade không chọn → lỗi required | Màn Your Details | 1. Bỏ trống Trade<br>2. Tap "Next →" | Trade = (none) | 1. Hiển thị lỗi required tại Trade; không điều hướng | High | 14739:16526 |
| PPAC_DTS_DETAILS_TC_015 | M02 | Trade multi-select sheet (checkbox + Search + Done) → tag ✕ | Màn Your Details | 1. Mở sheet "Select Trade" (checkbox vuông + Search + "Done")<br>2. Tick "A-level", "Abrasive Wheel"<br>3. Tap "Done"<br>4. Bỏ 1 tag bằng ✕ | chọn A-level, Abrasive Wheel | 1. Sheet multi-select (checkbox), tap "Done" xác nhận<br>2. Lựa chọn hiện dạng **tag có ✕** ở field Trade; tap ✕ xóa được tag | Medium | 14739:16526 |
| PPAC_DTS_DETAILS_TC_016 | M02 | Qualification không chọn → lỗi required | Màn Your Details | 1. Bỏ trống Qualification<br>2. Tap "Next →" | Qualification = (none) | 1. Hiển thị lỗi required tại Qualification; không điều hướng | High | 14739:16526 |
| PPAC_DTS_DETAILS_TC_017 | M02 | Qualification multi-select sheet ("Search your qualification" + Done) → tag ✕ | Màn Your Details | 1. Mở sheet "Select your qualification" (ô "Search your qualification" + "Done")<br>2. Tick 2+ option<br>3. Tap "Done" | chọn 2 option | 1. Sheet multi-select; tap "Done"<br>2. Lựa chọn hiện dạng tag có ✕ ở field Qualification; bỏ tag được | Medium | 14739:16526 |
| PPAC_DTS_DETAILS_TC_018 | M02 | Candidate's Mobile Phone sai định dạng → lỗi | Màn Your Details, field khác hợp lệ | 1. Nhập Candidate's Mobile Phone sai<br>2. Tap "Next →" | Candidate's Mobile Phone = 8272930 | 1. Hiển thị "Please enter a valid phone number"; không điều hướng | High | 14739:16526 |
| PPAC_DTS_DETAILS_TC_019 | M02 | Candidate's Mobile Phone để trống → lỗi required | Màn Your Details | 1. Để trống Candidate's Mobile Phone<br>2. Tap "Next →" | Phone = (empty) | 1. Hiển thị lỗi required tại Candidate's Mobile Phone; không điều hướng | High | 14739:16526 |
| PPAC_DTS_DETAILS_TC_020 | M02 | Street address autocomplete — gõ → gợi ý → chọn | Màn Your Details | 1. Gõ "Syra" vào Street address<br>2. Quan sát dropdown gợi ý<br>3. Chọn 1 gợi ý | gõ = "Syra" | 1. Field focus viền xanh; dropdown gợi ý hiển thị (vd "Syracuse, Connecticut", "Lansing, Illinois") kèm dấu ✓; chọn 1 → điền vào ô | High | 14739:16526 |
| PPAC_DTS_DETAILS_TC_021 | M02 | Street address để trống → lỗi required | Màn Your Details | 1. Để trống Street address<br>2. Tap "Next →" | Street address = (empty) | 1. Hiển thị lỗi required tại Street address; không điều hướng | High | 14739:16526 |
| PPAC_DTS_DETAILS_TC_022 | M02 | Address line 2 OPTIONAL — để trống vẫn Next được | Màn Your Details, các field bắt buộc hợp lệ, Address line 2 trống | 1. Để trống Address line 2 ("Door, Flat, House Name")<br>2. Tap "Next →" | Address line 2 = (empty) | 1. KHÔNG báo lỗi tại Address line 2; điều hướng sang References bình thường (field optional) | Medium | 14739:16526 |
| PPAC_DTS_DETAILS_TC_023 | M02 | City để trống → lỗi required | Màn Your Details | 1. Để trống City<br>2. Tap "Next →" | City = (empty) | 1. Hiển thị lỗi required tại City; không điều hướng | Medium | 14739:16526 |
| PPAC_DTS_DETAILS_TC_024 | M02 | ZIP / Postcode để trống → lỗi required | Màn Your Details | 1. Để trống ZIP/Postcode<br>2. Tap "Next →" | Postcode = (empty) | 1. Hiển thị lỗi required tại ZIP/Postcode; không điều hướng | Medium | 14739:16526 |
| PPAC_DTS_DETAILS_TC_025 | M02 | NIN sai format → lỗi format | Màn Your Details | 1. Nhập NIN sai định dạng<br>2. Tap "Next →" | NIN = MIDORI | 1. Hiển thị "Please enter the right format: 2 letters, 6 numbers, 1 letter (e.g. AA999999A)"; không điều hướng | High | 14739:16526 |
| PPAC_DTS_DETAILS_TC_026 | M02 | NIN đúng format AA999999A → chấp nhận | Màn Your Details | 1. Nhập NIN đúng định dạng<br>2. Rời focus | NIN = AB123456C | 1. Không lỗi NIN; field nhận giá trị | High | 14739:16526 |
| PPAC_DTS_DETAILS_TC_027 | M02 | NIN sai số ký tự (boundary) → lỗi | Màn Your Details | 1. Nhập NIN thiếu 1 số (AB12345C)<br>2. Tap "Next →" | NIN = AB12345C | 1. Hiển thị lỗi format NIN | Medium | 14739:16526 |
| PPAC_DTS_DETAILS_TC_028 | M02 | Sentinel Number để trống (Rail) → lỗi required | Màn Your Details, field khác hợp lệ | 1. Để trống Sentinel Number<br>2. Tap "Next →" | Sentinel = (empty) | 1. Hiển thị lỗi required tại Sentinel Number; không điều hướng | High | 14739:16526 |
| PPAC_DTS_DETAILS_TC_029 | M02 | Sentinel Number hợp lệ → chấp nhận | Màn Your Details | 1. Nhập Sentinel Number<br>2. Rời focus | Sentinel = SEN20260617 | 1. Không lỗi; field nhận giá trị | Medium | 14739:16526 |
| PPAC_DTS_DETAILS_TC_030 | M02 | "Where did you hear about us?" không chọn → lỗi required | Màn Your Details | 1. Bỏ trống "Where did you hear about us?"<br>2. Tap "Next →" | = (none) | 1. Hiển thị lỗi required; không điều hướng (field bắt buộc) | Medium | 14739:16526 |
| PPAC_DTS_DETAILS_TC_031 | M02 | "Where did you hear about us?" sheet single-select + Search + CheckCircle | Màn Your Details | 1. Mở dropdown<br>2. Quan sát sheet + ô Search<br>3. Chọn 1 option | option bất kỳ | 1. Sheet "Where did you hear about us?" có Search; chọn 1 → CheckCircle xanh (single-select) | Low | 14739:16526 |
| PPAC_DTS_DETAILS_TC_032 | M02 | Next of Kin Name để trống → lỗi required | Màn Your Details | 1. Để trống Next of Kin Name<br>2. Tap "Next →" | Next of Kin Name = (empty) | 1. Hiển thị "Please fill in this field" tại Next of Kin Name; không điều hướng | High | 14739:16526 |
| PPAC_DTS_DETAILS_TC_033 | M02 | Relationship to Candidate để trống → lỗi required (placeholder gợi ý) | Màn Your Details | 1. Quan sát placeholder<br>2. Để trống Relationship<br>3. Tap "Next →" | Relationship = (empty) | 1. Placeholder "e.g. Parent, Spouse, Sibling" hiển thị<br>2. Để trống → lỗi required; không điều hướng | Medium | 14739:16526 |
| PPAC_DTS_DETAILS_TC_034 | M02 | Next of Kin Contact Phone sai định dạng → lỗi | Màn Your Details | 1. Nhập Contact Phone Number sai<br>2. Tap "Next →" | Contact Phone = 8272930 | 1. Hiển thị "Please enter a valid phone number"; không điều hướng | High | 14739:16526 |
| PPAC_DTS_DETAILS_TC_035 | M02 | Nhiều field bắt buộc trống cùng lúc → hiển thị tất cả lỗi | Màn Your Details trống (trừ pre-filled) | 1. Để trống toàn bộ field bắt buộc<br>2. Tap "Next →" | all empty | 1. Mỗi field bắt buộc hiển thị lỗi tương ứng; không điều hướng | Medium | 14739:16526 |
| PPAC_DTS_DETAILS_TC_036 | M02 | Verify KHÔNG có field Payroll provider (khác Ballycommon) | Màn Your Details | 1. Cuộn toàn bộ form, tìm field Payroll provider | N/A | 1. KHÔNG có field "payroll provider"/"CWG"/"Workwell" trên màn (DTSource bỏ payroll — BR-02) | Medium | 14739:16526 |
| PPAC_DTS_DETAILS_TC_037 | M02 | Surname/Email/DOB pre-filled — Next được mà không sửa | Màn Your Details, Surname/Email/DOB có sẵn giá trị | 1. Không sửa Surname/Email/DOB<br>2. Điền các field bắt buộc còn lại hợp lệ<br>3. Tap "Next →" | giữ nguyên pre-filled | 1. Cho phép Next bình thường; 3 field pre-filled không yêu cầu nhập lại (document: read-only hay editable) | Medium | 14739:16526 |
| PPAC_DTS_DETAILS_TC_038 | M02 | Back từ Your Details giữ dữ liệu đã nhập | Đã nhập 1 phần Your Details | 1. Tap Back<br>2. Quay lại Your Details | dữ liệu một phần | 1. Document actual: dữ liệu được giữ hay reset | Low | 14739:16526 |

### M03 — References

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority | Figma Ref |
|-------|--------|---------------|---------------|------------|-----------|-----------------|----------|-----------|
| PPAC_DTS_REF_TC_040 | M03 | Hoàn thành đủ 2 Referee × 4 field hợp lệ → sang Medical | Đã hoàn thành Your Details, màn References | 1. Referee 1: Name, Contact Number, Contractor/Company Name, Project/Site<br>2. Referee 2: Name, Contact Number, Contractor/Company Name, Project/Site<br>3. Tap "Next →" | R1: John Smith / 07700900001 / ABC Rail Ltd / ABC Construction Site<br>R2: Mary Jones / 07700900002 / XYZ Rail Ltd / XYZ Depot | 1. Cả 8 field nhận giá trị, không lỗi<br>2. Điều hướng tới Medical Self-Certification | Critical | 14739:15920 |
| PPAC_DTS_REF_TC_041 | M03 | Hiển thị intro 3 đoạn verbatim + đúng 2 khối Referee × 4 field | Màn References | 1. Quan sát intro và 2 khối Referee | N/A | 1. Intro đủ 3 đoạn verbatim (xem requirements §4.3)<br>2. Có Referee 1 & Referee 2, mỗi khối đúng 4 field: Name, Contact Number, Contractor/Company Name, Project/Site you worked on | Medium | 14739:15920 |
| PPAC_DTS_REF_TC_042 | M03 | Referee 1 Name trống → lỗi required | Màn References | 1. Bỏ trống Name của Referee 1<br>2. Tap "Next →" | R1 Name = (empty) | 1. Hiển thị "Please fill in this field" tại Name; không điều hướng | High | 14739:15920 |
| PPAC_DTS_REF_TC_043 | M03 | Referee 1 Contact Number sai định dạng → lỗi | Màn References | 1. Nhập Contact Number không hợp lệ<br>2. Tap "Next →" | R1 Contact = 8272930 | 1. Hiển thị "Please enter a valid phone number" | High | 14739:15920 |
| PPAC_DTS_REF_TC_044 | M03 | Referee 1 Contractor/Company Name trống → lỗi required | Màn References | 1. Bỏ trống Contractor/Company Name R1<br>2. Tap "Next →" | R1 Contractor/Company = (empty) | 1. Hiển thị lỗi required tại Contractor/Company Name; không điều hướng | High | 14739:15920 |
| PPAC_DTS_REF_TC_045 | M03 | Referee 1 Project/Site trống → lỗi required | Màn References | 1. Bỏ trống Project/Site you worked on R1<br>2. Tap "Next →" | R1 Project/Site = (empty) | 1. Hiển thị lỗi required tại Project/Site; không điều hướng | High | 14739:15920 |
| PPAC_DTS_REF_TC_046 | M03 | Chỉ điền Referee 1, bỏ trống Referee 2 → lỗi required | Màn References | 1. Hoàn thành đủ Referee 1<br>2. Bỏ trống Referee 2<br>3. Tap "Next →" | R2 = (empty) | 1. Hiển thị lỗi required tại 4 field Referee 2; không điều hướng (phải đủ 2 referee) | High | 14739:15920 |
| PPAC_DTS_REF_TC_047 | M03 | Bỏ trống toàn bộ 8 field → tất cả báo lỗi | Màn References trống | 1. Không nhập gì<br>2. Tap "Next →" | all empty | 1. Cả 8 field (2 referee × 4) hiển thị lỗi required; không điều hướng | Medium | 14739:15920 |
| PPAC_DTS_REF_TC_048 | M03 | Contact Number hợp lệ → chấp nhận | Màn References | 1. Nhập Contact Number hợp lệ<br>2. Rời focus | Contact = 07700900001 | 1. Không lỗi phone | Medium | 14739:15920 |
| PPAC_DTS_REF_TC_049 | M03 | Verify KHÔNG có Type/Relationship (khác Ballycommon) — chỉ 4 field cố định | Màn References | 1. Quan sát các field mỗi khối Referee | N/A | 1. KHÔNG có dropdown "Reference Type" (Personal/Employer), KHÔNG có "Relationship to Candidate"; 4 field (Name/Contact/Contractor-Company/Project-Site) **luôn hiển thị**, không phụ thuộc điều kiện | Medium | 14739:15920 |
| PPAC_DTS_REF_TC_050 | M03 | Cùng dữ liệu cho cả 2 referee → behavior trùng lặp | Màn References | 1. Nhập Referee 1 và Referee 2 trùng thông tin<br>2. Tap "Next →" | R1 = R2 (trùng) | 1. Document actual: hệ thống cho phép hay cảnh báo trùng referee | Low | 14739:15920 |

### M04 — Medical Self-Certification

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority | Figma Ref |
|-------|--------|---------------|---------------|------------|-----------|-----------------|----------|-----------|
| PPAC_DTS_MED_TC_060 | M04 | Trả lời tất cả 12 câu = NO → hiện toggle confirmation | Đã hoàn thành References, màn Medical Self-Certification | 1. Chọn "No" cho cả 12 câu<br>2. Quan sát cuối form | all = No | 1. Toggle "I confirm that I have selected 'NO' to all of the medical self-certification declarations above." hiển thị | Critical | 14739:15018 |
| PPAC_DTS_MED_TC_061 | M04 | All NO + bật toggle → sang Contract | Đã chọn NO cả 12 câu, toggle hiển thị | 1. Bật toggle confirmation<br>2. Tap "Next →" | all NO + toggle ON | 1. Điều hướng tới Contract of Sentinel Scheme Sponsorship | Critical | 14739:15018 |
| PPAC_DTS_MED_TC_062 | M04 | Có ≥1 câu = YES → ẩn toggle confirmation | Màn Medical Self-Certification | 1. Chọn "Yes" câu 1, "No" các câu còn lại<br>2. Quan sát cuối form | Q1=Yes, còn lại No | 1. Toggle confirmation KHÔNG hiển thị (BR-05) | High | 14739:15018 |
| PPAC_DTS_MED_TC_063 | M04 | Bỏ trống ≥1 câu → lỗi | Màn Medical Self-Certification | 1. Để trống câu 2<br>2. Trả lời các câu khác<br>3. Tap "Next →" | Q2 = (chưa trả lời) | 1. Hiển thị "Please answer this question" tại câu 2; không điều hướng | High | 14739:15018 |
| PPAC_DTS_MED_TC_064 | M04 | All NO nhưng KHÔNG bật toggle → chặn Next | Đã chọn NO cả 12 câu, toggle hiển thị nhưng OFF | 1. Không bật toggle<br>2. Tap "Next →" | all NO, toggle OFF | 1. Không điều hướng; hiển thị lỗi đỏ "Please confirm this to continue." dưới toggle | High | 14739:15018 |
| PPAC_DTS_MED_TC_065 | M04 | Hiển thị đúng intro railway + 12 câu đúng nội dung/thứ tự | Màn Medical Self-Certification | 1. Quan sát intro và danh sách câu hỏi | N/A | 1. Intro đủ 2 đoạn verbatim (đoạn thường + đoạn bold "When you declare NO…"); đủ 12 câu đúng thứ tự (xem requirements §5) | Medium | 14739:15018 |
| PPAC_DTS_MED_TC_066 | M04 | Đổi câu cuối YES→NO khi 11 câu NO → toggle xuất hiện (State Transition) | 11 câu NO, câu 10 = YES (toggle ẩn) | 1. Đổi câu 10 từ Yes sang No<br>2. Quan sát toggle | Q10 Yes→No | 1. Khi toàn bộ thành NO → toggle confirmation xuất hiện | Medium | 14739:15018 |
| PPAC_DTS_MED_TC_067 | M04 | Đổi 1 câu NO→YES sau khi đã bật toggle → toggle ẩn + reset (State Transition) | All NO, đã bật toggle | 1. Đổi câu 3 sang Yes<br>2. Quan sát toggle | Q3 No→Yes | 1. Toggle confirmation ẩn (và trạng thái ON được reset) | Medium | 14739:15018 |
| PPAC_DTS_MED_TC_068 | M04 | Tất cả câu = YES → vẫn cho tiếp tục, không có toggle | Màn Medical Self-Certification | 1. Chọn Yes cả 12 câu<br>2. Tap "Next →" | all YES | 1. Không có toggle confirmation; điều hướng tới Contract (document hành vi với YES) | Medium | 14739:15018 |
| PPAC_DTS_MED_TC_069 | M04 | Câu 12 (Hand/Arm vibration) hiển thị đúng có dấu * | Màn Medical Self-Certification | 1. Quan sát câu 12 | N/A | 1. Câu 12 "Have you experienced any Hand/Arm problems from operating vibrating equipment?*" hiển thị đúng (có dấu * cuối câu) | Low | 14739:15018 |
| PPAC_DTS_MED_TC_070 | M04 | Tất cả 12 câu chưa trả lời → tất cả báo lỗi | Màn Medical Self-Certification trống | 1. Không trả lời câu nào<br>2. Tap "Next →" | all empty | 1. Mỗi câu hiển thị "Please answer this question" | Low | 14739:15018 |
| PPAC_DTS_MED_TC_071 | M04 | Mỗi câu chỉ chọn 1 Yes/No (radio mutually exclusive) | Màn Medical Self-Certification | 1. Chọn Yes câu 1<br>2. Chọn No câu 1 | Q1 Yes→No | 1. Chỉ 1 lựa chọn active; chọn No bỏ chọn Yes | Medium | 14739:15018 |
| PPAC_DTS_MED_TC_072 | M04 | Toggle confirmation text đúng verbatim | All NO, toggle hiển thị | 1. Đọc text toggle | N/A | 1. Text đúng "I confirm that I have selected 'NO' to all of the medical self-certification declarations above." | Low | 14739:15018 |

### M05 — Contract of Sentinel Scheme Sponsorship (tab "Sentinel Scheme Contract")

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority | Figma Ref |
|-------|--------|---------------|---------------|------------|-----------|-----------------|----------|-----------|
| PPAC_DTS_CONTRACT_TC_080 | M05 | Tick acceptance → sang PPE | Đã qua Medical, màn Contract | 1. Cuộn đọc nội dung hợp đồng<br>2. Tick checkbox acceptance<br>3. Tap "Next →" | checkbox checked | 1. Điều hướng tới PPE | Critical | 14739:14874 |
| PPAC_DTS_CONTRACT_TC_081 | M05 | Không tick acceptance → chặn Next | Màn Contract | 1. Không tick checkbox<br>2. Tap "Next →" | checkbox unchecked | 1. Không điều hướng; hiển thị lỗi đỏ "Please review and confirm this to continue." dưới checkbox | High | 14739:14874 |
| PPAC_DTS_CONTRACT_TC_082 | M05 | Hiển thị đủ 5 section + Sentinel Scheme Declaration | Màn Contract | 1. Cuộn toàn bộ nội dung | N/A | 1. Đủ 5 section đánh số: 1. Duties · 2. Candidate Responsibilities · 3. Primary Sponsor Responsibilities · 4. Misconduct · 5. Withdrawal of Sentinel Competence Cards; + section "Sentinel Scheme Declaration" cuối | Medium | 14739:14874 |
| PPAC_DTS_CONTRACT_TC_083 | M05 | Acceptance checkbox text đúng verbatim | Màn Contract | 1. Đọc text checkbox | N/A | 1. Text đúng "I have read, and agree to be bound by, the above Contract of Sentinel Scheme Sponsorship." | Low | 14739:14874 |
| PPAC_DTS_CONTRACT_TC_084 | M05 | Nội dung scroll được, sponsor = "Dt Source", không e-signature | Màn Contract | 1. Cuộn nội dung dài<br>2. Quan sát tên sponsor trong text<br>3. Tìm trường ký tên | N/A | 1. Nội dung cuộn được; text dùng "Dt Source" (không phải Ballycommon); chấp nhận chỉ qua checkbox (không có ô e-signature) | Low | 14739:14874 |

### M06 — PPE

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority | Figma Ref |
|-------|--------|---------------|---------------|------------|-----------|-----------------|----------|-----------|
| PPAC_DTS_PPE_TC_090 | M06 | Trả lời đủ 9 item → sang Pre-Deployment (Safety Critical) | Đã qua Contract, màn PPE | 1. Chọn Yes/No cho từng item trong 9 item<br>2. Tap "Next →" | tất cả = Yes | 1. Điều hướng tới Safety Critical Certifications (tab Pre-Deployment) | High | 14739:14725 |
| PPAC_DTS_PPE_TC_091 | M06 | Hiển thị đủ 9 item đúng tên + intro | Màn PPE | 1. Quan sát intro và danh sách item | N/A | 1. Intro "Please indicate below which items of Personal Protective Equipment (PPE) you are in possession of."; đủ 9 item: Safety Shoes / Boots, Bump / Hard Hat, H.V. Vests, H.V. Clothing, Ear Protection, Eye Protection, Respiratory Equipment, Overalls, Gloves | Medium | 14739:14725 |
| PPAC_DTS_PPE_TC_092 | M06 | Bỏ trống ≥1 item → lỗi | Màn PPE | 1. Để trống item "Bump / Hard Hat"<br>2. Trả lời các item khác<br>3. Tap "Next →" | Bump/Hard Hat = (chưa chọn) | 1. Hiển thị "Please answer this question" tại item đó; không điều hướng | High | 14739:14725 |
| PPAC_DTS_PPE_TC_093 | M06 | Tất cả item = No → vẫn cho tiếp tục | Màn PPE | 1. Chọn No cho cả 9 item<br>2. Tap "Next →" | all No | 1. Điều hướng tới Safety Critical (document hành vi khi không sở hữu PPE) | Medium | 14739:14725 |
| PPAC_DTS_PPE_TC_094 | M06 | Trả lời hỗn hợp Yes/No → lưu đúng từng item | Màn PPE | 1. Chọn Yes item lẻ, No item chẵn<br>2. Tap "Next →" | mixed Yes/No | 1. Mỗi item lưu đúng lựa chọn; điều hướng | Low | 14739:14725 |
| PPAC_DTS_PPE_TC_095 | M06 | Mỗi item radio chỉ chọn 1 | Màn PPE | 1. Chọn Yes rồi No cho item "Safety Shoes / Boots" | toggle Yes→No | 1. Chỉ 1 lựa chọn active | Medium | 14739:14725 |
| PPAC_DTS_PPE_TC_096 | M06 | Bỏ trống toàn bộ → tất cả item báo lỗi | Màn PPE trống | 1. Không chọn item nào<br>2. Tap "Next →" | all empty | 1. Mỗi item hiển thị "Please answer this question" | Low | 14739:14725 |

### M07 — Safety Critical Certifications (tab "Pre-Deployment")

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority | Figma Ref |
|-------|--------|---------------|---------------|------------|-----------|-----------------|----------|-----------|
| PPAC_DTS_SAFETY_TC_100 | M07 | Gate = No → ẩn field detail; 16 cert vẫn hiển thị | Đã qua PPE, màn Safety Critical (tab Pre-Deployment) | 1. Chọn "No" cho "Are you subject to any medical restrictions?"<br>2. Quan sát form | gate = No | 1. Field "Please enter details of medical restrictions" KHÔNG hiển thị<br>2. Heading "Please provide below details of all competency restrictions" + 16 cert (Yes/No) VẪN hiển thị đầy đủ | High | 14739:14165 |
| PPAC_DTS_SAFETY_TC_101 | M07 | Gate = Yes → hiện field "details of medical restrictions" (required) | Màn Safety Critical | 1. Chọn "Yes" cho gate<br>2. Để trống field detail, tap "Next →" | gate = Yes, detail = (empty) | 1. Hiển thị field "Please enter details of medical restrictions" (placeholder "Please give us more details.", helper "Please be as specific as possible")<br>2. Bỏ trống → lỗi required, không điều hướng | High | 14739:14165 |
| PPAC_DTS_SAFETY_TC_102 | M07 | Gate = No + all 16 cert = No → Next sang Lost & Stolen | Màn Safety Critical, gate = No | 1. Chọn gate = No<br>2. Trả lời "No" cho cả 16 cert<br>3. Tap "Next →" | gate = No; all 16 cert = No | 1. Điều hướng tới Lost & Stolen Sentinel Cards | Critical | 14739:14165 |
| PPAC_DTS_SAFETY_TC_103 | M07 | Cert = Yes nhưng bỏ trống Duration → lỗi "Please enter the duration" | Màn Safety Critical, một cert (vd PTS AC) = Yes | 1. Để trống "PTS AC - Duration Held (Years & Months)"<br>2. Tap "Next →" | PTS AC = Yes, Duration = (empty) | 1. Hiển thị "Please enter the duration" tại Duration field của cert đó; không điều hướng | High | 14739:14165 |
| PPAC_DTS_SAFETY_TC_104 | M07 | Cert = Yes + nhập Duration hợp lệ → Next | Màn Safety Critical, gate = No | 1. Chọn 1 cert = Yes, nhập Duration; các cert còn lại = No<br>2. Tap "Next →" | PTS AC = Yes, Duration = "2 years 3 months"; còn lại = No | 1. Điều hướng tới Lost & Stolen Sentinel Cards | High | 14739:14165 |
| PPAC_DTS_SAFETY_TC_105 | M07 | Đổi gate Yes→No và cert Yes→No → field điều kiện ẩn (State Transition) | gate = Yes (detail hiển thị) + 1 cert = Yes (Duration hiển thị) | 1. Đổi gate sang No<br>2. Đổi cert đó sang No<br>3. Quan sát | gate Yes→No; cert Yes→No | 1. Field "details of medical restrictions" ẩn<br>2. Field "[Cert] - Duration Held" của cert đó ẩn | Medium | 14739:14165 |
| PPAC_DTS_SAFETY_TC_106 | M07 | Gate chưa trả lời → lỗi required | Màn Safety Critical | 1. Không trả lời gate<br>2. Tap "Next →" | gate = (empty) | 1. Hiển thị "Please answer this question" | Medium | 14739:14165 |
| PPAC_DTS_SAFETY_TC_107 | M07 | Hiển thị 16 cert đúng tên + mỗi cert bắt buộc | Màn Safety Critical | 1. Quan sát heading + danh sách cert<br>2. Bỏ trống 1 cert, tap "Next →" | N/A | 1. Heading "Please provide below details of all competency restrictions"; đủ 16 cert: PTS AC, PTS DCCR, AOD PO, AOD LXA, COSS, LKT/SW, Level A, IWA, PC, PS, ES, MC/CC, LB 3rd – R ST-i, DLR Track Awareness, PICOW, Other<br>2. Bỏ trống 1 cert → "Please answer this question"; không điều hướng | Medium | 14739:14165 |
| PPAC_DTS_SAFETY_TC_108 | M07 | Duration field placeholder đúng verbatim | 1 cert = Yes (Duration hiển thị) | 1. Quan sát placeholder Duration field | N/A | 1. Placeholder "e.g. 2 years 3 months" hiển thị | Low | 14739:14165 |
| PPAC_DTS_SAFETY_TC_109 | M07 | "Other" cert = Yes → hiện field "Other competencies" nhập tự do | Màn Safety Critical, cert "Other" = Yes | 1. Chọn cert "Other" = Yes<br>2. Nhập text vào "Other competencies" | "Track Inspection L2" | 1. Hiện field "Other competencies" (placeholder "Please give us more details."); nhận giá trị text tự do | Low | 14739:14165 |
| PPAC_DTS_SAFETY_TC_110 | M07 | Tab tên "Pre-Deployment" nhưng tiêu đề màn "Safety Critical Certifications" | Màn Safety Critical | 1. Quan sát nhãn tab và tiêu đề màn | N/A | 1. Tab stepper hiển thị "Pre-Deployment"; tiêu đề màn hiển thị "Safety Critical Certifications" (AMB-04) | Low | 14739:14165 |

### M08 — Lost & Stolen Sentinel Cards

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority | Figma Ref |
|-------|--------|---------------|---------------|------------|-----------|-----------------|----------|-----------|
| PPAC_DTS_LOST_TC_115 | M08 | Bật toggle £25 + VAT → sang Declaration | Đã qua Safety Critical, màn Lost & Stolen | 1. Bật toggle xác nhận<br>2. Tap "Next →" | toggle ON | 1. Điều hướng tới Declaration (Privacy & Submit) | High | 14739:14073 |
| PPAC_DTS_LOST_TC_116 | M08 | Không bật toggle → chặn Next | Màn Lost & Stolen | 1. Không bật toggle<br>2. Tap "Next →" | toggle OFF | 1. Không điều hướng; hiển thị lỗi yêu cầu bật xác nhận"Please review and confirm this to continue. | High | 14739:14073 |
| PPAC_DTS_LOST_TC_117 | M08 | Tiêu đề màn hiển thị đúng | Màn Lost & Stolen | 1. Quan sát tiêu đề | N/A | 1. Tiêu đề "Lost & Stolen Sentinel Cards" hiển thị | Low | 14739:14073 |

### M09 — Declaration (Privacy & Submit)

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority | Figma Ref |
|-------|--------|---------------|---------------|------------|-----------|-----------------|----------|-----------|
| PPAC_DTS_DECL_TC_120 | M09 | Bật đủ 4 toggle + Submit → lưu onboarding thành công | Đã qua Lost & Stolen, màn Declaration (Privacy & Submit) | 1. Bật toggle Declaration<br>2. Bật toggle Consent<br>3. Bật toggle Marketing<br>4. Bật toggle Initial Terms<br>5. Tap "Next →" (Submit) | cả 4 toggle ON | 1. Submit thành công; hệ thống lưu onboarding (hiển thị màn xác nhận hoàn tất) | Critical | 14739:13757 |
| PPAC_DTS_DECL_TC_121 | M09 | Thiếu ≥1 toggle bắt buộc → không submit được | Màn Declaration, 1 toggle OFF | 1. Bật 3 toggle, để 1 toggle (vd Initial Terms) OFF<br>2. Tap "Next →" | 1 toggle OFF | 1. Không submit; hiển thị lỗi đỏ "Please review and confirm this to continue." dưới toggle chưa bật | Critical | 14739:13757 |
| PPAC_DTS_DECL_TC_122 | M09 | Hiển thị đủ 4 section đúng nội dung verbatim | Màn Declaration | 1. Quan sát 4 section | N/A | 1. Đủ 4 section đúng verbatim: Declaration ("I hereby declare that all of the information entered on this form is true and accurate to the best of my knowledge and belief"), Consent ("I agree that my personal data may be processed in accordance with the DT Source Privacy Policy"), Marketing ("I agree that my personal data may be shared with selected partners for marketing purposes as described in the DT Source Privacy Policy"), Initial Terms ("I agree to the initial Terms and Work Finding Agreement") | Medium | 14739:13757 |
| PPAC_DTS_DECL_TC_123 | M09 | Marketing toggle là BẮT BUỘC (AMB-05) — để trống Marketing → chặn Submit | Màn Declaration, 3 toggle khác ON, Marketing OFF | 1. Bật Declaration + Consent + Initial Terms<br>2. Để Marketing OFF<br>3. Tap "Next →" | Marketing OFF | 1. Không submit; hiển thị "Please review and confirm this to continue." | High | 14739:13757 |
| PPAC_DTS_DECL_TC_124 | M09 | Link "DT Source Privacy Policy" (Consent) mở được policy | Màn Declaration | 1. Tap link "DT Source Privacy Policy" ở mục Consent | N/A | 1. Mở/điều hướng tới trang Privacy Policy (link gạch chân tappable) | Medium | 14739:13757 |
| PPAC_DTS_DECL_TC_125 | M09 | Link "DT Source Privacy Policy" (Marketing) mở được policy | Màn Declaration | 1. Tap link "DT Source Privacy Policy" ở mục Marketing | N/A | 1. Mở/điều hướng tới trang Privacy Policy | Low | 14739:13757 |
| PPAC_DTS_DECL_TC_126 | M09 | Initial Terms toggle text đúng verbatim | Màn Declaration | 1. Đọc text mục Initial Terms | N/A | 1. Text đúng "I agree to the initial Terms and Work Finding Agreement" | Low | 14739:13757 |
| PPAC_DTS_DECL_TC_127 | M09 | Để trống toàn bộ 4 toggle → chặn submit | Màn Declaration trống | 1. Không bật toggle nào<br>2. Tap "Next →" | all OFF | 1. Không submit; hiển thị lỗi yêu cầu bật các toggle bắt buộc | Low | 14739:13757 |
| PPAC_DTS_DECL_TC_128 | M09 | Nút cuối "Next →" thực hiện Submit (AMB-07) | Màn Declaration, cả 4 toggle ON | 1. Quan sát nhãn nút<br>2. Tap nút | N/A | 1. Nút hiển thị "Next →" ; tap thực hiện submit onboarding | High | 14739:13757 |
| PPAC_DTS_DECL_TC_129 | M09 | Mỗi toggle bật/tắt độc lập | Màn Declaration | 1. Bật/tắt lần lượt từng toggle | N/A | 1. Mỗi toggle thay đổi trạng thái độc lập, không ảnh hưởng toggle khác | Low | 14739:13757 |

### M10 — End-to-End (Rail)

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority | Figma Ref |
|-------|--------|---------------|---------------|------------|-----------|-----------------|----------|-----------|
| PPAC_DTS_E2E_TC_140 | M10 | E2E happy path Rail — hoàn tất đủ 8 form → submit thành công | Worker đã qua Verify identity (Surname/Email/DOB sẵn), prefix DTSOURCE-RAIL | 1. Your Details: điền đủ field bắt buộc (Consultant/Trade/Qualification/Phone/Address/City/Postcode/NIN/Sentinel/Where hear/Next of Kin)<br>2. References: 2 referee × 4 field<br>3. Medical: all NO + bật toggle<br>4. Contract: tick acceptance<br>5. PPE: đủ 9 item<br>6. Safety Critical: gate=No + 16 cert=No<br>7. Lost & Stolen: bật £25+VAT<br>8. Declaration: bật đủ 4 toggle + Submit | NIN = AB123456C; Sentinel = SEN140; Phone = 07700900111 | 1. Đi qua đúng thứ tự 8 form không bị chặn<br>2. Submit thành công; dữ liệu onboarding được lưu | Critical | 13380:5796 |
| PPAC_DTS_E2E_TC_141 | M10 | E2E negative — dừng giữa chừng rồi quay lại (resume) | Worker đã hoàn thành tới Medical rồi thoát app | 1. Mở lại onboarding, nhập prefix DTSOURCE-RAIL<br>2. Quan sát hành vi resume<br>3. Tiếp tục onboarding | onboarding pending tại Medical | Quay lại đúng màn dở dang, giữ dữ liệu | High | 13380:5796 |

### Defects / Regression

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority | Figma Ref |
|-------|--------|---------------|---------------|------------|-----------|-----------------|----------|-----------|
| PPAC_DTS_DEF_TC_153 | M05 | DEF-02 chính tả UK — Contract mục Duties phải dùng "authorised", KHÔNG "authorized" | Màn Contract of Sentinel Scheme Sponsorship, mục Duties | 1. Đọc verbatim câu "…unless authority is first sort and given by an authorized Dt Source representative…" | N/A | 1. **BUG:** hiện dùng US "**authorized**" → kỳ vọng UK "**authorised**". Quét cả màn không còn từ US spelling (authorize/organize/recognize…) | High | 14739:14898 |
| PPAC_DTS_DEF_TC_154 | M05 | DEF-02 văn phong — "authority is first sought" (không "sort") | Màn Contract, mục Duties | 1. Đọc câu chứa "is first sort" | N/A | 1. ⚠️ "is first **sort**" nhiều khả năng sai → đúng phải "is first **sought**"; document để BA/dev xác nhận | Low | 14739:14898 |

---

## Tổng kết coverage

| Module | TC range | Số TC | Ghi chú |
|--------|----------|-------|---------|
| M01 Prefix | TC_001–002 | 2 | ⚠️ inferred (baseline Ballycommon), verify Figma |
| M02 Your Details | TC_010–038 | 29 | bỏ Surname/Email/DOB pre-filled; Address line 2 optional |
| M03 References | TC_040–050 | 11 | 4 field × 2 referee, all required; không Type/Relationship |
| M04 Medical | TC_060–072 | 13 | Decision Table all-NO/any-YES + toggle |
| M05 Contract | TC_080–084 | 5 | 5 section + checkbox; sponsor "Dt Source" |
| M06 PPE | TC_090–096 | 7 | 9 item Yes/No |
| M07 Safety Critical | TC_100–110 | 11 | gate + 16 cert + Duration conditional; tab "Pre-Deployment" |
| M08 Lost & Stolen | TC_115–117 | 3 | toggle £25+VAT (chức năng) |
| M09 Declaration | TC_120–129 | 10 | 4 toggle all required (gồm Marketing) |
| M10 E2E | TC_140–141 | 2 | happy path + resume |
| Defects/Regression | TC_153–154 | 2 | DEF-02 authorised+sought (M05) |
| **Tổng** | | **95** | Critical 10 / High 34 / Medium 32 / Low 19 |

> Node-id thật từ Figma `13380:5796` (2026-06-17). Branding "Ballycommon" còn sót do Design phụ trách — **không** đưa vào test case. Còn verify: hành vi resume (TC_141), error verbatim M08 (TC_116), đúng biến thể References Rail (`14739:15920` vs `14739:16223`).
