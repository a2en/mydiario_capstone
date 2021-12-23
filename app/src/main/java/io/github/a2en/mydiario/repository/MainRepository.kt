package io.github.a2en.mydiario.repository

import android.util.Log
import androidx.lifecycle.LiveData
import io.github.a2en.mydiario.api.Api
import io.github.a2en.mydiario.api.NetworkResult
import io.github.a2en.mydiario.database.DiaryDatabase
import io.github.a2en.mydiario.domain.DiaryEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*

class MainRepository(private val database: DiaryDatabase) {
    companion object {
        @Volatile
        private var INSTANCE: MainRepository? = null

        fun getRepository(database: DiaryDatabase): MainRepository {
            return INSTANCE ?: synchronized(this) {
                MainRepository(database).also {
                    INSTANCE = it
                }
            }
        }
    }

    suspend fun refreshEntries() {
        withContext(Dispatchers.IO) {
            try {
                val diaryEntries =
                    Api.retrofitService.getDiaryEntries()
                database.diaryDao.deleteAll()
                database.diaryDao.insertAll(diaryEntries)
            } catch (e: Exception) {
                // Log the error
                Log.e("TAG", "refreshEntries: ", e)
            }
        }
    }

    suspend fun saveEntry(params: HashMap<String, String>, updateId: String?): NetworkResult<String?> {
        var response = if(updateId!=null) {
            Api.retrofitService.updateDiaryEntry(updateId,params)
        }else{
            Api.retrofitService.saveDiaryEntry(params)
        }
        if (response.isSuccessful) {
            refreshEntries()
            return NetworkResult.Success(response.body())
        }
        val inputAsString = response.errorBody()?.byteStream()?.bufferedReader().use { it?.readText() }  // defaults to UTF-8

        return NetworkResult.Error(inputAsString)
    }

    suspend  fun deleteEntry(id: String) {
        try {
            val diaryEntries =
                Api.retrofitService.deleteEntry(id)
            refreshEntries()
        } catch (e: Exception) {
            // Log the error
            Log.e("TAG", "refreshEntries: ", e)
        }
    }


    val diaryEntries: LiveData<List<DiaryEntry>> =
        database.diaryDao.getEntries()


}