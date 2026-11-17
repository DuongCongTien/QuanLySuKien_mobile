package com.group29.eventapp.data.mock

import com.group29.eventapp.data.model.*
import java.util.concurrent.TimeUnit

object MockData {
    val now: Long get() = System.currentTimeMillis()

    // 3 Địa điểm thực tế tại Đà Nẵng
    const val LOCATION_ARIYANA = "Trung tâm Hội nghị Quốc tế Ariyana, 107 Võ Nguyên Giáp, Đà Nẵng"
    const val LOCATION_MUONG_THANH = "Khách sạn Mường Thanh Luxury, 270 Võ Nguyên Giáp, Đà Nẵng"
    const val LOCATION_INNOVATION_HUB = "Không gian Đổi mới Sáng tạo TP. Đà Nẵng, 02 Quang Trung, Đà Nẵng"
    const val LOCATION_ONLINE = "Trực tuyến qua Google Meet & Hub Live"

    // 8 Sự kiện mẫu đầy đủ trạng thái
    val sampleEvents: List<EventModel> by lazy {
        listOf(
            // Event 1: Published (Sắp diễn ra, Đà Nẵng)
            EventModel(
                id = "event_1",
                title = "Tech Summit Danang 2026",
                description = "Hội nghị công nghệ thường niên lớn nhất miền Trung quy tụ hơn 1.000 chuyên gia, kỹ sư phần mềm và lãnh đạo công nghệ chia sẻ về AI, Cloud Computing và Kiến trúc phần mềm hiện đại.",
                category = "Công nghệ",
                location = LOCATION_ARIYANA,
                isOnline = false,
                startTime = now + TimeUnit.HOURS.toMillis(48),
                endTime = now + TimeUnit.HOURS.toMillis(56),
                capacity = 150,
                registeredCount = 92,
                status = EventStatus.Published,
                organizerName = "Hiệp hội Phần mềm Đà Nẵng (DSA)",
                speakerNames = listOf("TS. Lê Anh Tuấn (Google)", "Bà Hoàng Mai Linh (Microsoft)", "Ông Trần Quốc Bảo (FPT Software)"),
                requirements = "Yêu cầu mang theo vé QR điện tử trên app khi check-in tại cổng chính. Trang phục lịch sự.",
                isFeatured = true,
                sessions = listOf(
                    EventSessionModel("s1_1", "event_1", "Khai mạc & Toàn cảnh Xu hướng Công nghệ 2026", "TS. Lê Anh Tuấn", "Hội trường Grand Ballroom", now + TimeUnit.HOURS.toMillis(48), now + TimeUnit.HOURS.toMillis(49) + TimeUnit.MINUTES.toMillis(30), true),
                    EventSessionModel("s1_2", "event_1", "Ứng dụng GenAI vào phát triển phần mềm di động", "Bà Hoàng Mai Linh", "Phòng Hội thảo A1", now + TimeUnit.HOURS.toMillis(49) + TimeUnit.MINUTES.toMillis(45), now + TimeUnit.HOURS.toMillis(51), false),
                    EventSessionModel("s1_3", "event_1", "Tối ưu hóa Cloud Native & Serverless Architecture", "Ông Trần Quốc Bảo", "Phòng Hội thảo B2", now + TimeUnit.HOURS.toMillis(52), now + TimeUnit.HOURS.toMillis(54), false),
                    EventSessionModel("s1_4", "event_1", "Bế mạc, Networking & Trao giải Tech Awards", "Ban tổ chức DSA", "Hội trường Grand Ballroom", now + TimeUnit.HOURS.toMillis(54) + TimeUnit.MINUTES.toMillis(30), now + TimeUnit.HOURS.toMillis(56), true)
                )
            ),

            // Event 2: Ongoing (Đang diễn ra, User Demo đã check-in)
            EventModel(
                id = "event_2",
                title = "AI Vietnam Hackathon & Seminar 2026",
                description = "Chuỗi sự kiện Hackathon 24h và chuỗi hội thảo chuyên đề ứng dụng Trí tuệ nhân tạo tạo sinh giải quyết các bài toán giao thông, giáo dục và y tế thông minh.",
                category = "Trí tuệ nhân tạo",
                location = LOCATION_INNOVATION_HUB,
                isOnline = false,
                startTime = now - TimeUnit.HOURS.toMillis(2),
                endTime = now + TimeUnit.HOURS.toMillis(6),
                capacity = 100,
                registeredCount = 100,
                status = EventStatus.Ongoing,
                organizerName = "Cộng đồng AI Vietnam & Danang Innovation",
                speakerNames = listOf("PGS.TS. Đặng Minh Quân (ĐH Bách Khoa)", "ThS. Vũ Hoài Nam (VinAI Research)"),
                requirements = "Thí sinh mang theo laptop cá nhân, vé QR để vào phòng Lab Hackathon.",
                isFeatured = true,
                sessions = listOf(
                    EventSessionModel("s2_1", "event_2", "Check-in & Khởi động Hackathon AI", "Ban tổ chức AI Vietnam", "Sảnh Tầng 1", now - TimeUnit.HOURS.toMillis(2), now - TimeUnit.HOURS.toMillis(1), true),
                    EventSessionModel("s2_2", "event_2", "Hội thảo: Edge AI trên thiết bị di động Android", "ThS. Vũ Hoài Nam", "Hội trường Tầng 3", now - TimeUnit.MINUTES.toMillis(45), now + TimeUnit.MINUTES.toMillis(45), true),
                    EventSessionModel("s2_3", "event_2", "Pitching: Trình bày dự án Vòng 1", "Hội đồng Giám khảo", "Phòng Demo Lab", now + TimeUnit.HOURS.toMillis(1), now + TimeUnit.HOURS.toMillis(3), true),
                    EventSessionModel("s2_4", "event_2", "Công bố kết quả & Trao giải thưởng Hackathon", "PGS.TS. Đặng Minh Quân", "Hội trường Tầng 3", now + TimeUnit.HOURS.toMillis(4), now + TimeUnit.HOURS.toMillis(6), true)
                )
            ),

            // Event 3: Published (Trực tuyến)
            EventModel(
                id = "event_3",
                title = "Hội thảo Cloud Native & DevOps Mastery",
                description = "Chuyên đề chia sẻ kinh nghiệm triển khai Kubernetes, CI/CD Pipeline tự động và quản lý hạ tầng đám mây cho các hệ thống triệu người dùng.",
                category = "Điện toán đám mây",
                location = LOCATION_ONLINE,
                isOnline = true,
                startTime = now + TimeUnit.HOURS.toMillis(24),
                endTime = now + TimeUnit.HOURS.toMillis(28),
                capacity = 300,
                registeredCount = 185,
                status = EventStatus.Published,
                organizerName = "Cloud Native Danang Group",
                speakerNames = listOf("Kỹ sư trưởng Phạm Đức Trí (Viettel Cloud)", "Chuyên gia DevOps Mai Xuân Bách"),
                requirements = "Đường link phòng họp Google Meet sẽ được mở trước giờ bắt đầu 15 phút.",
                isFeatured = false,
                sessions = listOf(
                    EventSessionModel("s3_1", "event_3", "Giới thiệu & Kiến trúc Kubernetes Production", "Phạm Đức Trí", "Phòng Online 1", now + TimeUnit.HOURS.toMillis(24), now + TimeUnit.HOURS.toMillis(25), true),
                    EventSessionModel("s3_2", "event_3", "Thực chiến CI/CD Pipeline không gián đoạn", "Mai Xuân Bách", "Phòng Online 1", now + TimeUnit.HOURS.toMillis(25) + TimeUnit.MINUTES.toMillis(15), now + TimeUnit.HOURS.toMillis(26) + TimeUnit.MINUTES.toMillis(30), false),
                    EventSessionModel("s3_3", "event_3", "Bảo mật hệ thống Cloud Native (DevSecOps)", "Phạm Đức Trí", "Phòng Online 1", now + TimeUnit.HOURS.toMillis(26) + TimeUnit.MINUTES.toMillis(45), now + TimeUnit.HOURS.toMillis(27) + TimeUnit.MINUTES.toMillis(30), false),
                    EventSessionModel("s3_4", "event_3", "Hỏi đáp Q&A cùng các kỹ sư Cloud", "Ban tổ chức", "Phòng Online 1", now + TimeUnit.HOURS.toMillis(27) + TimeUnit.MINUTES.toMillis(30), now + TimeUnit.HOURS.toMillis(28), false)
                )
            ),

            // Event 4: RegistrationClosed / HẾT CHỖ (Dành cho kịch bản Danh sách chờ - Waitlist)
            EventModel(
                id = "event_4",
                title = "Workshop: Thiết kế Hệ thống Di động & Microservices",
                description = "Buổi thực hành chuyên sâu giới hạn số lượng học viên về thiết kế hệ thống backend phục vụ ứng dụng di động chịu tải cao.",
                category = "Thiết kế hệ thống",
                location = LOCATION_MUONG_THANH,
                isOnline = false,
                startTime = now + TimeUnit.HOURS.toMillis(72),
                endTime = now + TimeUnit.HOURS.toMillis(78),
                capacity = 50,
                registeredCount = 50, // ĐÃ HẾT CHỖ!
                status = EventStatus.RegistrationClosed,
                organizerName = "Mobile Architecture Club",
                speakerNames = listOf("Senior Architect Bùi Minh Tuấn"),
                requirements = "Sự kiện ĐÃ HẾT CHỖ. Bạn có thể đăng ký vào Danh sách chờ để nhận cơ hội tham gia khi có người hủy vé.",
                isFeatured = true,
                sessions = listOf(
                    EventSessionModel("s4_1", "event_4", "Phân tích yêu cầu phi chức năng hệ thống Mobile", "Bùi Minh Tuấn", "Phòng Lotus 1", now + TimeUnit.HOURS.toMillis(72), now + TimeUnit.HOURS.toMillis(73) + TimeUnit.MINUTES.toMillis(30), true),
                    EventSessionModel("s4_2", "event_4", "Chiến lược Caching, Offline-first & Data Sync", "Bùi Minh Tuấn", "Phòng Lotus 1", now + TimeUnit.HOURS.toMillis(73) + TimeUnit.MINUTES.toMillis(45), now + TimeUnit.HOURS.toMillis(75), false),
                    EventSessionModel("s4_3", "event_4", "Thiết kế REST/GraphQL/gRPC cho Mobile Client", "Bùi Minh Tuấn", "Phòng Lotus 1", now + TimeUnit.HOURS.toMillis(75) + TimeUnit.MINUTES.toMillis(15), now + TimeUnit.HOURS.toMillis(76) + TimeUnit.MINUTES.toMillis(45), false),
                    EventSessionModel("s4_4", "event_4", "Bài tập mô phỏng chịu tải 100k CCU", "Bùi Minh Tuấn", "Phòng Lotus 1", now + TimeUnit.HOURS.toMillis(77), now + TimeUnit.HOURS.toMillis(78), true)
                )
            ),

            // Event 5: Completed (Đã kết thúc, có dữ liệu Event Memory)
            EventModel(
                id = "event_5",
                title = "Diễn đàn Đổi mới Sáng tạo Sinh viên 2025",
                description = "Diễn đàn kết nối sinh viên công nghệ với các doanh nghiệp ICT hàng đầu miền Trung, triển lãm các đề tài tốt nghiệp xuất sắc.",
                category = "Giáo dục & Hướng nghiệp",
                location = LOCATION_INNOVATION_HUB,
                isOnline = false,
                startTime = now - TimeUnit.DAYS.toMillis(10),
                endTime = now - TimeUnit.DAYS.toMillis(9),
                capacity = 200,
                registeredCount = 188,
                status = EventStatus.Completed,
                organizerName = "Trung tâm Hỗ trợ Khởi nghiệp Đổi mới Sáng tạo",
                speakerNames = listOf("Bà Nguyễn Lan Phương (Director HR)", "Ông Đỗ Quốc Huy (CEO TechStart)"),
                requirements = "Sự kiện đã kết thúc tốt đẹp. Người tham dự có thể xem lại kỷ niệm tại mục Event Memory.",
                isFeatured = false,
                sessions = listOf(
                    EventSessionModel("s5_1", "event_5", "Khai mạc & Triển lãm dự án sinh viên", "Ban tổ chức", "Khuôn viên Sảnh chính", now - TimeUnit.DAYS.toMillis(10), now - TimeUnit.DAYS.toMillis(10) + TimeUnit.HOURS.toMillis(2), true),
                    EventSessionModel("s5_2", "event_5", "Tọa đàm: Nhu cầu tuyển dụng IT thời kỳ AI bùng nổ", "Bà Nguyễn Lan Phương", "Hội trường 1", now - TimeUnit.DAYS.toMillis(10) + TimeUnit.HOURS.toMillis(3), now - TimeUnit.DAYS.toMillis(10) + TimeUnit.HOURS.toMillis(5), false),
                    EventSessionModel("s5_3", "event_5", "Phỏng vấn thử & Chỉnh sửa CV trực tiếp 1-1", "HR Mentor Team", "Khu vực Bàn tròn", now - TimeUnit.DAYS.toMillis(10) + TimeUnit.HOURS.toMillis(5), now - TimeUnit.DAYS.toMillis(10) + TimeUnit.HOURS.toMillis(7), false),
                    EventSessionModel("s5_4", "event_5", "Lễ trao học bổng tài năng công nghệ trẻ", "Ông Đỗ Quốc Huy", "Hội trường 1", now - TimeUnit.DAYS.toMillis(9) - TimeUnit.HOURS.toMillis(2), now - TimeUnit.DAYS.toMillis(9), true)
                )
            ),

            // Event 6: Cancelled (Đã hủy)
            EventModel(
                id = "event_6",
                title = "Lễ hội Khởi nghiệp Trẻ Miền Trung",
                description = "Sự kiện ngoài trời nhằm tôn vinh các dự án khởi nghiệp sinh viên và thanh niên năng động.",
                category = "Khởi nghiệp",
                location = "Công viên Biển Đông, TP. Đà Nẵng",
                isOnline = false,
                startTime = now + TimeUnit.DAYS.toMillis(7),
                endTime = now + TimeUnit.DAYS.toMillis(8),
                capacity = 500,
                registeredCount = 210,
                status = EventStatus.Cancelled,
                organizerName = "Hội Liên hiệp Thanh niên Đà Nẵng",
                speakerNames = listOf("Ban lãnh đạo Thành đoàn"),
                requirements = "Sự kiện tạm hoãn do điều kiện thời tiết bão đổ bộ. Ban tổ chức sẽ thông báo thời gian mới qua email.",
                isFeatured = false,
                sessions = listOf(
                    EventSessionModel("s6_1", "event_6", "Lễ khai mạc lễ hội khởi nghiệp", "Ban tổ chức", "Sân khấu ngoài trời", now + TimeUnit.DAYS.toMillis(7), now + TimeUnit.DAYS.toMillis(7) + TimeUnit.HOURS.toMillis(2), true),
                    EventSessionModel("s6_2", "event_6", "Hội chợ gian hàng sản phẩm sáng tạo", "Các đội thi", "Gian hàng 1-50", now + TimeUnit.DAYS.toMillis(7) + TimeUnit.HOURS.toMillis(2), now + TimeUnit.DAYS.toMillis(7) + TimeUnit.HOURS.toMillis(6), false),
                    EventSessionModel("s6_3", "event_6", "Chung kết cuộc thi Ý tưởng Khởi nghiệp", "Ban giám khảo", "Sân khấu chính", now + TimeUnit.DAYS.toMillis(8) - TimeUnit.HOURS.toMillis(4), now + TimeUnit.DAYS.toMillis(8) - TimeUnit.HOURS.toMillis(2), true),
                    EventSessionModel("s6_4", "event_6", "Đêm nhạc hội thanh niên & Bế mạc", "Ca sĩ khách mời", "Sân khấu chính", now + TimeUnit.DAYS.toMillis(8) - TimeUnit.HOURS.toMillis(2), now + TimeUnit.DAYS.toMillis(8), false)
                )
            ),

            // Event 7: Draft (Chỉ Ban tổ chức thấy)
            EventModel(
                id = "event_7",
                title = "Hội nghị Thượng đỉnh Fintech & Web3 Vietnam (Bản nháp)",
                description = "Bản nháp dự thảo kế hoạch tổ chức diễn đàn an toàn tài chính số, quản trị rủi ro thanh toán điện tử và pháp lý cho tài sản số tại Việt Nam.",
                category = "Fintech",
                location = LOCATION_MUONG_THANH,
                isOnline = false,
                startTime = now + TimeUnit.DAYS.toMillis(30),
                endTime = now + TimeUnit.DAYS.toMillis(31),
                capacity = 250,
                registeredCount = 0,
                status = EventStatus.Draft,
                organizerName = "Hiệp hội Blockchain & Fintech",
                speakerNames = listOf("Đang cập nhật danh sách diễn giả"),
                requirements = "Bản nháp nội bộ đang xin phê duyệt của cơ quan chủ quản.",
                isFeatured = false,
                sessions = listOf(
                    EventSessionModel("s7_1", "event_7", "Phiên mở đầu: Xu hướng Fintech Đông Nam Á", "Chuyên gia Fintech", "Phòng VIP 1", now + TimeUnit.DAYS.toMillis(30), now + TimeUnit.DAYS.toMillis(30) + TimeUnit.HOURS.toMillis(2), true),
                    EventSessionModel("s7_2", "event_7", "Bảo mật thanh toán & Xác thực sinh trắc học", "Kỹ sư Bảo mật", "Phòng VIP 1", now + TimeUnit.DAYS.toMillis(30) + TimeUnit.HOURS.toMillis(2) + TimeUnit.MINUTES.toMillis(30), now + TimeUnit.DAYS.toMillis(30) + TimeUnit.HOURS.toMillis(4), false),
                    EventSessionModel("s7_3", "event_7", "Khung pháp lý thử nghiệm (Regulatory Sandbox)", "Đại diện cơ quan", "Phòng VIP 2", now + TimeUnit.DAYS.toMillis(31) - TimeUnit.HOURS.toMillis(4), now + TimeUnit.DAYS.toMillis(31) - TimeUnit.HOURS.toMillis(2), false),
                    EventSessionModel("s7_4", "event_7", "Tổng kết & Giao lưu kết nối", "Ban tổ chức", "Phòng VIP 1", now + TimeUnit.DAYS.toMillis(31) - TimeUnit.HOURS.toMillis(2), now + TimeUnit.DAYS.toMillis(31), true)
                )
            ),

            // Event 8: Published (Workshop phỏng vấn IT)
            EventModel(
                id = "event_8",
                title = "Workshop Kỹ năng Phỏng vấn & Xây dựng CV IT Chuẩn Quốc Tế",
                description = "Chương trình huấn luyện kỹ năng mềm, giải thuật LeetCode và thiết kế hệ thống dành cho lập trình viên chuẩn bị ứng tuyển công ty đa quốc gia.",
                category = "Kỹ năng nghề nghiệp",
                location = LOCATION_INNOVATION_HUB,
                isOnline = false,
                startTime = now + TimeUnit.DAYS.toMillis(5),
                endTime = now + TimeUnit.DAYS.toMillis(5) + TimeUnit.HOURS.toMillis(4),
                capacity = 80,
                registeredCount = 45,
                status = EventStatus.Published,
                organizerName = "Câu lạc bộ Lập trình Trẻ",
                speakerNames = listOf("Mentor Phan Tuấn Anh (Tech Lead)", "Bà Đặng Cẩm Tú (Senior Recruiter)"),
                requirements = "Học viên chuẩn bị sẵn 1 bản CV cá nhân (PDF hoặc in giấy) để được sửa trực tiếp.",
                isFeatured = false,
                sessions = listOf(
                    EventSessionModel("s8_1", "event_8", "Giải mã cấu trúc một bản CV IT ấn tượng", "Bà Đặng Cẩm Tú", "Phòng Đào tạo 201", now + TimeUnit.DAYS.toMillis(5), now + TimeUnit.DAYS.toMillis(5) + TimeUnit.HOURS.toMillis(1), true),
                    EventSessionModel("s8_2", "event_8", "Chiến thuật vượt qua vòng phỏng vấn kỹ thuật Coding", "Phan Tuấn Anh", "Phòng Đào tạo 201", now + TimeUnit.DAYS.toMillis(5) + TimeUnit.HOURS.toMillis(1) + TimeUnit.MINUTES.toMillis(15), now + TimeUnit.DAYS.toMillis(5) + TimeUnit.HOURS.toMillis(2) + TimeUnit.MINUTES.toMillis(30), true),
                    EventSessionModel("s8_3", "event_8", "Xử lý các câu hỏi tình huống & Thỏa thuận lương", "Bà Đặng Cẩm Tú", "Phòng Đào tạo 201", now + TimeUnit.DAYS.toMillis(5) + TimeUnit.HOURS.toMillis(2) + TimeUnit.MINUTES.toMillis(45), now + TimeUnit.DAYS.toMillis(5) + TimeUnit.HOURS.toMillis(3) + TimeUnit.MINUTES.toMillis(30), false),
                    EventSessionModel("s8_4", "event_8", "Phỏng vấn thử nghiệm Mock Interview 1-1", "Mentor Team", "Phòng Đào tạo 201", now + TimeUnit.DAYS.toMillis(5) + TimeUnit.HOURS.toMillis(3) + TimeUnit.MINUTES.toMillis(30), now + TimeUnit.DAYS.toMillis(5) + TimeUnit.HOURS.toMillis(4), false)
                )
            )
        )
    }

    // Đăng ký mẫu của người dùng Demo
    val sampleRegistrations: List<RegistrationModel> by lazy {
        listOf(
            // Đã đăng ký và ĐÃ CHECK-IN sự kiện event_2 (Ongoing)
            RegistrationModel(
                id = "reg_demo_1",
                eventId = "event_2",
                userId = "user_demo_attendee",
                userName = "Nguyễn Văn A",
                userEmail = "attendee@group29.vn",
                status = RegistrationStatus.Registered,
                checkInStatus = CheckInStatus.CheckedIn,
                ticketCode = "SEH-2026-AI888",
                registeredAt = now - TimeUnit.DAYS.toMillis(3),
                checkInAt = now - TimeUnit.HOURS.toMillis(1)
            ),
            // Đã đăng ký sự kiện event_1 (Sắp diễn ra, chưa check-in)
            RegistrationModel(
                id = "reg_demo_2",
                eventId = "event_1",
                userId = "user_demo_attendee",
                userName = "Nguyễn Văn A",
                userEmail = "attendee@group29.vn",
                status = RegistrationStatus.Registered,
                checkInStatus = CheckInStatus.NotCheckedIn,
                ticketCode = "SEH-2026-TECH999",
                registeredAt = now - TimeUnit.DAYS.toMillis(2),
                checkInAt = null
            ),
            // Đã tham gia sự kiện event_5 (Đã kết thúc, có Memory)
            RegistrationModel(
                id = "reg_demo_3",
                eventId = "event_5",
                userId = "user_demo_attendee",
                userName = "Nguyễn Văn A",
                userEmail = "attendee@group29.vn",
                status = RegistrationStatus.Registered,
                checkInStatus = CheckInStatus.CheckedIn,
                ticketCode = "SEH-2025-SV777",
                registeredAt = now - TimeUnit.DAYS.toMillis(15),
                checkInAt = now - TimeUnit.DAYS.toMillis(10)
            ),
            // Vé đã hủy tại sự kiện event_6 (Cancelled)
            RegistrationModel(
                id = "reg_demo_4",
                eventId = "event_6",
                userId = "user_demo_attendee",
                userName = "Nguyễn Văn A",
                userEmail = "attendee@group29.vn",
                status = RegistrationStatus.Cancelled,
                checkInStatus = CheckInStatus.NotCheckedIn,
                ticketCode = "SEH-2026-KN000",
                registeredAt = now - TimeUnit.DAYS.toMillis(5),
                checkInAt = null
            )
        )
    }

    // 6 Nhiệm vụ Quest thông thường
    val sampleQuests: List<QuestModel> by lazy {
        listOf(
            QuestModel("q_1", "event_2", "Check-in sự kiện sớm", "Có mặt tại cổng và quét mã QR check-in trước giờ khai mạc 15 phút.", 50, 1, "Quét mã QR tại cổng", QuestStatus.Active, QuestCompletionStatus.Completed),
            QuestModel("q_2", "event_2", "Khám phá 3 gian hàng công nghệ", "Ghé thăm ít nhất 3 gian hàng triển lãm và quét mã QR tại mỗi gian hàng.", 100, 3, "Quét mã QR gian hàng", QuestStatus.Active, QuestCompletionStatus.InProgress),
            QuestModel("q_3", "event_2", "Gửi câu hỏi cho diễn giả qua Trợ lý AI", "Sử dụng tính năng Trợ lý sự kiện AI để đặt câu hỏi thảo luận cho phiên báo cáo.", 60, 2, "Gửi qua giao diện AI", QuestStatus.Active, QuestCompletionStatus.PendingVerification),
            QuestModel("q_4", "event_2", "Mở rộng mạng lưới kết nối", "Kết nối thành công với ít nhất 2 bạn đồng hành cùng sự kiện qua Smart Match.", 120, 1, "Hệ thống tự động duyệt khi đối phương chấp nhận", QuestStatus.Active, QuestCompletionStatus.InProgress),
            QuestModel("q_5", "event_2", "Gửi thông điệp vào Hộp thời gian", "Tạo và niêm phong một thông điệp kỷ niệm hoặc mục tiêu cá nhân cho sự kiện.", 80, 1, "Tạo xong Time Capsule", QuestStatus.Active, QuestCompletionStatus.Completed),
            QuestModel("q_6", "event_2", "Hoàn thành khảo sát phản hồi sự kiện", "Đánh giá chất lượng các phiên hội thảo và đóng góp ý kiến cho ban tổ chức.", 90, 1, "Biểu mẫu khảo sát", QuestStatus.Active, QuestCompletionStatus.InProgress)
        )
    }

    // 4 Secret Mission với các trạng thái khác nhau
    val sampleSecretMissions: List<SecretMissionModel> by lazy {
        listOf(
            // Nhiệm vụ 1: Available (Có thể mở, nội dung bị che/khóa)
            SecretMissionModel(
                id = "sm_1",
                eventId = "event_2",
                title = "Mật vụ: Mật mã Khởi nguồn AI",
                description = "Tìm kiếm tấm bảng chứa thuật toán bí ẩn được giấu tại phòng Lab Hackathon tầng 3 và giải mã từ khóa cốt lõi.",
                hints = listOf(
                    "Gợi ý 1: Vị trí nằm gần góc bảng thông tin cạnh cửa sổ phòng Lab 302.",
                    "Gợi ý 2: Chú ý công thức gradient descent viết bằng phấn màu vàng.",
                    "Gợi ý 3: Quét mã QR ẩn dưới góc phải tấm bảng giải thuật."
                ),
                unlockedHintsCount = 0,
                targetAction = "Quét mã QR ẩn tại Lab 302",
                rewardPoints = 200,
                badgeName = "Thám tử AI Xuất chúng",
                assignmentStatus = MissionAssignmentStatus.Available,
                expiresAt = now + TimeUnit.HOURS.toMillis(4)
            ),

            // Nhiệm vụ 2: InProgress (Đang làm, đã mở 2 gợi ý)
            SecretMissionModel(
                id = "sm_2",
                eventId = "event_2",
                title = "Mật vụ: Bắt tay Chuyên gia Thầm lặng",
                description = "Tìm kiếm và bắt chuyện với diễn giả giấu mặt đang ngồi tại khu vực cà phê có huy hiệu màu tím.",
                hints = listOf(
                    "Gợi ý 1: Chuyên gia đang mặc áo thun polo màu xanh than và đeo thẻ có viền tím.",
                    "Gợi ý 2: Câu hỏi mở đầu: 'Xin chào, anh/chị có phải tác giả mô hình TinyLLM không?'",
                    "Gợi ý 3: Nhờ diễn giả ký xác nhận số hoặc quét mã trao đổi trên app."
                ),
                unlockedHintsCount = 2,
                targetAction = "Nhập mã xác nhận nhận từ diễn giả giấu mặt",
                rewardPoints = 250,
                badgeName = "Bậc thầy Networking",
                assignmentStatus = MissionAssignmentStatus.InProgress,
                expiresAt = now + TimeUnit.HOURS.toMillis(3)
            ),

            // Nhiệm vụ 3: PendingVerification (Đã gửi bằng chứng, chờ BTC duyệt)
            SecretMissionModel(
                id = "sm_3",
                eventId = "event_2",
                title = "Mật vụ: Bức ảnh Sáng tạo Cùng Đội ngũ",
                description = "Chụp một bức ảnh check-in sáng tạo cùng ít nhất 4 thành viên mới quen tại khu vực Photobooth trung tâm.",
                hints = listOf(
                    "Gợi ý 1: Photobooth nằm ngay giữa Sảnh chính tầng 1.",
                    "Gợi ý 2: Bức ảnh phải thể hiện rõ biểu tượng tay chữ 'AI'.",
                    "Gợi ý 3: Tải ảnh bằng chứng lên để Ban tổ chức phê duyệt thủ công."
                ),
                unlockedHintsCount = 3,
                targetAction = "Tải ảnh nhóm tại Photobooth",
                rewardPoints = 180,
                badgeName = "Đồng đội Vàng",
                assignmentStatus = MissionAssignmentStatus.PendingVerification,
                expiresAt = now + TimeUnit.HOURS.toMillis(5),
                evidenceSubmitted = "Đã tải lên ảnh 'photobooth_team_ai_2026.png' (chờ BTC kiểm tra)."
            ),

            // Nhiệm vụ 4: Completed (Đã xong và đã nhận thưởng, không nhận lại)
            SecretMissionModel(
                id = "sm_4",
                eventId = "event_2",
                title = "Mật vụ: Người đi đầu Xu hướng",
                description = "Là một trong 20 người đầu tiên hoàn thành thiết lập User Journey cá nhân hóa trong sự kiện.",
                hints = listOf(
                    "Gợi ý 1: Truy cập mục Journey từ chi tiết sự kiện.",
                    "Gợi ý 2: Chọn mục tiêu và hoàn tất lộ trình trước 09:00 sáng."
                ),
                unlockedHintsCount = 2,
                targetAction = "Tạo User Journey trước thời hạn",
                rewardPoints = 150,
                badgeName = "Người tiên phong",
                assignmentStatus = MissionAssignmentStatus.Completed,
                expiresAt = now - TimeUnit.HOURS.toMillis(1),
                evidenceSubmitted = "Hệ thống tự động ghi nhận User Journey đã tạo lúc 08:45.",
                isClaimed = true
            )
        )
    }

    // 8 Người dùng mẫu cho Smart Match (Networking)
    val sampleUsers: List<UserModel> by lazy {
        listOf(
            UserModel("u_1", "Lê Bảo Ngọc", "ngoc.le@techcorp.vn", "Android Lead Developer", "Fintech Solutions", "Đam mê kiến trúc Jetpack Compose, Clean Architecture và Kotlin Multiplatform. Muốn tìm cộng sự làm dự án AI di động.", listOf("Android", "Kotlin", "AI/ML", "Mobile Architecture"), listOf("Tìm cộng sự làm dự án", "Học hỏi công nghệ mới"), listOf("Kotlin", "Coroutines", "System Design"), "event_2", 96, "Trùng 4/4 sở thích (Android, Kotlin, AI, System Design) và cùng mục tiêu tìm cộng sự"),
            UserModel("u_2", "Trần Đình Khang", "khang.tran@startup.io", "AI Research Engineer", "DataMind Lab", "Nghiên cứu về mô hình ngôn ngữ lớn trên Edge Devices và thị giác máy tính.", listOf("AI/ML", "Deep Learning", "Edge Computing", "Python"), listOf("Học hỏi công nghệ mới", "Tìm đối tác nghiên cứu"), listOf("PyTorch", "ONNX", "TensorFlow Lite"), "event_2", 91, "Trùng sở thích AI/ML và mục tiêu học hỏi công nghệ sâu"),
            UserModel("u_3", "Phạm Thị Quỳnh Nga", "nga.pham@edu.vn", "Product Manager", "EdTech Vietnam", "Quản lý sản phẩm ứng dụng học tập, quan tâm đến việc ứng dụng AI cá nhân hóa trải nghiệm người dùng.", listOf("Product Management", "UX/UI", "EdTech", "AI/ML"), listOf("Khám phá sản phẩm", "Mở rộng quan hệ đối tác"), listOf("Agile", "User Research", "Data Analytics"), "event_2", 84, "Cùng quan tâm đến trải nghiệm sản phẩm và ứng dụng AI"),
            UserModel("u_4", "Nguyễn Hoàng Minh", "minh.nguyen@cloud.com", "DevOps & Cloud Engineer", "Viettel IDC", "Chuyên về tự động hóa hạ tầng, CI/CD và kiến trúc microservices Kubernetes.", listOf("Cloud Computing", "DevOps", "Docker", "Kubernetes"), listOf("Học hỏi", "Trao đổi kỹ thuật"), listOf("Terraform", "K8s", "Go"), "event_2", 78, "Cùng tham gia phiên kiến trúc hệ thống và quan tâm công nghệ backend"),
            UserModel("u_5", "Hoàng Kim Oanh", "oanh.hoang@designhub.vn", "Senior UI/UX Designer", "Freelancer Studio", "Thiết kế giao diện người dùng theo chuẩn Material 3, trải nghiệm thiết kế cho ứng dụng di động thông minh.", listOf("UI/UX", "Material Design", "Figma", "Design Systems"), listOf("Tìm việc làm", "Tìm đối tác dự án"), listOf("Figma", "User Testing", "Prototyping"), "event_2", 75, "Trùng sở thích giao diện người dùng và thiết kế ứng dụng di động"),
            UserModel("u_6", "Đỗ Văn Thắng", "thang.do@corp.vn", "Data Analyst", "FPT Telecom", "Phân tích hành vi người dùng và xây dựng pipeline dữ liệu thời gian thực.", listOf("Data Analytics", "BigData", "SQL", "PowerBI"), listOf("Học hỏi kinh nghiệm", "Networking"), listOf("Python", "SQL", "ETL"), "event_2", 70, "Cùng hướng tới phát triển giải pháp dữ liệu thông minh"),
            UserModel("u_7", "Bùi Thanh Trúc", "truc.bui@hrlink.com", "Technical Recruiter", "HR Link Asia", "Tìm kiếm các bạn lập trình viên tài năng mảng Mobile & AI cho các công ty công nghệ tại Đà Nẵng.", listOf("Tuyển dụng IT", "Hướng nghiệp", "Networking"), listOf("Tìm kiếm ứng viên", "Xây dựng thương hiệu nhà tuyển dụng"), listOf("Talent Acquisition", "Career Coaching"), "event_2", 65, "Cung cấp cơ hội việc làm và tư vấn lộ trình sự nghiệp"),
            UserModel("u_8", "Vũ Gia Huy", "huy.vu@student.dut.udn.vn", "Sinh viên năm 4 CNTT", "ĐH Bách Khoa - ĐHĐN", "Sinh viên đam mê phát triển ứng dụng di động Android, muốn học hỏi từ các anh chị đi trước.", listOf("Android", "Sinh viên", "Lập trình viên trẻ"), listOf("Tìm việc làm thực tập", "Học hỏi"), listOf("Java", "Kotlin", "Git"), "event_2", 62, "Cùng tham gia sự kiện và quan tâm đến lập trình di động")
        )
    }

    // 5 Huy hiệu mẫu
    val sampleBadges: List<BadgeModel> by lazy {
        listOf(
            BadgeModel("b_1", "Người tiên phong", "Đăng ký và check-in trong nhóm 50 người đầu tiên", "ic_badge_pioneer", now - TimeUnit.DAYS.toMillis(10)),
            BadgeModel("b_2", "Thợ săn Nhiệm vụ", "Hoàn thành xuất sắc 5 nhiệm vụ Quest sự kiện", "ic_badge_hunter", now - TimeUnit.DAYS.toMillis(10)),
            BadgeModel("b_3", "Bậc thầy Kết nối", "Gửi lời mời và kết nối thành công với 5 bạn mới", "ic_badge_connector", null),
            BadgeModel("b_4", "Thám tử AI", "Giải mã thành công Secret Mission khó nhất sự kiện", "ic_badge_detective", null),
            BadgeModel("b_5", "Nhà du hành Thời gian", "Tạo và mở ít nhất một Hộp thời gian Time Capsule", "ic_badge_timetraveler", now - TimeUnit.DAYS.toMillis(1))
        )
    }

    // 8 Thông báo mẫu
    val sampleNotifications: List<NotificationModel> by lazy {
        listOf(
            NotificationModel("notif_1", "Nhắc nhở phiên sắp bắt đầu", "Phiên 'Edge AI trên thiết bị di động' sẽ diễn ra sau 15 phút tại Hội trường Tầng 3.", NotificationType.EventUpdate, now - TimeUnit.MINUTES.toMillis(15), false, "event_2"),
            NotificationModel("notif_2", "Nhiệm vụ bí mật mới!", "Bạn có một nhiệm vụ bí mật mới phù hợp với mục tiêu học tập. Hãy mở hộp thư Secret Mission để khám phá.", NotificationType.Mission, now - TimeUnit.MINUTES.toMillis(40), false, "sm_1"),
            NotificationModel("notif_3", "Lời mời nhận chỗ từ Danh sách chờ", "Chúc mừng! Có chỗ trống cho sự kiện 'Thiết kế Hệ thống Di động & Microservices'. Xác nhận trong 15 phút.", NotificationType.SeatOffer, now - TimeUnit.MINUTES.toMillis(5), false, "offer_1"),
            NotificationModel("notif_4", "Lời mời kết nối mới", "Lê Bảo Ngọc vừa gửi lời mời kết nối trên Smart Event Match.", NotificationType.Networking, now - TimeUnit.HOURS.toMillis(1), true, "u_1"),
            NotificationModel("notif_5", "Thông báo Hộp thời gian", "Bạn có 1 hộp thời gian sắp đến thời hạn mở khóa. Vui lòng kiểm tra kho Time Capsule.", NotificationType.TimeCapsule, now - TimeUnit.HOURS.toMillis(2), true, "tc_2"),
            NotificationModel("notif_6", "Check-in thành công!", "Bạn đã check-in thành công tại sự kiện 'AI Vietnam Hackathon 2026'. Nhận ngay 50 điểm thưởng.", NotificationType.System, now - TimeUnit.HOURS.toMillis(3), true, "event_2"),
            NotificationModel("notif_7", "Ban tổ chức điều chỉnh lịch trình", "Phiên 'Pitching Vòng 1' đã được dời sang 10:30 tại Phòng Demo Lab.", NotificationType.EventUpdate, now - TimeUnit.HOURS.toMillis(4), true, "event_2"),
            NotificationModel("notif_8", "Khảo sát ý kiến người tham gia", "Vui lòng dành 2 phút đánh giá để giúp ban tổ chức hoàn thiện các sự kiện tiếp theo.", NotificationType.System, now - TimeUnit.DAYS.toMillis(1), true, "event_5")
        )
    }

    // 10 Câu hỏi thường gặp cho Trợ lý AI (Kèm nguồn tham chiếu chính xác)
    val sampleFaqs: List<FaqModel> by lazy {
        listOf(
            FaqModel("faq_1", "Wifi của sự kiện tên gì và mật khẩu là gì?", "Mạng Wifi: 'Danang_Tech_Summit_Guest'. Mật khẩu: 'Danang2026!'. Vui lòng không tải tệp dung lượng lớn qua mạng này.", "Tiện ích & Cơ sở vật chất", "Sổ tay hướng dẫn người tham gia mục 1.3"),
            FaqModel("faq_2", "Khu vực ăn uống và phòng vệ sinh nằm ở đâu?", "Khu vực cà phê/tea-break nằm tại sảnh tầng 1 và hành lang tầng 3. Phòng vệ sinh nằm cạnh thang máy phía Tây các tầng.", "Sơ đồ & Tiện ích", "Bản đồ sơ đồ sự kiện Ariyana - Tầng 1 & 3"),
            FaqModel("faq_3", "Quy trình check-in sự kiện diễn ra như thế nào?", "Bạn mở mục 'Vé của tôi' trên ứng dụng, đưa mã QR cho tình nguyện viên tại bàn tiếp đón quét mã. Sau khi check-in bạn sẽ nhận thẻ đeo và bộ quà tặng.", "Check-in & Vé", "Quy định tham dự sự kiện Điều 4"),
            FaqModel("faq_4", "Tôi có thể nhận chứng nhận tham gia sự kiện ở đâu?", "Sau khi sự kiện kết thúc và bạn đã hoàn thành check-in, chứng nhận số sẽ tự động xuất hiện tại mục 'Event Memory' trên ứng dụng để bạn tải về hoặc chia sẻ.", "Chứng nhận & Kỷ niệm", "Quy chế cấp chứng nhận số ban hành bởi BTC"),
            FaqModel("faq_5", "Danh sách chờ (Waitlist) hoạt động theo nguyên tắc nào?", "Hệ thống áp dụng cơ chế FIFO (vào trước nhận trước). Khi có người hủy vé, lời mời nhận chỗ sẽ tự động gửi đến người đầu hàng đợi với thời hạn xác nhận là 15 phút.", "Danh sách chờ", "Chính sách Smart Seat Recovery - Nhóm 29"),
            FaqModel("faq_6", "Hộp thời gian (Time Capsule) là gì và khi nào mở được?", "Time Capsule cho phép bạn niêm phong lời nhắn/kỷ niệm và hẹn giờ mở trong tương lai. Trước thời điểm unlockAt hộp sẽ bị khóa an toàn và không ai xem được.", "Time Capsule", "Quy định bảo mật và bảo toàn dữ liệu Capsule"),
            FaqModel("faq_7", "Làm thế nào để nhận thưởng từ Nhiệm vụ bí mật (Secret Mission)?", "Bạn bấm vào nhiệm vụ, thực hiện theo gợi ý và gửi bằng chứng (như quét mã QR ẩn hoặc tải ảnh). Khi nhiệm vụ hoàn thành bạn bấm 'Nhận thưởng'. Mỗi nhiệm vụ chỉ nhận thưởng 1 lần.", "Secret Mission", "Hướng dẫn tham gia Gamification V2.0"),
            FaqModel("faq_8", "Tôi có thể đổi lịch trình cá nhân trong User Journey không?", "Có. Bạn có thể bấm 'Bỏ qua bước này' đối với các hoạt động tùy chọn. Nếu Ban tổ chức đổi giờ phiên gây xung đột, ứng dụng sẽ hiện cảnh báo và đề xuất bước thay thế.", "User Journey", "Hướng dẫn cá nhân hóa lịch trình Journey"),
            FaqModel("faq_9", "Điểm phù hợp trong Smart Match được tính như thế nào?", "Điểm phù hợp (MatchScore từ 0-100) được tính dựa trên: 40% sở thích chung + 25% mục tiêu tương đồng + 20% kỹ năng bổ trợ + 15% cùng tham gia sự kiện.", "Kết nối Networking", "Thuật toán Smart Match v2"),
            FaqModel("faq_10", "Nếu tôi gặp sự cố khẩn cấp hoặc mất đồ thì liên hệ ai?", "Bàn hỗ trợ kỹ thuật và y tế nằm tại Sảnh lễ tân tầng 1 (cạnh cửa ra vào số 2). Hoặc liên hệ hotline khẩn cấp của BTC: 0905.123.456.", "Hỗ trợ khẩn cấp", "Kế hoạch an ninh & y tế sự kiện 2026")
        )
    }

    // 5 Time Capsule mẫu
    val sampleTimeCapsules: List<TimeCapsuleModel> by lazy {
        listOf(
            // Capsule 1: Locked (Cá nhân, khóa, mở sau 3 ngày)
            TimeCapsuleModel(
                id = "tc_1",
                eventId = "event_2",
                creatorId = "user_demo_attendee",
                creatorName = "Nguyễn Văn A",
                recipientName = "Bản thân tôi trong tương lai",
                title = "Mục tiêu trở thành Senior Mobile AI Engineer",
                message = "Gửi tôi của tương lai: Hôm nay tham gia Hackathon AI tại Đà Nẵng, mình đã học được rất nhiều về Edge AI. Hy vọng sau sự kiện này mình sẽ hoàn thiện app tốt nghiệp và tìm được việc làm mơ ước!",
                type = TimeCapsuleType.Personal,
                status = TimeCapsuleStatus.Locked,
                createdAt = now - TimeUnit.HOURS.toMillis(2),
                unlockAt = now + TimeUnit.DAYS.toMillis(3),
                isPublic = false
            ),

            // Capsule 2: AvailableToOpen / Sắp mở (Bạn bè, mở sau 60 giây để demo)
            TimeCapsuleModel(
                id = "tc_2",
                eventId = "event_2",
                creatorId = "u_1",
                creatorName = "Lê Bảo Ngọc",
                recipientName = "Nguyễn Văn A",
                title = "Lời hẹn cà phê công nghệ sau sự kiện",
                message = "Chào bạn Văn A! Rất vui vì đã kết nối được tại bàn Hackathon. Sau khi sự kiện bế mạc, hẹn bạn một buổi cà phê giao lưu chuyên sâu về Kotlin và Kiến trúc phần mềm nhé!",
                type = TimeCapsuleType.Friend,
                status = TimeCapsuleStatus.AvailableToOpen,
                createdAt = now - TimeUnit.HOURS.toMillis(5),
                unlockAt = now + TimeUnit.SECONDS.toMillis(60), // Mở sau 60 giây để demo đếm ngược!
                isPublic = false
            ),

            // Capsule 3: Opened (Đã mở, hiển thị nội dung, đã có openedAt)
            TimeCapsuleModel(
                id = "tc_3",
                eventId = "event_5",
                creatorId = "user_demo_attendee",
                creatorName = "Nguyễn Văn A",
                recipientName = "Cộng đồng Sinh viên Sáng tạo",
                title = "Lời cảm ơn Ban tổ chức Diễn đàn 2025",
                message = "Cảm ơn Ban tổ chức và các anh chị diễn giả đã chia sẻ những kinh nghiệm phỏng vấn vô giá. Nhờ sự kiện này mà mình đã tự tin hơn rất nhiều khi làm CV và phỏng vấn!",
                type = TimeCapsuleType.Community,
                status = TimeCapsuleStatus.Opened,
                createdAt = now - TimeUnit.DAYS.toMillis(10),
                unlockAt = now - TimeUnit.DAYS.toMillis(1),
                openedAt = now - TimeUnit.DAYS.toMillis(1),
                isPublic = true
            ),

            // Capsule 4: Sealed (Mới niêm phong, mở sau 30 ngày)
            TimeCapsuleModel(
                id = "tc_4",
                eventId = "event_1",
                creatorId = "user_demo_attendee",
                creatorName = "Nguyễn Văn A",
                recipientName = "Cộng đồng Tech Summit",
                title = "Dự đoán xu hướng công nghệ 2027",
                message = "Tôi dự đoán trong năm 2027 các thiết bị di động sẽ tích hợp chip xử lý NPU trực tiếp chạy mô hình 7B tham số mượt mà.",
                type = TimeCapsuleType.Community,
                status = TimeCapsuleStatus.Sealed,
                createdAt = now - TimeUnit.MINUTES.toMillis(30),
                unlockAt = now + TimeUnit.DAYS.toMillis(30),
                isPublic = true
            ),

            // Capsule 5: Archived (Lưu trữ từ sự kiện cũ)
            TimeCapsuleModel(
                id = "tc_5",
                eventId = "event_5",
                creatorId = "u_2",
                creatorName = "Trần Đình Khang",
                recipientName = "Nguyễn Văn A",
                title = "Chia sẻ tài liệu slides AI Seminar",
                message = "Đã gửi link slides bài giảng và mã nguồn demo cho bạn. Chúc bạn ứng dụng thành công vào đồ án tốt nghiệp!",
                type = TimeCapsuleType.Friend,
                status = TimeCapsuleStatus.Archived,
                createdAt = now - TimeUnit.DAYS.toMillis(15),
                unlockAt = now - TimeUnit.DAYS.toMillis(8),
                openedAt = now - TimeUnit.DAYS.toMillis(8),
                isPublic = false
            )
        )
    }

    // 6 Waitlist Entries
    val sampleWaitlistEntries: List<WaitlistEntryModel> by lazy {
        listOf(
            WaitlistEntryModel("wl_1", "event_4", "user_demo_attendee", "Nguyễn Văn A", "attendee@group29.vn", 1, now - TimeUnit.HOURS.toMillis(2), WaitlistEntryStatus.Offered),
            WaitlistEntryModel("wl_2", "event_4", "u_2", "Trần Đình Khang", "khang.tran@startup.io", 2, now - TimeUnit.HOURS.toMillis(1) + TimeUnit.MINUTES.toMillis(30), WaitlistEntryStatus.Waiting),
            WaitlistEntryModel("wl_3", "event_4", "u_3", "Phạm Thị Quỳnh Nga", "nga.pham@edu.vn", 3, now - TimeUnit.HOURS.toMillis(1), WaitlistEntryStatus.Waiting),
            WaitlistEntryModel("wl_4", "event_4", "u_4", "Nguyễn Hoàng Minh", "minh.nguyen@cloud.com", 4, now - TimeUnit.MINUTES.toMillis(45), WaitlistEntryStatus.Waiting),
            WaitlistEntryModel("wl_5", "event_4", "u_5", "Hoàng Kim Oanh", "oanh.hoang@designhub.vn", 5, now - TimeUnit.MINUTES.toMillis(20), WaitlistEntryStatus.Waiting),
            WaitlistEntryModel("wl_6", "event_4", "u_8", "Vũ Gia Huy", "huy.vu@student.dut.udn.vn", 6, now - TimeUnit.MINUTES.toMillis(10), WaitlistEntryStatus.Waiting)
        )
    }

    // 1 SeatOffer Pending hết hạn sau 15 phút (cho User Demo tại sự kiện event_4)
    val sampleSeatOffer: SeatOfferModel by lazy {
        SeatOfferModel(
            id = "offer_1",
            waitlistEntryId = "wl_1",
            eventId = "event_4",
            eventTitle = "Workshop: Thiết kế Hệ thống Di động & Microservices",
            userId = "user_demo_attendee",
            createdAt = now,
            expiresAt = now + TimeUnit.MINUTES.toMillis(15), // Hết hạn sau 15 phút
            status = SeatOfferStatus.Pending
        )
    }

    // 8 Journey Steps (trong đó bước js_3 và js_4 bị trùng giờ để kích hoạt Cảnh báo xung đột)
    val sampleJourneySteps: List<JourneyStepModel> by lazy {
        listOf(
            JourneyStepModel(
                id = "js_1",
                journeyId = "journey_demo",
                eventId = "event_2",
                title = "Check-in & Nhận tài liệu tại Cổng đón khách",
                location = "Sảnh Tầng 1 - Bàn tiếp đón",
                startTime = now - TimeUnit.HOURS.toMillis(2),
                endTime = now - TimeUnit.HOURS.toMillis(1) - TimeUnit.MINUTES.toMillis(30),
                durationMinutes = 30,
                isMandatory = true,
                progress = JourneyStepProgress.Completed,
                fitScore = 100,
                recommendationReason = "Bước bắt buộc để xác thực quyền tham dự và nhận quà sự kiện",
                requiresCheckIn = true,
                isCheckedIn = true
            ),
            JourneyStepModel(
                id = "js_2",
                journeyId = "journey_demo",
                eventId = "event_2",
                title = "Tham quan Triển lãm Giải pháp AI Thông minh",
                location = "Gian hàng triển lãm A1-A6",
                startTime = now - TimeUnit.HOURS.toMillis(1) - TimeUnit.MINUTES.toMillis(15),
                endTime = now - TimeUnit.MINUTES.toMillis(45),
                durationMinutes = 30,
                isMandatory = false,
                progress = JourneyStepProgress.Completed,
                fitScore = 88,
                recommendationReason = "Phù hợp với mục tiêu Khám phá sản phẩm và mở rộng tầm nhìn",
                requiresCheckIn = false,
                isCheckedIn = true
            ),
            // CẶP BƯỚC BỊ TRÙNG GIỜ (XUNG ĐỘT): js_3 và js_4
            JourneyStepModel(
                id = "js_3",
                journeyId = "journey_demo",
                eventId = "event_2",
                title = "Hội thảo: Edge AI trên thiết bị di động Android",
                location = "Hội trường Tầng 3",
                startTime = now - TimeUnit.MINUTES.toMillis(30),
                endTime = now + TimeUnit.MINUTES.toMillis(45),
                durationMinutes = 75,
                isMandatory = true,
                progress = JourneyStepProgress.InProgress,
                fitScore = 95,
                recommendationReason = "Khớp 100% với mục tiêu Học hỏi công nghệ AI của bạn",
                requiresCheckIn = true,
                isCheckedIn = true
            ),
            JourneyStepModel(
                id = "js_4",
                journeyId = "journey_demo",
                eventId = "event_2",
                title = "Tọa đàm song song: Tối ưu Pin & Hiệu năng On-Device ML",
                location = "Phòng Hội thảo B1",
                startTime = now - TimeUnit.MINUTES.toMillis(15), // BỊ TRÙNG GIỜ VỚI js_3!
                endTime = now + TimeUnit.MINUTES.toMillis(45),
                durationMinutes = 60,
                isMandatory = true, // Cả 2 đều bắt buộc nên gây ra CẢNH BÁO XUNG ĐỘT!
                progress = JourneyStepProgress.Planned,
                fitScore = 89,
                recommendationReason = "Cùng khung giờ chuyên sâu kỹ thuật với phiên Edge AI",
                requiresCheckIn = true,
                isCheckedIn = false
            ),
            JourneyStepModel(
                id = "js_5",
                journeyId = "journey_demo",
                eventId = "event_2",
                title = "Giao lưu Networking & Thảo luận tự do",
                location = "Khu vực Coffee Lounge Tầng 2",
                startTime = now + TimeUnit.HOURS.toMillis(1),
                endTime = now + TimeUnit.HOURS.toMillis(1) + TimeUnit.MINUTES.toMillis(45),
                durationMinutes = 45,
                isMandatory = false,
                progress = JourneyStepProgress.Recommended,
                fitScore = 85,
                recommendationReason = "Khung giờ vàng kết nối các chuyên gia theo thuật toán Smart Match",
                requiresCheckIn = false,
                isCheckedIn = false
            ),
            JourneyStepModel(
                id = "js_6",
                journeyId = "journey_demo",
                eventId = "event_2",
                title = "Pitching: Trình bày dự án Vòng 1",
                location = "Phòng Demo Lab Tầng 2",
                startTime = now + TimeUnit.HOURS.toMillis(2),
                endTime = now + TimeUnit.HOURS.toMillis(3) + TimeUnit.MINUTES.toMillis(30),
                durationMinutes = 90,
                isMandatory = false,
                progress = JourneyStepProgress.Planned,
                fitScore = 82,
                recommendationReason = "Quan sát các đội thi trình bày mô hình thực tế",
                requiresCheckIn = false,
                isCheckedIn = false
            ),
            JourneyStepModel(
                id = "js_7",
                journeyId = "journey_demo",
                eventId = "event_2",
                title = "Niêm phong Hộp thời gian sự kiện (Time Capsule)",
                location = "Khu vực Kỷ niệm số",
                startTime = now + TimeUnit.HOURS.toMillis(3) + TimeUnit.MINUTES.toMillis(45),
                endTime = now + TimeUnit.HOURS.toMillis(4),
                durationMinutes = 15,
                isMandatory = false,
                progress = JourneyStepProgress.Planned,
                fitScore = 80,
                recommendationReason = "Lưu lại bài học kinh nghiệm sau một ngày trải nghiệm sự kiện",
                requiresCheckIn = false,
                isCheckedIn = false
            ),
            JourneyStepModel(
                id = "js_8",
                journeyId = "journey_demo",
                eventId = "event_2",
                title = "Lễ Công bố kết quả & Bế mạc Hackathon",
                location = "Hội trường Tầng 3",
                startTime = now + TimeUnit.HOURS.toMillis(4) + TimeUnit.MINUTES.toMillis(15),
                endTime = now + TimeUnit.HOURS.toMillis(6),
                durationMinutes = 105,
                isMandatory = true,
                progress = JourneyStepProgress.Planned,
                fitScore = 92,
                recommendationReason = "Hoạt động trọng tâm kết thúc sự kiện và trao giải",
                requiresCheckIn = true,
                isCheckedIn = false
            )
        )
    }

    // User Journey mẫu
    val sampleUserJourney: UserJourneyModel by lazy {
        UserJourneyModel(
            id = "journey_demo",
            userId = "user_demo_attendee",
            eventId = "event_2",
            goal = "Học hỏi công nghệ AI & Mở rộng quan hệ kết nối",
            topics = listOf("Trí tuệ nhân tạo", "Android", "Kiến trúc hệ thống"),
            availableTime = "Cả ngày (08:00 - 17:00)",
            priority = "Ưu tiên phiên chuyên sâu kỹ thuật",
            status = UserJourneyStatus.Active,
            steps = sampleJourneySteps,
            progressPercent = 35
        )
    }

    // Map Markers cho Sơ đồ mặt bằng tĩnh
    val sampleMapMarkers: List<MapMarkerModel> by lazy {
        listOf(
            MapMarkerModel("m_1", "Sân khấu Grand Ballroom", "SanKhau", 0.50f, 0.22f, "Đang diễn ra: Khai mạc sự kiện"),
            MapMarkerModel("m_2", "Phòng Hội thảo A1 (Edge AI)", "PhongHoiThao", 0.25f, 0.45f, "Đang diễn ra: Báo cáo chuyên đề AI"),
            MapMarkerModel("m_3", "Phòng Hội thảo B2 (Cloud Native)", "PhongHoiThao", 0.75f, 0.45f, "Sắp diễn ra: Báo cáo Kubernetes"),
            MapMarkerModel("m_4", "Gian hàng Triển lãm Công nghệ", "GianHang", 0.50f, 0.55f, "Hoạt động: Trải nghiệm sản phẩm & Minigame"),
            MapMarkerModel("m_5", "Khu vực Cà phê & Tea-break", "AnUong", 0.18f, 0.72f, "Mở cửa phục vụ miễn phí"),
            MapMarkerModel("m_6", "Phòng Vệ sinh (Nam/Nữ)", "VeSinh", 0.85f, 0.72f, "Cạnh thang máy phía Đông"),
            MapMarkerModel("m_7", "Bàn Thông tin & Hỗ trợ Y tế", "HoTro", 0.50f, 0.85f, "Trực tiếp hỗ trợ người tham dự"),
            MapMarkerModel("m_8", "Cửa Thoát hiểm số 1", "ThoatHiem", 0.10f, 0.90f, "Lối thoát hiểm khẩn cấp hành lang Tây"),
            MapMarkerModel("m_9", "Cửa Thoát hiểm số 2", "ThoatHiem", 0.90f, 0.90f, "Lối thoát hiểm khẩn cấp hành lang Đông")
        )
    }

    // Event Memory mẫu (cho sự kiện event_5 đã kết thúc)
    val sampleEventMemory: EventMemoryModel by lazy {
        EventMemoryModel(
            id = "mem_1",
            eventId = "event_5",
            eventTitle = "Diễn đàn Đổi mới Sáng tạo Sinh viên 2025",
            checkInTime = now - TimeUnit.DAYS.toMillis(10),
            completedQuestsCount = 5,
            earnedPoints = 420,
            badgeName = "Thợ săn Nhiệm vụ & Người tiên phong",
            certificateTitle = "Chứng nhận Tham dự Xuất sắc - Nhóm 29",
            connectedUsers = listOf("Lê Bảo Ngọc", "Trần Đình Khang", "Bùi Thanh Trúc"),
            isPublic = true
        )
    }
}
