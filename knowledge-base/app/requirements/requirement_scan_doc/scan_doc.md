# UC-01: Xác minh Passport và tạo Submission

## 1. Thông tin Use Case

**Mã Use Case:** UC-01

**Tên Use Case:** Xác minh Passport và tạo Submission

**Module:** Identity Verification

**Độ ưu tiên:** High

---

# 2. Tác nhân (Actors)

### Tác nhân chính

* Người dùng (Applicant)

### Tác nhân phụ

* Mobile Application
* Regula SDK
* Face Verification Service
* Document Scoring Engine
* Backend Service
* Admin Portal

---

# 3. Mục tiêu (Objective)

Cho phép người dùng hoàn tất quá trình xác minh danh tính bằng Face Scan và Passport Scan.

Sau khi quá trình xác minh hoàn tất, hệ thống sẽ tính điểm đánh giá tài liệu, xác định trạng thái xác minh và lưu kết quả.

Khi người dùng chọn **New Check**, hệ thống sẽ tạo một Submission để Admin có thể xem lại toàn bộ kết quả đánh giá đã được tính trước đó.

---

# 4. Điều kiện kích hoạt (Trigger)

Người dùng bắt đầu quy trình xác minh danh tính.

---

# 5. Tiền điều kiện (Pre-conditions)

1. Người dùng đã đăng ký tài khoản thành công.
2. Người dùng đã đăng nhập vào ứng dụng.
3. Thiết bị đã cấp quyền sử dụng camera.
4. Regula SDK hoạt động bình thường.
5. Hệ thống sẵn sàng thực hiện Face Verification và Passport Verification.

---

# 6. Luồng chính (Main Flow)

| Bước | Người dùng                                | Hệ thống                                                                                 |
| ---- | ----------------------------------------- | ---------------------------------------------------------------------------------------- |
| 1    | Bắt đầu quy trình xác minh danh tính.     | Hiển thị màn hình Face Scan.                                                             |
| 2    | Thực hiện Face Scan.                      | Xác minh khuôn mặt thành công.                                                           |
| 3    | Chọn chức năng Passport Scan.             | Mở camera để quét Passport.                                                              |
| 4    | Quét Passport.                            | Regula kiểm tra chất lượng ảnh, nhận diện Passport và trích xuất thông tin.              |
| 5    | -                                         | Hệ thống hoàn tất xác minh Passport.                                                     |
| 6    | -                                         | Hệ thống thực hiện Document Scoring dựa trên kết quả Face Scan và Passport Scan.         |
| 7    | -                                         | Hệ thống tính Total Failed Weight.                                                       |
| 8    | -                                         | Hệ thống xác định Verification Status theo Business Rules.                               |
| 9    | -                                         | Hệ thống lưu kết quả đánh giá của phiên xác minh.                                        |
| 10   | Hệ thống hiển thị màn hình **New Check**. | Người dùng có thể tạo Submission.                                                        |
| 11   | Người dùng chọn **New Check**.            | Hệ thống tạo Submission và liên kết với kết quả đánh giá đã lưu.                         |
| 12   | Admin mở Submission trên Admin Portal.    | Hiển thị Decision Categories, Failed Weight, Total Failed Weight và Verification Status. |

---

# 7. Luồng phụ / Ngoại lệ (Alternative / Exception Flows)

## AF-01 - Face Scan thất bại

**Tại bước 2**

Điều kiện:

* Không nhận diện được khuôn mặt.
* Chất lượng ảnh không đạt.
* Face Verification thất bại.

Kết quả:

* Hiển thị thông báo lỗi.
* Cho phép người dùng thực hiện Face Scan lại.
* Không chuyển sang bước Passport Scan.

---

## AF-02 - Passport Scan thất bại

**Tại bước 4**

Điều kiện:

* Không nhận diện được Passport.
* Ảnh bị mờ.
* Ảnh bị chói.
* Passport bị cắt góc.
* Không đọc được MRZ.
* OCR thất bại.

Kết quả:

* Hiển thị thông báo lỗi.
* Cho phép người dùng quét lại Passport.
* Không thực hiện Document Scoring.

---

## AF-03 - Tạo Submission thất bại

**Tại bước 11**

Kết quả:

* Hiển thị thông báo lỗi.
* Submission không được tạo.
* Kết quả Document Scoring vẫn được giữ nguyên.
* Người dùng có thể thực hiện lại thao tác tạo Submission.

---

# 8. Hậu điều kiện (Post-conditions)

## Thành công

* Face Scan hoàn tất.
* Passport Scan hoàn tất.
* Document Scoring được thực hiện thành công.
* Total Failed Weight được tính.
* Verification Status được xác định.
* Submission được tạo.
* Admin có thể xem kết quả đánh giá.

## Thất bại

* Submission không được tạo hoặc quá trình xác minh chưa hoàn tất.
* Không thay đổi kết quả Document Scoring đã được lưu trước đó.

---

# 9. Business Rules

### BR-01

Face Scan phải hoàn thành thành công trước khi thực hiện Passport Scan.

---

### BR-02

Passport Scan phải hoàn thành thành công trước khi thực hiện Document Scoring.

---

### BR-03

Document Scoring chỉ được thực hiện **một lần** cho mỗi phiên xác minh.

---

### BR-04

Document Scoring phải được hoàn tất trước khi người dùng tạo Submission.

---

### BR-05

Việc tạo Submission **không được thực hiện tính điểm lại**.

Submission chỉ sử dụng kết quả Document Scoring đã được lưu.

---

### BR-06

Verification Status được xác định theo Total Failed Weight như sau:

| Total Failed Weight   | Verification Status |
| --------------------- | ------------------- |
| < 0.5                 | Waiting to Pass     |
| 0.5 ≤ Total < 1.0     | Review Required     |
| ≥ 1.0                 | Failed              |

> **Lưu ý (bổ sung):** Khoảng `Review Required` được định nghĩa lại thành `0.5 ≤ Total < 1.0` (thay cho `0.5 – 0.9`) để không còn "khoảng trống" cho các giá trị như 0.95 (vd 0.25 + 0.7). Mọi giá trị `≥ 1.0` đều là **Failed**.

---

### BR-07

Submission phải liên kết đúng với kết quả Document Scoring của phiên xác minh tương ứng.

---

### BR-08

Sau khi Submission được tạo, Admin phải xem được:

* Decision Categories
* Failed Criteria
* Failed Weight
* Total Failed Weight
* Verification Status

---

### BR-09

**Total Failed Weight** được tính bằng **tổng Failed Weight của tất cả tiêu chí bị FAIL** trong phiên xác minh (xem Mục 10).

Tiêu chí PASS không cộng điểm.

---

### BR-10

Hai tiêu chí `faceSimilarityLow` (weight 1.0) và `faceSimilarityMedium` (weight 0.5) **loại trừ lẫn nhau** theo ngưỡng `faceSimilarity`:

* `faceSimilarity < threshold` → chỉ áp dụng `faceSimilarityLow`.
* `lowThreshold ≤ faceSimilarity < threshold` → chỉ áp dụng `faceSimilarityMedium`.
* `faceSimilarity ≥ threshold` → không áp dụng tiêu chí nào.

**Không được cộng dồn** cả hai tiêu chí này trong cùng một phiên.

---

### BR-11

Bất kỳ tiêu chí nào có **Failed Weight = 1.0** khi FAIL sẽ làm Total Failed Weight `≥ 1.0` → Verification Status = **Failed** (bất kể các tiêu chí khác).

---

# 10. Document Scoring — Decision Categories & Weights

Danh sách tiêu chí chấm điểm được cấu hình trong `verification-configs` (`thresholds`). Mỗi tiêu chí khi **FAIL** sẽ cộng một **Failed Weight** vào **Total Failed Weight** (theo BR-09).

## 10.1. Danh sách tiêu chí và trọng số

| Mã    | Key                             | Failed Weight | Điều kiện fail (kỹ thuật)                                                      | Tiêu chí (nghiệp vụ)                                                          |
| ----- | ------------------------------- | ------------- | ------------------------------------------------------------------------------ | ---------------------------------------------------------------------------- |
| DS-01 | deepFakeInvalid                 | 1.0           | Mọi `deepFake.className` phải bằng `expectedClassName`.                         | Khuôn mặt có dấu hiệu bị AI tạo ra hoặc chỉnh sửa (deepfake).                 |
| DS-02 | livenessFailed                  | 1.0           | `livenessStatus` phải bằng `expectedStatus`.                                    | Kiểm tra liveness thất bại — khuôn mặt có thể là ảnh, video hoặc mặt nạ.      |
| DS-03 | documentDeprecated              | 1.0           | Loại tài liệu không được là "deprecated".                                       | Tài liệu đã lỗi thời hoặc không còn được chấp nhận.                          |
| DS-04 | mrzMissing                      | 1.0           | Mảng vị trí MRZ phải khác rỗng.                                                 | Vùng MRZ (Machine Readable Zone) bị thiếu hoặc không đọc được.               |
| DS-05 | faceSimilarityLow               | 1.0           | `faceSimilarity < threshold`.                                                   | Khuôn mặt trong ảnh selfie không khớp với ảnh trên tài liệu.                 |
| DS-06 | rfidInvalid                     | 1.0           | `detailsRFID.aa` phải success nếu có chip RFID.                                 | Chip RFID trong tài liệu không vượt qua xác minh.                            |
| DS-07 | citizenshipMismatch             | 1.0           | Quốc tịch trên tài liệu phải khớp quốc tịch hồ sơ.                              | Quốc tịch không khớp với hồ sơ.                                              |
| DS-08 | passportExpired                 | 1.0           | Passport không được hết hạn.                                                    | Passport đã hết hạn.                                                          |
| DS-09 | faceSimilarityMedium            | 0.5           | `lowThreshold ≤ faceSimilarity < threshold` (xem BR-10).                        | Mức độ khớp khuôn mặt không chắc chắn — không rõ khớp hay không khớp.         |
| DS-10 | minimumAge                      | 0.5           | Không đạt yêu cầu tuổi tối thiểu.                                               | Người dùng chưa đủ tuổi yêu cầu.                                             |
| DS-11 | livenessElectronicDeviceFailed  | 0.5           | Phát hiện dùng thiết bị điện tử trong lúc xác minh liveness.                    | Người dùng có thể đang dùng thiết bị điện tử hiển thị trong quá trình xác minh. |
| DS-12 | livenessBlackAndWhiteCopyFailed | 0.5           | Phát hiện dùng bản sao trắng đen trong lúc xác minh liveness.                   | Người dùng có thể đã dùng bản sao trắng đen thay vì chụp trực tiếp.          |
| DS-13 | comparisonStatusInvalid         | 0.25          | `textResult.comparisonStatus` phải bằng `expectedComparison`.                   | So khớp văn bản trên tài liệu thất bại (vd: sai lệch tên).                   |
| DS-14 | docPositionMissing              | 0.1           | Mảng `documentPosition` phải khác rỗng.                                         | Tài liệu không được phát hiện đúng trong ảnh.                                |
| DS-15 | docPerspectiveBad               | 0.1           | `documentPosition.perspectiveTr` phải bằng `expectedPerspective`.              | Tài liệu bị nghiêng hoặc không chụp thẳng góc.                               |
| DS-16 | docAngleDeviation               | 0.1           | `documentPosition.objIntAngleDev` phải bằng `expectedDev`.                     | Tài liệu chụp ở góc bất thường hoặc bị lệch.                                 |
| DS-17 | ageMismatch                     | 0.1           | `abs(agePrediction − tuổi tính từ DOB) ≤ maxGapYears`.                          | Tuổi dự đoán không khớp với tuổi trên tài liệu.                              |
| DS-18 | imageType01Bad                  | 0.1           | Với type 0 hoặc 1 trong `imageQualityList`, `result` phải bằng `expectedResult`. | Một hoặc nhiều loại ảnh bắt buộc có chất lượng không đạt.                    |

## 10.2. Tổng hợp theo trọng số

| Failed Weight | Số tiêu chí | Ý nghĩa                                                          |
| ------------- | ----------- | --------------------------------------------------------------- |
| 1.0           | 8           | Lỗi nghiêm trọng — chỉ cần 1 tiêu chí fail → **Failed** (BR-11). |
| 0.5           | 4           | 1 tiêu chí → Review Required; 2 tiêu chí → Failed.              |
| 0.25          | 1           | Cộng dồn với các tiêu chí khác.                                 |
| 0.1           | 5           | Lỗi chất lượng ảnh / lệch góc nhẹ.                              |

---

# 11. Validation Rules

| Mã    | Validation Rule                                                                                               | Kết quả mong đợi                                       |
| ----- | ------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------ |
| VR-01 | Chỉ cho phép Passport Scan sau khi Face Scan thành công.                                                      | Không cho phép bỏ qua Face Scan.                       |
| VR-02 | Chỉ thực hiện Document Scoring khi Passport Scan thành công.                                                  | Nếu Passport Scan thất bại thì không tính điểm.        |
| VR-03 | Chỉ cho phép tạo Submission sau khi Document Scoring hoàn tất.                                                | Nếu chưa có kết quả đánh giá thì không tạo Submission. |
| VR-04 | Việc tạo Submission không được kích hoạt Document Scoring lần thứ hai.                                        | Sử dụng kết quả đã lưu.                                |
| VR-05 | Verification Status phải được xác định theo Business Rules.                                                   | Trạng thái hiển thị chính xác.                         |
| VR-06 | Submission phải hiển thị đúng Decision Categories, Failed Weight, Total Failed Weight và Verification Status. | Dữ liệu hiển thị đầy đủ và nhất quán.                  |
| VR-07 | Total Failed Weight phải bằng tổng Failed Weight của các tiêu chí fail (Mục 10).                              | Không cộng điểm tiêu chí PASS; không cộng dồn thiếu/thừa. |
| VR-08 | Chỉ áp dụng đúng một trong `faceSimilarityLow` / `faceSimilarityMedium` theo ngưỡng similarity (BR-10).       | Không cộng dồn cả hai tiêu chí face similarity.        |

---

# 12. Dữ liệu đầu ra (Output)

Sau khi hoàn tất, Submission phải lưu tối thiểu các thông tin sau:

* Submission ID
* Passport Information
* Decision Categories
* Failed Criteria
* Failed Weight
* Total Failed Weight
* Verification Status
* Evaluation Timestamp
