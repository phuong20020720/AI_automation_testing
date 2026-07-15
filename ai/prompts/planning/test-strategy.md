---
name: test-strategy
description: Sinh/cập nhật Test Strategy cấp project — test levels, automation split, tooling, data & env strategy
inputs: Phạm vi project hoặc thay đổi lớn cần cập nhật strategy (nền tảng mới, tool mới)
references: templates/test-plan-template.md (section Approach), docs/framework-design.md, ai/rules/automation_rules.md
---

> Test Plan cho 1 release/sprint cụ thể → dùng `planning/test-plan.md`. Prompt này cho chiến lược dài hạn cấp project.

Sinh/cập nhật Test Strategy cho: `{{scope}}`

Nội dung bắt buộc:
1. **Test levels & loại test**: API (Postman/newman) → UI Web (Selenium) → UI Mobile (Appium) — phạm vi mỗi level, cái gì test ở level nào (đẩy test xuống level thấp nhất có thể)
2. **Automation vs Manual split**: tiêu chí quyết định (lặp lại, regression, data-driven → automation; exploratory, UX, một-lần → manual)
3. **Tooling hiện trạng**: 1 Maven project (`co.uk.ppac.*`), TestNG, profiles `web|mobile`, newman cho API, Allure report — đề xuất tool mới phải kèm lý do + chi phí chuyển đổi
4. **Environment strategy**: các env trong `config/environments/`, env nào cho loại test nào, quy tắc không phá data shared env
5. **Test data strategy**: unique + traceable theo `ai/rules/automation_rules.md` mục 2, data tĩnh ở `test-data/`
6. **Quy trình chất lượng**: AI sinh code → `generated-tests/` → review gate (`review/review-automation.md`) → `src/`
7. **Non-functional**: phạm vi performance/security/accessibility (tham chiếu `non-functional/`) — làm gì, KHÔNG làm gì, vì sao
8. **Metrics theo dõi**: pass rate, automation coverage, defect escape rate, flaky rate

Quy tắc:
- Strategy mô tả "cách chơi" dài hạn — KHÔNG chứa schedule/feature cụ thể (đó là test plan)
- Mỗi quyết định strategy phải có lý do; điểm chưa quyết được → Open Questions
- Output Tiếng Việt, lưu `docs/test-strategy.md` (cập nhật file cũ nếu đã có, không tạo bản thứ hai)
