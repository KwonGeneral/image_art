package com.kwon.image_art

import androidx.annotation.ColorRes
import androidx.annotation.Px

data class Slice(
    val dataPoint: Float,
    @ColorRes val color: Int,
    val name: String,
    var arc: Arc? = null,
    @Px var scaledValue: Float? = 0f,
    var percentage: Int? = null
)
