package no.nav.helsearbeidsgiver.altinn

import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import no.nav.helsearbeidsgiver.maskinporten.createHttpClient

class RequestSystemUserClient {
    suspend fun lagSystembrukerForespoersel(
    kundeOrgnr: String,
    maskinportenToken: String,
): RequestSystemResponse {
    val request =
        CreateRequestSystemUser(
            systemId = "315339138_tigersys",
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
        .post("https://platform.tt02.altinn.no/authentication/api/v1/systemuser/request/vendor") {
            setBody(request)
            bearerAuth(maskinportenToken)
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }.body<RequestSystemResponse>()
    }
}
