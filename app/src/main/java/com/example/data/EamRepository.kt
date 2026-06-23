package com.example.data

import kotlinx.coroutines.flow.Flow

class EamRepository(private val db: EamDatabase) {
    val userFlow: Flow<UserEntity?> = db.userDao().getUserFlow()
    val allUsersFlow: Flow<List<UserEntity>> = db.userDao().getAllUsersFlow()
    val messagesFlow: Flow<List<MessageEntity>> = db.messageDao().getMessagesFlow()
    val premiumRequestsFlow: Flow<List<PremiumRequestEntity>> = db.premiumRequestDao().getRequestsFlow()
    val settingsFlow: Flow<AppSettingsEntity?> = db.appSettingsDao().getSettingsFlow()

    suspend fun getUserSync(): UserEntity? = db.userDao().getUserSync()
    suspend fun insertOrUpdateUser(user: UserEntity) = db.userDao().insertOrUpdateUser(user)
    suspend fun searchUsers(query: String) = db.userDao().searchUsers(query)
    suspend fun getAllUsers() = db.userDao().getAllUsers()
    suspend fun updateUser(user: UserEntity) = db.userDao().updateUser(user)

    suspend fun insertMessage(msg: MessageEntity) = db.messageDao().insertMessage(msg)
    suspend fun clearAllMessages() = db.messageDao().clearAllMessages()

    fun getCertificatesForUser(username: String): Flow<List<CertificateEntity>> =
        db.certificateDao().getCertificatesForUser(username)
    fun getAllCertificatesFlow(): Flow<List<CertificateEntity>> =
        db.certificateDao().getAllCertificatesFlow()
    suspend fun getAllCertificatesSync() = db.certificateDao().getAllCertificatesSync()
    suspend fun insertCertificate(cert: CertificateEntity) = db.certificateDao().insertCertificate(cert)
    suspend fun deleteCertificateById(id: String) = db.certificateDao().deleteCertificateById(id)

    suspend fun getAllRequestsSync() = db.premiumRequestDao().getAllRequestsSync()
    suspend fun insertRequest(req: PremiumRequestEntity) = db.premiumRequestDao().insertRequest(req)
    suspend fun updateRequest(req: PremiumRequestEntity) = db.premiumRequestDao().updateRequest(req)

    suspend fun getSettingsSync(): AppSettingsEntity? = db.appSettingsDao().getSettingsSync()
    suspend fun saveSettings(settings: AppSettingsEntity) = db.appSettingsDao().saveSettings(settings)
}
