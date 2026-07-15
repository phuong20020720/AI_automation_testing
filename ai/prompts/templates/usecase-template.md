---
name: usecase-template
description: Format chuẩn cho Use Case
---

## UC_<MODULE>_<SỐ>: <Tên use case>

| | |
|---|---|
| **Actor** | <actor chính (+ phụ nếu có)> |
| **Mô tả** | <1-2 câu mục tiêu nghiệp vụ> |
| **Trigger** | <điều gì khởi phát UC> |
| **Precondition** | <trạng thái hệ thống/data trước khi bắt đầu> |
| **Postcondition** | <trạng thái sau khi UC thành công> |
| **Liên quan** | <requirement IDs, BR IDs, Figma ref> |

### Main Flow
1. <Actor làm gì>
2. <Hệ thống phản hồi gì>
3. ...

### Alternative Flows
**AF-1: <tên nhánh>** (rẽ tại bước <n>)
1. ...
2. Quay lại bước <n> / Kết thúc

### Exception Flows
**EF-1: <tên lỗi>** (tại bước <n>)
1. <Hệ thống hiển thị lỗi gì, trạng thái ra sao>

### Business Rules áp dụng
- BR-XX: <tóm tắt>

### Open Questions
- <điểm chưa rõ>
