package type.config

data class Config(
    val admin_id: List<Long>,
    val bot_token: String,
    val corona_headers: CoronaHeaders,
    val ffmpeg_path: String,
    val group_id: Long,
    val speech_api: String,
    val python3_path: String,
    val youget_path: String
)