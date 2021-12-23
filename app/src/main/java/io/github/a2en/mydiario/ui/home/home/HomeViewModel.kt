package io.github.a2en.mydiario.ui.home.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import io.github.a2en.mydiario.database.getDatabase
import io.github.a2en.mydiario.domain.DiaryEntry
import io.github.a2en.mydiario.repository.MainRepository
import kotlinx.coroutines.launch

class HomeViewModel(app: Application) : AndroidViewModel(app)  {


    private val database = getDatabase(app)
    private val mainRepository = MainRepository.getRepository(database)

    val diaryEntryLiveData: LiveData<List<DiaryEntry>>
        get() = mainRepository.diaryEntries

    init {
        viewModelScope.launch {
            mainRepository.refreshEntries()
        }
    }

    fun deleteEntry(id: String) {
        viewModelScope.launch {
            mainRepository.deleteEntry(id)
        }
    }

}