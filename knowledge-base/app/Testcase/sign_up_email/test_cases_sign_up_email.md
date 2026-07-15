# PPAC Mobile — M10 Sign Up by Email (RBT)

> **Hệ thống:** PPAC Mobile (Android — `com.ppac.app.sandbox` v3.1.16)
> **Module:** M10 Authentication — Sign Up by Email flow
> **Tech stack:** Flutter (Jetpack Compose render) — locator qua `content-desc`
> **Phương pháp:** AI-RBT (Risk-Based Testing) — Coverage **Deep RBT**
> **Ngày tạo:** 2026-05-26
> **QA Owner:** harry.vo@ppac.co.uk
> **Source recon:** `plans/manual/ppac_mobile/sign_up_email_exploration.md`
> **Tổng số TC:** 78 (Critical 14 / High 36 / Medium 22 / Low 6)

---

## Phạm vi

**Trong scope (Deep RBT):**
- M10.1 Welcome screen — Terms toggle gate + SSO buttons + "Sign up with Email" entry
- M10.2 Sign up by email screen — Email input + client/server validation
- M10.3 Create password screen — Password rules real-time + Confirm match + Create account
- M10.4 Check your inbox (OTP) — 6-digit code entry + Validate + Resend + Edit my email
- M10.5 WELCOME TO onboarding intro
- M10.6 Selfie time! instructions (Step 1/3)
- M10.7 Logout from onboarding (early abandon)
- M10.8 E2E happy/negative flow

**Ngoài scope (deferred):**
- M10.9 Selfie Camera Live (cần real device — face detection) — flag-only
- M10.10 Documents upload (Step 2/3) — chưa explore
- M10.11 Skill card verification (Step 3/3) — chưa explore
- M10.12 Sign up with Google / Microsoft SSO

**Test design techniques áp dụng:**
- **Equivalence Partitioning (EP):** Email format, password chars
- **Boundary Value Analysis (BVA):** Password length 7/8/9 + 63/64/65, OTP digit count
- **Decision Table:** Password rules pass/fail × Confirm match
- **State Transition:** OTP timer / resend cooldown / app navigation
- **Pairwise:** Toggle ON/OFF × Email valid/invalid × SSO method

---

## Risk Hot-Spots

| Sub-module | Risk Level | Lý do | Test depth |
|---|---|---|---|
| M10.4 OTP (Verification code) | 🔴 HIGH | Security boundary, expire window 5min, brute-force vector | Decision Table + State Transition + Negative |
| M10.3 Create Password | 🔴 HIGH | Authentication strength, rule enforcement, compliance | Decision Table (5 rules × 2 states) + BVA |
| M10.2 Email validation | 🔴 HIGH | Server-side reject vs client format — UX ambiguity F-UX-NEW-1 | EP + Pairwise |
| M10.1 Terms toggle | 🟡 MEDIUM | Compliance gate, GDPR consent | EP + Negative |
| M10.7 Logout mid-flow | 🟡 MEDIUM | Data residue / partial account state | State + Cleanup |
| M10.5 Onboarding intro | 🟢 LOW | Display-only | Smoke |
| M10.6 Selfie instructions | 🟢 LOW | Display-only (Camera live = flag-only) | Smoke |

---

## Gap Analysis (cần escalate)

| Gap | Severity | Hành động |
|---|---|---|
| Selfie face detection cần real device — không test được trên emulator | High (coverage) | Recommend backend mock service OR test trên real device dedicated |
| Multi-Resend rate limit chưa biết (chỉ test 1 resend) | Medium | Verify với dev — bao nhiêu resend / phút |
| Partial signup cleanup khi user logout mid-onboarding | Medium | Verify backend behavior (orphan account?) |
| OTP brute-force protection chưa rõ (chỉ test 1 invalid) | High (security) | Pen test với multiple invalid → lockout? |
| Account đã tồn tại behavior (re-signup cùng email) | Medium | Test scenario riêng — cần cleanup mechanism |

---

## Test Data Convention

- Format email: `qa_signup_<module>_<TC_num>_<timestamp>@yopmail.com`
  - Ví dụ: `qa_signup_m10_010_1779768529@yopmail.com`
- Password test: `StrongP@ss<timestamp_short>` (đảm bảo unique cho parallel runs)
- OTP: retrieve từ yopmail web (HTTP fetch hoặc Playwright)

---

# Part 1 — M10.1 Welcome Screen (8 TC)

## A. Terms toggle gate (5 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M10_TC_001 | M10.1 | Critical | Default — Terms toggle OFF khi app launch fresh | App clean install, first launch | 1. Launch app<br>2. Observe Welcome screen Terms toggle | Toggle OFF (gray) + warning text "You must accept the Terms of Service and Privacy Policy to continue." (yellow) visible | Critical | clean install |
| PPAC_M10_TC_002 | M10.1 | High | Tap "Sign up with Email" KHI Terms toggle OFF — blocked | App ở Welcome, toggle OFF | 1. Tap "Sign up with Email" button | KHÔNG navigate forward. Warning text vẫn visible. Có thể có haptic feedback / shake animation (verify actual) | High | toggle OFF |
| PPAC_M10_TC_003 | M10.1 | High | Tap toggle ON → warning text disappear | Toggle OFF, warning visible | 1. Tap toggle (~190, 1395) | Toggle ON (green) + warning text disappear immediately | High | toggle interaction |
| PPAC_M10_TC_004 | M10.1 | High | Tap "Sign up with Email" SAU KHI Terms ON — navigate to Email screen | Toggle ON | 1. Tap "Sign up with Email" | Navigate to "Sign up by email" screen (Screen 2) | High | toggle ON |
| PPAC_M10_TC_005 | M10.1 | Medium | Toggle có giữ state sau khi navigate qua + back? | Toggle ON, navigate forward, back | 1. Toggle ON<br>2. Sign up with Email → Email screen<br>3. Tap back arrow<br>4. Quan sát toggle | Document actual: toggle vẫn ON hay reset OFF | Medium | state persistence |

## B. Welcome screen elements (3 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M10_TC_010 | M10.1 | Medium | 3 SSO buttons present đúng thứ tự | Welcome screen loaded | 1. Quan sát Welcome screen | Thứ tự từ trên xuống: Google → Microsoft → Email | Medium | layout |
| PPAC_M10_TC_011 | M10.1 | Low | Language selector "ENG" present + tap-able | Welcome screen | 1. Tap "ENG" dropdown top-left | Dropdown options xuất hiện (document languages available) | Low | language dropdown |
| PPAC_M10_TC_012 | M10.1 | Low | "Have an account? Log in" link navigates to Login flow | Welcome screen | 1. Tap "Log in" link | Navigate to Login screen (out of M10 scope, verify just transition) | Low | log in link |

---

# Part 2 — M10.2 Sign up by email Screen (12 TC)

## A. Email field validation (8 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M10_TC_020 | M10.2 | Critical | Empty email + Next → validation error | On Email screen, field empty | 1. Tap Next button without typing | Error text "Please enter a valid email" (red) appears + field border turns red | Critical | empty |
| PPAC_M10_TC_021 | M10.2 | High | Email không có '@' → validation error | On Email screen | 1. Type "notanemail"<br>2. Tap Next | Same error "Please enter a valid email" + red border | High | "notanemail" |
| PPAC_M10_TC_022 | M10.2 | High | Email không có domain (chỉ @) → reject | On Email screen | 1. Type "user@"<br>2. Tap Next | Same validation error | High | "user@" |
| PPAC_M10_TC_023 | M10.2 | High | Email không có TLD → reject | On Email screen | 1. Type "user@domain"<br>2. Tap Next | Document actual: client accept hay reject (lenient regex possible) | High | "user@domain" |
| PPAC_M10_TC_024 | M10.2 | Critical | Email valid format `@yopmail.com` → navigate to Password screen | On Email screen | 1. Type `qa_signup_<ts>@yopmail.com`<br>2. Tap Next | Navigate to "Create password" screen | Critical | valid yopmail |
| PPAC_M10_TC_025 | M10.2 | High | Email `@test.com` (server-side reject domain) → cùng error "Please enter a valid email" | On Email screen | 1. Type `qa_signup_<ts>@test.com`<br>2. Tap Next<br>3. Pass client validation<br>4. After Create account → server reject<br>5. App navigate back to email screen | Server reject + same error. **F-UX-NEW-1 finding:** error message không phân biệt client vs server fail | High | @test.com |
| PPAC_M10_TC_026 | M10.2 | High | Email với uppercase characters preserved | On Email screen | 1. Type `QA_SIGNUP_<ts>@Yopmail.COM`<br>2. Tap Next | Document: app normalize lowercase hay preserve case (verify backend treats same email) | High | uppercase variant |
| PPAC_M10_TC_027 | M10.2 | High | Email với SQLi/XSS payload | On Email screen | 1. Type `'; DROP TABLE users; --@test.com`<br>2. Tap Next<br>3. Type `<script>alert(1)</script>@x.com`<br>4. Tap Next | Reject với validation error. KHÔNG execute script. Backend log không break | High | injection payloads |

## B. Boundary email length (3 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M10_TC_030 | M10.2 | Medium | Email max length boundary (RFC 5321 = 254 chars) | On Email screen | 1. Type email 254 chars (e.g., 244 a's + `@y.com`)<br>2. Tap Next | Accept HOẶC reject với clear message. Document max | Medium | BVA 254 |
| PPAC_M10_TC_031 | M10.2 | Medium | Email > 254 chars → reject | On Email screen | 1. Type email 300 chars<br>2. Tap Next | Reject với "too long" hoặc same error | Medium | BVA 300 |
| PPAC_M10_TC_032 | M10.2 | Medium | Email min length (3 chars `a@b`) → behavior | On Email screen | 1. Type "a@b"<br>2. Tap Next | Document: accept (lenient) hoặc reject (strict TLD) | Medium | BVA min |

## C. Auxiliary (1 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M10_TC_040 | M10.2 | Medium | Back arrow → return to Welcome với toggle state preserved | On Email screen từ Welcome (toggle ON) | 1. Tap back arrow | Return to Welcome screen, Terms toggle vẫn ON (state preserved) | Medium | back navigation |

---

# Part 3 — M10.3 Create Password Screen (18 TC)

## A. Password rules real-time validation — Decision Table (12 TC)

**Decision Table — 5 rules:**

| # | 8+ chars | Upper | Lower | Number | Special | Expected (5 rules) |
|---|---|---|---|---|---|---|
| TC_050 | ❌ (3 chars "abc") | ❌ | ✅ | ❌ | ❌ | 1/5 green check |
| TC_051 | ❌ (7 chars "abcDEF1") | ✅ | ✅ | ✅ | ❌ | 3/5 (4 if 7 chars counted partial) |
| TC_052 | ✅ (8 chars "abcdefgh") | ❌ | ✅ | ❌ | ❌ | 2/5 |
| TC_053 | ✅ ("ABCDEFGH") | ✅ | ❌ | ❌ | ❌ | 2/5 |
| TC_054 | ✅ ("12345678") | ❌ | ❌ | ✅ | ❌ | 2/5 |
| TC_055 | ✅ ("Aa1@xxxx") | ✅ | ✅ | ✅ | ✅ | 5/5 — full satisfy |
| TC_056 | ✅ ("StrongP@ss123") | ✅ | ✅ | ✅ | ✅ | 5/5 |
| TC_057 | empty "" | ❌ | ❌ | ❌ | ❌ | All gray ⊘ |

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M10_TC_050 | M10.3 | Medium | Pwd 3 chars lowercase "abc" → 1/5 rule green | On Create password screen | 1. Type "abc" in Password | Chỉ rule "lowercase letter (a-z)" ✅ green; 4 rules còn lại ⊘ gray | Medium | "abc" |
| PPAC_M10_TC_051 | M10.3 | High | Pwd 7 chars (BVA min-1) "abcDEF1" → "at least 8" fails | On Create password screen | 1. Type "abcDEF1" | Rule "at least 8 characters" ⊘ gray; rules upper/lower/number ✅ green; special ⊘ gray | High | BVA 7 |
| PPAC_M10_TC_052 | M10.3 | High | Pwd 8 chars all lowercase → "uppercase" rule ⊘ | Same | 1. Type "abcdefgh" | Rules 8+chars ✅, lower ✅; upper/number/special ⊘ | High | "abcdefgh" |
| PPAC_M10_TC_053 | M10.3 | Medium | Pwd 8 chars all uppercase | Same | 1. Type "ABCDEFGH" | Rules 8+chars ✅, upper ✅; lower/number/special ⊘ | Medium | "ABCDEFGH" |
| PPAC_M10_TC_054 | M10.3 | Medium | Pwd 8 chars all digits | Same | 1. Type "12345678" | Rules 8+chars ✅, number ✅; upper/lower/special ⊘ | Medium | "12345678" |
| PPAC_M10_TC_055 | M10.3 | Critical | Pwd 8 chars BVA min satisfy ALL 5 rules → 5/5 green | Same | 1. Type "Aa1@xxxx" | All 5 rules ✅ green; field border green (success state) | Critical | "Aa1@xxxx" |
| PPAC_M10_TC_056 | M10.3 | Critical | Pwd strong typical "StrongP@ss123" → 5/5 green | Same | 1. Type "StrongP@ss123" | All 5 ✅ green | Critical | "StrongP@ss123" |
| PPAC_M10_TC_057 | M10.3 | High | Pwd clear (delete all) → rules reset ⊘ | Pwd typed valid, all green | 1. Clear field | All 5 rules back to ⊘ gray (real-time clear) | High | clear |
| PPAC_M10_TC_058 | M10.3 | Medium | Pwd với Unicode (Vietnamese chars) "Mật@khẩu1" → behavior | Same | 1. Type Vietnamese pwd với dấu | Document: rules count Vietnamese as lowercase/uppercase? Allow or reject? | Medium | Unicode |
| PPAC_M10_TC_059 | M10.3 | High | Pwd với only special chars passing 4/5 (missing length) "@#$%" | Same | 1. Type "@#$%" | Rules special ✅, length ⊘, others ⊘ | High | special only |
| PPAC_M10_TC_060 | M10.3 | High | Pwd 64+ chars BVA max | Same | 1. Type 64-char password "Aa1@" × 16 | All 5 rules ✅ (assuming no max limit). Document if app caps | High | BVA 64 |
| PPAC_M10_TC_061 | M10.3 | Medium | Pwd 256 chars (extreme) — test no overflow | Same | 1. Type 256-char password | App KHÔNG crash. Document max accepted len. Server reject với clear error if over limit | Medium | BVA 256 |

## B. Confirm password match (4 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M10_TC_070 | M10.3 | Critical | Confirm matches Password → field border GREEN | Pwd "StrongP@ss123" typed | 1. Tap Confirm field<br>2. Type "StrongP@ss123" identical | Confirm field border green; no error text | Critical | match |
| PPAC_M10_TC_071 | M10.3 | High | Confirm khác Password → field border ? + error | Pwd "StrongP@ss123" typed | 1. Confirm = "WrongP@ss123" | Document actual: Confirm field border state (red?) + error text (e.g., "Passwords don't match") | High | mismatch |
| PPAC_M10_TC_072 | M10.3 | High | Confirm match but with trailing space "StrongP@ss123 " | Pwd "StrongP@ss123" | 1. Confirm = "StrongP@ss123 " (with space) | Document: trim or treat as mismatch | High | trailing space |
| PPAC_M10_TC_073 | M10.3 | High | Eye icon toggle reveal password text | Confirm has password | 1. Tap eye icon trên Confirm field | Password text becomes visible (`•••` → "StrongP@ss123"). Tap again → masked | High | show/hide toggle |

## C. Create account submit (2 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M10_TC_080 | M10.3 | Critical | Create account với pwd 5/5 + matching Confirm → navigate to OTP screen | All rules green, Confirm match | 1. Tap Create account | Navigate to "Check your inbox" (Screen 4). Backend send email với 6-digit OTP | Critical | happy submit |
| PPAC_M10_TC_081 | M10.3 | High | Create account với pwd KHÔNG đủ 5/5 → button disabled hoặc reject | Some rules ⊘ gray | 1. Tap Create account | Document: button disabled (gray) hay click reject với error | High | weak pwd submit |

---

# Part 4 — M10.4 Check Your Inbox (OTP) Screen (18 TC)

## A. OTP entry (8 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M10_TC_090 | M10.4 | Critical | OTP entry — 6 boxes accept 1 digit each + auto-advance | On OTP screen | 1. Tap first box<br>2. Type "1" | Digit "1" hiển thị box 1, cursor auto-advance to box 2 | Critical | first digit |
| PPAC_M10_TC_091 | M10.4 | Critical | OTP đầy đủ valid → Validate → navigate to WELCOME TO | On OTP, fresh OTP retrieved | 1. Type 6-digit OTP from email<br>2. Tap Validate | Navigate to "WELCOME TO" onboarding intro (Screen 5) | Critical | valid OTP |
| PPAC_M10_TC_092 | M10.4 | High | OTP invalid (wrong 6 digits) → error | On OTP screen | 1. Type "000000" hoặc random wrong code<br>2. Tap Validate | Error text "Invalid Code!" (red) + 6 box borders turn red | High | wrong code |
| PPAC_M10_TC_093 | M10.4 | High | OTP expired (> 5min) → error | OTP received, wait > 5min trước Validate | 1. Type expired OTP<br>2. Tap Validate | Same "Invalid Code!" hoặc clearer "Code expired". Document actual | High | expired |
| PPAC_M10_TC_094 | M10.4 | High | OTP partial (chỉ 5 digits) + Validate → behavior | On OTP screen | 1. Type only 5 digits<br>2. Tap Validate | Document: button disabled / Validate ignored / Show "Complete the code" | High | partial 5 |
| PPAC_M10_TC_095 | M10.4 | High | OTP với non-digit chars "abcdef" → blocked | On OTP screen | 1. Try typing "abcdef" | Boxes reject letters HOẶC document accept (UX issue) | High | non-digit |
| PPAC_M10_TC_096 | M10.4 | Medium | Paste OTP từ clipboard (6 digits) → all boxes fill | OTP copied to clipboard | 1. Long-press first box<br>2. Tap Paste | All 6 boxes fill correctly | Medium | paste 6 |
| PPAC_M10_TC_097 | M10.4 | Medium | Backspace từ last box → cursor về box trước | Boxes 1-6 filled "123456" | 1. Tap last box<br>2. Press Backspace | Last box clear, cursor on box 5 | Medium | backspace nav |

## B. Resend behavior (5 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M10_TC_100 | M10.4 | High | Resend disabled trong cooldown 52s | OTP just sent | 1. Quan sát "Resend after 52s" text<br>2. Try tap | Resend disabled (countdown active). Document: link inactive hoặc visually faded | High | cooldown |
| PPAC_M10_TC_101 | M10.4 | High | Resend active sau cooldown đến 0s | Wait cooldown done | 1. Quan sát "Resend" link | Text changes from "Resend after Xs" to clickable "Resend" link (underlined green) | High | cooldown done |
| PPAC_M10_TC_102 | M10.4 | High | Tap Resend → new email arrives với fresh OTP | Resend active | 1. Tap Resend | New email noreply@ppac.co.uk arrives với new 6-digit OTP. Old OTP còn valid hay invalidated? Document | High | resend |
| PPAC_M10_TC_103 | M10.4 | Critical | Old OTP after Resend → invalid (đảm bảo security) | After Resend, get new OTP | 1. Try old OTP first | Old OTP rejected "Invalid Code!" (only newest valid) | Critical | security old OTP |
| PPAC_M10_TC_104 | M10.4 | Medium | Multi-Resend rate limit | After 1st resend | 1. Wait + Resend × 5 trong 5 phút | Document: bao nhiêu lần allowed / minute. Backend protection from email spam | Medium | rate limit |

## C. Edit my email round-trip (3 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M10_TC_110 | M10.4 | High | Tap "Edit my email" → back to Email screen với email PRE-FILLED | On OTP screen | 1. Tap "Edit my email" link | Navigate back to Email screen (Screen 2), email field filled với current email | High | edit nav |
| PPAC_M10_TC_111 | M10.4 | Medium | Edit email + Next → new OTP screen với new email | After Edit nav back | 1. Modify email<br>2. Tap Next | OTP screen reload với new email displayed; new OTP sent to new address | Medium | edit + resubmit |
| PPAC_M10_TC_112 | M10.4 | Medium | Edit email + Next với SAME email → behavior | Edit but không thay đổi email | 1. Tap Next without changing | Document: re-send OTP hay use existing | Medium | same email |

## D. Brute-force protection (2 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M10_TC_120 | M10.4 | Critical | Multiple invalid OTP attempts → lockout/throttle | OTP screen | 1. Try wrong OTP × 5-10 lần liên tiếp | Document: account locked, IP throttled, cooldown forced. Failing here = security vulnerability | Critical | brute force |
| PPAC_M10_TC_121 | M10.4 | High | OTP attempt counter UI (e.g., "2 attempts left") | After invalid attempt | 1. Submit wrong OTP<br>2. Quan sát UI | Document if UI shows attempts remaining (better UX) | High | UX feedback |

---

# Part 5 — M10.5 WELCOME TO Onboarding Intro (4 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M10_TC_130 | M10.5 | High | WELCOME TO screen displayed sau OTP valid | Just validated OTP | 1. Verify on WELCOME TO screen | Title "WELCOME TO" + "Secure Identity Verification" + "Quick & Accurate" + 3 numbered steps + "Let's start" button visible | High | post-OTP |
| PPAC_M10_TC_131 | M10.5 | Medium | 3 steps đúng thứ tự + nội dung | Same | 1. Read 3 steps | "1. Take live photos for verification" / "2. Provide your Identity or Right-to-work documents" / "3. Verify your skill card" | Medium | content |
| PPAC_M10_TC_132 | M10.5 | High | Tap "Let's start" → navigate to Selfie time! | Same | 1. Tap "Let's start" | Navigate to Screen 6 "Selfie time!" | High | navigate |
| PPAC_M10_TC_133 | M10.5 | Low | No back option (cannot return to OTP) | Same | 1. Quan sát top-left | No back arrow visible. State final after OTP commit | Low | terminal |

---

# Part 6 — M10.6 Selfie time! Instructions (5 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M10_TC_140 | M10.6 | Medium | Selfie instructions screen với 3 prerequisites + 2 example images | On Selfie time screen | 1. Read content | Title "Selfie time!" + description + "Get ready before we take your photo." + 3 bullets (Good lighting / No safety gear / Camera at eye level) + 2 example images (❌ bad / ✅ good) + "Open Camera" button | Medium | display |
| PPAC_M10_TC_141 | M10.6 | High | Tap "Open Camera" → camera permission flow OR live camera | Same | 1. Tap "Open Camera" | Camera permission requested first time OR direct navigate to Live camera (if granted). Document permission state | High | permission |
| PPAC_M10_TC_142 | M10.6 | High | Tap back arrow → return to WELCOME TO | On Selfie time, has back arrow | 1. Tap back | Return to WELCOME TO screen | High | back nav |
| PPAC_M10_TC_143 | M10.6 | High | Tap top-right logout icon → Confirm Log Out dialog | Same | 1. Tap top-right logout icon | Modal dialog "Confirm Log Out" + 2 buttons (Log Out / Cancel) | High | logout dialog |
| PPAC_M10_TC_144 | M10.6 | **[FLAG-ONLY]** | Selfie Camera Live verification — cần real face | Real device | 1. Open Camera<br>2. Center face | Auto-capture khi face detected. Backend Regula verify. Document outcome paths: success / retry / fail | Critical (flag) | real face — emulator gap |

---

# Part 7 — M10.7 Logout Confirm Dialog (4 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M10_TC_150 | M10.7 | High | Confirm Log Out dialog đầy đủ elements | Logout icon tapped | 1. Quan sát dialog | Title "Confirm Log Out" + warning text "You'll have to log back in next time you use the app. Make sure you've saved or remembered your login information." + 2 buttons | High | dialog content |
| PPAC_M10_TC_151 | M10.7 | High | Cancel button → dismiss dialog, stay on current screen | Dialog visible | 1. Tap Cancel | Dialog dismissed, return to underlying onboarding screen, state preserved | High | cancel |
| PPAC_M10_TC_152 | M10.7 | Critical | Log Out button → kill session, return to Welcome screen | Dialog visible | 1. Tap Log Out | Session cleared (token deleted). Navigate to Welcome screen (fresh state, Terms toggle OFF) | Critical | logout commit |
| PPAC_M10_TC_153 | M10.7 | High | Logout từ mid-signup → partial account state | Just created account, mid-onboarding (before Selfie complete) | 1. Logout<br>2. Try login với cùng credentials | Document: account complete (can login) hoặc partial (must re-do onboarding) hoặc deleted | High | partial cleanup |

---

# Part 8 — M10.8 E2E Happy & Negative Flows (9 TC)

| TC ID | Module | Risk | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
|---|---|---|---|---|---|---|---|---|
| PPAC_M10_TC_160 | M10.8 | Critical | E2E HAPPY — sign up complete tới WELCOME TO | Fresh `qa_signup_<ts>@yopmail.com` | 1. Welcome — toggle ON, Sign up Email<br>2. Email — enter `qa_signup_<ts>@yopmail.com`, Next<br>3. Password — "StrongP@ss123" × 2, Create account<br>4. Inbox — fetch OTP từ yopmail, type 6 digits, Validate<br>5. WELCOME TO appears | Mọi step navigate đúng. Final screen WELCOME TO với "Let's start" button visible | Critical | full happy |
| PPAC_M10_TC_161 | M10.8 | Critical | E2E NEGATIVE — Terms OFF blocks signup ngay từ Welcome | Fresh launch | 1. Welcome (toggle OFF)<br>2. Tap Sign up Email | Stay on Welcome (warning text). KHÔNG navigate forward | Critical | block at gate |
| PPAC_M10_TC_162 | M10.8 | High | E2E NEGATIVE — email server reject blocks at email step | Toggle ON | 1. Welcome → Sign up Email<br>2. Email = `qa_signup_<ts>@test.com`, Next<br>3. Pass client, navigate to Password<br>4. Password valid, Create account<br>5. Server reject → navigate back to Email with error | F-UX-NEW-1: Same generic error displayed. User stuck without knowing reason | High | server reject |
| PPAC_M10_TC_163 | M10.8 | High | E2E NEGATIVE — wrong password match blocks at Create | Toggle ON | 1. Welcome → Email → Password<br>2. Password = "StrongP@ss123"<br>3. Confirm = "WrongP@ss123" mismatch<br>4. Tap Create account | Error "Passwords don't match" (or actual). KHÔNG navigate to OTP | High | password mismatch |
| PPAC_M10_TC_164 | M10.8 | Critical | E2E NEGATIVE — invalid OTP blocks at Validate | Pass to OTP step | 1. Type wrong 6 digits<br>2. Validate | "Invalid Code!" error. Stay on OTP screen | Critical | wrong OTP |
| PPAC_M10_TC_165 | M10.8 | High | E2E PARTIAL — abandon at OTP (Edit my email back) | On OTP screen | 1. Tap Edit my email<br>2. Modify email<br>3. Next → new OTP | New email gets new OTP. Old email account state? Document | High | abandon + retry |
| PPAC_M10_TC_166 | M10.8 | High | E2E PARTIAL — abandon at WELCOME TO (logout) | Reach WELCOME TO | 1. Tap logout top-right<br>2. Confirm Log Out | Session cleared. Login lại với same email → resume from where (Welcome / Login / Onboarding)? Document | High | abandon onboarding |
| PPAC_M10_TC_167 | M10.8 | Medium | E2E EDGE — re-signup với email đã exist | After successful signup of email X | 1. Logout<br>2. Try sign up lại với same email X | Server reject. Document error message (e.g., "Account already exists. Please log in") | Medium | duplicate signup |
| PPAC_M10_TC_168 | M10.8 | Medium | E2E EDGE — slow network during Create account | Network throttled | 1. Slow 3G<br>2. Submit Create account | Loading indicator. Eventually timeout or success. Document UX behavior | Medium | slow network |

---

# Recap & Next Steps

## Tổng kết bộ TC

| Part | Module | #TC | Critical | High | Medium | Low |
|---|---|---|---|---|---|---|
| 1 | M10.1 Welcome | 8 | 1 | 4 | 2 | 1 |
| 2 | M10.2 Email | 12 | 2 | 6 | 4 | 0 |
| 3 | M10.3 Password | 18 | 3 | 8 | 7 | 0 |
| 4 | M10.4 OTP | 18 | 3 | 11 | 4 | 0 |
| 5 | M10.5 Onboarding intro | 4 | 0 | 2 | 1 | 1 |
| 6 | M10.6 Selfie instructions | 5 | 1 | 3 | 1 | 0 |
| 7 | M10.7 Logout | 4 | 1 | 3 | 0 | 0 |
| 8 | M10.8 E2E | 9 | 3 | 5 | 1 | 0 |
| **TOTAL** | | **78** | **14** | **42** | **20** | **2** |

> **Lưu ý:** Trong M10.6 có 1 TC `[FLAG-ONLY]` (PPAC_M10_TC_144 Selfie Camera Live) — không test được trên emulator, cần real device + backend Regula mock.

## Khuyến nghị thứ tự execute

1. **Smoke pass:** 14 Critical TCs — verify happy paths + security boundaries
2. **Regression pass 1:** 42 High TCs — chia theo screen
3. **Coverage pass:** 22 Medium + Low TCs
4. **Real device pass:** Selfie + Documents + Skill card flow (M10.9-10.11 — out of current scope)
5. **Security pen test:** TC_120 (OTP brute force) + TC_103 (old OTP after resend) + TC_027 (injection)

## Findings có thể phát sinh trong execute

- **F-UX-NEW-1** (TC_025, TC_162): Email error message không phân biệt client vs server validation → fix bằng cách differentiate
- **F-SEC-NEW-1** (TC_120): OTP brute force có thể không có lockout — security gap
- **F-SEC-NEW-2** (TC_103): Old OTP có thể vẫn valid sau Resend — token rotation bug
- **F-COMP-NEW-1** (TC_153): Partial account cleanup chưa rõ — GDPR risk khi user abandon
- **F-INFRA-NEW-1** (TC_144): Selfie verification không testable trên emulator — cần backend mock service hoặc real device farm

## Files liên quan

- Source recon: `plans/manual/ppac_mobile/sign_up_email_exploration.md`
- Mobile rules: `.claude/rules/appium_rules.md`
- Mobile project: `ppac-mobile-automation/`
