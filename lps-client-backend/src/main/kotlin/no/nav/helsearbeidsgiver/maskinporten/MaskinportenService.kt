package no.nav.helsearbeidsgiver.maskinporten

import no.nav.helsearbeidsgiver.maskinportenClientId
import no.nav.helsearbeidsgiver.maskinportenClientIssuer
import no.nav.helsearbeidsgiver.maskinportenIntegrasjonsId
import no.nav.helsearbeidsgiver.maskinportenKid
import no.nav.helsearbeidsgiver.maskinportenPrivateKey
import no.nav.helsearbeidsgiver.maskinportenTokenEndpoint
import no.nav.helsearbeidsgiver.systemId

class MaskinportenService {
    private val httpClient = createHttpClient()


    fun getMaskinportenTokenForSystembruker(orgNr:String,scope: String) = MaskinportenClient(
        maskinportenClientConfig =
        MaskinportenClientConfigPkey(
            kid = maskinportenKid,
            privateKey = maskinportenPrivateKey,
            issuer = maskinportenClientIssuer,
            scope = scope,
            clientId = maskinportenIntegrasjonsId,
            endpoint = maskinportenTokenEndpoint,
            additionalClaims = getSystemBrukerClaim(orgNr),
        )
    )

    fun getSimpleMaskinportenTokenForScope(scope: String) = MaskinportenClient(
        maskinportenClientConfig =
        MaskinportenClientConfigPkey(
            kid = maskinportenKid,
            privateKey = maskinportenPrivateKey,
            issuer = maskinportenClientIssuer,
            scope = scope,
            clientId = maskinportenIntegrasjonsId,
            endpoint = maskinportenTokenEndpoint,
        )
    )

    fun getMaskinportenTokenForOrgNr(
        consumerOrgNr: String,
        scope: String = "nav:inntektsmelding/lps.write",
    ) = MaskinportenClient(
        maskinportenClientConfig =
        MaskinportenClientConfigPkey(
            kid = maskinportenKid,
            privateKey = maskinportenPrivateKey,
            issuer = maskinportenClientIssuer,
            scope = scope,
            clientId = maskinportenIntegrasjonsId,
            endpoint = maskinportenTokenEndpoint,
            additionalClaims = getSystemBrukerClaim(consumerOrgNr),
        ),
    )
}
