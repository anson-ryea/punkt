package command

import com.an5on.command.Sync
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.config.Configuration
import com.an5on.config.GlobalConfiguration
import com.github.ajalt.clikt.command.test
import kotlinx.coroutines.test.runTest
import org.apache.commons.io.file.PathUtils
import org.junit.jupiter.api.BeforeEach
import java.nio.file.Files.createDirectory
import java.nio.file.Files.deleteIfExists
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SyncTest {

    private val command = Sync

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
    fun testSyncWithoutArgument() = runTest {

        activeStatePath.resolve("hello.txt").toFile().writeText("A total different messagee")
        val activeFileDir = activeStatePath.resolve(".test")
        createDirectory(activeFileDir)
        val activeFile = activeFileDir.resolve("test.txt")
        activeFile.toFile().writeText("test message")

        val result = command.test("")

        assertEquals(0, result.statusCode)

        val localFileDir = localStatePath.resolve(configuration.global.dotReplacementPrefix + "test")
        val localFile = localFileDir.resolve("test.txt")
        assertTrue(localFileDir.exists())
        assertTrue(localFile.exists())
        assertTrue(PathUtils.fileContentEquals(localFile, activeFile))
        assertTrue(
            PathUtils.fileContentEquals(
                activeStatePath.resolve("hello.txt"),
                localStatePath.resolve("hello.txt")
            )
        )

        activeStatePath.resolve("hello.txt").toFile().writeText("")
        deleteIfExists(localFile)
        deleteIfExists(localFileDir)
        deleteIfExists(activeFile)
        deleteIfExists(activeFileDir)

    }
}