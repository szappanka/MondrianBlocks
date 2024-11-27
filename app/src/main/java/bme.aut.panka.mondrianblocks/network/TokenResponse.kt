package bme.aut.panka.mondrianblocks.network

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)