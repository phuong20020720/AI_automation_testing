---
name: appium-java
description: Sinh Appium Java test code (mobile) từ test cases / screen flow
inputs: Test cases + app/screen đích (APK trong test-data/apps/ nếu cần)
references: ai/rules/appium_rules.md, ai/rules/automation_rules.md, automation/page-object-generation.md
---

Sinh Appium Java test cho: `{{testcases}}`

Ràng buộc project (BẮT BUỘC):
- Tuân thủ TOÀN BỘ `ai/rules/appium_rules.md` + `ai/rules/automation_rules.md` (locator priority: accessibility id → resource-id; explicit wait; không Thread.sleep)
- Test class extend `MobileBaseTest` từ `co.uk.ppac.core.base`
- Screen Objects (hậu tố `Screen`) đặt trong package `co.uk.ppac.mobile`; KHÔNG assertion trong Screen class
- Locator lấy từ page source thật (Appium MCP: `appium_get_page_source` / `generate_locators`) — KHÔNG bịa resource-id
- Element off-screen → scroll trước (UiScrollable), không query trực tiếp
- Test data: `DataGenerator` (unique + traceable); data JSON ngoài code → `test-data/mobile/`
- Code sinh ra ghi vào `generated-tests/mobile/` — KHÔNG ghi trực tiếp vào `src/`
- Chạy verify: `mvn test -P mobile`

Output: Screen class(es) + Test class + bảng mapping `TC ID → test method` + danh sách locator đã verify/TODO
