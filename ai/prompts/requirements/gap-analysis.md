---
name: gap-analysis
description: So sánh 2 nguồn (requirements vs design / req cũ vs req mới / req vs test cases) để tìm khoảng trống
inputs: 2 nguồn cần so sánh (đường dẫn file hoặc mô tả)
references: figma/identify-missing-requirements.md (riêng cho Figma vs req)
---

So sánh `{{source_a}}` với `{{source_b}}`, tìm toàn bộ khoảng trống:

1. **Bảng đối chiếu** từng mục (field, rule, flow, AC):

   | Mục | {{source_a}} | {{source_b}} | Trạng thái |
   |---|---|---|---|

   Trạng thái: ✅ Khớp · 🔴 Chỉ có ở A · 🟠 Chỉ có ở B · 🟡 Khác nhau

2. **Phân tích impact** — mỗi gap 🔴/🟠/🟡 ghi rõ: ảnh hưởng gì (test case nào thiếu/sai, code nào phải đổi)

3. **Action items** — đánh số, gán loại: `[CLARIFY]` hỏi BA · `[UPDATE-REQ]` sửa requirements · `[UPDATE-TC]` sửa test cases · `[UPDATE-CODE]` sửa automation

Dùng cho test cases: kiểm tra coverage — mọi AC/BR phải có ít nhất 1 TC trỏ tới; TC không trace về req nào → đánh dấu orphan.
