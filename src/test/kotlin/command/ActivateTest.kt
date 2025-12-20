package command

import BaseTestWithTestConfiguration
import com.an5on.command.Activate
import com.an5on.config.ActiveConfiguration.configuration
import com.github.ajalt.clikt.command.test
import kotlinx.coroutines.test.runTest
import org.apache.commons.io.file.PathUtils
import java.util.concurrent.locks.ReentrantLock
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.pathString
import kotlin.test.*

private val globalLock = ReentrantLock()

class ActivateTest : BaseTestWithTestConfiguration() {
    private val command = Activate

    private val activeStatePath
        get() = configuration.global.activeStatePath.toAbsolutePath()
    private val localStatePath
        get() = configuration.global.localStatePath.toAbsolutePath()

    @BeforeTest
    fun initialize() {
        globalLock.lock()
    }

    @AfterTest
    fun cleanup() {
        globalLock.unlock()
    }

    @Test
    fun testActivateInSampleState1WithoutArguments() = runTest {

        localStatePath.resolve("diff.txt").toFile().apply { writeText("hello") }
        localStatePath.resolve("hello.txt").toFile().apply { writeText("test") }

        val result = command.test(listOf(localStatePath.pathString), stdin = "y")

        assertEquals(0, result.statusCode)
        assertTrue(localStatePath.resolve("diff.txt").exists())
        assertTrue(
            PathUtils.fileContentEquals(
                localStatePath.resolve("hello.txt"),
                activeStatePath.resolve("hello.txt")
            )
        )
        assertTrue(PathUtils.fileContentEquals(localStatePath.resolve("diff.txt"), activeStatePath.resolve("diff.txt")))

        localStatePath.resolve("hello.txt").toFile().apply { writeText("") }
        activeStatePath.resolve("hello.txt").toFile().apply { writeText("") }
        localStatePath.resolve("diff.txt").deleteIfExists()
        activeStatePath.resolve("diff.txt").deleteIfExists()
    }

//    @Test
//    fun testActivateInSampleState1WithNoRecursive() = runTest {
//
//        val localDir = createTempDirectory(localStatePath)
//        val localSubDir = createTempDirectory(localDir, "subdir")
//        val localFile = localSubDir.resolve("diff.txt")
//        localFile.toFile().writeText("")
//
//        val result = command.test(listOf("--no-recursive", localDir.pathString), stdin = "y\n")
//        val activeDir = Path(localDir.pathString.replace(localStatePath.pathString, activeStatePath.pathString))
//        val activeSubDir = Path(localSubDir.pathString.replace(localStatePath.pathString, activeStatePath.pathString))
//        val activeFile = activeSubDir.resolve("diff.txt")
//
//        assertEquals(0, result.statusCode)
//        assertTrue(activeDir.exists())
//        assertFalse(activeSubDir.exists())
//        assertFalse(activeFile.exists())
//        localFile.deleteIfExists()
//        localSubDir.deleteIfExists()
//        localDir.deleteIfExists()
//        activeDir.deleteIfExists()
//    }

    @Test
    fun testActivateInSampleState1WithExclude() = runTest {

        val localIncludeFile = localStatePath.resolve("include.txt")
        val localExcludeFile = localStatePath.resolve("exclude.md")
        localIncludeFile.toFile().writeText("")
        localExcludeFile.toFile().writeText("")

        val result = command.test(listOf("-x", ".*\\.md", localStatePath.pathString), stdin = "y\n")
        val activeIncludeFile = activeStatePath.resolve("include.txt")
        val activeExcludeFile = activeStatePath.resolve("exclude.md")

        assertEquals(0, result.statusCode)
        assertTrue(activeIncludeFile.exists())
        assertFalse(activeExcludeFile.exists())
        activeIncludeFile.deleteIfExists()
        localIncludeFile.deleteIfExists()
        localExcludeFile.deleteIfExists()
    }

    @Test
    fun testActivateInSampleState1WithInclude() = runTest {

        val localIncludeFile = localStatePath.resolve("include.md")
        val localExcludeFile = localStatePath.resolve("exclude.txt")
        localIncludeFile.toFile().writeText("")
        localExcludeFile.toFile().writeText("")

        val result = command.test(listOf("-i", ".*\\.md", localStatePath.pathString), stdin = "y\n")
        val activeIncludeFile = activeStatePath.resolve("include.md")
        val activeExcludeFile = activeStatePath.resolve("exclude.txt")

        assertEquals(0, result.statusCode)
        assertTrue(activeIncludeFile.exists())
        assertFalse(activeExcludeFile.exists())
        activeIncludeFile.deleteIfExists()
        localIncludeFile.deleteIfExists()
        localExcludeFile.deleteIfExists()
    }

}