package com.example.bloglikeitsinsta.wordpress.api

import com.example.bloglikeitsinsta.wordpress.model.AuthResponse
import com.example.bloglikeitsinsta.wordpress.model.WordPressPost
import retrofit2.Response
import retrofit2.http.*


interface WordPressApiService {
    /**
     * Authenticate user with WordPress JWT
     */
    @POST("wp-json/jwt-auth/v1/token")
    @FormUrlEncoded
    suspend fun authenticate(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<AuthResponse>

    /**
     * Validate JWT token
     */
    @POST("wp-json/jwt-auth/v1/token/validate")
    suspend fun validateToken(
        @Header("Authorization") token: String
    ): Response<Any>

    /**
     * Get posts (public - no auth required)
     */
    @GET("wp/v2/posts")
    suspend fun getPosts(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 10,
        @Query("_embed") embed: Boolean = true
    ): Response<List<WordPressPost>>

    /**
     * Get posts with authentication (can access private posts)
     */
    @GET("wp-json/wp/v2/posts")
    suspend fun getPostsAuthenticated(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 10,
        @Query("_embed") embed: Boolean = true
    ): Response<List<WordPressPost>>

    /**
     * Create a new post (requires authentication)
     */
    @POST("wp-json/wp/v2/posts")
    suspend fun createPost(
        @Header("Authorization") token: String,
        @Body post: Map<String, Any>
    ): Response<WordPressPost>

    /**
     * Upload media (requires authentication)
     */
    @Multipart
    @POST("wp-json/wp/v2/media")
    suspend fun uploadMedia(
        @Header("Authorization") token: String,
        @Part("title") title: String,
        @Part("alt_text") altText: String,
        @Part file: okhttp3.MultipartBody.Part
    ): Response<Any>
}