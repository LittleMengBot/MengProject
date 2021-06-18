package type.speech

data class Result(
    val audio: String?,
    val message: List<String>?,
    val synthesis_successful: Boolean
)