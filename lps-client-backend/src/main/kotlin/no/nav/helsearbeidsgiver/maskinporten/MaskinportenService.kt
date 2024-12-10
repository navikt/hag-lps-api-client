package no.nav.helsearbeidsgiver.maskinporten

import no.nav.helsearbeidsgiver.maskinportenClientId
import no.nav.helsearbeidsgiver.maskinportenClientIssuer
import no.nav.helsearbeidsgiver.maskinportenIntegrasjonsId
import no.nav.helsearbeidsgiver.maskinportenKid
import no.nav.helsearbeidsgiver.maskinportenPrivateKey
import no.nav.helsearbeidsgiver.maskinportenTokenEndpoint

class MaskinportenService {
    private val httpClient = createHttpClient()

    fun getMaskinportenToken(): String = "maskinporten-token"

    fun getMaskinportenTokenForOrgNr(): String = "maskinporten-token2"

    fun getMaskinportenTokenForSystembruker(): String = "maskinporten-token3"

    fun getMaskinportenTokenForOrgNr(
        consumerOrgNr: String,
        scope: String = "nav:inntektsmelding/lps.write",
    ) = MaskinportenClient(
        maskinportenClientConfig =
        MaskinportenClientConfigPkey(
            kid = maskinportenKid,
            privateKey = maskinportenPrivateKey,
            issuer = maskinportenIntegrasjonsId,
            scope = scope,
            clientId = maskinportenClientId,
            endpoint = maskinportenTokenEndpoint,
            additionalClaims = getSystemBrukerClaim(consumerOrgNr),
        ),
    )
}
