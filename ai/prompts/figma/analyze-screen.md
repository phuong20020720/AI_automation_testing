---
name: analyze-screen
description: Phân tích 1 màn hình Figma thành Design Inventory có cấu trúc
inputs: Figma URL (có node-id) hoặc fileKey + nodeId
references: mcp__figma__get_figma_data, workflows/generate_testcases_from_figma_requirement.md (Bước 2)
---

Phân tích màn hình Figma sau: `{{figma_url}}`

Lấy design context bằng `mcp__figma__get_figma_data` — KHÔNG đoán UI, mọi field/label/state phải lấy từ Figma data thực tế.

Output (Tiếng Việt):

1. **Purpose** — màn hình dùng để làm gì, thuộc flow nào
2. **Components** — bảng Design Inventory (cấu trúc chuẩn — workflow Figma cũng dùng bảng này):

   | # | Màn hình/Frame | Element | Loại | Label/Text | States thấy trong design | Ràng buộc suy ra |
   |---|---|---|---|---|---|---|

3. **User Actions** — các hành động user có thể thực hiện
4. **System Responses** — phản hồi hệ thống tương ứng (success/error/loading)
5. **Validation** — rule validation suy ra từ design (error message, format hint, required marker)
6. **Assumptions** — điều phải giả định vì design không thể hiện rõ
7. **Questions** — câu hỏi cần clarify với BA/Designer (đánh số Q-01, Q-02...)
