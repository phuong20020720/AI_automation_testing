---
name: identify-missing-requirements
description: Đối chiếu Figma vs Requirements — phát hiện inconsistency và requirement còn thiếu
inputs: Figma URL + đường dẫn file requirements trong knowledge-base
references: workflows/generate_testcases_from_figma_requirement.md (Bước 3), requirements/gap-analysis.md
---

Đối chiếu design `{{figma_url}}` với requirements `{{requirements_path}}`:

1. **Bảng đối chiếu:**

   | Mục | Requirements nói | Figma thể hiện | Khớp? | Ghi chú |
   |---|---|---|---|---|

2. **Phân loại điểm không khớp:**
   - 🔴 Field/flow có trong design nhưng KHÔNG có trong requirements
   - 🟠 Requirement có nhưng design KHÔNG thể hiện
   - 🟡 Cả hai có nhưng khác nhau (label, format, behavior)

3. **Danh sách Ambiguities** — mỗi điểm không khớp ghi thành `AMB-XX` kèm câu hỏi clarify cụ thể gửi BA/Designer

4. **Khuyến nghị** — mục nào chặn việc sinh test case (phải clarify trước), mục nào có thể test với assumption ghi rõ
