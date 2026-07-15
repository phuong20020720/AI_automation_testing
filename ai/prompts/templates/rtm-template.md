---
name: rtm-template
description: Format chuẩn Requirements Traceability Matrix (RTM) — BR ↔ UC ↔ TC ↔ Automation
---

> File lưu tại `knowledge-base/{app|web}/planning/rtm_<scope>.md`.
> Chỉ ghi mapping có bằng chứng (ID xuất hiện trong file nguồn); mapping nghi ngờ đánh dấu `(?)`.

# RTM — <Scope>

> Nguồn: <requirements + Testcase + src/generated-tests đã quét>
> Ngày sinh: YYYY-MM-DD · Tổng BR: <n> · Tổng TC: <n>

## 1. Ma trận trace

| BR / Requirement | Mô tả ngắn | Risk | UC | TC IDs | Automation (class#method) | Trạng thái coverage |
|---|---|---|---|---|---|---|
| BR-01 | <1 dòng> | High | UC_DECL_01 | PPAC_DECL_TC_001, _002 | DeclarationTest#testSubmitValid | ✅ Đủ |
| BR-02 | <1 dòng> | High | — | — | — | 🔴 GAP — chưa có TC |
| — | (orphan) | — | — | PPAC_DECL_TC_015 | — | 🟡 TC không trace về BR nào |

Trạng thái: ✅ Đủ (có TC, risk High có automation) · 🟡 Một phần / orphan · 🔴 Gap

## 2. Tổng hợp

| Chỉ số | Giá trị |
|---|---|
| BR có ít nhất 1 TC | x/y (z%) |
| BR High-risk có automation | x/y (z%) |
| TC có automation | x/y (z%) |
| Orphan TC | <n> |

## 3. Gap cần xử lý (xếp theo risk)

| # | Gap | Risk | Hành động đề xuất |
|---|---|---|---|
| 1 | BR-02 chưa có TC | High | Sinh TC bằng `test-design/generate-testcases.md` |
