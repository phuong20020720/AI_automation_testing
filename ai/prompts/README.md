# AI Prompts — Thư Viện Mẫu Thực Thi

> Vai trò trong kiến trúc AI của project:
>
> | Tài sản | Vai trò |
> |---|---|
> | `ai/rules/` | **Luật** — luôn áp dụng, auto-load qua CLAUDE.md |
> | `ai/skills/` + `~/.claude/skills` | **Chuyên gia** — năng lực đóng gói, nạp qua Skill tool |
> | `ai/workflows/` | **Quy trình** — nhiều bước, có checkpoint |
> | `ai/prompts/` | **Mẫu thực thi** — tác vụ một-lần, có tham số, tái sử dụng |
> | `ai/prompts/templates/` | **Mẫu output** — format chuẩn cho mọi artifact |

## Nguyên tắc

1. **KHÔNG duplicate rules** — prompt tham chiếu `ai/rules/*.md`, không chép lại nội dung.
2. **Output luôn theo template** — prompt sinh artifact phải trỏ tới file trong `templates/`.
3. **Một prompt = một tác vụ** — nếu cần nhiều bước + checkpoint → viết workflow, không viết prompt.
4. **Không tạo prompt theo feature** (`generate-login-testcase.md` ❌) — prompt generic theo tác vụ, feature là tham số đầu vào.
5. **Input thiếu/mơ hồ → KHÔNG đoán** — mọi prompt khi gặp nguồn thiếu, mâu thuẫn hoặc nhiều cách hiểu: ghi vào Open Questions (kèm các cách hiểu có thể) hoặc hỏi lại user, không tự suy diễn rồi sinh artifact từ giả định ngầm.

## Cách sử dụng

```text
# Tham chiếu trực tiếp trong chat
@ai/prompts/test-design/generate-testcases.md cho màn hình Declaration

# Hoặc được workflow tham chiếu (xem mapping bên dưới)
```

## Cấu trúc

| Thư mục | Giai đoạn | Prompts |
|---|---|---|
| `planning/` | Chiến lược & kế hoạch | test-strategy, test-plan, entry-exit-criteria, traceability-matrix |
| `figma/` | Figma → Requirement | analyze-screen, extract-business-rules, discover-user-flow, generate-requirements, identify-missing-requirements |
| `requirements/` | Requirement → Use Case | generate-usecase, review-requirement, gap-analysis, business-rule-extraction |
| `test-design/` | Use Case → Test Case | generate-scenarios, generate-testcases, boundary-analysis, exploratory-testing, risk-based-testing, regression-impact, api-test-design, mobile-test-design, generate-test-data |
| `automation/` | Test Case → Code | selenium-java, appium-java, api-java, api-contract-testing, locator-generation, framework-generation, page-object-generation |
| `non-functional/` | Performance · Security · A11y | performance-testing, security-testing, accessibility-testing |
| `review/` | AI reviewer | review-testcases, review-automation, review-requirements, review-locators, review-test-data, review-api-tests |
| `bug-analysis/` | Phân tích lỗi | root-cause, flaky-test-analysis, bug-triage, failure-classification, write-bug-report |
| `reporting/` | Báo cáo QA | sprint-report, release-report, qa-status-report, defect-summary |
| `templates/` | Mẫu output | requirement, usecase, testcase, bug-report, rca, test-report, test-plan, rtm, release-checklist |

## Mapping với workflows

Xem **`docs/workflow-mapping.md`** — nguồn chân lý duy nhất về quan hệ workflow ↔ skills ↔ prompts (kèm chuỗi end-to-end và decision tree chọn workflow). Quy tắc viết prompt mới: `docs/prompt-guidelines.md`.

## Format file prompt

```markdown
---
name: <kebab-case, trùng tên file>
description: <1 dòng — tác vụ làm gì>
inputs: <tham số cần cung cấp khi dùng>
references: <rules / skills / templates liên quan>
---

<Nội dung prompt — ngắn gọn, output rõ ràng>
```
