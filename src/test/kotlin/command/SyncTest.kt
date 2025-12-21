package command

import BaseTestWithTestConfiguration
import com.an5on.command.Sync
import com.an5on.config.ActiveConfiguration.configuration
import com.github.ajalt.clikt.command.test
import kotlinx.coroutines.test.runTest
import org.apache.commons.io.file.PathUtils
import org.junit.jupiter.api.BeforeEach
import java.nio.file.StandardOpenOption
import kotlin.io.path.createFile
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertTrue

class SyncTest : BaseTestWithTestConfiguration() {
    private val command = Sync

    private val activeStatePath
        get() = configuration.global.activeStatePath
    private val localStatePath
        get() = configuration.global.localStatePath

    @BeforeEach
    fun setup() {
        PathUtils.cleanDirectory(localStatePath)
        PathUtils.cleanDirectory(activeStatePath)
    }

    @Test
    fun testSyncWithoutArgument() = runTest {
        activeStatePath.resolve("hello.txt")
            .writeText("A total different message", options = arrayOf(StandardOpenOption.CREATE_NEW))
        localStatePath.resolve("hello.txt").createFile()

        command.test("-y")

        assertTrue(
            PathUtils.fileContentEquals(
                activeStatePath.resolve("hello.txt"),
                localStatePath.resolve("hello.txt")
            )
        )
    }

// Shall be fixed: Race condition
//    @Test
//    fun testSyncWithExclude() = runTest {
//
//        val activeIncludeFile = activeStatePath.resolve("include.txt")
//        val activeExcludeFile = activeStatePath.resolve("exclude.md")
//        val localIncludeFile = localStatePath.resolve("include.txt")
//        val localExcludeFile = localStatePath.resolve("exclude.md")
//
//        activeIncludeFile.writeText("a test message which should be synced", options = arrayOf(StandardOpenOption.CREATE_NEW))
//        activeExcludeFile.writeText("a test message which should not be synced", options = arrayOf(StandardOpenOption.CREATE_NEW))
//        localIncludeFile.createFile()
//        localExcludeFile.createFile()
//
//        val result = command.test(listOf("-y", "-x", ".*\\.md", activeStatePath.pathString))
//
//        assertEquals(0, result.statusCode)
//        assertTrue(
//            PathUtils.fileContentEquals(
//                activeIncludeFile,
//                localIncludeFile
//            )
//        )
//        assertFalse(
//            PathUtils.fileContentEquals(
//                activeExcludeFile,
//                localExcludeFile
//            )
//        )
//    }

//    @Test
//    fun testActivateWithInclude() = runTest {
//
//        val localIncludeFile = localStatePath.resolve("include.md")
//        val localExcludeFile = localStatePath.resolve("exclude.txt")
//        val activeIncludeFile = activeStatePath.resolve("include.md")
//        val activeExcludeFile = activeStatePath.resolve("exclude.txt")
//
//        activeIncludeFile.writeText(
//            "a test message which should be synced",
//            options = arrayOf(StandardOpenOption.CREATE_NEW)
//        )
//        activeExcludeFile.writeText(
//            "a test message which should not be synced",
//            options = arrayOf(StandardOpenOption.CREATE_NEW)
//        )
//        localIncludeFile.createFile()
//        localExcludeFile.createFile()
//
//        val result = command.test(listOf("-y", "-i", ".*\\.md", activeStatePath.pathString))
//
//        assertEquals(0, result.statusCode)
//        assertTrue(
//            PathUtils.fileContentEquals(
//                activeIncludeFile,
//                localIncludeFile
//            )
//        )
//        assertFalse(
//            PathUtils.fileContentEquals(
//                activeExcludeFile,
//                localExcludeFile
//            )
//        )
//    }
}