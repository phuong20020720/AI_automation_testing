---
name: risk-based-testing
description: Đánh giá rủi ro các khu vực chức năng để quyết định độ sâu test (RBT)
inputs: Requirements/feature list + thông tin nghiệp vụ (tần suất dùng, impact)
references: skill rbt_manual_testing (quy trình FULL RBT 6 bước), workflows/generate_manual_testcases_rbt.md
---

> Cần quy trình RBT đầy đủ 6 bước có checkpoint → dùng workflow `generate_manual_testcases_rbt`. Prompt này dùng cho đánh giá nhanh 1 lần.

Đánh giá rủi ro cho: `{{features}}`

1. **Ma trận rủi ro** từng khu vực:

   | # | Khu vực | Likelihood (1-5) | Impact (1-5) | Risk Score | Risk Level | Lý do |
   |---|---|---|---|---|---|---|

   - Likelihood: độ phức tạp logic, tần suất thay đổi code, lịch sử bug
   - Impact: ảnh hưởng nghiệp vụ/tiền/pháp lý/dữ liệu cá nhân, số user bị ảnh hưởng
   - Risk Score = L × I → Level: 🔴 ≥15 · 🟠 8–14 · 🟡 4–7 · 🟢 ≤3

2. **Chiến lược test theo level:**
   - 🔴 Critical: TC chi tiết đầy đủ + automation + exploratory
   - 🟠 High: TC chi tiết các flow chính + negative chính
   - 🟡 Medium: TC happy path + checklist
   - 🟢 Low: checklist / smoke

3. **Đề xuất thứ tự thực hiện** + ước lượng số TC mỗi khu vực
