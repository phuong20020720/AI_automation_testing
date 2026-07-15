# Use Case – Ballycommon Worker Onboarding

## 1. Overview

Cho phép worker hoàn thành quy trình onboarding Ballycommon.

Hệ thống sẽ hiển thị các form khác nhau dựa trên:

* Sector được chọn

  * Construction
  * Rail
* Payroll Company được chọn

  * CWG – Provider for CIS
  * Workwell Contractor Solutions – Provider for PAYE

Mục tiêu của onboarding là thu thập đầy đủ thông tin worker trước khi hoàn tất đăng ký.

---

# 2. Actors

* Worker

---

# 3. Preconditions

* Worker truy cập onboarding link Ballycommon.
* Hệ thống có thể truy xuất dữ liệu:

  * Consultant từ Infinity API
  * Trade từ Infinity API
  * Qualification từ Infinity API

---

# 4. Main Flow

## MF-01 – Select Sector

| Step | Actor/System | Action                                    |
| ---- | ------------ | ----------------------------------------- |
| 1    | Worker       | Chọn Sector                               |
| 2    | System       | Hiển thị các trường tương ứng theo Sector |
| 3    | Worker       | Tiếp tục tới form Your Details            |

---

## MF-02 – Complete Your Details

| Step | Actor/System | Action                           |
| ---- | ------------ | -------------------------------- |
| 1    | Worker       | Nhập Personal Information        |
| 2    | Worker       | Nhập Address Information         |
| 3    | Worker       | Chọn Consultant                  |
| 4    | Worker       | Chọn Trade                       |
| 5    | Worker       | Chọn Qualification               |
| 6    | Worker       | Nhập Next of Kin Information     |
| 7    | Worker       | Chọn Payroll Company             |
| 8    | Worker       | Nhập Sentinel Number (Rail only) |
| 9    | Worker       | Click Next                       |
| 10   | System       | Validate dữ liệu                 |
| 11   | System       | Điều hướng tới References        |

---

## MF-03 – Complete References

| Step | Actor/System | Action                                             |
| ---- | ------------ | -------------------------------------------------- |
| 1    | Worker       | Hoàn thành Referee 1                               |
| 2    | Worker       | Hoàn thành Referee 2                               |
| 3    | Worker       | Click Next                                         |
| 4    | System       | Validate dữ liệu                                   |
| 5    | System       | Điều hướng tới form tiếp theo theo Payroll Company |

---

## MF-04 – CWG (CIS) Journey

| Step | Actor/System | Action                    |
| ---- | ------------ | ------------------------- |
| 1    | System       | Hiển thị Declaration Form |
| 2    | Worker       | Tick Declaration          |
| 3    | Worker       | Submit onboarding         |
| 4    | System       | Lưu dữ liệu onboarding    |

---

## MF-05 – Workwell Contractor Solutions (PAYE) Journey

| Step | Actor/System | Action                                   |
| ---- | ------------ | ---------------------------------------- |
| 1    | System       | Hiển thị Medical Self-Certification      |
| 2    | Worker       | Trả lời toàn bộ câu hỏi                  |
| 3    | System       | Hiển thị Sentinel Scheme Contract        |
| 4    | Worker       | Chấp nhận Contract                       |
| 5    | System       | Hiển thị PPE Form                        |
| 6    | Worker       | Hoàn thành PPE Form                      |
| 7    | System       | Hiển thị Pre-Deployment Form             |
| 8    | Worker       | Hoàn thành Pre-Deployment Form           |
| 9    | System       | Hiển thị Lost Sentinel Cards Declaration |
| 10   | Worker       | Tick xác nhận                            |
| 11   | System       | Hiển thị Final Declaration               |
| 12   | Worker       | Tick xác nhận                            |
| 13   | Worker       | Submit onboarding                        |
| 14   | System       | Lưu dữ liệu onboarding                   |

---

# 5. Alternative Flow

## AF-01 – Sector = Rail

| Step | Actor/System | Action                               |
| ---- | ------------ | ------------------------------------ |
| 1    | Worker       | Chọn Rail                            |
| 2    | System       | Hiển thị Sentinel Number             |
| 3    | System       | Đánh dấu Sentinel Number là bắt buộc |

---

## AF-02 – Sector = Construction

| Step | Actor/System | Action                         |
| ---- | ------------ | ------------------------------ |
| 1    | Worker       | Chọn Construction              |
| 2    | System       | Không hiển thị Sentinel Number |

---

## AF-03 – Medical Questions All Answered NO

| Step | Actor/System | Action                         |
| ---- | ------------ | ------------------------------ |
| 1    | Worker       | Chọn NO cho tất cả câu hỏi     |
| 2    | System       | Hiển thị Confirmation Checkbox |
| 3    | Worker       | Tick xác nhận                  |

---

## AF-04 – Medical Questions Contain YES

| Step | Actor/System | Action                               |
| ---- | ------------ | ------------------------------------ |
| 1    | Worker       | Chọn YES cho ít nhất một câu hỏi     |
| 2    | System       | Không hiển thị Confirmation Checkbox |

---

## AF-05 – Safety Critical Certifications = YES

| Step | Actor/System | Action                     |
| ---- | ------------ | -------------------------- |
| 1    | Worker       | Chọn YES                   |
| 2    | System       | Hiển thị Additional Fields |
| 3    | Worker       | Nhập thông tin bổ sung     |

---

## AF-06 – Safety Critical Certifications = NO

| Step | Actor/System | Action                           |
| ---- | ------------ | -------------------------------- |
| 1    | Worker       | Chọn NO                          |
| 2    | System       | Không hiển thị Additional Fields |

---

# 6. Exception Flow

## EF-01 – Required Field Empty

| Step | Actor/System | Action                               |
| ---- | ------------ | ------------------------------------ |
| 1    | Worker       | Để trống trường bắt buộc             |
| 2    | Worker       | Click Next/Submit                    |
| 3    | System       | Hiển thị "Please fill in this field" |

---

## EF-02 – Invalid Email

| Step | Actor/System | Action                                        |
| ---- | ------------ | --------------------------------------------- |
| 1    | Worker       | Nhập email sai định dạng                      |
| 2    | Worker       | Click Next                                    |
| 3    | System       | Hiển thị "Please enter a valid email address" |

---

## EF-03 – Invalid National Insurance Number

| Step | Actor/System | Action                                                                                    |
| ---- | ------------ | ----------------------------------------------------------------------------------------- |
| 1    | Worker       | Nhập NIN sai định dạng                                                                    |
| 2    | Worker       | Click Next                                                                                |
| 3    | System       | Hiển thị "Please enter the right format: 2 letters, 6 numbers, 1 letter (e.g. AA999999A)" |

---

## EF-04 – Candidate Under 16 Years Old

| Step | Actor/System | Action                                                                     |
| ---- | ------------ | -------------------------------------------------------------------------- |
| 1    | Worker       | Chọn ngày sinh dưới 16 tuổi                                                |
| 2    | System       | Hiển thị lỗi "Only individuals aged 16 or above are eligible to register." |

---

## EF-05 – Referee Type Empty

| Step | Actor/System | Action                        |
| ---- | ------------ | ----------------------------- |
| 1    | Worker       | Không chọn Reference Type     |
| 2    | Worker       | Click Next                    |
| 3    | System       | Hiển thị "Please select here" |

---

## EF-06 – Invalid Referee Phone Number

| Step | Actor/System | Action                                       |
| ---- | ------------ | -------------------------------------------- |
| 1    | Worker       | Nhập số điện thoại không hợp lệ              |
| 2    | Worker       | Click Next                                   |
| 3    | System       | Hiển thị "Please enter a valid phone number" |

---

## EF-07 – Medical Question Not Answered

| Step | Actor/System | Action                                 |
| ---- | ------------ | -------------------------------------- |
| 1    | Worker       | Bỏ trống một hoặc nhiều câu hỏi        |
| 2    | Worker       | Click Next                             |
| 3    | System       | Hiển thị "Please answer this question" |

---

# 7. Business Rules

| ID    | Rule                                                                                   |
| ----- | -------------------------------------------------------------------------------------- |
| BR-01 | Worker phải chọn Sector trước khi tiếp tục                                             |
| BR-02 | Rail phải hiển thị Sentinel Number                                                     |
| BR-03 | Sentinel Number là bắt buộc khi Sector = Rail                                          |
| BR-04 | Construction không hiển thị Sentinel Number                                            |
| BR-05 | Worker phải chọn Payroll Company                                                       |
| BR-06 | CWG chỉ yêu cầu Your Details, References, Declaration                                  |
| BR-07 | Workwell yêu cầu toàn bộ các form PAYE                                                 |
| BR-08 | Hệ thống phải thu thập đúng 2 References                                               |
| BR-09 | Mỗi Reference phải thuộc một trong hai loại: Personal hoặc Company                     |
| BR-10 | Consultant phải được lấy từ Infinity API                                               |
| BR-11 | Trade phải được lấy từ Infinity API                                                    |
| BR-12 | Qualification phải được lấy từ Infinity API                                            |
| BR-13 | Medical Self-Certification yêu cầu trả lời tất cả câu hỏi                              |
| BR-14 | Confirmation Checkbox chỉ hiển thị khi tất cả câu trả lời là NO                        |
| BR-15 | Checkbox xác nhận không được hiển thị nếu có ít nhất một câu trả lời YES               |
| BR-16 | Additional Pre-Deployment Fields chỉ hiển thị khi Safety Critical Certifications = YES |
| BR-17 | Lost Sentinel Card Declaration phải được chấp nhận trước khi tiếp tục                  |
| BR-18 | Final Declaration phải được chấp nhận trước khi submit                                 |
| BR-19 | Worker phải từ 16 tuổi trở lên mới được đăng ký                                        |

---

# 8. Validation

| ID     | Validation                                           |
| ------ | ---------------------------------------------------- |
| VAL-01 | Tất cả field trong Your Details là bắt buộc          |
| VAL-02 | Tất cả field trong References là bắt buộc            |
| VAL-03 | Sentinel Number bắt buộc khi Sector = Rail           |
| VAL-04 | Email phải đúng định dạng                            |
| VAL-05 | National Insurance Number phải theo format AA999999A |
| VAL-06 | Worker phải từ 16 tuổi trở lên                       |
| VAL-07 | Referee Type bắt buộc                                |
| VAL-08 | Referee Contact Number phải đúng định dạng           |
| VAL-09 | Toàn bộ Medical Questions phải được trả lời          |
| VAL-10 | Declaration Checkbox bắt buộc                        |
| VAL-11 | Contract Acceptance Checkbox bắt buộc                |
| VAL-12 | Lost Sentinel Card Checkbox bắt buộc                 |
| VAL-13 | Final Declaration Checkbox bắt buộc                  |

---

# 9. Acceptance Criteria

| ID    | Acceptance Criteria                                                  |
| ----- | -------------------------------------------------------------------- |
| AC-01 | User có thể chọn Construction hoặc Rail                              |
| AC-02 | Rail hiển thị Sentinel Number                                        |
| AC-03 | Construction không hiển thị Sentinel Number                          |
| AC-04 | User có thể chọn Payroll Company                                     |
| AC-05 | CWG hiển thị đúng 3 form                                             |
| AC-06 | Workwell hiển thị đúng 8 form                                        |
| AC-07 | User phải hoàn thành đúng 2 References                               |
| AC-08 | Medical Questions yêu cầu trả lời đầy đủ                             |
| AC-09 | Confirmation Checkbox chỉ hiển thị khi tất cả câu trả lời là NO      |
| AC-10 | Additional Pre-Deployment Fields chỉ hiển thị khi chọn YES           |
| AC-11 | User không thể submit nếu thiếu Declaration bắt buộc                 |
| AC-12 | User không thể submit nếu thiếu dữ liệu bắt buộc                     |
| AC-13 | User dưới 16 tuổi không thể đăng ký                                  |
| AC-14 | Hệ thống lưu thành công onboarding sau khi hoàn tất toàn bộ các bước |

---

# 10. Dependencies

* Infinity API – Consultant
* Infinity API – Trade
* Infinity API – Qualification
* Worker Onboarding Service
* Validation Service
* Payroll Provider Configuration Service
* Form Submission Service
* Declaration Management Service
