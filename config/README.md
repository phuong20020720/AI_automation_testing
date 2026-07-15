# Config — Cấu Hình Framework

## Cấu trúc

```text
config/
├── framework.properties        # Hành vi framework (browser, timeouts, appium server) — mọi env dùng chung
├── environments/
│   ├── uat.properties          # URL/host/username theo môi trường (KHÔNG có password)
│   └── _template.properties    # Copy thành dev/sit/prod.properties khi cần
└── mobile/
    └── android.properties      # Capabilities Android (thêm ios.properties khi hỗ trợ iOS)
```

## Thứ tự ưu tiên (cao → thấp) — `AppConfig` resolve

1. `-Dkey=value` (JVM system property)
2. Biến môi trường (`KEY_NAME` — dot thành underscore, viết hoa)
3. `.env` ở root (git-ignored — **chỗ duy nhất chứa password/token**)
4. `config/environments/<env>.properties` — env chọn qua `-Denv=...` hoặc `ENV` trong `.env`, mặc định `uat`
5. `config/mobile/<platform>.properties` — platform từ `platform.name`, mặc định `android`
6. `config/framework.properties`
7. `src/main/resources/config.properties` (legacy fallback — sẽ bỏ dần)

## Quy tắc

- ❌ KHÔNG hardcode URL/credentials trong Java — luôn `AppConfig.get("key")` / `AppConfig.getRequired("key")`
- ❌ KHÔNG đặt password/token/secret trong bất kỳ file nào ở `config/` — chúng thuộc về `.env` (mẫu: `.env.example`)
- ✅ `config/` được commit Git; `.env` thì không
- Đổi môi trường: `mvn test -P web -Denv=sit` (sau khi tạo `environments/sit.properties`)

## Ví dụ dùng trong code

```java
AppConfig.get("app.baseUrl");                          // null nếu thiếu
AppConfig.get("browser.name", "chrome");               // có default
AppConfig.getRequired("login.test.password");          // fail-fast nếu thiếu (đọc từ .env)
AppConfig.getInt("timeout.default", 15);
AppConfig.getBoolean("screenshot.on.failure", true);
```
