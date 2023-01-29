package core.reading

data class TorrentData(
    val urls: List<String>,
    val createdBy: String,
    val creationDate: Long,
    val torrentInfo: TorrentInfo
)

data class TorrentInfo(
    val files: List<TorrentFile>,
    val length: Long,
    val name: String,
    val pieceLength: Long,
    val hash: String,
    val pieces: List<ByteArray>
)

data class TorrentFile(val name: String, val length: Long)

enum class BencodeType {
  NUMBER,
  LIST,
  DICTIONARY,
  BYTE_STRING
}
