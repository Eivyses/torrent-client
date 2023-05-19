package core.network

import org.junit.Test
import java.nio.ByteBuffer
import kotlin.test.assertEquals

class UdpCommunicationResponseTest {

  @Test
  fun testParseConnectionResponse() {
    val action = 0
    val transactionId = 1234567890
    val connectionId = 465798456132496
    // Receive a packet
    // Offset  Size            Name            Value
    // 0       32-bit integer  action          0 // connect
    // 4       32-bit integer  transaction_id
    // 8       64-bit integer  connection_id
    // 16
    val buffer = ByteBuffer.allocate(CONNECTION_RESPONSE_SIZE)
    buffer.putInt(action)
    buffer.putInt(transactionId)
    buffer.putLong(connectionId)

    val response = parseConnectionResponse(buffer.array())
    assertEquals(action, response.action)
    assertEquals(transactionId, response.transactionId)
    assertEquals(connectionId, response.connectionId)
  }

  @Test
  fun testParseAnnounceResponse() {
    val action = 1
    val transactionId = 1234567890
    val interval = 3600
    val leechers = 1667
    val seeders = 420
    val peers =
        listOf<Pair<String, UShort>>(
            "245.242.231.61" to 8080u,
            "62.185.141.218" to 1123u,
            "192.168.64.12" to 51413u,
            "245.245.254.254" to 65500u)
    // Offset      Size            Name            Value
    // 0           32-bit integer  action          1 // announce
    // 4           32-bit integer  transaction_id
    // 8           32-bit integer  interval
    // 12          32-bit integer  leechers
    // 16          32-bit integer  seeders
    // 20 + 6 * n  32-bit integer  IP address
    // 24 + 6 * n  16-bit integer  TCP port
    // 20 + 6 * N
    val buffer = ByteBuffer.allocate(ANNOUNCE_RESPONSE_SIZE)
    buffer.putInt(action)
    buffer.putInt(transactionId)
    buffer.putInt(interval)
    buffer.putInt(leechers)
    buffer.putInt(seeders)
    peers.forEach { (ip, port) ->
      ip.split(".").forEach { ipPart -> buffer.put(ipPart.toInt().toByte()) }
      buffer.putShort(port.toShort())
    }

    val response = parseAnnounceResponse(buffer.array())
    assertEquals(action, response.action)
    assertEquals(transactionId, response.transactionId)
    assertEquals(interval, response.interval)
    assertEquals(leechers, response.leechers)
    assertEquals(seeders, response.seeders)
    assertEquals(peers, response.peers)
  }
}
