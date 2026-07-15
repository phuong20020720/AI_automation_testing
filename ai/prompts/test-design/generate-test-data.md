---
name: generate-test-data
description: Sinh test data unique, traceable cho form/API — positive, negative, boundary, edge
inputs: Danh sách field + ràng buộc (hoặc đường dẫn requirements/spec) + format output (inline/CSV/JSON)
references: skill test_data_generator, workflows/generate_test_data.md, ai/rules/automation_rules.md (mục 2)
---

> Cần bộ data lớn/phức tạp nhiều dataset → dùng workflow `/generate_test_data` (skill test_data_generator). Prompt này cho nhu cầu nhanh 1 form/1 API.

Sinh test data cho: `{{fields}}`

Mỗi field sinh đủ 4 loại:
- ✅ **Positive**: hợp lệ điển hình + hợp lệ ít gặp (tên có dấu, email subdomain...)
- ❌ **Negative**: rỗng, sai format, sai type, ký tự đặc biệt, khoảng trắng đầu/cuối
- 🔲 **Boundary**: min-1 / min / min+1 / max-1 / max / max+1 theo ràng buộc — số liệu lấy TỪ RÀNG BUỘC được cung cấp, không bịa
- ⚡ **Edge**: unicode/emoji, chuỗi rất dài, số 0/âm, ngày 29/02, timezone — chọn theo type của field

Quy tắc (theo `ai/rules/automation_rules.md` mục 2):
- Field unique (email, username, mã...) BẮT BUỘC format traceable: `auto_<testName>_<timestamp>_<random>` — VD `auto_createWorker_20260612_K7P2@test.com`
- KHÔNG dùng data thật của người dùng (PII) — mọi data là giả lập
- Field có ràng buộc không được cung cấp rõ → liệt kê vào "Cần xác nhận ràng buộc", sinh tạm theo giả định ghi rõ
- Output theo format yêu cầu: bảng inline (gắn vào TC) · CSV → `test-data/api/` (newman) · JSON → `test-data/mobile/`

Output: bảng data theo loại (mỗi dòng ghi rõ mục đích — test case nào dùng) + file data nếu được yêu cầu. Output Tiếng Việt.
