package command

import BaseTestWithTestConfiguration
import com.an5on.command.Ignored
import com.an5on.file.PunktIgnore
import com.github.ajalt.clikt.command.test
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.test.assertContentEquals

class IgnoredTest : BaseTestWithTestConfiguration() {

    private val command = Ignored

    private val punktIgnoreContent = listOf(
        "ignored.txt",
        "**/ignored_folder",
        "**/ignored_folder/**",
        "*.aux"
    )

    @BeforeEach
    fun setup() {
        PunktIgnore.ignoreFilePath = Path("src/test/resources/.punktignore")
    }

    @Test
    fun testIgnored() = runTest {
        val result = command.test("")
        assertContentEquals(punktIgnoreContent, result.stdout.lines().filterNot { it.isBlank() })
    }
}