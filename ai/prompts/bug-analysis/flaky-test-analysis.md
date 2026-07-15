---
name: flaky-test-analysis
description: Phân tích nhanh 1 test flaky — phân loại nguyên nhân + đề xuất fix
inputs: Test file path + error log / lịch sử fail
references: workflows/analyze_flaky_tests.md (quy trình đầy đủ 2 mode), skill flaky_test_analyzer
---

> Cần quy trình đầy đủ (chạy lại 3 lần, auto-fix, verify stability) → dùng workflow `analyze_flaky_tests`. Prompt này cho phân tích nhanh khi đã có evidence.

Phân tích flaky test: `{{test}}` với evidence: `{{evidence}}`

1. **Phân loại** theo bảng root cause của workflow `analyze_flaky_tests` (Bước 2): 🎯 Locator · ⏱️ Timing · 📊 Data conflict · 🔄 State dependency · 🌐 Environment · 🖼️ Animation · 📱 Viewport · 🧹 Cleanup

2. **Inspect code** theo 4 checklist: locator (dynamic class? positional xpath?), wait (sleep? fixed delay?), data (hardcode? unique?), independence (phụ thuộc thứ tự? share state?)

3. **Output:**

   | # | Vị trí (file:line) | Category | Vấn đề | Mức độ | Fix đề xuất (code cũ → mới) |
   |---|---|---|---|---|---|

4. Kết luận: nguyên nhân chính + confidence + bước verify đề xuất (chạy N lần sau fix)
