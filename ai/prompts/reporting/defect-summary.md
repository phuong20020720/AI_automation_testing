---
name: defect-summary
description: Tổng hợp + phân tích xu hướng defects theo module/severity/nguồn gốc
inputs: Danh sách defects (export tracker hoặc bug reports) + khoảng thời gian
references: bug-analysis/bug-triage.md, templates/bug-report-template.md
---

Tổng hợp defects `{{scope}}`:

1. **Bảng tổng quan:**

   | | Critical | Major | Minor | Trivial | Tổng |
   |---|---|---|---|---|---|
   | Mới | | | | | |
   | Đóng | | | | | |
   | Còn mở | | | | | |

2. **Phân bố theo module** — module nào nhiều bug nhất, có hotspot không
3. **Phân tích nguồn gốc** — bug lọt từ giai đoạn nào (requirement mơ hồ / thiếu TC / không automation / regression)? Bug nào ĐÁNG LẼ test bắt được → bài học cụ thể
4. **Bug già** — mở > 2 sprint, lý do tồn đọng
5. **Xu hướng** — so kỳ trước (nếu có data): tăng/giảm, chất lượng đang tốt lên hay xấu đi
6. **Hành động đề xuất** — tối đa 3 mục (VD: thêm TC khu vực X, automation cho flow Y, review requirements module Z)

Số liệu thiếu → ghi "N/A". Output Tiếng Việt.
