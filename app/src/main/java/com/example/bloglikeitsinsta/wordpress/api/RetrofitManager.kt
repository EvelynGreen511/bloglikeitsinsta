package com.example.bloglikeitsinsta.wordpress.api
import com.example.bloglikeitsinsta.wordpress.config.SecureConfigManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitManager @Inject constructor(
    private val configManager: SecureConfigManager
) {
    @Volatile
    private var retrofit: Retrofit? = null

    fun getRetrofit(): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .followRedirects(false) // disables HTTP redirects
            .followSslRedirects(true) // disables HTTPS redirects
            .build()

        val baseUrl = configManager.getWordPressUrl()
        // If the URL changed, rebuild Retrofit
        if (retrofit == null || retrofit!!.baseUrl().toString() != baseUrl) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    fun getWordPressApiService(): WordPressApiService {
        return getRetrofit().create(WordPressApiService::class.java)
    }
}