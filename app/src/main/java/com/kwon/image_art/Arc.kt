package com.kwon.image_art

data class Arc(
    val startAngle: Float,
    val sweepAngle: Float
) {
    fun average(): Double =
        (startAngle / 2) + (sweepAngle / 2) + (((startAngle % 2) + (sweepAngle % 2)) / 2).toDouble()
}
