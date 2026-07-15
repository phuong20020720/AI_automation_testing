---
name: review-api-tests
description: Review Postman collection / API tests — coverage, test scripts, data-driven, secrets
inputs: Đường dẫn collection (mặc định execution/api/cscs/) + spec đối chiếu (nếu có)
references: automation/api-java.md, test-design/api-test-design.md, review/review-test-data.md
---

Review API tests: `{{collection_path}}`

Checklist:

| Nhóm | Kiểm tra |
|---|---|
| 📁 Cấu trúc | Folder theo module? Tên request mô tả hành vi (không phải "Copy of...")? Không request trùng lặp? |
| ✅ Test scripts | MỌI request có test script? Assert đủ 3 tầng: status code + schema/field chính + business rule? Không test rỗng kiểu chỉ `pm.response.code === 200`? |
| 📊 Coverage | Đối chiếu spec (nếu có): đủ 2xx · 4xx validation · 401/403 auth · boundary? Endpoint nào trong spec chưa có test? |
| 🔄 Data-driven | Giá trị test trong CSV `test-data/api/` (newman iteration-data), không hardcode trong request? Data unique + traceable (`auto_<api>_<timestamp>`)? |
| 🔒 Secrets | Token/password qua environment variable / `.env`? KHÔNG có credential nằm trong collection JSON (kể cả trong example/history)? |
| 🔗 Độc lập | Request không phụ thuộc kết quả run trước? Pre-request script tự chuẩn bị data? Có cleanup cho data tạo ra? |
| ⚙️ Environment | Base URL qua variable? Không trỏ cứng vào 1 env? |

Output:

```markdown
## Audit — <collection>
| # | Request/Folder | Nhóm | Vi phạm | Mức độ | Đề xuất fix |
|---|---|---|---|---|---|

## Coverage gap (đối chiếu spec)
| Endpoint | Thiếu loại test |
|---|---|

## Kết luận gate: ✅ Đạt / 🟡 Đạt có điều kiện / 🔴 Chưa đạt + lý do
```

Phát hiện secret hardcode → 🔴 block ngay, liệt kê vị trí, yêu cầu revoke credential đã lộ.
