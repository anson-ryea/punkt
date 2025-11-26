package file.filter

import com.an5on.file.PunktIgnore
import com.an5on.file.filter.PunktIgnoreFileFilter
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PunktIgnoreFileFilterTest {
    init {
        PunktIgnore.ignoreFilePath = Path("src/test/resources/.punktignore")
    }

    @TempDir
    lateinit var tempDir: Path

    @Test
    fun acceptWithFileNotInPunktIgnore() {
        val notIgnored = File(tempDir.toFile(), "keep.txt")
        assertTrue(PunktIgnoreFileFilter.accept(notIgnored), "Non-ignored file should be accepted")
    }

    @Test
    fun acceptWithFileInPunktIgnore() {
        val ignored = File("ignored.txt")
        assertFalse(PunktIgnoreFileFilter.accept(ignored), "File matching ignore pattern should be rejected")
    }

    @Test
    fun acceptWithDirectoryInPunktIgnore() {
        val sub = File(tempDir.toFile(), "ignored_folder")
        val nested = File(sub, "file.txt")

        assertFalse(PunktIgnoreFileFilter.accept(sub), "Ignored directory itself should be accepted")
        assertFalse(PunktIgnoreFileFilter.accept(nested), "File under ignored directory should be rejected")
    }

    @Test
    fun acceptWithDirectoryNotInPunktIgnore() {
        val root = tempDir.toFile()
        val file = File(root, "keep2.txt")

        assertEquals(
            PunktIgnoreFileFilter.accept(file),
            PunktIgnoreFileFilter.accept(root, file.name)
        )
    }
}