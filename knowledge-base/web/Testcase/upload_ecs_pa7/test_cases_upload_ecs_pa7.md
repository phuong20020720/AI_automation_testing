# Test Cases — Upload ECS / PA7 in Worker Management

**Module:** Worker Management → Worker Detail → Document Upload (ECS / PA7)
**URL:** https://uat-client-dashboard.sandbox-compliant101.co.uk/en/worker-management
**Test account (Manager):** `ppac_mun@yopmail.com` / `L1W82sMLoO`
**Source requirement:**
- Manager can directly upload **ECS** and **PA7** documents from Worker Management.
- Uploaded files must be visible and accessible in Worker Details / History.

**Business rules (extracted from whiteboard `WorkfollowECS.jpeg`):**

- **Subcontractor — common case:** Upload ECS/PA7 → status **unchanged**.
- **ECS flow — wrong worker name:** Rename only (no re-upload) → status **unchanged**.
- **ECS flow — wrong document → new upload → OCR success:** Replace file → status **unchanged**.
- **ECS flow — wrong document → new upload → OCR fail:** Replace file → status **Pending (Recheck)**.
- **Worker currently in `Pending Info` and ECS uploaded:** New upload → status **Pending (Recheck)**.

---

## Test Cases (QUICK mode)

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|
| PPAC_WM_UPL_TC_001 | Access & Permission | Manager sees Upload ECS/PA7 buttons in Worker Detail | Logged in as Manager `ppac_mun@yopmail.com` | 1. Go to Worker Management<br>2. Click any worker to open Worker Detail<br>3. Observe the Documents area (ECS / PA7) | Any worker on page 1 | **Upload ECS** and **Upload PA7** buttons are visible and enabled in Worker Detail | Critical |
| PPAC_WM_UPL_TC_002 | Access & Permission | Non-manager user (Viewer/Worker) cannot upload | Logged in with a non-manager account | 1. Open Worker Detail of any worker<br>2. Observe the Documents area | Account with role = Viewer | Upload ECS/PA7 buttons are **hidden** or disabled; calling the API directly must return **HTTP 403** | Critical |
| PPAC_WM_UPL_TC_003 | Access & Permission | Unauthenticated user cannot reach the upload endpoint | Fully logged out (cookies/localStorage cleared) | 1. Call the API `POST /worker/{id}/document/ecs` directly via DevTools/Postman | Valid PDF file | Server returns **HTTP 401 Unauthorized**; no record created in DB | Critical |
| PPAC_WM_UPL_TC_004 | Upload UI | Open Upload ECS dialog from Worker Detail | Already on Worker Detail of any worker | 1. Click **Upload ECS**<br>2. Observe the dialog | — | Dialog opens with: file input, "ECS" label, **Upload** button, **Cancel** button, and a hint for allowed format/size | High |
| PPAC_WM_UPL_TC_005 | Upload UI | Open Upload PA7 dialog from Worker Detail | Already on Worker Detail of any worker | 1. Click **Upload PA7**<br>2. Observe the dialog | — | Upload PA7 dialog opens, mirroring ECS, with the label **PA7** shown correctly | High |
| PPAC_WM_UPL_TC_006 | Happy Path — Subcontractor common | Upload a valid ECS for a subcontractor — status unchanged | Subcontractor worker `Valid`, no ECS yet | 1. Open Worker Detail<br>2. Click **Upload ECS**<br>3. Select a valid 2 MB PDF<br>4. Click **Upload**<br>5. Wait for the response | File: `ecs_valid_sample.pdf` (2 MB, PDF) | Toast "Upload successful"; the ECS file appears in the Documents area; **worker status stays `Valid`** (does not move to Pending) | Critical |
| PPAC_WM_UPL_TC_007 | Happy Path — PA7 | Upload a valid PA7 — status unchanged | Worker `Valid`, no PA7 yet | 1. Click **Upload PA7**<br>2. Select a 1.5 MB PDF<br>3. Click **Upload** | `pa7_valid_sample.pdf` (1.5 MB) | Upload succeeds; the PA7 file appears in Worker Detail; status unchanged | Critical |
| PPAC_WM_UPL_TC_008 | Visibility — Worker Detail | Uploaded ECS shows file name + upload date in Worker Detail | One ECS file successfully uploaded | 1. Refresh Worker Detail (F5)<br>2. Observe the Documents section | — | File name `ecs_valid_sample.pdf` and upload date (e.g., `19/05/2026 12:30`) are displayed and persist after reload | Critical |
| PPAC_WM_UPL_TC_009 | Visibility — Worker Detail | Clicking the uploaded ECS opens preview/download | One ECS file uploaded | 1. Click the ECS file name/icon in Documents<br>2. Observe the tab/preview that opens | — | The file opens with the correct content in a new tab or viewer; checksum matches the original | Critical |
| PPAC_WM_UPL_TC_010 | Visibility — History | Upload appears in History / Audit log | One ECS file uploaded successfully | 1. Open the worker's History tab/section<br>2. Observe the latest entry | — | History contains an entry with: action = `Upload ECS`, actor = `ppac_mun@yopmail.com`, timestamp = upload time, link to the file | Critical |
| PPAC_WM_UPL_TC_011 | Visibility — History | Multiple uploads → history keeps all versions in chronological order | Worker already has an ECS | 1. Upload ECS #1 (file A) → confirm replace<br>2. Upload ECS #2 (file B) → confirm replace<br>3. Open History | File A: `ecs_v1.pdf`<br>File B: `ecs_v2.pdf` | History shows **both entries**, sorted newest-first; clicking each entry opens the matching version | High |
| PPAC_WM_UPL_TC_012 | Replace flow — Confirm dialog | Upload over an existing ECS → confirmation dialog appears | Worker already has a valid ECS file | 1. Click **Upload ECS** a second time<br>2. Select a new file<br>3. Click **Upload** | New file `ecs_v2.pdf` | A dialog appears: **"Do you want to change the ECS/PA7 document?"** with **Confirm** / **Cancel** buttons | Critical |
| PPAC_WM_UPL_TC_013 | Replace flow — Cancel | Click Cancel on the confirmation dialog → keep the old file | Confirmation dialog for changing ECS is open | 1. Click **Cancel** | — | Dialog closes; the old ECS file is kept; History records **no** upload event | High |
| PPAC_WM_UPL_TC_014 | Replace flow — OCR success | Replace ECS, OCR reads name/data correctly → status unchanged | Worker `Valid` already has an ECS | 1. Click **Upload ECS**<br>2. Select a clear PDF with the correct name<br>3. Confirm replace<br>4. Wait for OCR | `ecs_correct_name.pdf` (name matches profile) | OCR success; the new file replaces the old; **worker status stays `Valid`** | Critical |
| PPAC_WM_UPL_TC_015 | Replace flow — OCR fail → Pending (Recheck) | Replace ECS, OCR cannot read / wrong name → status moves to Pending (Recheck) | Worker `Valid` already has an ECS | 1. Upload a blurred PDF / wrong-name PDF<br>2. Confirm replace<br>3. Wait for OCR | `ecs_blurred.pdf` (OCR unreadable) | OCR fail; the new file is stored; **status moves to `Pending (Recheck)`**; History records the status transition | Critical |
| PPAC_WM_UPL_TC_016 | State Transition — Pending Info | Worker in `Pending Info` uploads ECS → status → `Pending (Recheck)` | Worker is currently in status `Pending Info` | 1. Open Worker Detail<br>2. Upload a valid ECS file<br>3. Observe status after upload | `ecs_valid.pdf` | Status moves from `Pending Info` → **`Pending (Recheck)`**; History records the status transition with actor | Critical |
| PPAC_WM_UPL_TC_017 | Name-mismatch flow | Wrong worker name detected → rename only (no upload) → status unchanged | Worker `Valid`, minor name typo | 1. Open Worker Detail<br>2. Edit the name → match the ECS<br>3. Save | New name matches ECS | Name is updated; **no new upload occurs**; worker status unchanged | High |
| PPAC_WM_UPL_TC_018 | Validation — Empty submit | Click Upload without selecting a file | Upload ECS dialog open | 1. Leave the file input empty<br>2. Click **Upload** | — | Upload button is disabled or an inline error "Please select a file" appears; no API call is made | High |
| PPAC_WM_UPL_TC_019 | Validation — File type | Upload a file that is not PDF / not in the allowed list | Upload ECS dialog open | 1. Choose an `.exe` file<br>2. Click Upload | `malware.exe` (12 KB) | "Invalid file type" error is shown; no upload; no History entry | Critical |
| PPAC_WM_UPL_TC_020 | Validation — File type (.txt) | Upload a `.txt` file not in the whitelist | Upload PA7 dialog open | 1. Choose `note.txt`<br>2. Click Upload | `note.txt` (1 KB) | Format error is shown; no upload | High |
| PPAC_WM_UPL_TC_021 | Validation — Image type | Upload a `.jpg` image (if PDF-only) | Upload ECS dialog open | 1. Choose `photo.jpg`<br>2. Click Upload | `photo.jpg` (500 KB) | If PDF-only per requirement → reject; if images are allowed → accept and continue with OCR (confirm with BA) | Medium |
| PPAC_WM_UPL_TC_022 | Boundary — Empty file (0 B) | Upload a 0-byte empty file | Upload ECS dialog open | 1. Choose a 0 B PDF<br>2. Click Upload | `empty.pdf` (0 bytes) | "File is empty" error is shown; reject; no record created | High |
| PPAC_WM_UPL_TC_023 | Boundary — Max size pass | Upload a file exactly at the max size | Assumes max = 10 MB | 1. Choose a 10 MB PDF<br>2. Click Upload | `ecs_10mb.pdf` (10,485,760 bytes) | Upload succeeds | High |
| PPAC_WM_UPL_TC_024 | Boundary — Over max size | Upload a file 1 byte over the max size | Assumes max = 10 MB | 1. Choose a PDF at 10 MB + 1 byte<br>2. Click Upload | `ecs_10mb_plus.pdf` (10,485,761 bytes) | Reject with "File exceeds 10 MB limit"; server must also reject (do not trust client-side validation) | Critical |
| PPAC_WM_UPL_TC_025 | Boundary — Very large file | Upload a 100 MB file | Upload dialog open | 1. Choose a 100 MB PDF<br>2. Click Upload | `huge.pdf` (100 MB) | Rejected before upload (client-side) or by the server with 413; UI does not freeze | Medium |
| PPAC_WM_UPL_TC_026 | Boundary — Filename length | Filename of 255 characters | Upload dialog open | 1. Rename the file to a 255-char string `a...a.pdf`<br>2. Upload | 255-char filename | Upload OK; name is shown in full or truncated with a tooltip; History records no error | Medium |
| PPAC_WM_UPL_TC_027 | Boundary — Filename special chars | Filename with spaces, Vietnamese diacritics, and special characters | Upload dialog open | 1. Upload a file named `ECS tài liệu (final) #1.pdf` | Filename `ECS tài liệu (final) #1.pdf` | Upload OK; the name is preserved with Unicode; the download link works | Medium |
| PPAC_WM_UPL_TC_028 | Security — XSS in filename | Filename contains a script payload | Upload dialog open | 1. Upload a file named `<img src=x onerror=alert(1)>.pdf` | Filename `<img src=x onerror=alert(1)>.pdf` | The displayed name is **HTML-escaped**, the script does not execute; React does not inject via `dangerouslySetInnerHTML` | Critical |
| PPAC_WM_UPL_TC_029 | Security — Direct URL anonymous access | The file URL (S3/CDN) is not publicly readable to anonymous users | One ECS file uploaded | 1. Copy the ECS file URL from the Network tab<br>2. Open an incognito tab and paste the URL | Direct file URL | Returns **HTTP 403/401 Forbidden**, or uses a pre-signed URL with a short TTL (not a public S3 URL). **References finding F-SEC-03 already filed for RtW** | Critical |
| PPAC_WM_UPL_TC_030 | Security — Cross-tenant access | Manager of company A cannot upload for a worker of company B | Two tenants A and B | 1. Manager A obtains a worker_id from company B<br>2. Call the upload API with B's worker_id | Worker B ID | Server returns **HTTP 403/404**; no file is created; audit logged | Critical |
| PPAC_WM_UPL_TC_031 | Resilience — Network interrupted | Network drops mid-upload | Upload dialog uploading a 5 MB file | 1. Start the upload<br>2. At ~50% progress, turn off WiFi<br>3. Observe the UI | 5 MB PDF + WiFi disconnect | UI shows "Upload failed"; no half-saved file in DB/S3; a Retry button is available | High |
| PPAC_WM_UPL_TC_032 | Resilience — Double-click submit | Click Upload twice quickly → no duplicate record | Upload dialog open, file selected | 1. Click Upload twice in <300 ms | 1 MB PDF | Only 1 entry in History; the Upload button is disabled after the first click until the response arrives | High |
| PPAC_WM_UPL_TC_033 | UX — Loading indicator | Spinner/progress shown while uploading a large file | Upload dialog open | 1. Upload an 8 MB file<br>2. Observe the UI while uploading | 8 MB PDF | A progress bar or spinner is shown; UI is not frozen; success message after completion | Medium |
| PPAC_WM_UPL_TC_034 | UX — Cancel mid-upload | Click Cancel while uploading | Uploading an 8 MB file | 1. Start the upload<br>2. Click Cancel during the upload | 8 MB PDF | The request is aborted (XHR/fetch abort); no file stored in DB; no History entry | Medium |
| PPAC_WM_UPL_TC_035 | Audit | History records correct uploader metadata | One ECS uploaded | 1. Open the History entry just created<br>2. Observe the fields | — | Entry contains: actor email = `ppac_mun@yopmail.com`, timestamp in UTC, action = "Upload ECS", file name, file size, IP/User-Agent (if available) | High |
| PPAC_WM_UPL_TC_036 | Compliance — F-COMP-02 link | When upload triggers a status change, History contains an audit trail | Worker `Pending Info` uploads ECS → `Pending (Recheck)` | 1. Upload ECS<br>2. Open History | — | **Two entries** present: (1) Upload ECS, (2) Status change `Pending Info` → `Pending (Recheck)` with reason; both include actor + timestamp | Critical |
| PPAC_WM_UPL_TC_037 | Browser support | Upload works on Chrome (default) | Chrome stable | 1. Repeat TC_006 on Chrome 148 | `ecs_valid.pdf` | Pass | High |
| PPAC_WM_UPL_TC_038 | Browser support | Upload works on Firefox | Firefox ESR | 1. Repeat TC_006 on Firefox | `ecs_valid.pdf` | Pass | Medium |
| PPAC_WM_UPL_TC_039 | I18n | Button and dialog labels are correct when switching language | EN + VN support (if any) | 1. Toggle locale<br>2. Open Upload dialog | — | Labels "Upload" / "Tải lên", "Cancel" / "Hủy", "Confirm" / "Xác nhận" display correctly in context | Low |
| PPAC_WM_UPL_TC_040 | Regression | Uploading ECS does not break other Worker Detail actions | ECS upload completed | 1. After a successful upload, try: edit profile, open RtW tab, navigate back to list<br>2. Observe | — | All other features work as expected; no JS console errors; no memory leak | Medium |

---

## Notes & Open questions for BA/PO

- **Q1:** Allowed file formats — PDF only, or also JPG/PNG for document images?
- **Q2:** Max file size (assumed **10 MB** in tests) — BA must confirm the exact figure.
- **Q3:** Is the OCR engine **synchronous** (waits for response) or **asynchronous** (callback / polling)? If async, where does the UI show OCR status?
- **Q4:** When a Manager overwrites a file (replace), is the **old** file kept in history (versioned) or permanently deleted?
- **Q5:** Is upload restricted to the Manager role only, or also to "Supervisor" / "Compliance Officer"?
- **Q6:** OCR fail → Pending (Recheck): does "Recheck" mean a manual review by the Compliance team, or an OCR re-trigger?
- **Q7:** Is the file URL returned to the client a **pre-signed S3 URL** (short TTL) or a **public URL**? (Related to finding **F-SEC-03** already raised for RtW — must be tested to avoid repeating the PII leak.)
- **Q8:** Is there a per-window upload limit (rate-limit) to prevent abuse?
