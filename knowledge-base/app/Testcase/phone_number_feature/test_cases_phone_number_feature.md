# Test Cases — Phone Number Feature (PPAC v2)

> **Tester:** Mun (`ppac_mun@yopmail.com`)  
> **Feature:** Add Phone Number field to Worker Profile/Details (searchable + displayed in listing/details)  
> **App URL:** https://ppac-v2-web-uat.prod-verification.compliant101.co.uk/  
> **Generated:** 2026-05-18 — Mode: FULL RBT (6 steps)  
> **Total:** 58 TCs (11 Critical, 20 High, 19 Medium, 8 Low)

---

## Default Pre-condition (áp dụng cho mọi TC trừ khi nói khác)

1. Đã login PPAC bằng admin `ppac_mun@yopmail.com` / `L1W82sMLoO`
2. Đang ở Worker Listing screen
3. Browser viewport = 1920x1080 (desktop), trừ TCs về responsive

---

## Test Cases Table

| TC ID | Module | Risk Level | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_PHONE_M1_TC_001 | M1 WORKER_FORM | High | Save phone UK mobile hợp lệ | Default + đã mở Detail worker `aashigaur.16@gmail.com`, click Edit | 1. Nhập `07700900123` vào field Phone Number<br>2. Click Save Changes<br>3. Đóng modal, mở lại Detail worker | 1. Field accept input không có error<br>2. Toast success "Worker updated successfully"<br>3. Field Phone hiển thị `+44 7700 900 123`, DB lưu `+447700900123` | Critical | Phone = `07700900123` |
| PPAC_PHONE_M1_TC_002 | M1 WORKER_FORM | High | Save phone international (US) | Default + đã mở Edit form | 1. Nhập `+14155552671`<br>2. Click Save Changes | 1. Save OK<br>2. DB lưu `+14155552671`<br>3. Detail hiển thị `+14155552671` (raw E.164) | High | Phone = `+14155552671` |
| PPAC_PHONE_M1_TC_003 | M1 WORKER_FORM | High | Phone có formatting chars (space) | Default + Edit form | 1. Nhập `+44 7700 900 123`<br>2. Click Save Changes<br>3. Mở lại Detail | 1. Save OK<br>2. DB normalize về `+447700900123`<br>3. Detail hiển thị `+44 7700 900 123` | High | Phone = `+44 7700 900 123` |
| PPAC_PHONE_M1_TC_004 | M1 WORKER_FORM | High | Phone optional — để trống save được | Default + Edit form | 1. Để trống Phone<br>2. Sửa Surname `Test → TestEdit`<br>3. Click Save Changes | 1. Save OK không error<br>2. Detail hiển thị Surname mới<br>3. Phone hiển thị `-` | High | Phone = `` (empty) |
| PPAC_PHONE_M1_TC_005 | M1 WORKER_FORM | High | Negative — phone chứa chữ cái | Default + Edit form | 1. Nhập `0770ABC0123`<br>2. Blur field (click out) | 1. Inline error: "Phone Number must contain only digits, +, space, -, ()"<br>2. Save Changes button disabled | Critical | Phone = `0770ABC0123` |
| PPAC_PHONE_M1_TC_006 | M1 WORKER_FORM | High | Negative — ký tự đặc biệt | Default + Edit form | 1. Nhập `0770@900#123`<br>2. Blur field | 1. Inline error hiển thị<br>2. Save Changes button disabled | High | Phone = `0770@900#123` |
| PPAC_PHONE_M1_TC_007 | M1 WORKER_FORM | High | BVA min 7 digits — valid | Default + Edit form | 1. Nhập `+1234567`<br>2. Click Save Changes | 1. Save OK (7 digits là min E.164)<br>2. DB lưu `+1234567` | Medium | Phone = `+1234567` |
| PPAC_PHONE_M1_TC_008 | M1 WORKER_FORM | High | BVA min-1 (6 digits) — invalid | Default + Edit form | 1. Nhập `+123456`<br>2. Blur field | 1. Inline error: "Phone Number must be at least 7 digits"<br>2. Save Changes disabled | High | Phone = `+123456` |
| PPAC_PHONE_M1_TC_009 | M1 WORKER_FORM | High | BVA max 15 digits — valid | Default + Edit form | 1. Nhập `+123456789012345`<br>2. Click Save Changes | 1. Save OK (15 digits là max E.164)<br>2. DB lưu đúng giá trị | Medium | Phone = `+123456789012345` |
| PPAC_PHONE_M1_TC_010 | M1 WORKER_FORM | High | BVA max+1 (16 digits) — invalid | Default + Edit form | 1. Nhập `+1234567890123456`<br>2. Blur field | 1. Inline error: "Phone Number must not exceed 15 digits"<br>2. Save Changes disabled | High | Phone = `+1234567890123456` |
| PPAC_PHONE_M1_TC_011 | M1 WORKER_FORM | High | Negative — chỉ có dấu `+` | Default + Edit form | 1. Nhập `+`<br>2. Blur field | 1. Inline error: "Phone Number must contain at least 7 digits"<br>2. Save disabled | Medium | Phone = `+` |
| PPAC_PHONE_M1_TC_012 | M1 WORKER_FORM | High | Edge — leading/trailing spaces | Default + Edit form | 1. Nhập `  07700900123  ` (2 spaces trước & sau)<br>2. Click Save Changes | 1. Hệ thống trim spaces<br>2. DB lưu `+447700900123`<br>3. Save OK | Medium | Phone = `  07700900123  ` |
| PPAC_PHONE_M1_TC_013 | M1 WORKER_FORM | High | Negative — server return 500 | Default + Mock API `/workers/{id}` → 500 | 1. Nhập phone hợp lệ `07700900456`<br>2. Click Save Changes | 1. Toast error: "Failed to update worker. Please try again."<br>2. Modal vẫn mở, dữ liệu nhập còn nguyên | High | Phone = `07700900456` |
| PPAC_PHONE_M1_TC_014 | M1 WORKER_FORM | High | Negative — mất kết nối khi save | Default + DevTools tắt network | 1. Nhập phone hợp lệ `07700900789`<br>2. Click Save Changes | 1. Toast error: "Network error. Changes not saved."<br>2. Phone cũ trong DB không thay đổi (no partial save) | High | Phone = `07700900789` |
| PPAC_PHONE_M1_TC_015 | M1 WORKER_FORM | High | Edge — duplicate phone với worker khác | Worker A đã có phone `07700900111` | 1. Edit Worker B<br>2. Nhập `07700900111`<br>3. Click Save Changes | 1. Warning toast: "Phone Number is already used by another worker (Worker A)"<br>2. Worker B vẫn save thành công (không unique) | Medium | Phone B = `07700900111` (trùng A) |
| PPAC_PHONE_M2_TC_001 | M2 WORKER_DETAIL | Medium | Display UK format | Worker có Phone DB = `+447700900123` | 1. Click row worker → mở Detail | 1. Field Phone Number hiển thị `+44 7700 900 123` (có space format UK) | High | DB Phone = `+447700900123` |
| PPAC_PHONE_M2_TC_002 | M2 WORKER_DETAIL | Medium | Display international raw E.164 | Worker có Phone DB = `+14155552671` | 1. Mở Detail | 1. Hiển thị `+14155552671` (không format US, giữ E.164) | Medium | DB Phone = `+14155552671` |
| PPAC_PHONE_M2_TC_003 | M2 WORKER_DETAIL | Medium | Copy button copy E.164 | Worker có Phone `+447700900123`, Detail đang mở | 1. Click nút Copy cạnh field Phone Number | 1. Clipboard chứa `+447700900123` (raw E.164)<br>2. Toast: "Copied to clipboard" | Medium | DB Phone = `+447700900123` |
| PPAC_PHONE_M2_TC_004 | M2 WORKER_DETAIL | Medium | Click-to-call tel: link | Worker có Phone `+447700900123`, Detail đang mở | 1. Click vào số phone hiển thị | 1. Browser trigger `tel:+447700900123` (mở dialer hoặc handler) | Low | DB Phone = `+447700900123` |
| PPAC_PHONE_M2_TC_005 | M2 WORKER_DETAIL | Medium | Empty state cho legacy worker | Worker legacy chưa có phone (Phone = null trong DB) | 1. Mở Detail worker này | 1. Field Phone Number hiển thị `-`<br>2. Copy button disabled hoặc ẩn | High | DB Phone = null |
| PPAC_PHONE_M2_TC_006 | M2 WORKER_DETAIL | Medium | Vị trí field — sau Surname | Bất kỳ worker | 1. Mở Detail<br>2. Quan sát thứ tự field trong Worker Information panel | 1. Phone Number nằm ngay sau Surname, trước Reusable Passport | Medium | N/A |
| PPAC_PHONE_M2_TC_007 | M2 WORKER_DETAIL | Medium | Style label consistent | Detail đang mở | 1. So sánh visual label "Phone Number:" với "Name:" và "Surname:" | 1. Cùng font, size, color, weight | Low | N/A |
| PPAC_PHONE_M2_TC_008 | M2 WORKER_DETAIL | Medium | Phone dài 15 digits không vỡ layout | Worker có Phone = `+123456789012345` | 1. Mở Detail | 1. Phone hiển thị đầy đủ (wrap hoặc tooltip)<br>2. Panel layout không vỡ | Low | DB Phone = `+123456789012345` |
| PPAC_PHONE_M3_TC_001 | M3 WORKER_LISTING | Low | Column "Phone" sau Email | Default | 1. Truy cập Worker Listing | 1. Header có columns theo thứ tự: Company, Nationality, Type, Name, Surname, Email, **Phone**, DoB, ... | High | N/A |
| PPAC_PHONE_M3_TC_002 | M3 WORKER_LISTING | Low | Format consistent với Detail | Worker có Phone DB = `+447700900123` | 1. Xem row worker trên listing | 1. Cell Phone hiển thị `+44 7700 900 123` (same format as Detail) | High | DB Phone = `+447700900123` |
| PPAC_PHONE_M3_TC_003 | M3 WORKER_LISTING | Low | Empty state cho legacy worker | Worker legacy chưa có phone | 1. Xem row worker | 1. Cell Phone hiển thị `-` | Medium | DB Phone = null |
| PPAC_PHONE_M3_TC_004 | M3 WORKER_LISTING | Low | Mobile <768px ẩn cột Phone | Browser viewport = 375x812 (iPhone) | 1. Resize browser → 375px<br>2. Mở Listing | 1. Cột Phone bị ẩn<br>2. Cột priority hiển thị: Name, Status | Medium | Viewport = 375x812 |
| PPAC_PHONE_M3_TC_005 | M3 WORKER_LISTING | Low | Tablet scroll horizontal | Browser viewport = 768x1024 (iPad portrait) | 1. Resize browser → 768x1024<br>2. Mở Listing, scroll right | 1. Bảng scroll horizontal<br>2. Cột Phone visible khi scroll | Low | Viewport = 768x1024 |
| PPAC_PHONE_M3_TC_006 | M3 WORKER_LISTING | Low | Performance render 323 workers | Listing có 323 workers (current data) | 1. F5 reload Listing<br>2. Đo thời gian DevTools Performance từ navigate → cột Phone render xong | 1. First Contentful Paint cột Phone < 2 giây | Medium | 323 workers |
| PPAC_PHONE_M3_TC_007 | M3 WORKER_LISTING | Low | Sort by Phone (nếu support) | Default | 1. Click header "Phone"<br>2. Click lần 2 | 1. Sort ascending theo E.164 numeric<br>2. Click lần 2 → descending | Low | N/A |
| PPAC_PHONE_M3_TC_008 | M3 WORKER_LISTING | Low | Header label đúng | Default | 1. Inspect header column thứ 7 | 1. Text label = `Phone` hoặc `Phone Number` consistent với Detail | Low | N/A |
| PPAC_PHONE_M4_TC_001 | M4 WORKER_SEARCH | Medium | Search full E.164 | Worker A có Phone = `+447700900123` | 1. Gõ `+447700900123` vào Search box<br>2. Wait debounce 500ms | 1. Listing chỉ show 1 row = Worker A | Critical | Query = `+447700900123` |
| PPAC_PHONE_M4_TC_002 | M4 WORKER_SEARCH | Medium | Search partial digits | Worker A có Phone = `+447700900123` | 1. Gõ `7700900` vào Search | 1. Listing show Worker A (partial match) | Critical | Query = `7700900` |
| PPAC_PHONE_M4_TC_003 | M4 WORKER_SEARCH | Medium | Search với formatting chars | Worker A có Phone = `+447700900123` | 1. Gõ `+44 7700 900 123` vào Search | 1. Listing show Worker A (system normalize cả query và DB digits-only trước khi match) | High | Query = `+44 7700 900 123` |
| PPAC_PHONE_M4_TC_004 | M4 WORKER_SEARCH | Medium | Search country code | Có ≥3 worker phone bắt đầu `+44` | 1. Gõ `+44` vào Search | 1. Listing show tất cả workers UK có phone bắt đầu +44 | Medium | Query = `+44` |
| PPAC_PHONE_M4_TC_005 | M4 WORKER_SEARCH | Medium | Combined: Phone + Status=Pending | Worker A: phone `+447700900123` Status=Approved; Worker B: phone `+447700900456` Status=Pending | 1. Set filter Status=Pending<br>2. Gõ search `+447700900` | 1. Chỉ show Worker B (match cả phone AND status) | High | Query=`+447700900`, Status=Pending |
| PPAC_PHONE_M4_TC_006 | M4 WORKER_SEARCH | Medium | Combined: Phone + Date range | Default + có workers trong khoảng date | 1. Set Start Date=2026-01-01, End Date=2026-12-31<br>2. Gõ search `7700900` | 1. Show worker có phone match AND created/updated trong date range | Medium | Query=`7700900`, Date range 2026 |
| PPAC_PHONE_M4_TC_007 | M4 WORKER_SEARCH | Medium | Combined: Phone + Distinct | Default | 1. Check Distinct checkbox<br>2. Gõ search `+44` | 1. Mỗi worker chỉ xuất hiện 1 lần (không duplicate row) | Medium | Query=`+44`, Distinct=true |
| PPAC_PHONE_M4_TC_008 | M4 WORKER_SEARCH | Medium | No result state | Default | 1. Gõ phone không tồn tại `+99999999999` | 1. Bảng hiển thị empty state "No workers found" | Medium | Query = `+99999999999` |
| PPAC_PHONE_M4_TC_009 | M4 WORKER_SEARCH | Medium | Security — SQL Injection | Default | 1. Gõ `' OR '1'='1` vào Search<br>2. Wait response | 1. Không leak toàn bộ data (không show tất cả workers)<br>2. Return "No workers found" hoặc escape input<br>3. Không trigger DB error 500 | Critical | Query = `' OR '1'='1` |
| PPAC_PHONE_M4_TC_010 | M4 WORKER_SEARCH | Medium | Security — XSS | Default | 1. Gõ `<script>alert('xss')</script>` vào Search<br>2. Wait response | 1. Input bị escape khi render listing<br>2. Không execute JS, không alert popup | Critical | Query = `<script>alert('xss')</script>` |
| PPAC_PHONE_M4_TC_011 | M4 WORKER_SEARCH | Medium | Performance search 1000+ records | DB seed 1000+ workers | 1. Gõ `+44`<br>2. Đo thời gian từ keystroke cuối đến result render (DevTools Network) | 1. Response < 2 giây | Medium | Query=`+44`, DB 1000+ records |
| PPAC_PHONE_M4_TC_012 | M4 WORKER_SEARCH | Medium | Empty search reset filter | Đang có search `7700900` active | 1. Xóa search box → empty<br>2. Wait debounce | 1. Listing reset, show toàn bộ workers (theo pagination 100/page) | Medium | Query=`` (empty) |
| PPAC_PHONE_M5_TC_001 | M5 SECURITY | High | Audit log khi view phone | Audit log table cleared trước test | 1. Mở Detail Worker A<br>2. Close modal<br>3. Query audit log table | 1. Audit log có entry: `{user: ppac_mun, action: VIEW_PHONE, worker_id: A, timestamp: NOW}` | Critical | Worker A any |
| PPAC_PHONE_M5_TC_002 | M5 SECURITY | High | Audit log khi edit phone | Worker A phone = `+447700900123` | 1. Edit Worker A<br>2. Đổi phone sang `+447700900999`<br>3. Save<br>4. Query audit log | 1. Audit log entry: `{user: ppac_mun, action: EDIT_PHONE, worker_id: A, old: +447700900123, new: +447700900999, timestamp}` | Critical | Old=`+447700900123`, New=`+447700900999` |
| PPAC_PHONE_M5_TC_003 | M5 SECURITY | High | Non-admin role API 403 | User có role `viewer` (không phải admin), có token hợp lệ | 1. Call API `GET /api/workers/{id}` với header Authorization=Bearer <viewer_token> | 1. Response 403 Forbidden<br>2. Body response KHÔNG chứa field `phone` | Critical | Token role = viewer |
| PPAC_PHONE_M5_TC_004 | M5 SECURITY | High | Expired token API 401 | Token đã hết hạn (exp < NOW) | 1. Call API `GET /api/workers/{id}` với token expired | 1. Response 401 Unauthorized<br>2. Body KHÔNG chứa phone | Critical | Token expired |
| PPAC_PHONE_M5_TC_005 | M5 SECURITY | High | Expiry Report có cột Phone | Default | 1. Click button "Expiry Report"<br>2. Download file<br>3. Mở file Excel | 1. File có cột `Phone Number`<br>2. Mỗi row có giá trị phone đúng (hoặc empty cho worker chưa có) | High | N/A |
| PPAC_PHONE_M5_TC_006 | M5 SECURITY | High | Download report log audit | Audit log cleared | 1. Click "Expiry Report" → download<br>2. Query audit log | 1. Audit log entry: `{user: ppac_mun, action: EXPORT_REPORT, file: expiry_report_2026-05-18.xlsx, num_records: 323, timestamp}` | High | N/A |
| PPAC_PHONE_M5_TC_007 | M5 SECURITY | High | DSAR export include phone | Có flow DSAR triggerable | 1. Trigger DSAR cho Worker A (qua API hoặc admin tool) | 1. File export trả về cho Worker A có chứa field `phone` với giá trị đúng | High | Worker A có phone |
| PPAC_PHONE_M5_TC_008 | M5 SECURITY | High | Rate limit chống enumeration | Token admin valid, script gọi API | 1. Script gọi `GET /api/workers/{id}` 1000 lần / 60 giây với id từ 1 → 1000 | 1. Sau ~100 requests, server return 429 Too Many Requests<br>2. Throttle áp dụng | High | 1000 requests/60s |
| PPAC_PHONE_M5_TC_009 | M5 SECURITY | High | Phone không leak trong error | Default + trigger backend error | 1. Gây lỗi backend (ví dụ save phone qua API với body invalid)<br>2. Đọc response error message | 1. Error message generic ("Failed to save", "Invalid input")<br>2. KHÔNG chứa phone numbers trong error body/stack | High | Trigger 400/500 error |
| PPAC_PHONE_M5_TC_010 | M5 SECURITY | High | Phone không có trong URL params | Default | 1. Search phone `+447700900123`<br>2. Check URL bar<br>3. Check browser history (Ctrl+H) | 1. URL không chứa phone (search dùng POST body hoặc query encrypted)<br>2. History không lưu phone plaintext | Medium | Query = `+447700900123` |
| PPAC_PHONE_INT_TC_001 | INT Integration | Medium | E2E: Add → Search → Detail | Worker A chưa có phone | 1. Edit Worker A, nhập phone `+447700900555` → Save<br>2. Xóa search box, gõ `7700900555`<br>3. Click row Worker A | 1. Save OK<br>2. Listing show 1 row Worker A<br>3. Detail panel hiển thị `+44 7700 900 555` | Critical | Phone = `+447700900555` |
| PPAC_PHONE_INT_TC_002 | INT Integration | Medium | E2E: Edit → Reload → Listing | Worker A có phone `+447700900111` | 1. Edit Worker A: phone → `+447700900222`<br>2. Save<br>3. F5 reload Listing<br>4. Tìm row Worker A | 1. Sau reload, cell Phone của Worker A = `+44 7700 900 222` | Critical | Old=`+447700900111`, New=`+447700900222` |
| PPAC_PHONE_INT_TC_003 | INT Integration | Medium | Duplicate phone warning vẫn save | Worker A có phone `+447700900333` | 1. Edit Worker B<br>2. Nhập `+447700900333`<br>3. Click Save Changes | 1. Warning toast: "Phone Number is already used by Worker A"<br>2. Worker B vẫn save thành công với cùng phone | High | Phone B = `+447700900333` |
| PPAC_PHONE_INT_TC_004 | INT Integration | Medium | Export Excel — Phone column đúng | Đã edit ≥5 workers với phone khác nhau | 1. Click "Expiry Report" → download<br>2. Mở file Excel<br>3. So sánh cột Phone với Detail từng worker | 1. Cột Phone Number có giá trị đúng tương ứng từng worker | High | ≥5 workers có phone |
| PPAC_PHONE_INT_TC_005 | INT Integration | Medium | Regression — filters cũ không bị ảnh hưởng | Default | 1. Set filter Status=Pending<br>2. Set Date range 2026-01-01 → 2026-05-18<br>3. Check Distinct<br>4. Select Contractors = EKFB<br>5. Quan sát listing | 1. Tất cả filter hoạt động đúng như trước khi thêm cột Phone<br>2. Không lỗi UI/data<br>3. Cột Phone vẫn hiển thị đúng cho mỗi row match | High | Filters multi-set |

---

## Risk-Based Summary

| Module | Risk Level | # TCs | Critical | High | Medium | Low |
|---|---|---|---|---|---|---|
| M1 WORKER_FORM | 🔴 High | 15 | 2 | 7 | 5 | 1 |
| M2 WORKER_DETAIL | 🟡 Medium | 8 | 0 | 2 | 3 | 3 |
| M3 WORKER_LISTING | 🟢 Low | 8 | 0 | 2 | 3 | 3 |
| M4 WORKER_SEARCH | 🟡 Medium | 12 | 3 | 1 | 7 | 1 |
| M5 SECURITY_COMPLIANCE | 🔴 High | 10 | 4 | 5 | 1 | 0 |
| INT Integration | 🟡 Medium | 5 | 2 | 3 | 0 | 0 |
| **TOTAL** | | **58** | **11** | **20** | **19** | **8** |

---

## Traceability Matrix (REQ ↔ TC)

| REQ-ID | Requirement | Covered by TCs |
|---|---|---|
| REQ-01 | Worker form có Phone field, optional, sau Surname | M1_TC_001, M1_TC_004, M2_TC_006 |
| REQ-02 | Validate E.164, normalize | M1_TC_001 → M1_TC_015 (validation suite) |
| REQ-03 | Detail panel display + Copy + tel: | M2_TC_001 → M2_TC_008 |
| REQ-04 | Listing column sau Email + responsive | M3_TC_001 → M3_TC_008 |
| REQ-05 | Search by phone partial + normalized + combinable | M4_TC_001 → M4_TC_012 |
| REQ-06 | PII audit + role-based authorization | M5_TC_001 → M5_TC_004, M5_TC_008 → M5_TC_010 |
| REQ-07 | Export + DSAR + audit download | M5_TC_005 → M5_TC_007 |
| Cross-cutting | E2E + regression | INT_TC_001 → INT_TC_005 |

---

## Assumptions Đã Chốt (Q&A Step 2)

| Q# | Topic | Assumption |
|---|---|---|
| Q1 | Required/Optional | Optional |
| Q2 | Format | E.164 (UK + International), max 15 digits |
| Q3 | Format chars | Accept space/dash/brackets khi nhập, lưu DB normalized |
| Q4 | Unique | Không unique, cảnh báo duplicate (warning, vẫn save) |
| Q5 | Min length | Min 7, max 15 |
| Q6 | Listing position | Cột mới sau Email |
| Q7 | Display format | UK → `+44 7700 900 123`; khác → E.164 |
| Q8 | Mask PII | Không mask cho admin role |
| Q9 | Detail position | Sau Surname |
| Q10 | Copy button | Có |
| Q11 | Click-to-call | Có (`tel:` link) |
| Q12 | Search match | Partial |
| Q13 | Search normalize | Có (digits-only comparison) |
| Q14 | Country code search | Có (partial logic) |
| Q15 | Case sensitivity | Insensitive |
| Q16 | Audit trail | Có (compliance UK) |
| Q17 | Role limits | Tạm tất cả admin xem được |
| Q18 | Export include | Có, log audit + DSAR |
| Q19 | API authorization | Có, token role check |
| Q20 | Responsive | Hide trên mobile |
| Q21 | i18n | en-UK only |
| Q22 | Self-update | Chỉ admin update |
| Q23 | Country picker UI | Text tự do, placeholder |
| Q24 | 3 unlabeled buttons | Chưa rõ — cần inspect thêm |
| Q25 | Migration | Phone = null cho legacy worker |
| Q26 | Bulk import | Không trong MVP |

---

## Hướng Dẫn Import vào Jira/Excel

1. **Excel:** Mở file `.md` này → copy bảng Test Cases → paste vào Excel → mỗi cell sẽ tự tách
2. **Jira (Xray/Zephyr):**
   - Mỗi row = 1 Test Issue
   - Trường `TC ID` → External ID
   - Cột `Test Steps` → Steps field (paste, dùng line break thay `<br>`)
   - Cột `Expected Result` → Expected Result field
3. **Test execution thứ tự đề xuất:**
   - Run **Critical** TCs trước (M1, M4, M5)
   - Sau đó High → Medium → Low
   - Run **M1 trước M2/M3/M4** (vì các module sau phụ thuộc dữ liệu từ M1)
   - **M5** chạy song song được vì có path API riêng

---

*Generated by RBT Manual Testing Skill — FULL RBT Mode (6 Steps)*  
*PPAC v2 Compliance UK | Tester: Mun | Date: 2026-05-18*
