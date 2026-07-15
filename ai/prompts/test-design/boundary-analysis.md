---
name: boundary-analysis
description: Phân tích EP + BVA cho các input field — sinh bảng giá trị test đầy đủ
inputs: Danh sách fields + ràng buộc (từ requirements hoặc Design Inventory)
references: test-design/generate-testcases.md, skill test_data_generator
---

Phân tích Equivalence Partitioning + Boundary Value Analysis cho các field của `{{feature}}`:

Với MỖI field, output:

| Field | Ràng buộc | Partition hợp lệ | Partition không hợp lệ | Boundary values |
|---|---|---|---|---|
| Tuổi | 18–65, số nguyên | 18–65 | <18, >65, không phải số, rỗng | 17, 18, 19, 64, 65, 66 |

Quy tắc:
- Boundary: min−1, min, min+1, max−1, max, max+1
- Luôn xét thêm partition đặc biệt: rỗng, null, khoảng trắng, ký tự đặc biệt, unicode/tiếng Việt có dấu, quá dài (overflow), SQL/script injection cơ bản
- String có format (email, phone, postcode UK...) → liệt kê các format sai điển hình
- Ràng buộc KHÔNG có trong requirements → đánh dấu `[ASSUMED]` + câu hỏi clarify

Cuối output: tổng hợp số test value cần thiết tối thiểu (mỗi partition ≥1 đại diện + toàn bộ boundary) — input cho `generate-testcases`.
