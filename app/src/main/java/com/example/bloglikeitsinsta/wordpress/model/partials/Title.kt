package com.example.bloglikeitsinsta.wordpress.model.partials

import com.google.gson.annotations.SerializedName

data class Title(
    @SerializedName("rendered")
    val rendered: String
)