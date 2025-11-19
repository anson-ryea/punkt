package file.filter

import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.config.Configuration
import com.an5on.config.GlobalConfiguration
import com.an5on.file.filter.ExistsInBothActiveAndLocalFileFilter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ExistsInBothActiveAndLocalFileFilterTest {


    val testConfiguration = Configuration(
        GlobalConfiguration(
            localStatePath = Files.createTempDirectory("test-share").toAbsolutePath().resolve("local"),
            activeStatePath = Files.createTempDirectory("test-home").resolve("active"),
        )
    )

    init {
        configuration = testConfiguration
    }

    val localDir: Path = testConfiguration.global.localStatePath.resolve(testConfiguration.global.dotReplacementPrefix + "testDir")
    val activeDir: Path = testConfiguration.global.activeStatePath.resolve(".testDir")
    val localFileDir = localDir.resolve("test")
    val localFile = localFileDir.resolve("test.txt")
    val activeFileDir = activeDir.resolve("test")
    val activeFile = activeFileDir.resolve("test.txt")

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

    @Test
    fun acceptWithLocalFiles() {
        assertTrue(ExistsInBothActiveAndLocalFileFilter.accept(localFile.toFile()))
        Files.walk(activeDir).sorted(Comparator.reverseOrder()).forEach(Files::delete)
        // Clean up the temporary directories and files after tests
        assertFalse(ExistsInBothActiveAndLocalFileFilter.accept(localFile.toFile()))
        Files.walk(localDir).sorted(Comparator.reverseOrder()).forEach(Files::delete)
    }

    @Test
    fun acceptWithActiveFiles() {
        assertTrue(ExistsInBothActiveAndLocalFileFilter.accept(activeFile.toFile()))
        Files.walk(localDir).sorted(Comparator.reverseOrder()).forEach(Files::delete)
        // Clean up the temporary directories and files after tests
        assertFalse(ExistsInBothActiveAndLocalFileFilter.accept(activeFile.toFile()))
        Files.walk(activeDir).sorted(Comparator.reverseOrder()).forEach(Files::delete)
    }

    @Test
    fun acceptWithNeitherActiveNorLocalFile(@TempDir tempDir: File) {

        val testFile = tempDir.resolve("test.txt")
        assertFalse(ExistsInBothActiveAndLocalFileFilter.accept(testFile))

        Files.walk(localDir).sorted(Comparator.reverseOrder()).forEach(Files::delete)
        Files.walk(activeDir).sorted(Comparator.reverseOrder()).forEach(Files::delete)

    }

    @Test
    fun acceptWithNullParentDirectoryAndNullFileName(@TempDir dir: File) {
        assertFalse(ExistsInBothActiveAndLocalFileFilter.accept(dir, null))
        assertFalse(ExistsInBothActiveAndLocalFileFilter.accept(null, "Non-existentFile.txt"))
        val p: File? = null
        val attrs: String? = null
        assertFalse( ExistsInBothActiveAndLocalFileFilter.accept(p, attrs))
    }

}