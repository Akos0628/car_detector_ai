package hu.bme.vik.plugins

import hu.bme.vik.Config
import hu.bme.vik.model.Detected
import hu.bme.vik.utils.createImageFromBytes
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.kotlinx.dl.onnx.inference.ONNXModelHub
import org.jetbrains.kotlinx.dl.onnx.inference.ONNXModels
import java.io.File

val modelHub = ONNXModelHub(cacheDirectory = File("data/pretrainedModels"))
val model = ONNXModels.ObjectDetection.SSD.pretrainedModel(modelHub)

fun Application.configureRouting() {
    install(ContentNegotiation) {
        json()
    }
    routing {
        post("/detect") {
            val multipartData = call.receiveMultipart()
            var fileBytes: ByteArray? = null
            var threshold = Config.threshold
            var detectionLimit = Config.detectionLimit
            val classLabels = mutableListOf(Config.defaultClass)

            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        fileBytes = part.streamProvider().readBytes()
                    }
                    is PartData.FormItem -> {
                        when (part.name) {
                            "threshold" -> if (part.value.isNotBlank()) threshold = part.value.toFloat()
                            "detectionLimit" -> if (part.value.isNotBlank()) detectionLimit = part.value.toInt()
                            "classLabelNames" -> if (part.value.isNotBlank())  {
                                classLabels.clear()
                                part.value.split(",").forEach {
                                    classLabels += it.trim()
                                }
                            }
                        }
                    }
                    is PartData.BinaryItem -> {}
                    is PartData.BinaryChannelItem -> {}
                }
            }

            if (fileBytes == null) {
                val text = "No image given"
                call.respondText(text, status = HttpStatusCode(400, text))
            }

            val bufferedImage = createImageFromBytes(fileBytes!!)

            if (bufferedImage.width > 4096 || bufferedImage.height > 2160) {
                val text = "Image size is too big"
                call.respondText(text, status = HttpStatusCode(400, text))
            }

            val detectedObjects = model.detectObjects(bufferedImage, topK = detectionLimit)

            val result = detectedObjects.filter {
                it.probability > threshold && it.label in classLabels
            }.map {
                Detected(
                    it.xMin,
                    it.xMax,
                    it.yMin,
                    it.yMax,
                    it.probability,
                    it.label
                )
            }

            call.respond(result)
        }
    }
}
