LỊCH VIỆT cho Android (v1.2.0)
Phần mềm được phát triển bởi Lâm Mạnh - TTYT Tương Dương © 2026

Ứng dụng chạy hoàn toàn ngoại tuyến, không xin quyền gì, không gửi dữ liệu đi đâu.
Yêu cầu Android 8.0 trở lên.

CÁCH LẤY FILE .APK (không cần cài Android Studio)
Máy tạo ra file này không có Android SDK nên CHƯA biên dịch ra APK. Cách dễ nhất là để GitHub biên dịch giúp (miễn phí):
 1. Tạo tài khoản https://github.com, bấm New repository, đặt tên LichViet, chọn Private, bấm Create.
 2. Bấm "uploading an existing file", kéo TOÀN BỘ nội dung đã giải nén (nhớ cả thư mục ẩn .github) vào, bấm Commit.
    Nếu thư mục .github không kéo được: Add file > Create new file, gõ tên  .github/workflows/build.yml  rồi dán nội dung từ file cùng tên.
 3. Mở thẻ Actions, chọn "Build APK", đợi khoảng 3-5 phút (dấu tích xanh).
 4. Bấm vào lần chạy đó, kéo xuống mục Artifacts, tải "LichViet-apk" (file .zip, bên trong là app-debug.apk).
 5. Chép app-debug.apk sang điện thoại, mở file. Android hỏi "Cho phép cài ứng dụng từ nguồn này" thì bật lên.
    Nếu Play Protect cảnh báo "ứng dụng chưa được xác minh" thì chọn "Vẫn cài đặt" (vì ký bằng khóa debug của chính bạn).

CÁCH KHÁC: Android Studio
 Mở thư mục này bằng Android Studio (bản Ladybug trở lên), đợi Sync xong, menu Build > Build APK(s).
 File ở app/build/outputs/apk/debug/app-debug.apk

ĐƯA LÊN CH PLAY
 CH Play yêu cầu file .aab được ký bằng khóa của bạn (APK debug không dùng được).
 1. Tạo khóa ký (làm 1 lần, CẤT KỸ file và mật khẩu - mất là không cập nhật được ứng dụng nữa):
      keytool -genkeypair -v -keystore lichviet.jks -alias lichviet -keyalg RSA -keysize 2048 -validity 10000
 2. Trên GitHub: Settings > Secrets and variables > Actions > New repository secret, tạo 4 secret:
      KEYSTORE_BASE64   = nội dung file lichviet.jks đã mã hóa base64 (Windows: certutil -encode lichviet.jks k.txt, bỏ 2 dòng BEGIN/END)
      KEYSTORE_PASSWORD = mật khẩu kho khóa
      KEY_ALIAS         = lichviet
      KEY_PASSWORD      = mật khẩu khóa
 3. Chạy lại Actions, tải artifact "LichViet-release", file app-release.aab dùng để tải lên Play Console.
 4. Cần tài khoản nhà phát triển Google Play (phí đăng ký một lần 25 USD). Tài khoản cá nhân tạo sau 2023 phải chạy kiểm thử kín
    với ít nhất 12 người trong 14 ngày trước khi được phát hành công khai - hãy kiểm tra lại quy định hiện hành trên Play Console.
 5. Chuẩn bị: ảnh chụp màn hình, mô tả, chính sách quyền riêng tư (ứng dụng không thu thập dữ liệu), mức phân loại nội dung.
 Mỗi lần phát hành bản mới, tăng versionCode trong app/build.gradle.kts.

WIDGET MÀN HÌNH CHÍNH (từ v1.2.0)
 Nhấn giữ vào chỗ trống trên màn hình chính > Tiện ích (Widget) > Lịch Việt, kéo ra màn hình. Mặc định 4x2 ô, kéo viền để đổi kích cỡ.
 Hiển thị ngày dương, thứ, âm lịch, can chi, hoàng đạo/hắc đạo, mức tốt/xấu và ngày lễ hôm nay. Chạm vào widget để mở ứng dụng.
 Tự đổi khi sang ngày mới (và cập nhật định kỳ 30 phút). Một số máy (Xiaomi, Oppo, Vivo...) hạn chế chạy nền: nếu widget không tự đổi
 qua nửa đêm, hãy cho Lịch Việt "Tự khởi động" và không bật tiết kiệm pin cho ứng dụng; mở ứng dụng một lần cũng làm mới widget.

CẤU TRÚC
 app/src/main/java/.../Lunar.kt, Almanac.kt   thuật toán âm lịch và xem ngày bằng Kotlin (dùng cho widget)
 app/src/main/java/.../LichVietWidget.kt      widget màn hình chính
 app/src/main/assets/index.html   toàn bộ giao diện + thuật toán lịch âm + xem ngày (JavaScript)
 app/src/main/java/.../MainActivity.kt   khung WebView, nút Back, tràn viền Android 15
 app/build.gradle.kts             cấu hình ứng dụng (applicationId vn.tuongduong.lichviet, minSdk 26, targetSdk 35)

KHÁC BẢN WINDOWS
 - Không có khay hệ thống/icon số ngày, Liquid Glass và "Chạy cùng Windows" (không áp dụng trên điện thoại).
 - Thêm vài ngày kỷ niệm: 3/2, 27/2 (Thầy thuốc), 7/4, 28/6, 27/7, 22/12 (đã ghi "mới" trong chi tiết ngày).
 - Phần xem ngày giữ NGUYÊN cách tính của bản Windows (Trực và 12 thần theo tháng âm lịch, không theo tiết khí).
 - Vuốt ngang trên lưới để đổi tháng; trong trang chi tiết vuốt ngang để đổi ngày.
 Nội dung xem ngày theo quan niệm dân gian, chỉ mang tính tham khảo.
