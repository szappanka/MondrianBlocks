package bme.aut.panka.mondrianblocks.network

data class TokenRequest(
    val grantType: String,
    val code: String,
    val redirectUri: String,
    val clientId: String,
    val codeVerifier: String
)
