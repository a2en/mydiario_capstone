package io.github.a2en.mydiario.api

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.github.a2en.mydiario.MyApp
import io.github.a2en.mydiario.R
import io.github.a2en.mydiario.domain.DiaryEntry
import io.github.a2en.mydiario.utils.Constants.Companion.AUTH_TOKEN
import io.github.a2en.mydiario.utils.Constants.Companion.BASE_URL
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.Request
import retrofit2.http.*


private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

var logging = HttpLoggingInterceptor().also {
    it.setLevel(HttpLoggingInterceptor.Level.BODY);
}
var client: OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(logging)
    .addNetworkInterceptor(Interceptor { chain ->
        val requestBuilder: Request.Builder = chain.request().newBuilder()
        var ctx = MyApp.applicationContext()
        val pref = ctx.getSharedPreferences(
            ctx.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        requestBuilder.header(AUTH_TOKEN, pref.getString(AUTH_TOKEN, "") ?: "")
        chain.proceed(requestBuilder.build())
    })
    .build()


private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(client)
    .build()

interface ApiService {

    @POST("user/register")
    suspend fun register(@Body params: Map<String, String>): Response<String>

    @POST("user/login")
    suspend fun login(@Body params: Map<String, String>): Response<String>

    @GET("diaryEntry")
    suspend fun getDiaryEntries(): List<DiaryEntry>

    @POST("diaryEntry")
    suspend fun saveDiaryEntry(@Body params: Map<String, String>): Response<String>

    @PATCH("diaryEntry/{id}")
    suspend fun updateDiaryEntry(@Path("id") id:String, @Body params: Map<String, String>): Response<String>

    @DELETE("diaryEntry/{id}")
    suspend fun deleteEntry(@Path("id") id:String): Response<String>

}


object Api {
    val retrofitService: ApiService by lazy { retrofit.create(ApiService::class.java) }
}

sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null
) {

    class Success<T>(data: T) : NetworkResult<T>(data)

    class Error<T>(message: String?, data: T? = null) : NetworkResult<T>(data, message)

    class Loading<T> : NetworkResult<T>()

}