package io.github.a2en.mydiario.ui.home.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.github.a2en.mydiario.R
import io.github.a2en.mydiario.api.NetworkResult
import io.github.a2en.mydiario.database.getDatabase
import io.github.a2en.mydiario.repository.MainRepository
import io.github.a2en.mydiario.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class DetailViewModel(val app: Application) : AndroidViewModel(app) {

    var place: String = ""
    var updateId: String? = null
    private val database = getDatabase(app)
    private val mainRepository = MainRepository.getRepository(database)

    var title = MutableLiveData<String>()
    var body = MutableLiveData<String>()
    var date = MutableLiveData<String>()
    val showToast = SingleLiveEvent<String>()
    val navigate = SingleLiveEvent<Boolean>()
    var loading = MutableLiveData<Boolean>()


    fun saveEntry() {
        if (!validateData()) {
            return
        }
        val params = HashMap<String, String>()
        params["title"] = title.value ?: ""
        params["body"] = body.value ?: ""
        params["date"] = formattedDate(date.value) ?: ""
        params["place"] = place

        viewModelScope.launch {
            loading.value = true
            val result = mainRepository.saveEntry(params,updateId)
            loading.value = false
            if (result is NetworkResult.Error) {
                showToast.value = result.message ?: ""
            } else {
                navigate.value = true
            }
        }
    }

    private fun formattedDate(value: String?): String? {
        return try {
            val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
            val date: Date = dateFormat.parse(value) ?: return null
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            df.format(date);
        } catch (e: Exception) {
            null
        }
    }

    private fun validateData(): Boolean {
        if (title.value.isNullOrBlank() || body.value.isNullOrBlank() || date.value.isNullOrBlank()) {
            showToast.value = app.getString(R.string.mandatory)
            return false
        }
        return true
    }

    fun formatDate(value: String): String? {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date: Date = dateFormat.parse(value) ?: return null
            val df = SimpleDateFormat("MMM d, yyyy", Locale.US)
            df.format(date);
        } catch (e: Exception) {
            null
        }
    }

}