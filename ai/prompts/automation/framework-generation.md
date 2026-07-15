---
name: framework-generation
description: Scaffold / mở rộng automation framework (base classes, config, driver factory, reporting)
inputs: Phạm vi cần scaffold (module mới, listener, reporting, CI...)
references: skill framework_architect, workflows/generate_automation_framework.md, CLAUDE.md (package layout)
---

> Scaffold framework hoàn chỉnh từ đầu → dùng workflow `generate_automation_framework`. Prompt này cho việc mở rộng framework hiện có.

Mở rộng framework cho: `{{scope}}`

Ràng buộc kiến trúc project (BẮT BUỘC — xem CLAUDE.md):
- **1 Maven project duy nhất** tại `src/`, package `co.uk.ppac.{core,web,mobile,api}`
- `core` chứa TOÀN BỘ phần dùng chung: `base`, `config`, `driver`, `factory`, `utils`, `listeners`, `reporting`, `constants`
- `web`/`mobile` CHỈ chứa pages|screens + locators — logic chung đẩy về `core`
- Suite files ở `testng/` (`testng-web.xml`, `testng-mobile.xml`); profiles Maven `-P web` / `-P mobile`
- Config qua `config/` (framework / environments / mobile — xem `config/README.md`); secrets qua `.env` (mẫu `.env.example`) — KHÔNG hardcode vào config file/source

Trước khi tạo file mới: kiểm tra cấu trúc hiện có trong `src/` để tránh duplicate class/util đã tồn tại.

Output: file mới/sửa + giải thích vị trí trong kiến trúc + lệnh verify (`mvn test -P ...`)
