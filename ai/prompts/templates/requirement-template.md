---
name: requirement-template
description: Format chuẩn cho Requirements Document — lưu tại knowledge-base/{app|web}/requirements/<feature>.md
---

# Requirements — <Tên Feature/Màn hình>

| Metadata | |
|---|---|
| Hệ thống | PPAC App / Web / Contractor |
| Module | <mã module> |
| Nguồn design | <Figma URL + node-id> |
| Version / Ngày | <x.x — YYYY-MM-DD> |
| Người viết | <tên> |
| Trạng thái | Draft / Reviewed / Approved |

## 1. Mục đích
<Màn hình/feature giải quyết nhu cầu gì, cho actor nào, thuộc flow nào>

## 2. User Stories / Acceptance Criteria

### US-01: <tiêu đề>
> Là <actor>, tôi muốn <hành động> để <mục đích>.

**AC:**
- AC-01.1: <điều kiện chấp nhận — đo lường được>
- AC-01.2: ...

## 3. Business Rules

| ID | Rule | Loại | Nguồn |
|---|---|---|---|
| BR-01 | <NẾU... THÌ...> | Validation | <Figma node / stakeholder> |

## 4. UI Specification

| Element | Loại | Label | Bắt buộc | Validation | Ghi chú |
|---|---|---|---|---|---|

## 5. Flows
<Main flow + alternative flows, hoặc mermaid diagram. Tham chiếu knowledge-base/{app|web}/flows/ nếu có>

## 6. Assumptions
- ASM-01: <điều giả định vì nguồn không nói rõ>

## 7. Open Questions
- Q-01: <câu hỏi cần BA/Designer trả lời> — **Trạng thái:** Open / Answered: <trả lời>
