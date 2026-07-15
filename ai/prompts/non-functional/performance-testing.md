---
name: performance-testing
description: Thiết kế performance test — workload model, KPI, kịch bản load/stress/spike/soak + script skeleton
inputs: Phạm vi (API/flow cần đo) + dữ liệu sử dụng thực tế nếu có (số user, peak hours) + môi trường được phép chạy
references: templates/test-plan-template.md, execution/api/cscs/ (endpoint hiện có)
---

Thiết kế performance test cho: `{{scope}}`

> ⚠️ Project CHƯA có performance tool — đề xuất mặc định **k6** (script JS, chạy CLI, dễ tích hợp CI) hoặc JMeter nếu team quen GUI. Ghi rõ lựa chọn + lý do trong output.
> ⚠️ CHỈ chạy load test trên môi trường được cho phép — KHÔNG BAO GIỜ bắn load vào production hoặc env shared khi chưa có xác nhận.

Quy trình:
1. **Workload model**: xác định flow/API quan trọng (theo tần suất dùng + risk), số user đồng thời, tỷ lệ thao tác — số liệu phải có NGUỒN (analytics, ước tính business ghi rõ là ước tính)
2. **KPI + ngưỡng**: response time p50/p95/p99, throughput (req/s), error rate, ngưỡng pass/fail cụ thể (VD p95 < 2s, error < 1%) — ngưỡng lấy từ requirement; không có → đề xuất + đánh dấu cần business confirm
3. **Kịch bản**: 📈 Load (mức bình thường + peak) · 💥 Stress (tăng tới gãy, tìm breaking point) · ⚡ Spike (tăng đột ngột) · 🕐 Soak (giữ tải dài, tìm memory leak) — chọn loại theo mục tiêu, không làm đủ 4 loại nếu không cần
4. **Script skeleton**: sinh script k6/JMeter cho kịch bản đã chọn — auth setup, data unique theo VU (virtual user), ramp-up/down, thresholds khai báo trong script
5. **Tiêu chí phân tích**: bảng kết quả mong đợi, cách đọc (p95 vs avg, error theo thời gian), điều kiện dừng test khẩn cấp

Output: tài liệu thiết kế (workload + KPI + kịch bản) + script skeleton vào `generated-tests/performance/` + lệnh chạy. Output Tiếng Việt.
