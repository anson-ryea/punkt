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
import kotlin.test.assertContentEquals
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

    @Test
    fun testListInSampleState1WithoutArguments() = runTest {
        val result = command.test("")

        val expected = listOf(
            "/audrey",
            "/audrey/test.txt",
            "/hello.txt",
            "/.hidden_audrey",
            "/.hidden_audrey/.dot.txt"
        ).map { activeStatePath.pathString + it }

        assertEquals(0, result.statusCode)
        assertContentEquals(expected, result.stdout.dropLastWhile { it == '\n' }.lines())
    }

    @Test
    fun testListInSampleState1WithPathStyles() = runTest {
        val relativeActivePaths = listOf(
            "audrey",
            "audrey/test.txt",
            "hello.txt",
            ".hidden_audrey",
            ".hidden_audrey/.dot.txt"
        ).map { Path(it) }

        val relativeLocalPaths = listOf(
            "audrey",
            "audrey/test.txt",
            "hello.txt",
            "punkt_hidden_audrey",
            "punkt_hidden_audrey/punkt_dot.txt"
        ).map { Path(it) }

        // Test for absolute path style
        var result = command.test("--path-style absolute")
        var expected = relativeActivePaths.map { activeStatePath.resolve(it).pathString }
        assertEquals(0, result.statusCode)
        assertContentEquals(expected, result.stdout.dropLastWhile { it == '\n' }.lines())

        // Test for relative path style
        result = command.test("--path-style relative")
        expected = relativeActivePaths.map { it.pathString }
        assertEquals(0, result.statusCode)
        assertContentEquals(expected, result.stdout.dropLastWhile { it == '\n' }.lines())

        // Test for local-absolute path style
        result = command.test("--path-style local-absolute")
        expected = relativeLocalPaths.map { localStatePath.resolve(it).pathString }

        assertEquals(0, result.statusCode)
        assertContentEquals(expected, result.stdout.dropLastWhile { it == '\n' }.lines())

        // Test for local-relative path style
        result = command.test("--path-style local-relative")
        expected = relativeLocalPaths.map { it.pathString }
        assertEquals(0, result.statusCode)
        assertContentEquals(expected, result.stdout.dropLastWhile { it == '\n' }.lines())
    }
}