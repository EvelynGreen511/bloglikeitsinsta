package com.example.bloglikeitsinsta.wordpress.config

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureConfigManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs by lazy {
        context.getSharedPreferences("wp_config", Context.MODE_PRIVATE)
    }

    fun getWordPressUrl(): String = prefs.getString(KEY_URL, "") ?: ""
    fun getUsername(): String = prefs.getString(KEY_USERNAME, "") ?: ""
    fun getPassword(): String = prefs.getString(KEY_PASSWORD, "") ?: ""
    fun getToken(): String = prefs.getString(KEY_TOKEN, "") ?: ""  // ← Add this!

    fun setWordPressUrl(url: String) = prefs.edit().putString(KEY_URL, url).apply()
    fun setUsername(username: String) = prefs.edit().putString(KEY_USERNAME, username).apply()
    fun setPassword(password: String) = prefs.edit().putString(KEY_PASSWORD, password).apply()
    fun setToken(token: String) = prefs.edit().putString(KEY_TOKEN, token).apply()  // ← Add this!

    fun clear() = prefs.edit().clear().apply()

    companion object {
        private const val KEY_URL = "wp_url"
        private const val KEY_USERNAME = "wp_username"
        private const val KEY_PASSWORD = "wp_password"
        private const val KEY_TOKEN = "wp_token"  // ← Add this!
    }
}