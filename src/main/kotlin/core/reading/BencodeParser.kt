package core.reading

import java.nio.file.Path
import kotlin.io.path.readBytes

class BencodeParser {

  fun parseBencodeFile(path: Path) {
    val bytes = path.readBytes()
    bytes.forEach { print(it.toChar().toString()) }
    println()

    var pos = 0
    while (pos < bytes.size) {
      pos = readNextObject(pos, bytes)
      pos++
    }
  }

  private fun readNextObject(startPos: Int, bytes: ByteArray): Int {
    if (startPos >= bytes.size || bytes[startPos].toInt().toChar() == 'e') {
      println("DONE")
      return startPos
    }
    var pos = startPos
    val currentByte = bytes[pos]
    when (currentByte.asBencodeType()) {
      BencodeType.INTEGER -> {
        // drop prefix
        pos++
        val numberEndIndex = bytes.firstIndexFrom(pos) { it == 'e'.code.toByte() }
        val number = String(bytes.copyOfRange(pos, numberEndIndex))
        // drop suffix
        pos = numberEndIndex + 1
        println("Number found: $number")
      }
      BencodeType.BYTE_STRING -> {
        val lengthEndIndex = bytes.firstIndexFrom(pos) { it == ':'.code.toByte() }
        val length = String(bytes.copyOfRange(pos, lengthEndIndex)).toInt()
        val string = String(bytes.copyOfRange(lengthEndIndex + 1, lengthEndIndex + 1 + length))
        pos = lengthEndIndex + 1 + length
        println("New String found: $string")
      }
      BencodeType.LIST -> {
        // drop prefix
        pos++
        pos = readNextObject(pos, bytes)
        // drop suffix
        pos++
        println("New list entry found")
      }
      BencodeType.DICTIONARY -> {
        // drop prefix
        pos++
        while (pos < bytes.size && bytes[pos].toInt().toChar() != 'e') {
          val lengthEndIndex = bytes.firstIndexFrom(pos) { it == ':'.code.toByte() }
          val length = String(bytes.copyOfRange(pos, lengthEndIndex)).toInt()
          val keyEndIndex = lengthEndIndex + 1 + length
          val key = String(bytes.copyOfRange(lengthEndIndex + 1, keyEndIndex))
          println("Dictionary key: $key")
          pos = readNextObject(keyEndIndex, bytes)
          val a = 5
        }
      }
    }
    return pos
  }
}

fun Byte.asBencodeType(): BencodeType {
  return when (this.toInt().toChar()) {
    'd' -> BencodeType.DICTIONARY
    'l' -> BencodeType.LIST
    'i' -> BencodeType.INTEGER
    else -> BencodeType.BYTE_STRING
  }
}

fun ByteArray.firstIndexFrom(offset: Int, predicate: (Byte) -> Boolean): Int {
  (offset..this.size).forEach { index ->
    if (predicate(this[index])) {
      return index
    }
  }
  return -1
}
