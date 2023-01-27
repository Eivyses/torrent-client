package core.reading

import java.nio.file.Path
import kotlin.io.path.readBytes
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

private const val PIECE_SIZE = 20

class TorrentReader {
  private val parser = BencodeParser()

  fun readTorrentFile(path: Path): TorrentData {
    val parsedMap = parser.parseBencodeFile(path)
    val urls = String(parsedMap.getValue("announce") as ByteArray)

    val infoMap = parsedMap.getValue("info") as Map<String, Any>
    val files: List<String> = infoMap.getOrElse("files") { emptyList<String>() } as List<String>
    val length = infoMap.getValue("length") as Long
    val name = String(infoMap.getValue("name") as ByteArray)

    // number of bytes per piece in bytes
    val pieceLength = infoMap.getValue("piece length") as Long

    val piecesByteArray = infoMap.getValue("pieces") as ByteArray
    // pieces are strings whose length is a multiple of 20 bytes
    val piecesCount = piecesByteArray.size / PIECE_SIZE
    val pieces = mutableListOf<ByteArray>()
    for (i in 0 until piecesCount) {
      val startIndex = i * PIECE_SIZE
      val piece = piecesByteArray.copyOfRange(startIndex, startIndex + PIECE_SIZE)
      pieces.add(piece)
    }
    val hash = getTorrentHash(path)

    val torrentInfo =
        TorrentInfo(
            files = files, length = length, name = name, pieceLength = pieceLength, pieces = pieces)
    val torrentData = TorrentData(urls = listOf(urls), torrentInfo = torrentInfo)
    logger.debug { "Torrent file read: $torrentData" }
    return torrentData
  }

  // torrent hash is calculated over the content of the `info` dictionary in bencode form
  private fun getTorrentHash(path: Path): String {
    val bytes = path.readBytes()
    val searchString = "4:info"
    val infoOffset = bytes.findFirst(searchString.toByteArray())
    val infoMap = bytes.copyOfRange(infoOffset + searchString.length, bytes.size - 1)
    throw RuntimeException("Hash parsing failed")
  }
}

private fun ByteArray.findFirst(sequence: ByteArray): Int {
  var matchOffset = 0
  var start = 0
  var offset = 0
  while (offset < size) {
    if (this[offset] == sequence[matchOffset]) {
      if (matchOffset++ == 0) {
        start = offset
      }
      if (matchOffset == sequence.size) {
        return start
      }
    } else {
      matchOffset = 0
    }
    offset++
  }
  return -1
}
