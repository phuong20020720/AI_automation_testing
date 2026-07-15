---
name: entry-exit-criteria
description: Sinh Entry/Exit Criteria đo lường được cho một phase test (sprint test, regression, UAT, release)
inputs: Phase cần criteria + bối cảnh (scope, mức rủi ro release)
references: templates/test-plan-template.md, templates/release-checklist-template.md
---

Sinh Entry/Exit Criteria cho phase: `{{phase}}`

Quy tắc:
- Mỗi criterion phải **đo lường được + có nguồn kiểm chứng** — ghi rõ đo bằng gì (Allure report, newman report, bug tracker). Cấm tiêu chí mơ hồ kiểu "chất lượng tốt", "test đầy đủ"
- Entry: điều kiện để BẮT ĐẦU phase (build deploy thành công, smoke pass, TC đã review, data/env sẵn sàng)
- Exit: điều kiện để KẾT THÚC phase (pass rate ≥ X%, 0 bug Critical/Major open, regression hoàn thành, RTM không còn gap High-risk)
- Mỗi criterion có **hành động khi không đạt** (block, conditional pass kèm người chịu trách nhiệm quyết)

Output — 2 bảng:

```markdown
## Entry Criteria — <phase>
| # | Tiêu chí | Ngưỡng | Cách kiểm chứng | Khi không đạt |
|---|---|---|---|---|
| E1 | Smoke suite pass | 100% | Allure report run gần nhất | Block — trả build về dev |

## Exit Criteria — <phase>
| # | Tiêu chí | Ngưỡng | Cách kiểm chứng | Khi không đạt |
|---|---|---|---|---|
| X1 | Pass rate regression | ≥ 95% | artifacts/reports/ | Triage fail; Product bug Critical → No-Go |
```

Output Tiếng Việt. Nếu criteria phục vụ release → nhúng vào `templates/release-checklist-template.md` thay vì file rời.
