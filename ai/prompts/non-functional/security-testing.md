---
name: security-testing
description: Thiết kế security test cases mức QA (OWASP-based) — authz, authn, input validation, data exposure
inputs: Phạm vi (module/API/màn hình) + danh sách role của hệ thống
references: templates/testcase-template.md, test-design/api-test-design.md (auth matrix)
---

> ⚠️ Phạm vi QA security testing — CHỈ thực hiện trên môi trường test của PPAC được cho phép. Đây là test mức QA (kiểm tra control cơ bản theo OWASP), KHÔNG thay thế pentest chuyên sâu — nêu rõ giới hạn này trong output.

Thiết kế security test cases cho: `{{scope}}`

Bao phủ theo nhóm (map về OWASP Top 10):

| Nhóm | Kiểm tra | OWASP |
|---|---|---|
| 🔑 Authorization | Ma trận role × chức năng: role thấp gọi API/mở màn hình của role cao? Đổi ID trên URL/payload đọc data người khác (IDOR)? | A01 |
| 🔐 Authentication | Lockout sau N lần sai? Password policy enforce ở server (không chỉ UI)? Session hết hạn đúng? Logout vô hiệu token? | A07 |
| 🧪 Input validation | Mỗi input nhận chuỗi: thử payload XSS (`<script>alert(1)</script>`), SQLi (`' OR 1=1--`) — expected: encode/reject, KHÔNG thực thi | A03 |
| 📤 Data exposure | Response API có trả thừa field nhạy cảm? Password/token xuất hiện trong log (`artifacts/logs/`), URL, error message? Stack trace lộ ra user? | A02/A05 |
| 📱 Mobile riêng | Data nhạy cảm trong storage app (SharedPreferences/plist) không mã hóa? Screenshot màn hình nhạy cảm bị chặn? Permission xin đúng nhu cầu? | — |

Quy tắc:
- Format Bảng theo `templates/testcase-template.md` — TC ID: `PPAC_SEC_<MODULE>_TC_<SỐ>`
- Payload test ghi CỤ THỂ trong Test Data; expected result mô tả hành vi an toàn mong đợi
- Ma trận authorization sinh đủ tổ hợp role × chức năng trong scope — thiếu danh sách role → hỏi lại, không đoán
- Phát hiện lỗ hổng khi chạy → báo bug theo `templates/bug-report-template.md` với Severity tối thiểu Major, KHÔNG ghi credential/payload khai thác chi tiết vào report công khai
- Output Tiếng Việt, lưu `knowledge-base/{app|web}/Testcase/test_cases_security_<module>.md`
