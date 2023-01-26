package core.reading

data class TorrentData(val urls: List<String>, val torrentInfo: TorrentInfo)

data class TorrentInfo(
    val files: List<String>,
    val length: Long,
    val name: String,
    val pieceLength: Long,
    val pieces: List<ByteArray>
)

enum class BencodeType {
  NUMBER,
  LIST,
  DICTIONARY,
  BYTE_STRING
}
