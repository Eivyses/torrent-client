package core.reading

import core.crypto.hashAsSHA1
import mu.KotlinLogging
import java.nio.file.Path
import kotlin.io.path.readBytes

private val logger = KotlinLogging.logger {}

private const val PIECE_SIZE = 20

class TorrentReader {
  private val parser = BencodeParser()

  fun readTorrentFile(path: Path): TorrentData {
    val parsedMap = parser.parseBencodeFile(path)
    val urls = getUrls(parsedMap)
    val createdBy = parsedMap.getValueAsParsedString("created by")
    val creationDate = parsedMap.getValue("creation date") as Long

    val infoMap = parsedMap.getValue("info") as Map<String, Any>
    val files = getFiles(infoMap)
    val length = (infoMap["length"] ?: files.sumOf { it.length }) as Long
    val name = infoMap.getValueAsParsedString("name")

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
            files = files,
            length = length,
            name = name,
            pieceLength = pieceLength,
            pieces = pieces,
            hash = hash)
    val torrentData =
        TorrentData(
            urls = urls,
            createdBy = createdBy,
            creationDate = creationDate,
            torrentInfo = torrentInfo)
    logger.debug { "Torrent file read: $torrentData" }
    return torrentData
  }

  // torrent hash is calculated over the content of the `info` dictionary in bencode form
  private fun getTorrentHash(path: Path): String {
    val bytes = path.readBytes()
    val searchString = "4:info"
    val infoOffset = bytes.findFirst(searchString.toByteArray())
    val infoMap = bytes.copyOfRange(infoOffset + searchString.length, bytes.size - 1)
    return hashAsSHA1(infoMap)
  }

  private fun getUrls(parsedMap: Map<String, Any>): List<String> {
    val baseUrl = parsedMap.getValueAsParsedString("announce")
    val urls = mutableListOf(baseUrl)
    val additionalUrls =
        parsedMap.getOrElse("announce-list") { emptyList<List<ByteArray>>() }
            as List<List<ByteArray>>
    additionalUrls
        .flatten()
        .map { String(it) }
        .filter { !urls.contains(it) }
        .forEach { urls.add(it) }
    return urls.toList()
  }

  private fun getFiles(infoMap: Map<String, Any>): List<TorrentFile> {
    val files = mutableListOf<TorrentFile>()
    val filesMap =
        infoMap.getOrElse("files") { emptyList<Map<String, Any>>() } as List<Map<String, Any>>
    filesMap.forEach {
      val length = it.getValue("length") as Long
      val paths = it.getValue("path") as List<ByteArray>
      val fullPath = paths.joinToString("/") { path -> String(path) }
      files.add(TorrentFile(fullPath, length))
    }
    return files.toList()
  }
}

private fun Map<String, Any>.getValueAsParsedString(key: String): String {
  return String(this.getValue(key) as ByteArray)
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
