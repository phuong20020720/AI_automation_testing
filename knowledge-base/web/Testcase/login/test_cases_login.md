# PPAC Login — Manual Test Cases (Full RBT)

> **Hệ thống:** PPAC v2 — Compliance/Regulatory portal (UK)
> **Môi trường:** UAT — `https://ppac-v2-web-uat.prod-verification.compliant101.co.uk/`
> **Tài khoản test:** `ppac_obr@yopmail.com` / `WLTvhMwEuC` (giả định chưa bật 2FA)
> **Phương pháp:** AI-RBT (Risk-Based Testing) 6 bước — Coverage **Trung bình**
> **Ngày tạo:** 2026-05-07
> **QA Owner:** harry.vo@ppac.co.uk
> **Tổng số TC:** 36 (Critical 8 / High 10 / Medium 15 / Low 3)

---

## Tóm tắt phạm vi

**Trong scope:**
- Functional: Email + Password authentication, 2FA TOTP (Google Authenticator)
- UI Validation: form fields, show/hide password, mobile responsive ≥ 360px
- Security cơ bản: SQLi, XSS, transport HTTPS, cookie flags, anti-enumeration
- Session management cơ bản, Logout flow

**Ngoài scope:**
- Forgot Password full flow (chỉ test entry-point link)
- Account lockout/disable, full performance/pentest
- Audit log verify (chỉ ở backend, role `obr` không xem được)
- Cross-browser matrix lớn, WCAG accessibility

**Security findings ghi nhận (cần escalate cho PO/Security):**
- R-SEC-01: Không rate-limit + không lockout + không giới hạn OTP retry → brute-force possible
- R-SEC-02: Browser back sau logout không bị block (theo user confirm Q25)
- R-SEC-03: TOTP có thể không có replay protection (Q9, Q10 không giới hạn)

---

## Bảng Test Cases

| TC ID | Module | Risk Level | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_LOGIN_TC_001 | M1 UI | Low | Verify Login page render đầy đủ elements | Browser sạch, có URL UAT | 1. Mở `https://ppac-v2-web-uat.prod-verification.compliant101.co.uk/`<br>2. Đợi page load xong<br>3. Quan sát các elements | 1. Page load thành công, không có console error<br>2. Hiển thị logo PPAC, tiêu đề Login/Sign in<br>3. Có Email input, Password input (có eye icon), Submit button, link "Forgot password"<br>4. KHÔNG có CAPTCHA, Remember me, Social login | High | N/A |
| PPAC_LOGIN_TC_002 | M1 UI | Low | Show/Hide password toggle | Đang ở Login page | 1. Nhập password `WLTvhMwEuC`<br>2. Quan sát input — phải mask<br>3. Click icon mắt<br>4. Quan sát input<br>5. Click icon mắt lần 2 | 1. Ban đầu hiển thị dạng dot/bullet<br>2. Sau click 1 → hiển thị plain text `WLTvhMwEuC`<br>3. Sau click 2 → quay lại dạng masked | Medium | Password = `WLTvhMwEuC` |
| PPAC_LOGIN_TC_003 | M1 UI | Low | Mobile responsive ≥ 360px width | Chrome DevTools mở | 1. Mở Login page<br>2. DevTools → set viewport `375x667` (iPhone SE)<br>3. Quan sát layout<br>4. Set viewport `360x640` (boundary tối thiểu)<br>5. Quan sát layout | 1. Form không bị overflow horizontal scroll<br>2. Tất cả fields/buttons clickable, không che lấp<br>3. Text readable, không bị cắt | Medium | Viewport `360x640`, `375x667` |
| PPAC_LOGIN_TC_004 | M1 UI | Low | Smoke cross-browser render | Có Chrome, Edge, Firefox, Safari (latest 2 versions) | 1. Mở Login page lần lượt trên 4 browsers<br>2. Quan sát render<br>3. Thử nhập 1 ký tự vào mỗi field | 1. Page render đồng nhất trên cả 4 browsers<br>2. Input fields chấp nhận text bình thường | Medium | N/A |
| PPAC_LOGIN_TC_005 | M2 Validation | Medium | Bỏ trống Email | Đang ở Login page | 1. Để trống Email<br>2. Nhập Password `WLTvhMwEuC`<br>3. Click Submit | 1. Hiển thị error tại Email (vd: "Email is required")<br>2. KHÔNG gửi request tới backend (kiểm tra Network tab)<br>3. Form không bị submit | High | Email = "", Password = `WLTvhMwEuC` |
| PPAC_LOGIN_TC_006 | M2 Validation | Medium | Bỏ trống Password | Đang ở Login page | 1. Nhập Email `ppac_obr@yopmail.com`<br>2. Để trống Password<br>3. Click Submit | 1. Hiển thị error tại Password (vd: "Password is required")<br>2. KHÔNG gửi request tới backend | High | Email = `ppac_obr@yopmail.com`, Password = "" |
| PPAC_LOGIN_TC_007 | M2 Validation | Medium | Email sai format (EP) | Đang ở Login page | Lặp với mỗi giá trị Test Data:<br>1. Nhập email vào field<br>2. Tab khỏi field hoặc click Submit | 1. Hiển thị error "Invalid email format"<br>2. KHÔNG cho submit | High | `abc` / `abc@` / `@abc.com` / `abc@@a.com` / `abc@a` / `abc def@a.com` / `.abc@a.com` |
| PPAC_LOGIN_TC_008 | M2 Validation | Medium | Email max length boundary (BVA) | Đang ở Login page | 1. Nhập email 253 ký tự (max-1)<br>2. Submit với password đúng — kiểm tra accept<br>3. Reset, nhập 254 ký tự (max) — submit, kiểm tra accept<br>4. Reset, nhập 255 ký tự (max+1) — kiểm tra reject hoặc truncate | 1. 253, 254 ký tự: chấp nhận, gọi API auth<br>2. 255 ký tự: bị truncate xuống 254 (verify trong DOM input value) HOẶC validation error rõ | Medium | Email tự sinh đúng độ dài + `@yopmail.com`, Password = `WLTvhMwEuC` |
| PPAC_LOGIN_TC_009 | M2 Validation | Medium | Password max length boundary (BVA) | Đang ở Login page | 1. Nhập email valid<br>2. Nhập password 127 ký tự<br>3. Reset, nhập password 128 ký tự<br>4. Reset, nhập password 129 ký tự<br>5. Mỗi lần verify input value độ dài | 1. 127, 128 ký tự: input chấp nhận đầy đủ<br>2. 129 ký tự: bị cắt xuống 128 hoặc validation error | Medium | Password = chuỗi `Aa1!` lặp lại đủ 127/128/129 ký tự |
| PPAC_LOGIN_TC_010 | M2 Validation | Low | Trim whitespace trong Email | Đang ở Login page | 1. Nhập email `  ppac_obr@yopmail.com  ` (có space đầu/cuối)<br>2. Nhập password đúng<br>3. Submit<br>4. Mở Network tab kiểm tra request payload | 1. Email được trim trước khi gửi (network payload không có space) — login thành công<br>HOẶC<br>2. App reject với validation error rõ ràng (KHÔNG silently fail với "wrong credential") | Low | Email = `  ppac_obr@yopmail.com  `, Password = `WLTvhMwEuC` |
| PPAC_LOGIN_TC_011 | M3 Auth | High | Login thành công không 2FA (Happy Path) | Tài khoản `ppac_obr@yopmail.com` chưa bật 2FA | 1. Mở Login page<br>2. Nhập Email `ppac_obr@yopmail.com`<br>3. Nhập Password `WLTvhMwEuC`<br>4. Click Submit<br>5. Quan sát response và URL | 1. API auth trả 200 + token<br>2. Redirect tới `/dashboard` (REQ-10)<br>3. Cookie session được set (verify ở TC_025)<br>4. Dashboard render với username/email user | Critical | Email = `ppac_obr@yopmail.com`, Password = `WLTvhMwEuC` |
| PPAC_LOGIN_TC_012 | M3 Auth | High | Password sai → generic error | Đang ở Login page | 1. Nhập Email `ppac_obr@yopmail.com`<br>2. Nhập password sai `WrongPass@123`<br>3. Click Submit | 1. API trả 401 (không 200, không 500)<br>2. UI hiển thị **generic** error: "Invalid email or password" hoặc tương đương (REQ-13)<br>3. KHÔNG hiển thị "Password incorrect" (chống enumeration)<br>4. Form vẫn ở Login page, password field clear | Critical | Email = `ppac_obr@yopmail.com`, Password = `WrongPass@123` |
| PPAC_LOGIN_TC_013 | M3 Auth | High | Email không tồn tại → generic error (anti-enumeration) | Đang ở Login page | 1. Nhập email không tồn tại `nonexistent_user_xyz_${timestamp}@yopmail.com`<br>2. Nhập password bất kỳ valid format<br>3. Click Submit<br>4. Đo response time, so với TC_012 | 1. UI hiển thị **CHÍNH XÁC** message giống TC_012 (REQ-13)<br>2. Response time tương đương TC_012 (timing attack — nếu lệch >300ms → flag Issue) | Critical | Email = `nonexistent_user_xyz_20260507_a1b2@yopmail.com`, Password = `AnyValidPass@1` |
| PPAC_LOGIN_TC_014 | M3 Auth | Medium | Server 500 error handling | DevTools mở, có thể block API hoặc dùng MITM | 1. DevTools → Network → Block API endpoint auth (hoặc intercept response edit thành 500)<br>2. Nhập credential đúng<br>3. Click Submit | 1. UI hiển thị friendly error (vd: "Something went wrong, please try again")<br>2. KHÔNG expose stack trace, hostname, version<br>3. Button Submit vẫn click được sau lỗi (không treo) | Medium | Email/Password valid, network mocked về 500 |
| PPAC_LOGIN_TC_015 | M3 Auth | Medium | Mất kết nối khi đang submit | Login page với credential valid | 1. DevTools → Network → throttle "Offline"<br>2. Click Submit<br>3. Đợi 5–10s<br>4. Bật lại network<br>5. Click Submit lần 2 | 1. Lần 1: error "Network error" hoặc spinner timeout, không treo UI<br>2. Lần 2: thành công, vào Dashboard | Low | Email/Password valid |
| PPAC_LOGIN_TC_016 | M4 TOTP | High | Login thành công với TOTP đúng (Happy Path 2FA) | Tài khoản `ppac_2fa_user@yopmail.com` đã bật 2FA, biết TOTP secret | 1. Mở Login page<br>2. Nhập email + password đúng<br>3. Click Submit<br>4. Verify chuyển sang trang nhập TOTP<br>5. Mở Google Authenticator, lấy 6-digit code hiện hành<br>6. Nhập code → Submit | 1. Sau bước 3: chuyển trang TOTP, KHÔNG vào Dashboard ngay<br>2. Sau bước 6: vào Dashboard | Critical | `ppac_2fa_user@yopmail.com` + valid TOTP từ Authenticator |
| PPAC_LOGIN_TC_017 | M4 TOTP | High | TOTP sai → reject, retry được | Đang ở trang nhập TOTP (sau auth basic) | 1. Nhập TOTP `000000`<br>2. Submit<br>3. Quan sát error<br>4. Thử nhập lại TOTP đúng | 1. API trả 401, UI hiển thị "Invalid code" hoặc tương đương<br>2. Vẫn ở trang TOTP, KHÔNG quay về Login<br>3. Nhập đúng lần 2 → vào Dashboard (vì Q9 không giới hạn) | Critical | TOTP = `000000` |
| PPAC_LOGIN_TC_018 | M4 TOTP | High | TOTP hết hạn (BVA timing) | Đang ở trang nhập TOTP, có đồng hồ chính xác | 1. Lấy TOTP từ Authenticator app, ghi lại thời điểm<br>2. Đợi 4:55 → nhập code → Submit<br>3. Reset, lấy TOTP mới, đợi 5:05 → nhập → Submit | 1. Trong window ≤ 5 phút: chấp nhận, vào Dashboard<br>2. Quá 5 phút (5:05): reject với "Code expired" hoặc generic invalid | High | TOTP code + timing 4:55 và 5:05 |
| PPAC_LOGIN_TC_019 | M4 TOTP | Medium | TOTP replay protection (R-SEC-03) | Đã login thành công với TOTP `123456` ở phiên trước, vừa logout | 1. Login lại với credential<br>2. Nhập **lại** TOTP `123456` (TOTP cũ vẫn còn trong window 30s)<br>3. Submit | 1. Hệ thống reject TOTP đã dùng (best practice OWASP)<br>2. NẾU accept → flag Issue cho security team (R-SEC-03 — replay possible) | Medium | TOTP code đã dùng ở phiên trước |
| PPAC_LOGIN_TC_020 | M4 TOTP | Medium | TOTP định dạng sai (EP) | Đang ở trang nhập TOTP | Lặp với mỗi Test Data:<br>1. Nhập input vào field TOTP<br>2. Submit | 1. Validation error trước khi gửi backend (vd: "Code must be 6 digits") | Medium | `12345` (5 digits) / `1234567` (7 digits) / `abcdef` / `12 34 56` (có space) / `` (rỗng) |
| PPAC_LOGIN_TC_021 | M4 TOTP | Medium | Quay lại Login từ trang TOTP — không bypass | Đang ở trang nhập TOTP (chưa nhập) | 1. Bấm browser Back hoặc click logo về trang Login<br>2. Quan sát state form<br>3. Đăng nhập lại từ đầu | 1. Session intermediate (đã pass auth basic) bị invalidate<br>2. Login lần sau yêu cầu re-enter credential + TOTP từ đầu<br>3. KHÔNG bypass được TOTP bằng cách đi vòng URL | Low | N/A |
| PPAC_LOGIN_TC_022 | M5 Redirect | Medium | Redirect Dashboard sau login | Tại Login page với credential valid | 1. Sau khi login thành công ở TC_011<br>2. Quan sát URL bar<br>3. Quan sát layout Dashboard | 1. URL ends với `/dashboard` (hoặc path tương đương)<br>2. Page Dashboard render đúng (có nav, user menu) | High | Account valid |
| PPAC_LOGIN_TC_023 | M5 Redirect | Medium | Deep-link redirect sau login | Browser sạch, chưa login | 1. Mở URL `https://ppac-v2-web-uat.../profile` (hoặc page protected)<br>2. Quan sát redirect<br>3. Login với credential valid<br>4. Quan sát URL sau login | 1. Bước 2: redirect về Login page với query `?redirect=/profile` hoặc tương đương<br>2. Bước 4: sau login → quay lại `/profile` (KHÔNG đi `/dashboard`) | Medium | URL deep-link `/profile` |
| PPAC_LOGIN_TC_024 | M5 Redirect | Medium | Truy cập /login khi đã login | Đã login, đang ở Dashboard | 1. Gõ URL `/login` vào address bar<br>2. Enter | 1. Redirect tự động về Dashboard, KHÔNG show lại Login form | Medium | N/A |
| PPAC_LOGIN_TC_025 | M6 Session | High | Verify cookie flags (HttpOnly + Secure + SameSite) | Đã login thành công | 1. DevTools → Application → Cookies → chọn domain<br>2. Quan sát cookie session (vd: `session`, `JSESSIONID`, `auth_token`)<br>3. Kiểm tra 3 flags: HttpOnly, Secure, SameSite | 1. HttpOnly = true (REQ-15)<br>2. Secure = true (REQ-15)<br>3. SameSite = Strict hoặc Lax (REQ-15) | Critical | N/A |
| PPAC_LOGIN_TC_026 | M6 Session | Medium | Multi-tab session share | Đã login ở tab 1 | 1. Mở tab 2 cùng browser<br>2. Truy cập URL Dashboard | 1. Tab 2 cũng đã login (cookie shared)<br>2. KHÔNG bị redirect về Login | Medium | N/A |
| PPAC_LOGIN_TC_027 | M6 Session | High | Token KHÔNG expose ngoài cookie | Đã login | 1. DevTools → Application → Local Storage<br>2. DevTools → Application → Session Storage<br>3. Tìm các key chứa "token", "jwt", "auth", "session"<br>4. Kiểm tra URL bar có `token=` query param không | 1. Token KHÔNG nằm trong localStorage/sessionStorage<br>2. Token KHÔNG xuất hiện trong URL<br>3. NẾU có → flag Issue cho security team | High | N/A |
| PPAC_LOGIN_TC_028 | M7 Logout | High | Logout invalidate session | Đã login | 1. Click button Logout<br>2. Quan sát redirect<br>3. DevTools → Cookies, verify session cookie bị xóa hoặc set expired<br>4. Truy cập URL Dashboard trực tiếp | 1. Redirect về Login page<br>2. Cookie session bị clear/expired<br>3. Bước 4: bị redirect về Login (token cũ không truy cập được Dashboard) | High | N/A |
| PPAC_LOGIN_TC_029 | M7 Logout | High | Browser back sau logout (R-SEC-02) | Đã login → đã logout, đang ở Login page | 1. Bấm browser Back button<br>2. Quan sát<br>3. Bấm Back tiếp (xem có vào được trang protected nào không)<br>4. Mở DevTools Network → kiểm tra response headers các trang đã visit | 1. Khi back → KHÔNG hiển thị nội dung Dashboard cached<br>2. Phải redirect về Login hoặc hiển thị "Session expired"<br>3. Verify HTTP headers: `Cache-Control: no-store`, `Pragma: no-cache`<br>4. **Lưu ý:** Q25 user nói "không bị block" — nếu thực tế cho phép xem trang cũ → đây là **Compliance Issue** cần escalate | High | N/A |
| PPAC_LOGIN_TC_030 | M7 Logout | Medium | Logout → Re-login cùng tài khoản | Đã logout | 1. Login lại với cùng credential<br>2. Quan sát flow<br>3. DevTools → so sánh cookie value với session cũ | 1. Login thành công, vào Dashboard bình thường<br>2. Session mới được tạo (cookie value khác cookie cũ) | Medium | Cùng account như TC_011 |
| PPAC_LOGIN_TC_031 | M8 ForgotPwd | Low | Click Forgot Password link | Đang ở Login page | 1. Click link "Forgot password?" (hoặc tên tương đương) | 1. Điều hướng đến trang Forgot Password (URL chứa `forgot` hoặc `reset`)<br>2. Trang Forgot Password load được, hiển thị form nhập email<br>3. (Test full flow Forgot Password — out of scope) | Medium | N/A |
| PPAC_LOGIN_TC_032 | M9 Security | High | SQL Injection — không bypass được auth | Đang ở Login page | Lặp với mỗi payload Test Data:<br>1. Nhập email payload<br>2. Nhập password payload<br>3. Submit<br>4. Quan sát response | 1. KHÔNG bypass auth (luôn 401 hoặc 400)<br>2. KHÔNG expose database error message<br>3. UI hiển thị generic error | Critical | Email/Password pairs:<br>• `admin' OR '1'='1` / `anything`<br>• `' OR 1=1 --` / `' OR 1=1 --`<br>• `admin'/*` / `*/--`<br>• `'; DROP TABLE users; --` / `x` |
| PPAC_LOGIN_TC_033 | M9 Security | High | XSS payload — escape đúng | Đang ở Login page | Lặp với mỗi payload:<br>1. Nhập payload vào Email field<br>2. Password = `Test@123`<br>3. Submit<br>4. Quan sát error message hiển thị | 1. Payload bị escape/sanitize, KHÔNG trigger alert popup<br>2. Nếu echo back trong error message → phải HTML-encoded | Critical | Email payloads:<br>• `<script>alert('xss')</script>`<br>• `"><img src=x onerror=alert(1)>`<br>• `<svg onload=alert(1)>`<br>• `javascript:alert(1)` |
| PPAC_LOGIN_TC_034 | M9 Security | High | HTTP redirect to HTTPS + HSTS | Browser sạch | 1. Truy cập `http://ppac-v2-web-uat.prod-verification.compliant101.co.uk/`<br>2. Quan sát redirect<br>3. DevTools → Network → kiểm tra response headers | 1. HTTP request được redirect sang HTTPS (301/308)<br>2. Response có header `Strict-Transport-Security` (HSTS) | High | N/A (URL HTTP) |
| PPAC_LOGIN_TC_035 | M9 Security | High | Brute-force basic (R-SEC-01 — Issue Note) | Email tồn tại + tools (Postman/curl) hoặc UI manual | 1. Thử login với password sai 20 lần liên tục từ cùng IP<br>2. Đo response time mỗi lần<br>3. Quan sát có CAPTCHA xuất hiện không / có HTTP 429 không | 1. Vì user nói "không có rate limit" → tất cả 20 lần trả 401 bình thường (test PASS theo spec hiện tại)<br>2. **FINDING cho Security Review** (R-SEC-01): "No rate limiting on login endpoint, brute-force protection insufficient for compliance system" — cần ghi Bug/Issue | Medium | Email valid + 20 password sai khác nhau |
| PPAC_LOGIN_TC_036 | M9 Security | Medium | Response không expose info nội bộ | Mở DevTools Network | 1. Trigger các error: 401 (TC_012), 400 (TC_007), 500 (TC_014)<br>2. Quan sát response body và headers từng case | 1. Response body KHÔNG có stack trace, framework version, server version<br>2. Header `Server` (nếu có) chỉ generic, không phiên bản chi tiết<br>3. Header `X-Powered-By` không tồn tại | Medium | Multiple invalid requests |

---

## Đề xuất hậu Bước 6

### A. Security Issues cần escalate cho PO/Security team
- **R-SEC-01:** Bổ sung rate-limit (vd: 10 req/phút/IP) trên endpoint `/auth/login`
- **R-SEC-02:** Verify behavior `Cache-Control: no-store` sau logout (TC_029)
- **R-SEC-03:** Verify TOTP replay protection (TC_019)

### B. Out-of-scope cần test ở phase tiếp theo
- Forgot Password full flow (reset email, token expiry, password complexity tại reset)
- 2FA setup flow (binding Google Authenticator, recovery codes)
- Session timeout test full 8h (cần test environment ổn định)
- Concurrent login same account từ device khác nhau (Q12 chỉ confirm single role)
- WCAG accessibility (keyboard nav, screen reader)

### C. Cần PO/BA confirm thêm trước khi execute
- Tài khoản test có 2FA enabled (cho M4 TOTP) — cần PO cấp + chia sẻ TOTP secret
- Confirm exact text của error messages để verify chính xác
