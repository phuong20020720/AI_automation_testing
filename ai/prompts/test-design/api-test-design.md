---
name: api-test-design
description: Thiết kế API test cases từ OpenAPI/Swagger hoặc endpoint list — đủ happy/negative/auth/boundary/schema
inputs: OpenAPI spec hoặc endpoint list + business rules liên quan
references: templates/testcase-template.md, automation/api-java.md, workflows/generate_api_tests_from_swagger.md
---

> Cần cả TC + scripts + chạy verify → dùng workflow `generate_api_tests_from_swagger`. Prompt này chỉ THIẾT KẾ test cases (chưa sinh code).

Thiết kế API test cases cho: `{{api_spec}}`

Bao phủ BẮT BUỘC cho mỗi endpoint:
- ✅ **Happy path**: 2xx với payload hợp lệ tối thiểu + payload đầy đủ
- ❌ **Validation 4xx**: thiếu từng required field · sai type · sai format (email, date, enum ngoài danh sách) — mỗi field 1 TC riêng, không gộp
- 🔒 **Auth matrix**: không token (401) · token hết hạn (401) · token role không đủ quyền (403) · truy cập resource của user khác — IDOR (403/404)
- 🔲 **Boundary theo schema**: minLength/maxLength, minimum/maximum, mảng rỗng/1 phần tử/max — lấy số liệu TỪ SPEC, không bịa
- 📋 **Response schema**: field bắt buộc có mặt, đúng type, không lộ field nhạy cảm (password, token nội bộ)
- ⚡ **Nghiệp vụ**: business rule (BR-XX) liên quan endpoint, idempotency (gọi 2 lần), pagination/filter nếu có

Quy tắc:
- Format Bảng theo `templates/testcase-template.md` — TC ID: `PPAC_API_<MODULE>_TC_<SỐ>`
- Test Steps ghi: method + path + payload tóm tắt; Test Data ghi payload/param cụ thể, unique traceable (`auto_<api>_<timestamp>`)
- Expected Result ghi: status code + body/field cần assert
- Spec thiếu thông tin (constraint không khai báo, response không có schema) → ghi vào Open Questions, KHÔNG tự suy constraint
- Output Tiếng Việt, lưu `knowledge-base/{app|web}/Testcase/test_cases_api_<module>.md`
