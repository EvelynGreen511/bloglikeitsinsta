package com.example.bloglikeitsinsta.wordpress.model.partials

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Embedded(
    @SerializedName("wp:featuredmedia")
    val featuredMedia: List<FeaturedMedia>?,

    @SerializedName("author")
    val author: List<Author>?
) : Parcelable