package hu.bme.vik

import hu.bme.vik.plugins.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    Config.threshold = environment.config.property("application.threshold").getString().toFloat()
    Config.detectionLimit = environment.config.property("application.detectionLimit").getString().toInt()
    Config.defaultClass = environment.config.property("application.defaultClass").getString()

    configureRouting()

    routing {
        get("/health") {
            call.respond("Up and running.")
        }
    }
}
