---
name: test-plan-template
description: Format chuẩn cho Test Plan (release/sprint/module)
---

> File lưu tại `knowledge-base/{app|web}/planning/test_plan_<scope>.md` (snake_case).

# Test Plan — <Scope: release/sprint/module>

| | |
|---|---|
| **Phạm vi** | <release X / sprint N / module> |
| **Nền tảng** | App / Web / API |
| **Thời gian** | YYYY-MM-DD → YYYY-MM-DD (TBD nếu chưa có) |
| **QA Owner** | <tên> |
| **Version** | v1.0 · YYYY-MM-DD |
| **Nguồn requirements** | <đường dẫn knowledge-base/> |

## 1. Scope

### In Scope
| Feature/Module | Risk | Approach | Nguồn TC |
|---|---|---|---|
| <feature> | High/Med/Low | Automation / Manual / ET | knowledge-base/.../test_cases_<feature>.md |

### Out of Scope
| Hạng mục | Lý do loại |
|---|---|

## 2. Approach
<Manual vs automation split, thứ tự ưu tiên theo risk, loại test áp dụng (functional, regression, ET, non-functional nếu có)>

## 3. Môi trường & Test Data
| Hạng mục | Chi tiết | Trạng thái |
|---|---|---|
| Environment | <env từ config/environments/> | Sẵn sàng / TBD |
| Thiết bị (mobile) | <device matrix> | |
| Test data | <nguồn: test-data/ hoặc sinh động> | |

## 4. Entry / Exit Criteria
<2 bảng theo format `planning/entry-exit-criteria.md` — tiêu chí, ngưỡng, cách kiểm chứng, hành động khi không đạt>

## 5. Schedule & Deliverables
| Mốc | Ngày | Deliverable |
|---|---|---|
| <milestone> | | <TC / report / RTM> |

## 6. Rủi ro & Mitigation
| # | Rủi ro | Ảnh hưởng | Mitigation |
|---|---|---|---|

## 7. Open Questions
| # | Câu hỏi | Hỏi ai | Trạng thái |
|---|---|---|---|
