package com.group29.eventapp.data.model

// ==================== ENUMS ====================

enum class EventStatus(val displayName: String) {
    Draft("Bản nháp"),
    Published("Sắp diễn ra"),
    RegistrationClosed("Đã đóng đăng ký"),
    Ongoing("Đang diễn ra"),
    Completed("Đã kết thúc"),
    Cancelled("Đã hủy")
}

enum class RegistrationStatus(val displayName: String) {
    Registered("Đã đăng ký"),
    Cancelled("Đã hủy"),
    Waitlisted("Danh sách chờ")
}

enum class CheckInStatus(val displayName: String) {
    NotCheckedIn("Chưa check-in"),
    CheckedIn("Đã check-in")
}

enum class QuestStatus(val displayName: String) {
    Draft("Bản nháp"),
    Published("Đã công bố"),
    Active("Đang hoạt động"),
    Expired("Hết hạn"),
    Disabled("Tạm dừng")
}

enum class QuestCompletionStatus(val displayName: String) {
    InProgress("Đang thực hiện"),
    PendingVerification("Chờ xác minh"),
    Completed("Hoàn thành"),
    Rejected("Bị từ chối")
}

enum class ConnectionStatus(val displayName: String) {
    Pending("Chờ phản hồi"),
    Accepted("Đã kết nối"),
    Declined("Đã từ chối"),
    Blocked("Đã chặn")
}

enum class UserJourneyStatus(val displayName: String) {
    Draft("Bản nháp"),
    Active("Đang hoạt động"),
    Paused("Tạm dừng"),
    NeedsUpdate("Cần cập nhật"),
    Completed("Hoàn thành"),
    Cancelled("Đã hủy")
}

enum class JourneyStepProgress(val displayName: String) {
    Locked("Khóa"),
    Recommended("Đề xuất"),
    Planned("Đã lên kế hoạch"),
    InProgress("Đang thực hiện"),
    Completed("Hoàn thành"),
    Skipped("Đã bỏ qua"),
    Unavailable("Không khả dụng")
}

enum class MissionAssignmentStatus(val displayName: String) {
    Locked("Bị khóa"),
    Available("Khả dụng"),
    InProgress("Đang làm"),
    PendingVerification("Chờ duyệt"),
    Completed("Đã xong"),
    Rejected("Bị từ chối"),
    Expired("Hết hạn")
}

enum class TimeCapsuleStatus(val displayName: String) {
    Draft("Bản nháp"),
    Sealed("Đã niêm phong"),
    Locked("Đang khóa"),
    AvailableToOpen("Có thể mở"),
    Opened("Đã mở"),
    Archived("Lưu trữ"),
    Deleted("Đã xóa")
}

enum class TimeCapsuleType(val displayName: String) {
    Personal("Cá nhân"),
    Friend("Bạn bè"),
    Community("Cộng đồng")
}

enum class WaitlistEntryStatus(val displayName: String) {
    Waiting("Đang chờ"),
    Offered("Đã gửi lời mời"),
    Registered("Đã nhận chỗ"),
    Left("Đã rời hàng đợi"),
    Skipped("Đã bỏ qua"),
    Expired("Hết hạn"),
    Cancelled("Đã hủy")
}

enum class SeatOfferStatus(val displayName: String) {
    Pending("Chờ phản hồi"),
    Accepted("Đã chấp nhận"),
    Declined("Đã từ chối"),
    Expired("Hết hạn"),
    Revoked("Đã thu hồi")
}

enum class NotificationType(val displayName: String) {
    System("Hệ thống"),
    EventUpdate("Cập nhật sự kiện"),
    Mission("Nhiệm vụ bí mật"),
    TimeCapsule("Hộp thời gian"),
    SeatOffer("Danh sách chờ"),
    Networking("Kết nối")
}

// ==================== DATA MODELS ====================

data class EventSessionModel(
    val id: String,
    val eventId: String,
    val title: String,
    val speaker: String,
    val room: String,
    val startTime: Long,
    val endTime: Long,
    val isMandatory: Boolean = false
)

data class EventModel(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val location: String,
    val isOnline: Boolean,
    val startTime: Long,
    val endTime: Long,
    val capacity: Int,
    val registeredCount: Int,
    val status: EventStatus,
    val organizerName: String,
    val speakerNames: List<String>,
    val requirements: String,
    val sessions: List<EventSessionModel> = emptyList(),
    val isFeatured: Boolean = false
)

data class RegistrationModel(
    val id: String,
    val eventId: String,
    val userId: String,
    val userName: String,
    val userEmail: String,
    val status: RegistrationStatus,
    val checkInStatus: CheckInStatus,
    val ticketCode: String,
    val registeredAt: Long,
    val checkInAt: Long? = null
)

data class QuestModel(
    val id: String,
    val eventId: String,
    val title: String,
    val description: String,
    val points: Int,
    val maxAttempts: Int,
    val verificationMethod: String,
    val status: QuestStatus,
    val completionStatus: QuestCompletionStatus = QuestCompletionStatus.InProgress
)

data class SecretMissionModel(
    val id: String,
    val eventId: String,
    val title: String,
    val description: String,
    val hints: List<String>,
    val unlockedHintsCount: Int = 0,
    val targetAction: String,
    val rewardPoints: Int,
    val badgeName: String,
    val assignmentStatus: MissionAssignmentStatus,
    val expiresAt: Long,
    val evidenceSubmitted: String? = null,
    val rejectionReason: String? = null,
    val isClaimed: Boolean = false
)

data class UserModel(
    val id: String,
    val name: String,
    val email: String,
    val title: String,
    val company: String,
    val bio: String,
    val interests: List<String>,
    val goals: List<String>,
    val skills: List<String>,
    val eventId: String,
    val matchScore: Int = 85,
    val matchReason: String = "Cùng quan tâm đến AI & Chuyển đổi số"
)

data class NetworkingMatchModel(
    val user: UserModel,
    val matchScore: Int,
    val matchReason: String,
    var connectionStatus: ConnectionStatus
)

data class ChatMessageModel(
    val id: String,
    val sender: String,
    val message: String,
    val timestamp: Long,
    val isFromAi: Boolean,
    val sourceReference: String? = null
)

data class MapMarkerModel(
    val id: String,
    val title: String,
    val category: String, // SanKhau, PhongHoiThao, GianHang, AnUong, VeSinh, HoTro, ThoatHiem
    val xPercent: Float,
    val yPercent: Float,
    val currentActivity: String
)

data class EventMemoryModel(
    val id: String,
    val eventId: String,
    val eventTitle: String,
    val checkInTime: Long,
    val completedQuestsCount: Int,
    val earnedPoints: Int,
    val badgeName: String,
    val certificateTitle: String,
    val connectedUsers: List<String>,
    val isPublic: Boolean = true
)

data class NotificationModel(
    val id: String,
    val title: String,
    val summary: String,
    val type: NotificationType,
    val timestamp: Long,
    val isRead: Boolean,
    val targetId: String? = null
)

data class JourneyStepModel(
    val id: String,
    val journeyId: String,
    val eventId: String,
    val title: String,
    val location: String,
    val startTime: Long,
    val endTime: Long,
    val durationMinutes: Int,
    val isMandatory: Boolean,
    val progress: JourneyStepProgress,
    val fitScore: Int,
    val recommendationReason: String,
    val requiresCheckIn: Boolean = true,
    val isCheckedIn: Boolean = false,
    val prerequisiteStepId: String? = null,
    val skipReason: String? = null
)

data class UserJourneyModel(
    val id: String,
    val userId: String,
    val eventId: String,
    val goal: String,
    val topics: List<String>,
    val availableTime: String,
    val priority: String,
    val status: UserJourneyStatus,
    val steps: List<JourneyStepModel>,
    val progressPercent: Int
)

data class TimeCapsuleModel(
    val id: String,
    val eventId: String,
    val creatorId: String,
    val creatorName: String,
    val recipientName: String,
    val title: String,
    val message: String,
    val type: TimeCapsuleType,
    val status: TimeCapsuleStatus,
    val createdAt: Long,
    val unlockAt: Long,
    val openedAt: Long? = null,
    val isPublic: Boolean = false
)

data class WaitlistEntryModel(
    val id: String,
    val eventId: String,
    val userId: String,
    val userName: String,
    val userEmail: String,
    val queuePosition: Int,
    val joinedAt: Long,
    val status: WaitlistEntryStatus
)

data class SeatOfferModel(
    val id: String,
    val waitlistEntryId: String,
    val eventId: String,
    val eventTitle: String,
    val userId: String,
    val createdAt: Long,
    val expiresAt: Long,
    val status: SeatOfferStatus
)

data class BadgeModel(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val earnedAt: Long? = null
)

data class FaqModel(
    val id: String,
    val question: String,
    val answer: String,
    val category: String,
    val sourceReference: String
)
