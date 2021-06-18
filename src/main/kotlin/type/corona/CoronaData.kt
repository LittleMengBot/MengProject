package type.corona

data class CoronaData(
    val errors: List<Any>,
    val `get`: String,
    val parameters: Parameters,
    val response: List<Response>,
    val results: Int
)
