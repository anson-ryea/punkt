package states.local

import BaseTestWithTestConfiguration
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.file.FileUtils.isLocal
import com.an5on.file.FileUtils.toLocal
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.pathString
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class LocalUtilsTest : BaseTestWithTestConfiguration() {
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

        assertFalse(subject.isLocal())
    }

    @Test
    fun pathIsLocalWithRelativePathForWin() {

        val subject = Path(".test\\file.txt")

        assertFalse(subject.isLocal())
    }

    @Test
    fun pathIsLocalWithNotInHomePathForWin() {

        val subject = Path("C:\\ProgramFiles\\.test\\file.txt")

        assertFalse(subject.isLocal())
    }

    @Test
    fun pathIsLocalWithLocalPathForWin() {

        val subject = configuration.global.activeStatePath.resolve(".test\\file.txt").toLocal()

        assertTrue(subject.isLocal())
    }

    @Test
    fun fileIsLocalWithLocalPathForWin() {

        val subject = configuration.global.activeStatePath.resolve(".test\\file.txt").toLocal().toFile()

        assertTrue(subject.isLocal())
    }

    @Test
    fun fileIsLocalWithNonLocalPathForWin() {

        val subject = Path(".test\\file.txt").toFile()

        assertFalse(subject.isLocal())
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
    fun fileToLocalWithRelativePathForUnix() {

        val subject = File("/ProgramFiles/.test/file.txt")
        val result =
            Path("/ProgramFiles/" + configuration.global.dotReplacementPrefix + "test/file.txt").toFile()
        assertEquals(subject.toLocal(), result)
    }

    @Test
    fun pathIsLocalWithPathInHomeForUnix() {

        val subject = configuration.global.activeStatePath.resolve(".test/file.txt")

        assertFalse(subject.isLocal())
    }

    @Test
    fun pathIsLocalWithRelativePathForUnix() {

        val subject = Path(".test/file.txt")

        assertFalse(subject.isLocal())
    }

    @Test
    fun pathIsLocalWithNotInHomePathForUnix() {

        val subject = Path("/ProgramFiles/.test/file.txt")

        assertFalse(subject.isLocal())
    }

    @Test
    fun pathIsLocalWithLocalPathForUnix() {

        val subject = configuration.global.activeStatePath.resolve(".test/file.txt").toLocal()

        assertTrue(subject.isLocal())
    }

    @Test
    fun fileIsLocalWithLocalPathForUnix() {

        val subject = configuration.global.activeStatePath.resolve(".test/file.txt").toLocal().toFile()

        assertTrue(subject.isLocal())
    }

    @Test
    fun fileIsLocalWithNonLocalPathForUnix() {

        val subject = Path(".test/file.txt").toFile()

        assertFalse(subject.isLocal())
    }
}