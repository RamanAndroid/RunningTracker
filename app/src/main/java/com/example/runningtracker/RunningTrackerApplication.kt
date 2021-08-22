package com.example.runningtracker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import com.example.runningtracker.database.RunningTrackerDatabaseHelper
import com.example.runningtracker.retrofit.ApiRunningTracker
import com.google.gson.GsonBuilder
import okhttp3.CipherSuite.Companion.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
import okhttp3.CipherSuite.Companion.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256
import okhttp3.CipherSuite.Companion.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit


class RunningTrackerApplication : Application() {

    companion object {
        lateinit var apiService: ApiRunningTracker
            private set

        lateinit var okHttpClient: OkHttpClient
            private set

        lateinit var gsonConverter: GsonConverterFactory
            private set

        lateinit var localLanguage: String
            private set

        lateinit var sharedPreferences: SharedPreferences
            private set

        lateinit var calendar: Calendar
            private set

        lateinit var databaseHelper: SQLiteDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        createGsonConverter()
        createOkHttpClient()
        createApiService()
        createDatabaseHelper()
        getDefaultLanguage()
        getSharedPreferences()
        getCalendar()
    }

    private fun createApiService() {

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.BASE_URL_API)
            .addConverterFactory(gsonConverter)
            .client(okHttpClient)
            .build()
        apiService = retrofit.create(ApiRunningTracker::class.java)
    }

    private fun createOkHttpClient() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val spec: ConnectionSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_2)
            .cipherSuites(
                TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
            )
            .build()

        val httpBuilder = OkHttpClient.Builder()
        httpBuilder
            .addInterceptor { chain ->
                var original = chain.request()
                original = original.newBuilder().build()
                chain.proceed(original)
            }
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .connectionSpecs(Collections.singletonList(spec))
            .addInterceptor(interceptor)
        okHttpClient = httpBuilder.build()

    }

    private fun createGsonConverter() {
        gsonConverter = GsonConverterFactory
            .create(
                GsonBuilder()
                    .setLenient()
                    .disableHtmlEscaping()
                    .create()
            )
    }

    private fun getDefaultLanguage() {
        localLanguage = Locale.getDefault().language
    }

    private fun getSharedPreferences() {
        sharedPreferences = applicationContext.getSharedPreferences(
            ApplicationConstants.CONSTANTS_USER,
            Context.MODE_PRIVATE
        )
    }

    private fun getCalendar() {
        calendar = Calendar.getInstance()
    }

    private fun createDatabaseHelper() {
        databaseHelper = RunningTrackerDatabaseHelper(this).writableDatabase
    }
}