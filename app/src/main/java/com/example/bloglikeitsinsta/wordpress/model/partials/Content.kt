package com.example.bloglikeitsinsta.wordpress.model.partials

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Content(
    @SerializedName("rendered")
    val rendered: String
) : Parcelable