---
name: bug-report-template
description: Format chuẩn cho Bug Report
---

# BUG-<ID>: <Tiêu đề — hành vi sai + vị trí, 1 dòng>

| | |
|---|---|
| **Severity** | 🔴 Critical / 🟠 Major / 🟡 Minor / 🟢 Trivial |
| **Priority** | P1 / P2 / P3 / P4 |
| **Module** | <module/màn hình> |
| **Môi trường** | <env, build/version, OS, browser/device> |
| **Tài khoản test** | <role + account dùng tái hiện (không ghi password)> |
| **Phát hiện bởi** | Manual / Automation (<TC ID / test method>) |
| **Ngày phát hiện** | YYYY-MM-DD |

## Steps to Reproduce
1. <bước cụ thể — người khác làm theo tái hiện được 100%>
2. ...

**Test data dùng:** <data cụ thể>
**Tần suất:** Luôn luôn / Thỉnh thoảng (x/y lần)

## Expected Result
<theo requirement/design nào — trích ID nếu có>

## Actual Result
<điều thực tế xảy ra — trích error message nguyên văn>

## Evidence
- Screenshot/video: <link/path>
- Log/stacktrace: <trích đoạn liên quan>

## Ghi chú
<workaround nếu có, nghi ngờ root cause, bug liên quan>
