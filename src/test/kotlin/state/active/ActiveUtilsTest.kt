package state.active

//import com.an5on.states.active.ActiveUtils.dotReplacementPrefix
import com.an5on.config.ActiveConfiguration
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.config.Configuration
import com.an5on.config.GlobalConfiguration
import com.an5on.file.FileUtils.contentEqualsActive
import com.an5on.file.FileUtils.existsInActive
import com.an5on.file.FileUtils.isLocal
import com.an5on.file.FileUtils.toActive
import com.an5on.system.SystemUtils
import org.junit.jupiter.api.Assertions.assertFalse
import java.io.File
import java.nio.file.Files
import kotlin.io.path.pathString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ActiveUtilsTest {


    val config = Configuration(
        GlobalConfiguration(
            localStatePath = SystemUtils.homePath.resolve(".local/share/punkt/test"),
            activeStatePath = SystemUtils.homePath.resolve(".local/share/punkt/test"),
            )
    )

    init {
        ActiveConfiguration.configuration = config
    }
//
//    @Test
//    fun test(){
//        println(config.global.activeStatePath)
//    }

    @Test
    fun pathToActiveWithActivePathForWin() {
        val subject = configuration.global.activeStatePath.resolve(".test\\active\\file.txt")

        assertEquals(subject, subject.toActive())
    }

//    @Test
//    fun pathToActiveWithRelativePathForWin() {
//        val subject = Path(configuration.global.dotReplacementPrefix + "test\\active\\file.txt")
//        val result = configuration.global.activeStatePath.resolve(".test\\active\\file.txt")
//
//        assertEquals(result,subject.toActive() )
//    }

    @Test
    fun pathToActiveWithLocalPathForWin() {
        val subject =
            configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix + "test\\active\\file.txt")
        val result = configuration.global.activeStatePath.resolve(".test\\active\\file.txt")

        assertEquals(result, subject.toActive())
    }

//    @Test
//    fun pathToActiveWithAbsoluteNonLocalPath() {
//
//        val subject = Path("C:\\ProgramFiles\\.test\\active\\file.txt")
//        val
//
//    }
    @Test
    fun fileToActiveWithPathForWin() {
        val subject = File(configuration.global.activeStatePath.resolve(".test\\active\\file.txt").pathString)

        assertEquals(subject, subject.toActive())
    }

    @Test
    fun pathToActiveWithActivePathForUnix() {
        val subject = configuration.global.activeStatePath.resolve(".test/active/file.txt")

        assertEquals(subject, subject)
    }

//    @Test
//    fun pathToActiveWithRelativePathForUnix() {
//        val subject = Path(configuration.global.dotReplacementPrefix + "test/active/file.txt")
//        val result = configuration.global.activeStatePath.resolve(".test/active/file.txt")
//
//        assertEquals(result,subject.toActive() )
//    }

    @Test
    fun pathToActiveWithLocalPathForUnix() {
        val subject =
            configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix + "test/active/file.txt")
        val result = configuration.global.activeStatePath.resolve(".test/active/file.txt")

        assertEquals(result, subject.toActive())
    }

//    @Test
//    fun pathToActiveWithAbsoluteNonLocalPath() {
//
//        val subject = Path("C:/ProgramFiles/.test/active/file.txt")
//        val
//
//    }
    @Test
    fun fileToActiveWithPathForUnix() {
        val subject = File(configuration.global.activeStatePath.resolve(".test/active/file.txt").pathString)

        assertEquals(subject, subject.toActive())
    }

    @Test
    fun fileExistsInActiveTest(){
        val tempDir = config.global.localStatePath.resolve(config.global.dotReplacementPrefix + "testDir")
        assertTrue(tempDir.isLocal())
        val activeDir = config.global.activeStatePath.resolve(".testDir")
//        println(tempDir)
//        println(activeDir)
//        println(activeDir.toActive())

        // Create sample test files
        Files.createDirectories(tempDir)
        Files.createDirectories(activeDir)
        // Create subdirectory for test files
        val testFileDir = tempDir.resolve("test")
        Files.createDirectories(testFileDir) // Create the 'test' subdirect
        // Now we can safely create the test file
        val testFile = testFileDir.resolve("test.txt").toFile().apply {
            writeText("Sample content") // Write to the test file
        }
        // Create the active file in the active directory
        val activeFileDir = activeDir.resolve("test")
        Files.createDirectories(activeFileDir) // Create the 'test' subdirectory for active file
        val activeFile = activeFileDir.resolve("test.txt").toFile().apply {
            writeText("Sample content") // Write to the active file
        }
        assertTrue(activeFile.existsInActive())
        assertTrue(testFile.existsInActive())
        // Clean up the temporary directories and files after tests
        Files.walk(tempDir).sorted(Comparator.reverseOrder()).forEach(Files::delete)
        Files.walk(activeDir).sorted(Comparator.reverseOrder()).forEach(Files::delete)
    }

    @Test
    fun pathExistsInActiveTest(){
        val tempDir = config.global.localStatePath.resolve(config.global.dotReplacementPrefix + "testDir")
        assertTrue(tempDir.isLocal())
        val activeDir = config.global.activeStatePath.resolve(".testDir")
//        println(tempDir)
//        println(activeDir)
//        println(activeDir.toActive())

        // Create sample test files
        Files.createDirectories(tempDir)
        Files.createDirectories(activeDir)
        // Create subdirectory for test files
        val testFileDir = tempDir.resolve("test")
        Files.createDirectories(testFileDir) // Create the 'test' subdirect
        // Now we can safely create the test file
        val testFile = testFileDir.resolve("test.txt").toFile().apply {
            writeText("Sample content") // Write to the test file
        }
        // Create the active file in the active directory
        val activeFileDir = activeDir.resolve("test")
        Files.createDirectories(activeFileDir) // Create the 'test' subdirectory for active file
        val activeFile = activeFileDir.resolve("test.txt").toFile().apply {
            writeText("Sample content") // Write to the active file
        }
        assertTrue(activeFile.toPath().existsInActive())
        assertTrue(testFile.toPath().existsInActive())
        // Clean up the temporary directories and files after tests
        Files.walk(tempDir).sorted(Comparator.reverseOrder()).forEach(Files::delete)
        Files.walk(activeDir).sorted(Comparator.reverseOrder()).forEach(Files::delete)
    }

    @Test
    fun fileContentEqualsActiveTest(){
        val tempDir = config.global.localStatePath.resolve(config.global.dotReplacementPrefix + "testDir")
//        assertTrue(tempDir.isLocal())
        val activeDir = config.global.activeStatePath.resolve(".testDir")
//        println(tempDir)
//        println(activeDir)
//        println(activeDir.toActive())

        // Create sample test files
        Files.createDirectories(tempDir)
        Files.createDirectories(activeDir)
        // Create subdirectory for test files
        val testFileDir = tempDir.resolve("test")
        Files.createDirectories(testFileDir) // Create the 'test' subdirect
        // Now we can safely create the test file
        val testFile = testFileDir.resolve("test.txt").toFile().apply {
            writeText("Sample content") // Write to the test file
        }
        // Create the active file in the active directory
        val activeFileDir = activeDir.resolve("test")
        Files.createDirectories(activeFileDir) // Create the 'test' subdirectory for active file
        val activeFile = activeFileDir.resolve("test.txt").toFile().apply {
            writeText("Sample content") // Write to the active file
        }
        assertTrue(testFile.contentEqualsActive())

        // Modify the active file to break equality
        activeFile.writeText("Different content")

        // Now, check that the content equality fails
        assertFalse(testFile.contentEqualsActive(), "Contents should not be equal after modification")

        // Clean up the temporary directories and files after tests
        Files.walk(tempDir).sorted(Comparator.reverseOrder()).forEach(Files::delete)
        Files.walk(activeDir).sorted(Comparator.reverseOrder()).forEach(Files::delete)
    }

}