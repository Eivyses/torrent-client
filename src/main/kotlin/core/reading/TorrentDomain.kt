package core.reading

data class TorrentData(
    val urls: List<String>,
    val createdBy: String,
    val creationDate: Long,
    val torrentInfo: TorrentInfo,
    val downloaded: Long = 0,
    val left: Long = 0,
    val uploaded: Long = 0,
    val event: TorrentEvent = TorrentEvent.NONE
)

data class TorrentInfo(
    val files: List<TorrentFile>,
    val length: Long,
    val name: String,
    val pieceLength: Long,
    val hash: ByteArray,
    val hashString: String,
    val pieces: List<ByteArray>
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as TorrentInfo

    if (files != other.files) return false
    if (length != other.length) return false
    if (name != other.name) return false
    if (pieceLength != other.pieceLength) return false
    if (!hash.contentEquals(other.hash)) return false
    return pieces == other.pieces
  }

  override fun hashCode(): Int {
    var result = files.hashCode()
    result = 31 * result + length.hashCode()
    result = 31 * result + name.hashCode()
    result = 31 * result + pieceLength.hashCode()
    result = 31 * result + hash.contentHashCode()
    result = 31 * result + pieces.hashCode()
    return result
  }

  override fun toString(): String {
    return "TorrentInfo(files=$files, length=$length, name='$name', pieceLength=$pieceLength, hashString='$hashString', pieces=${pieces.size})"
  }
}

data class TorrentFile(val name: String, val length: Long)

enum class BencodeType {
  NUMBER,
  LIST,
  DICTIONARY,
  BYTE_STRING
}

enum class TorrentEvent(val value: Int) {
  NONE(0),
  COMPLETED(1),
  STARTED(2),
  STOPPED(3)
}
