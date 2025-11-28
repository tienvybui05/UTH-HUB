# UTH HUB - Mạng xã hội nội bộ dành riêng cho sinh viên UTH

## Lý do chọn đề tài
Trong bối cảnh chuyển đổi số hiện nay, sinh viên các trường đại học ngày càng có nhu cầu kết nối, trao đổi và chia sẻ thông tin học tập, sự kiện, cũng như đời sống tinh thần trong môi trường học đường. Tuy nhiên, các nền tảng mạng xã hội phổ biến hiện nay như Facebook, Instagram, Thread hay Zalo có một số hạn chế:

- Môi trường mở, thiếu tính riêng tư, dễ bị loãng thông tin, quảng cáo và nội dung ngoài lề thậm chí là lừa đảo  
- Khó tập trung vào những vấn đề liên quan trực tiếp đến học tập và hoạt động tại trường  
- Không có sự quản lý hoặc định hướng nội dung phù hợp với đặc thù của sinh viên UTH  

Do đó, nhóm lựa chọn đề tài **“UTH HUB – Mạng xã hội nội bộ dành riêng cho sinh viên UTH”** nhằm xây dựng một nền tảng khép kín, riêng tư, đơn giản và thân thiện, chỉ dành cho cộng đồng sinh viên UTH. Ứng dụng giúp:

- Kết nối sinh viên trong cùng trường, cùng khoa để trao đổi học thuật và chia sẻ kinh nghiệm  
- Cập nhật các thông tin sự kiện, hoạt động, và phong trào trong trường một cách nhanh chóng  
- Giải trí & giao lưu trong một môi trường an toàn, tránh nội dung độc hại hoặc spam  

Từ những lý do trên, nhóm quyết định phát triển ứng dụng UTH HUB như một mạng xã hội thu nhỏ, phù hợp với nhu cầu và đặc thù sinh viên UTH.

---
---
---

## Ngôn ngữ:
Kotlin

## UI framework:
Jetpack Compose (Material 3)

## Kiến trúc tổng thể:
- MVVM (Model – View – ViewModel)
- Repository pattern (AuthRepository, PostRepository, …)
- StateFlow + SnapshotListener realtime
- Firebase-first architecture (Auth + Firestore + Storage + FCM)

## Firebase services sử dụng:
- Firebase Authentication (Email/Password + Google Sign-In domain @ut.edu.vn)
- Cloud Firestore (lưu user profile, posts, likes, saves, comments, notifications…)
- Firebase Storage (lưu avatar, hình ảnh bài đăng)
- Firebase Cloud Messaging (FCM) (notify)
- Firebase Hosting (App Links + share profile)
- Firebase Functions (gửi thông báo khi có comment/like)

## Cấu trúc Module:
- feature/auth — đăng ký, đăng nhập, quên mật khẩu, hoàn tất hồ sơ
- feature/profile — trang cá nhân, đổi avatar, share profile, xem bài viết của user
- feature/post — tạo bài đăng, upload ảnh, feed, like/save/comment
- feature/notifications — hiển thị danh sách thông báo
- feature/admin — quản lý bài viết, sinh viên, báo cáo
- core/design — theme, màu, icon, component tái sử dụng
- core/notification — xử lý FCM
- app/navigation — điều hướng NavGraph + deep link
- app — MainActivity + AppLinkResolver

# Luồng hoạt động chính của ứng dụng

## 1. MainActivity khởi chạy
- Kiểm tra quyền thông báo (Android 13+)
- Lấy FCM token và cập nhật vào Firestore
- Kiểm tra deep link (https://uth-hub-49b77.web.app/user/{uid})
- Khởi tạo NavHostController và gọi NavGraph với optional startDeepLink.

## 2. Điều hướng tổng – NavGraph
- Nếu user chưa đăng nhập → SignInScreen
- Nếu đăng nhập bằng Google lần đầu → CompleteProfileScreen
- Nếu user là admin → vào màn hình quản trị
- Nếu là sinh viên → vào HomeScreen

NavGraph quản lý các route:
- Home, CreatePost, Notification, Profile
- LikedPost, SavedPost, PostComment
- OtherProfile/{uid} (deep link)
- Auth: SignIn, SignUp, Forgot, Otp, Reset, CompleteProfile
- Admin: ManagerProfile, StudentManager, ReportedPost,…

## 3. Màn hình chính (HomeScreen)
- Hiển thị danh sách bài viết lấy realtime từ Firestore
- Mỗi bài có:
  - like/unlike
  - save/unsave
  - xem chi tiết + comment
  - hiển thị số lượng tương tác realtime

## 4. Chức năng tạo bài viết (CreatePost)
- Chọn ảnh bằng Photo Picker
- Upload ảnh lên Firebase Storage
- Tạo PostModel và ghi vào posts với thông tin:
  - authorId, authorName, mssv
  - content, imageUrls, createdAt
  - counter: likeCount, commentCount, saveCount

## 5. Hồ sơ sinh viên (ProfileScreen)
- Giao diện đọc realtime từ Firestore users/{uid}
- Chức năng:
  - đổi avatar (gallery + camera)
  - đổi mật khẩu
  - xem bài viết của mình
  - xem bài đã lưu, đã thích
  - chia sẻ trang cá nhân
  - cập nhật FCM Token

## 6. Trang cá nhân người khác (OtherProfileScreen)
- Lấy dữ liệu user theo uid
- Hiển thị avatar + info + bài đăng của user
- Cho phép chia sẻ trang cá nhân của người khác
- Load realtime posts theo authorId

## 7. Chia sẻ trang cá nhân
- Link HTTPS:
  https://uth-hub-49b77.web.app/user/{uid}
- Firebase Hosting + assetlinks.json (SHA-256 release)
- QR code generate từ HTTPS
- Khi người khác mở link → deep link → điều hướng đến trang Profile/OtherProfile

## 8. Like / Save / Comment
- Like: /posts/{postId}/likes/{uid}
- Save: /posts/{postId}/saves/{uid}
- Comment: /posts/{postId}/comments/{commentId}
- ViewModel sử dụng optimistic update
- Repository thực hiện đọc/ghi Firestore
- Hiển thị realtime qua snapshot listener

## 9. Thông báo (Notifications)
- FCM gửi thông báo khi có:
  - comment mới
  - ai đó like bài viết
- MyFirebaseMessagingService nhận thông báo và đẩy vào NotificationScreen.

## 10. Quản trị viên (Admin)
- AdminPanel:
  - xem danh sách bài báo cáo
  - quản lý sinh viên
  - quản lý bài đăng
- Kiểm tra quyền qua role = "admin" trong Firestore.

---
---
---


#  Hướng dẫn cài đặt và chạy thử (Local)

Phần này hướng dẫn cách clone và chạy ứng dụng trực tiếp trong Android Studio.  
Dự án đã bao gồm toàn bộ file Firebase (google-services.json, assetlinks.json, rules…).  
Không cần tạo Firebase mới, không cần cấu hình thủ công, không cần keystore mới.



## 1. Yêu cầu môi trường
- Android Studio Hedgehog hoặc mới hơn  
- JDK đi kèm Android Studio  
- Android SDK 24 trở lên (target 36)  
- Có kết nối Internet  

Không yêu cầu:
- tạo Firebase project  
- cấu hình Hosting  
- cấu hình Functions  
- ký lại keystore  


## 2. Clone dự án

Mở Terminal / CMD / PowerShell:
git clone https://github.com/tienvybui05/UTH-HUB.git

Sau đó mở thư mục dự án bằng Android Studio.
- Khởi động Android Studio
- Chọn Open hoặc Open Existing Project
- Trỏ đến thư mục gốc của app trong máy: <path...>UTH-HUB

## 3. Đồng bộ Gradle

Android Studio sẽ tự thực hiện:
- Sync Gradle  
- Tải Firebase + Compose dependencies  

Nếu không: File → Sync Project with Gradle Files



## 4. Chạy ứng dụng

### 4.1. Máy ảo (AVD)
1. Mở Device Manager  
2. Tạo AVD Android 13+  
3. Nhấn Run  

### 4.2. Máy thật
1. Bật USB Debugging  
2. Cắm thiết bị  
3. Nhấn Run  



## 5. Login để test
Ứng dụng chỉ cho phép đăng nhập:
- email `@ut.edu.vn`  
- tài khoản Google miền `@ut.edu.vn`  

Nếu có tài khoản test → đăng nhập trực tiếp.


## 6. Test deep link (không bắt buộc)

Link profile: https://uth-hub-49b77.web.app/user/{uid}
ADB test: adb shell am start -W -a android.intent.action.VIEW -d "https://uth-hub-49b77.web.app/user/<uid>"

Ví dụ: ./adb shell am start -W -a android.intent.action.VIEW -d "https://uth-hub-49b77.web.app/user/BguiNKfFn9XuRzn0Cg7wdqrlxCf2"



## 7. Firebase đã được cấu hình sẵn
- `google-services.json` đã nằm trong `/app`  
- `assetlinks.json` đã nằm trong `/public/.well-known/`  
- Firestore rules đã được set  
- Hosting, FCM, Storage đã hoạt động  

Chỉ cần nhấn Run.



## 8. Build Release (nếu cần)
Nếu chỉ chạy thử → không cần build release.

Nếu muốn build:
Build → Generate Signed App Bundle / APK

Dùng keystore do nhóm cung cấp.  
Không tạo keystore mới (sẽ làm deep link không hoạt động).



## 9. Lỗi thường gặp

### Lỗi thiếu SDK
Cài đặt:
- Android 13 / 14  
- SDK Platform  
- Build Tools  
- Google USB Driver  

### Lỗi dependency
File → Invalidate Caches → Restart
### Lỗi không login
- Không phải email `@ut.edu.vn`  
- User chưa có trong Firestore  



## 10. Hoàn tất
Sau khi setup, bạn có thể sử dụng đầy đủ:
- đăng nhập  
- đăng bài  
- like / save / comment  
- đổi avatar  
- chia sẻ trang cá nhân  
- xem thông báo  
- xem trang người khác  
- giống bản chạy thực tế hoàn chỉnh  
