package com.group29.eventapp.common

enum class AppError(val defaultMessage: String) {
    NOT_AUTHENTICATED("Bạn chưa đăng nhập hoặc phiên làm việc đã hết hạn. Vui lòng đăng nhập lại."),
    PERMISSION_DENIED("Bạn không có quyền truy cập khu vực này. Tính năng dành riêng cho Ban tổ chức."),
    EVENT_CLOSED("Sự kiện đã đóng đăng ký hoặc đã kết thúc. Không thể thực hiện thao tác này."),
    CAPACITY_FULL("Sự kiện đã hết chỗ đăng ký chính thức. Bạn có thể tham gia danh sách chờ."),
    OFFER_EXPIRED("Rất tiếc! Lời mời nhận chỗ đã hết hiệu lực. Chỗ ngồi đã được chuyển cho người tiếp theo."),
    ALREADY_REGISTERED("Bạn đã đăng ký tham gia sự kiện này rồi."),
    ALREADY_COMPLETED("Nhiệm vụ này đã được hoàn thành trước đó. Bạn không thể nhận thưởng 2 lần."),
    CAPSULE_LOCKED("Hộp thời gian đang trong trạng thái niêm phong và chưa tới thời điểm mở khóa."),
    INVALID_EVIDENCE("Bằng chứng gửi lên không hợp lệ hoặc chưa đáp ứng yêu cầu nhiệm vụ."),
    NETWORK_SIMULATED_ERROR("Lỗi kết nối giả lập: Không thể kết nối tới máy chủ. Vui lòng kiểm tra cài đặt mạng.")
}
