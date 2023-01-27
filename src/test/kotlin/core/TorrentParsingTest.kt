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
        TorrentParsingTest::class.java.getResource("/reading/singleFile.torrent")!!.toURI().toPath()
    val torrentReader = TorrentReader()

    val torrentData = torrentReader.readTorrentFile(torrentFile)
    assertEquals("Transmission/3.00 (bb6b5a062e)", torrentData.createdBy)
    assertEquals(1671368224, torrentData.creationDate)
    assertEquals(listOf("udp://tracker.opentrackr.org:1337/announce"), torrentData.urls)

    assertTrue(torrentData.torrentInfo.files.isEmpty())
    assertEquals("linuxmint-21.1-cinnamon-64bit.iso", torrentData.torrentInfo.name)
    assertEquals(1279, torrentData.torrentInfo.pieces.size)
    assertEquals("b0a64119022786e4f6a0bfd797158ee0e006a8dd", torrentData.torrentInfo.hash)
    assertEquals(2.49, torrentData.torrentInfo.length.convertTo(ByteSize.GIBIBYTE))
    assertEquals(2.0, torrentData.torrentInfo.pieceLength.convertTo(ByteSize.MEBIBYTE))
  }

  @Test
  fun testMultiFileTorrentParsing() {
    val torrentFile =
        TorrentParsingTest::class.java.getResource("/reading/multiFile.torrent")!!.toURI().toPath()
    val torrentReader = TorrentReader()

    val torrentData = torrentReader.readTorrentFile(torrentFile)
    assertEquals("kimbatt.github.io/torrent-creator", torrentData.createdBy)
    assertEquals(1674837180, torrentData.creationDate)
    assertEquals(
        listOf(
            "udp://tracker.opentrackr.org:1337/announce",
            "udp://9.rarbg.com:2810/announce",
            "udp://tracker.torrent.eu.org:451/announce",
            "udp://opentracker.i2p.rocks:6969/announce"),
        torrentData.urls)

    assertEquals(
        listOf("procexp64a.exe", "procexp64.exe", "procexp.exe", "procexp.chm", "Eula.txt"),
        torrentData.torrentInfo.files)
    assertEquals("ProcessExplorer", torrentData.torrentInfo.name)
    assertEquals(361, torrentData.torrentInfo.pieces.size)
    assertEquals("7319bc061301f687f555954935dde0a03730d433", torrentData.torrentInfo.hash)
    assertEquals(5.60, torrentData.torrentInfo.length.convertTo(ByteSize.MEBIBYTE))
    assertEquals(16.0, torrentData.torrentInfo.pieceLength.convertTo(ByteSize.KIBIBYTE))
  }

  @Test
  fun testMultiTrackersTorrentParsing() {
    val torrentFile =
        TorrentParsingTest::class
            .java
            .getResource("/reading/multiTrackers.torrent")!!
            .toURI()
            .toPath()
    val torrentReader = TorrentReader()

    val torrentData = torrentReader.readTorrentFile(torrentFile)
    assertEquals("kimbatt.github.io/torrent-creator", torrentData.createdBy)
    assertEquals(1674837120, torrentData.creationDate)
    assertEquals(listOf("udp://tracker.opentrackr.org:1337/announce"), torrentData.urls)

    assertTrue(torrentData.torrentInfo.files.isEmpty())
    assertEquals("webos-dev-manager.1.8.2.exe", torrentData.torrentInfo.name)
    assertEquals(935, torrentData.torrentInfo.pieces.size)
    assertEquals("2c469eb1c21c1aeaa444efc965add4e7e5ef5bd1", torrentData.torrentInfo.hash)
    assertEquals(58.3, torrentData.torrentInfo.length.convertTo(ByteSize.MEBIBYTE))
    assertEquals(64.0, torrentData.torrentInfo.pieceLength.convertTo(ByteSize.KIBIBYTE))
  }
}
