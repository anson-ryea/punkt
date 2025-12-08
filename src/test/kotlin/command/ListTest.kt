package command

import com.an5on.command.List
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.config.Configuration
import com.an5on.config.GlobalConfiguration
import com.github.ajalt.clikt.command.test
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import kotlin.io.path.Path
import kotlin.io.path.pathString
import kotlin.test.Test
import kotlin.test.assertEquals

class ListTest {
    private val command = List

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

    private val relativeActivePaths = listOf(
        "audrey",
        "audrey/test.txt",
        "hello.txt",
        ".hidden_audrey",
        ".hidden_audrey/.dot.txt"
    ).map { Path(it) }

    private val relativeLocalPaths = listOf(
        "audrey",
        "audrey/test.txt",
        "hello.txt",
        "punkt_hidden_audrey",
        "punkt_hidden_audrey/punkt_dot.txt"
    ).map { Path(it) }

    @Test
    fun testListInSampleState1WithoutArguments() = runTest {
        val result = command.test("")

        val expected = relativeActivePaths.joinToString(
            separator = "\n",
            postfix = "\n"
        ) { activeStatePath.resolve(it).pathString }

        assertEquals(0, result.statusCode)
        assertEquals(expected, result.stdout)
    }

    @Test
    fun testListInSampleState1WithPathStyles() = runTest {
        // Test for absolute path style
        var result = command.test("--path-style absolute")
        var expected = relativeActivePaths.joinToString(
            separator = "\n",
            postfix = "\n"
        ) { activeStatePath.resolve(it).pathString }
        assertEquals(0, result.statusCode)
        assertEquals(expected, result.stdout)

        // Test for relative path style
        result = command.test("--path-style relative")
        expected = relativeActivePaths.joinToString(separator = "\n", postfix = "\n")
        assertEquals(0, result.statusCode)
        assertEquals(expected, result.stdout)

        // Test for local-absolute path style
        result = command.test("--path-style local-absolute")
        expected =
            relativeLocalPaths.joinToString(separator = "\n", postfix = "\n") { localStatePath.resolve(it).pathString }

        assertEquals(0, result.statusCode)
        assertEquals(expected, result.stdout)

        // Test for local-relative path style
        result = command.test("--path-style local-relative")
        expected = relativeLocalPaths.joinToString(separator = "\n", postfix = "\n")
        assertEquals(0, result.statusCode)
        assertEquals(expected, result.stdout)
    }

    @Test
    fun testListInSampleState1WithSyncedPath() = runTest {
        // Test for synced single file
        var result = command.test(activeStatePath.resolve("hello.txt").pathString.replace('\\', '/'))
        var expected = activeStatePath.resolve("hello.txt").pathString + '\n'
        assertEquals(0, result.statusCode)
        assertEquals(expected, result.stdout)

        // Test for synced directory
        result = command.test(activeStatePath.resolve("audrey").pathString.replace('\\', '/'))
        expected =
            relativeActivePaths.filter { it.startsWith("audrey") }.joinToString(separator = "\n", postfix = "\n") {
                activeStatePath.resolve(it).pathString
            }
        assertEquals(0, result.statusCode)
        assertEquals(expected, result.stdout)
    }

    @Test
    fun testListInSampleState1WithUnsyncedPath() = runTest {
        // Test for unsynced single file
        var result = command.test(activeStatePath.resolve("non_existent.txt").pathString.replace('\\', '/'))
        val expected = ""
        assertEquals(expected, result.stdout)

        // Test for unsynced directory
        result = command.test(activeStatePath.resolve("non_existent_dir").pathString.replace('\\', '/'))
        assertEquals(expected, result.stdout)
    }
}