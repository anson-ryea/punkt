package file.filter

import BaseTestWithTestConfiguration
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.config.Configuration
import com.an5on.config.GlobalConfiguration
import com.an5on.file.filter.ActiveEqualsLocalFileFilter
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ActiveEqualsLocalFileFilterTest : BaseTestWithTestConfiguration() {


    companion object {
        @JvmStatic
        @BeforeAll
        fun setup() {
            val systemTempDir = Paths.get(System.getProperty("java.io.tmpdir"))
            configuration = Configuration(
                GlobalConfiguration(
                    activeStatePath = systemTempDir.resolve("test-active-state"),
                    localStatePath = systemTempDir.resolve("test-local-state")
                )
            )
        }
    }

    val localDir: Path
        get() =
            configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix + "testDir")
    val activeDir: Path
        get() = configuration.global.activeStatePath.resolve(".testDir")
    val localFileDir: Path
        get() = localDir.resolve("test")
    val localFile: Path
        get() = localFileDir.resolve("test.txt")
    val activeFileDir: Path
        get() = activeDir.resolve("test")
    val activeFile: Path
        get() = activeFileDir.resolve("test.txt")

    @BeforeEach
    fun setUp() {

        // Create sample test files
        Files.createDirectories(localDir)
        Files.createDirectories(activeDir)
        // Create subdirectory for test files

        Files.createDirectories(localFileDir) // Create the 'test' subdirect
        // Now we can safely create the test file
        localFile.toFile().apply {
            writeText("Sample content") // Write to the test file
        }
        // Create the active file in the active directory

        Files.createDirectories(activeFileDir) // Create the 'test' subdirectory for active file
        activeFile.toFile().apply {
            writeText("Sample content") // Write to the active file
        }
    }

    @AfterEach
    fun tearDown() {
        // Clean up the temporary directories and files after tests
        Files.walk(localDir).sorted(Comparator.reverseOrder()).forEach(Files::delete)
        Files.walk(activeDir).sorted(Comparator.reverseOrder()).forEach(Files::delete)
    }

    @Test
    fun acceptWithNullPath() {
        val ex = assertThrows(IllegalArgumentException::class.java) {
            ActiveEqualsLocalFileFilter.accept(null)
        }

        assertEquals("ActiveEqualsLocalFileFilter only accepts non-directory files.", ex.message)
    }

    @Test
    fun acceptWithDirectoryPath(@TempDir dir: File) {

        val ex = assertThrows(IllegalArgumentException::class.java) {
            ActiveEqualsLocalFileFilter.accept(dir)
        }
        assertEquals("ActiveEqualsLocalFileFilter only accepts non-directory files.", ex.message)
    }

    @Test
    fun acceptWithLocalFile() {
        assertTrue(ActiveEqualsLocalFileFilter.accept(localFile.toFile()))

        localFile.toFile().apply {
            writeText("Different content")
        }

        assertFalse(ActiveEqualsLocalFileFilter.accept(localFile.toFile()))
    }

    @Test
    fun acceptWithActiveFile() {
        assertTrue(ActiveEqualsLocalFileFilter.accept(activeFile.toFile()))

        localFile.toFile().apply {
            writeText("Different content")
        }

        assertFalse(ActiveEqualsLocalFileFilter.accept(activeFile.toFile()))
    }

    @Test
    fun acceptWithNullParentDirectoryAndNullFileName(@TempDir dir: File) {
        assertFalse(ActiveEqualsLocalFileFilter.accept(dir, null))
        assertFalse(ActiveEqualsLocalFileFilter.accept(null, "Non-existentFile.txt"))
        val p: File? = null
        val attrs: String? = null
        assertFalse(ActiveEqualsLocalFileFilter.accept(p, attrs))
    }


}