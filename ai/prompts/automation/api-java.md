---
name: api-java
description: Sinh API tests (Postman/newman hiện tại; REST Assured khi package api được kích hoạt)
inputs: Swagger/OpenAPI spec hoặc endpoint list + test cases
references: workflows/generate_api_tests_from_swagger.md, execution/api/cscs/ (Postman collection hiện có)
---

Sinh API tests cho: `{{api_spec}}`

Hiện trạng project: API test chạy qua **Postman collection + newman** (`execution/api/cscs/`, data CSV ở `test-data/api/`, report ra `artifacts/reports/`). Package `co.uk.ppac.api` (REST Assured) chưa dùng.

Quy tắc:
- Mặc định sinh/bổ sung vào **Postman collection** hiện có — giữ cấu trúc folder theo module, không tạo collection mới khi đã có collection phù hợp
- Mỗi request có test script: status code, schema/field chính, business rule
- Bao phủ: 2xx happy · 4xx validation (thiếu field, sai format, sai type) · 401/403 auth · boundary theo spec
- Data-driven: giá trị test đưa vào CSV ở `test-data/api/` (newman `--iteration-data`), KHÔNG hardcode trong request
- Dữ liệu tạo mới phải unique + traceable (`auto_<api>_<timestamp>`)
- Secrets/token lấy từ environment Postman / `.env` — KHÔNG hardcode

Output: collection items (JSON) hoặc diff vào collection hiện có + CSV data + lệnh newman chạy verify
