---
name: api-contract-testing
description: Sinh contract tests — validate response đúng schema OpenAPI, phát hiện breaking change giữa các version
inputs: OpenAPI spec (hoặc 2 version spec để so sánh) + Postman collection hiện có
references: automation/api-java.md, execution/api/cscs/ (collection hiện có), test-data/api/
---

Sinh contract tests cho: `{{api_spec}}`

Hai chế độ (chọn theo input):

**Mode 1 — Schema validation** (1 spec): mỗi response 2xx của endpoint có 1 test script validate schema
- Trích JSON Schema từ spec → lưu `test-data/api/schemas/<module>/<endpoint>_<status>.json`
- Postman test script dùng `pm.response` + `tv4`/ajv validate response body với schema — fail khi: thiếu required field, sai type, field enum ngoài danh sách
- Schema validate cấu trúc, KHÔNG validate giá trị nghiệp vụ (đó là việc của functional test)

**Mode 2 — Breaking change detection** (2 version spec): so sánh và báo cáo
- 🔴 Breaking: xóa endpoint/field, đổi type, field optional → required, thêm required field vào request, đổi status code
- 🟡 Cần chú ý: thêm enum value, đổi description ràng buộc, deprecated
- ✅ An toàn: thêm endpoint mới, thêm optional field vào response

Quy tắc:
- Bổ sung test script vào collection HIỆN CÓ ở `execution/api/cscs/` — không tạo collection mới
- Schema lưu file riêng để tái dùng — KHÔNG inline schema dài trong test script
- Secrets từ Postman environment / `.env` — không hardcode
- Output: schema files + collection diff + bảng breaking change (mode 2) + lệnh newman verify
