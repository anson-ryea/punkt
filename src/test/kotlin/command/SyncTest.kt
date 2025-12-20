package command

import BaseTestWithTestConfiguration
import com.an5on.command.Sync
import com.an5on.config.ActiveConfiguration.configuration
import com.github.ajalt.clikt.command.test
import kotlinx.coroutines.test.runTest
import org.apache.commons.io.file.PathUtils
import kotlin.io.path.deleteIfExists
import kotlin.io.path.pathString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SyncTest : BaseTestWithTestConfiguration() {

    private val command = Sync

    private val activeStatePath
        get() = configuration.global.activeStatePath
    private val localStatePath
        get() = configuration.global.localStatePath


    @Test
    fun testSyncWithoutArgument() = runTest {
        activeStatePath.resolve("hello.txt").toFile().writeText("A total different messagee")
        localStatePath.resolve("hello.txt").toFile().writeText("")

        command.test("", stdin = "y\n")

        assertTrue(
            PathUtils.fileContentEquals(
                activeStatePath.resolve("hello.txt"),
                localStatePath.resolve("hello.txt")
            )
        )

        activeStatePath.resolve("hello.txt").toFile().writeText("")
        localStatePath.resolve("hello.txt").toFile().writeText("")
    }

//    @Test
//    fun testSyncWithNoRecursive() = runTest {
//        val activeDir = createTempDirectory(activeStatePath)
//        val activeSubDir = createTempDirectory(activeDir, "subdir")
//        val activeFile = activeSubDir.resolve("diff.txt")
//        activeFile.toFile().writeText("")
//
//        val result = command.test("--no-recursive", stdin = "y\n")
//        val localDir = Path(activeDir.pathString.replace(activeStatePath.pathString, localStatePath.pathString))
//        val localSubDir = Path(activeSubDir.pathString.replace(activeStatePath.pathString, localStatePath.pathString))
//        val localFile = activeSubDir.resolve("diff.txt")
//
//        assertEquals(0, result.statusCode)
//        assertTrue(localDir.exists())
//        assertFalse(localSubDir.exists())
//        assertFalse(localFile.exists())
//        activeFile.deleteIfExists()
//        activeSubDir.deleteIfExists()
//        activeDir.deleteIfExists()
//        localDir.deleteIfExists()
//    }

    @Test
    fun testSyncWithExclude() = runTest {

        val activeIncludeFile = activeStatePath.resolve("include.txt")
        val activeExcludeFile = activeStatePath.resolve("exclude.md")
        val localIncludeFile = localStatePath.resolve("include.txt")
        val localExcludeFile = localStatePath.resolve("exclude.md")

        activeIncludeFile.toFile().writeText("a test message which should be synced")
        activeExcludeFile.toFile().writeText("a test message which should not be synced")
        localIncludeFile.toFile().writeText("")
        localExcludeFile.toFile().writeText("")

        val result = command.test(listOf("-x", ".*\\.md", activeStatePath.pathString), stdin = "y\n")

        assertEquals(0, result.statusCode)
        assertTrue(
            PathUtils.fileContentEquals(
                activeIncludeFile,
                localIncludeFile
            )
        )
        assertFalse(
            PathUtils.fileContentEquals(
                activeExcludeFile,
                localExcludeFile
            )
        )
        activeIncludeFile.deleteIfExists()
        activeExcludeFile.deleteIfExists()
        localIncludeFile.deleteIfExists()
        localExcludeFile.deleteIfExists()
    }

    @Test
    fun testActivateInSampleState1WithInclude() = runTest {

        val localIncludeFile = localStatePath.resolve("include.md")
        val localExcludeFile = localStatePath.resolve("exclude.txt")
        val activeIncludeFile = activeStatePath.resolve("include.md")
        val activeExcludeFile = activeStatePath.resolve("exclude.txt")

        activeIncludeFile.toFile().writeText("a test message which should be synced")
        activeExcludeFile.toFile().writeText("a test message which should not be synced")
        localIncludeFile.toFile().writeText("")
        localExcludeFile.toFile().writeText("")

        val result = command.test(listOf("-i", ".*\\.md", activeStatePath.pathString), stdin = "y\n")

        assertEquals(0, result.statusCode)
        assertTrue(
            PathUtils.fileContentEquals(
                activeIncludeFile,
                localIncludeFile
            )
        )
        assertFalse(
            PathUtils.fileContentEquals(
                activeExcludeFile,
                localExcludeFile
            )
        )
        activeIncludeFile.deleteIfExists()
        activeExcludeFile.deleteIfExists()
        localIncludeFile.deleteIfExists()
        localExcludeFile.deleteIfExists()
    }
}