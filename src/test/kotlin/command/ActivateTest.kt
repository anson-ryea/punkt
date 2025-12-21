package command

import BaseTestWithTestConfiguration
import com.an5on.command.Activate
import com.an5on.config.ActiveConfiguration.configuration
import com.github.ajalt.clikt.command.test
import kotlinx.coroutines.test.runTest
import org.apache.commons.io.file.PathUtils
import org.junit.jupiter.api.BeforeEach
import java.nio.file.StandardOpenOption
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.pathString
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ActivateTest : BaseTestWithTestConfiguration() {
    private val command = Activate

    private val activeStatePath
        get() = configuration.global.activeStatePath.toAbsolutePath()
    private val localStatePath
        get() = configuration.global.localStatePath.toAbsolutePath()

    @BeforeEach
    fun setup() {
        PathUtils.cleanDirectory(localStatePath)
        PathUtils.cleanDirectory(activeStatePath)
    }

    @Test
    fun testActivateWithoutArguments() = runTest {

        localStatePath.resolve("diff.txt").writeText("diff", options = arrayOf(StandardOpenOption.CREATE_NEW))
        localStatePath.resolve("hello.txt").writeText("hello", options = arrayOf(StandardOpenOption.CREATE_NEW))
        activeStatePath.resolve("hello.txt").createFile()

        val result = command.test(listOf("-y", localStatePath.pathString))

        assertEquals(0, result.statusCode)
        assertTrue(localStatePath.resolve("diff.txt").exists())
        assertTrue(
            PathUtils.fileContentEquals(
                localStatePath.resolve("hello.txt"),
                activeStatePath.resolve("hello.txt")
            )
        )
        assertTrue(
            PathUtils.fileContentEquals(
                localStatePath.resolve("diff.txt"),
                activeStatePath.resolve("diff.txt")
            )
        )
    }

// Shall be fixed: Race condition
//    @Test
//    fun testActivateWithExclude() = runTest {
//
//        val localIncludeFile = localStatePath.resolve("include.txt")
//        val localExcludeFile = localStatePath.resolve("exclude.md")
//        localIncludeFile.createFile()
//        localExcludeFile.createFile()
//
//        val result = command.test(listOf("-y", "-x", ".*\\.md", localStatePath.pathString))
//        val activeIncludeFile = activeStatePath.resolve("include.txt")
//        val activeExcludeFile = activeStatePath.resolve("exclude.md")
//
//        assertEquals(0, result.statusCode)
//        assertTrue(activeIncludeFile.exists())
//        assertTrue(activeExcludeFile.notExists())
//    }

//    @Test
//    fun testActivateWithInclude() = runTest {
//
//        val localIncludeFile = localStatePath.resolve("include.md")
//        val localExcludeFile = localStatePath.resolve("exclude.txt")
//        localIncludeFile.createFile()
//        localExcludeFile.createFile()
//
//        val result = command.test(listOf("-y", "-i", ".*\\.md", localStatePath.pathString))
//        val activeIncludeFile = activeStatePath.resolve("include.md")
//        val activeExcludeFile = activeStatePath.resolve("exclude.txt")
//
//        assertEquals(0, result.statusCode)
//        assertTrue(activeIncludeFile.exists())
//        assertTrue(activeExcludeFile.notExists())
//    }
}