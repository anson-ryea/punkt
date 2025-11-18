package file.filter

import com.an5on.config.ActiveConfiguration
import com.an5on.config.Configuration
import com.an5on.config.GlobalConfiguration
import com.an5on.file.filter.ActiveEqualsLocalFileFilter
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertThrows
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

class ActiveEqualsLocalFileFilterTest {

    val config = Configuration(
        GlobalConfiguration(
            localStatePath = Paths.get("test").toAbsolutePath().resolve("local"),
            activeStatePath = Paths.get("test").toAbsolutePath().resolve("active"),
        )
    )



    init {
        ActiveConfiguration.configuration = config
    }

    val localDir: Path = config.global.localStatePath.resolve(config.global.dotReplacementPrefix + "testDir")
    val activeDir: Path = config.global.activeStatePath.resolve(".testDir")
    val testFileDir = localDir.resolve("test")
    val testFile = testFileDir.resolve("test.txt")
    val activeFileDir = activeDir.resolve("test")
    val activeFile = activeFileDir.resolve("test.txt")

    @BeforeEach
    fun setUp() {

        // Create sample test files
        Files.createDirectories(localDir)
        Files.createDirectories(activeDir)
        // Create subdirectory for test files

        Files.createDirectories(testFileDir) // Create the 'test' subdirect
        // Now we can safely create the test file
        testFile.toFile().apply {
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
        assertTrue(ActiveEqualsLocalFileFilter.accept(testFile.toFile()))

        testFile.toFile().apply {
            writeText("Different content")
        }

        assertFalse(ActiveEqualsLocalFileFilter.accept(testFile.toFile()))
    }

    @Test
    fun acceptWithActiveFile() {
        assertTrue(ActiveEqualsLocalFileFilter.accept(activeFile.toFile()))

        testFile.toFile().apply {
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
        assertFalse( ActiveEqualsLocalFileFilter.accept(p, attrs))
    }


}