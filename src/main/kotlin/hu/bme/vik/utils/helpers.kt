package hu.bme.vik.utils

import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.IOException
import javax.imageio.ImageIO

fun createImageFromBytes(imageData: ByteArray): BufferedImage {
    val bais = ByteArrayInputStream(imageData)
    try {
        return ImageIO.read(bais)
    } catch (e: IOException) {
        throw RuntimeException(e)
    }
}
