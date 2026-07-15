---
name: qa-status-report
description: Báo cáo trạng thái QA nhanh (daily/weekly) — ngắn gọn cho standup/stakeholder
inputs: Khoảng thời gian + nguồn data (reports, bug list, việc đang làm)
references: reporting/sprint-report.md (báo cáo đầy đủ cuối sprint)
---

Báo cáo trạng thái QA `{{period}}`:

Format NGẮN (đọc < 1 phút):

```markdown
# QA Status — <ngày/tuần>

**Trạng thái chung:** 🟢 On track / 🟡 Có rủi ro / 🔴 Blocked

## Đã làm
- <việc + kết quả, mỗi dòng 1 ý>

## Số liệu nhanh
| Test chạy | Pass | Fail | Bug mới | Bug đóng |
|---|---|---|---|---|

## Blockers / Rủi ro
- <gì đang chặn, cần ai xử lý>

## Kế tiếp
- <tối đa 3 việc>
```

Quy tắc: không quá 20 dòng; blocker phải nêu người/đội cần hành động; không có blocker thì ghi "Không" — đừng bỏ section.
