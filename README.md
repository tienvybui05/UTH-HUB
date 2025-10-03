# UTH HUB - Mạng xã hội nội bộ dành riêng cho sinh viên UTH

## I. Giới thiệu và lý do chọn đề tài
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

## II. Ý tưởng ứng dụng
Ý tưởng chính của UTH HUB là xây dựng một ứng dụng mạng xã hội nội bộ với ba vai trò chính: Sinh viên (Student), Người điều hành (Moderator), và Quản trị viên (Admin).

- Sinh viên (Student): đăng ký bằng email trường, chia sẻ bài viết ngắn, hình ảnh, tham gia thảo luận, kết nối với bạn bè cùng trường  
- Moderator: là sinh viên tình nguyện hoặc cán bộ đoàn, hỗ trợ quản lý nội dung, phê duyệt bài viết, xử lý báo cáo vi phạm  
- Admin: quản lý toàn bộ hệ thống, phân quyền, giám sát hoạt động, thống kê dữ liệu  

Ứng dụng sẽ có giao diện thân thiện, dễ dùng, tích hợp các chức năng cơ bản giống như một mạng xã hội nhỏ (tạo bài viết, like, comment, thông báo, tìm kiếm) nhưng được giới hạn trong phạm vi trường để đảm bảo tính riêng tư, gắn kết, và hữu ích.

Ngoài ra, ứng dụng cũng có thể mở rộng trong tương lai như:

- Kết nối với hệ thống quản lý học tập (LMS)  
- Tích hợp chatbot hỗ trợ giải đáp thông tin  
- Tổ chức mini-game, khảo sát trực tuyến trong cộng đồng sinh viên  
- Tích hợp thêm chức năng nhắn tin cá nhân và nhóm  

---

## III. Nghiên cứu và phân tích

### 1. Nhu cầu
Qua thực tế tại UTH, sinh viên có các nhu cầu sau:

- Thông tin học tập & sự kiện: Nhiều sinh viên bỏ lỡ thông báo vì kênh truyền thông chính thức (website, fanpage) chưa linh hoạt và thiếu tương tác hai chiều  
- Kết nối cộng đồng: Mong muốn có nơi trao đổi kinh nghiệm học tập, định hướng nghề nghiệp, hoặc chỉ đơn giản là chia sẻ đời sống sinh viên  
- Môi trường an toàn: Khác với mạng xã hội lớn, sinh viên cần một không gian riêng tư, ít quảng cáo, hạn chế nội dung tiêu cực  
- Quản lý & định hướng: Nhà trường và tổ chức đoàn – hội có thể truyền tải thông tin chính thống, kiểm soát nội dung để tránh tình trạng sai lệch hoặc gây ảnh hưởng tiêu cực  

=> Từ nhu cầu đó, một ứng dụng riêng biệt cho sinh viên UTH là cần thiết và có tính khả thi, có khả năng triển khai trong tương lai gần.

### 2. Hệ thống

#### 2.1. Actors
- Student (Sinh viên UTH): Người dùng chính, tạo và tương tác với nội dung  
- Moderator (Người điều hành): Sinh viên tình nguyện hoặc cán bộ đoàn. Gần giống như một user bình thường nhưng có chức năng quản trị, phê duyệt nội dung  
- Admin (Quản trị viên): quản lý toàn bộ hệ thống  

#### 2.2. Chức năng

##### 2.2.1. Chức năng cho Sinh viên (Student)
a. Đăng ký & quản lý tài khoản  
- Đăng ký bằng email trường (@uth.edu.vn) để xác thực sinh viên UTH  
- Cập nhật profile cơ bản (tên, khoa, năm học, ảnh đại diện...)  

b. Chia sẻ & thảo luận nội dung  
- Tạo bài viết (bài viết ngắn tối đa 500 ký tự hoặc hình ảnh) về học tập, sự kiện trường, hoặc vấn đề nào đó  
- Like, comment, lưu hay báo cáo bài viết của người khác  
- Tìm kiếm bài viết theo từ khóa hoặc lọc theo từng khoa, viện  
- Nhận thông báo bài viết, comment…  

c. Kết nối & hỗ trợ  
- Theo dõi sinh viên khác trong trường  
- Gửi phản hồi hoặc báo cáo bài viết vi phạm (spam, nội dung không phù hợp)  

##### 2.2.2. Chức năng cho Người điều hành (Moderator)
a. Quản lý nội dung  
- Xem và phê duyệt bài viết mới để tránh spam hoặc nội dung không phù hợp  
- Xóa hoặc ẩn bài viết vi phạm quy định trường  

b. Hỗ trợ cộng đồng  
- Theo dõi báo cáo từ sinh viên và xử lý (VD: cảnh báo user vi phạm)  
- Tạo bài viết thông báo sự kiện trường có ghim  

##### 2.2.3. Chức năng cho Quản trị (Admin)
a. Quản lý người dùng  
- Xem danh sách sinh viên đăng ký, xác thực email @uth.edu.vn  
- Phân quyền moderator cho sinh viên đáng tin cậy  

b. Quản lý ứng dụng  
- Theo dõi hoạt động tổng thể (số bài viết, số user active, hashtag phổ biến)  
- Xử lý khiếu nại từ sinh viên hoặc moderator  

c. Báo cáo cơ bản  
- Thống kê đơn giản: Số lượt đăng bài, người dùng…  

---

## IV. Công nghệ sử dụng
- Frontend: Android Studio (Kotlin), XML layouts  
- Backend: Firebase (Authentication, Firestore cho bài viết, Cloud Messaging cho thông báo)  
- Cơ sở dữ liệu: MySQL hoặc MongoDB  
- Thiết kế: Figma (UI/UX đơn giản, tối ưu cho sinh viên)  
- Permissions: GPS (cho traffic alerts), internet (Firebase), notifications  
- Bảo mật: Mã hóa dữ liệu, bảo vệ API bằng JWT  
