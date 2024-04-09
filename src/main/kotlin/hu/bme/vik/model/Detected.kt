package hu.bme.vik.model

import kotlinx.serialization.Serializable

@Serializable
data class Detected(
    val xMin: Float,
    val xMax: Float,
    val yMin: Float,
    val yMax: Float,
    val probability: Float,
    val label: String? = null
)
