---
name: generate-scenarios
description: Sinh test scenarios (mức cao) từ requirements/use case — bước trước khi viết test cases chi tiết
inputs: Requirements / use cases / business rules (đường dẫn hoặc text)
references: test-design/generate-testcases.md, skill rbt_manual_testing
---

Sinh test scenarios cho: `{{feature}}`

Quy tắc:
- Scenario = MỘT mục tiêu kiểm thử mức cao, KHÔNG có steps chi tiết
- Bao phủ: ✅ Happy path · ❌ Negative · 🔲 Boundary · ⚡ Edge case · 🎨 UI-state (empty/error/loading) · 🔒 Permission
- Mỗi scenario trace về requirement/BR/UC nguồn

Output:

| ID | Scenario | Loại | Nguồn (Req/BR/UC) | Risk | Ưu tiên |
|---|---|---|---|---|---|
| SC-01 | Đăng nhập thành công với tài khoản hợp lệ | Happy | UC_LOGIN_01 | High | P1 |

Cuối output:
1. **Ma trận coverage** — mỗi requirement/BR có bao nhiêu scenario; mục nào 0 scenario → ghi rõ lý do
2. **Đề xuất scope** — scenario nào nên làm test case chi tiết trước (theo risk), scenario nào để exploratory

Sau khi user duyệt danh sách → dùng `test-design/generate-testcases.md` để chi tiết hóa.
