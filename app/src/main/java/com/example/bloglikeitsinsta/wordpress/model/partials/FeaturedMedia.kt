package com.example.bloglikeitsinsta.wordpress.model.partials

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeaturedMedia(
    @SerializedName("id")
    val id: Int,

    @SerializedName("source_url")
    val sourceUrl: String,

    @SerializedName("alt_text")
    val altText: String
) : Parcelable