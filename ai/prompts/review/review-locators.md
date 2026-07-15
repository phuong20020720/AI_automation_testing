---
name: review-locators
description: Audit toàn bộ locators trong Page/Screen classes — phát hiện locator fragile trước khi chúng gây flaky
inputs: Đường dẫn package pages/screens (hoặc toàn bộ src)
references: ai/rules/locator_strategy.md, skill smart_locator_agent, automation/locator-generation.md
---

Audit locators trong: `{{path}}`

Quét toàn bộ `By.*` / `AppiumBy.*` / `@FindBy`, phân loại từng locator:

| Mức | Tiêu chí |
|---|---|
| ✅ Tốt | id / data-testid / accessibility id / resource-id / name ổn định |
| 🟡 Chấp nhận | CSS theo attribute semantic; xpath theo text ổn định |
| 🔴 Fragile | Dynamic class (`css-xxx`, `sc-xxx`, `MuiXxx`), positional xpath (`//div[3]/...`), `nth-child` khi có lựa chọn tốt hơn, auto-generated ID |

Output:
1. Bảng audit: `| File:Line | Element | Locator hiện tại | Mức | Locator đề xuất | Cần verify trên DOM? |`
2. Thống kê: tổng số locator, % theo mức, file tệ nhất
3. Danh sách 🔴 ưu tiên sửa ngay (kèm locator thay thế — verify trên DOM thật nếu app truy cập được, nếu không đánh dấu cần verify)
4. Danh sách element nên đề nghị dev thêm `data-testid`/`accessibility id`
