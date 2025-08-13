package no.nav.helsearbeidsgiver.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import no.nav.helsearbeidsgiver.utils.AsStringSerializer
import no.nav.helsearbeidsgiver.utils.logger
import java.util.UUID

fun Application.configureAltinnEventRouting() {
    routing {
        mottaDialogportenEvent()
    }
}

private fun Routing.mottaDialogportenEvent() {
    post("/dialogporten-event") {
        try {
            logger().info("Mottok Dialogporten event.")
            val dialogportenEvent = call.receive<DialogportenEvent>()
            logger().info("Mottok Dialogporten event ${dialogportenEvent.tilLogString()}")
            call.respond(HttpStatusCode.OK)
        } catch (e: Exception) {
            logger().error("Feil ved mottak av Dialogporten event", e)
            call.respond(HttpStatusCode.InternalServerError, "Klarte ikke motta dialogporten event")
        }
    }
}

@Serializable
data class DialogportenEvent(
    @Serializable(with = UuidSerializer::class)
    val resourceinstance: UUID? = null,
    val type: String,
    val resource: String? = null,
    val subject: String? = null,
    val source: String,
) {
    fun tilLogString(): String =
        "DialogportenEvent(resourceinstance=$resourceinstance, type=$type, resource=$resource, subject=$subject, source=$source)"
}

object UuidSerializer : AsStringSerializer<UUID>(
    serialName = "lps-client.kotlinx.UuidSerializer",
    parse = UUID::fromString,
)
