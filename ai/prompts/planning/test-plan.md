---
name: test-plan
description: Sinh Test Plan cho release/sprint/module — scope, approach, schedule, entry/exit criteria
inputs: Phạm vi (release/sprint/module) + đường dẫn requirements liên quan + timeline (nếu có)
references: templates/test-plan-template.md, planning/entry-exit-criteria.md, test-design/risk-based-testing.md
---

Sinh Test Plan cho: `{{scope}}`

Quy trình:
1. Đọc requirements/TC liên quan trong `knowledge-base/` để xác định danh sách feature thuộc scope
2. Phân loại **In Scope / Out of Scope** — out of scope phải ghi lý do (không test ≠ quên test)
3. Xác định approach cho từng feature: Manual / Automation (web `mvn test -P web` · mobile `-P mobile` · API newman) / Exploratory — ưu tiên theo risk (dùng `test-design/risk-based-testing.md` nếu chưa có risk matrix)
4. Liệt kê nhu cầu môi trường + test data + thiết bị (mobile: tham chiếu `config/mobile/`)
5. Đặt Entry/Exit Criteria — dùng `planning/entry-exit-criteria.md`, đo lường được, không chung chung
6. Lập schedule theo timeline được cung cấp; KHÔNG tự bịa deadline khi không có input

Quy tắc:
- Format theo `templates/test-plan-template.md`
- Mọi con số (số TC, % automation, effort) phải có nguồn từ `knowledge-base/` hoặc `artifacts/` — không ước lượng suông; thiếu dữ liệu → ghi "TBD" + câu hỏi vào Open Questions
- Risk + mitigation: tối thiểu liệt kê rủi ro về môi trường, data, dependency, timeline
- Input thiếu/mơ hồ (không rõ scope, không có timeline) → hỏi lại hoặc ghi Open Questions, không suy diễn
- Output Tiếng Việt, lưu `knowledge-base/{app|web}/planning/test_plan_<scope>.md`
