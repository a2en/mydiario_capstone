package io.github.a2en.mydiario.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.a2en.mydiario.domain.DiaryEntry
import io.github.a2en.mydiario.utils.DateConverter
import retrofit2.http.DELETE

@Dao
interface DiaryDao {
    @Query("select * from diaryentry order by date ASC")
    fun getEntries(): LiveData<List<DiaryEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(asteroids: List<DiaryEntry>)

    @Query("DELETE FROM diaryentry")
    fun deleteAll()
}

@Database(entities = [DiaryEntry::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class DiaryDatabase : RoomDatabase() {
    abstract val diaryDao: DiaryDao
}

private lateinit var INSTANCE: DiaryDatabase

fun getDatabase(context: Context): DiaryDatabase {
    synchronized(DiaryDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                DiaryDatabase::class.java,
                "diary3").build()
        }
    }
    return INSTANCE
}