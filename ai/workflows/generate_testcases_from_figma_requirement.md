---
description: Kết nối Figma design (qua Figma MCP) + requirements để sinh test cases. Hỗ trợ 3 mode — QUICK (test cases), ANALYZE (requirements doc + test cases), FULL (kèm automation scripts).
skills:
  - requirements_analyzer
  - rbt_manual_testing
  - qa_automation_engineer
---

> **BẮT BUỘC (MANDATORY SKILL):** Bạn PHẢI nạp và đọc kỹ skill **`rbt_manual_testing`** (nạp qua công cụ Skill — tự resolve theo tên) để sinh test cases đúng chuẩn. Khi cần phân tích design thành requirements, tham khảo thêm skill **`requirements_analyzer`**; khi sinh automation (Mode FULL) tham khảo **`qa_automation_engineer`**.

# Workflow: Sinh Test Cases từ Figma Design + Requirements

Workflow này **kết nối trực tiếp tới Figma** bằng Figma MCP để lấy design context (layout, components, fields, text, variables), **đối chiếu với requirements** do user cung cấp, sau đó sinh ra test cases bám sát đúng những gì design thể hiện — thay vì đoán mò UI.

## ⚠️ Nguyên tắc thực thi

- **Tất cả output bằng Tiếng Việt**
- **Quy trình BẮT BUỘC theo thứ tự:** ① Đọc Figma → ② Đọc Requirements → ③ **Đối chiếu** Figma vs Requirements → ④ Rồi MỚI sinh test cases. KHÔNG được sinh TC chỉ từ Figma hoặc chỉ từ Requirements.
- **KHÔNG đoán UI** — mọi mô tả field/component/label/state phải lấy từ Figma data thực tế qua MCP
- **PHẢI đọc requirements** — tìm trong `knowledge-base/{app|web}/requirements/` hoặc theo đường dẫn user cung cấp. Nếu KHÔNG có requirements → KHÔNG sinh TC ngay; chuyển **Mode ANALYZE** (sinh requirements từ design trước) và báo user.
- **PHẢI đối chiếu Figma vs Requirements** — phát hiện và ghi rõ mọi điểm không khớp (inconsistency) → Ambiguities
- **Phải chờ user xác nhận scope** tại Bước 3 (CHECKPOINT) trước khi sinh test cases chi tiết
- Nếu user chưa cung cấp Figma URL → hỏi trước khi bắt đầu
- ⚠️ **Rule E3** (chỉ Mode FULL): khi test FAIL → tự đọc log → phân tích → sửa → chạy lại, KHÔNG hỏi user trong lúc fix

## Yêu cầu kết nối Figma (MCP)

Workflow dùng Figma MCP server (Framelink `figma-developer-mcp`) với 2 tool:

| Tool | Công dụng |
|---|---|
| `mcp__figma__get_figma_data` | Lấy layout, content, components, variables của file/frame |
| `mcp__figma__download_figma_images` | Tải PNG/SVG của node ảnh/icon để embed làm evidence |

> [!NOTE]
> Nếu Figma MCP chưa cấu hình trong `.claude/mcp.json`, thêm server (cần `FIGMA_API_KEY`):
> ```json
> "figma": {
>   "command": "npx",
>   "args": ["-y", "figma-developer-mcp", "--stdio"],
>   "env": { "FIGMA_API_KEY": "fig_YOUR_TOKEN_HERE" }
> }
> ```

## 3 Chế độ (Mode)

| Mode | Khi nào sử dụng | Output |
|---|---|---|
| **QUICK** (mặc định) | Đã có requirements rõ ràng, chỉ cần test cases bám design | Manual Test Cases (Markdown) |
| **ANALYZE** | Design phức tạp / requirements sơ sài → cần phân tích trước | Requirements Doc + Manual Test Cases |
| **FULL** | Cần cả automation scripts | Như ANALYZE + Automation Scripts (đã chạy PASS) |

> Nếu user nói "generate automation", "viết code test" → tự chuyển **Mode FULL**.
> Nếu design mơ hồ, nhiều state/flow chồng chéo, hoặc thiếu requirements → tự đề xuất **Mode ANALYZE** (hoặc FULL RBT của `rbt_manual_testing`).

## Đầu vào (Input)

| # | Input | Bắt buộc | Mô tả |
|---|---|---|---|
| 1 | **Figma URL** | ✅ | Link `figma.com/(file\|design)/<fileKey>/...`, ưu tiên có `node-id=<nodeId>` để khoanh đúng frame |
| 2 | **Requirements** | ✅ (cốt lõi) | User story / AC / business rules. Tìm trong `knowledge-base/{app\|web}/requirements/` hoặc user cung cấp. Thiếu → chuyển Mode ANALYZE (không bỏ qua bước đối chiếu) |
| 3 | **Scope** | ⭕ Tùy chọn | Frame/màn hình cụ thể cần test (nếu file lớn nhiều screen) |
| 4 | **Tech stack** | ⭕ Tùy chọn | Chỉ Mode FULL — framework automation mong muốn |

> [!NOTE]
> **Quy ước thư mục project:** requirements để ở `knowledge-base/{app|web}/requirements/<feature>.md`, design flow ở `knowledge-base/{app|web}/flows/`. Agent PHẢI chủ động tìm file requirements khớp với màn hình Figma trong thư mục này trước khi hỏi user.

**Cách lấy `fileKey` và `nodeId` từ URL Figma:**
- `https://figma.com/design/`**`ABC123xyz`**`/My-App?node-id=`**`1234-5678`** → `fileKey = ABC123xyz`, `nodeId = 1234:5678` (đổi `-` thành `:`)
- Nếu user chỉ đưa URL file (không có `node-id`) → fetch toàn file rồi hỏi user chọn frame ở Bước 3.

## Các bước thực hiện

### Bước 1: Đọc Figma + Đọc Requirements (Dual-source Intake)

> Đây là 2 nguồn BẮT BUỘC. Phải có cả hai trước khi sang Bước 2.

**1a. Đọc Figma:**
1. **Parse Figma URL** → trích `fileKey` + `nodeId` (nếu có).
2. **Gọi `mcp__figma__get_figma_data`** với `fileKey` (+ `nodeId` nếu có):
   - KHÔNG dùng `depth` trừ khi file quá lớn cần giới hạn.
   - Nếu trả về quá nhiều node → khoanh vùng bằng `nodeId` của frame cụ thể.
   - ⚠️ Nếu output quá lớn (vượt token limit) → giao subagent đọc file kết quả theo chunk và trả về Design Inventory, giữ data thô ngoài context chính.

**1b. Đọc Requirements:**
3. **Tìm file requirements** khớp với màn hình Figma:
   - Quét `knowledge-base/{app|web}/requirements/` → chọn file khớp tên feature/màn hình.
   - Nếu user đã chỉ đường dẫn cụ thể → đọc đúng file đó.
4. **Đọc kỹ requirements** — trích Main Flow, Business Rules, Validation, Acceptance Criteria liên quan tới màn hình đang test.
5. Nếu **KHÔNG tìm thấy requirements** → KHÔNG sinh TC; chuyển **Mode ANALYZE** (sinh requirements từ design trước, cho user duyệt) và báo user.
6. **Xác nhận** đã có CẢ design context VÀ requirements → tiếp tục Bước 2.

### Bước 2: Trích xuất Design Context (Design Analysis)

Từ Figma data, trích xuất có cấu trúc:

1. **Cấu trúc màn hình** — Frame/screen names, layout (header, sidebar, main, footer), navigation/breadcrumb.
2. **Components & Fields** — Liệt kê từng UI element:
   - Loại: input / dropdown / checkbox / radio / date picker / button / table / modal / tab...
   - Label, placeholder, default value, options (cho dropdown/radio)
   - Trạng thái thể hiện trong design: default / hover / focus / error / disabled / empty / filled
3. **Text & Nội dung** — Tiêu đề, helper text, error messages, validation hints hiển thị trong design.
4. **Variables / Design tokens** (nếu có) — giá trị enum, status, range gợi ý từ component variants.
5. **Flow & State** — Suy ra các luồng/trạng thái từ nhiều frame (VD: empty state → có data; bước 1 → bước 2 của form wizard).
6. **(Tùy chọn) Tải ảnh evidence** — dùng `mcp__figma__download_figma_images` lưu vào `artifacts/figma/` để embed minh họa.

> **Bảng Design Inventory** (ghi nhận để dùng cho test design) — cấu trúc bảng theo **`ai/prompts/figma/analyze-screen.md`** (mục Components). Có thể dùng prompt đó cho từng màn hình để ra output đầy đủ 7 mục (Purpose, Components, User Actions, System Responses, Validation, Assumptions, Questions).

### Bước 3: Đối chiếu & Xác nhận Scope (CHECKPOINT — ⏸️ DỪNG LẠI)

1. **Đối chiếu Figma vs Requirements** — thực hiện theo **`ai/prompts/figma/identify-missing-requirements.md`**:
   - Lập bảng đối chiếu (Mục / Requirements nói / Figma thể hiện / Khớp? / Ghi chú)
   - Phân loại điểm không khớp: 🔴 chỉ có trong design · 🟠 chỉ có trong requirements · 🟡 cả hai có nhưng khác nhau
   - Mỗi điểm không khớp → ghi thành **Ambiguity (AMB-XX)** kèm câu hỏi cần clarify.

2. **Trình bày tóm tắt** cho user review:
   - Số màn hình/frame + số element phát hiện
   - Danh sách flows/states sẽ test
   - Các điểm mơ hồ / không khớp (nếu có)
   - Mode đề xuất (QUICK / ANALYZE / FULL)

3. **Hỏi user xác nhận:**
   - "Test toàn bộ màn hình hay chỉ frame/flow nào?"
   - "Output là test cases (QUICK), kèm requirements doc (ANALYZE), hay cả automation (FULL)?"
   - Mode FULL: "Tech stack mong muốn?"

4. **Chờ user xác nhận** trước khi sang Bước 4.

> [!IMPORTANT]
> Nếu Mode = ANALYZE/FULL: trước khi sinh test cases, sinh **Requirements Document** từ design + req (theo template của workflow `analyze_requirement_document` / skill `requirements_analyzer`), rồi mới qua Bước 4.

### Bước 4: Sinh Test Cases (Test Design)

1. **Áp dụng kỹ thuật thiết kế** (theo `rbt_manual_testing`):
   - Equivalence Partitioning (EP) + Boundary Value Analysis (BVA) cho từng input field (dựa ràng buộc suy ra từ design + req)
   - Decision Table khi có nhiều rule/điều kiện kết hợp
   - State Transition cho các flow/state phát hiện ở Bước 2
   - UI/UX checks bám đúng design: label đúng chữ, placeholder, thứ tự field, error state thể hiện trong Figma

2. **Bao phủ đủ loại case:** ✅ Happy Path · ❌ Negative (validation, auth) · 🔲 Boundary · ⚡ Edge case · 🎨 UI-state (empty/error/disabled/responsive).

3. **Sinh test cases đầy đủ fields:**
   - TC ID — format `[DỰ_ÁN]_[MODULE]_TC_[SỐ]`
   - Module / Màn hình
   - Test Scenario / Title
   - Pre-conditions
   - Test Steps (đánh số)
   - Test Data (**cụ thể**, unique + traceable — VD `auto_figma_login_20260602_A3F2@test.com`, KHÔNG placeholder)
   - Expected Result (đánh số tương ứng steps)
   - Priority (Critical / High / Medium / Low)
   - **Figma Ref** — frame/node liên quan (để trace ngược về design)

### Bước 5: Đóng gói Output (Delivery)

> [!IMPORTANT]
> **Vị trí lưu file** (BẮT BUỘC theo convention project):
> `knowledge-base/{app|web}/Testcase/test_cases_<feature_name>.md`
> - Chọn `app` hay `web` theo nền tảng của design/requirements (VD design mobile → `app`).
> - `<feature_name>` viết snake_case (VD `declaration`, `sign_up_email`).
> - Header file PHẢI có metadata block (blockquote): App / Phạm vi / Nguồn design (Figma) / Nguồn requirements / Đánh số / Ngày sinh / QA Owner / Tổng số TC — tham khảo các file mẫu trong `knowledge-base/app/Testcase/`.
> - TC dùng format **Bảng** theo `ai/prompts/templates/testcase-template.md` (cột đầu `TC ID`, đúng bộ cột chuẩn). Figma Ref ghi ở blockquote dưới heading Feature, KHÔNG thêm cột riêng.

1. **Xuất artifact** với cấu trúc:
   - **Tổng quan** — Figma file, frames test, requirements nguồn, tổng số TC
   - **Design Inventory** (bảng Bước 2)
   - **Bảng đối chiếu Figma vs Requirements** + danh sách Ambiguities (Bước 3)
   - **Test Cases** — format **Bảng** (mỗi TC = 1 dòng, nhóm theo heading `## Feature N`):

     ```markdown
     ## Feature 1: Declaration
     > Figma: 13116:4231

     | TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
     |---|---|---|---|---|---|---|---|---|
     | PPAC_DECL_TC_001 | DECL.1 | High | Hiển thị đúng màn Declaration | Worker đã hoàn thành Your Details + References | 1. Điều hướng đến tab Declaration | luồng CWG | 1. Tab "Declaration" active; hiển thị 12 checkbox, nút "Next →" | High |
     ```

     Quy tắc chi tiết (bộ cột hợp lệ, `<br>` cho field nhiều dòng, vị trí Figma Ref): theo **`ai/prompts/templates/testcase-template.md`** — nguồn chân lý duy nhất về format, tương thích `scripts/export_testcases_to_excel.ps1`.

   - **(Tùy chọn) Evidence** — ảnh design đã tải embed minh họa.

2. Nếu **Mode QUICK / ANALYZE** → KẾT THÚC tại đây (ANALYZE kèm thêm Requirements Document).

### Bước 6: Sinh Automation Scripts (Mode FULL)

> Chỉ thực hiện khi **Mode FULL**. Tuân thủ `qa_automation_engineer` + rules trong `.claude/rules/`.

1. **Thiết kế project structure** theo framework (Page Object Model).
2. **Sinh code** từ test cases:
   - ⚠️ Locator KHÔNG lấy từ Figma (Figma không có DOM thật). Lấy locator bằng cách inspect ứng dụng thật qua Playwright MCP (xem workflow `generate_automation_from_ui_flow`), HOẶC để TODO nếu app chưa sẵn sàng.
   - Page Object + Test class theo Arrange → Act → Assert, smart waits (KHÔNG `sleep`/`waitForTimeout`).
3. **Chạy test + Auto-Heal** (Rule E3): chạy → đọc log → sửa → lặp tối đa 5 vòng → verify chạy 2 lần PASS.

## Quy tắc quan trọng

- ✅ **Design context PHẢI từ Figma MCP** — không tự bịa field/label/state
- ✅ **PHẢI ghi rõ inconsistency** giữa Figma và Requirements (đây là giá trị cao nhất)
- ✅ Test Data cụ thể, unique, traceable — không placeholder
- ✅ Mỗi TC có Figma Ref để trace về design
- ✅ Bao gồm cả UI-state cases mà design thể hiện (empty/error/disabled)
- ❌ KHÔNG đoán business logic nếu cả design lẫn req không nói rõ → đưa vào Ambiguities
- ❌ KHÔNG suy ra locator automation từ Figma node id

## Mối quan hệ với workflows khác

| Tình huống | Workflow |
|---|---|
| Chỉ muốn phân tích requirement (không sinh TC) | `/analyze_requirement_document` |
| Đã có requirements text, không cần Figma | `/generate_testcases_from_requirements` |
| Cần quy trình RBT 6 bước bài bản | `/generate_manual_testcases_rbt` |
| Có app thật, cần thu locator + automation | `/generate_automation_from_ui_flow` |
| Đã có TC, cần sinh automation | `/generate_automation_from_testcases` |
