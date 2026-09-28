package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AnnouncementEntity
import com.example.data.model.CommentEntity
import com.example.data.model.CommunityEntity
import com.example.data.model.EventEntity
import com.example.data.model.ReportEntity
import com.example.data.model.TicketEntity
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    EventEntity::class,
    CommentEntity::class,
    CommunityEntity::class,
    AnnouncementEntity::class,
    TicketEntity::class,
    UserProfileEntity::class,
    ReportEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class CampusDatabase : RoomDatabase() {
  abstract fun campusDao(): CampusDao

  companion object {
    @Volatile
    private var INSTANCE: CampusDatabase? = null

    fun getDatabase(context: Context): CampusDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          CampusDatabase::class.java,
          "campus_connect_db"
        )
          .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
              super.onCreate(db)
              // Seed initial data asynchronously on creation
              CoroutineScope(Dispatchers.IO).launch {
                getDatabase(context).seedInitialData()
              }
            }
          })
          .build()
        INSTANCE = instance
        instance
      }
    }
  }

  suspend fun seedInitialData() {
    val dao = campusDao()
    dao.insertEvents(InitialData.events)
    dao.insertCommunities(InitialData.communities)
    dao.insertAnnouncements(InitialData.announcements)
    dao.insertComments(InitialData.comments)
    dao.saveUserProfile(InitialData.defaultProfile)
    dao.insertTickets(InitialData.initialTickets)
  }
}
