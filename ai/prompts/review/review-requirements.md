---
name: review-requirements
description: Review requirements document trước khi đưa vào test design (wrapper có ngữ cảnh PPAC + gate decision)
inputs: Đường dẫn file requirements
references: requirements/review-requirement.md (checklist chính), templates/requirement-template.md
---

Review requirements `{{requirements_path}}` theo checklist trong `ai/prompts/requirements/review-requirement.md`, bổ sung kiểm tra riêng PPAC:

- Đúng format `templates/requirement-template.md`? Có metadata (module, nguồn Figma, version)?
- Đặt đúng chỗ `knowledge-base/{app|web}/requirements/` theo nền tảng?
- Business rules có mã `BR-XX` để TC trace về?
- Thuật ngữ nghiệp vụ (worker, contractor, CSCS, declaration...) dùng nhất quán với các requirements đã có trong `knowledge-base/`?
- Có mâu thuẫn với requirements của module liên quan đã tồn tại?

Output: như `review-requirement.md` + section riêng "Nhất quán với knowledge-base" liệt kê xung đột (nếu có) + **kết luận gate**:

| Gate | Điều kiện | Hệ quả |
|---|---|---|
| ✅ Đạt | Không vi phạm nào ở mức Major+ | Đưa vào test design ngay |
| 🟡 Đạt có điều kiện | Có Open Questions nhưng không chặn phần lớn TC | Test design phần rõ ràng; phần mơ hồ chờ trả lời — liệt kê cụ thể TC nào bị chặn |
| 🔴 Chưa đạt | Thiếu BR ID / mâu thuẫn nội bộ / mơ hồ ở flow chính | KHÔNG sinh TC — sinh TC từ requirement hỏng tạo TC hỏng; liệt kê việc phải sửa |

Kết luận gate viết ĐẦU output (người đọc bận chỉ cần dòng này), chi tiết audit bên dưới.
