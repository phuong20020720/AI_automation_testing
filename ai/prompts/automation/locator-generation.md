---
name: locator-generation
description: Sinh locator ổn định cho element từ DOM/page source thật
inputs: URL/screen + element cần locator (hoặc danh sách)
references: ai/rules/locator_strategy.md (priority map + stability rules), skill smart_locator_agent, workflows/generate_locator.md
---

> Cần quy trình đầy đủ 5 phase (phân tích yêu cầu → inspect → sinh → verify → cập nhật Page class) → dùng workflow `generate_locator`. Prompt này cho sinh nhanh khi đã rõ element + trang đích.

Sinh locator cho các element của: `{{target}}`

Quy trình BẮT BUỘC:
1. Lấy DOM/page source **thật**: web → Playwright MCP (`browser_navigate` + `browser_snapshot`); mobile → Appium MCP (`appium_get_page_source` / `generate_locators`). KHÔNG suy ra locator từ screenshot/Figma.
2. Chọn locator theo priority map trong `ai/rules/locator_strategy.md` (web: id → data-testid → name → css → xpath; mobile: accessibility id → resource-id → id → predicate → xpath)
3. Verify từng locator: match đúng 1 element, sống sót qua reload, không dùng dynamic class / positional xpath

Output:

| Element | Locator đề xuất | Strategy | Unique? | Fallback | Ghi chú |
|---|---|---|---|---|---|

- Element không có locator tốt (chỉ còn positional xpath) → ghi rõ + đề xuất dev thêm `data-testid`/`accessibility id` (kèm tên đề xuất)
- Format output sẵn để dán vào Page/Screen class (`By.id(...)` / `AppiumBy.accessibilityId(...)`)
