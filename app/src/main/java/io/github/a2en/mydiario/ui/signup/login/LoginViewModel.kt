package io.github.a2en.mydiario.ui.signup.login

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import io.github.a2en.mydiario.R
import io.github.a2en.mydiario.api.NetworkResult
import io.github.a2en.mydiario.repository.SignupRepository
import io.github.a2en.mydiario.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class LoginViewModel(var app: Application) : AndroidViewModel(app) {
    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()
    var loading = MutableLiveData<Boolean>()
    val showToast = SingleLiveEvent<String>()
    val navigate  = SingleLiveEvent<NavDirections>()
    val saveAuthToken = SingleLiveEvent<String>()

    private val signupRepo = SignupRepository.getRepository()

    fun login(){
        if (!validateData()) {
            return
        }

        val params = HashMap<String, String>()
        params["email"] = email.value ?: ""
        params["password"] = password.value ?: ""

        viewModelScope.launch {
            loading.value = true
            val result = signupRepo.login(params)
            loading.value = false
            if (result is NetworkResult.Error) {
                showToast.value = result.message ?:""
            }else{
                saveAuthToken.value = result.data ?:""
                navigate.value = LoginFragmentDirections.actionLoginFragmentToMainActivity()
            }
        }
    }

    private fun validateData(): Boolean {
        if (email.value.isNullOrBlank() || password.value.isNullOrBlank()) {
            showToast.value = app.getString(R.string.mandatory)
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.value!!).matches()) {
            showToast.value = app.getString(R.string.invalid_email)
            return false
        }
        if (password.value!!.length < 6) {
            showToast.value = app.getString(R.string.password_length)
            return false
        }
        return true
    }
}