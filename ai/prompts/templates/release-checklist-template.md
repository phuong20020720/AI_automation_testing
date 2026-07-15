---
name: release-checklist-template
description: Format chuẩn Release Checklist — gate Go/No-Go trước release
---

> File lưu tại `artifacts/test-results/release_checklist_<version>.md`.
> Nguyên tắc: trung thực — mục không đạt ghi rõ KHÔNG ĐẠT kèm bằng chứng, không "đạt vớt".

# Release Checklist — <version>

| | |
|---|---|
| **Version/Build** | <version> |
| **Ngày đánh giá** | YYYY-MM-DD |
| **Người đánh giá** | <QA owner> |
| **Khuyến nghị** | ✅ GO / 🟡 GO có điều kiện / 🔴 NO-GO |

> Lý do khuyến nghị (1-2 dòng, viết NGAY sau bảng — người đọc bận chỉ đọc tới đây):

## 1. Test execution

| # | Hạng mục | Ngưỡng | Thực tế | Bằng chứng | Đạt? |
|---|---|---|---|---|---|
| 1 | Regression suite pass rate | ≥ 95% | | artifacts/reports/ | ☐ |
| 2 | Smoke trên build release | 100% | | | ☐ |
| 3 | API tests (newman) pass | 100% | artifacts/reports/ | | ☐ |
| 4 | TC High-risk đã chạy hết | 100% | RTM | | ☐ |

## 2. Defects

| # | Hạng mục | Ngưỡng | Thực tế | Đạt? |
|---|---|---|---|---|
| 5 | Bug Critical open | 0 | | ☐ |
| 6 | Bug Major open | 0 (hoặc có waiver ký tên) | | ☐ |
| 7 | Bug open còn lại đã triage + có kế hoạch | 100% | | ☐ |

## 3. Coverage & chất lượng

| # | Hạng mục | Đạt? |
|---|---|---|
| 8 | RTM không còn gap High-risk | ☐ |
| 9 | Failures đã classify (Product/Automation/Env) — không còn fail chưa rõ nguyên nhân | ☐ |
| 10 | Known issues + workaround đã ghi vào release notes | ☐ |

## 4. Điều kiện kèm theo (nếu GO có điều kiện)

| # | Điều kiện | Người chịu trách nhiệm | Deadline |
|---|---|---|---|

## 5. Sign-off

| Vai trò | Tên | Quyết định | Ngày |
|---|---|---|---|
| QA Lead | | | |
| Product/PM | | | |
