package command

import com.an5on.command.List
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.config.Configuration
import com.an5on.config.GlobalConfiguration
import com.github.ajalt.clikt.command.test
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.io.path.Path
import kotlin.io.path.pathString
import kotlin.test.Test
import kotlin.test.assertEquals

class ListTest {
    private val command = List

    companion object {
        @JvmStatic
        @BeforeAll
        fun setup() {
            configuration = Configuration(
                GlobalConfiguration(
                    activeStatePath = Path("src/test/resources/sample_state/state_1/active").toAbsolutePath(),
                    localStatePath = Path("src/test/resources/sample_state/state_1/local").toAbsolutePath()
                )
            )
        }
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
    fun testListInSampleState1WithAbsolutePathStyle() = runTest {
        val result = command.test("--path-style absolute")
        val expected = relativeActivePaths.joinToString(
            separator = "\n",
            postfix = "\n"
        ) { activeStatePath.resolve(it).pathString }
        assertEquals(0, result.statusCode)
        assertEquals(expected, result.stdout)
    }

    @Test
    fun testListInSampleState1WithRelativePathStyle() = runTest {
        val result = command.test("--path-style relative")
        val expected = relativeActivePaths.joinToString(separator = "\n", postfix = "\n")
        assertEquals(0, result.statusCode)
        assertEquals(expected, result.stdout)
    }

    @Test
    fun testListInSampleState1WithLocalAbsolutePathStyle() = runTest {
        val result = command.test("--path-style local-absolute")
        val expected = relativeLocalPaths
            .joinToString(separator = "\n", postfix = "\n") {
                localStatePath.resolve(it).pathString
            }

        assertEquals(0, result.statusCode)
        assertEquals(expected, result.stdout)
    }

    @Test
    fun testListInSampleState1WithLocalRelativePathStyle() = runTest {
        val result = command.test("--path-style local-relative")
        val expected = relativeLocalPaths.joinToString(separator = "\n", postfix = "\n")
        assertEquals(0, result.statusCode)
        assertEquals(expected, result.stdout)
    }

    @ParameterizedTest
    @ValueSource(strings = ["hello.txt", "audrey"])
    fun testListInSampleState1WithSyncedPath(pathStringRelativeToActive: String) = runTest {
        val result = command.test(activeStatePath.resolve(pathStringRelativeToActive).pathString.replace('\\', '/'))
        val expected =
            relativeActivePaths.filter { it.startsWith(pathStringRelativeToActive) }
                .joinToString(separator = "\n", postfix = "\n") {
                    activeStatePath.resolve(it).pathString
                }
        assertEquals(0, result.statusCode)
        assertEquals(expected, result.stdout)
    }

    @ParameterizedTest
    @ValueSource(strings = ["non_existent.txt", "non_existent_dir"])
    fun testListInSampleState1WithUnsyncedPath(pathStringRelativeToActive: String) = runTest {
        val result = command.test(activeStatePath.resolve(pathStringRelativeToActive).pathString.replace('\\', '/'))
        val expected = ""
        assertEquals(expected, result.stdout)
    }
}