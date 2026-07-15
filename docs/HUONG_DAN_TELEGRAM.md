# Hướng Dẫn: Thông Báo Tự Động Qua Telegram

> Hệ thống gửi thông báo về điện thoại qua app Telegram khi có kết quả test,
> bug report mới, hoặc khi Claude Code cần bạn xác nhận.

## 1. Tổng Quan

Hệ thống gửi tin nhắn **một chiều** (máy → Telegram) tới một bot riêng.
Không cần server, không cần mở app — thông báo tự đẩy về điện thoại.

| Khi nào | Nội dung tin nhắn | Nguồn |
|---|---|---|
| Chạy `generate_test_report.ps1` xong | *"Anh vừa chạy xong test feature ... cho em rồi nè"* + tổng kết PASS/FAIL/SKIP/NOT RUN | Script test |
| Claude Code tạo file `bug_report_*` | *"Anh vừa tạo bug report mới cho em đây 🐞"* + tên file | Hook `PostToolUse` |
| Claude Code tạo file `test_result_*` | *"Anh vừa cập nhật file test result cho em rồi 📊"* + tên file | Hook `PostToolUse` |
| Claude Code hỏi / chờ xác nhận | *"Anh báo em nè 🥺 Claude Code đang chờ em: ..."* | Hook `Notification` |

> 📌 Mọi tin nhắn đều bắt đầu bằng tiêu đề **EM YÊU ƠI ❤️**, bot xưng "anh" / gọi người dùng là "em".

### Quan trọng: hệ thống chạy bằng "sự kiện", không bằng lời Claude nói

Tin Telegram chỉ gửi khi xảy ra đúng các **sự kiện** ở bảng trên. Việc Claude
*viết* câu "tôi sẽ báo bạn" trong khung chat **KHÔNG** kích hoạt gì cả — đó chỉ là
câu chữ; Claude không tự đẩy tin sang Telegram được.

Riêng thông báo loại Notification (*"Anh báo em nè..."*) được gửi khi Claude Code
**rảnh chờ em khoảng 60 giây** hoặc **cần xin quyền** — tức là *sau khi* Claude làm
xong việc mà em chưa trả lời, chứ không phải đúng giây Claude xong. Muốn báo **ngay
tức thì** khi một việc xong → xem Mục 8.

## 2. Các File Liên Quan

| File | Vai trò |
|---|---|
| `.claude/scripts/notify-telegram.ps1` | Script trung tâm — gửi tin tới Telegram |
| `.claude/telegram.local.json` | Cấu hình: chứa Bot Token + Chat ID (**không chia sẻ / không commit**) |
| `C:\Users\<user>\.claude\settings.json` | Hook `Notification` — đặt **toàn cục**, áp dụng mọi dự án / mọi đoạn chat |
| `.claude/settings.local.json` | Hook `PostToolUse` — chỉ riêng dự án này (phát hiện file bug report + test result) |
| `scripts/generate_test_report.ps1` | Mục số 5 cuối file — gửi kết quả test |

## 3. Cấu Hình

Token và Chat ID nằm trong `.claude/telegram.local.json`:

```json
{
  "botToken": "<bot-token-tu-BotFather>",
  "chatId": "<chat-id-cua-ban>"
}
```

> Có thể thay bằng biến môi trường `TELEGRAM_BOT_TOKEN` và `TELEGRAM_CHAT_ID`
> (script ưu tiên biến môi trường trước, sau đó mới đọc file json).

## 4. Thiết Lập Lần Đầu (nếu cần làm lại)

### Bước 1 — Tạo bot
1. Trong Telegram, chat với **@BotFather**
2. Gõ `/newbot` → đặt tên → BotFather trả về **Bot Token**

### Bước 2 — Lấy Chat ID
1. Mở bot vừa tạo, bấm **START** (hoặc gửi 1 tin bất kỳ)
2. Mở trình duyệt, truy cập (thay `<TOKEN>`):
   ```
   https://api.telegram.org/bot<TOKEN>/getUpdates
   ```
3. Tìm `"chat":{"id":123456789}` → số đó là **Chat ID**

### Bước 3 — Điền cấu hình
Dán Bot Token và Chat ID vào `.claude/telegram.local.json` (xem mục 3).

### Bước 4 — Kích hoạt hook
Hook `Notification` đặt ở cấu hình **toàn cục** (`C:\Users\<user>\.claude\settings.json`)
→ áp dụng cho **mọi đoạn chat, mọi dự án**. Hook `PostToolUse` đặt riêng trong dự án này.

Để nạp hook: **khởi động lại Claude Code** (đóng và mở lại cửa sổ). Đoạn chat đang mở
phải mở lại mới có hook; đoạn chat mới mở thì tự động có sẵn.
(Phần thông báo kết quả test không cần bước này.)

## 5. Kiểm Tra Hoạt Động

Chạy lệnh sau trong PowerShell — nếu điện thoại nhận được tin là OK:

```powershell
powershell -ExecutionPolicy Bypass -File ".claude\scripts\notify-telegram.ps1" -Message "Test Telegram OK"
```

## 6. Bật / Tắt Thông Báo

| Mục đích | Cách làm |
|---|---|
| Tắt tạm thời tất cả | Để trống `chatId` trong `.claude/telegram.local.json` |
| Tắt riêng hook (cho Claude Code) | Gõ `/hooks` → chọn hook → disable |
| Bật lại | Điền lại `chatId`, hoặc bật hook trong `/hooks` |

## 7. Xử Lý Sự Cố

| Hiện tượng | Nguyên nhân / Cách xử lý |
|---|---|
| Không nhận được tin nào | Kiểm tra `botToken` / `chatId` trong file json đã đúng chưa |
| Script báo `chua cau hinh` | `telegram.local.json` thiếu token hoặc chat id |
| Script báo `gui that bai` | Sai token, hoặc máy không vào được mạng |
| Hook không chạy / đoạn chat khác không báo | Chưa nạp lại — đóng và mở lại Claude Code |
| Telegram báo trễ ~1 phút sau khi xong | Bình thường — hook `Notification` chờ Claude rảnh ~60 giây (xem Mục 1) |
| Quá nhiều tin "Anh báo em" | Hook `Notification` báo mỗi lần xin quyền — giảm bằng cách cho phép sẵn các lệnh hay dùng |
| Tiếng Việt bị lỗi dấu trên Telegram | File `.ps1` mất BOM. Re-save: `[IO.File]::WriteAllText($p, (Get-Content $p -Raw -Encoding UTF8), [Text.UTF8Encoding]::new($true))` |

## 8. Mở Rộng

Muốn thêm loại thông báo mới (ví dụ: CI/CD build xong, deploy xong...):
gọi script trung tâm với tham số `-Message`:

```powershell
& ".claude\scripts\notify-telegram.ps1" -Message "Noi dung thong bao tu do"
```

Đặt dòng này ở bất kỳ script nào (pipeline CI, script deploy...) là xong.

## 9. Bảo Mật

- `telegram.local.json` chứa Bot Token — coi như mật khẩu, **không commit lên git**, không chia sẻ.
- Nếu lộ token: chat `/revoke` với @BotFather để cấp token mới, rồi cập nhật lại file json.
