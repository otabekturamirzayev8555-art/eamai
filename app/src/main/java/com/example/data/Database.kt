package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val fullName: String,
    val username: String,
    val email: String,
    val passwordHash: String,
    val goal: String,
    val level: Int = 1,
    val xp: Int = 0,
    val streak: Int = 0,
    val lastActiveDate: String = "",
    val isPremium: Boolean = false,
    val levelSelectedStr: String = "Boshlovchi"
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val role: String, // "user", "model", "system"
    val senderName: String,
    val text: String,
    val attachmentUri: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "certificates")
data class CertificateEntity(
    @PrimaryKey val id: String, // e.g. EAM-9831-72
    val levelCode: String, // A1, A2, B2, C1, IELTS 9.0
    val userName: String,
    val issueDate: String,
    val qrCodePayload: String,
    val signatureName: String = "EAM Director"
)

@Entity(tableName = "premium_requests")
data class PremiumRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val tariff: String, // "1 Oylik" (30 000 UZS) or "1 Yillik" (280 000 UZS)
    val receiptImageUri: String, // base64 or file path
    val status: String, // "PENDING", "APPROVED", "REJECTED"
    val submittedTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val adminCode: String = "Bek0047",
    val telegramLink: String = "",
    val instagramLink: String = ""
)

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = 1 LIMIT 1")
    fun getUserFlow(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = 1 LIMIT 1")
    suspend fun getUserSync(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE username LIKE :query OR fullName LIKE :query")
    suspend fun searchUsers(query: String): List<UserEntity>

    @Query("SELECT * FROM users")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>

    @Update
    suspend fun updateUser(user: UserEntity)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun getMessagesFlow(): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("DELETE FROM messages")
    suspend fun clearAllMessages()
}

@Dao
interface CertificateDao {
    @Query("SELECT * FROM certificates WHERE userName = :userName")
    fun getCertificatesForUser(userName: String): Flow<List<CertificateEntity>>

    @Query("SELECT * FROM certificates")
    fun getAllCertificatesFlow(): Flow<List<CertificateEntity>>

    @Query("SELECT * FROM certificates")
    suspend fun getAllCertificatesSync(): List<CertificateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCertificate(cert: CertificateEntity)

    @Query("DELETE FROM certificates WHERE id = :id")
    suspend fun deleteCertificateById(id: String)
}

@Dao
interface PremiumRequestDao {
    @Query("SELECT * FROM premium_requests ORDER BY submittedTime DESC")
    fun getRequestsFlow(): Flow<List<PremiumRequestEntity>>

    @Query("SELECT * FROM premium_requests ORDER BY submittedTime DESC")
    suspend fun getAllRequestsSync(): List<PremiumRequestEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(req: PremiumRequestEntity)

    @Update
    suspend fun updateRequest(req: PremiumRequestEntity)
}

@Dao
interface AppSettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsSync(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: AppSettingsEntity)
}

@Database(
    entities = [
        UserEntity::class,
        MessageEntity::class,
        CertificateEntity::class,
        PremiumRequestEntity::class,
        AppSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class EamDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun messageDao(): MessageDao
    abstract fun certificateDao(): CertificateDao
    abstract fun premiumRequestDao(): PremiumRequestDao
    abstract fun appSettingsDao(): AppSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: EamDatabase? = null

        fun getDatabase(context: Context): EamDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EamDatabase::class.java,
                    "eam_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
