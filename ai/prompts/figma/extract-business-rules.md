---
name: extract-business-rules
description: Trích xuất business rules ẩn trong Figma design (validation, điều kiện hiển thị, phân quyền)
inputs: Figma URL hoặc Design Inventory đã có (từ analyze-screen)
references: figma/analyze-screen.md, templates/requirement-template.md
---

Từ design context của màn hình `{{screen}}`, trích xuất toàn bộ business rules thể hiện qua design:

- **Validation rules** — required, format, min/max, error messages hiển thị trong design
- **Conditional display** — element chỉ xuất hiện ở state/variant nào (suy từ các frame khác nhau)
- **Phân quyền / Role** — khác biệt UI giữa các loại user (nếu design có nhiều variant theo role)
- **Trạng thái dữ liệu** — empty state, loading, error, có data
- **Giá trị enum** — options của dropdown/radio, status badge values

Output: bảng rules đánh mã `BR-XX`:

| ID | Rule | Nguồn (frame/node) | Độ chắc chắn | Ghi chú |
|---|---|---|---|---|
| BR-01 | Email bắt buộc, format chuẩn | 1234:5678 | Cao — error message hiển thị rõ | |

Rule suy đoán (design không nói rõ) → đánh dấu độ chắc chắn **Thấp** + thêm vào danh sách câu hỏi clarify, KHÔNG khẳng định.
