package no.nav.helsearbeidsgiver.maskinporten

import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64
import java.util.Date
import java.util.UUID

interface MaskinportenClientConfig {
    val endpoint: String

    fun getJwtAssertion(): String
}

class MaskinportenClientConfigPkey(
    val kid: String,
    val privateKey: String,
    val issuer: String,
    val consumerOrgNr: String,
    val scope: String = LPS_API_SCOPE,
    override val endpoint: String = "https://test.maskinporten.no/token",
) : MaskinportenClientConfig {
    companion object {
        const val LPS_API_SCOPE = "nav:inntektsmelding/lps.write"
    }
    private fun loadPrivateKey(key: String): PrivateKey {
        val keyText =
            key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("\n", "")
                .replace("\\s".toRegex(), "")

        val encoded = Base64.getDecoder().decode(keyText)
        return KeyFactory
            .getInstance("RSA")
            .generatePrivate(PKCS8EncodedKeySpec(encoded))
    }

    override fun getJwtAssertion(): String {
        val currentTimestamp = System.currentTimeMillis() / 1000

        val header =
            JWSHeader
                .Builder(JWSAlgorithm.RS256)
                .keyID(kid)
                .type(JOSEObjectType.JWT)
                .build()

        val claims =
            JWTClaimsSet
                .Builder()
                .audience("https://test.maskinporten.no/")
                .issuer(issuer)
                .claim("scope", scope)
                .claim("consumer_org", consumerOrgNr)
                .issueTime(Date(currentTimestamp * 1000))
                .expirationTime(Date((currentTimestamp + 120) * 1000))
                .jwtID(UUID.randomUUID().toString())
                .build()

        val privateKey = loadPrivateKey(privateKey)

        val signer = RSASSASigner(privateKey)
        val signedJWT = SignedJWT(header, claims)
        signedJWT.sign(signer)

        val jwtSigned = signedJWT.serialize()

        return jwtSigned
    }
}
