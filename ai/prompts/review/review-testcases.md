---
name: review-testcases
description: Review bộ test cases — coverage, chất lượng, đúng format project
inputs: Đường dẫn file test cases (knowledge-base/{app|web}/Testcase/...) + requirements nguồn nếu có
references: templates/testcase-template.md, requirements/gap-analysis.md
---

Review test cases: `{{testcase_file}}`

Checklist:

| Nhóm | Kiểm tra |
|---|---|
| **Coverage** | Đủ Positive/Negative/Boundary/Edge/UI-state? Mỗi requirement/BR có ≥1 TC? TC orphan (không trace về req)? |
| **Chất lượng TC** | Steps tái hiện được không cần hỏi thêm? Expected Result cụ thể, đo được? 1 TC = 1 mục tiêu (không gộp nhiều verify không liên quan)? |
| **Test Data** | Cụ thể (không placeholder)? Unique + traceable? |
| **Độc lập** | Pre-Condition tự dựng được, không phụ thuộc TC khác chạy trước? |
| **Format** | Đúng format Bảng (`templates/testcase-template.md` — cột đầu `TC ID`, đúng bộ cột)? TC ID đúng `PPAC_<MODULE>_TC_<SỐ>`? Đủ metadata header? Còn sót format Section COMPACT cũ? |
| **Priority** | Hợp lý theo risk? Có quá nhiều Critical không? |

Output:
1. Bảng findings: `| # | TC ID | Nhóm | Vấn đề | Mức độ | Đề xuất sửa |`
2. Ma trận coverage requirement → TC (đánh dấu gap)
3. Kết luận: ✅ Đạt / ⚠️ Sửa minor / ❌ Cần làm lại — kèm 3 việc ưu tiên nhất
