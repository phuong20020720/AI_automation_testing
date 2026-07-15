---
name: write-bug-report
description: Viết bug report hoàn chỉnh từ bằng chứng (log, screenshot, mô tả lỗi, test fail)
inputs: Bằng chứng lỗi — mô tả/steps + log/screenshot/test fail (artifacts/) + môi trường
references: templates/bug-report-template.md, bug-analysis/bug-triage.md, bug-analysis/failure-classification.md
---

> Fail từ automation chưa rõ là bug product hay lỗi script → chạy `bug-analysis/failure-classification.md` TRƯỚC. Chỉ viết bug report cho 🐞 Product bug.

Viết bug report từ: `{{evidence}}`

Quy trình:
1. **Kiểm tra trùng**: tìm bug report đã có cùng triệu chứng (cùng module/error message) — trùng thì cập nhật report cũ thay vì tạo mới
2. **Xác minh tái hiện**: steps phải tái hiện được — ghi tần suất thực tế (x/y lần); chưa tự tái hiện được → ghi rõ "chưa tái hiện độc lập, evidence từ <nguồn>"
3. Viết theo `templates/bug-report-template.md`

Quy tắc:
- **Tiêu đề** = hành vi sai + vị trí, 1 dòng — đọc tiêu đề hiểu ngay vấn đề (❌ "Lỗi màn Declaration" · ✅ "Declaration: Submit thành công dù trường NI Number để trống")
- Steps to Reproduce từ trạng thái sạch (account nào, data nào) — người chưa biết context làm theo tái hiện được 100%
- Expected Result phải TRÍCH NGUỒN (requirement BR-XX / design / behavior chuẩn) — không có nguồn thì ghi "theo behavior mong đợi thông thường, cần PM confirm"
- Actual Result trích error message/log NGUYÊN VĂN, không diễn giải lại
- Severity theo ảnh hưởng kỹ thuật, Priority theo mức khẩn nghiệp vụ — 2 trục độc lập (xem `bug-analysis/bug-triage.md`); không chắc → đặt theo triage rule kèm ghi chú
- Evidence: đường dẫn screenshot/log trong `artifacts/` — trích đoạn log LIÊN QUAN, không dán cả file
- KHÔNG ghi password/token vào report

Output Tiếng Việt, 1 bug = 1 report (2 triệu chứng khác nhau = 2 report, kể cả khi nghi cùng root cause — ghi liên kết ở Ghi chú).
