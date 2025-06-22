package com.example.bloglikeitsinsta.wordpress.repository

import android.util.Log
import com.example.bloglikeitsinsta.wordpress.api.RetrofitManager
import com.example.bloglikeitsinsta.wordpress.api.WordPressApiService
import com.example.bloglikeitsinsta.wordpress.model.AuthResponse
import com.example.bloglikeitsinsta.wordpress.model.WordPressPost
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
            val postData = mutableMapOf<String, Any>(
                "title" to title,
                "content" to content,
                "excerpt" to excerpt,
                "status" to status,
                "categories" to categories,
                "tags" to tags
            )

            featuredMediaId?.let { postData["featured_media"] = it }

            val response = apiService.createPost("Bearer $token", postData)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create post: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}