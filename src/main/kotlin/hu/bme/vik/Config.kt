package hu.bme.vik

import kotlin.properties.Delegates

object Config {
    var threshold by Delegates.notNull<Float>()
    var detectionLimit by Delegates.notNull<Int>()
    lateinit var defaultClass: String
}