---
description: Sinh manual test cases nhanh từ requirements (QUICK mode — không qua quy trình 6 bước).
skills:
  - rbt_manual_testing
---

> **BẮT BUỘC (MANDATORY SKILL):** Bạn PHẢI nạp và đọc kỹ nội dung của skill **`rbt_manual_testing`** (nạp qua công cụ Skill — tự resolve theo tên) trước khi bắt đầu thực hiện tác vụ này. Sử dụng **Mode QUICK** của skill.

# Workflow: Sinh Manual Test Cases Nhanh từ Requirements

Workflow này sử dụng **Mode QUICK** của skill `rbt_manual_testing` để sinh test cases nhanh từ requirements đã sẵn có.

## ⚠️ Nguyên tắc

- **Mode:** QUICK (1 lượt duy nhất, không chờ user giữa chừng)
- Phù hợp cho module đơn giản, requirements đã rõ ràng
- Nếu phát hiện requirements quá phức tạp hoặc mơ hồ → **tự động chuyển sang FULL RBT** và thông báo user
- Tất cả output bằng **Tiếng Việt**

## Các bước thực hiện

1. **Đọc và hiểu requirements** được user cung cấp
2. **Xác định các luồng chính:** Happy Path, Negative Path, Boundary Cases
3. **Áp dụng kỹ thuật thiết kế test case tự động:**
   - Equivalence Partitioning (EP)
   - Boundary Value Analysis (BVA)
   - Decision Table (nếu có nhiều rules)
   - State Transition (nếu có workflow)
4. **Sinh test cases đầy đủ fields:**
   - TC ID (format: `[DỰ_ÁN]_[MODULE]_TC_[SỐ]`)
   - Module
   - Test Scenario / Test Case Title
   - Pre-conditions
   - Test Steps (đánh số)
   - Expected Results (đánh số tương ứng)
   - Test Data (**phải cụ thể**, không placeholder)
   - Priority (Critical / High / Medium / Low)
5. **Xuất ra format Bảng (Table) chuẩn**

## Output Format — Bảng (Table)

Mỗi test case là **một dòng** trong bảng markdown. Cột đầu tiên PHẢI tên `TC ID`.

```markdown
| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|---|
| PPAC_LOGIN_TC_001 | Login | High | Đăng nhập với thông tin hợp lệ | Đã có tài khoản active | 1. Mở trang Login<br>2. Nhập email + password hợp lệ<br>3. Nhấn Đăng nhập | auto_login_20260602_A3F2@test.com | Điều hướng tới Dashboard | High |
```

Quy tắc:
- Cột đầu tiên PHẢI là `TC ID` (ID viết HOA, chỉ chữ/số/`_`).
- Thứ tự cột chuẩn: `TC ID`, `Module`, `Risk Level` (nếu có), `Test Scenario`, `Pre-Condition`, `Test Steps`, `Test Data`, `Expected Result`, `Priority`.
- Field nhiều dòng (Test Steps, Expected Result): các bước/ý xuống dòng trong ô bằng `<br>`, đánh số `1.` `2.` `3.`.
- Mỗi TC một dòng — KHÔNG gộp, KHÔNG bỏ sót TC nào.
- Nếu một màn/nhóm có nhiều TC → tách thành nhiều bảng con dưới các heading mô tả.
- Tương thích `scripts/export_testcases_to_excel.ps1` (table parser nhận diện bảng qua cột `TC ID`).

## Quy tắc quan trọng

- Test Data phải cụ thể: `test_login_01@domain.com`, không phải "email hợp lệ"
- Phải bao gồm cả Positive, Negative, và Boundary cases
- TC ID theo format thống nhất do user quy ước hoặc mặc định `[DỰ_ÁN]_[MODULE]_TC_[SỐ]`
- Nếu quá nhiều TCs → chia thành Part 1, Part 2 và hỏi user

## Khi nào chuyển sang FULL RBT

Agent **tự động đề xuất chuyển mode** nếu phát hiện:
- Requirements mơ hồ, cần hỏi Q&A
- Scope lớn (>3 modules)
- Logic nghiệp vụ phức tạp, nhiều điều kiện chồng chéo
- User yêu cầu Traceability Matrix hoặc Risk Assessment