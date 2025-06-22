package com.example.bloglikeitsinsta.wordpress.model.partials

import com.google.gson.annotations.SerializedName

data class FeaturedMedia(
    @SerializedName("id")
    val id: Int,

    @SerializedName("source_url")
    val sourceUrl: String,

    @SerializedName("alt_text")
    val altText: String
)