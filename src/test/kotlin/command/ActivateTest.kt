package command

import com.an5on.command.Activate
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.config.Configuration
import com.an5on.config.GlobalConfiguration
import com.github.ajalt.clikt.command.test
import kotlinx.coroutines.test.runTest
import org.apache.commons.io.file.PathUtils
import org.junit.jupiter.api.BeforeEach
import java.io.File
import java.nio.file.Files.createDirectory
import kotlin.io.path.Path
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.pathString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ActivateTest {
    private val command = Activate

    @BeforeEach
    fun setup() {
        configuration = Configuration(
            GlobalConfiguration(
                activeStatePath = Path("src/test/resources/sample_state/state_1/active").toAbsolutePath(),
                localStatePath = Path("src/test/resources/sample_state/state_1/local").toAbsolutePath()
            )
        )
    }

    private val activeStatePath
        get() = configuration.global.activeStatePath
    private val localStatePath
        get() = configuration.global.localStatePath

    @Test
    fun testActivateInSampleState1WithoutArguments() = runTest {

        localStatePath.resolve("diff.txt").toFile().apply { writeText("hello") }
        localStatePath.resolve("hello.txt").toFile().apply { writeText("test") }

        val result = command.test("", stdin = "y")

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

    @Test
    fun testActivateInSampleState1WithNoRecursive() = runTest {

        val localDir = localStatePath.resolve("diff")
        createDirectory(localDir)
        localDir.resolve("diff.txt").toFile().apply { writeText("hello") }

        val result = command.test("--no-recursive", stdin = "y")
        val file = File(activeStatePath.resolve("diff").pathString, "diff.txt")

        assertEquals(0, result.statusCode)
        assertTrue(activeStatePath.resolve("diff").exists())
        assertFalse(file.exists())
        localDir.resolve("diff.txt").deleteIfExists()
        localStatePath.resolve("diff").deleteIfExists()
        activeStatePath.resolve("diff.txt").deleteIfExists()
        activeStatePath.resolve("diff").deleteIfExists()
    }

    @Test
    fun testActivateInSampleState1WithExclude() = runTest {

        val localDir = localStatePath.resolve("diff")
        createDirectory(localDir)
        localDir.resolve("diff.txt").toFile().apply { writeText("hello") }

        val result = command.test(listOf("-x", "/diff/diff.txt"), stdin = "y\n")
        val file = File(activeStatePath.resolve("diff").pathString, "diff.txt")

        assertEquals(0, result.statusCode)
        assertTrue(activeStatePath.resolve("diff").exists())
        assertFalse(file.exists())
        localDir.resolve("diff.txt").deleteIfExists()
        localStatePath.resolve("diff").deleteIfExists()
        activeStatePath.resolve("diff").deleteIfExists()
    }

    @Test
    fun testActivateInSampleState1WithInclude() = runTest {

        val localDir = localStatePath.resolve("diff")
        createDirectory(localDir)
        localDir.resolve("diff.txt").toFile().apply { writeText("hello") }
        localDir.resolve("hello.txt").toFile().apply { writeText("test") }

        val result = command.test("-i diff.txt", stdin = "y")
        val diffFile = File(activeStatePath.resolve("diff").pathString, "diff.txt")
        val helloFile = File(activeStatePath.resolve("diff").pathString, "hello.txt")

        assertEquals(0, result.statusCode)
        assertTrue(diffFile.exists())
        assertFalse(helloFile.exists())
        localDir.resolve("diff.txt").deleteIfExists()
        localDir.resolve("hello.txt").deleteIfExists()
        localStatePath.resolve("diff").deleteIfExists()
        activeStatePath.resolve("diff").deleteIfExists()
    }

}