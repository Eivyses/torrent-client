package core.reading

import java.io.InputStream
import java.nio.file.Path
import kotlin.io.path.inputStream
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

// bencode is the encoding used for torrent files
// https://en.wikipedia.org/wiki/Bencode
class BencodeParser {

  fun parseBencodeFile(path: Path): Map<String, Any> {
    val map = mutableMapOf<String, Any>()
    path.inputStream().use { inputStream ->
      while (true) {
        val values = readNextObject(inputStream) ?: break
        if (values is Map<*, *>) {
          @Suppress("UNCHECKED_CAST") map.putAll(values as Map<out String, Any>)
        }
      }
    }
    return map.toMap()
  }

  private fun readNextObject(inputStream: InputStream): Any? {
    val currentByte = inputStream.read()
    if (currentByte == -1 || currentByte.toChar() == 'e') {
      return null
    }
    when (currentByte.asBencodeType()) {
      BencodeType.NUMBER -> {
        // number format: i<number>e
        val number = inputStream.readLongUntilSuffix('e')!!
        logger.trace { "Number found: $number" }
        return number
      }
      BencodeType.BYTE_STRING -> {
        // byte string format: <length>:<content>, we did already read first digit of the number
        val lengthString = inputStream.readLongUntilSuffix(':') ?: ""
        val length = (currentByte.toChar().toString() + lengthString).toInt()
        val bytes = inputStream.readNBytes(length)
        val string = String(bytes)
        logger.trace { "New String found: $string" }
        return bytes
      }
      BencodeType.LIST -> {
        // list format: l<content>e
        // list can contain anything
        val list = mutableListOf<Any>()
        while (true) {
          val nextValue = readNextObject(inputStream) ?: break
          list.add(nextValue)
        }
        return list
      }
      BencodeType.DICTIONARY -> {
        // dictionary format: d<content>e
        // content key is always BYTE_STRING, value can be anything
        val map = mutableMapOf<String, Any>()
        while (true) {
          val keyLength = inputStream.readLongUntilSuffix(':')?.toInt() ?: break
          val key = String(inputStream.readNBytes(keyLength))

          logger.trace { "Dictionary key: $key" }
          val value = readNextObject(inputStream)!!
          map[key] = value
        }
        return map
      }
    }
  }

  private fun InputStream.readLongUntilSuffix(endSuffix: Char): Long? {
    var numberString = ""
    while (true) {
      val byte = this.read().toChar()
      // additional escape in case we're reading end of complex object
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
