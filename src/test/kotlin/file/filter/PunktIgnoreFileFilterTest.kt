package file.filter

import com.an5on.file.PunktIgnore.buildPathMatchersFromPatterns
import com.an5on.file.PunktIgnore.parse
import org.apache.commons.io.filefilter.IOFileFilter
import org.apache.commons.io.filefilter.PathMatcherFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import java.nio.file.PathMatcher
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PunktIgnoreFileFilterTest {

    lateinit var testPunktIgnoreFileFilter: IOFileFilter
    lateinit var ignoreFilePath: Path
    lateinit var ignorePatterns: Set<String>
    lateinit var pathMatchers: Set<PathMatcher>

    @BeforeEach
    fun setup() {
        ignoreFilePath = Path("src/test/resources/.punktIgnore")
        ignorePatterns = parse(ignoreFilePath)
        pathMatchers = buildPathMatchersFromPatterns(ignorePatterns)
        testPunktIgnoreFileFilter = pathMatchers.fold(TrueFileFilter.INSTANCE) { acc, m ->
            acc.and(PathMatcherFileFilter(m).negate())
        }
    }

    @TempDir
    lateinit var tempDir: Path

    @Test
    fun acceptWithFileNotInPunktIgnore() {
        val file = File(tempDir.toFile(), "keep.txt").also { it.writeText("keep") }
        assertTrue(testPunktIgnoreFileFilter.accept(file), "Non-ignored file should be accepted")
    }

    @Test
    fun acceptWithFileInPunktIgnore() {

        val ignored = File(tempDir.toFile(), "ignored.txt").also { it.writeText("x") }
        assertFalse(testPunktIgnoreFileFilter.accept(ignored), "File matching ignore pattern should be rejected")
    }
    @Test
    fun acceptWithDirectoryInPunktIgnore() {
        val sub = File(tempDir.toFile(), "JustATestPattern").apply { mkdir() }
        val nested = File(sub, "file.txt").also { it.writeText("x") }
        println(nested )
        assertFalse(testPunktIgnoreFileFilter.accept(nested), "File under ignored directory should be rejected")
    }

    @Test
    fun acceptWithDirectoryNotInPunktIgnore() {
        val root = tempDir.toFile()
        val f = File(root, "keep2.txt").also { it.writeText("x") }

        assertEquals(
            testPunktIgnoreFileFilter.accept(f),
            testPunktIgnoreFileFilter.accept(root, f.name),
            "accept(dir,name) should delegate to accept(file)"
        )
    }
}