package blog

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
class App {
    fun main(args: Array<String>) {
        embeddedServer(Netty, 8080) {
            routing {
                get("/") {
                    call.respondText("My Example Blog", ContentType.Text.Html)
                }
            }
        }.start(wait = true)
    }
}
