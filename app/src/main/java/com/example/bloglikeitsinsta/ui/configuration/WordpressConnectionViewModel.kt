package com.example.bloglikeitsinsta.ui.configuration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bloglikeitsinsta.wordpress.config.SecureConfigManager
import com.example.bloglikeitsinsta.wordpress.repository.WordPressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val hasValidToken: Boolean = false
)

data class WordPressSettings(
    val url: String = "",
    val username: String = "",
    val password: String = ""
)

@HiltViewModel
class WordpressConnectionViewModel@Inject constructor(
    private val repository: WordPressRepository,
    private val configManager: SecureConfigManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()  // ← Expose this!

    private val _currentSettings = MutableStateFlow(WordPressSettings())
    val currentSettings: StateFlow<WordPressSettings> = _currentSettings.asStateFlow()

    fun loadCurrentSettings() {
        viewModelScope.launch {
            val settings = WordPressSettings(
                url = configManager.getWordPressUrl(),
                username = configManager.getUsername(),
                password = configManager.getPassword()
            )
            _currentSettings.value = settings

            // Get token from config
            val token = configManager.getToken()
            val hasValidToken = if (token.isNotEmpty()) repository.validateToken(token) else false
            _uiState.value = _uiState.value.copy(hasValidToken = hasValidToken)
        }
    }

    fun saveSettings(url: String, username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, message = null)

            try {
                if (!url.startsWith("http")) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = "URL must start with http:// or https://"
                    )
                    return@launch
                }

                // Save to config
                configManager.setWordPressUrl(if (url.endsWith("/")) url else "$url/")
                configManager.setUsername(username)
                configManager.setPassword(password)

                // Try to authenticate with new credentials
                repository.authenticate(username, password)
                    .onSuccess { authResponse ->
                        configManager.setToken(authResponse.token) // Save token!
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            message = "Settings saved and authenticated successfully!",
                            hasValidToken = true
                        )
                    }
                    .onFailure { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            message = "Settings saved but authentication failed: ${exception.message}",
                            hasValidToken = false
                        )
                    }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Error saving settings: ${e.message}"
                )
            }
        }
    }

    fun testConnection() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, message = null)

            val username = configManager.getUsername()
            val password = configManager.getPassword()

            try {
                repository.authenticate(username, password)
                    .onSuccess { authResponse ->
                        configManager.setToken(authResponse.token)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            message = "✅ Connection successful! Welcome ${authResponse.userDisplayName}",
                            hasValidToken = true
                        )
                    }
                    .onFailure { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            message = "❌ Connection failed: ${exception.message}",
                            hasValidToken = false
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "❌ Error testing connection: ${e.message}"
                )
            }
        }
    }

    fun clearToken() {
        viewModelScope.launch {
            configManager.setToken("")
            _uiState.value = _uiState.value.copy(
                hasValidToken = false,
                message = "Token cleared. You'll need to authenticate again."
            )
        }
    }
}