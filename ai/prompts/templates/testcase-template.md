---
name: testcase-template
description: Format chuẩn test case — Bảng markdown (cột đầu TC ID), tương thích execution/scripts/export_testcases_to_excel.ps1
---

> File test cases lưu tại `knowledge-base/{app|web}/Testcase/test_cases_<feature>.md` (snake_case).
> Format chuẩn là **Bảng** — khớp các file thật trong `knowledge-base/app/Testcase/`.

## Header file (đầu file — blockquote metadata)

```markdown
# Test Cases — <Tên Feature>

> App: **PPAC Audit / Web** — <nền tảng>
> Phạm vi: <mô tả scope>
> Nguồn requirements: <đường dẫn> · Nguồn design: <Figma URL nếu có>
> Đánh số: <quy ước chuỗi TC theo module — VD SIGNUP / LOGIN / SUBM>
> Ngày sinh: YYYY-MM-DD · QA Owner: <tên> · Tổng số TC: <n>
```

## Format TC — Bảng (mỗi TC = 1 dòng)

Nhóm TC theo heading `## Feature N: <tên>`, mỗi nhóm một bảng:

```markdown
## Feature 1: Đăng nhập

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority | Status |
|---|---|---|---|---|---|---|---|---|---|
| PPAC_LOGIN_TC_001 | Login | High | Đăng nhập với thông tin hợp lệ | Đã có tài khoản active | 1. Mở trang Login<br>2. Nhập email + password<br>3. Nhấn Đăng nhập | Email: auto_login_20260611_A3F2@test.com<br>Password: ValidPass@123 | 1. Form Login hiển thị<br>3. Điều hướng tới Dashboard | High |  |
```

## Quy tắc (để `export_testcases_to_excel.ps1` parse không vỡ)

- **Cột đầu PHẢI là `TC ID`** — parser nhận diện bảng qua cột này. ID viết HOA, chỉ chữ/số/`_`: `PPAC_<MODULE>_TC_<SỐ 3 chữ số>`
- **Bộ cột hợp lệ** (parser chỉ nhận đúng các bộ này — KHÔNG thêm cột lạ):
  - `TC ID, Module, Risk Level, Test Scenario, Pre-Condition, Test Steps, Test Data, Expected Result, Priority, Status`
  - Bộ không có `Risk Level` cũng hợp lệ; cột `Status` luôn đặt **cuối cùng**, có thể bỏ trống khi test case chưa thực thi
- **Cột `Status`** — chỉ nhận `Passed` hoặc `Failed`. Khi export ra Excel: ô Status có **dropdown click chọn Passed/Failed** và **tự tô màu** (Passed → xanh, Failed → đỏ). Để trống nếu chưa chạy test.
- Field nhiều dòng (Test Steps, Test Data, Expected Result): xuống dòng trong ô bằng `<br>`, đánh số `1.` `2.`; **Expected Result đánh số tương ứng bước**
- Mỗi TC một dòng — KHÔNG gộp, KHÔNG bỏ sót
- **Figma Ref** (nếu sinh từ design): ghi ở blockquote dưới heading Feature (`> Figma: <node-id>`), KHÔNG thêm cột riêng
- Test Data cụ thể, unique + traceable (`auto_<test>_<timestamp>_<random>@test.com`) — không placeholder
