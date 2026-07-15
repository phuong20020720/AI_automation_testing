---
name: exploratory-testing
description: Sinh exploratory testing charters (session-based) cho khu vực chưa có test case chi tiết
inputs: Feature/màn hình + thời lượng session dự kiến
references: test-design/generate-scenarios.md
---

Sinh exploratory testing charters cho: `{{feature}}`

Mỗi charter theo format:

```text
CHARTER EX-01
Explore:  <khu vực/chức năng>
With:     <data, tool, điều kiện — VD: tài khoản role X, network chậm, mobile viewport>
To discover: <loại rủi ro nhắm tới — VD: lỗi validation, mất data khi back, race condition>
Time-box: 30–60 phút
Priority: High/Medium/Low
```

Hướng tour đa dạng (chọn phù hợp): Data tour (giá trị cực đoan) · Interruption tour (back/refresh/timeout/mất mạng) · Permission tour (đổi role giữa chừng) · Concurrency tour (2 tab/2 user) · UI-state tour (empty/loading/error) · Localization tour (tiếng Việt có dấu, ký tự dài)

Cuối output: **bảng ghi nhận session** trống để QA điền khi thực thi:

| Charter | Thời gian thực tế | Bugs tìm thấy | Notes/Câu hỏi | Follow-up TC cần viết |
|---|---|---|---|---|
