---
description: Inspect app Android/iOS thật qua Appium MCP để lấy element/locator THẬT (không bịa), rồi sinh locator theo POM convention của project.
skills:
  - ui_debug_agent
  - smart_locator_agent
mcp:
  - appium
---

# /inspect_mobile_locators — Lấy Locator Mobile THẬT Qua Appium MCP

> Mục đích: AI mở app đang chạy trên emulator/device THẬT, đọc UI hierarchy thật,
> lấy `resource-id` / `accessibility-id` / `content-desc` / `text` thật → sinh locator.
> **TUYỆT ĐỐI KHÔNG đoán/bịa element hay locator.** Mọi locator phải đến từ `appium_get_page_source` thật.

> **BẮT BUỘC đọc trước khi chạy:**
> - Rule: `ai/rules/appium_rules.md` — thứ tự ưu tiên locator mobile + điều NGHIÊM CẤM
> - Skill: `ui_debug_agent`, `smart_locator_agent`

---

## Yêu cầu môi trường (kiểm 1 lần)

| Thứ | Trạng thái mong đợi | Cách kiểm |
|---|---|---|
| MCP server `appium` | Đã khai báo ở `.claude/mcp.json` | Khởi động lại Claude Code sau khi thêm |
| Emulator/device | Đang chạy, hiện trong `adb devices` | `adb devices` → có dòng `... device` |
| `ANDROID_HOME`, `JAVA_HOME` | Đã set (đã cấu hình trong mcp.json env) | — |
| App under test | Đã cài hoặc có APK | xem `config/mobile/android.properties` (`app.package`, `app.activity`) |

> Lần đầu chạy `npx appium-mcp@latest` sẽ tải package — chờ vài chục giây là bình thường.

---

## Input cần từ User

| Input | Bắt buộc | Mô tả |
|---|---|---|
| Màn hình cần lấy locator | ✅ | VD: "màn Login", "form Declaration", "danh sách Contractor" |
| Cách điều hướng tới màn đó | ✅ nếu cần đăng nhập/nhiều bước | VD: "login bằng tài khoản test rồi vào tab Profile" — AI KHÔNG tự đọc `.env` |
| Screen class đích | ❌ | File `XxxScreen` + `XxxLocators` để ghi locator vào (xem Phase 5) |

---

## Các bước thực hiện

### Phase 1 — Kết nối device & app

1. `select_device` → chọn `emulator-5554` (hoặc device hiện có). Nếu nhiều device, hỏi User.
2. `appium_session_management` → start session. Capabilities lấy từ `config/mobile/android.properties`:
   - `appPackage` = `app.package`, `appActivity` = `app.activity` (app đã cài), HOẶC `app` = `app.path` (cài từ APK).
   - `autoGrantPermissions=true`, `noReset` theo file config.
3. Xác nhận app đã mở đúng màn hình mong muốn (nếu chưa → điều hướng ở Phase 2).

### Phase 2 — Điều hướng tới màn cần inspect

4. Dùng `appium_find_element` + `appium_gesture` (tap/scroll) để đi tới đúng màn hình User yêu cầu.
5. Nếu cần đăng nhập: dùng cách User cung cấp. **KHÔNG đọc `.env`, KHÔNG đoán credentials.**

### Phase 3 — Thu thập UI hierarchy THẬT  ⚠️ Trái tim của quy trình

6. `appium_get_page_source` → lấy XML hierarchy thật của màn hình hiện tại.
7. (Tùy chọn) `appium_screenshot` → ảnh đối chiếu trực quan với hierarchy.
8. Với mỗi element cần locator, đọc trực tiếp từ page source và ghi lại **đúng giá trị thật**:
   - `resource-id` (VD `com.ppac.app.sandbox:id/btn_login`)
   - `content-desc` (= accessibility id)
   - `text`
   - `class` (VD `android.widget.Button`)
   - `clickable`, `enabled`, `displayed`, `bounds`
9. Nếu element nằm ngoài màn hình → `appium_gesture` scroll tới, rồi `appium_get_page_source` lại. **Không lấy locator của element chưa thấy trong page source.**

### Phase 4 — Sinh locator theo priority (Appium)

10. Theo `ai/rules/appium_rules.md`, ưu tiên:

| # | Chiến lược | Java (`AppiumBy`) | Dùng khi |
|---|---|---|---|
| 1 | accessibility id | `AppiumBy.accessibilityId("...")` | có `content-desc` ổn định |
| 2 | resource-id (Android) | `AppiumBy.id("com.ppac...:id/...")` | có `resource-id` không động |
| 3 | id chung | `AppiumBy.id("...")` | — |
| 4 | androidUIAutomator | `AppiumBy.androidUIAutomator("new UiSelector().text(\"...\")")` | cần scroll/text match |
| 5 | xpath tương đối | `AppiumBy.xpath("//android.widget.Button[@text='Login']")` | cuối cùng |

11. NGHIÊM CẤM (theo rule): xpath tuyệt đối theo vị trí (`//FrameLayout[1]/LinearLayout[2]/...`), `Thread.sleep`, hardcode chờ.

### Phase 5 — Ghi vào POM (đúng convention project)

> Quy tắc vàng #3 của project: Screen KHÔNG chứa `By.xpath(...)` raw — luôn tham chiếu `XxxLocators.LOCATOR`.

12. Locator tĩnh → `public static final By UPPER_SNAKE` trong `co.uk.ppac.mobile.locators.XxxLocators`:
    ```java
    // src/main/java/co/uk/ppac/mobile/locators/LoginLocators.java
    public final class LoginLocators {
        public static final By EMAIL_INPUT  = AppiumBy.id("com.ppac.app.sandbox:id/et_email");
        public static final By LOGIN_BUTTON = AppiumBy.accessibilityId("login_button");
        private LoginLocators() {}
    }
    ```
13. Locator động → factory method:
    ```java
    public static By contractorRowByName(String name) {
        return AppiumBy.androidUIAutomator(
            "new UiScrollable(new UiSelector().scrollable(true))" +
            ".scrollIntoView(new UiSelector().text(\"" + name + "\"))");
    }
    ```
14. Code AI sinh ra để vào `generated-tests/mobile/` (staging), **không ghi thẳng `src/`** — QA review rồi mới move (quy tắc vàng #2).

### Phase 6 — Đóng session

15. `appium_session_management` → kết thúc session khi xong.

---

## Output bắt buộc

Với mỗi element, trả về bảng:

```markdown
| Element | Strategy | Locator (THẬT, từ page source) | Unique? | Nguồn |
|---|---|---|---|---|
| Nút Login | accessibility id | AppiumBy.accessibilityId("login_button") | ✅ | content-desc="login_button" |
| Ô Email  | resource-id      | AppiumBy.id("com.ppac.app.sandbox:id/et_email") | ✅ | resource-id thật |
```

+ Kèm `XxxLocators` class hoàn chỉnh (Phase 5) nếu User yêu cầu.

---

## NGHIÊM CẤM

| ❌ Không được | ✅ Đúng |
|---|---|
| Bịa `resource-id`/`content-desc` từ tên tính năng | Chỉ dùng giá trị có thật trong `appium_get_page_source` |
| Lấy locator của element chưa scroll tới (không có trong page source) | Scroll rồi `appium_get_page_source` lại |
| xpath tuyệt đối theo index | accessibility id → resource-id → uiautomator → xpath tương đối |
| Ghi `By.xpath(...)` raw trong Screen | Tham chiếu `XxxLocators.LOCATOR` |
| Ghi thẳng vào `src/` | Sinh vào `generated-tests/mobile/`, chờ QA review |
| Đọc `.env` để login | Hỏi User cách login / dùng fixture |

---

## Checklist cuối

- [ ] Session Appium mở đúng app + đúng màn hình
- [ ] Mọi locator trích từ `appium_get_page_source` thật (không đoán)
- [ ] Theo đúng priority: accessibility id > resource-id > id > uiautomator > xpath tương đối
- [ ] Không xpath tuyệt đối, không `Thread.sleep`
- [ ] Locator đặt trong `XxxLocators`, Screen chỉ tham chiếu
- [ ] Code sinh vào `generated-tests/mobile/`
- [ ] Đã đóng session
