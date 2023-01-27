import core.reading.TorrentReader
import java.nio.file.Path

fun main() {
  println("Hello World!")

  val reader = TorrentReader()
  reader.readTorrentFile(
      Path.of("C:\\Users\\Eivys\\Downloads\\linuxmint-21.1-cinnamon-64bit.iso.torrent"))
}
