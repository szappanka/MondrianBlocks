package bme.aut.panka.mondrianblocks.network

data class OAuthResponse(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Int,
    val refreshToken: String?,
    val scope: String?,
    val idToken: String?
)
