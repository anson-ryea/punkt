package file

import BaseTestWithTestConfiguration
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.file.FileUtils.contentEqualsActive
import com.an5on.file.FileUtils.contentEqualsLocal
import com.an5on.file.FileUtils.existsInActive
import com.an5on.file.FileUtils.existsInLocal
import com.an5on.file.FileUtils.expandTildeWithHomePathname
import com.an5on.file.FileUtils.isLocal
import com.an5on.file.FileUtils.toActive
import com.an5on.file.FileUtils.toLocal
import com.an5on.file.FileUtils.toStringInPathStyle
import com.an5on.system.SystemUtils.homePath
import com.an5on.type.PathStyle
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.pathString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FileUtilsTest : BaseTestWithTestConfiguration() {

    /*__________________________________________Tests For File__________________________________________________________________*/
    @Test
    fun stringExpandTildeWithHomePathNameForWin() {
        val subject = "~\\.test\\file\\test.txt"
        val result = homePath.pathString + "\\.test\\file\\test.txt"

        assertEquals(result, subject.expandTildeWithHomePathname())
    }

    @Test
    fun pathToStringInPathStyleAbsoluteWithAbsoluteLocalPathForWin() {
        val subject =
            configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix + "test\\file\\test.txt")
        val result = configuration.global.activeStatePath.resolve(".test\\file\\test.txt").pathString

        assertEquals(result, subject.toStringInPathStyle(PathStyle.ABSOLUTE))
    }

    @Test
    fun pathToStringInPathStyleRelativeWithAbsoluteLocalPathForWin() {
        val subject =
            configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix + "test\\file\\test.txt")
        val result = Path(".test\\file\\test.txt").pathString

        assertEquals(result, subject.toStringInPathStyle(PathStyle.RELATIVE))
    }

    @Test
    fun pathToStringInPathStyleLocalAbsoluteWithAbsoluteActivePathForWin() {
        val subject = configuration.global.activeStatePath.resolve(".test\\file\\test.txt")
        val result =
            configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix + "test\\file\\test.txt").pathString

        assertEquals(result, subject.toStringInPathStyle(PathStyle.LOCAL_ABSOLUTE))
    }

    @Test
    fun pathToStringInPathStyleLocalRelativeWithAbsoluteActivePathForWin() {
        val subject = configuration.global.activeStatePath.resolve(".test\\file\\test.txt")
        val result = Path(configuration.global.dotReplacementPrefix + "test\\file\\test.txt").pathString

        assertEquals(result, subject.toStringInPathStyle(PathStyle.LOCAL_RELATIVE))
    }

    @Test
    fun collectionPathToStringPathStyleWithListOfPathsForWin() {
        val subject = listOf(
            configuration.global.activeStatePath.resolve(".test\\file\\test1.txt"),
            configuration.global.activeStatePath.resolve(".test\\file\\test2.txt"),
            configuration.global.activeStatePath.resolve(".test\\file\\test3.txt")
        )
        val result = Path(configuration.global.dotReplacementPrefix + "test\\file\\test1.txt").pathString + "\n" + Path(
            configuration.global.dotReplacementPrefix + "test\\file\\test2.txt"
        ).pathString + "\n" + Path(configuration.global.dotReplacementPrefix + "test\\file\\test3.txt").pathString

        assertEquals(result, subject.toStringInPathStyle(PathStyle.LOCAL_RELATIVE))

    }

    @Test
    @EnabledOnOs(OS.MAC, OS.LINUX)
    fun stringExpandTildeWithHomePathNameForUnix() {
        val subject = "~/.test/file/test.txt"
        val result = homePath.pathString + "/.test/file/test.txt"

        assertEquals(result, subject.expandTildeWithHomePathname())
    }

    @Test
    fun pathToStringInPathStyleAbsoluteWithAbsoluteLocalPathForUnix() {
        val subject =
            configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix + "test/file/test.txt")
        val result = configuration.global.activeStatePath.resolve(".test/file/test.txt").pathString

        assertEquals(result, subject.toStringInPathStyle(PathStyle.ABSOLUTE))
    }

    @Test
    fun pathToStringInPathStyleRelativeWithAbsoluteLocalPathForUnix() {
        val subject =
            configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix + "test/file/test.txt")
        val result = Path(".test/file/test.txt").pathString

        assertEquals(result, subject.toStringInPathStyle(PathStyle.RELATIVE))
    }

    @Test
    fun pathToStringInPathStyleLocalAbsoluteWithAbsoluteActivePathForUnix() {
        val subject = configuration.global.activeStatePath.resolve(".test/file/test.txt")
        val result =
            configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix + "test/file/test.txt").pathString

        assertEquals(result, subject.toStringInPathStyle(PathStyle.LOCAL_ABSOLUTE))
    }

    @Test
    fun pathToStringInPathStyleLocalRelativeWithAbsoluteActivePathForUnix() {
        val subject = configuration.global.activeStatePath.resolve(".test/file/test.txt")
        val result = Path(configuration.global.dotReplacementPrefix + "test/file/test.txt").pathString

        assertEquals(result, subject.toStringInPathStyle(PathStyle.LOCAL_RELATIVE))
    }

    @Test
    @EnabledOnOs(OS.MAC, OS.LINUX)
    fun pathToStringInPathStyleLocalAbsoluteWithAbsolutePathForUnix() {
        val subject = Path("/.test/file/test.txt")
        val result = Path("/" + configuration.global.dotReplacementPrefix + "test/file/test.txt").pathString

        assertEquals(result, subject.toStringInPathStyle(PathStyle.LOCAL_ABSOLUTE))
    }

    @Test
    fun collectionPathToStringPathStyleWithListOfPathsForUnix() {
        val subject = listOf(
            configuration.global.activeStatePath.resolve(".test/file/test1.txt"),
            configuration.global.activeStatePath.resolve(".test/file/test2.txt"),
            configuration.global.activeStatePath.resolve(".test/file/test3.txt")
        )
        val result = Path(configuration.global.dotReplacementPrefix + "test/file/test1.txt").pathString + "\n" + Path(
            configuration.global.dotReplacementPrefix + "test/file/test2.txt"
        ).pathString + "\n" + Path(configuration.global.dotReplacementPrefix + "test/file/test3.txt").pathString

        assertEquals(result, subject.toStringInPathStyle(PathStyle.LOCAL_RELATIVE))
    }

    /*__________________________________________________________________________________________________________________________*/

    /*__________________________________________Tests For Local__________________________________________________________________*/

    @Test
    fun pathToLocalWithPathInHomeForWin() {

        val subject = configuration.global.activeStatePath.resolve(".test\\file.txt")
        val result =
            configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix + "test\\file.txt")

        assertEquals(subject.toLocal(), result)
    }

    @Test
    fun pathToLocalWithRelativePathForWin() {

        val subject = Path(".test\\file.txt")
        val result =
            configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix + "test\\file.txt")

        assertEquals(subject.toLocal(), result)
    }

    @Test
    fun pathToLocalWithLocalPathForWin() {

        val subject = configuration.global.activeStatePath.resolve(".test\\file.txt").toLocal()

        assertEquals(subject, subject)
    }

    @Test
    fun fileToLocalWithPathInHomeForWin() {

        val subject = File(configuration.global.activeStatePath.resolve(".test\\file.txt").pathString)
        val result =
            configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix + "test\\file.txt")
                .toFile()
        assertEquals(subject.toLocal(), result)
    }

    @Test
    fun fileToLocalWithNotInHomePathForWin() {

        val subject = File(".test\\file.txt")
        val result =
            configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix + "test\\file.txt")
                .toFile()
        assertEquals(subject.toLocal(), result)
    }

    @Test
    fun pathIsLocalWithPathInHomeForWin() {

        val subject = configuration.global.activeStatePath.resolve(".test\\file.txt")

        kotlin.test.assertFalse(subject.isLocal())
    }

    @Test
    fun pathIsLocalWithRelativePathForWin() {

        val subject = Path(".test\\file.txt")

        kotlin.test.assertFalse(subject.isLocal())
    }

    @Test
    fun pathIsLocalWithNotInHomePathForWin() {

        val subject = Path("C:\\ProgramFiles\\.test\\file.txt")

        kotlin.test.assertFalse(subject.isLocal())
    }

    @Test
    fun pathIsLocalWithLocalPathForWin() {

        val subject = configuration.global.activeStatePath.resolve(".test\\file.txt").toLocal()

        Assertions.assertTrue(subject.isLocal())
    }

    @Test
    fun fileIsLocalWithLocalPathForWin() {

        val subject = configuration.global.activeStatePath.resolve(".test\\file.txt").toLocal().toFile()

        Assertions.assertTrue(subject.isLocal())
    }

    @Test
    fun fileIsLocalWithNonLocalPathForWin() {

        val subject = Path(".test\\file.txt").toFile()

        kotlin.test.assertFalse(subject.isLocal())
    }

    @Test
    fun pathToLocalWithPathInHomeForUnix() {

        val subject = configuration.global.activeStatePath.resolve(".test/file.txt")
        val result =
            configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix + "test/file.txt")

        assertEquals(subject.toLocal(), result)
    }

    @Test
    fun pathToLocalWithLocalPathForUnix() {

        val subject = configuration.global.activeStatePath.resolve(".test/file.txt").toLocal()

        assertEquals(subject, subject)
    }

    @Test
    fun pathToLocalWithRelativePathForUnix() {
        val subject = Path(".test/file.txt")
        val result =
            configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix + "test/file.txt")

        assertEquals(subject.toLocal(), result)
    }

    @Test
    @EnabledOnOs(OS.MAC, OS.LINUX)
    fun pathToLocalWithNotInHomePathForUnix() {

        val subject = Path("/ProgramFiles/.test/file.txt")
        val result = Path("/ProgramFiles/" + configuration.global.dotReplacementPrefix + "test/file.txt")

        assertEquals(subject.toLocal(), result)
    }

    @Test
    fun fileToLocalWithPathInHomeForUnix() {

        val subject = File(configuration.global.activeStatePath.resolve(".test/file.txt").pathString)
        val result =
            configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix + "test/file.txt")
                .toFile()
        assertEquals(subject.toLocal(), result)
    }

    @Test
    fun fileToLocalWithNotInHomePathForUnix() {

        val subject = File(".test/file.txt")
        val result =
            configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix + "test/file.txt")
                .toFile()
        assertEquals(subject.toLocal(), result)
    }

    @Test
    @EnabledOnOs(OS.MAC, OS.LINUX)
    fun fileToLocalWithRelativePathForUnix() {

        val subject = File("/ProgramFiles/.test/file.txt")
        val result =
            Path("/ProgramFiles/" + configuration.global.dotReplacementPrefix + "test/file.txt").toFile()
        assertEquals(subject.toLocal(), result)
    }

    @Test
    fun pathIsLocalWithPathInHomeForUnix() {

        val subject = configuration.global.activeStatePath.resolve(".test/file.txt")

        kotlin.test.assertFalse(subject.isLocal())
    }

    @Test
    fun pathIsLocalWithRelativePathForUnix() {

        val subject = Path(".test/file.txt")

        kotlin.test.assertFalse(subject.isLocal())
    }

    @Test
    fun pathIsLocalWithNotInHomePathForUnix() {

        val subject = Path("/ProgramFiles/.test/file.txt")

        kotlin.test.assertFalse(subject.isLocal())
    }

    @Test
    fun pathIsLocalWithLocalPathForUnix() {

        val subject = configuration.global.activeStatePath.resolve(".test/file.txt").toLocal()

        Assertions.assertTrue(subject.isLocal())
    }

    @Test
    fun fileIsLocalWithLocalPathForUnix() {

        val subject = configuration.global.activeStatePath.resolve(".test/file.txt").toLocal().toFile()

        Assertions.assertTrue(subject.isLocal())
    }

    @Test
    fun fileIsLocalWithNonLocalPathForUnix() {

        val subject = Path(".test/file.txt").toFile()

        kotlin.test.assertFalse(subject.isLocal())
    }

    @Test
    fun fileExistsInLocalTest() {
        val localDir =
            configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix + "testDir")
        assertTrue(localDir.isLocal())
        val activeDir = configuration.global.activeStatePath.resolve(".testDir")

        // Create sample test files
        Files.createDirectories(localDir)
        Files.createDirectories(activeDir)
        // Create subdirectory for test files
        val testFileDir = localDir.resolve("test")
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
        assertTrue(activeFile.existsInLocal())
        assertTrue(testFile.existsInLocal())
        // Clean up the temporary directories and files after tests
        Files.walk(localDir).sorted(Comparator.reverseOrder()).forEach(Files::delete)
        Files.walk(activeDir).sorted(Comparator.reverseOrder()).forEach(Files::delete)
    }

    @Test
    fun pathExistsInLocalTest() {
        val localDir =
            configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix + "testDir")
        assertTrue(localDir.isLocal())
        val activeDir = configuration.global.activeStatePath.resolve(".testDir")

        // Create sample test files
        Files.createDirectories(localDir)
        Files.createDirectories(activeDir)
        // Create subdirectory for test files
        val testFileDir = localDir.resolve("test")
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

        assertTrue(activeFile.toPath().existsInLocal())
        assertTrue(testFile.toPath().existsInLocal())
        // Clean up the temporary directories and files after tests
        Files.walk(localDir).sorted(Comparator.reverseOrder()).forEach(Files::delete)
        Files.walk(activeDir).sorted(Comparator.reverseOrder()).forEach(Files::delete)

    }

    @Test
    fun fileContentEqualsLocalTest() {
        val localDir =
            configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix + "testDir")
        val activeDir = configuration.global.activeStatePath.resolve(".testDir")

        // Create sample test files
        Files.createDirectories(localDir)
        Files.createDirectories(activeDir)
        // Create subdirectory for test files
        val testFileDir = localDir.resolve("test")
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
        assertTrue(activeFile.contentEqualsLocal())

        // Modify the active file to break equality
        testFile.writeText("Different content")

        // Now, check that the content equality fails
        assertFalse(activeFile.contentEqualsLocal(), "Contents should not be equal after modification")

        // Clean up the temporary directories and files after tests
        Files.walk(localDir).sorted(Comparator.reverseOrder()).forEach(Files::delete)
        Files.walk(activeDir).sorted(Comparator.reverseOrder()).forEach(Files::delete)

    }
    /*___________________________________________________________________________________________________________________________*/

    /*__________________________________________Tests For Active__________________________________________________________________*/


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

    @Test
    fun pathToActiveWithLocalPathForUnix() {
        val subject =
            configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix + "test/active/file.txt")
        val result = configuration.global.activeStatePath.resolve(".test/active/file.txt")

        assertEquals(result, subject.toActive())
    }

    @Test
    fun fileToActiveWithPathForUnix() {
        val subject = File(configuration.global.activeStatePath.resolve(".test/active/file.txt").pathString)

        assertEquals(subject, subject.toActive())
    }

    @Test
    fun fileExistsInActiveTest() {
        val tempDir = configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix + "testDir")
        assertTrue(tempDir.isLocal())
        val activeDir = configuration.global.activeStatePath.resolve(".testDir")

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
    fun pathExistsInActiveTest() {
        val tempDir = configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix + "testDir")
        assertTrue(tempDir.isLocal())
        val activeDir = configuration.global.activeStatePath.resolve(".testDir")

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
    fun fileContentEqualsActiveTest() {
        val tempDir = configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix + "testDir")
        val activeDir = configuration.global.activeStatePath.resolve(".testDir")

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