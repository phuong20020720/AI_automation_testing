---
name: release-report
description: Báo cáo QA sign-off cho release — kết quả test + đánh giá rủi ro + khuyến nghị go/no-go
inputs: Release version + scope thay đổi + test results
references: templates/test-report-template.md, test-design/regression-impact.md
---

Lập báo cáo QA cho release `{{version}}`:

1. **Khuyến nghị đầu tiên:** ✅ GO / ⚠️ GO có điều kiện / ❌ NO-GO — kèm 1 đoạn lý do
2. **Scope đã test** — features mới, regression scope (từ `regression-impact` nếu có), khu vực KHÔNG test + lý do
3. **Kết quả:** bảng pass/fail theo module (manual + automation), link reports trong `artifacts/`
4. **Open defects khi release:**

   | ID | Severity | Mô tả | Workaround | Chấp nhận release? |
   |---|---|---|---|---|

5. **Rủi ro còn lại** — khu vực test mỏng, assumption chưa verify, môi trường khác production thế nào
6. **Điều kiện** (nếu GO có điều kiện) — việc phải làm trước/ngay sau release, ai làm

Nguyên tắc: trung thực số liệu — fail là fail, không làm mềm; khu vực không test phải nói rõ. Lưu `artifacts/test-results/release_<version>_qa_signoff.md`.
