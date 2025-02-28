package no.nav.helsearbeidsgiver.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.plugins.origin
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import no.nav.helsearbeidsgiver.utils.logger

fun Application.configureAltinnEventRouting() {
    routing {
        mottaDialogportenEvent()
    }
}

private fun Routing.mottaDialogportenEvent() {
    post("/dialogporten-event") {
        try {
            if (call.request.origin.remoteAddress != "20.100.24.41/32") {
                logger().info("Mottok Dialogporten event fra en annen IP enn forventet")
                call.respond(HttpStatusCode.Forbidden, "Forbidden")
            } else {
                logger().info("Mottok Dialogporten event fra forventet IP")
                call.respond(HttpStatusCode.OK)
            }
        } catch (e: Exception) {
            logger().error("Feil ved mottak av Dialogporten event", e)
            call.respond(HttpStatusCode.InternalServerError, "Klarte ikke motta dialogporten event")
        }
    }
}
