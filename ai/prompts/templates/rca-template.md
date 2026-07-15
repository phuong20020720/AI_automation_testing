---
name: rca-template
description: Format chuẩn cho Root Cause Analysis
---

# RCA: <tiêu đề sự cố/bug>

| | |
|---|---|
| **Liên quan** | BUG-<ID> / test <file:method> |
| **Ngày phân tích** | YYYY-MM-DD |
| **Phân loại** | 🐞 Product / 🤖 Automation / 🌐 Environment |
| **Confidence** | <%> |

## 1. Triệu chứng
<Điều quan sát được — error, hành vi sai, tần suất>

## 2. Evidence
| # | Evidence | Nguồn | Cho thấy điều gì |
|---|---|---|---|

## 3. Phân tích (5 Whys)
1. **Tại sao <triệu chứng>?** → <nguyên nhân 1>
2. **Tại sao <nguyên nhân 1>?** → ...
3. ... (dừng khi tới nguyên nhân gốc có thể hành động được)

## 4. Root Cause
<Kết luận — 1-2 câu, kèm file:line nếu là code>

**Giả thuyết đã loại trừ:** <giả thuyết — lý do loại>

## 5. Fix
| Loại | Hành động | Owner | Trạng thái |
|---|---|---|---|
| Ngắn hạn | <sửa ngay> | | |
| Dài hạn | <chống tái diễn — process/framework/test bổ sung> | | |

## 6. Bài học
<Tại sao không bắt được sớm hơn? Cần thay đổi gì trong quy trình test?>
