package type.crypto

data class CryptoInfo(
    val `data`: HashMap<String, Coin>,
    val status: Status
)