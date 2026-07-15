---
name: discover-user-flow
description: Suy ra user flow / state transition từ nhiều frame Figma
inputs: Figma URL của file/section chứa nhiều frame liên quan
references: figma/analyze-screen.md, knowledge-base/{app|web}/flows/
---

Từ các frame trong `{{figma_url}}`, suy ra user flow hoàn chỉnh:

1. **Liệt kê frames** theo thứ tự logic (dựa tên frame, prototype links, nội dung)
2. **Vẽ flow diagram** dạng mermaid:
   ```mermaid
   flowchart LR
     A[Màn Login] -->|Submit hợp lệ| B[Dashboard]
     A -->|Sai password| A1[Error state]
   ```
3. **Bảng transitions:**

   | # | Từ màn | Hành động | Đến màn | Điều kiện |
   |---|---|---|---|---|

4. **Nhánh thiếu** — transition mà design KHÔNG thể hiện (VD: có frame error nhưng không rõ từ đâu tới) → liệt kê thành câu hỏi

Đối chiếu với flow đã có trong `knowledge-base/{app|web}/flows/` (nếu có) — ghi rõ điểm khác biệt.
