---
name: generate-usecase
description: Sinh use cases từ requirements document
inputs: Đường dẫn requirements (knowledge-base/{app|web}/requirements/<feature>.md) hoặc text
references: templates/usecase-template.md, requirements/business-rule-extraction.md
---

Sinh use cases từ requirements: `{{requirements}}`

Quy trình:
1. Xác định **actors** (worker, contractor, admin...) và mục tiêu nghiệp vụ của từng actor — requirements không nói rõ actor → ghi Open Questions, không tự gán
2. Mỗi mục tiêu nghiệp vụ độc lập = 1 use case (kiểm tra: actor đạt được giá trị gì khi UC kết thúc? Không trả lời được → đó là 1 bước, không phải UC)
3. Viết Main Flow → Alternative/Exception Flows → trace BR

Quy tắc:
- Format theo `templates/usecase-template.md` — UC ID: `UC_<MODULE>_<SỐ>` (VD `UC_DECL_01`)
- Main Flow đánh số từng bước, mỗi bước 1 hành động actor HOẶC 1 phản hồi hệ thống — không gộp 2 hành động/bước
- Alternative Flow (`AF-x`) = nhánh hợp lệ khác · Exception Flow (`EF-x`) = lỗi/validation — đều trỏ về bước rẽ nhánh trong Main Flow và ghi điểm quay lại/kết thúc
- Precondition/Postcondition phải kiểm chứng được (VD "user đã đăng nhập với role contractor", không phải "hệ thống sẵn sàng")
- Mọi business rule trong requirements phải xuất hiện ở ít nhất 1 flow — cuối output thêm bảng trace `BR-XX → UC/Flow`; BR không map được vào UC nào → liệt kê riêng kèm lý do (thiếu UC hay BR ngoài scope?)
- Requirement mơ hồ/mâu thuẫn → ghi vào Open Questions của UC kèm các cách hiểu có thể, KHÔNG tự chọn một cách hiểu

Ví dụ mức chi tiết mong đợi (1 bước Main Flow):

```markdown
3. Worker nhấn "Submit Declaration"
4. Hệ thống validate các trường bắt buộc theo BR-03 → hợp lệ: chuyển bước 5 · không hợp lệ: EF-1
```

Checklist trước khi deliver: mỗi UC có ≥1 Exception Flow (flow chỉ toàn happy path thường là chưa phân tích đủ) · bảng trace BR đầy đủ · Open Questions tách riêng khỏi nội dung UC.

Output Tiếng Việt, lưu `knowledge-base/{app|web}/usecases/usecase_<feature>.md`.
