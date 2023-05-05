package core.network

import core.reading.TorrentData
import java.nio.ByteBuffer

// all request and response structures used for UDP protocol communication. More info in
// http://www.bittorrent.org/beps/bep_0015.html

fun getConnectRequest(): ByteArray {
  // Offset  Size            Name            Value
  // 0       64-bit integer  protocol_id     0x41727101980 // magic constant
  // 8       32-bit integer  action          0 // connect
  // 12      32-bit integer  transaction_id
  // 16
  val buffer = ByteBuffer.allocate(16)
  buffer.putLong(0x41727101980)
  buffer.putInt(0)
  buffer.putInt(0)
  return buffer.array()
}

fun parseConnectionResponse(bytes: ByteArray): ConnectionResponse {
  // Receive a packet
  // Offset  Size            Name            Value
  // 0       32-bit integer  action          0 // connect
  // 4       32-bit integer  transaction_id
  // 8       64-bit integer  connection_id
  // 16
  val action = ByteBuffer.wrap(bytes.copyOfRange(0, 4)).getInt()
  val transactionId = ByteBuffer.wrap(bytes.copyOfRange(4, 8)).getInt()
  val connectionId = ByteBuffer.wrap(bytes.copyOfRange(8, 16)).getLong()
  return ConnectionResponse(
      action = action, transactionId = transactionId, connectionId = connectionId)
}

fun getAnnounceRequest(
    torrentData: TorrentData,
    connectionResponse: ConnectionResponse
): ByteArray {
  // Offset  Size    Name    Value
  // 0       64-bit integer  connection_id
  // 8       32-bit integer  action          1 // announce
  // 12      32-bit integer  transaction_id
  // 16      20-byte string  info_hash
  // 36      20-byte string  peer_id
  // 56      64-bit integer  downloaded
  // 64      64-bit integer  left
  // 72      64-bit integer  uploaded
  // 80      32-bit integer  event           0 // 0: none; 1: completed; 2: started; 3: stopped
  // 84      32-bit integer  IP address      0 // default
  // 88      32-bit integer  key
  // 92      32-bit integer  num_want        -1 // default
  // 96      16-bit integer  port
  // 98
  val buffer = ByteBuffer.allocate(98)
  buffer.putLong(connectionResponse.connectionId)
  buffer.putInt(1) // action
  buffer.putInt(connectionResponse.transactionId)
  buffer.put(torrentData.torrentInfo.hash)
  // TODO: make unique per client?
  buffer.put("12345678901234567890".toByteArray())
  buffer.putLong(torrentData.downloaded)
  buffer.putLong(torrentData.left)
  buffer.putLong(torrentData.uploaded)
  buffer.putInt(torrentData.event.value)
  buffer.putInt(0) // ip, 0 = default
  buffer.putInt(0) // key
  buffer.putInt(10) // num_want
  // TODO: port huh?
  buffer.putShort(0) // port
  return buffer.array()
}

fun parseAnnounceResponse(bytes: ByteArray): AnnounceResponse {
  // Offset      Size            Name            Value
  // 0           32-bit integer  action          1 // announce
  // 4           32-bit integer  transaction_id
  // 8           32-bit integer  interval
  // 12          32-bit integer  leechers
  // 16          32-bit integer  seeders
  // 20 + 6 * n  32-bit integer  IP address
  // 24 + 6 * n  16-bit integer  TCP port
  // 20 + 6 * N
  val action = ByteBuffer.wrap(bytes.copyOfRange(0, 4)).getInt()
  val transactionId = ByteBuffer.wrap(bytes.copyOfRange(4, 8)).getInt()
  val interval = ByteBuffer.wrap(bytes.copyOfRange(8, 12)).getInt()
  val leechers = ByteBuffer.wrap(bytes.copyOfRange(12, 16)).getInt()
  // TODO: reading these is different :/
  val seeders = ByteBuffer.wrap(bytes.copyOfRange(16, 20)).getInt()
  val ipAddress = ByteBuffer.wrap(bytes.copyOfRange(20, 24)).getInt()
  val tcpPort = ByteBuffer.wrap(bytes.copyOfRange(24, 28)).getInt()
  return AnnounceResponse(
      action = action,
      transactionId = transactionId,
      interval = interval,
      leechers = leechers,
      seeders = seeders,
      ipAddress = ipAddress,
      tcpPort = tcpPort)
}
