---
name: sprint-report
description: Tổng hợp báo cáo QA cuối sprint từ test results + bug tracker
inputs: Sprint number/dates + nguồn data (artifacts/reports/, artifacts/test-results/, danh sách bugs)
references: templates/test-report-template.md, reporting/defect-summary.md
---

Tổng hợp báo cáo QA Sprint `{{sprint}}`:

Nguồn data: đọc `artifacts/reports/` (allure, newman) + `artifacts/test-results/` + thông tin user cung cấp. Số liệu KHÔNG có trong nguồn → ghi "N/A", không bịa.

Output (theo khung `templates/test-report-template.md`):

1. **Tóm tắt 3 dòng** — sprint đạt mục tiêu QA chưa, rủi ro lớn nhất, khuyến nghị
2. **Test execution:** planned/executed/pass/fail/blocked (manual + automation tách riêng), pass rate, so sánh sprint trước nếu có data
3. **Defects:** mới/đóng/còn mở theo severity, bug nổi bật
4. **Automation:** test mới thêm, flaky đã fix, coverage thay đổi
5. **Việc chưa xong** — TC chưa chạy, bug chưa verify, lý do
6. **Đề xuất sprint sau** — tối đa 3 mục, cụ thể

Lưu vào `artifacts/test-results/sprint_<số>_qa_report.md`. Output Tiếng Việt, số liệu dạng bảng.
