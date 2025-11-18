package file.filter

import com.an5on.file.filter.DefaultLocalIgnoreFileFilter
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse

class DefaultLocalIgnoreFileFilterTest {

    @Test
    fun acceptWithIgnoredLocalFiles(@TempDir tempDir: File) {
        // single hidden files (matches ".*")
        println(File(tempDir, ".gitignore"))
        assertFalse(DefaultLocalIgnoreFileFilter.accept(tempDir, ".gitignore"))
        assertFalse(DefaultLocalIgnoreFileFilter.accept(tempDir, ".env"))
        assertFalse(DefaultLocalIgnoreFileFilter.accept(File(".gitignore")))
        assertFalse(DefaultLocalIgnoreFileFilter.accept(File(".env")))

        // recursive hidden files and hidden directories (matches ".*/**")
        assertFalse(DefaultLocalIgnoreFileFilter.accept(tempDir, ".hidden/file.txt"))
        assertFalse(DefaultLocalIgnoreFileFilter.accept(tempDir, ".config/sub/file"))
        assertFalse(DefaultLocalIgnoreFileFilter.accept(File(".hidden/file.txt")))
        assertFalse(DefaultLocalIgnoreFileFilter.accept(File(".config/sub/file")))
    }


    @Test
    fun acceptWithNullParentDirectoryAndNullFileName(@TempDir dir: File) {
        assertFalse(DefaultLocalIgnoreFileFilter.accept(dir, null))
        assertFalse(DefaultLocalIgnoreFileFilter.accept(null, "Non-existentFile.txt"))
        val p: File? = null
        val attrs: String? = null
        assertFalse( DefaultLocalIgnoreFileFilter.accept(p, attrs))
    }

}