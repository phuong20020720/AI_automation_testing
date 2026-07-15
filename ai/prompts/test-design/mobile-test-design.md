---
name: mobile-test-design
description: Thiết kế test cases đặc thù mobile — device matrix, interrupt, lifecycle, permission, offline
inputs: Feature/màn hình cần test + nền tảng (Android/iOS/cả hai)
references: templates/testcase-template.md, ai/rules/appium_rules.md (mục 5), config/mobile/
---

> Prompt này BỔ SUNG cho TC functional (sinh bằng `test-design/generate-testcases.md`) — chỉ tập trung khía cạnh đặc thù mobile, không lặp lại functional coverage.

Thiết kế mobile-specific test cases cho: `{{feature}}`

Bao phủ theo nhóm (chọn nhóm áp dụng cho feature — không phải feature nào cũng cần đủ):

| Nhóm | Kiểm tra |
|---|---|
| 📱 Device/OS matrix | Đề xuất matrix từ `config/mobile/` + thị phần user: OS min được hỗ trợ, màn hình nhỏ/lớn, notch — ghi rõ căn cứ chọn |
| 🔄 App lifecycle | Background lúc đang nhập liệu → quay lại: data còn? Kill app giữa flow → mở lại: trạng thái đúng? Update app: data cũ migrate? |
| 📞 Interrupt | Cuộc gọi đến giữa flow · notification chen ngang · alarm — quay lại app đúng chỗ, không mất data? |
| 📶 Network | Mất mạng giữa thao tác submit: báo lỗi rõ + không mất data + không duplicate khi retry? Chuyển WiFi↔4G? Mạng chậm: có loading state, không treo? |
| 🔐 Permission | Từ chối permission lần đầu: app xử lý gracefully? Thu hồi permission từ Settings khi app chạy? Hướng dẫn user bật lại? |
| 🔃 Orientation | Xoay màn hình giữa nhập liệu: layout đúng + data còn? (chỉ khi app hỗ trợ landscape) |
| 💾 Tài nguyên | Pin yếu/battery saver · bộ nhớ đầy khi lưu file · font size hệ thống lớn: layout không vỡ? |
| 📴 Offline (nếu app hỗ trợ) | Thao tác offline được queue? Sync khi có mạng lại: đúng thứ tự, không duplicate? Conflict resolution? |

Quy tắc:
- Format Bảng theo `templates/testcase-template.md` — TC ID `PPAC_<MODULE>_TC_<SỐ>` nối tiếp dải số TC functional của module (không tạo dải riêng)
- Pre-Condition ghi rõ trạng thái thiết bị (mạng, permission, pin...)
- Ghi chú TC nào automate được bằng Appium (lifecycle, network qua driver command — xem `ai/rules/appium_rules.md` mục 5) vs manual-only (cuộc gọi thật, pin)
- Output Tiếng Việt, gộp vào file TC của feature: `knowledge-base/app/Testcase/test_cases_<feature>.md` dưới heading `## Feature N: Mobile-specific`
