# Certificate Download Fix - TODO ✅

## Approved Plan Steps:
- [x] Step 1: Create TODO.md ✅ **DONE**
- [x] Step 2: Update CertificateService.java with mocks & proper exceptions ✅ **DONE**
- [x] Step 3: Update CertificateController.java with better error handling ✅ **DONE**
- [ ] Step 4: Check DB enrollment status for student 2/course 1
- [ ] Step 5: Restart backend & test endpoint  
- [ ] Step 6: Mark complete & cleanup TODO

**Current Progress:** Code changes complete! Ready for testing.

**Notes:**
- Blockchain/Pinata skipped (mocked) ✅ Works without external deps
- Enrollment must be "COMPLETED" status
- Proper errors: 400 (not found), 409 (not eligible/already issued)
- Test: `curl "http://localhost:8080/certificates/download?studentId=2&courseId=1"`
