---
name: business-rule-extraction
description: Trích xuất business rules từ requirements/user story thành danh sách có mã, kiểm thử được
inputs: Requirements text hoặc đường dẫn file
references: templates/requirement-template.md, test-design/generate-testcases.md
---

Trích xuất toàn bộ business rules từ: `{{requirements}}`

Mỗi rule viết lại thành dạng **kiểm thử được** (điều kiện → kết quả):

| ID | Rule (viết lại testable) | Loại | Nguồn (trích dẫn) | Test design gợi ý |
|---|---|---|---|---|
| BR-01 | NẾU email sai format THÌ hiển thị lỗi "..." và chặn submit | Validation | "§2.1: Email phải hợp lệ" | EP + BVA |

Loại rule: Validation · Calculation · Conditional flow · Permission · Data constraint · Integration

Quy tắc:
- Rule ngầm định (requirements không nói nhưng bắt buộc phải có — VD: max length, duplicate check) → vẫn liệt kê, đánh dấu `[IMPLICIT]` + đưa vào câu hỏi clarify
- Rule mơ hồ không viết lại được thành điều kiện → kết quả → đánh dấu `[AMBIGUOUS]` + câu hỏi cụ thể
- Output này là input trực tiếp cho `test-design/generate-testcases.md`
