package type.config

data class Config(
    val admin_id: List<Long>,
    val bot_token: String,
    val handle_method: String,
    val corona_headers: CoronaHeaders,
    val ffmpeg_path: String,
    val group_id: Long,
    val speech_api: String,
    val python3_path: String,
    val youget_path: String,
    val webhook_url: String,
    val webhook_proxy_port: Int,
    val cert_file_path: String,
    val crypto_key: String
)
