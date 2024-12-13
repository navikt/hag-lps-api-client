package no.nav.helsearbeidsgiver.altinn

import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import no.nav.helsearbeidsgiver.altinnSystemUserRequestUrl
import no.nav.helsearbeidsgiver.maskinporten.createHttpClient
import no.nav.helsearbeidsgiver.systemId
import no.nav.helsearbeidsgiver.utils.logger

class AltinnService {
    suspend fun lagSystembrukerForespoersel(
        kundeOrgnr: String,
        maskinportenToken: String,
    ): RequestSystemResponse {
        val request =
            CreateRequestSystemUser(
                systemId = systemId,
                partyOrgNo = kundeOrgnr,
                rights =
                    listOf(
                        Right(
                            action = "write",
                            resource =
                                listOf(
                                    AttributePair(
                                        id = "urn:altinn:resource",
                                        value = "nav_sykepenger_inntektsmelding-nedlasting",
                                    ),
                                ),
                        ),
                    ),
                redirectUrl = "https://hag-lps-api-client.ekstern.dev.nav.no/velkommen",
            )
        logger().info("Lager systembrukerforespørsel for systemId ${request.systemId}")
        return createHttpClient()
            .post(altinnSystemUserRequestUrl) {
                setBody(request)
                bearerAuth(maskinportenToken)
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
            }.body<RequestSystemResponse>()
    }
    suspend fun altinnExchangeToken(token: String) {
        createHttpClient()
            .get("https://platform.tt02.altinn.no/authentication/api/v1/exchange/maskinporten") {
                bearerAuth(token)
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
            }.body<String>()
    }
}
