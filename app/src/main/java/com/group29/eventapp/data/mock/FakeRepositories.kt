package com.group29.eventapp.data.mock

import com.group29.eventapp.common.AppError
import com.group29.eventapp.common.SessionManager
import com.group29.eventapp.data.model.*
import com.group29.eventapp.domain.repository.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

// ==================== FAKE EVENT REPOSITORY ====================

object FakeEventRepository : EventRepository {
    private val _events = MutableStateFlow<List<EventModel>>(MockData.sampleEvents)

    override fun getEvents(): Flow<List<EventModel>> = _events.asStateFlow()

    override suspend fun getEventById(id: String): EventModel? {
        // MOCK: Mô phỏng độ trễ truy xuất dữ liệu từ bộ nhớ
        delay(700)
        checkSimulatedNetworkError()
        return _events.value.find { it.id == id }
    }

    override fun searchEvents(
        query: String,
        category: String?,
        status: EventStatus?,
        isOnline: Boolean?
    ): Flow<List<EventModel>> {
        return _events.map { list ->
            list.filter { event ->
                val matchQuery = query.isBlank() || event.title.contains(query, ignoreCase = true) ||
                        event.description.contains(query, ignoreCase = true) ||
                        event.location.contains(query, ignoreCase = true)
                val matchCategory = category.isNullOrBlank() || category == "Tất cả" || event.category == category
                val matchStatus = status == null || event.status == status
                val matchOnline = isOnline == null || event.isOnline == isOnline
                matchQuery && matchCategory && matchStatus && matchOnline
            }
        }
    }

    override suspend fun createEvent(event: EventModel): Boolean {
        // MOCK: Giả lập lưu sự kiện mới
        delay(800)
        checkSimulatedNetworkError()
        val current = _events.value.toMutableList()
        current.add(0, event)
        _events.value = current
        return true
    }

    override suspend fun updateEvent(event: EventModel): Boolean {
        // MOCK: Giả lập cập nhật sự kiện
        delay(800)
        checkSimulatedNetworkError()
        val current = _events.value.toMutableList()
        val index = current.indexOfFirst { it.id == event.id }
        if (index != -1) {
            current[index] = event
            _events.value = current
            return true
        }
        return false
    }

    override suspend fun cancelEvent(id: String): Boolean {
        // MOCK: Giả lập hủy sự kiện
        delay(750)
        checkSimulatedNetworkError()
        val current = _events.value.toMutableList()
        val index = current.indexOfFirst { it.id == id }
        if (index != -1) {
            current[index] = current[index].copy(status = EventStatus.Cancelled)
            _events.value = current
            return true
        }
        return false
    }

    private fun checkSimulatedNetworkError() {
        if (SessionManager.simulateNetworkError.value) {
            throw RuntimeException(AppError.NETWORK_SIMULATED_ERROR.defaultMessage)
        }
    }
}

// ==================== FAKE REGISTRATION REPOSITORY ====================

object FakeRegistrationRepository : RegistrationRepository {
    private val _registrations = MutableStateFlow<List<RegistrationModel>>(MockData.sampleRegistrations)

    override fun getUserRegistrations(userId: String): Flow<List<RegistrationModel>> {
        return _registrations.map { list -> list.filter { it.userId == userId } }
    }

    override suspend fun getRegistration(userId: String, eventId: String): RegistrationModel? {
        // MOCK: Kiểm tra vé đã đăng ký
        delay(650)
        checkSimulatedNetworkError()
        return _registrations.value.find { it.userId == userId && it.eventId == eventId && it.status == RegistrationStatus.Registered }
    }

    override suspend fun registerEvent(
        userId: String,
        userName: String,
        userEmail: String,
        eventId: String
    ): Result<RegistrationModel> {
        // MOCK: Giả lập đăng ký sự kiện mới
        delay(900)
        checkSimulatedNetworkError()

        val event = FakeEventRepository.getEventById(eventId)
            ?: return Result.failure(Exception(AppError.EVENT_CLOSED.defaultMessage))

        if (event.status == EventStatus.Cancelled || event.status == EventStatus.RegistrationClosed) {
            return Result.failure(Exception(AppError.EVENT_CLOSED.defaultMessage))
        }

        if (event.registeredCount >= event.capacity) {
            return Result.failure(Exception(AppError.CAPACITY_FULL.defaultMessage))
        }

        val existing = _registrations.value.find {
            it.userId == userId && it.eventId == eventId && it.status == RegistrationStatus.Registered
        }
        if (existing != null) {
            return Result.failure(Exception(AppError.ALREADY_REGISTERED.defaultMessage))
        }

        val newReg = RegistrationModel(
            id = "reg_${System.currentTimeMillis()}",
            eventId = eventId,
            userId = userId,
            userName = userName,
            userEmail = userEmail,
            status = RegistrationStatus.Registered,
            checkInStatus = CheckInStatus.NotCheckedIn,
            ticketCode = "SEH-${(1000..9999).random()}-${eventId.uppercase()}",
            registeredAt = System.currentTimeMillis()
        )

        val updated = _registrations.value.toMutableList()
        updated.add(0, newReg)
        _registrations.value = updated

        // Cập nhật số lượng người đã đăng ký sự kiện
        FakeEventRepository.updateEvent(event.copy(registeredCount = event.registeredCount + 1))

        return Result.success(newReg)
    }

    override suspend fun cancelRegistration(registrationId: String): Boolean {
        // MOCK: Giả lập hủy vé và tự động kích hoạt ghế trống cho Waitlist (FIFO)
        delay(850)
        checkSimulatedNetworkError()
        val list = _registrations.value.toMutableList()
        val index = list.indexOfFirst { it.id == registrationId }
        if (index != -1) {
            val reg = list[index]
            list[index] = reg.copy(status = RegistrationStatus.Cancelled)
            _registrations.value = list

            // Giảm số người đăng ký
            FakeEventRepository.getEventById(reg.eventId)?.let { ev ->
                FakeEventRepository.updateEvent(ev.copy(registeredCount = maxOf(0, ev.registeredCount - 1)))
            }

            // Kích hoạt phục hồi ghế trống cho người đầu danh sách chờ
            FakeWaitlistRepository.offerSeatToNextInQueue(reg.eventId)
            return true
        }
        return false
    }

    override suspend fun verifyAndCheckInTicket(ticketCode: String): Result<RegistrationModel> {
        // MOCK: Giả lập quét QR check-in
        delay(700)
        checkSimulatedNetworkError()

        val list = _registrations.value.toMutableList()
        val index = list.indexOfFirst { it.ticketCode.equals(ticketCode, ignoreCase = true) }

        if (index == -1) {
            return Result.failure(Exception("Mã vé không tồn tại trong hệ thống!"))
        }

        val reg = list[index]
        if (reg.status == RegistrationStatus.Cancelled) {
            return Result.failure(Exception("Vé này đã bị hủy bỏ trước đó."))
        }

        if (reg.checkInStatus == CheckInStatus.CheckedIn) {
            return Result.failure(Exception("Vé đã được check-in lúc ${reg.checkInAt ?: "trước đó"}. Không thể check-in lại!"))
        }

        val updatedReg = reg.copy(
            checkInStatus = CheckInStatus.CheckedIn,
            checkInAt = System.currentTimeMillis()
        )
        list[index] = updatedReg
        _registrations.value = list
        return Result.success(updatedReg)
    }

    override fun getEventAttendees(eventId: String): Flow<List<RegistrationModel>> {
        return _registrations.map { list -> list.filter { it.eventId == eventId } }
    }

    fun addRegistrationDirect(reg: RegistrationModel) {
        val updated = _registrations.value.toMutableList()
        updated.add(0, reg)
        _registrations.value = updated
    }

    private fun checkSimulatedNetworkError() {
        if (SessionManager.simulateNetworkError.value) {
            throw RuntimeException(AppError.NETWORK_SIMULATED_ERROR.defaultMessage)
        }
    }
}

// ==================== FAKE QUEST REPOSITORY ====================

object FakeQuestRepository : QuestRepository {
    private val _quests = MutableStateFlow<List<QuestModel>>(MockData.sampleQuests)
    private val _secretMissions = MutableStateFlow<List<SecretMissionModel>>(MockData.sampleSecretMissions)

    override fun getQuests(eventId: String): Flow<List<QuestModel>> {
        return _quests.map { list -> list.filter { it.eventId == eventId } }
    }

    override fun getSecretMissions(eventId: String): Flow<List<SecretMissionModel>> {
        return _secretMissions.map { list -> list.filter { it.eventId == eventId } }
    }

    override suspend fun unlockMissionHint(missionId: String): Result<SecretMissionModel> {
        // MOCK: Mở từng gợi ý
        delay(600)
        checkSimulatedNetworkError()
        val list = _secretMissions.value.toMutableList()
        val index = list.indexOfFirst { it.id == missionId }
        if (index != -1) {
            val mission = list[index]
            val nextHintCount = minOf(mission.hints.size, mission.unlockedHintsCount + 1)
            val updated = mission.copy(
                unlockedHintsCount = nextHintCount,
                assignmentStatus = if (mission.assignmentStatus == MissionAssignmentStatus.Available)
                    MissionAssignmentStatus.InProgress else mission.assignmentStatus
            )
            list[index] = updated
            _secretMissions.value = list
            return Result.success(updated)
        }
        return Result.failure(Exception("Không tìm thấy nhiệm vụ."))
    }

    override suspend fun submitMissionEvidence(missionId: String, evidence: String): Result<SecretMissionModel> {
        // MOCK: Gửi bằng chứng xác minh
        delay(850)
        checkSimulatedNetworkError()
        val list = _secretMissions.value.toMutableList()
        val index = list.indexOfFirst { it.id == missionId }
        if (index != -1) {
            val updated = list[index].copy(
                evidenceSubmitted = evidence,
                assignmentStatus = MissionAssignmentStatus.PendingVerification
            )
            list[index] = updated
            _secretMissions.value = list
            return Result.success(updated)
        }
        return Result.failure(Exception("Không tìm thấy nhiệm vụ."))
    }

    override suspend fun reviewMission(
        missionId: String,
        isApproved: Boolean,
        rejectionReason: String?
    ): Result<SecretMissionModel> {
        // MOCK: BTC phê duyệt hoặc từ chối bằng chứng
        delay(800)
        checkSimulatedNetworkError()
        val list = _secretMissions.value.toMutableList()
        val index = list.indexOfFirst { it.id == missionId }
        if (index != -1) {
            val updated = list[index].copy(
                assignmentStatus = if (isApproved) MissionAssignmentStatus.Completed else MissionAssignmentStatus.Rejected,
                rejectionReason = rejectionReason
            )
            list[index] = updated
            _secretMissions.value = list
            return Result.success(updated)
        }
        return Result.failure(Exception("Không tìm thấy nhiệm vụ."))
    }

    override suspend fun claimMissionReward(missionId: String): Result<SecretMissionModel> {
        // MOCK: Nhận thưởng một lần duy nhất
        delay(650)
        checkSimulatedNetworkError()
        val list = _secretMissions.value.toMutableList()
        val index = list.indexOfFirst { it.id == missionId }
        if (index != -1) {
            val mission = list[index]
            if (mission.isClaimed) {
                return Result.failure(Exception(AppError.ALREADY_COMPLETED.defaultMessage))
            }
            val updated = mission.copy(isClaimed = true)
            list[index] = updated
            _secretMissions.value = list
            return Result.success(updated)
        }
        return Result.failure(Exception("Không tìm thấy nhiệm vụ."))
    }

    override suspend fun createQuest(quest: QuestModel): Boolean {
        // MOCK: BTC tạo nhiệm vụ mới
        delay(700)
        checkSimulatedNetworkError()
        val list = _quests.value.toMutableList()
        list.add(0, quest)
        _quests.value = list
        return true
    }

    private fun checkSimulatedNetworkError() {
        if (SessionManager.simulateNetworkError.value) {
            throw RuntimeException(AppError.NETWORK_SIMULATED_ERROR.defaultMessage)
        }
    }
}

// ==================== FAKE NETWORKING REPOSITORY ====================

object FakeNetworkingRepository : NetworkingRepository {
    private val _matches = MutableStateFlow<List<NetworkingMatchModel>>(
        MockData.sampleUsers.mapIndexed { idx, user ->
            NetworkingMatchModel(
                user = user,
                matchScore = user.matchScore,
                matchReason = user.matchReason,
                connectionStatus = if (idx == 0) ConnectionStatus.Pending else ConnectionStatus.Pending
            )
        }
    )

    override fun getSuggestedMatches(eventId: String, userId: String): Flow<List<NetworkingMatchModel>> {
        return _matches.asStateFlow()
    }

    override fun getPendingInvitations(): Flow<List<NetworkingMatchModel>> {
        return _matches.map { list -> list.filter { it.connectionStatus == ConnectionStatus.Pending } }
    }

    override suspend fun sendInvitation(targetUserId: String): Boolean {
        // MOCK: Gửi lời mời kết nối
        delay(600)
        checkSimulatedNetworkError()
        val list = _matches.value.toMutableList()
        val index = list.indexOfFirst { it.user.id == targetUserId }
        if (index != -1) {
            list[index] = list[index].copy(connectionStatus = ConnectionStatus.Pending)
            _matches.value = list
            return true
        }
        return false
    }

    override suspend fun respondToInvitation(targetUserId: String, isAccepted: Boolean): Boolean {
        // MOCK: Chấp nhận hoặc từ chối kết nối
        delay(600)
        checkSimulatedNetworkError()
        val list = _matches.value.toMutableList()
        val index = list.indexOfFirst { it.user.id == targetUserId }
        if (index != -1) {
            list[index] = list[index].copy(
                connectionStatus = if (isAccepted) ConnectionStatus.Accepted else ConnectionStatus.Declined
            )
            _matches.value = list
            return true
        }
        return false
    }

    private fun checkSimulatedNetworkError() {
        if (SessionManager.simulateNetworkError.value) {
            throw RuntimeException(AppError.NETWORK_SIMULATED_ERROR.defaultMessage)
        }
    }
}

// ==================== FAKE ASSISTANT REPOSITORY ====================

object FakeAssistantRepository : AssistantRepository {
    private val _messages = MutableStateFlow<List<ChatMessageModel>>(
        listOf(
            ChatMessageModel(
                id = "msg_init",
                sender = "AI Assistant",
                message = "Xin chào! Tôi là Trợ lý sự kiện thông minh. Bạn có thể hỏi tôi về lịch trình, vị trí gian hàng, mật khẩu wifi hoặc quy định check-in.",
                timestamp = System.currentTimeMillis() - 60000,
                isFromAi = true,
                sourceReference = "Hệ thống AI Event Hub"
            )
        )
    )

    override fun getMessages(): Flow<List<ChatMessageModel>> = _messages.asStateFlow()

    override suspend fun askAssistant(question: String): ChatMessageModel {
        // MOCK: Phản hồi AI dựa trên dữ liệu FAQ thực tế, không bịa
        delay(950)
        if (SessionManager.simulateNetworkError.value) {
            throw RuntimeException(AppError.NETWORK_SIMULATED_ERROR.defaultMessage)
        }

        val userMsg = ChatMessageModel(
            id = "user_${System.currentTimeMillis()}",
            sender = "Bạn",
            message = question,
            timestamp = System.currentTimeMillis(),
            isFromAi = false
        )
        val curList = _messages.value.toMutableList()
        curList.add(userMsg)
        _messages.value = curList

        // Tìm kiếm trong FAQ
        val normalizedQuestion = question.lowercase()
        val matchedFaq = MockData.sampleFaqs.find { faq ->
            val keywords = faq.question.lowercase().split(" ", ",", "?")
                .filter { it.length > 2 }
            keywords.any { normalizedQuestion.contains(it) }
        }

        val aiResponse = if (matchedFaq != null) {
            ChatMessageModel(
                id = "ai_${System.currentTimeMillis()}",
                sender = "AI Assistant",
                message = matchedFaq.answer,
                timestamp = System.currentTimeMillis(),
                isFromAi = true,
                sourceReference = matchedFaq.sourceReference
            )
        } else {
            ChatMessageModel(
                id = "ai_${System.currentTimeMillis()}",
                sender = "AI Assistant",
                message = "Hiện tại hệ thống chưa có thông tin chính thức về câu hỏi này từ Ban tổ chức. Vui lòng liên hệ Bàn hỗ trợ tại Sảnh chính để được giải đáp trực tiếp.",
                timestamp = System.currentTimeMillis(),
                isFromAi = true,
                sourceReference = "Thông tin chưa xác thực"
            )
        }

        curList.add(aiResponse)
        _messages.value = curList
        return aiResponse
    }
}

// ==================== FAKE JOURNEY REPOSITORY ====================

object FakeJourneyRepository : JourneyRepository {
    private val _journey = MutableStateFlow<UserJourneyModel?>(MockData.sampleUserJourney)

    override fun getUserJourney(eventId: String, userId: String): Flow<UserJourneyModel?> = _journey.asStateFlow()

    override suspend fun setupJourney(
        eventId: String,
        userId: String,
        goal: String,
        topics: List<String>,
        availableTime: String,
        priority: String
    ): Result<UserJourneyModel> {
        // MOCK: Khởi tạo lộ trình cá nhân
        delay(1100)
        checkSimulatedNetworkError()

        val newJourney = UserJourneyModel(
            id = "journey_${System.currentTimeMillis()}",
            userId = userId,
            eventId = eventId,
            goal = goal,
            topics = topics,
            availableTime = availableTime,
            priority = priority,
            status = UserJourneyStatus.Active,
            steps = MockData.sampleJourneySteps,
            progressPercent = 25
        )
        _journey.value = newJourney
        return Result.success(newJourney)
    }

    override suspend fun checkInStep(stepId: String): Result<JourneyStepModel> {
        // MOCK: Check-in bước hành trình
        delay(600)
        checkSimulatedNetworkError()
        val journey = _journey.value ?: return Result.failure(Exception("Chưa có hành trình."))
        val steps = journey.steps.toMutableList()
        val index = steps.indexOfFirst { it.id == stepId }
        if (index != -1) {
            val updatedStep = steps[index].copy(
                isCheckedIn = true,
                progress = JourneyStepProgress.InProgress
            )
            steps[index] = updatedStep
            _journey.value = journey.copy(steps = steps)
            return Result.success(updatedStep)
        }
        return Result.failure(Exception("Không tìm thấy bước."))
    }

    override suspend fun completeStep(stepId: String): Result<JourneyStepModel> {
        // MOCK: Đánh dấu hoàn thành bước (yêu cầu đã check-in nếu có quy định)
        delay(700)
        checkSimulatedNetworkError()
        val journey = _journey.value ?: return Result.failure(Exception("Chưa có hành trình."))
        val steps = journey.steps.toMutableList()
        val index = steps.indexOfFirst { it.id == stepId }
        if (index != -1) {
            val step = steps[index]
            if (step.requiresCheckIn && !step.isCheckedIn) {
                return Result.failure(Exception("Bạn cần check-in có mặt tại địa điểm trước khi đánh dấu hoàn thành bước này."))
            }
            val updatedStep = step.copy(progress = JourneyStepProgress.Completed)
            steps[index] = updatedStep

            val completedCount = steps.count { it.progress == JourneyStepProgress.Completed }
            val newPercent = (completedCount * 100) / steps.size

            _journey.value = journey.copy(steps = steps, progressPercent = newPercent)
            return Result.success(updatedStep)
        }
        return Result.failure(Exception("Không tìm thấy bước."))
    }

    override suspend fun skipStep(stepId: String, reason: String): Result<JourneyStepModel> {
        // MOCK: Bỏ qua bước tùy chọn kèm lý do
        delay(600)
        checkSimulatedNetworkError()
        val journey = _journey.value ?: return Result.failure(Exception("Chưa có hành trình."))
        val steps = journey.steps.toMutableList()
        val index = steps.indexOfFirst { it.id == stepId }
        if (index != -1) {
            val step = steps[index]
            if (step.isMandatory) {
                return Result.failure(Exception("Không thể bỏ qua hoạt động bắt buộc trong sự kiện."))
            }
            val updatedStep = step.copy(
                progress = JourneyStepProgress.Skipped,
                skipReason = reason
            )
            steps[index] = updatedStep
            _journey.value = journey.copy(steps = steps)
            return Result.success(updatedStep)
        }
        return Result.failure(Exception("Không tìm thấy bước."))
    }

    override suspend fun simulateScheduleChange(eventId: String): Boolean {
        // MOCK: Kích hoạt trạng thái NeedsUpdate khi BTC thay đổi lịch phiên
        delay(800)
        checkSimulatedNetworkError()
        val journey = _journey.value ?: return false
        _journey.value = journey.copy(status = UserJourneyStatus.NeedsUpdate)
        return true
    }

    private fun checkSimulatedNetworkError() {
        if (SessionManager.simulateNetworkError.value) {
            throw RuntimeException(AppError.NETWORK_SIMULATED_ERROR.defaultMessage)
        }
    }
}

// ==================== FAKE TIME CAPSULE REPOSITORY ====================

object FakeTimeCapsuleRepository : TimeCapsuleRepository {
    private val _capsules = MutableStateFlow<List<TimeCapsuleModel>>(MockData.sampleTimeCapsules)

    override fun getCapsules(userId: String): Flow<List<TimeCapsuleModel>> {
        return _capsules.asStateFlow()
    }

    override suspend fun getCapsuleById(capsuleId: String): TimeCapsuleModel? {
        // MOCK: Lấy chi tiết capsule
        delay(600)
        checkSimulatedNetworkError()
        return _capsules.value.find { it.id == capsuleId }
    }

    override suspend fun createCapsule(capsule: TimeCapsuleModel): Result<TimeCapsuleModel> {
        // MOCK: Tạo và niêm phong hộp thời gian
        delay(950)
        checkSimulatedNetworkError()

        if (capsule.unlockAt <= System.currentTimeMillis()) {
            return Result.failure(Exception("Thời điểm mở khóa bắt buộc phải lớn hơn thời gian hiện tại."))
        }

        val list = _capsules.value.toMutableList()
        list.add(0, capsule)
        _capsules.value = list
        return Result.success(capsule)
    }

    override suspend fun openCapsule(capsuleId: String): Result<TimeCapsuleModel> {
        // MOCK: Mở hộp thời gian khi đến hạn unlockAt
        delay(850)
        checkSimulatedNetworkError()

        val list = _capsules.value.toMutableList()
        val index = list.indexOfFirst { it.id == capsuleId }
        if (index != -1) {
            val capsule = list[index]
            if (System.currentTimeMillis() < capsule.unlockAt) {
                return Result.failure(Exception(AppError.CAPSULE_LOCKED.defaultMessage))
            }
            val updated = capsule.copy(
                status = TimeCapsuleStatus.Opened,
                openedAt = System.currentTimeMillis()
            )
            list[index] = updated
            _capsules.value = list
            return Result.success(updated)
        }
        return Result.failure(Exception("Không tìm thấy Hộp thời gian."))
    }

    private fun checkSimulatedNetworkError() {
        if (SessionManager.simulateNetworkError.value) {
            throw RuntimeException(AppError.NETWORK_SIMULATED_ERROR.defaultMessage)
        }
    }
}

// ==================== FAKE WAITLIST REPOSITORY ====================

object FakeWaitlistRepository : WaitlistRepository {
    private val _waitlistEntries = MutableStateFlow<List<WaitlistEntryModel>>(MockData.sampleWaitlistEntries)
    private val _seatOffer = MutableStateFlow<SeatOfferModel?>(MockData.sampleSeatOffer)

    override fun getWaitlistEntries(eventId: String): Flow<List<WaitlistEntryModel>> {
        return _waitlistEntries.map { list -> list.filter { it.eventId == eventId } }
    }

    override suspend fun getUserWaitlistEntry(eventId: String, userId: String): WaitlistEntryModel? {
        // MOCK: Lấy vị trí của user trong hàng đợi
        delay(600)
        checkSimulatedNetworkError()
        return _waitlistEntries.value.find { it.eventId == eventId && it.userId == userId && it.status == WaitlistEntryStatus.Waiting }
    }

    override suspend fun joinWaitlist(
        eventId: String,
        userId: String,
        userName: String,
        userEmail: String
    ): Result<WaitlistEntryModel> {
        // MOCK: Tham gia danh sách chờ (FIFO)
        delay(850)
        checkSimulatedNetworkError()

        val existing = _waitlistEntries.value.find {
            it.eventId == eventId && it.userId == userId && (it.status == WaitlistEntryStatus.Waiting || it.status == WaitlistEntryStatus.Offered)
        }
        if (existing != null) {
            return Result.failure(Exception("Bạn đã có mặt trong danh sách chờ của sự kiện này."))
        }

        val queuePos = _waitlistEntries.value.count { it.eventId == eventId && it.status == WaitlistEntryStatus.Waiting } + 1
        val newEntry = WaitlistEntryModel(
            id = "wl_${System.currentTimeMillis()}",
            eventId = eventId,
            userId = userId,
            userName = userName,
            userEmail = userEmail,
            queuePosition = queuePos,
            joinedAt = System.currentTimeMillis(),
            status = WaitlistEntryStatus.Waiting
        )

        val list = _waitlistEntries.value.toMutableList()
        list.add(newEntry)
        _waitlistEntries.value = list
        return Result.success(newEntry)
    }

    override suspend fun leaveWaitlist(entryId: String): Boolean {
        // MOCK: Rời danh sách chờ
        delay(650)
        checkSimulatedNetworkError()
        val list = _waitlistEntries.value.toMutableList()
        val index = list.indexOfFirst { it.id == entryId }
        if (index != -1) {
            list[index] = list[index].copy(status = WaitlistEntryStatus.Left)
            _waitlistEntries.value = list
            return true
        }
        return false
    }

    override fun getActiveSeatOffer(userId: String): Flow<SeatOfferModel?> = _seatOffer.asStateFlow()

    override suspend fun respondToSeatOffer(offerId: String, isAccepted: Boolean): Result<RegistrationModel?> {
        // MOCK: Chấp nhận hoặc từ chối lời mời nhận chỗ
        delay(900)
        checkSimulatedNetworkError()

        val offer = _seatOffer.value ?: return Result.failure(Exception("Không có lời mời nào."))
        if (offer.id != offerId) return Result.failure(Exception("Mã lời mời không khớp."))

        // Kiểm tra hết hạn (15 phút)
        if (System.currentTimeMillis() > offer.expiresAt) {
            _seatOffer.value = offer.copy(status = SeatOfferStatus.Expired)
            return Result.failure(Exception(AppError.OFFER_EXPIRED.defaultMessage))
        }

        if (isAccepted) {
            _seatOffer.value = offer.copy(status = SeatOfferStatus.Accepted)

            // Tạo Registration mới cho người dùng
            val newReg = RegistrationModel(
                id = "reg_recovered_${System.currentTimeMillis()}",
                eventId = offer.eventId,
                userId = offer.userId,
                userName = SessionManager.currentUserName.value,
                userEmail = SessionManager.currentUserEmail.value,
                status = RegistrationStatus.Registered,
                checkInStatus = CheckInStatus.NotCheckedIn,
                ticketCode = "SEH-SEAT-RECOVERED-${(1000..9999).random()}",
                registeredAt = System.currentTimeMillis()
            )
            FakeRegistrationRepository.addRegistrationDirect(newReg)

            // Cập nhật waitlist entry sang Registered
            val wlList = _waitlistEntries.value.toMutableList()
            val wlIndex = wlList.indexOfFirst { it.id == offer.waitlistEntryId }
            if (wlIndex != -1) {
                wlList[wlIndex] = wlList[wlIndex].copy(status = WaitlistEntryStatus.Registered)
                _waitlistEntries.value = wlList
            }

            return Result.success(newReg)
        } else {
            _seatOffer.value = offer.copy(status = SeatOfferStatus.Declined)
            // Chuyển ghế cho người tiếp theo
            offerSeatToNextInQueue(offer.eventId)
            return Result.success(null)
        }
    }

    fun offerSeatToNextInQueue(eventId: String) {
        val list = _waitlistEntries.value.toMutableList()
        val nextInQueue = list.firstOrNull { it.eventId == eventId && it.status == WaitlistEntryStatus.Waiting }
        if (nextInQueue != null) {
            val index = list.indexOf(nextInQueue)
            list[index] = nextInQueue.copy(status = WaitlistEntryStatus.Offered)
            _waitlistEntries.value = list

            _seatOffer.value = SeatOfferModel(
                id = "offer_${System.currentTimeMillis()}",
                waitlistEntryId = nextInQueue.id,
                eventId = eventId,
                eventTitle = "Workshop: Thiết kế Hệ thống Di động & Microservices",
                userId = nextInQueue.userId,
                createdAt = System.currentTimeMillis(),
                expiresAt = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(15),
                status = SeatOfferStatus.Pending
            )
        }
    }

    private fun checkSimulatedNetworkError() {
        if (SessionManager.simulateNetworkError.value) {
            throw RuntimeException(AppError.NETWORK_SIMULATED_ERROR.defaultMessage)
        }
    }
}
