---
name: generate-testcases
description: Sinh test cases chi tiết theo format chuẩn project (Bảng — cột đầu TC ID)
inputs: Requirements / scenarios / Design Inventory + tên module
references: templates/testcase-template.md, skill rbt_manual_testing, workflows/generate_testcases_from_requirements.md
---

Sinh test cases chi tiết cho: `{{feature}}`

Kỹ thuật thiết kế (chọn theo input):
- **EP + BVA** cho từng input field có ràng buộc
- **Decision Table** khi nhiều điều kiện kết hợp
- **State Transition** cho flow nhiều màn/trạng thái

Bao phủ đủ loại: ✅ Positive · ❌ Negative (validation, auth) · 🔲 Boundary · ⚡ Edge · 🎨 UI-state · 🔒 Security cơ bản · ♿ Accessibility cơ bản

Quy tắc BẮT BUỘC:
- Format **Bảng** theo `templates/testcase-template.md` (cột đầu `TC ID`, đúng bộ cột chuẩn — tương thích `execution/scripts/export_testcases_to_excel.ps1`)
- TC ID: `PPAC_<MODULE>_TC_<SỐ>` (3 chữ số)
- Test Data **cụ thể, unique, traceable** (VD `auto_login_20260611_A3F2@test.com`) — KHÔNG placeholder kiểu `<email hợp lệ>`
- Expected Result đánh số tương ứng Steps
- Mỗi TC trace về requirement/BR/Figma node nguồn
- Lưu file: `knowledge-base/{app|web}/Testcase/test_cases_<feature>.md` với metadata header (xem file mẫu trong `knowledge-base/app/Testcase/`)
- Output Tiếng Việt
