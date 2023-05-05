package core.crypto

import java.security.MessageDigest

fun hashAsSHA1(data: ByteArray): ByteArray {
  val md = MessageDigest.getInstance("SHA-1")
  md.update(data, 0, data.size)
  val sha1hash: ByteArray = md.digest()
  return sha1hash
}

fun convertToHex(data: ByteArray): String {
  val sb = StringBuilder()
  for (b in data) {
    sb.append(String.format("%02x", b))
  }
  return sb.toString()
}
