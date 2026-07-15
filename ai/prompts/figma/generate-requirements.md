---
name: generate-requirements
description: Sinh Requirements Document từ Figma design (khi chưa có requirements)
inputs: Figma URL; output của analyze-screen + extract-business-rules nếu đã chạy
references: skill requirements_analyzer, templates/requirement-template.md, workflows/analyze_requirement_document.md
---

Sinh Requirements Document cho màn hình/module `{{screen}}` từ Figma design.

Quy trình:
1. Nếu chưa có Design Inventory → chạy `figma/analyze-screen.md` trước
2. Trích business rules theo `figma/extract-business-rules.md`
3. Viết requirements theo format `templates/requirement-template.md`

Ràng buộc:
- Mọi requirement phải trace về frame/node Figma cụ thể
- Điểm design không thể hiện rõ → đưa vào **Assumptions** hoặc **Open Questions**, KHÔNG bịa business logic
- Lưu file vào `knowledge-base/{app|web}/requirements/<feature>.md` (snake_case)
- Output Tiếng Việt
