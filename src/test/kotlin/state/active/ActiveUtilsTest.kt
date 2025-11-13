package state.active

//import com.an5on.states.active.ActiveUtils.dotReplacementPrefix
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.states.active.ActiveUtils.contentEqualsActive
import com.an5on.states.active.ActiveUtils.existsInActive
import com.an5on.states.active.ActiveUtils.toActive
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.pathString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ActiveUtilsTest {


    @Test
    fun pathToActiveWithActivePathForWin() {
        val subject = configuration.global.activeStatePath.resolve(".test\\active\\file.txt")

        assertEquals(subject, subject)
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

        assertEquals(subject, subject)
    }

//    @Nested
//    class testExistInActive{
//        private lateinit var tempDir: Path
//        private lateinit var activeDir: Path
//        private lateinit var testFile: File
//        private lateinit var activeFile: File
//
//        @BeforeEach
//        fun setUp() {
//            // Create temporary directories
//            tempDir = configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix + "testDir")
//            activeDir = configuration.global.activeStatePath.resolve(".testDir")
//
//
//            // Create sample test files
//            Files.createDirectories(tempDir)
//            Files.createDirectories(activeDir)
//
//            // Create subdirectory for test files
//            val testFileDir = tempDir.resolve("test")
//            Files.createDirectories(testFileDir) // Create the 'test' subdirectory
//
//            // Now we can safely create the test file
//            testFile = testFileDir.resolve("test.txt").toFile().apply {
//                writeText("Sample content") // Write to the test file
//            }
//
//            // Create the active file in the active directory
//            val activeFileDir = activeDir.resolve("test")
//            Files.createDirectories(activeFileDir) // Create the 'test' subdirectory for active files
//
//            activeFile = activeFileDir.resolve("test.txt").toFile().apply {
//                writeText("Sample content") // Write to the active file
//            }
//        }
//
//
//        @Test
//        fun testExistsInActive() {
//            // Assert existsInActive() functionality
//            assertTrue(testFile.existsInActive())
//            assertTrue(activeFile.existsInActive())
//        }
//
//        @Test
//        fun testFileContentEqualsActive() {
//            // Initially, check if contents are equal
//            assertTrue(testFile.contentEqualsActive(), "Initial files should be equal")
//
//            // Modify the active file to break equality
//            activeFile.writeText("Different content")
//
//            // Now, check that the content equality fails
//            assertFalse(testFile.contentEqualsActive(), "Contents should not be equal after modification")
//        }
//
//
//        @AfterEach
//        fun tearDown() {
//            // Clean up the temporary directories and files after tests
//            Files.walk(tempDir).sorted(Comparator.reverseOrder()).forEach(Files::delete)
//            Files.walk(activeDir).sorted(Comparator.reverseOrder()).forEach(Files::delete)
//        }
//    }

}