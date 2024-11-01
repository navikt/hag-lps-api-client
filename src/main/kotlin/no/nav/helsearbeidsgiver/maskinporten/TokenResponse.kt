package no.nav.helsearbeidsgiver.maskinporten

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresInSeconds: Long,
    val scope: String,
) {
    override fun toString(): String =
        "TokenResponse(accessToken='${accessToken.take(3)}', tokenType='$tokenType', expiresInSeconds=$expiresInSeconds, scope='$scope')"
}

@Serializable
class TokenResponseWrapper(
    val tokenResponse: TokenResponse,
) {
    private val issueTime = System.currentTimeMillis() / 1000

    val remainingTimeInSeconds: Long
        get() = tokenResponse.expiresInSeconds - (System.currentTimeMillis() / 1000 - issueTime)

    val remainingTimePercentage: Double
        get() = (remainingTimeInSeconds.toDouble() / tokenResponse.expiresInSeconds) * 100
}
