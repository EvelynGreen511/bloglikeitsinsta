package com.example.bloglikeitsinsta.wordpress.model.partials

import com.google.gson.annotations.SerializedName

data class Content(
    @SerializedName("rendered")
    val rendered: String
)