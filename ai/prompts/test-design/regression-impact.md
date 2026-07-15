---
name: regression-impact
description: Phân tích thay đổi (release/PR/feature mới) để xác định scope regression cần chạy
inputs: Mô tả thay đổi (changelog, PR diff, feature spec) + bộ test cases hiện có
references: requirements/gap-analysis.md
---

Phân tích impact của thay đổi: `{{change}}`

1. **Bản đồ ảnh hưởng:**

   | Thay đổi | Khu vực ảnh hưởng trực tiếp | Khu vực ảnh hưởng gián tiếp | Lý do |
   |---|---|---|---|

   Xét gián tiếp qua: shared component, shared data/DB, API contract, navigation flow, permission

2. **Scope regression đề xuất:**

   | Mức | Test cần chạy | Nguồn TC |
   |---|---|---|
   | 🔴 Phải chạy | TC của khu vực ảnh hưởng trực tiếp | `knowledge-base/{app\|web}/Testcase/...` |
   | 🟠 Nên chạy | TC khu vực gián tiếp + integration points | |
   | 🟢 Smoke | Happy path toàn hệ thống | testng suite tương ứng |

3. **Gap** — khu vực ảnh hưởng nhưng CHƯA có TC/automation → liệt kê để viết bổ sung
4. **Automation** — suite nào chạy được ngay (`mvn test -P web` / `-P mobile`), TC nào phải manual
