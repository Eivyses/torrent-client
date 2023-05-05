import core.network.UdpSocket
import core.reading.TorrentReader
import mu.KotlinLogging
import java.nio.file.Path

private val logger = KotlinLogging.logger {}

fun main() {
  println("Hello World!")

  val reader = TorrentReader()
  val torrentData =
      reader.readTorrentFile(
          Path.of("C:\\Users\\Eivys\\Downloads\\linuxmint-21.1-cinnamon-64bit.iso.torrent"))

  logger.debug { torrentData.urls }

  val udpSocket = UdpSocket(8888, torrentData.urls.first())
  val connectResponse = udpSocket.connect()

  udpSocket.announce(torrentData, connectResponse)
}
