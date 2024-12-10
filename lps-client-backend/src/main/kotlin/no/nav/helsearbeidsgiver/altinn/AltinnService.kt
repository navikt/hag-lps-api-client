package no.nav.helsearbeidsgiver.altinn

import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import no.nav.helsearbeidsgiver.altinnSystemUserRequestUrl
import no.nav.helsearbeidsgiver.maskinporten.createHttpClient
import no.nav.helsearbeidsgiver.systemId

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

        return createHttpClient()
            .post(altinnSystemUserRequestUrl) {
                setBody(request)
                bearerAuth(maskinportenToken)
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
            }.body<RequestSystemResponse>()
    }
}
