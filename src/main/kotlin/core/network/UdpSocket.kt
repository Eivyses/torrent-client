package core.network

import core.reading.TorrentData
import mu.KotlinLogging
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

private val logger = KotlinLogging.logger {}

class UdpSocket(socketPort: Int, announceUrl: String) {
  private val socket: DatagramSocket
  private val port: Int
  private val url: String

  init {
    port = announceUrl.substringAfterLast(":").substringBefore("/").toInt()
    url = announceUrl.substringAfter("udp://").substringBefore(":$port")
    socket = DatagramSocket(socketPort)
    socket.connect(InetAddress.getByName(url), port)
    socket.soTimeout = 30_000
  }

  private fun <T> sendReceiveData(
      responseSize: Int,
      requestData: () -> ByteArray,
      responseParse: (ByteArray) -> T
  ): T {
    val sendData = requestData()
    val sendPacket = sendData.asPacket()
    socket.send(sendPacket)

    val receiveData = ByteArray(responseSize)
    val receivePacket = receiveData.asPacket()
    socket.receive(receivePacket)

    return responseParse(receivePacket.data)
  }

  fun connect(): ConnectionResponse {
    val response =
        sendReceiveData(CONNECTION_RESPONSE_SIZE, { getConnectRequest() }) {
          parseConnectionResponse(it)
        }
    logger.debug { response }
    return response
  }

  fun announce(torrentData: TorrentData, connectionResponse: ConnectionResponse) {
    val response =
        sendReceiveData(
            ANNOUNCE_RESPONSE_SIZE, { getAnnounceRequest(torrentData, connectionResponse) }) {
              parseAnnounceResponse(it)
            }
    logger.debug { response }
  }
}

private fun ByteArray.asPacket(): DatagramPacket {
  return DatagramPacket(this, this.size)
}
