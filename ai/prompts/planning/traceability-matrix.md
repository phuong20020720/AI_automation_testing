---
name: traceability-matrix
description: Xây/cập nhật Requirements Traceability Matrix (RTM) — BR/Requirement ↔ UC ↔ TC ↔ Automation, chỉ ra gap
inputs: Module hoặc phạm vi cần trace (mặc định toàn bộ knowledge-base/)
references: templates/rtm-template.md, requirements/gap-analysis.md
---

Xây RTM cho: `{{scope}}`

Quy trình:
1. Quét requirements trong `knowledge-base/{app|web}/requirements/` → danh sách Requirement + `BR-XX`
2. Quét use cases (nếu có) → map `UC_<MODULE>_<SỐ>` về BR
3. Quét TC trong `knowledge-base/{app|web}/Testcase/` → map `PPAC_<MODULE>_TC_<SỐ>` về BR/UC (dựa cột trace trong file TC)
4. Quét automation trong `src/` + `generated-tests/` → map test method về TC ID (theo tên method/comment/@Test description)
5. Đối chiếu 2 chiều:
   - BR không có TC nào → **gap coverage** (nghiêm trọng nhất nếu BR thuộc feature High-risk)
   - TC không trace về BR nào → **orphan TC** (thừa hoặc thiếu trace)

Quy tắc:
- Format theo `templates/rtm-template.md`
- KHÔNG đoán mapping — chỉ map khi có bằng chứng (ID xuất hiện trong file); mapping nghi ngờ đánh dấu `(?)` kèm lý do
- Kết thúc bằng bảng tổng hợp: % BR có TC, % TC có automation, danh sách gap xếp theo risk
- Output Tiếng Việt, lưu `knowledge-base/{app|web}/planning/rtm_<scope>.md`
