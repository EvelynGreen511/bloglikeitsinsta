package com.example.bloglikeitsinsta.ui.create

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bloglikeitsinsta.wordpress.config.SecureConfigManager
import com.example.bloglikeitsinsta.wordpress.repository.WordPressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class CreatePostUiState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val shouldNavigateBack: Boolean = false
)

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    application: Application,
    private val repository: WordPressRepository,
    private val configManager: SecureConfigManager
) : AndroidViewModel(application) {

    private val _uiState = MutableLiveData(CreatePostUiState())
    val uiState: LiveData<CreatePostUiState> = _uiState

    fun createPost(imageFile: File, title: String, content: String, mimeType: String) {
        viewModelScope.launch {
            _uiState.value = CreatePostUiState(isLoading = true)
            val token = configManager.getToken()
            val uploadResult = repository.uploadMedia(token, imageFile, mimeType)
            Log.d("CreatePostViewModel", uploadResult.toString())
            uploadResult.fold(
                onSuccess = { mediaResponse ->
                    val postResult = repository.createPost(
                        token = token,
                        title = title,
                        content = content,
                        featuredMediaId = mediaResponse.id
                    )
                    postResult.fold(
                        onSuccess = {
                            Log.d("CreatePostViewModel", "Post created successfully")
                            _uiState.value = CreatePostUiState(message = "Post created successfully!", shouldNavigateBack = true)
                        },
                        onFailure = {
                            Log.d("CreatePostViewModel", "Failed to create post: ${it.message}")
                            _uiState.value = CreatePostUiState(message = "Failed to create post: ${it.message}")
                        }
                    )
                },
                onFailure = {
                    _uiState.value = CreatePostUiState(message = "Failed to upload image: ${it.message}")
                }
            )
        }
    }
}