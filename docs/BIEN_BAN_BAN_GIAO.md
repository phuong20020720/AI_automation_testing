# Biên Bản Bàn Giao — Dự Án PPAC v2.0 (AI Automation for QC)

| Hạng mục | Thông tin |
|---|---|
| **Tên dự án** | PPAC v2.0 — AI Automation for QC |
| **Repository** | `ppac-v2.0-ai-automation-for-qc` (tổ chức GitHub: `ppacvn`) |
| **Phạm vi** | Tự động hoá kiểm thử QC: Web (Selenium) + Mobile App (Appium) + tài sản AI hỗ trợ QC |
| **Bên giao** | harry.vo@ppac.co.uk |
| **Bên nhận** | ……………………………………… |
| **Ngày lập** | 2026-07-08 |

---

## Tuần 3 – Bàn giao & tổng quan dự án PPAC v2.0 (AI Automation for QC)

### Mục tiêu
Giúp thành viên mới tiếp nhận đầy đủ dự án trong một tuần: nắm rõ **cấu trúc project**, **môi trường**, **quy trình chạy test**, cách **ứng dụng AI** trong công việc QC, và cách **vận hành báo cáo** — để có thể làm việc độc lập sau khi bàn giao.

### Nội dung đã hướng dẫn

---

#### 1. Cấu trúc project

**1.1. Sơ đồ thư mục cấp cao & vai trò**

| Thư mục | Vai trò |
|---|---|
| `knowledge-base/` | Đầu vào tri thức: `requirements/`, `flows/`, `Testcase/` (web + app) |
| `generated-tests/` | Code AI sinh ra (staging) — QC review xong mới chuyển vào `src/` |
| `src/` | Code chính thức: framework Java (`core` / `web` / `mobile` / `api`) |
| `config/` | Cấu hình framework (commit git, **không chứa password**) |
| `testng/` | Các file suite `.xml` để chọn nhóm test cần chạy |
| `execution/` | Script chạy test, sinh báo cáo, export test case |
| `artifacts/` | Output khi chạy: log, screenshot, report, test-results |
| `ai/` | Tài sản AI: `rules/`, `skills/`, `workflows/`, `prompts/` |
| `docs/` | Tài liệu vận hành (HUONG_DAN_CHAY, HUONG_DAN_TELEGRAM, README_mobile…) |

**1.2. Quy trình 4 lớp (nguyên tắc vàng)**

```
knowledge-base (input)  →  generated-tests (AI staging)  →  src (code chính thức)  →  artifacts (output)
```
> AI **không** sinh code thẳng vào `src/` — luôn qua `generated-tests/<platform>/`, QC review rồi mới move.

**1.3. Chi tiết `src/` (1 Maven duy nhất, `pom.xml` ở root)**

- `src/main/java/co/uk/ppac/`
  - `core/` — **hạ tầng dùng chung (web + mobile)**: `base/` (BasePage, BaseScreen), `config/` (AppConfig, ConfigReader), `driver/` (DriverFactory, AppiumDriverFactory), `factory/` (CapabilitiesManager), `utils/` (DataGenerator, WaitHelper, MobileGestures, XlsxReader, MailinatorOtpFetcher…), `reporting/` (ScreenshotUtil).
  - `web/` — Selenium: `pages/` + `locators/` (+ `recon/` khảo sát UI, `utils/` session helper).
  - `mobile/` — Appium: `screens/` + `locators/` (+ `data/`); module Bally đặt ở `screens/bally/`, `locators/bally/`.
  - `api/` — REST API (đã tạo khung, **chưa dùng**).
- `src/test/java/co/uk/ppac/`
  - `core/base/` — `WebBaseTest`, `MobileBaseTest` (vòng đời test + log từng test).
  - `core/listeners/` — `TelegramNotifier`, `TelegramSuiteListener`.
  - `web/tests/` — theo module: `auth`, `compliantportal`, `workermanagement`, `security`, `session`, `ui`, `validation`.
  - `mobile/tests/` — smoke, login, home, newcheck, signup… + `bally/` (onboarding E2E).

**1.4. Nguyên tắc Page Object Model (POM)**

- Mỗi `XxxPage`/`XxxScreen` có một `XxxLocators` tương ứng.
- Locator tĩnh: `public static final By UPPER_SNAKE`; locator động: `public static By factory(...)`.
- Page/Screen **không** chứa `By.xpath(...)` thô — luôn tham chiếu `XxxLocators.LOCATOR`.
- Test class kế thừa `WebBaseTest` hoặc `MobileBaseTest`.

---

#### 2. Môi trường

**2.1. Phần mềm cần cài (một lần)**

| Phần mềm | Bản | Dùng cho |
|---|---|---|
| JDK (Java) | 17+ | Web + App |
| Maven | 3.9+ | Web + App |
| Google Chrome | mới nhất | Web (driver tự tải qua WebDriverManager) |
| Node.js | LTS | App |
| Appium | 3.x + driver `uiautomator2` | App |
| Android Studio + emulator | — | App (AVD `Pixel7_API33`) |

**2.2. Cấu hình phân tầng — `AppConfig` resolve (cao → thấp)**

1. `-Dkey=value` (JVM system property)
2. Biến môi trường
3. **`.env`** ở root (git-ignored — **chỗ duy nhất chứa password/token**)
4. `config/environments/<env>.properties` (mặc định `uat`)
5. `config/mobile/<platform>.properties` (mặc định `android`)
6. `config/framework.properties`
7. `src/main/resources/config.properties` (legacy fallback)

> Trong code: `AppConfig.get("key")`, `AppConfig.getRequired("key")`, `AppConfig.getInt(...)`, `AppConfig.getBoolean(...)`. **Không hardcode** URL/credentials.

**2.3. Môi trường UAT (mặc định)**

- Web base URL: `https://ppac-v2-web-uat.prod-verification.compliant101.co.uk/`
- Tài khoản test (username trong `config/environments/uat.properties`; **password ở `.env`**): `ppac_obr@yopmail.com`, `ppac_stgeo@yopmail.com`, `ppac.team101@gmail.com`.
- App Android: package `com.ppac.app.sandbox` (cài sẵn trên emulator); capabilities ở `config/mobile/android.properties`.
- `config/framework.properties`: `browser.name=chrome`, `browser.headless=false`, `timeout.default=15`, `appium.server.url=http://127.0.0.1:4723/`, `screenshot.on.failure=true`.

**2.4. Bảo mật**

- Password/token chỉ ở `.env` và `*.local.json` — **không commit**; luôn có `.env.example` làm mẫu.
- `config/` chỉ chứa URL/host/username, **không** chứa password.

---

#### 3. Quy trình chạy

**3.1. Chọn nền tảng qua Maven profile**

```powershell
mvn test -P web       # Selenium (Chrome) — suite mặc định testng/testng-web.xml
mvn test -P mobile    # Appium (emulator) — mặc định nếu không truyền -P
```

**3.2. Chạy test WEB (chỉ cần Chrome)**

```powershell
mvn test -P web                                            # tất cả test web
mvn test -P web "-Dsuite.xml=testng/testng-m1-critical.xml"  # chạy 1 nhóm
mvn test -P web "-Dbrowser.headless=true"                  # ẩn trình duyệt
mvn test -P web "-Dtelegram.notify=false"                  # không gửi Telegram
```

**3.3. Chạy test APP (theo thứ tự)**

1. Bật emulator: `emulator -avd Pixel7_API33` (đợi tới màn home).
2. Bật Appium ở cửa sổ riêng: `appium` (thấy `listener started on http://127.0.0.1:4723`).
3. Kiểm tra thiết bị: `adb devices` (thấy `emulator-5554  device`).
4. Chạy test: `mvn test -P mobile` (hoặc `-Dsuite.xml=testng/testng-login.xml`).

**3.4. Một số suite tiêu biểu (`testng/`)**

| Nhóm | File suite |
|---|---|
| Web tổng | `testng-web.xml`, `testng-compliant-portal.xml`, `testng-worker-management.xml` |
| Web critical | `testng-m1-critical.xml`, `testng-m5-critical.xml`, `testng-login-valid.xml` |
| App | `testng-mobile.xml`, `testng-login.xml`, `testng-signup.xml`, `testng-smoke.xml` |
| App New Check | `testng-newcheck-normal.xml`, `testng-newcheck-notskillcard.xml`, `testng-macec.xml` |

**3.5. Cờ dòng lệnh hữu ích**

| Cờ | Ý nghĩa |
|---|---|
| `-P web` / `-P mobile` | Chọn nền tảng |
| `-Dsuite.xml=...` | Chọn file suite |
| `-Denv=sit` | Đổi môi trường (mặc định `uat`) |
| `-Dbrowser.headless=true` | Chạy web ẩn trình duyệt |
| `-Dtelegram.notify=false` | Tắt thông báo Telegram (listener Java) |

---

#### 4. AI hỗ trợ công việc QC

**4.1. Bốn lớp tài sản AI (`ai/`)**

| Lớp | Vai trò | Cách dùng |
|---|---|---|
| `ai/rules/` | Luật coding bắt buộc | Tự động (CLAUDE.md `@import`) |
| Skills | Năng lực đóng gói | Agent tự nạp khi task khớp |
| `ai/workflows/` | Quy trình nhiều bước có checkpoint | Gõ `/<tên_workflow>` |
| `ai/prompts/` | Mẫu thực thi một-lần, có tham số | `@ai/prompts/<nhóm>/<tên>.md <tham số>` |

**4.2. Công cụ & năng lực có sẵn**

- Công cụ chính: **Claude Code** (+ ChatGPT, Claude, GitHub Copilot bổ trợ).
- **10 skills**: `rbt_manual_testing`, `requirements_analyzer`, `qa_automation_engineer`, `smart_locator_agent`, `locator_healer_agent`, `flaky_test_analyzer`, `framework_architect`, `test_data_generator`, `ui_debug_agent`, `inspect_mobile_locators`.
- **12 workflows** (VD `/generate_testcases_from_requirements`, `/generate_manual_testcases_rbt`, `/generate_locator`, `/generate_automation_from_testcases`, `/analyze_flaky_tests`).
- **Prompts** theo 9 nhóm: `figma/`, `requirements/`, `test-design/`, `automation/`, `review/`, `bug-analysis/`, `reporting/`, `planning/`, `non-functional/` + `templates/` (mẫu output chuẩn).

**4.3. Ứng dụng vào công việc QC**

- Phân tích requirement, sinh test scenario/case/checklist, review requirement, sinh bug report → `ai/prompts/requirements|test-design|review|bug-analysis` + template ở `ai/prompts/templates/`.
- Hỗ trợ automation: sinh locator/code Selenium/Appium, giải thích & review code → skill + `ai/prompts/automation/`.
- Xử lý lỗi: phân tích log, giải thích error, debug, flaky → skill + `ai/prompts/bug-analysis/`.
- Mọi artifact (requirement, test case, bug, report) **phải theo mẫu trong `ai/prompts/templates/`** để tương thích script export.

**4.4. Nguyên tắc sử dụng AI**

- Không phụ thuộc hoàn toàn vào AI; luôn kiểm chứng kết quả.
- Không chia sẻ dữ liệu nhạy cảm; kiểm tra lại business logic trước khi dùng.

---

#### 5. Vận hành báo cáo (report)

**5.1. Script sinh báo cáo — `execution/scripts/generate_test_report_java.ps1`**

```powershell
powershell -ExecutionPolicy Bypass -File execution/scripts/generate_test_report_java.ps1 -Project selenium   # web
powershell -ExecutionPolicy Bypass -File execution/scripts/generate_test_report_java.ps1 -Project appium     # app
```

Luồng script (5 bước): **(1)** chạy `mvn test -P web|mobile` → **(2)** đọc `target/surefire-reports/testng-results.xml` → **(3)** sinh `.md` (bảng PASS/FAIL/SKIP theo test method) → **(4)** xuất `.xlsx` → **(5)** gửi Telegram kèm đường dẫn báo cáo.

Tham số:

| Tham số | Ý nghĩa |
|---|---|
| `-Project selenium\|appium\|both` | Chọn bộ test chạy báo cáo (mặc định `both`) |
| `-Suite testng/....xml` | Chỉ chạy một suite cụ thể |
| `-SkipRun` | Chỉ sinh report từ kết quả có sẵn (không chạy lại test) |

**5.2. Nơi lưu kết quả**

- Báo cáo tổng hợp: `artifacts/test-results/test_result_<project>_<timestamp>.md` + `.xlsx` (**có commit**).
- Chi tiết theo class: `target/surefire-reports/` (`.txt`/`.xml`).
- Report giàu hình: **Allure** (từ `target/allure-results/`).
- Console: dòng cuối `Tests run: X, Failures: Y, Errors: Z, Skipped: W`.

**5.3. Thông báo Telegram**

- Gửi tự động sau khi chạy report: tổng kết PASS/FAIL/SKIP + danh sách test lỗi + đường dẫn báo cáo.
- Cấu hình bot ở `.claude/telegram.local.json` (token + chatId — **không commit**); chi tiết ở [docs/HUONG_DAN_TELEGRAM.md](HUONG_DAN_TELEGRAM.md).
- Tắt: để trống `chatId`, hoặc chạy test với `-Dtelegram.notify=false`.

**5.4. Xuất test case ra Excel/CSV**

- `execution/scripts/export_testcases_to_excel.ps1`, `export_testcases_to_csv.ps1`, `export_testcases_combined_to_excel.ps1`.
- Lưu ý format: bản xuất **bỏ cột Figma Ref**; bảng tổng quan dùng tên cột **"Dải TC"** (không dùng "TC ID").

**5.5. Bằng chứng khi test fail (evidence)**

- Screenshot tự chụp khi FAIL → `artifacts/screenshots/` + đính kèm Allure (tắt bằng `screenshot.on.failure=false`).
- Log tập trung ở `artifacts/logs/`: `framework/` (hạ tầng) + `execution/<TestClass>.log` (từng test).

---

### Kết quả đạt được
- Nắm rõ cấu trúc project và quy trình 4 lớp; biết code chính thức nằm ở đâu và POM tổ chức thế nào.
- Tự thiết lập môi trường, hiểu cơ chế config phân tầng và quản lý secret an toàn.
- Chạy được test web/app theo từng suite/module với các cờ dòng lệnh phù hợp.
- Ứng dụng được tài sản AI (rules/skills/workflows/prompts) vào phân tích requirement, thiết kế test, automation và xử lý lỗi.
- Vận hành được quy trình báo cáo đầy đủ: sinh report `.md`/`.xlsx`, đọc kết quả, xuất test case, và nhận thông báo Telegram.

---

## Xác nhận bàn giao

Hai bên đã cùng rà soát nội dung, source code, tài liệu và tài khoản nêu trên. Bên nhận xác nhận đã tiếp nhận đầy đủ và có khả năng vận hành dự án.

| Vai trò | Họ tên | Chữ ký | Ngày |
|---|---|---|---|
| Bên giao | ………………………… | ………… | ………… |
| Bên nhận | ………………………… | ………… | ………… |
| Quản lý / Xác nhận | ………………………… | ………… | ………… |
