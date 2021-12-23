package io.github.a2en.mydiario.repository

import android.content.Context
import android.util.Log
import androidx.room.Room
import io.github.a2en.mydiario.MyApp
import io.github.a2en.mydiario.api.Api
import io.github.a2en.mydiario.api.NetworkResult
import io.github.a2en.mydiario.utils.Constants.Companion.AUTH_TOKEN
import okhttp3.Headers
import java.util.HashMap

class SignupRepository {


    suspend fun register(params: HashMap<String, String>): NetworkResult<String?> {
        var response = Api.retrofitService.register(params)
        if (response.isSuccessful) {
            return NetworkResult.Success(response.headers()[AUTH_TOKEN])
        }
        val inputAsString = response.errorBody()?.byteStream()?.bufferedReader().use { it?.readText() }  // defaults to UTF-8

        return NetworkResult.Error(inputAsString)
    }


    suspend fun login(params: HashMap<String, String>): NetworkResult<String?> {
        var response = Api.retrofitService.login(params)
        if (response.isSuccessful) {
            return NetworkResult.Success(response.headers()[AUTH_TOKEN])
        }
        val inputAsString = response.errorBody()?.byteStream()?.bufferedReader().use { it?.readText() }  // defaults to UTF-8

        return NetworkResult.Error(inputAsString)
    }


    companion object {
        @Volatile
        private var INSTANCE: SignupRepository? = null

        fun getRepository(): SignupRepository {
            return INSTANCE ?: synchronized(this) {
                SignupRepository().also {
                    INSTANCE = it
                }
            }
        }
    }
}
