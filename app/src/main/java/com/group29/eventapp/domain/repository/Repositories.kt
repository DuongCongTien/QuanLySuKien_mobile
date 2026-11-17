package com.group29.eventapp.domain.repository

import com.group29.eventapp.data.model.*
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun getEvents(): Flow<List<EventModel>>
    suspend fun getEventById(id: String): EventModel?
    fun searchEvents(query: String, category: String?, status: EventStatus?, isOnline: Boolean?): Flow<List<EventModel>>
    suspend fun createEvent(event: EventModel): Boolean
    suspend fun updateEvent(event: EventModel): Boolean
    suspend fun cancelEvent(id: String): Boolean
}

interface RegistrationRepository {
    fun getUserRegistrations(userId: String): Flow<List<RegistrationModel>>
    suspend fun getRegistration(userId: String, eventId: String): RegistrationModel?
    suspend fun registerEvent(userId: String, userName: String, userEmail: String, eventId: String): Result<RegistrationModel>
    suspend fun cancelRegistration(registrationId: String): Boolean
    suspend fun verifyAndCheckInTicket(ticketCode: String): Result<RegistrationModel>
    fun getEventAttendees(eventId: String): Flow<List<RegistrationModel>>
}

interface QuestRepository {
    fun getQuests(eventId: String): Flow<List<QuestModel>>
    fun getSecretMissions(eventId: String): Flow<List<SecretMissionModel>>
    suspend fun unlockMissionHint(missionId: String): Result<SecretMissionModel>
    suspend fun submitMissionEvidence(missionId: String, evidence: String): Result<SecretMissionModel>
    suspend fun reviewMission(missionId: String, isApproved: Boolean, rejectionReason: String?): Result<SecretMissionModel>
    suspend fun claimMissionReward(missionId: String): Result<SecretMissionModel>
    suspend fun createQuest(quest: QuestModel): Boolean
}

interface NetworkingRepository {
    fun getSuggestedMatches(eventId: String, userId: String): Flow<List<NetworkingMatchModel>>
    fun getPendingInvitations(): Flow<List<NetworkingMatchModel>>
    suspend fun sendInvitation(targetUserId: String): Boolean
    suspend fun respondToInvitation(targetUserId: String, isAccepted: Boolean): Boolean
}

interface AssistantRepository {
    fun getMessages(): Flow<List<ChatMessageModel>>
    suspend fun askAssistant(question: String): ChatMessageModel
}

interface JourneyRepository {
    fun getUserJourney(eventId: String, userId: String): Flow<UserJourneyModel?>
    suspend fun setupJourney(eventId: String, userId: String, goal: String, topics: List<String>, availableTime: String, priority: String): Result<UserJourneyModel>
    suspend fun checkInStep(stepId: String): Result<JourneyStepModel>
    suspend fun completeStep(stepId: String): Result<JourneyStepModel>
    suspend fun skipStep(stepId: String, reason: String): Result<JourneyStepModel>
    suspend fun simulateScheduleChange(eventId: String): Boolean
}

interface TimeCapsuleRepository {
    fun getCapsules(userId: String): Flow<List<TimeCapsuleModel>>
    suspend fun getCapsuleById(capsuleId: String): TimeCapsuleModel?
    suspend fun createCapsule(capsule: TimeCapsuleModel): Result<TimeCapsuleModel>
    suspend fun openCapsule(capsuleId: String): Result<TimeCapsuleModel>
}

interface WaitlistRepository {
    fun getWaitlistEntries(eventId: String): Flow<List<WaitlistEntryModel>>
    suspend fun getUserWaitlistEntry(eventId: String, userId: String): WaitlistEntryModel?
    suspend fun joinWaitlist(eventId: String, userId: String, userName: String, userEmail: String): Result<WaitlistEntryModel>
    suspend fun leaveWaitlist(entryId: String): Boolean
    fun getActiveSeatOffer(userId: String): Flow<SeatOfferModel?>
    suspend fun respondToSeatOffer(offerId: String, isAccepted: Boolean): Result<RegistrationModel?>
}
