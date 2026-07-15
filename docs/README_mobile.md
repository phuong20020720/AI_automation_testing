# PPAC Mobile Automation

Khung tự động hóa kiểm thử mobile (Android) cho PPAC — **Appium + Java + TestNG**, theo mô hình **Screen Object Model**.

Tuân thủ `.claude/rules/appium_rules.md`, `.claude/rules/automation_rules.md` và `.claude/rules/locator_strategy.md`.

---

## 1. Yêu cầu môi trường

| Thành phần | Phiên bản | Ghi chú |
|---|---|---|
| JDK | 17 | `JAVA_HOME` phải trỏ đúng JDK 17 |
| Maven | 3.9+ | |
| Node.js + Appium | Appium 3.x | `npm install -g appium` |
| Appium driver | `uiautomator2` | `appium driver install uiautomator2` |
| Android SDK | API 35 | `ANDROID_HOME` đã cấu hình |
| Emulator/Thiết bị | AVD `Pixel_7_API_35` | hoặc thiết bị thật bật USB Debugging |

> Môi trường Appium + Android SDK trên máy này đã được cài sẵn. Kiểm tra lại bằng `appium driver doctor uiautomator2`.

---

## 2. Cấu trúc project

```
ppac-mobile-automation/
├── pom.xml                     # Maven config + dependencies
├── testng.xml                  # TestNG suite
├── .env.example                # Mẫu biến môi trường (copy thành .env)
├── apps/                       # APK/AAB của app cần test (git-ignored)
├── test-data/
│   └── login-scenarios.json    # Dữ liệu test data-driven
└── src/
    ├── main/java/co/uk/ppac/mobile/
    │   ├── config/    AppConfig            # Đọc config đa nguồn
    │   ├── data/      LoginScenario        # Model dữ liệu test
    │   ├── drivers/   AppiumDriverFactory  # Tạo driver (ThreadLocal)
    │   │              CapabilitiesManager  # Build capabilities
    │   ├── screens/   BaseScreen           # Screen Object cha
    │   │              LoginScreen, HomeScreen
    │   └── utils/     WaitHelper           # Explicit waits
    │                  MobileGestures       # Scroll/swipe native
    │                  ScreenshotUtil       # Chụp màn hình -> Allure
    │                  DataGenerator        # Sinh data unique/traceable
    │                  TestDataReader       # Đọc JSON test data
    ├── main/resources/
    │   └── config.properties   # Cấu hình mặc định (không chứa secret)
    └── test/java/co/uk/ppac/mobile/
        ├── base/      BaseTest             # setup/teardown lifecycle
        └── tests/     LoginMobileTest, HomeMobileTest
```

---

## 3. Cấu hình

Mọi giá trị được phân giải theo thứ tự ưu tiên (cao → thấp):

1. System property — `-Dkey=value`
2. Biến môi trường — `KEY_NAME` (chữ hoa, dấu `.` thành `_`)
3. File `.env` ở thư mục gốc project
4. `src/main/resources/config.properties`

**Thiết lập lần đầu:**

```bash
cp .env.example .env
# Mở .env và điền: APP_PATH, TEST_USER_EMAIL, TEST_USER_PASSWORD
```

> `.env` đã được git-ignore. **Tuyệt đối không** hardcode credentials trong source hay `config.properties`.

App cần test: đặt file APK vào `apps/` rồi set `app.path` (vd `apps/ppac-mobile.apk`),
hoặc dùng `app.package` + `app.activity` nếu app đã cài sẵn trên thiết bị.

---

## 4. Chạy test

```bash
# 1. Khởi động emulator
emulator -avd Pixel_7_API_35

# 2. Khởi động Appium server (terminal khác)
appium

# 3. Chạy toàn bộ suite
mvn test

# Chạy theo group
mvn test -Dgroups=smoke
mvn test -Dgroups=regression

# Ghi đè cấu hình lúc chạy
mvn test -Dapp.path=apps/ppac-mobile.apk -Ddevice.name=Pixel_7_API_35
```

---

## 5. Báo cáo (Allure)

```bash
mvn allure:serve     # Mở report tương tác trong trình duyệt
mvn allure:report    # Sinh report tĩnh vào target/site/allure-maven-plugin
```

Screenshot tự động được đính kèm vào report khi test fail.

---

## 6. CI/CD

`.github/workflows/appium.yml` — chạy suite trên Android emulator qua GitHub Actions.
Cần khai báo secrets `TEST_USER_EMAIL`, `TEST_USER_PASSWORD` trong repo settings.

---

## 7. Quy ước & nguyên tắc

- **Screen Object Model** — locator + hành vi nằm trong `screens/`, assertion chỉ nằm trong `tests/`.
- **Locator** — ưu tiên `accessibilityId` → `id` (resource-id) → `androidUIAutomator` → `xpath`. Cấm xpath tuyệt đối theo vị trí.
- **Wait** — chỉ dùng explicit wait qua `WaitHelper`. Cấm `Thread.sleep()`.
- **Test data** — sinh động, unique, traceable: `auto_testName_timestamp_random`.
- **Test độc lập** — mỗi test có 1 Appium session riêng (ThreadLocal), không chia sẻ state.

## 8. Mở rộng

- **Thêm screen mới:** tạo class kế thừa `BaseScreen` trong `screens/`, khai báo locator bằng `AppiumBy`.
- **Thêm test mới:** tạo class kế thừa `BaseTest` trong `tests/`, gắn group `mobile`.
- **Hỗ trợ iOS:** thêm nhánh `XCUITestOptions` trong `CapabilitiesManager` dựa trên property `platform.name`.
