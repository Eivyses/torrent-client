package core

import core.convertion.ByteSize
import core.convertion.convertTo
import core.reading.TorrentReader
import kotlin.io.path.toPath
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.Test

class TorrentParsingTest {

  @Test
  fun testSingleFileTorrentParsing() {
    val torrentFile =
        TorrentParsingTest::class
            .java
            .getResource("/reading/linuxmint-21.1-cinnamon-64bit.iso.torrent")!!
            .toURI()
            .toPath()
    val torrentReader = TorrentReader()

    val torrentData = torrentReader.readTorrentFile(torrentFile)
    assertEquals("Transmission/3.00 (bb6b5a062e)", torrentData.createdBy)
    assertEquals(1671368224, torrentData.creationDate)

    assertTrue(torrentData.torrentInfo.files.isEmpty())
    assertEquals("linuxmint-21.1-cinnamon-64bit.iso", torrentData.torrentInfo.name)
    assertEquals(1279, torrentData.torrentInfo.pieces.size)
    assertEquals("b0a64119022786e4f6a0bfd797158ee0e006a8dd", torrentData.torrentInfo.hash)
    assertEquals(2.49, torrentData.torrentInfo.length.convertTo(ByteSize.GIBIBYTE))
    assertEquals(2.0, torrentData.torrentInfo.pieceLength.convertTo(ByteSize.MEBIBYTE))
  }
}
