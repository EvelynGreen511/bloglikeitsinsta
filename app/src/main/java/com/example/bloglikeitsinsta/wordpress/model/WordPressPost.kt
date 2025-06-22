package com.example.bloglikeitsinsta.wordpress.model

import com.example.bloglikeitsinsta.wordpress.model.partials.Content
import com.example.bloglikeitsinsta.wordpress.model.partials.Embedded
import com.example.bloglikeitsinsta.wordpress.model.partials.Excerpt
import com.example.bloglikeitsinsta.wordpress.model.partials.Title
import com.google.gson.annotations.SerializedName

data class WordPressPost (
    @SerializedName("id")
    val id: Int,

    @SerializedName("date")
    val date: String,

    @SerializedName("title")
    val title: Title,

    @SerializedName("content")
    val content: Content,

    @SerializedName("excerpt")
    val excerpt: Excerpt,

    @SerializedName("author")
    val author: Int,

    @SerializedName("featured_media")
    val featuredMedia: Int,

    @SerializedName("status")
    val status: String,

    @SerializedName("categories")
    val categories: List<Int>,

    @SerializedName("tags")
    val tags: List<Int>,

    @SerializedName("_embedded")
    val embedded: Embedded?
)