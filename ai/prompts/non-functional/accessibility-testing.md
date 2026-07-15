---
name: accessibility-testing
description: Thiết kế accessibility test cases theo WCAG 2.1 AA — web + mobile (TalkBack/VoiceOver)
inputs: Phạm vi (màn hình/flow) + nền tảng (web/mobile)
references: templates/testcase-template.md, ai/rules/locator_strategy.md (semantic attributes)
---

Thiết kế accessibility test cases cho: `{{scope}}` — chuẩn WCAG 2.1 mức AA

Bao phủ theo nhóm:

| Nhóm | Kiểm tra | WCAG |
|---|---|---|
| 🏷️ Label & semantic | Mọi input có label gắn đúng? Button/icon có accessible name? Ảnh có ý nghĩa có alt text? (web: aria/label · mobile: content-desc/accessibilityLabel) | 1.1.1, 4.1.2 |
| ⌨️ Keyboard (web) | Toàn bộ flow thao tác được chỉ bằng bàn phím? Focus order theo thứ tự đọc? Focus indicator nhìn thấy? Không focus trap? | 2.1.1, 2.4.3, 2.4.7 |
| 🗣️ Screen reader | TalkBack (Android) / VoiceOver (iOS) / NVDA (web): đọc đúng tên + vai trò + trạng thái element? Thông báo lỗi validation được đọc lên? | 4.1.3 |
| 🎨 Contrast & hiển thị | Text contrast ≥ 4.5:1 (text thường) / 3:1 (text lớn)? Thông tin không truyền đạt CHỈ bằng màu? Phóng to 200% không vỡ layout/mất nội dung? | 1.4.3, 1.4.1, 1.4.4 |
| 📝 Form & lỗi | Error message gắn với field lỗi? Hướng dẫn sửa rõ ràng? Field bắt buộc được đánh dấu cho cả screen reader? | 3.3.1, 3.3.2 |
| 📱 Mobile riêng | Touch target ≥ 44×44pt? Hỗ trợ font size hệ thống? Xoay màn hình không mất chức năng? | 2.5.5 |

Quy tắc:
- Format Bảng theo `templates/testcase-template.md` — TC ID: `PPAC_A11Y_<MODULE>_TC_<SỐ>`
- Test Steps ghi rõ công cụ kiểm tra (axe DevTools cho contrast/aria scan tự động · TalkBack/VoiceOver thao tác tay) — scan tự động chỉ bắt ~30-40% vấn đề, các TC screen reader/keyboard là manual
- Mỗi TC trace về tiêu chí WCAG (ghi trong Test Scenario)
- Lưu ý chéo: element thiếu accessible name cũng là element khó automation (xem `ai/rules/locator_strategy.md`) — phát hiện thì báo cả 2 mục đích
- Output Tiếng Việt, lưu `knowledge-base/{app|web}/Testcase/test_cases_a11y_<module>.md`
