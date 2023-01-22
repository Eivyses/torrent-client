package core.reading

import java.nio.file.Path
import kotlin.io.path.exists

class TorrentReader {
  private val parser = BencodeParser()

  fun readTorrentFile(path: Path) {
    println("exists: ${path.exists()}")
    parser.parseBencodeFile(path)
  }
}
