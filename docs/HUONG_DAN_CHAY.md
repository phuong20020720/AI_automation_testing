# Hướng Dẫn Chạy Test & Dùng Git — PPAC Automation

> File này dành cho người mới. Mỗi bước có lệnh **copy-paste sẵn**.
> Dự án đã gộp thành **1 Maven duy nhất** ở thư mục gốc, chọn web/mobile qua *profile*.

**Thư mục gốc dự án (mở PowerShell vào đây trước):**

```powershell
cd "C:\Users\kiuph\OneDrive - DTSource PPAC\Automation\PPAC Automation for QC - Github repo\ppac-v2.0-ai-automation-for-qc"
```

---

## 0. Cấu trúc ngắn gọn

```
src/main/java/co/uk/ppac/
├── core/      # khung framework dùng chung (driver, config, utils...)
├── web/       # Selenium: pages, locators, recon  (code web)
└── mobile/    # Appium: screens, locators, data   (code app)
src/test/java/co/uk/ppac/
├── web/tests/      # test web (36 test)
└── mobile/tests/   # test app (9 test)
config/        # cấu hình (KHÔNG chứa mật khẩu — mật khẩu ở .env)
testng/        # các file "suite" để chọn nhóm test cần chạy
```

- **Web** = Selenium (trình duyệt Chrome). **Mobile/App** = Appium (emulator Android).
- Chọn bên nào bằng *profile*: `-P web` hoặc `-P mobile` (mặc định là `mobile`).

---

## A. Cài đặt 1 lần

| Phần mềm | Bản | Kiểm tra |
|---|---|---|
| **JDK (Java)** | 17+ | `java -version` |
| **Maven** | 3.9+ | `mvn -v` |
| **Google Chrome** | mới nhất | (chỉ cần cho test **web**) |
| **Node.js** | LTS | `node -v` — (chỉ cần cho test **app**) |
| **Appium** | 3.x | `appium -v` — (chỉ cho **app**) |
| **UiAutomator2 driver** | — | `appium driver list --installed` — (chỉ cho **app**) |
| **Android Studio + emulator** | — | (chỉ cho **app**) |

Cài Appium + driver (nếu chạy app, làm 1 lần):
```powershell
npm install -g appium
appium driver install uiautomator2
```

> Driver Chrome cho test web **không cần cài tay** — framework tự tải qua WebDriverManager.

---

## B. Chạy test WEB (Selenium)

Chỉ cần Chrome trên máy. Vào thư mục gốc dự án rồi:

```powershell
# Chạy TẤT CẢ test web
mvn test -P web

# Chạy 1 nhóm (suite) có sẵn — vd nhóm critical M1
mvn test -P web "-Dsuite.xml=testng/testng-m1-critical.xml"

# Chạy ẩn trình duyệt (headless) cho nhanh
mvn test -P web "-Dbrowser.headless=true"

# Không gửi Telegram khi chạy thử
mvn test -P web "-Dtelegram.notify=false"
```

Các file suite web có trong thư mục [../testng/](../testng/) (vd `testng-compliant-portal.xml`, `testng-worker-management.xml`, `testng-m5-critical.xml`...).

---

## C. Chạy test APP (Appium) — làm theo thứ tự

### Bước 1 — Bật emulator
Android Studio → `Device Manager` → bấm ▶ ở `Pixel7_API33`.
Hoặc bằng lệnh:
```powershell
emulator -avd Pixel7_API33
```
Đợi tới khi hiện màn hình home (~30s–2 phút). App `com.ppac.app.sandbox` phải đã cài sẵn trên emulator.

### Bước 2 — Bật Appium (mở **cửa sổ PowerShell RIÊNG**, để nguyên suốt buổi test)
```powershell
appium
```
Thấy dòng `Appium REST http interface listener started on http://127.0.0.1:4723` là sẵn sàng.

### Bước 3 — Kiểm tra emulator đã kết nối (cửa sổ làm việc)
```powershell
adb devices
```
Phải thấy 1 dòng kiểu `emulator-5554   device`.

### Bước 4 — Chạy test app
```powershell
# Chạy TẤT CẢ test app
mvn test -P mobile
adb -s emulator-5554 emu kill

# Chạy 1 nhóm có sẵn — vd login
mvn test -P mobile "-Dsuite.xml=testng/testng-login.xml"

# Chạy nhóm khác: testng-signup.xml, testng-smoke.xml, testng-newcheck-normal.xml...
```

> Thông tin app (package/activity/thiết bị) đã đặt sẵn trong [../config/mobile/android.properties](../config/mobile/android.properties) — không cần truyền tay.

---

## D. Cấu hình & mật khẩu (config)

Giá trị cấu hình được đọc theo thứ tự ưu tiên (cao → thấp):

1. `-Dkey=value` ngay trên dòng lệnh
2. Biến môi trường
3. **`.env`** ở thư mục gốc — *chỗ duy nhất để mật khẩu/token* (file này KHÔNG lên git)
4. `config/environments/<env>.properties` (URL, host, email theo môi trường — mặc định `uat`)
5. `config/mobile/android.properties` (capabilities Android)
6. `config/framework.properties` (browser, timeout, appium server)
7. `src/main/resources/config.properties` (dự phòng cũ)

- Đổi môi trường (nếu sau này có `sit`/`prod`): `mvn test -P web "-Denv=sit"`
- Mật khẩu mới: sửa trong file `.env` (xem mẫu [../.env.example](../.env.example)). **Không** đặt mật khẩu trong `config/`.

---

## E. Xem kết quả

- **Console**: dòng cuối `Tests run: X, Failures: Y, Errors: Z, Skipped: W`.
- **Report chi tiết**: thư mục `target/surefire-reports/` (file `.txt`/`.xml` mỗi class).
- **Báo cáo + Telegram** (web/app, có file .md/.xlsx):
  ```powershell
  powershell -ExecutionPolicy Bypass -File execution/scripts/generate_test_report_java.ps1 -Project selenium   # web
  powershell -ExecutionPolicy Bypass -File execution/scripts/generate_test_report_java.ps1 -Project appium     # app
  ```
  Kết quả lưu ở `artifacts/test-results/`.

---

## F. Lỗi thường gặp (app)

| Lỗi | Cách xử lý |
|---|---|
| `Could not find a connected Android device` | Emulator chưa bật → làm lại **C-Bước 1**, kiểm tra `adb devices` |
| `Connection refused ... 4723` | Appium chưa chạy → làm lại **C-Bước 2** |
| `Tests run: 0` | Sai tên suite/class → kiểm tra lại tên file trong `testng/` |
| `running scripts is disabled` | Chạy 1 lần: `Set-ExecutionPolicy -Scope CurrentUser RemoteSigned` rồi gõ `Y` |
| Test fail do `NoSuchElement`/`Timeout` | App có thể đổi UI → cập nhật locator trong `web/locators` hoặc `mobile/locators` |

---

# G. Hướng dẫn dùng Git (cho người mới)

> Git giống nút **"Save game"** cho code: lưu lại các **mốc** để sau này quay về được.
> Có 2 nơi: **máy bạn (local)** và **GitHub (online, cả team thấy)**.

### G1. Các lệnh xem (an toàn, chỉ xem — chạy thoải mái)

```powershell
git status        # xem file nào đang thay đổi
git status --short      # xem gọn (mỗi file 1 dòng + ký hiệu trạng thái)
git log --oneline -10   # xem 10 mốc gần nhất
git branch        # xem đang ở nhánh nào (dấu * là nhánh hiện tại)
```

#### Ý nghĩa ký hiệu của `git status --short`

Mỗi dòng có **ký hiệu** cho biết file đang ở trạng thái nào:

| Ký hiệu | Viết tắt | Nghĩa |
|---|---|---|
| `M` | Modified | File cũ đã bị **sửa** nội dung |
| `A` | Added | File **mới**, đã `git add` (sẵn sàng commit) |
| `D` | Deleted | File bị **xóa** |
| `R` | Renamed | File được **đổi tên / di chuyển** (git vẫn giữ lịch sử) |
| `RM` | Renamed + Modified | Vừa **đổi chỗ vừa sửa** nội dung |
| `??` | Untracked | File git **chưa theo dõi** (vd `.env` — đang bị bỏ qua) |
| `U` | Unmerged | File đang **xung đột (conflict)** khi merge — cần xử lý |

**Có 2 cột ký hiệu:**
- Cột **trái** = trạng thái đã `git add` (staged — sẽ vào commit)
- Cột **phải** = trạng thái ở thư mục làm việc (sửa nhưng **chưa** `git add`)

Ví dụ:
```
M  pom.xml          ← M ở cột trái: đã add, sẵn sàng commit
 M config.txt       ← M ở cột phải (có dấu cách trước): đã sửa nhưng CHƯA add
?? notes.txt        ← file mới chưa được theo dõi
R  a.java -> b.java ← đổi tên file, đã add
```

> 💡 Mẹo: chạy `git add -A` sẽ đưa mọi thay đổi ở cột phải sang cột trái (staged).

### G2. Lưu mốc trên MÁY (commit) — không lên mạng

```powershell
git add -A                          # chọn tất cả thay đổi
git commit -m "Mô tả ngắn việc vừa làm"
```
→ Tạo 1 **mốc phục hồi** trên máy bạn. Riêng tư, không ai khác thấy.

### G3. Đẩy lên GitHub (push) — chia sẻ cho cả team

```powershell
git push
```
⚠️ **Cẩn thận**: đây là đưa code **lên GitHub của tổ chức** (`ppacvn`), đồng nghiệp sẽ thấy và tải về. Một khi người khác đã tải, **rất khó rút lại**.
⚠️ **Trước khi push**: đảm bảo **không có mật khẩu** trong file sẽ lên git. Mật khẩu phải nằm ở `.env` (file này đã được git bỏ qua, không lên mạng).

### G4. Lấy code mới nhất của team về

```powershell
git pull
```

### G5. Quay về một mốc đã lưu (khi lỡ sửa hỏng)

```powershell
git log --oneline -10          # tìm mã mốc muốn quay về (vd feb8fe5)
git restore .                  # bỏ MỌI thay đổi chưa commit, về mốc gần nhất
# (muốn quay sâu hơn thì nhờ hỗ trợ, vì có thể mất thay đổi)
```

### G6. Quy trình thường ngày (gợi ý)

```powershell
# 1. Sửa code / chạy test...
# 2. Xem mình đã đổi gì
git status
# 3. Lưu mốc trên máy
git add -A
git commit -m "Thêm test login cho màn X"
# 4. Khi muốn chia sẻ cho team (đã chắc không có mật khẩu)
git push
```

> 💡 Bạn **không bắt buộc tự gõ git** — có thể nhờ trợ lý làm hộ (commit/push) và chỉ cần xác nhận.

---

## H. Cheat sheet

```powershell
# === WEB ===
mvn test -P web                                          # tất cả test web
mvn test -P web "-Dsuite.xml=testng/testng-m1-critical.xml"
mvn test -P web "-Dbrowser.headless=true"

# === APP === (cần: emulator bật + appium chạy)
emulator -avd Pixel7_API33          # cửa sổ 1
appium                              # cửa sổ 2 (để nguyên)
adb devices                        # kiểm tra thấy emulator-5554
mvn test -P mobile                                       # tất cả test app
mvn test -P mobile "-Dsuite.xml=testng/testng-login.xml"

# === GIT ===
git status
git add -A
git commit -m "Mô tả việc vừa làm"
git push                           # đẩy lên GitHub (cẩn thận: công khai cho team)
```
