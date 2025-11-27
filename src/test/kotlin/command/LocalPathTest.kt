package command

import com.an5on.command.LocalPath
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.config.Configuration
import com.an5on.config.GlobalConfiguration
import com.github.ajalt.clikt.command.test
import com.github.ajalt.clikt.testing.CliktCommandTestResult
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import kotlin.io.path.Path
import kotlin.io.path.pathString
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalPathTest {
    private val command = LocalPath

    @BeforeEach
    fun setup() {
        configuration = Configuration(
            GlobalConfiguration(
                activeStatePath = Path("src/test/resources/sample-state/state-1/active"),
                localStatePath = Path("src/test/resources/sample-state/state-1/local")
            )
        )
    }

    private val activeStatePath
        get() = configuration.global.activeStatePath
    private val localStatePath
        get() = configuration.global.localStatePath

    @Test
    fun testLocalPathWithoutArguments() = runTest {
        val result = command.test("")

        assertEquals(activeStatePath.pathString + '\n', result.stdout)
        assertEquals(0, result.statusCode)
    }

    @Test
    fun testLocalPathWithSyncedFiles() = runTest {
        var result: CliktCommandTestResult

        result = command.test(activeStatePath.resolve("hello.txt").pathString)
        assertEquals(localStatePath.resolve("hello.txt").pathString + '\n', result.stdout)
        assertEquals(0, result.statusCode)

        result = command.test(activeStatePath.resolve("audrey/test.txt").pathString)
        assertEquals(localStatePath.resolve("audrey/test.txt").pathString + '\n', result.stdout)
        assertEquals(0, result.statusCode)

        result = command.test(activeStatePath.resolve(".hidden-audrey/.dot.txt").pathString)
        assertEquals(localStatePath.resolve("punkt_hidden-audrey/punkt_dot.txt").pathString + '\n', result.stdout)
        assertEquals(0, result.statusCode)
    }
}