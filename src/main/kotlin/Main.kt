import core.reading.TorrentReader
import java.nio.file.Path

fun main() {
  println("Hello World!")

  val reader = TorrentReader()
  reader.readTorrentFile(
      Path.of(
          "C:\\Users\\Eivys\\Downloads\\The.Last.of.Us.S01E01.When.Youre.Lost.in.the.Darkness.1080p.AMZN.WEB-DL.DDP5.1.H.264-NTb.mkv.torrent"))
}
