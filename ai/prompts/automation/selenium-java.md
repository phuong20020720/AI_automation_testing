---
name: selenium-java
description: Sinh Selenium Java test code từ test cases / UI flow
inputs: Test cases (file hoặc TC IDs) + URL/trang đích
references: ai/rules/selenium_rules.md, ai/rules/automation_rules.md, automation/page-object-generation.md, templates — KHÔNG chép lại rules
---

Sinh Selenium Java test cho: `{{testcases}}`

Ràng buộc project (BẮT BUỘC):
- Tuân thủ TOÀN BỘ `ai/rules/selenium_rules.md` + `ai/rules/automation_rules.md` (POM, explicit wait, không Thread.sleep, naming, assertions có message)
- Test class extend `WebBaseTest` từ `co.uk.ppac.core.base`
- Page class đặt trong package `co.uk.ppac.web` (chỉ pages + locators); KHÔNG đặt assertion trong Page class
- Test data sinh động qua `DataGenerator` (core.utils) — unique + traceable
- Code sinh ra ghi vào `generated-tests/web/` — KHÔNG ghi trực tiếp vào `src/` (chờ QA review)
- Locator: lấy từ DOM thật (inspect qua Playwright MCP nếu app sẵn sàng) hoặc để `// TODO: verify locator` — KHÔNG bịa
- TestNG groups khớp loại TC (`smoke`, `regression`)

Output:
1. Page class(es) + Test class
2. Bảng mapping: `TC ID → test method` để trace
3. Ghi chú locator nào đã verify trên DOM thật, locator nào còn TODO
