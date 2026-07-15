---
name: review-automation
description: Review automation code (Selenium/Appium) theo rules project — gate trước khi move generated-tests vào src
inputs: Đường dẫn file/package cần review (thường generated-tests/{web|mobile}/)
references: ai/rules/automation_rules.md, ai/rules/selenium_rules.md, ai/rules/appium_rules.md, ai/rules/locator_strategy.md
---

Review automation code: `{{path}}`

Kiểm tra theo rules project (trích vi phạm kèm file:line):

| Nhóm | Kiểm tra |
|---|---|
| **Kiến trúc** | POM đúng (assertion CHỈ trong test class)? Test extend đúng `WebBaseTest`/`MobileBaseTest`? Page/Screen đúng package `co.uk.ppac.{web\|mobile}`? Logic chung có bị viết lại thay vì dùng `core`? |
| **Locator** | Đúng priority (`locator_strategy.md`)? Có dynamic class, positional xpath, auto-generated ID? Locator unused? |
| **Wait** | Có `Thread.sleep`/fixed delay? Explicit wait đúng chỗ? |
| **Test data** | Hardcode data unique (email, username)? Dùng `DataGenerator`? Traceable? |
| **Độc lập** | Test phụ thuộc thứ tự / share state? Setup-teardown đủ? |
| **Assertions** | Mỗi test ≥1 assertion cuối, có message mô tả? |
| **Sạch code** | Còn `System.out.println` debug, code comment-out, biến unused? Duplication cần helper? |
| **Secrets** | Có hardcode credentials/URL nhạy cảm thay vì `.env`? |

Output:
1. Bảng findings: `| # | File:Line | Nhóm | Vi phạm | Mức độ | Code đề xuất |`
2. Kết luận gate: ✅ Đủ điều kiện move vào `src/` / ❌ Phải sửa trước (liệt kê blocking items)
