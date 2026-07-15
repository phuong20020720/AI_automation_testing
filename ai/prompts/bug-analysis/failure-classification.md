---
name: failure-classification
description: Phân loại hàng loạt test failures sau 1 lần chạy suite — Product / Automation / Environment
inputs: Test report (Allure/TestNG/newman trong artifacts/reports/) hoặc danh sách failures + logs
references: bug-analysis/root-cause.md (phân tích sâu từng bug), bug-analysis/flaky-test-analysis.md
---

Phân loại failures từ: `{{report}}`

Với MỖI failed test, phân tích stacktrace + screenshot + log và phân loại:

| Loại | Dấu hiệu | Hành động tiếp |
|---|---|---|
| 🐞 **Product Defect** | App trả sai kết quả/lỗi server, test logic đúng | Viết bug report (`templates/bug-report-template.md`) |
| 🤖 **Automation Defect** | Locator hỏng, wait sai, data conflict, assertion lỗi thời | Fix test code (xem `flaky-test-analysis`) |
| 🌐 **Environment Defect** | Server down, network, config env, thiết bị/emulator | Báo DevOps/infra, retry sau khi env ổn |

Output:

| Test | Error tóm tắt | Phân loại | Confidence | Lý do | Hành động |
|---|---|---|---|---|---|

Quy tắc:
- Mỗi phân loại kèm **confidence score** (%) — dưới 70% đánh dấu ⚠️ cần human verify, ghi rõ thiếu evidence gì
- Tổng hợp cuối: số lượng theo loại, common factor (nhiều test fail cùng nguyên nhân?), trạng thái build: ❌ Block release / ⚠️ Có product bug / ✅ Chỉ lỗi automation-env
