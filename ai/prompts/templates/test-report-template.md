---
name: test-report-template
description: Format chuẩn cho Test Report (sprint/release) — lưu tại artifacts/test-results/
---

# Test Report — <Sprint X / Release vX.X>

| | |
|---|---|
| **Kỳ báo cáo** | <từ ngày — đến ngày> |
| **Scope** | <features/modules trong scope> |
| **Môi trường** | <env, build> |
| **Người lập** | <tên> |

## 1. Tóm tắt (Executive Summary)
<3-5 dòng: kết quả chung, rủi ro lớn nhất, khuyến nghị. Người đọc bận chỉ đọc mục này.>

**Trạng thái:** ✅ Đạt / ⚠️ Đạt có điều kiện / ❌ Không đạt

## 2. Kết quả Test Execution

| Loại | Planned | Executed | Pass | Fail | Blocked | Pass rate |
|---|---|---|---|---|---|---|
| Manual | | | | | | |
| Automation Web | | | | | | |
| Automation Mobile | | | | | | |
| API (newman) | | | | | | |

Link reports: `artifacts/reports/<...>`

## 3. Kết quả theo Module

| Module | TC | Pass | Fail | Ghi chú |
|---|---|---|---|---|

## 4. Defects

| | Critical | Major | Minor | Trivial |
|---|---|---|---|---|
| Mới | | | | |
| Đóng | | | | |
| Còn mở | | | | |

**Bug nổi bật / chặn release:** <danh sách + trạng thái>

## 5. Khu vực KHÔNG test / test mỏng
<Liệt kê trung thực + lý do + rủi ro>

## 6. Đề xuất
1. <tối đa 3 mục, cụ thể, có owner gợi ý>
