package core.network

const val CONNECTION_RESPONSE_SIZE = 16
const val ANNOUNCE_RESPONSE_SIZE = 65508

data class ConnectionResponse(val action: Int, val transactionId: Int, val connectionId: Long)

data class AnnounceResponse(
    val action: Int,
    val transactionId: Int,
    val interval: Int,
    val leechers: Int,
    val seeders: Int,
    val ipAddress: Int, // TODO: this doesn't seem right?
    val tcpPort: Short
)
