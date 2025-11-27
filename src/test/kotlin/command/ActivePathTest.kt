package command

import com.an5on.command.ActivePath
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.config.Configuration
import com.an5on.config.GlobalConfiguration
import com.github.ajalt.clikt.command.test
import com.github.ajalt.clikt.testing.CliktCommandTestResult
import org.junit.jupiter.api.Test
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import kotlin.io.path.Path
import kotlin.io.path.pathString
import kotlin.test.assertEquals

class ActivePathTest {
    private val command = ActivePath

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
    fun testActivePathWithoutArguments() = runTest {
        val result = command.test("")

        assertEquals(configuration.global.activeStatePath.pathString + '\n', result.stdout)
        assertEquals(0, result.statusCode)
    }

    @Test
    fun testLocalPathWithSyncedFiles() = runTest {
        var result: CliktCommandTestResult

        result = command.test(localStatePath.resolve("hello.txt").pathString)
        assertEquals(activeStatePath.resolve("hello.txt").pathString + '\n', result.stdout)
        assertEquals(0, result.statusCode)

        result = command.test(localStatePath.resolve("audrey/test.txt").pathString)
        assertEquals(activeStatePath.resolve("audrey/test.txt").pathString + '\n', result.stdout)
        assertEquals(0, result.statusCode)

        result = command.test(localStatePath.resolve("punkt_hidden-audrey/punkt_dot.txt").pathString)
        assertEquals(activeStatePath.resolve(".hidden-audrey/.dot.txt").pathString + '\n', result.stdout)
        assertEquals(0, result.statusCode)
    }
}