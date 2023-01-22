package core.reading

import java.io.InputStream
import java.nio.file.Path
import kotlin.io.path.inputStream
import kotlin.io.path.readBytes

// bencode is the encoding used for torrent files
class BencodeParser {

  fun parseBencodeFile(path: Path) {
    val bytes = path.readBytes()
    bytes.forEach { print(it.toChar().toString()) }
    println()

    path.inputStream().use { inputStream ->
      var pos = 0
      while (pos != -1) {
        pos = readNextObject(inputStream)
      }
    }
  }

  private fun readNextObject(inputStream: InputStream): Int {
    val currentByte = inputStream.read()
    if (currentByte == -1 || currentByte.toChar() == 'e') {
      println("End of object")
      return -1
    }
    when (currentByte.asBencodeType()) {
      BencodeType.NUMBER -> {
        val number = inputStream.readLong('e')!!
        println("Number found: $number")
      }
      BencodeType.BYTE_STRING -> {
        val lengthString = inputStream.readLong(':')
        val length = (currentByte.toChar().toString() + lengthString).toInt()
        val string = String(inputStream.readNBytes(length))
        println("New String found: $string")
      }
      BencodeType.LIST -> {
        //        pos = readNextObject(pos, bytes)
        // drop suffix
        inputStream.read()
        println("New list entry found")
      }
      BencodeType.DICTIONARY -> {
        while (true) {
          val keyLength = inputStream.readLong(':')?.toInt() ?: break
          val key = String(inputStream.readNBytes(keyLength))
          println("Dictionary key: $key")
          readNextObject(inputStream)
        }
      }
    }
    return currentByte
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
