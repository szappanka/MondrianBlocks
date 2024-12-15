package bme.aut.panka.mondrianblocks.network

data class RecommendedGame(
    val id: Int,
    val gameId: Int,
    val name: String,
    val description: String,
    val thumbnail: String,
    val recommendationDate: String,
    val recommender: String,
    val recommendedTo: String,
    val completed: Boolean,
    val config: Map<String, Any>
)

