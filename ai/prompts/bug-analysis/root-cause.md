---
name: root-cause
description: Phân tích root cause một bug/test failure từ evidence (stacktrace, log, screenshot)
inputs: Stacktrace / error log / screenshot / test report + test file liên quan
references: templates/rca-template.md, bug-analysis/failure-classification.md
---

Phân tích root cause cho failure: `{{evidence}}`

Quy trình:
1. **Đọc evidence** — stacktrace, screenshot, console/network log, test report (`artifacts/reports/`)
2. **Đọc code liên quan** — test class, page/screen object, helper
3. **Tái hiện nếu cần** — chạy lại test để xác nhận pattern (KHÔNG đoán khi chưa đủ evidence)
4. **Phân loại** theo `bug-analysis/failure-classification.md` (Product / Automation / Environment)
5. **5 Whys** — truy ngược tới nguyên nhân gốc, không dừng ở triệu chứng

Output theo `templates/rca-template.md`:
- Triệu chứng → chuỗi nguyên nhân → root cause (kèm confidence %)
- Evidence cụ thể cho từng kết luận (trích log/code, file:line)
- Fix đề xuất: ngắn hạn (sửa ngay) + dài hạn (chống tái diễn)
- Nếu confidence < 70% → liệt kê giả thuyết còn lại + cách kiểm chứng từng giả thuyết
