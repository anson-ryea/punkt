package file

import com.an5on.file.PunktIgnore
import com.an5on.file.PunktIgnore.stripComment
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals

class PunktIgnoreTest {

    @Test
    fun parseWithNonExistingPath() {
        val missing = Path("nonexistent-file-xyz")
        val result = PunktIgnore.parse(missing)
        assertTrue(result.isEmpty())
    }

    @Test
    fun parseWithCommentsAndDuplicatedPattern(@TempDir tempDir: Path) {
        val file = tempDir.resolve("ignore.txt")
        val content = """
            # full line comment
            pattern1
            pattern2 # trailing comment
              
            pattern1   # duplicate with trailing spaces
        """.trimIndent()
        Files.writeString(file, content)

        val expected = setOf("pattern1", "pattern2")
        val result = PunktIgnore.parse(file)
        assertEquals(expected, result)
    }

    @Test
    fun stripCommentWithComment() {
        val s = "  foo/bar  # comment here"
        val cleaned = s.stripComment()
        assertEquals("foo/bar", cleaned)
    }

    @Test
    fun stripCommentWithoutComment() {
        val s = "  justText  "
        assertEquals("justText", s.stripComment())
    }

    @Test
    fun stripCommentWithHashInBetween() {
        val s = "abc#def"
        assertEquals("abc", s.stripComment())
    }

    @Test
    fun stripCommentWithCommentOnly() {
        val s = "   #comment"
        assertEquals("", s.stripComment())
    }
}