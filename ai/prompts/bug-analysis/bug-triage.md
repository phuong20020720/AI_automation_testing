---
name: bug-triage
description: Triage danh sách bugs — đánh severity/priority, gom nhóm trùng lặp, đề xuất thứ tự xử lý
inputs: Danh sách bugs (export từ tracker hoặc mô tả)
references: templates/bug-report-template.md
---

Triage các bugs sau: `{{bugs}}`

1. **Chuẩn hóa từng bug:**

   | ID | Tóm tắt | Severity | Priority | Module | Trùng với | Thiếu thông tin? |
   |---|---|---|---|---|---|---|

   - **Severity** (mức kỹ thuật): 🔴 Critical (crash, mất data, security) · 🟠 Major (chức năng chính hỏng, không workaround) · 🟡 Minor (có workaround) · 🟢 Trivial (cosmetic)
   - **Priority** (mức nghiệp vụ): P1 hotfix · P2 sprint này · P3 backlog · P4 khi rảnh
   - Severity ≠ Priority — bug cosmetic ở màn hình khách hàng thấy nhiều có thể P2

2. **Gom nhóm** — bugs nghi cùng root cause → gộp, ghi rõ lý do nghi ngờ

3. **Bug thiếu thông tin** — liệt kê cần bổ sung gì (steps, env, evidence) theo chuẩn `templates/bug-report-template.md`

4. **Đề xuất thứ tự xử lý** + bug nào cần escalate ngay
