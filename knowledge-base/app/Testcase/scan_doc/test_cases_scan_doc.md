# Test Cases — Xác minh Passport và tạo Submission (UC-01)

> App: **PPAC Audit** — Mobile (Flutter) + Regula SDK + Backend + Admin Portal
> Phạm vi: Face Scan · Passport Scan (Regula) · Document Scoring (18 tiêu chí) · Verification Status · tạo Submission · hiển thị Admin Portal
> Nguồn requirements: `knowledge-base/app/requirements/requirement_scan_doc/scan_doc.md` · Config điểm: `uat-db.verification-configs.json`
> Đánh số: **IDV** (Identity Verification) — `PPAC_IDV_TC_NNN`
> Ngày sinh: 2026-07-06 · QA Owner: harry.vo · Tổng số TC: **65** · Mode: **QUICK**
> Kỹ thuật áp dụng: Equivalence Partitioning, Boundary Value Analysis (ngưỡng BR-06), Decision Table (tổ hợp tiêu chí scoring), State Transition (Face → Passport → Scoring → Submission).

## Tổng quan

| Module | Dải TC | Số TC | Nguồn req |
|---|---|---|---|
| M1 — Face Scan | TC001–TC006 | 6 | AF-01, BR-01, VR-01 |
| M2 — Passport Scan | TC007–TC014 | 8 | AF-02, BR-02, VR-02 |
| M3 — Document Scoring (18 tiêu chí) | TC015–TC036 | 22 | DS-01…DS-18, BR-03, BR-09 |
| M4 — Verification Status (ngưỡng) | TC037–TC045 | 9 | BR-06, BR-11 |
| M5 — Face Similarity (loại trừ) | TC046–TC049 | 4 | BR-10, VR-08, DS-05/DS-09 |
| M6 — Tạo Submission | TC050–TC055 | 6 | AF-03, BR-04, BR-05, BR-07, VR-03, VR-04 |
| M7 — Admin Portal & Output | TC056–TC059 | 4 | BR-08, VR-06, Mục 12 |
| M8 — End-to-End & Edge | TC060–TC065 | 6 | Pre-conditions, Main Flow, Edge |
| **Tổng** | | **65** | |

**Phân bố Priority:** Critical: 10 · High: 33 · Medium: 18 · Low: 4

**Dữ liệu test dùng chung:**
- Applicant hợp lệ: `applicant.valid@ppac.co.uk` — hồ sơ citizenship = `GBR`, DOB = `01/01/1990`.
- Passport hợp lệ mẫu: số `GBR123456789`, expiryDate = `01/01/2030`, MRZ đọc được, có chip RFID.
- Giả định ngưỡng (chưa có trong req — xem Ambiguities): `faceSimilarity.threshold = 0.75`, `lowThreshold = 0.55`; `minimumAge = 18`; `maxGapYears = 10`.

---

## M1 — Face Scan

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority | Status |
|---|---|---|---|---|---|---|---|---|---|
| PPAC_IDV_TC_001 | M1 Face Scan | High | Face Scan thành công → mở bước Passport Scan (Happy Path) | Đã đăng nhập, đã cấp quyền camera, Regula sẵn sàng | 1. Bắt đầu quy trình xác minh<br>2. Thực hiện Face Scan với khuôn mặt thật, đủ sáng | applicant.valid@ppac.co.uk | 1. Hiển thị màn hình Face Scan<br>2. Xác minh khuôn mặt thành công, mở/enable chức năng Passport Scan | High |  |
| PPAC_IDV_TC_002 | M1 Face Scan | High | AF-01 — Không nhận diện được khuôn mặt | Đang ở màn hình Face Scan | 1. Đưa camera vào vật thể không phải khuôn mặt<br>2. Thực hiện Face Scan | Ảnh không có khuôn mặt | 1–2. Hiển thị thông báo lỗi; cho phép Face Scan lại; KHÔNG chuyển sang Passport Scan | High |  |
| PPAC_IDV_TC_003 | M1 Face Scan | Medium | AF-01 — Chất lượng ảnh Face Scan không đạt | Đang ở màn hình Face Scan | 1. Thực hiện Face Scan trong điều kiện thiếu sáng/rung mờ | Ảnh mờ, thiếu sáng | 1. Hiển thị lỗi chất lượng; yêu cầu chụp lại; không sang Passport Scan | Medium |  |
| PPAC_IDV_TC_004 | M1 Face Scan | High | BR-01/VR-01 — Chặn Passport Scan khi Face Scan chưa thành công | Đang ở màn hình Face Scan, chưa scan thành công | 1. Thử truy cập/kích hoạt chức năng Passport Scan | — | 1. Chức năng Passport Scan bị disable/chặn; không cho bỏ qua Face Scan | High |  |
| PPAC_IDV_TC_005 | M1 Face Scan | High | AF-01 — Face Verification thất bại (liveness/không khớp) | Đang ở màn hình Face Scan | 1. Thực hiện Face Scan bằng ảnh in khuôn mặt (không phải người thật) | Ảnh in giấy | 1. Face Verification thất bại; hiển thị lỗi; cho scan lại; không sang Passport Scan | High |  |
| PPAC_IDV_TC_006 | M1 Face Scan | Medium | Face Scan lại thành công sau khi thất bại | Vừa Face Scan thất bại | 1. Nhấn thử lại<br>2. Thực hiện Face Scan đúng cách | applicant.valid@ppac.co.uk | 2. Face Scan thành công; mở bước Passport Scan | Medium |  |

---

## M2 — Passport Scan

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority | Status |
|---|---|---|---|---|---|---|---|---|---|
| PPAC_IDV_TC_007 | M2 Passport Scan | High | Quét Passport rõ nét → trích xuất thông tin (Happy Path) | Face Scan đã thành công | 1. Chọn Passport Scan<br>2. Quét passport rõ nét, đủ sáng, thẳng góc | Passport GBR123456789 | 1. Mở camera quét passport<br>2. Regula kiểm tra chất lượng OK, nhận diện passport, trích xuất thông tin (MRZ, họ tên, ngày sinh, hạn) và hoàn tất xác minh passport | High |  |
| PPAC_IDV_TC_008 | M2 Passport Scan | High | AF-02 — Ảnh passport bị mờ | Đang ở màn hình Passport Scan | 1. Quét passport với camera rung/lấy nét kém | Ảnh mờ | 1. Hiển thị lỗi; cho quét lại; KHÔNG thực hiện Document Scoring | High |  |
| PPAC_IDV_TC_009 | M2 Passport Scan | Medium | AF-02 — Ảnh passport bị chói (glare) | Đang ở màn hình Passport Scan | 1. Quét passport dưới ánh sáng gây chói/lóa | Ảnh chói | 1. Hiển thị lỗi chất lượng; cho quét lại; không Document Scoring | Medium |  |
| PPAC_IDV_TC_010 | M2 Passport Scan | Medium | AF-02 — Passport bị cắt góc / che khuất | Đang ở màn hình Passport Scan | 1. Quét passport nhưng để lọt/cắt 1 góc ra ngoài khung | Passport thiếu góc | 1. Hiển thị lỗi; cho quét lại; không Document Scoring | Medium |  |
| PPAC_IDV_TC_011 | M2 Passport Scan | High | AF-02 — Không đọc được MRZ | Đang ở màn hình Passport Scan | 1. Quét trang passport không có/không rõ vùng MRZ | MRZ không đọc được | 1. Hiển thị lỗi MRZ; cho quét lại; không Document Scoring | High |  |
| PPAC_IDV_TC_012 | M2 Passport Scan | Medium | AF-02 — OCR thất bại | Đang ở màn hình Passport Scan | 1. Quét passport có chữ mờ/hư hỏng khiến OCR fail | Passport chữ mờ | 1. Hiển thị lỗi OCR; cho quét lại; không Document Scoring | Medium |  |
| PPAC_IDV_TC_013 | M2 Passport Scan | High | BR-02/VR-02 — Passport Scan thất bại thì không tính điểm | Passport Scan vừa thất bại | 1. Kiểm tra trạng thái sau khi passport fail | — | 1. Document Scoring KHÔNG được kích hoạt; không có kết quả điểm nào được lưu | High |  |
| PPAC_IDV_TC_014 | M2 Passport Scan | Medium | AF-02 — Không nhận diện được passport (sai loại giấy tờ) | Đang ở màn hình Passport Scan | 1. Quét một thẻ không phải passport (vd thẻ ngân hàng) | Thẻ không hợp lệ | 1. Không nhận diện được passport; hiển thị lỗi; cho quét lại; không Document Scoring | Medium |  |

---

## M3 — Document Scoring (18 tiêu chí — kiểm tra weight từng key)

> Mỗi TC: cấu hình phiên xác minh để **đúng 1 tiêu chí FAIL**, sau đó kiểm tra tiêu chí đó xuất hiện trong Failed Criteria và cộng **đúng Failed Weight** vào Total Failed Weight (BR-09).

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority | Status |
|---|---|---|---|---|---|---|---|---|---|
| PPAC_IDV_TC_015 | M3 Scoring | High | DS-01 deepFakeInvalid — weight 1.0 | Face + Passport Scan hoàn tất | 1. Xác minh với selfie là ảnh deepfake<br>2. Chạy Document Scoring | deepFake.className = "fake" (≠ expectedClassName) | 2. Failed Criteria có `deepFakeInvalid`; Failed Weight = 1.0 cộng vào Total | High |  |
| PPAC_IDV_TC_016 | M3 Scoring | High | DS-02 livenessFailed — weight 1.0 | Face + Passport Scan hoàn tất | 1. Liveness không đạt (ảnh/video/mask)<br>2. Chạy scoring | livenessStatus ≠ expectedStatus | 2. `livenessFailed` FAIL; weight 1.0 | High |  |
| PPAC_IDV_TC_017 | M3 Scoring | High | DS-03 documentDeprecated — weight 1.0 | Passport Scan hoàn tất | 1. Dùng loại tài liệu đã deprecated<br>2. Chạy scoring | Document type = deprecated | 2. `documentDeprecated` FAIL; weight 1.0 | High |  |
| PPAC_IDV_TC_018 | M3 Scoring | High | DS-04 mrzMissing — weight 1.0 | Passport Scan hoàn tất | 1. Passport có mảng vị trí MRZ rỗng<br>2. Chạy scoring | MRZ position array = [] | 2. `mrzMissing` FAIL; weight 1.0 | High |  |
| PPAC_IDV_TC_019 | M3 Scoring | High | DS-05 faceSimilarityLow — weight 1.0 | Face + Passport Scan hoàn tất | 1. Selfie không khớp ảnh passport<br>2. Chạy scoring | faceSimilarity = 0.30 (< threshold 0.75) | 2. `faceSimilarityLow` FAIL; weight 1.0; KHÔNG cộng faceSimilarityMedium | High |  |
| PPAC_IDV_TC_020 | M3 Scoring | High | DS-06 rfidInvalid — weight 1.0 | Passport có chip RFID | 1. Passport có chip RFID nhưng xác thực aa fail<br>2. Chạy scoring | detailsRFID.aa = fail | 2. `rfidInvalid` FAIL; weight 1.0 | High |  |
| PPAC_IDV_TC_021 | M3 Scoring | High | DS-07 citizenshipMismatch — weight 1.0 | Passport Scan hoàn tất, hồ sơ có citizenship | 1. Citizenship trên passport khác hồ sơ<br>2. Chạy scoring | Passport = GBR, profile = VNM | 2. `citizenshipMismatch` FAIL; weight 1.0 | High |  |
| PPAC_IDV_TC_022 | M3 Scoring | High | DS-08 passportExpired — weight 1.0 | Passport Scan hoàn tất | 1. Passport đã hết hạn<br>2. Chạy scoring | expiryDate = 01/01/2020 | 2. `passportExpired` FAIL; weight 1.0 | High |  |
| PPAC_IDV_TC_023 | M3 Scoring | Medium | DS-09 faceSimilarityMedium — weight 0.5 | Face + Passport Scan hoàn tất | 1. Mức khớp khuôn mặt ở vùng "không chắc chắn"<br>2. Chạy scoring | faceSimilarity = 0.60 (0.55 ≤ x < 0.75) | 2. `faceSimilarityMedium` FAIL; weight 0.5; KHÔNG cộng faceSimilarityLow | Medium |  |
| PPAC_IDV_TC_024 | M3 Scoring | Medium | DS-10 minimumAge — weight 0.5 | Passport Scan hoàn tất | 1. Applicant dưới tuổi tối thiểu<br>2. Chạy scoring | DOB → 16 tuổi (min 18) | 2. `minimumAge` FAIL; weight 0.5 | Medium |  |
| PPAC_IDV_TC_025 | M3 Scoring | Medium | DS-11 livenessElectronicDeviceFailed — weight 0.5 | Face Scan hoàn tất | 1. Liveness thực hiện qua màn hình thiết bị điện tử<br>2. Chạy scoring | Hiển thị qua màn hình điện tử | 2. `livenessElectronicDeviceFailed` FAIL; weight 0.5 | Medium |  |
| PPAC_IDV_TC_026 | M3 Scoring | Medium | DS-12 livenessBlackAndWhiteCopyFailed — weight 0.5 | Face Scan hoàn tất | 1. Liveness bằng bản copy trắng đen<br>2. Chạy scoring | Bản sao trắng đen | 2. `livenessBlackAndWhiteCopyFailed` FAIL; weight 0.5 | Medium |  |
| PPAC_IDV_TC_027 | M3 Scoring | Medium | DS-13 comparisonStatusInvalid — weight 0.25 | Passport Scan hoàn tất | 1. So khớp văn bản trên tài liệu sai (lệch tên)<br>2. Chạy scoring | textResult.comparisonStatus ≠ expected | 2. `comparisonStatusInvalid` FAIL; weight 0.25 | Medium |  |
| PPAC_IDV_TC_028 | M3 Scoring | Low | DS-14 docPositionMissing — weight 0.1 | Passport Scan hoàn tất | 1. Ảnh không detect được vị trí tài liệu<br>2. Chạy scoring | documentPosition = [] | 2. `docPositionMissing` FAIL; weight 0.1 | Low |  |
| PPAC_IDV_TC_029 | M3 Scoring | Low | DS-15 docPerspectiveBad — weight 0.1 | Passport Scan hoàn tất | 1. Passport chụp nghiêng, không thẳng góc<br>2. Chạy scoring | perspectiveTr ≠ expectedPerspective | 2. `docPerspectiveBad` FAIL; weight 0.1 | Low |  |
| PPAC_IDV_TC_030 | M3 Scoring | Low | DS-16 docAngleDeviation — weight 0.1 | Passport Scan hoàn tất | 1. Passport chụp ở góc lệch bất thường<br>2. Chạy scoring | objIntAngleDev ≠ expectedDev | 2. `docAngleDeviation` FAIL; weight 0.1 | Low |  |
| PPAC_IDV_TC_031 | M3 Scoring | Medium | DS-17 ageMismatch — weight 0.1 | Passport Scan hoàn tất | 1. Tuổi dự đoán lệch quá maxGapYears so với DOB<br>2. Chạy scoring | agePrediction 40 vs DOB-age 25 (gap 15 > 10) | 2. `ageMismatch` FAIL; weight 0.1 | Medium |  |
| PPAC_IDV_TC_032 | M3 Scoring | Medium | DS-18 imageType01Bad — weight 0.1 | Passport Scan hoàn tất | 1. Ảnh loại type 0/1 chất lượng không đạt<br>2. Chạy scoring | imageQualityList type0.result ≠ expected | 2. `imageType01Bad` FAIL; weight 0.1 | Medium |  |
| PPAC_IDV_TC_033 | M3 Scoring | High | BR-03 — Document Scoring chỉ chạy 1 lần / phiên | Passport Scan vừa hoàn tất | 1. Hoàn tất scoring lần 1<br>2. Kích lại quy trình scoring trong cùng phiên | applicant.valid@ppac.co.uk | 2. Hệ thống KHÔNG tính điểm lại; giữ nguyên kết quả lần 1 | High |  |
| PPAC_IDV_TC_034 | M3 Scoring | High | BR-09 — Total = tổng weight nhiều tiêu chí fail (Decision Table) | Passport Scan hoàn tất | 1. Cấu hình fail đồng thời nhiều tiêu chí<br>2. Chạy scoring<br>3. Kiểm tra Total | comparisonStatusInvalid (0.25) + docPositionMissing (0.1) + ageMismatch (0.1) | 3. Total Failed Weight = 0.45 (đúng tổng weight các tiêu chí fail) | High |  |
| PPAC_IDV_TC_035 | M3 Scoring | High | BR-09 — Tất cả tiêu chí PASS → Total = 0 | Passport Scan hoàn tất, hồ sơ hợp lệ hoàn toàn | 1. Xác minh với dữ liệu hợp lệ mọi tiêu chí<br>2. Chạy scoring | applicant.valid@ppac.co.uk + passport hợp lệ | 2. Không tiêu chí nào fail; Total Failed Weight = 0 | High |  |
| PPAC_IDV_TC_036 | M3 Scoring | Medium | BR-09 — Tiêu chí PASS không cộng điểm | Passport Scan hoàn tất | 1. Cấu hình 1 tiêu chí fail, các tiêu chí còn lại pass<br>2. Chạy scoring | Chỉ passportExpired fail (1.0), phần còn lại pass | 2. Chỉ cộng 1.0 của tiêu chí fail; các tiêu chí pass không cộng weight | Medium |  |

---

## M4 — Verification Status theo ngưỡng (BR-06 · BR-11 — Boundary Value Analysis)

> Ngưỡng: `< 0.5` → Waiting to Pass · `0.5 ≤ Total < 1.0` → Review Required · `≥ 1.0` → Failed.

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority | Status |
|---|---|---|---|---|---|---|---|---|---|
| PPAC_IDV_TC_037 | M4 Status | Medium | Total = 0 → Waiting to Pass | Scoring hoàn tất | 1. Xác minh hồ sơ hợp lệ hoàn toàn<br>2. Kiểm tra Verification Status | Total = 0.0 | 2. Verification Status = **Waiting to Pass** | Medium |  |
| PPAC_IDV_TC_038 | M4 Status | Medium | Total = 0.4 → Waiting to Pass (biên dưới < 0.5) | Scoring hoàn tất | 1. Fail 4 tiêu chí 0.1<br>2. Kiểm tra Status | docPositionMissing + docPerspectiveBad + docAngleDeviation + imageType01Bad = 0.4 | 2. Status = **Waiting to Pass** | Medium |  |
| PPAC_IDV_TC_039 | M4 Status | High | Total = 0.45 → Waiting to Pass (ngay dưới ngưỡng 0.5) | Scoring hoàn tất | 1. Fail combo = 0.45<br>2. Kiểm tra Status | comparisonStatusInvalid (0.25) + docPositionMissing (0.1) + ageMismatch (0.1) | 2. Status = **Waiting to Pass** | High |  |
| PPAC_IDV_TC_040 | M4 Status | High | Total = 0.5 → Review Required (biên dưới của Review) | Scoring hoàn tất | 1. Fail 1 tiêu chí 0.5<br>2. Kiểm tra Status | faceSimilarityMedium = 0.5 | 2. Status = **Review Required** | High |  |
| PPAC_IDV_TC_041 | M4 Status | Medium | Total = 0.9 → Review Required | Scoring hoàn tất | 1. Fail combo = 0.9<br>2. Kiểm tra Status | faceSimilarityMedium (0.5) + 4× 0.1 = 0.9 | 2. Status = **Review Required** | Medium |  |
| PPAC_IDV_TC_042 | M4 Status | High | Total = 0.95 → Review Required (khoảng từng bị hở trước khi sửa BR-06) | Scoring hoàn tất | 1. Fail combo = 0.95<br>2. Kiểm tra Status | faceSimilarityMedium (0.5) + comparisonStatusInvalid (0.25) + 2× 0.1 = 0.95 | 2. Status = **Review Required** (không rơi vào vùng không xác định) | High |  |
| PPAC_IDV_TC_043 | M4 Status | High | Total = 1.0 (hai tiêu chí 0.5) → Failed (biên dưới của Failed) | Scoring hoàn tất | 1. Fail 2 tiêu chí 0.5<br>2. Kiểm tra Status | faceSimilarityMedium (0.5) + minimumAge (0.5) = 1.0 | 2. Status = **Failed** | High |  |
| PPAC_IDV_TC_044 | M4 Status | High | BR-11 — 1 tiêu chí weight 1.0 fail → Failed ngay | Scoring hoàn tất | 1. Fail 1 tiêu chí 1.0<br>2. Kiểm tra Status | deepFakeInvalid = 1.0 | 2. Total ≥ 1.0; Status = **Failed** | High |  |
| PPAC_IDV_TC_045 | M4 Status | Medium | Total > 1.0 (nhiều tiêu chí nặng) → Failed | Scoring hoàn tất | 1. Fail nhiều tiêu chí<br>2. Kiểm tra Status | passportExpired (1.0) + faceSimilarityMedium (0.5) = 1.5 | 2. Status = **Failed** | Medium |  |

---

## M5 — Face Similarity loại trừ lẫn nhau (BR-10 · VR-08)

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority | Status |
|---|---|---|---|---|---|---|---|---|---|
| PPAC_IDV_TC_046 | M5 FaceSim | High | faceSimilarity < threshold → chỉ áp dụng faceSimilarityLow | Face + Passport Scan hoàn tất | 1. Cấu hình similarity thấp<br>2. Chạy scoring<br>3. Kiểm tra Failed Criteria | faceSimilarity = 0.30 (threshold 0.75) | 3. Chỉ `faceSimilarityLow` (1.0) xuất hiện; KHÔNG có faceSimilarityMedium | High |  |
| PPAC_IDV_TC_047 | M5 FaceSim | High | lowThreshold ≤ faceSimilarity < threshold → chỉ faceSimilarityMedium | Face + Passport Scan hoàn tất | 1. Cấu hình similarity vùng giữa<br>2. Chạy scoring<br>3. Kiểm tra Failed Criteria | faceSimilarity = 0.60 (0.55 ≤ x < 0.75) | 3. Chỉ `faceSimilarityMedium` (0.5); KHÔNG có faceSimilarityLow | High |  |
| PPAC_IDV_TC_048 | M5 FaceSim | Medium | faceSimilarity ≥ threshold → không áp dụng tiêu chí nào | Face + Passport Scan hoàn tất | 1. Cấu hình similarity cao<br>2. Chạy scoring | faceSimilarity = 0.90 (≥ 0.75) | 2. Không có faceSimilarityLow lẫn faceSimilarityMedium; không cộng weight | Medium |  |
| PPAC_IDV_TC_049 | M5 FaceSim | High | Không bao giờ cộng dồn cả hai tiêu chí face similarity (Negative) | Face + Passport Scan hoàn tất | 1. Chạy scoring ở nhiều mức similarity<br>2. Kiểm tra không xuất hiện đồng thời low + medium | Lần lượt 0.30 / 0.60 / 0.90 | 2. Tối đa 1 tiêu chí similarity được cộng; không có trường hợp cộng 1.5 (1.0 + 0.5) | High |  |

---

## M6 — Tạo Submission (New Check)

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority | Status |
|---|---|---|---|---|---|---|---|---|---|
| PPAC_IDV_TC_050 | M6 Submission | Critical | Chọn New Check → tạo Submission thành công (Happy Path) | Document Scoring đã hoàn tất & lưu | 1. Ở màn hình New Check, nhấn **New Check** | applicant.valid@ppac.co.uk | 1. Hệ thống tạo Submission và liên kết với kết quả đánh giá đã lưu; hiển thị xác nhận tạo thành công | Critical |  |
| PPAC_IDV_TC_051 | M6 Submission | High | AF-03 — Tạo Submission thất bại | Document Scoring đã hoàn tất | 1. Nhấn New Check khi backend lỗi tạo submission | Giả lập lỗi service | 1. Hiển thị lỗi; Submission KHÔNG được tạo; kết quả Document Scoring giữ nguyên; cho phép thao tác lại | High |  |
| PPAC_IDV_TC_052 | M6 Submission | High | BR-04/VR-03 — Không cho tạo Submission khi chưa có kết quả scoring | Passport Scan chưa hoàn tất / chưa có kết quả scoring | 1. Thử tạo Submission | Chưa chạy scoring | 1. Không cho tạo Submission (nút disable/chặn); yêu cầu hoàn tất Document Scoring trước | High |  |
| PPAC_IDV_TC_053 | M6 Submission | Critical | BR-05/VR-04 — Tạo Submission KHÔNG tính điểm lại | Document Scoring đã hoàn tất & lưu | 1. Ghi nhận Total/Status hiện có<br>2. Nhấn New Check tạo Submission<br>3. So sánh kết quả | Total đã lưu = 0.5 | 3. Submission dùng đúng kết quả đã lưu; KHÔNG kích hoạt Document Scoring lần 2; Total/Status không đổi | Critical |  |
| PPAC_IDV_TC_054 | M6 Submission | High | BR-07 — Submission liên kết đúng phiên xác minh | Có nhiều phiên xác minh khác nhau | 1. Tạo Submission cho phiên A<br>2. Kiểm tra dữ liệu liên kết | Phiên A (Total 0.95), phiên B (Total 0.1) | 2. Submission gắn đúng kết quả của phiên A (0.95), không lẫn sang phiên B | High |  |
| PPAC_IDV_TC_055 | M6 Submission | Medium | Retry tạo Submission sau khi thất bại → thành công | Vừa tạo Submission thất bại (AF-03) | 1. Nhấn New Check lại khi service đã phục hồi | applicant.valid@ppac.co.uk | 1. Submission được tạo thành công; liên kết đúng kết quả scoring đã lưu | Medium |  |

---

## M7 — Admin Portal & Output

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority | Status |
|---|---|---|---|---|---|---|---|---|---|
| PPAC_IDV_TC_056 | M7 Admin | High | BR-08 — Admin xem đầy đủ thông tin đánh giá | Submission đã được tạo | 1. Admin đăng nhập Admin Portal<br>2. Mở Submission vừa tạo | Submission ID của phiên hợp lệ | 2. Hiển thị Decision Categories, Failed Criteria, Failed Weight, Total Failed Weight, Verification Status | High |  |
| PPAC_IDV_TC_057 | M7 Admin | High | VR-06 — Dữ liệu hiển thị đầy đủ & nhất quán với kết quả đã lưu | Submission đã tạo với Total/Status đã biết | 1. Mở Submission trên Admin Portal<br>2. Đối chiếu với kết quả scoring đã lưu | Total = 0.95, Status = Review Required | 2. Số liệu hiển thị khớp chính xác kết quả đã lưu; Failed Weight từng tiêu chí cộng lại đúng Total | High |  |
| PPAC_IDV_TC_058 | M7 Admin | Medium | Output — Submission lưu tối thiểu các trường bắt buộc | Submission đã tạo | 1. Kiểm tra dữ liệu Submission (DB/API) | Submission ID của phiên hợp lệ | 1. Có đủ: Submission ID, Passport Information, Decision Categories, Failed Criteria, Failed Weight, Total Failed Weight, Verification Status, Evaluation Timestamp | Medium |  |
| PPAC_IDV_TC_059 | M7 Admin | Medium | BR-08 — Failed Criteria hiển thị khớp các tiêu chí đã fail | Submission có nhiều tiêu chí fail | 1. Mở Submission<br>2. Đối chiếu danh sách Failed Criteria với các DS đã fail | passportExpired (1.0) + comparisonStatusInvalid (0.25) | 2. Failed Criteria liệt kê đúng passportExpired + comparisonStatusInvalid với weight tương ứng | Medium |  |

---

## M8 — End-to-End & Edge Cases

| TC ID | Module | Risk Level | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority | Status |
|---|---|---|---|---|---|---|---|---|---|
| PPAC_IDV_TC_060 | M8 E2E | Critical | Luồng chính hoàn chỉnh (Face → Passport → Scoring → New Check → Submission → Admin) | Đăng nhập, quyền camera OK, Regula sẵn sàng | 1. Face Scan<br>2. Passport Scan<br>3. Hệ thống Document Scoring + tính Total + xác định Status + lưu<br>4. Nhấn New Check tạo Submission<br>5. Admin mở Submission | applicant.valid@ppac.co.uk + passport hợp lệ | 1–5. Toàn bộ luồng thành công theo Main Flow; Admin xem được kết quả đánh giá | Critical |  |
| PPAC_IDV_TC_061 | M8 Edge | High | Pre-condition — Chưa cấp quyền camera | Đã đăng nhập, quyền camera bị từ chối | 1. Bắt đầu quy trình xác minh | Camera permission = Denied | 1. Không mở được camera Face/Passport Scan; hiển thị yêu cầu cấp quyền; quy trình không tiếp tục | High |  |
| PPAC_IDV_TC_062 | M8 Edge | High | Pre-condition — Regula SDK không hoạt động | Đã đăng nhập, Regula SDK lỗi/không khởi tạo | 1. Chọn Passport Scan | Regula SDK unavailable | 1. Hiển thị lỗi; không thực hiện quét/trích xuất; không Document Scoring | High |  |
| PPAC_IDV_TC_063 | M8 Edge | High | Mất kết nối mạng khi Document Scoring / tạo Submission | Passport Scan hoàn tất | 1. Bật Airplane Mode<br>2. Kích hoạt Document Scoring / New Check | Airplane Mode ON | 2. Hiển thị lỗi network; không tạo kết quả/Submission treo; có tùy chọn Retry; dữ liệu đã lưu không hỏng | High |  |
| PPAC_IDV_TC_064 | M8 Edge | Medium | DS-08 BVA — Passport hết hạn đúng hôm nay vs còn hạn 1 ngày | Passport Scan hoàn tất | 1. Scoring với passport hết hạn hôm nay<br>2. Scoring với passport còn hạn 1 ngày | expiryDate = hôm nay / hôm nay+1 | 1. Hết hạn hôm nay → `passportExpired` FAIL (1.0)<br>2. Còn hạn 1 ngày → PASS, không cộng weight | Medium |  |
| PPAC_IDV_TC_065 | M8 Edge | Medium | DS-10 BVA — Đúng tuổi tối thiểu vs thiếu 1 ngày | Passport Scan hoàn tất | 1. Scoring với applicant tròn 18 tuổi<br>2. Scoring với applicant thiếu 1 ngày đủ 18 | DOB → đúng 18 / 17 tuổi 364 ngày | 1. Đủ 18 → PASS<br>2. Thiếu 1 ngày → `minimumAge` FAIL (0.5) | Medium |  |

---

## Ghi chú & Điểm mờ cần xác nhận (Ambiguities)

1. **Ngưỡng `faceSimilarity`** (`threshold`, `lowThreshold`) — req không nêu giá trị cụ thể; TC đang giả định `0.75` / `0.55`. Cần BA/dev xác nhận để chốt test data cho M5 và DS-05/DS-09.
2. **`minimumAge` và `maxGapYears`** — chưa có giá trị chính thức trong req (giả định 18 tuổi và 10 năm). Ảnh hưởng TC024, TC031, TC065.
3. **Giới hạn số lần retry** Face Scan / Passport Scan — req chỉ ghi "cho phép quét lại", chưa nêu có giới hạn số lần / khoá tạm thời không.
4. **Timeout** cho Face/Passport Scan, Document Scoring, tạo Submission — chưa định nghĩa; TC edge (TC063) đang giả định hành vi báo lỗi + Retry.
5. **Hành vi mất kết nối** giữa chừng (đặc biệt khi scoring đã tính nhưng chưa lưu) — cần xác nhận có cơ chế lưu tạm / phục hồi phiên không.
6. **Decision Categories** — req liệt kê là output nhưng chưa định nghĩa cách nhóm 18 tiêu chí (Face / Document / Identity...). Cần xác nhận để test hiển thị Admin (TC056–TC059) chính xác.
7. **RFID không có chip** (DS-06) — điều kiện chỉ tính khi "chip present"; cần xác nhận passport không có chip thì tiêu chí này bỏ qua hay PASS mặc định.
8. **Cận trên "Review Required"** — theo BR-06 đã sửa là `< 1.0`; nếu tồn tại giá trị như 0.99 do cấu hình weight khác trong tương lai, vẫn phải là Review Required (đã phủ tinh thần ở TC042).
9. **Đơn vị/định dạng Evaluation Timestamp** và timezone lưu trong Output — chưa định nghĩa (ảnh hưởng TC058).
