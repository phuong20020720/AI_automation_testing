---
name: review-test-data
description: Review chiến lược test data trong code/data files — unique, traceable, an toàn parallel
inputs: Đường dẫn test code và/hoặc test-data/
references: ai/rules/automation_rules.md (mục 2), skill test_data_generator
---

Review test data trong: `{{path}}`

Kiểm tra:

| Nhóm | Câu hỏi |
|---|---|
| **Unique** | Trường yêu cầu unique (email, username, mã KH) có hardcode? Có sinh động (timestamp/UUID/Faker)? |
| **Traceable** | Format `[prefix]_[testName]_[timestamp]_[random]`? Nhìn DB biết test nào tạo? |
| **Parallel-safe** | 2 test method chạy song song có đụng data? Data theo worker/method hay shared? |
| **Cleanup** | Data tạo ra có dọn sau test? Chạy lần 2 có bị conflict? |
| **Tách khỏi code** | Data cấu trúc có nằm đúng chỗ (`test-data/mobile/*.json`, `test-data/api/*.csv`) thay vì hardcode trong class? |
| **Nhạy cảm** | Có thông tin thật (email người thật, credentials) trong data file? Secrets có trong `.env` thay vì code? |

Output:
1. Bảng findings: `| # | File:Line | Nhóm | Vấn đề | Mức độ | Đề xuất |`
2. Đề xuất chuẩn hóa: helper `DataGenerator` còn thiếu method nào cho các loại data đang hardcode
