package core.convertion

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

enum class ByteSize(val sizeInBytes: Long) {
  KIBIBYTE(1024),
  MEBIBYTE(KIBIBYTE.sizeInBytes * KIBIBYTE.sizeInBytes),
  GIBIBYTE(MEBIBYTE.sizeInBytes * KIBIBYTE.sizeInBytes)
}

fun Long.convertTo(byteSize: ByteSize): Double {
  return (this.toDouble() / byteSize.sizeInBytes).roundToTwoPlaces()
}

private fun Double.roundToTwoPlaces(): Double {
  val df = DecimalFormat("#.##", DecimalFormatSymbols(Locale.ENGLISH))
  df.roundingMode = RoundingMode.FLOOR
  return df.format(this).toDouble()
}
