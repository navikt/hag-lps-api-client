package no.nav.helsearbeidsgiver.maskinporten

import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.formUrlEncode
import no.nav.helsearbeidsgiver.sikkerLogger

private const val GRANT_TYPE = "urn:ietf:params:oauth:grant-type:jwt-bearer"

class MaskinportenClient(
    private val maskinportenClientConfig: MaskinportenClientConfig,
) {
    private val httpClient = createHttpClient()

    suspend fun fetchNewAccessToken(): TokenResponseWrapper {
        sikkerLogger().info("Maskinporten: Henter ny access token fra Maskinporten")

        val result =
            runCatching {
                val response: HttpResponse =
                    httpClient.post(maskinportenClientConfig.endpoint) {
                        contentType(ContentType.Application.FormUrlEncoded)
                        setBody(
                            listOf(
                                "grant_type" to GRANT_TYPE,
                                "assertion" to maskinportenClientConfig.getJwtAssertion(),
                            ).formUrlEncode(),
                        )
                    }
                response.body<TokenResponse>()
            }
        return result.fold(
            onSuccess = { tokenResponse ->
                TokenResponseWrapper(tokenResponse).also {
                    sikkerLogger().info("Maskinporten: Hentet ny access token. ${it.tokenResponse} ")
                    sikkerLogger().info("Maskinporten: Hentet ny access token. Expires in ${it.remainingTimeInSeconds} seconds.")
                }
            },
            onFailure = { e ->
                when (e) {
                    is ClientRequestException -> {
                        sikkerLogger().error(
                            "Maskinporten:: ClientRequestException: Feilet å hente ny access token fra Maskinporten. Status: ${e.response.status}, Message: ${e.message} Exception: $e",
                        )
                    }

                    is ServerResponseException -> {
                        sikkerLogger().error(
                            "Maskinporten: ServerResponseException: Feilet å hente ny access token fra Maskinporten. Status: ${e.response.status}, Message: ${e.message} Exception: $e",
                        )
                    }

                    else -> {
                        sikkerLogger().error("Maskinporten: Feilet å hente ny access token fra Maskinporten: $e")
                    }
                }
                throw e
            },
        )
    }
}
