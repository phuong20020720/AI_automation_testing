---
name: page-object-generation
description: Sinh Page Object (web) / Screen Object (mobile) từ DOM/page source thật
inputs: URL hoặc screen + tên trang; DOM snapshot nếu đã có
references: ai/rules/automation_rules.md (POM + naming), automation/locator-generation.md, skill ui_debug_agent
---

Sinh Page/Screen Object cho: `{{page}}`

Quy trình:
1. Lấy locators qua `automation/locator-generation.md` (DOM thật, đã verify)
2. Sinh class theo chuẩn project:
   - Web: `<Tên>Page.java` trong `co.uk.ppac.web` · Mobile: `<Tên>Screen.java` trong `co.uk.ppac.mobile`
   - Locator fields: lowerCamelCase + hậu tố element (`loginButton`, `usernameInput`)
   - Methods: hành động nghiệp vụ (`login(email, password)`) thay vì thao tác lẻ; method điều hướng trả về Page/Screen đích
   - Explicit wait bên trong method tương tác — KHÔNG Thread.sleep
   - KHÔNG assertion trong Page/Screen class; cung cấp method trạng thái cho test assert (`isDisplayed()`, `getErrorMessage()`)
3. Ghi vào `generated-tests/{web|mobile}/` — không ghi trực tiếp `src/`

Output: class hoàn chỉnh + bảng locator (element → locator → đã verify chưa) + gợi ý method còn thiếu cho các flow phổ biến của trang
