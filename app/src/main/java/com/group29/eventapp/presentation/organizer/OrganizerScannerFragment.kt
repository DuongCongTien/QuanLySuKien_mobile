package com.group29.eventapp.presentation.organizer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.group29.eventapp.R
import com.group29.eventapp.common.AppError
import com.group29.eventapp.common.DateTimeUtils
import com.group29.eventapp.common.SessionManager
import com.group29.eventapp.data.mock.FakeRegistrationRepository
import com.group29.eventapp.databinding.DialogScanResultBinding
import com.group29.eventapp.databinding.FragmentOrganizerScannerBinding

class OrganizerScannerFragment : Fragment() {

    private var _binding: FragmentOrganizerScannerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrganizerScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Kiểm tra quyền Ban tổ chức
        if (!SessionManager.isOrganizer()) {
            showPermissionDenied()
            return
        }

        setupSimulationButtons()
    }

    private fun showPermissionDenied() {
        binding.includePermissionDenied.layoutErrorRoot.visibility = View.VISIBLE
        binding.includePermissionDenied.tvErrorMessage.text = AppError.PERMISSION_DENIED.defaultMessage
        binding.includePermissionDenied.btnRetry.text = "Quay lại"
        binding.includePermissionDenied.btnRetry.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupSimulationButtons() {
        // 1. Quét thành công
        binding.btnSimulateSuccess.setOnClickListener {
            // // MOCK: Check-in vé hợp lệ
            showScanResult(
                isSuccess = true,
                title = "Check-in thành công!",
                desc = "Vé hợp lệ. Người tham dự đã được xác thực vào sự kiện.",
                attendee = "Nguyễn Văn A (attendee@group29.vn)",
                ticketCode = "SEH-2026-AI888",
                time = DateTimeUtils.formatDateTime(System.currentTimeMillis())
            )
        }

        // 2. Đã check-in trước đó
        binding.btnSimulateAlreadyChecked.setOnClickListener {
            // // MOCK: Quét vé đã check-in
            showScanResult(
                isSuccess = false,
                title = "Vé đã được check-in!",
                desc = "Cảnh báo: Vé này đã được check-in lúc 08:30 hôm nay. Không thể check-in lần hai!",
                attendee = "Nguyễn Văn A",
                ticketCode = "SEH-2026-AI888",
                time = "Đã vào cổng lúc 08:30"
            )
        }

        // 3. Sai sự kiện
        binding.btnSimulateWrongEvent.setOnClickListener {
            // // MOCK: Quét vé sự kiện khác
            showScanResult(
                isSuccess = false,
                title = "Vé không thuộc sự kiện này!",
                desc = "Mã vé thuộc sự kiện 'Hội thảo Cloud Native'. Vui lòng kiểm tra lại sự kiện của người tham dự.",
                attendee = "Người dùng không xác định",
                ticketCode = "SEH-2026-CLOUD123",
                time = "Không hợp lệ"
            )
        }

        // 4. Đã hủy vé
        binding.btnSimulateCancelled.setOnClickListener {
            // // MOCK: Vé đã bị hủy
            showScanResult(
                isSuccess = false,
                title = "Vé đã bị hủy bỏ!",
                desc = "Người tham dự đã hủy đăng ký sự kiện này trước đó. Vé không còn giá trị vào cổng.",
                attendee = "Nguyễn Văn A",
                ticketCode = "SEH-2026-CANCEL00",
                time = "Đã hủy"
            )
        }

        // 5. Ngoài khung giờ
        binding.btnSimulateOutOfTime.setOnClickListener {
            // // MOCK: Ngoài khung giờ check-in
            showScanResult(
                isSuccess = false,
                title = "Ngoài khung giờ check-in!",
                desc = "Bàn tiếp đón chỉ mở check-in trước giờ diễn ra 60 phút. Vui lòng quay lại sau.",
                attendee = "Trần Đình Khang",
                ticketCode = "SEH-2026-TECH999",
                time = "Chưa đến giờ mở cổng"
            )
        }

        // 6. Không có quyền
        binding.btnSimulateNoPermission.setOnClickListener {
            // // MOCK: Không có quyền quét khu vực VIP
            showScanResult(
                isSuccess = false,
                title = "Không có quyền truy cập khu vực!",
                desc = "Vé thường không có quyền vào Khu vực VIP / Speaker Lounge.",
                attendee = "Phạm Thị Quỳnh Nga",
                ticketCode = "SEH-2026-VIP-REQ",
                time = "Từ chối quyền truy cập"
            )
        }

        // 7. Mất kết nối mạng
        binding.btnSimulateNetworkError.setOnClickListener {
            // // MOCK: Giả lập lỗi mất mạng
            showScanResult(
                isSuccess = false,
                title = "Lỗi kết nối mạng!",
                desc = AppError.NETWORK_SIMULATED_ERROR.defaultMessage,
                attendee = "Chưa xác định",
                ticketCode = "UNKNOWN",
                time = "Không thể đồng bộ dữ liệu"
            )
        }
    }

    private fun showScanResult(
        isSuccess: Boolean,
        title: String,
        desc: String,
        attendee: String,
        ticketCode: String,
        time: String
    ) {
        val bottomSheet = BottomSheetDialog(requireContext())
        val dialogBinding = DialogScanResultBinding.inflate(layoutInflater)
        bottomSheet.setContentView(dialogBinding.root)

        with(dialogBinding) {
            tvScanResultTitle.text = title
            tvScanResultDesc.text = desc
            tvAttendeeName.text = "Người tham dự: $attendee"
            tvTicketCode.text = "Mã vé: $ticketCode"
            tvCheckInTime.text = "Thời gian: $time"

            if (isSuccess) {
                ivScanResultIcon.setImageResource(R.drawable.ic_check)
                ivScanResultIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.status_success))
                tvScanResultTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_success))
            } else {
                ivScanResultIcon.setImageResource(R.drawable.ic_error)
                ivScanResultIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.status_error))
                tvScanResultTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_error))
            }

            btnDismissScanResult.setOnClickListener {
                bottomSheet.dismiss()
            }
        }

        bottomSheet.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
