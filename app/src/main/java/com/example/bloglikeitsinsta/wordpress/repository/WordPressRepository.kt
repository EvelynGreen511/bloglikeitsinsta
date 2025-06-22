package com.example.bloglikeitsinsta.wordpress.repository

import android.util.Log
import com.example.bloglikeitsinsta.wordpress.model.MediaResponse
import com.example.bloglikeitsinsta.wordpress.api.RetrofitManager
import com.example.bloglikeitsinsta.wordpress.api.WordPressApiService
import com.example.bloglikeitsinsta.wordpress.model.AuthResponse
import com.example.bloglikeitsinsta.wordpress.model.CreatePostRequest
import com.example.bloglikeitsinsta.wordpress.model.WordPressPost
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordPressRepository @Inject constructor(
    private val retrofitManager: RetrofitManager
) {

    private val apiService: WordPressApiService
        get() = retrofitManager.getWordPressApiService()

    /**
     * Authenticate user and return JWT token
     */
    suspend fun authenticate(username: String, password: String): Result<AuthResponse> {
        return try {
            val response = apiService.authenticate(username, password)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Log.e("WordPressRepository", "Authentication failed: ${response.message()}")
                Result.failure(Exception("Authentication failed: ${response.message()}"))

            }
        } catch (e: Exception) {
            Log.e("WordPressRepository", "Exception during authentication", e)
            Result.failure(e)
        }
    }

    /**
     * Validate JWT token
     */
    suspend fun validateToken(token: String): Boolean {
        return try {
            val response = apiService.validateToken("Bearer $token")
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get posts as Flow for reactive UI updates
     */
    fun getPosts(page: Int = 1, perPage: Int = 10): Flow<Result<List<WordPressPost>>> = flow {
        try {
            val response = apiService.getPosts(page, perPage)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                Log.e("WordPressRepository", response.headers().joinToString())
                emit(Result.failure(Exception("Failed to fetch posts: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Get posts with authentication (for private content)
     */
    fun getPostsAuthenticated(
        token: String,
        page: Int = 1,
        perPage: Int = 10
    ): Flow<Result<List<WordPressPost>>> = flow {
        try {
            val response = apiService.getPostsAuthenticated("Bearer $token", page, perPage)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to fetch posts: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Create a new post
     */
    suspend fun createPost(
        token: String,
        title: String,
        content: String,
        excerpt: String = "",
        categories: List<Int> = emptyList(),
        tags: List<Int> = emptyList(),
        featuredMediaId: Int? = null,
        status: String = "publish"
    ): Result<WordPressPost> {
        return try {
            val postData = CreatePostRequest(
                title = title,
                content = content,
                excerpt = excerpt,
                status = status,
                categories = categories,
                tags = tags,
                featured_media = featuredMediaId
            )

            val response = apiService.createPost("Bearer $token", postData)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("WordPressRepository", "Create post error: $errorBody")
                Result.failure(Exception("Failed to create post: ${response.message()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadMedia(token: String, file: File, mimeType: String): Result<MediaResponse> {
        return try {
            val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val response = apiService.uploadMedia("Bearer $token", body)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Log.e("WordpressRepository", "" + response.errorBody()?.string())
                Result.failure(Exception("Failed to upload media: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}