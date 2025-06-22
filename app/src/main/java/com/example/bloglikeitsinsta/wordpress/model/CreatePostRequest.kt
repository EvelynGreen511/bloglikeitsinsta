package com.example.bloglikeitsinsta.wordpress.model

data class CreatePostRequest(
    val title: String,
    val content: String,
    val excerpt: String = "",
    val status: String = "publish",
    val categories: List<Int> = emptyList(),
    val tags: List<Int> = emptyList(),
    val featured_media: Int? = null
)