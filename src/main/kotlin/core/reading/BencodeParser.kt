package core.reading

import java.io.InputStream
import java.nio.file.Path
import kotlin.io.path.inputStream
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

// bencode is the encoding used for torrent files
class BencodeParser {

  fun parseBencodeFile(path: Path) {
    val map = mutableMapOf<String, Any>()
    path.inputStream().use { inputStream ->
      while (true) {
        val values = readNextObject(inputStream) ?: break
        if (values is Map<*, *>) {
          @Suppress("UNCHECKED_CAST") map.putAll(values as Map<out String, Any>)
        }
      }
    }
  }

  private fun readNextObject(inputStream: InputStream): Any? {
    val currentByte = inputStream.read()
    if (currentByte == -1 || currentByte.toChar() == 'e') {
      logger.trace { "End of object" }
      return null
    }
    when (currentByte.asBencodeType()) {
      BencodeType.NUMBER -> {
        val number = inputStream.readLong('e')!!
        logger.trace { "Number found: $number" }
        return number
      }
      BencodeType.BYTE_STRING -> {
        val lengthString = inputStream.readLong(':')
        val length = (currentByte.toChar().toString() + lengthString).toInt()
        val string = String(inputStream.readNBytes(length))
        logger.trace { "New String found: $string" }
        return string
      }
      BencodeType.LIST -> {
        TODO("Implement")
      }
      BencodeType.DICTIONARY -> {
        val map = mutableMapOf<String, Any>()
        while (true) {
          val keyLength = inputStream.readLong(':')?.toInt() ?: break
          val key = String(inputStream.readNBytes(keyLength))

          logger.trace { "Dictionary key: $key" }
          val value = readNextObject(inputStream)!!
          map[key] = value
        }
        return map
      }
    }
  }

  private fun InputStream.readLong(endSuffix: Char): Long? {
    var numberString = ""
    while (true) {
      val byte = this.read().toChar()
      if (byte == endSuffix || byte == 'e') {
        break
      }
      numberString += byte
    }
    return when {
      numberString.isEmpty() -> null
      else -> numberString.toLong()
    }
  }
}

fun Int.asBencodeType(): BencodeType {
  return when (this.toChar()) {
    'd' -> BencodeType.DICTIONARY
    'l' -> BencodeType.LIST
    'i' -> BencodeType.NUMBER
    else -> BencodeType.BYTE_STRING
  }
}
