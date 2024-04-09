package hu.bme.vik.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Detections(
    @SerialName("Detections")
    val detections: List<Detected>,
)
