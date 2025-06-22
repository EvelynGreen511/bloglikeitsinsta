package com.example.bloglikeitsinsta.wordpress.model.partials

import com.google.gson.annotations.SerializedName

data class Author(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("avatar_urls")
    val avatarUrls: Map<String, String>
)