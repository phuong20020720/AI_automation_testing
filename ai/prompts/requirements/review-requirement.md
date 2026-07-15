---
name: review-requirement
description: Review chất lượng requirements document — phát hiện mơ hồ, thiếu, mâu thuẫn
inputs: Đường dẫn file requirements hoặc text
references: workflows/analyze_requirement_document.md, skill requirements_analyzer
---

Review requirements: `{{requirements}}`

Đánh giá theo 6 tiêu chí, mỗi vi phạm ghi rõ vị trí + trích dẫn:

| Tiêu chí | Câu hỏi kiểm tra |
|---|---|
| **Rõ ràng** | Có từ mơ hồ ("nhanh", "phù hợp", "có thể", "v.v.")? Mỗi rule có đo lường được không? |
| **Đầy đủ** | Có thiếu error case, empty state, phân quyền, giới hạn dữ liệu? |
| **Nhất quán** | Có 2 chỗ mâu thuẫn nhau (label, flow, rule)? |
| **Kiểm thử được** | Mỗi AC có thể viết thành test case pass/fail rõ ràng? |
| **Trace được** | Requirement có ID? Có liên kết tới design/UC? |
| **Khả thi** | Có rule bất khả thi về kỹ thuật/dữ liệu? |

Output:
1. Bảng findings: `| # | Vị trí | Tiêu chí vi phạm | Trích dẫn | Mức độ (🔴/🟡/🟢) | Đề xuất sửa |`
2. Danh sách câu hỏi gửi BA (đánh số, copy-paste được)
3. Kết luận: ✅ Sẵn sàng test design / ⚠️ Cần clarify trước
