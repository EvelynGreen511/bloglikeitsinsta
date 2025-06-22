package com.example.bloglikeitsinsta.wordpress.model.partials

import com.google.gson.annotations.SerializedName

data class Embedded(
    @SerializedName("wp:featuredmedia")
    val featuredMedia: List<FeaturedMedia>?,

    @SerializedName("author")
    val author: List<Author>?
)