# Cấu Trúc Project — PPAC

> Cập nhật: 2026-06-11 (tái cấu trúc AI-first, gộp 2 Maven project thành 1)

```
PPAC /
├── ai/                          # Tài sản AI
│   ├── rules/                   #   Luật coding bắt buộc (nạp qua CLAUDE.md @import)
│   │     automation_rules.md, locator_strategy.md,
│   │     selenium_rules.md, appium_rules.md
│   ├── skills/                  #   Skill riêng project (bộ chung ở ~/.claude/skills)
│   ├── workflows/               #   Quy trình nhiều bước (lệnh /<tên>)
│   └── prompts/                 #   Prompt mẫu tái sử dụng (xem ai/prompts/README.md)
│         figma/                 #     Figma → Requirement (analyze-screen, extract-business-rules...)
│         requirements/          #     Requirement → Use Case (generate-usecase, review, gap-analysis)
│         test-design/           #     Sinh scenarios/test cases, BVA, RBT, regression impact
│         automation/            #     Sinh code Selenium/Appium/API, locator, page object
│         review/                #     AI review: test cases, automation code, locators, test data
│         bug-analysis/          #     Root cause, flaky, triage, phân loại failure
│         reporting/             #     Sprint/release/status report, defect summary
│         templates/             #     MẪU OUTPUT chuẩn: requirement, usecase, testcase, bug, RCA, report
│
├── knowledge-base/              # Đầu vào tri thức
│   ├── app/                     #   requirements/, flows/, Testcase/ (mobile)
│   ├── web/                     #   requirements/, flows/ (web)
│   └── contractor/              #   uat-db.contractors.json (UAT dump)
│
├── config/                      # Cấu hình framework — COMMIT Git, KHÔNG chứa password (xem config/README.md)
│   ├── framework.properties     #   Browser, timeouts, appium server, screenshot.on.failure
│   ├── environments/            #   uat.properties (URL/host/username theo env) + _template.properties
│   └── mobile/                  #   android.properties (capabilities; thêm ios.properties khi cần)
│
├── src/                         # 1 Maven project DUY NHẤT (pom.xml ở root)
│   ├── main/java/co/uk/ppac/
│   │   ├── core/                #   FRAMEWORK DÙNG CHUNG (web + mobile + api)
│   │   │   ├── base/            #     BasePage (web), BaseScreen (mobile)
│   │   │   ├── config/          #     ConfigReader, AppConfig
│   │   │   ├── driver/          #     DriverFactory (Selenium), AppiumDriverFactory
│   │   │   ├── factory/         #     CapabilitiesManager
│   │   │   ├── utils/           #     DataGenerator, WaitHelper, MobileGestures, LocatorUtils,
│   │   │   │                    #     XlsxReader, DownloadHelper, TestDataReader, MailinatorOtpFetcher...
│   │   │   ├── reporting/       #     ScreenshotUtil (Allure attachment)
│   │   │   └── constants/       #     Hằng số framework (chưa dùng)
│   │   ├── web/                 #   CHỈ pages/ + locators/ (Selenium page objects)
│   │   ├── mobile/              #   CHỈ screens/ + locators/ (Appium screen objects)
│   │   └── api/                 #   REST API tests (chưa dùng)
│   ├── main/resources/          #   config.properties (LEGACY fallback — config thật ở config/)
│   ├── test/java/co/uk/ppac/
│   │   └── core/
│   │       ├── base/            #   WebBaseTest, MobileBaseTest (test lifecycle + per-test logging)
│   │       └── listeners/       #   TelegramNotifier, TelegramSuiteListener
│   └── test/resources/          #   log4j2.xml (logging), META-INF/services (đăng ký listener)
│
├── testng/                      # Suite files: testng-web.xml, testng-mobile.xml
│
├── test-data/                   # Data test ngoài code
│   ├── api/                     #   CSV cho newman (CSCS)
│   ├── apps/                    #   APK/IPA (git-ignored)
│   └── mobile/                  #   JSON cho TestDataReader
│
├── generated-tests/             # Staging code AI sinh ra — QA review xong mới move vào src/
│   ├── web/
│   └── mobile/
│
├── execution/                   # Mọi thứ để CHẠY test
│   ├── scripts/                 #   generate_test_report_java.ps1, export_*.ps1, md_to_xlsx...
│   └── api/cscs/                #   Postman collection + Run.txt (newman)
│
├── artifacts/                   # Output sinh ra khi chạy
│   ├── logs/                    #   framework/ (log hạ tầng, roll theo ngày) + execution/ (<TestClass>.log) — KHÔNG commit
│   ├── screenshots/             #   PNG chụp khi test fail (ScreenshotUtil) — KHÔNG commit
│   ├── reports/cscs/            #   Báo cáo HTML newman — KHÔNG commit
│   ├── test-results/            #   test_result_*.md/.xlsx (từ script report) — CÓ commit
│   └── ai/                      #   Trace output AI sinh ra — CÓ commit
│         generated-requirements/, generated-testcases/,
│         generated-automation/, reviews/
│
├── docs/                        # Tài liệu
│   ├── framework-design.md      #   Thiết kế + lý do kiến trúc framework Java
│   ├── ai-architecture.md       #   Mô hình 5 lớp tài sản AI + luồng dữ liệu + guardrails
│   ├── prompt-guidelines.md     #   Quy tắc viết/dùng prompt + checklist + anti-patterns
│   ├── workflow-mapping.md      #   Bản đồ workflow ↔ skills ↔ prompts (nguồn chân lý mapping)
│   └── HUONG_DAN_CHAY, HUONG_DAN_TELEGRAM, README_mobile (hướng dẫn vận hành)
│
├── .claude/                     # Cấu hình Claude Code (settings, mcp, scripts/notify-telegram.ps1)
├── .github/workflows/           # CI: appium.yml (mvn test -P mobile tại root)
├── CLAUDE.md                    # Import ai/rules + quy ước project
├── pom.xml                      # Maven gộp: profiles `web` / `mobile`
├── .env / .env.example          # Secrets runtime (git-ignored / mẫu)
└── PROJECT_STRUCTURE.md
```

## Cách chạy test

```bash
mvn test -P web       # Selenium suite (testng/testng-web.xml)
mvn test -P mobile    # Appium suite (testng/testng-mobile.xml) — mặc định nếu không truyền -P
```

Hoặc chạy kèm report + Telegram:

```powershell
powershell -ExecutionPolicy Bypass -File execution/scripts/generate_test_report_java.ps1 -Project appium
```

## Cách dùng tài sản AI (`ai/`)

4 lớp tài sản, mỗi lớp một cách dùng:

| Lớp | Vai trò | Cách dùng |
|---|---|---|
| `ai/rules/` | **Luật** — luôn áp dụng | Tự động — CLAUDE.md `@import`, không cần gọi |
| Skills (`~/.claude/skills`) | **Chuyên gia** — năng lực đóng gói | Agent tự nạp qua Skill tool khi task khớp |
| `ai/workflows/` | **Quy trình** — nhiều bước, có checkpoint chờ duyệt | Gõ `/<tên_workflow>` (VD `/analyze_flaky_tests`) |
| `ai/prompts/` | **Mẫu thực thi** — tác vụ một-lần, có tham số | Tham chiếu `@ai/prompts/<nhóm>/<tên>.md` + tham số |

### Dùng prompt — cú pháp

```text
@ai/prompts/<nhóm>/<tên>.md <tham số theo mục `inputs` trong frontmatter của file>
```

Ví dụ thực tế:

```text
# Sinh test cases cho màn Declaration
@ai/prompts/test-design/generate-testcases.md cho màn Declaration,
requirements ở knowledge-base/app/requirements/declaration.md

# Review code AI sinh ra trước khi move vào src/
@ai/prompts/review/review-automation.md generated-tests/mobile/

# Audit toàn bộ locator
@ai/prompts/review/review-locators.md src/main/java/co/uk/ppac/web/locators/

# Phân loại failures sau khi chạy suite
@ai/prompts/bug-analysis/failure-classification.md artifacts/reports/cscs/
```

### Chọn prompt hay workflow?

- Tác vụ **một-lần, kết quả ngay** (review 1 file, phân tích 1 bug, sinh 1 báo cáo) → **prompt**.
- Quy trình **nhiều bước cần checkpoint** (sinh TC từ Figma, fix flaky có verify) → **workflow** — các prompt trùng địa hạt đều có dòng redirect sang workflow tương ứng ở đầu file.
- Mọi artifact (requirement, TC, bug report, RCA, report) **phải theo format trong `ai/prompts/templates/`** — đây là nguồn chân lý duy nhất về format, tương thích các script export trong `execution/scripts/`.

> Chi tiết đầy đủ (mapping workflow → prompts, format frontmatter, quy tắc thêm prompt mới): xem `ai/prompts/README.md`.

## Nguyên tắc vàng

1. **Tách 4 lớp:** `knowledge-base (input) → generated-tests (AI staging) → src (code chính thức) → artifacts (output)`.
2. **AI không sinh code thẳng vào `src/`** — sinh vào `generated-tests/<platform>/`, QA review rồi mới move.
3. **POM bắt buộc — tách lớp locator:**
   - Mỗi `XxxPage`/`XxxScreen` có 1 `XxxLocators` tương ứng (web: `co.uk.ppac.web.locators`, mobile: `co.uk.ppac.mobile.locators`).
   - Locator tĩnh: `public static final By UPPER_SNAKE`. Locator động: `public static By factoryMethod(...)`.
   - Page/Screen KHÔNG chứa `By.xpath(...)` raw — luôn tham chiếu `XxxLocators.LOCATOR`.
   - Helper chung: `core/utils/LocatorUtils.xpathLiteral(...)`.
4. **Toàn bộ hạ tầng framework đặt ở `core`** (base/config/driver/factory/utils/listeners/reporting/constants) — `web`/`mobile` chỉ chứa page/screen objects + locators + tests. Test class extend `WebBaseTest` hoặc `MobileBaseTest` từ `core/base`.
5. **Naming nhất quán:** suffix `Page`/`Screen`/`Test`; artifact prefix `test_cases_`/`test_result_`/`bug_report_`.
6. **Secret tách riêng:** `.env`, `*.local.json` — không commit, luôn có `.env.example`. KHÔNG đặt password/token trong `config/` hay source.
7. **Không hardcode config:** URL/timeout/credentials luôn đọc qua `AppConfig.get(...)` — không bao giờ `driver.get("https://uat...")` trong code. Layer config xem `config/README.md`.
8. **Log tập trung:** mọi log ra `artifacts/logs/` qua SLF4J + Log4j2 (`src/test/resources/log4j2.xml`) — `framework/` cho hạ tầng, `execution/<TestClass>.log` cho từng test (tự route qua ThreadContext). KHÔNG dùng `System.out.println`.

## Config & Logs — cách hoạt động

**Config** — `AppConfig` resolve theo thứ tự (cao → thấp): `-Dkey=value` → biến môi trường → `.env` → `config/environments/<env>.properties` → `config/mobile/<platform>.properties` → `config/framework.properties` → `config.properties` legacy. Đổi môi trường: `mvn test -P web -Denv=sit` (hoặc `ENV=sit` trong `.env`); mặc định `uat`.

**Logs** — chạy test là tự có:
- `artifacts/logs/framework/framework.log` — log hạ tầng (driver, config, listeners), roll theo ngày, giữ 14 ngày
- `artifacts/logs/execution/<TestClass>.log` — log riêng từng test class, gồm cả dòng `=== START/END <test> - PASS/FAIL ===` từ base test
- Screenshot khi fail: `artifacts/screenshots/` + đính kèm Allure (tắt bằng `screenshot.on.failure=false`)
