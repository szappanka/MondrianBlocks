package bme.aut.panka.mondrianblocks.network

import retrofit2.http.Body
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("/api/recommended_game/search")
    suspend fun getGameConfigurations(
        @Query("gameId") gameId: String,
        @Query("completed") completed: Boolean? = false,
        @Query("username") username: String
    ): Response<List<RecommendedGame>>

    @POST("/api/gameplay")
    suspend fun postGameplayResult(
        @Body resultData: String
    ): Response<String>

    @GET("/api/recommended_game/config/{id}")
    suspend fun getGameConfigurationById(
        @Path("id") configId: String
    ): Response<String>

    @GET("/api/user/personal_data")
    suspend fun getUserPersonalData(
    ): Response<CoglicaUser>

    @GET("/api/recommended_game/all")
    suspend fun triggerGameAssignment(): Response<Unit>

    @GET("/api/recommended_game/config/{id}")
    suspend fun getDetailedGameConfig(
        @Path("id") configId: String
    ): Response<RecommendedGameConfig>
}
