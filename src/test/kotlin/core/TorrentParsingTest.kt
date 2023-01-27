package core

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
    assertEquals("linuxmint-21.1-cinnamon-64bit.iso", torrentData.torrentInfo.name)
    assertTrue(torrentData.torrentInfo.files.isEmpty())
    assertEquals(1279, torrentData.torrentInfo.pieces.size)
    // TODO: figure a way to handle lengths as they are in bytes
    assertEquals(2494654, torrentData.torrentInfo.length)
    assertEquals(1256412, torrentData.torrentInfo.pieceLength)
  }
}
