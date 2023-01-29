package core

import core.convertion.ByteSize
import core.convertion.convertTo
import core.reading.TorrentFile
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
    assertEquals(1674844412, torrentData.creationDate)
    assertEquals(listOf("udp://tracker.opentrackr.org:1337/announce"), torrentData.urls)

    assertEquals(
        listOf(
            TorrentFile("Eula.txt", 7490),
            TorrentFile("procexp.chm", 72154),
            TorrentFile("procexp.exe", 2834320),
            TorrentFile("procexp64.exe", 1505160),
            TorrentFile("procexp64a.exe", 1493376)),
        torrentData.torrentInfo.files)
    assertEquals("ProcessExplorer", torrentData.torrentInfo.name)
    assertEquals(361, torrentData.torrentInfo.pieces.size)
    assertEquals("7319bc061301f687f555954935dde0a03730d433", torrentData.torrentInfo.hash)
    assertEquals(5.63, torrentData.torrentInfo.length.convertTo(ByteSize.MEBIBYTE))
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
    assertEquals(1674844347, torrentData.creationDate)
    assertEquals(
        listOf(
            "udp://tracker.opentrackr.org:1337/announce",
            "udp://9.rarbg.com:2810/announce",
            "udp://tracker.torrent.eu.org:451/announce",
            "udp://opentracker.i2p.rocks:6969/announce"),
        torrentData.urls)

    assertTrue(torrentData.torrentInfo.files.isEmpty())
    assertEquals("webos-dev-manager.1.8.2.exe", torrentData.torrentInfo.name)
    assertEquals(935, torrentData.torrentInfo.pieces.size)
    assertEquals("2c469eb1c21c1aeaa444efc965add4e7e5ef5bd1", torrentData.torrentInfo.hash)
    assertEquals(58.38, torrentData.torrentInfo.length.convertTo(ByteSize.MEBIBYTE))
    assertEquals(64.0, torrentData.torrentInfo.pieceLength.convertTo(ByteSize.KIBIBYTE))
  }

  @Test
  fun testNestedFilesTorrentParsing() {
    val torrentFile =
        TorrentParsingTest::class
            .java
            .getResource("/reading/nestedFiles.torrent")!!
            .toURI()
            .toPath()
    val torrentReader = TorrentReader()

    val torrentData = torrentReader.readTorrentFile(torrentFile)
    assertEquals("kimbatt.github.io/torrent-creator", torrentData.createdBy)
    assertEquals(1675008843, torrentData.creationDate)
    assertEquals(
        listOf(
            "udp://exodus.desync.com:6969/announce",
            "https://opentracker.i2p.rocks:443/announce",
            "udp://open.demonii.com:1337/announce"),
        torrentData.urls)

    assertEquals(
        listOf(
            TorrentFile("Info/Eula.txt", 7490),
            TorrentFile("Info/notes/notes.txt", 7490),
            TorrentFile("main/procexp.chm", 72154),
            TorrentFile("main/procexp.exe", 2834320),
            TorrentFile("main/procexp64.exe", 1505160),
            TorrentFile("main/procexp64a.exe", 1493376)),
        torrentData.torrentInfo.files)
    assertEquals("ProcessExplorerV2", torrentData.torrentInfo.name)
    assertEquals(362, torrentData.torrentInfo.pieces.size)
    assertEquals("0f3eadae8646ad3303ba2f728179b7fd8e637e28", torrentData.torrentInfo.hash)
    assertEquals(5.64, torrentData.torrentInfo.length.convertTo(ByteSize.MEBIBYTE))
    assertEquals(16.0, torrentData.torrentInfo.pieceLength.convertTo(ByteSize.KIBIBYTE))
  }
}
