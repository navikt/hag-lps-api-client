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
import no.nav.helsearbeidsgiver.maskinporten.MaskinportenService
import no.nav.helsearbeidsgiver.maskinporten.createHttpClient
import no.nav.helsearbeidsgiver.systemId
import no.nav.helsearbeidsgiver.utils.logger

class AltinnService {
    val maskinportenService = MaskinportenService()

    suspend fun lagSystembrukerForespoersel(kundeOrgnr: String): RequestSystemResponse {
        val maskinportenToken =
            maskinportenService
                .getSimpleMaskinportenTokenForScope(
                    "altinn:authentication/systemuser.request.write altinn:authentication/systemuser.request.read",
                ).fetchNewAccessToken()
                .tokenResponse.accessToken
        val request =
            CreateRequestSystemUser(
                systemId = systemId,
                partyOrgNo = kundeOrgnr,
                rights =
                    listOf(
                        Right(
                            action = "read",
                            resource =
                                listOf(
                                    AttributePair(
                                        id = "urn:altinn:resource",
                                        value = "nav_system_sykepenger_inntektsmelding",
                                    ),
                                    AttributePair(
                                        id = "urn:altinn:resource",
                                        value = "nav_system_sykepenger_sykmelding",
                                    ),
                                    AttributePair(
                                        id = "urn:altinn:resource",
                                        value = "nav_system_sykepenger_soknad",
                                    ),
                                ),
                        ),
                    ),
                redirectUrl = "https://hag-lps-api-client.ekstern.dev.nav.no/velkommen",
            )
        logger().info("Lager systembrukerforespørsel for systemId ${request.systemId}")
        try {
            val postResponse =
                createHttpClient()
                    .post(altinnSystemUserRequestUrl) {
                        setBody(request)
                        bearerAuth(maskinportenToken)
                        contentType(ContentType.Application.Json)
                        accept(ContentType.Application.Json)
                    }.body<RequestSystemResponse>()

            val getResponse =
                createHttpClient()
                    .get(altinnSystemUserRequestUrl + "/${postResponse.id}") {
                        bearerAuth(maskinportenToken)
                        contentType(ContentType.Application.Json)
                        accept(ContentType.Application.Json)
                    }.body<RequestSystemResponse>()
            return getResponse
        } catch (e: Exception) {
            logger().error("Feil ved henting av systembrukerforespørsel", e)
            throw e
        }
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
