package core.reading

data class TorrentData(
    val numbers: MutableList<Int>,
    val strings: MutableList<String>,
    val lists: MutableList<MutableList<String>>,
    val maps: MutableMap<String, Any>
)

enum class BencodeType {
  INTEGER,
  LIST,
  DICTIONARY,
  BYTE_STRING
}
