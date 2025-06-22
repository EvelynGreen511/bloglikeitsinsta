package com.example.bloglikeitsinsta.wordpress.model.partials

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Author(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("avatar_urls")
    val avatarUrls: Map<String, String>
) : Parcelable