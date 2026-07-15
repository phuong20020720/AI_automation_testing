# Test Cases — Account Deletion (24-Hour Grace Period) — UI-Grounded

> **Tính năng:** Worker yêu cầu xóa tài khoản, có 24h grace period để hủy.
> **Scope:** Mobile App + Backend Services + Notification Handling.
> **Nguồn:** Requirement + UI flow `screenshots/Delete_Account.png`.
> **Ngày sinh TC:** 21/05/2026 — Mode: QUICK.
> **Kỹ thuật áp dụng:** State Transition, Boundary Value Analysis (mốc 24h), Equivalence Partitioning (lý do rời đi), Decision Table (multi-select reason + Other text).

## Luồng UI thật (từ screenshot)

```
Settings ──tap "Delete account"──► Màn hình chọn lý do
   │  "Are you sure you want to delete your account?"
   │  "Select all that apply:" + 8 checkbox lý do + ô "Please enter details" (khi tick Other)
   │  [Delete account]
   ▼
Modal "Permanently delete your account?"  ──[Cancel]──► quay lại
   │  [Delete account]
   ▼
Màn hình "Deletion Scheduled"  +  đồng hồ đếm ngược "23h 59m"  +  [Cancel Account Deletion]
   │  tap "Cancel Account Deletion"
   ▼
Dialog "Keep your account?"  ──[Continue Deletion]──► giữ lịch xóa
   │  [Keep My Account]
   ▼
"My Wallet" + banner "Deletion request cancelled" (account Active trở lại)
```

## Tổng quan

| Module | TC ID | Số TC |
|---|---|---|
| M1 — Entry & Reason Selection | TC001–TC012 | 12 |
| M2 — Confirmation Modal & Submit | TC013–TC019 | 7 |
| M3 — Deletion Scheduled Screen | TC020–TC024 | 5 |
| M4 — Cancel Account Deletion | TC025–TC034 | 10 |
| M5 — Automatic Deletion (Backend) | TC035–TC039 | 5 |
| M6 — Reason Data & Analytics | TC040–TC042 | 3 |
| M7 — Notification Handling | TC043–TC045 | 3 |
| M8 — State Transition & Edge Cases | TC046–TC049 | 4 |
| **Tổng** | | **49** |

**Phân bố Priority:** Critical: 11 · High: 21 · Medium: 12 · Low: 5

**Dữ liệu test dùng chung:**
- Worker Active: `worker.active@ppac.co.uk` (Worker ID `WK-2026-0001`)
- Worker đang Pending Deletion: `worker.pending@ppac.co.uk` (`WK-2026-0002`)
- Mốc tham chiếu: submit lúc `21/05/2026 10:00:00` → lịch xóa `22/05/2026 10:00:00`

---

## M1 — Entry & Reason Selection

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|
| PPAC_ACCDEL_TC_001 | M1 Reason | Mở màn hình Delete account từ Settings | Worker đăng nhập, account Active | 1. Vào Settings<br>2. Tap link "Delete account" ở cuối màn hình | worker.active@ppac.co.uk | 1–2. Hiển thị màn hình "Are you sure you want to delete your account?" kèm danh sách lý do và dòng "Select all that apply:" | High |
| PPAC_ACCDEL_TC_002 | M1 Reason | Hiển thị đầy đủ 8 lý do đúng nội dung | Đang ở màn hình Delete account | 1. Quan sát danh sách lý do | — | 1. Hiển thị đúng 8 checkbox: trouble creating submissions / approval too long / no longer work on site / created by mistake / have another account / technical issues / privacy concern / Other | Medium |
| PPAC_ACCDEL_TC_003 | M1 Reason | Chọn 1 lý do | Đang ở màn hình Delete account | 1. Tick checkbox "I no longer work on the site" | — | 1. Checkbox được tick; nút "Delete account" ở trạng thái enable | Medium |
| PPAC_ACCDEL_TC_004 | M1 Reason | Chọn nhiều lý do cùng lúc (multi-select) | Đang ở màn hình Delete account | 1. Tick "Submission approval takes too long"<br>2. Tick "I am experiencing technical issues with the app" | 2 lý do | 1–2. Cả 2 checkbox cùng được tick (multi-select hoạt động đúng — "Select all that apply") | Medium |
| PPAC_ACCDEL_TC_005 | M1 Reason | Chọn tất cả 8 lý do | Đang ở màn hình Delete account | 1. Tick lần lượt cả 8 checkbox | 8 lý do | 1. Tất cả 8 checkbox được tick, không lỗi | Low |
| PPAC_ACCDEL_TC_006 | M1 Reason | Bỏ tick một lý do | Đã tick ≥1 lý do | 1. Tick "I created this account by mistake"<br>2. Tap lại checkbox đó để bỏ tick | — | 1. Checkbox được tick<br>2. Checkbox bỏ tick đúng | Low |
| PPAC_ACCDEL_TC_007 | M1 Reason | Không chọn lý do nào → không submit được (Negative) | Đang ở màn hình Delete account | 1. Không tick lý do nào<br>2. Tap "Delete account" | Không chọn gì | 1–2. Nút "Delete account" bị disable HOẶC hiện lỗi yêu cầu chọn ít nhất 1 lý do; không mở modal xác nhận | High |
| PPAC_ACCDEL_TC_008 | M1 Reason | Tick "Other" → hiện ô nhập details | Đang ở màn hình Delete account | 1. Tick checkbox "Other" | — | 1. Ô text "Please enter details" (placeholder "Please enter here") xuất hiện ngay dưới "Other" | Medium |
| PPAC_ACCDEL_TC_009 | M1 Reason | Tick "Other" + nhập nội dung details | Đã tick "Other" | 1. Tick "Other"<br>2. Nhập nội dung vào ô details | "App bị crash khi tải tài liệu lên" | 1–2. Nội dung nhập được hiển thị trong ô details | Medium |
| PPAC_ACCDEL_TC_010 | M1 Reason | Tick "Other" để trống ô details → lỗi (theo UI) | Đã tick "Other" | 1. Tick "Other"<br>2. Để trống ô details<br>3. Tap "Delete account" | Other, details rỗng | 3. Hiển thị lỗi "Please enter the details here", không mở modal. ⚠️ MÂU THUẪN: requirement ghi "optional text input" nhưng UI bắt buộc — cần BA xác nhận | High |
| PPAC_ACCDEL_TC_011 | M1 Reason | Bỏ tick "Other" → ô details ẩn đi | Đã tick "Other" và nhập text | 1. Tick "Other", nhập text<br>2. Bỏ tick "Other" | — | 2. Ô details ẩn đi; nội dung đã nhập được xử lý nhất quán (xóa/giữ — cần xác nhận BA) | Low |
| PPAC_ACCDEL_TC_012 | M1 Reason | Ô "Other details" — độ dài tối đa (BVA) | Đã tick "Other" | 1. Nhập 500 ký tự<br>2. Nhập tới ký tự thứ 501 | Chuỗi 500 / 501 ký tự | 1. Chấp nhận 500 ký tự<br>2. Chặn ký tự 501. *Requirement chưa định nghĩa max — giả định 500* | Low |

---

## M2 — Confirmation Modal & Submit

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|
| PPAC_ACCDEL_TC_013 | M2 Confirm | Tap "Delete account" → mở modal xác nhận | Đã chọn ≥1 lý do hợp lệ | 1. Tap nút "Delete account" | Reason: "I no longer work on the site" | 1. Hiển thị modal "Permanently delete your account?" có cảnh báo + nút "Delete account" và "Cancel" | Critical |
| PPAC_ACCDEL_TC_014 | M2 Confirm | Modal hiển thị warning message rõ ràng | Modal xác nhận đang mở | 1. Đọc nội dung modal | — | 1. Modal nêu rõ: account sẽ bị xóa vĩnh viễn trong vòng 24h và có thể hủy/khôi phục trong thời gian đó | High |
| PPAC_ACCDEL_TC_015 | M2 Confirm | Tap "Cancel" trên modal → không submit | Modal xác nhận đang mở | 1. Tap nút "Cancel" trên modal | — | 1. Modal đóng, quay lại màn hình chọn lý do; account vẫn Active; KHÔNG tạo deletion request | High |
| PPAC_ACCDEL_TC_016 | M2 Confirm | Tap "Delete account" trên modal → submit thành công (Happy Path) | Modal xác nhận đang mở | 1. Tap nút "Delete account" trên modal | worker.active@ppac.co.uk | 1. Request được submit; chuyển sang màn hình "Deletion Scheduled"; account status → "Pending Deletion" | Critical |
| PPAC_ACCDEL_TC_017 | M2 Confirm | Request được thêm vào Message Queue (delay 24h) | Vừa submit deletion request | 1. Submit request<br>2. Kiểm tra Message Queue qua công cụ backend | WK-2026-0001 | 2. Queue có 1 deletion job, delivery delay = 24h, payload chứa worker_id + lý do + timestamp | Critical |
| PPAC_ACCDEL_TC_018 | M2 Confirm | Account status chuyển "Pending Deletion" | Vừa submit deletion request | 1. Submit request<br>2. Kiểm tra status trong DB | WK-2026-0001 | 2. status = "Pending Deletion" | Critical |
| PPAC_ACCDEL_TC_019 | M2 Confirm | Submit khi mất kết nối mạng (Edge) | Modal xác nhận đang mở | 1. Bật Airplane Mode<br>2. Tap "Delete account" trên modal | Airplane Mode ON | 2. Hiển thị lỗi network; request KHÔNG submit; status vẫn Active; có tùy chọn Retry | High |

---

## M3 — Deletion Scheduled Screen

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|
| PPAC_ACCDEL_TC_020 | M3 Scheduled | Hiển thị màn hình "Deletion Scheduled" sau submit | Vừa submit deletion request | 1. Quan sát màn hình sau khi tap "Delete account" trên modal | — | 1. Hiển thị màn hình "Deletion Scheduled" kèm đồng hồ đếm ngược và nút "Cancel Account Deletion" | High |
| PPAC_ACCDEL_TC_021 | M3 Scheduled | Đồng hồ đếm ngược khởi tạo đúng ~24h | Vừa submit lúc 21/05 10:00:00 | 1. Quan sát đồng hồ đếm ngược ngay sau submit | — | 1. Đồng hồ hiển thị xấp xỉ "23h 59m" và bắt đầu đếm ngược về 0 | High |
| PPAC_ACCDEL_TC_022 | M3 Scheduled | Đồng hồ đếm ngược chạy theo thời gian thực | Đang ở màn hình "Deletion Scheduled" | 1. Ghi nhận giá trị timer<br>2. Chờ 5 phút, quan sát lại | — | 2. Timer giảm đúng ~5 phút (vd từ "23h 59m" → "23h 54m") | Medium |
| PPAC_ACCDEL_TC_023 | M3 Scheduled | Delay 24h được tính chính xác | Submit lúc 21/05/2026 10:00:00 | 1. Submit request<br>2. Kiểm tra trường scheduled_deletion_at | Submit: 21/05 10:00:00 | 2. scheduled_deletion_at = 22/05/2026 10:00:00 (đúng +24h) | High |
| PPAC_ACCDEL_TC_024 | M3 Scheduled | Đóng app rồi mở lại trong thời gian Pending Deletion | account Pending Deletion | 1. Submit deletion<br>2. Đóng hẳn app<br>3. Mở lại app, đăng nhập | worker.pending@ppac.co.uk | 3. App hiển thị lại màn hình "Deletion Scheduled" với timer tiếp tục đúng + nút "Cancel Account Deletion" | High |

---

## M4 — Cancel Account Deletion

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|
| PPAC_ACCDEL_TC_025 | M4 Cancel | Tap "Cancel Account Deletion" → mở dialog "Keep your account?" | Đang ở màn hình "Deletion Scheduled" | 1. Tap nút "Cancel Account Deletion" | worker.pending@ppac.co.uk | 1. Hiển thị dialog "Keep your account?" với nút "Keep My Account" và "Continue Deletion" | Critical |
| PPAC_ACCDEL_TC_026 | M4 Cancel | Tap "Keep My Account" → hủy deletion thành công (Happy Path) | Dialog "Keep your account?" đang mở | 1. Tap nút "Keep My Account" | worker.pending@ppac.co.uk | 1. Deletion bị hủy; chuyển sang màn hình "My Wallet" kèm banner "Deletion request cancelled"; account Active | Critical |
| PPAC_ACCDEL_TC_027 | M4 Cancel | Tap "Continue Deletion" → giữ nguyên lịch xóa | Dialog "Keep your account?" đang mở | 1. Tap nút "Continue Deletion" | — | 1. Dialog đóng, vẫn ở màn hình "Deletion Scheduled", timer tiếp tục chạy, deletion vẫn được lên lịch | High |
| PPAC_ACCDEL_TC_028 | M4 Cancel | Request bị xóa khỏi Message Queue khi hủy | account Pending Deletion, có job trong queue | 1. Cancel Account Deletion → Keep My Account<br>2. Kiểm tra Message Queue | WK-2026-0002 | 2. Deletion job tương ứng bị xóa / cancel event được xử lý, queue không còn job xóa account này | Critical |
| PPAC_ACCDEL_TC_029 | M4 Cancel | Account status trở về Active sau khi hủy | account Pending Deletion | 1. Cancel Account Deletion → Keep My Account<br>2. Kiểm tra status DB và UI | WK-2026-0002 | 2. status = "Active" ở cả DB lẫn UI | Critical |
| PPAC_ACCDEL_TC_030 | M4 Cancel | Banner "Deletion request cancelled" hiển thị trên My Wallet | Vừa tap "Keep My Account" | 1. Quan sát màn hình "My Wallet" sau khi hủy | — | 1. Banner xác nhận "Deletion request cancelled / account active again" hiển thị ở đầu màn hình | Medium |
| PPAC_ACCDEL_TC_031 | M4 Cancel | Hủy sát mốc 24h — biên trong (BVA: timer ~00h01m) | submit cách đây 23 giờ 59 phút | 1. Tap "Cancel Account Deletion" → "Keep My Account" | Cancel khi timer còn ~1 phút | 1. Hủy vẫn thành công, account trở về Active | High |
| PPAC_ACCDEL_TC_032 | M4 Cancel | Quá 24h không còn hủy được — biên ngoài (BVA) | submit > 24h trước, account đã xóa | 1. Thử mở app / đăng nhập bằng account đã xóa | submit 20/05 10:00, thao tác 21/05 10:30 | 1. Không còn màn hình "Deletion Scheduled" / nút Cancel; account đã bị xóa vĩnh viễn, đăng nhập thất bại | High |
| PPAC_ACCDEL_TC_033 | M4 Cancel | Hủy khi mất kết nối mạng (Edge) | Dialog "Keep your account?" đang mở | 1. Bật Airplane Mode<br>2. Tap "Keep My Account" | Airplane Mode ON | 2. Hiển thị lỗi network; hủy KHÔNG thực hiện; account vẫn Pending Deletion; có Retry | High |
| PPAC_ACCDEL_TC_034 | M4 Cancel | Submit lại deletion sau khi đã hủy → cửa sổ 24h mới | account vừa được hủy, status Active | 1. Vào Settings → "Delete account"<br>2. Chọn lý do → modal → "Delete account"<br>3. Kiểm tra timer + scheduled_deletion_at | Submit lần 2: 21/05 15:00:00 | 3. Request mới được tạo; timer "23h 59m" mới; scheduled_deletion_at = 22/05/2026 15:00:00 | Medium |

---

## M5 — Automatic Deletion (Backend)

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|
| PPAC_ACCDEL_TC_035 | M5 Auto-Delete | Account bị xóa tự động sau 24h nếu không hủy (Happy Path) | submit cách đây đủ 24h, không hủy, timer về 0 | 1. Chờ tới mốc 24h<br>2. Kiểm tra account trong hệ thống | submit 21/05 10:00 → 22/05 10:00 | 2. Backend xử lý queue event; account bị xóa vĩnh viễn khỏi hệ thống | Critical |
| PPAC_ACCDEL_TC_036 | M5 Auto-Delete | Quyền truy cập (auth) bị thu hồi sau khi xóa | account vừa bị auto-delete | 1. Dùng access token/session cũ gọi API<br>2. Thử đăng nhập lại | Token cũ của WK-2026-0001 | 1. API trả 401 — token bị revoke<br>2. Đăng nhập thất bại | Critical |
| PPAC_ACCDEL_TC_037 | M5 Auto-Delete | Account KHÔNG bị xóa trước mốc 24h (BVA: biên trong) | submit cách đây 23h | 1. Tại mốc 23h, kiểm tra account | submit 21/05 10:00, kiểm tra 22/05 09:00 | 1. Account vẫn tồn tại, status = "Pending Deletion", chưa bị xóa | High |
| PPAC_ACCDEL_TC_038 | M5 Auto-Delete | Đăng nhập bằng account đã bị xóa | account đã bị xóa vĩnh viễn | 1. Nhập email + password account đã xóa<br>2. Tap "Log in" | worker.active@ppac.co.uk (đã xóa) | 2. Đăng nhập thất bại, hiển thị "Account not found" / thông báo tương đương | Critical |
| PPAC_ACCDEL_TC_039 | M5 Auto-Delete | Backend xử lý queue event lỗi → retry / dead-letter | Giả lập lỗi service khi xử lý deletion job | 1. Đẩy job tới mốc xử lý, giả lập service lỗi<br>2. Quan sát hành vi queue | Job xóa WK-2026-0002 | 2. Job được retry theo policy hoặc chuyển vào dead-letter queue; job KHÔNG bị mất; account không kẹt trạng thái treo | High |

---

## M6 — Reason Data & Analytics

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|
| PPAC_ACCDEL_TC_040 | M6 Data | Các lý do (multi-select) được lưu cho analytics | Worker submit deletion với nhiều lý do | 1. Tick 2 lý do<br>2. Submit deletion<br>3. Kiểm tra bảng analytics/deletion_reasons | "Submission approval takes too long" + "I am concerned about privacy or data usage" | 3. Record lưu đủ tất cả lý do đã chọn + worker_id + timestamp | Medium |
| PPAC_ACCDEL_TC_041 | M6 Data | Lý do "Other" + nội dung text được lưu | Worker submit với lý do "Other" có nhập text | 1. Tick "Other", nhập text<br>2. Submit deletion<br>3. Kiểm tra data store | Text: "App bị crash khi tải tài liệu lên" | 3. Record lưu reason = "Other" và reason_text = nội dung đã nhập | Medium |
| PPAC_ACCDEL_TC_042 | M6 Data | Sự kiện hủy (cancellation) được track cho analytics | account Pending Deletion | 1. Cancel Account Deletion → Keep My Account<br>2. Kiểm tra bảng analytics/cancellation_events | WK-2026-0002 | 2. Có record cancellation event: worker_id, timestamp, lý do gốc của request | Medium |

---

## M7 — Notification Handling

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|
| PPAC_ACCDEL_TC_043 | M7 Notification | Worker nhận thông báo khi submit deletion request | Vừa submit deletion request | 1. Submit request<br>2. Kiểm tra push notification + email | worker.active@ppac.co.uk | 2. Worker nhận thông báo xác nhận: yêu cầu xóa đã lên lịch, có thể hủy trong 24h | Medium |
| PPAC_ACCDEL_TC_044 | M7 Notification | Worker nhận thông báo khi hủy thành công | Vừa hủy deletion request | 1. Cancel Account Deletion → Keep My Account<br>2. Kiểm tra notification + email | worker.pending@ppac.co.uk | 2. Worker nhận thông báo "Yêu cầu xóa tài khoản đã được hủy, tài khoản vẫn hoạt động" | Low |
| PPAC_ACCDEL_TC_045 | M7 Notification | Thông báo/email khi account đã bị xóa vĩnh viễn | account vừa bị auto-delete | 1. Kiểm tra email gửi tới địa chỉ đã đăng ký | worker.active@ppac.co.uk | 1. Email xác nhận "Tài khoản của bạn đã bị xóa vĩnh viễn" được gửi | Low |

---

## M8 — State Transition & Edge Cases

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|
| PPAC_ACCDEL_TC_046 | M8 State | Chuyển trạng thái hợp lệ: Active → Pending Deletion → Active (luồng hủy) | account Active | 1. Submit deletion (chọn lý do → modal → Delete account)<br>2. Cancel Account Deletion → Keep My Account | WK-2026-0001 | 1. status = Pending Deletion<br>2. status = Active; account dùng được bình thường | High |
| PPAC_ACCDEL_TC_047 | M8 State | Chuyển trạng thái hợp lệ: Active → Pending Deletion → Deleted (luồng tự động) | account Active | 1. Submit deletion<br>2. Không hủy, chờ qua 24h | WK-2026-0001 | 1. status = Pending Deletion<br>2. status = Deleted, account bị xóa vĩnh viễn | High |
| PPAC_ACCDEL_TC_048 | M8 Edge | Không tạo được deletion request thứ 2 khi đang Pending Deletion | account đang Pending Deletion | 1. Vào Settings | worker.pending@ppac.co.uk | 1. Link "Delete account" không khả dụng hoặc dẫn thẳng tới màn hình "Deletion Scheduled"; chỉ có 1 deletion job duy nhất trong queue | High |
| PPAC_ACCDEL_TC_049 | M8 Edge | Đăng nhập lại khi đang Pending Deletion | account Pending Deletion, đã logout | 1. Đăng nhập lại bằng account đang Pending Deletion | worker.pending@ppac.co.uk | 1. Đăng nhập thành công, app điều hướng tới màn hình "Deletion Scheduled" (timer + "Cancel Account Deletion") | High |

---

## Ghi chú & Điểm mờ cần xác nhận (Ambiguities)

1. **⚠️ Mâu thuẫn — ô "Other details" optional hay required:** Requirement ghi *"Other (with optional text input)"* nhưng UI (`delete acc 12`) hiển thị lỗi *"Please enter the details here"* → UI coi là **bắt buộc** khi tick Other. Cần BA chốt. (Ảnh hưởng TC010)
2. **Độ dài tối đa ô "Other details"** — requirement & UI chưa quy định (TC012 giả định 500 ký tự).
3. **"Continue Deletion"** trong dialog "Keep your account?" — hiểu là *giữ nguyên lịch xóa* (TC027); cần xác nhận đúng ngữ nghĩa.
4. **Hành vi khi bỏ tick "Other"** — nội dung text đã nhập bị xóa hay giữ lại (TC011).
5. **Data retention sau khi xóa** — PII xóa hẳn hay ẩn danh? Lý do analytics giữ lại thế nào?
6. **Reminder notification** trước thời điểm xóa — requirement chưa định nghĩa có/không, mốc nào.
7. **Timezone của mốc 24h** — tính theo server time hay local time của worker (ảnh hưởng TC021, TC023, TC031, TC037).
8. **Worker có dùng app bình thường khi Pending Deletion không** — hay bị khóa ở màn hình "Deletion Scheduled".
9. **Screenshot độ phân giải thấp** — nhãn nút/tiêu đề lấy theo phần đọc được; nên đối chiếu lại với Figma gốc.
