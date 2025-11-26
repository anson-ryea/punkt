package file.filter

import BaseTestWithTestConfiguration
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.file.filter.DefaultLocalIgnoreFileFilter
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.io.path.pathString
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultLocalIgnoreFileFilterTest : BaseTestWithTestConfiguration() {
    @Test
    fun acceptWithLocalFile() {
        // create a real file to exercise accept(File) on an actual file
        val goodFile = File(configuration.global.activeStatePath.toFile(), "normal.txt").apply { writeText("ok") }
        assertTrue(DefaultLocalIgnoreFileFilter.accept(goodFile))
        assertTrue(DefaultLocalIgnoreFileFilter.accept(configuration.global.activeStatePath.toFile(), "normal.txt"))
        // other non-hidden examples
        assertTrue(DefaultLocalIgnoreFileFilter.accept(File("README.md")))
        assertTrue(DefaultLocalIgnoreFileFilter.accept(File("src/Main.kt")))
    }

    @Test
    fun acceptWithIgnoredLocalFiles() {
        // single hidden files (matches ".*")
        assertFalse(DefaultLocalIgnoreFileFilter.accept(configuration.global.localStatePath.toFile(), ".gitignore"))
        assertFalse(DefaultLocalIgnoreFileFilter.accept(configuration.global.localStatePath.toFile(), ".env"))
        assertFalse(DefaultLocalIgnoreFileFilter.accept(File(configuration.global.localStatePath.resolve(".gitignore").pathString)))
        assertFalse(DefaultLocalIgnoreFileFilter.accept(File(configuration.global.localStatePath.resolve(".env").pathString)))

        // recursive hidden files and hidden directories (matches ".*/**")
        assertFalse(
            DefaultLocalIgnoreFileFilter.accept(
                configuration.global.localStatePath.toFile(),
                ".hidden/file.txt"
            )
        )
        assertFalse(DefaultLocalIgnoreFileFilter.accept(File(configuration.global.localStatePath.resolve(".hidden/file.txt").pathString)))
    }

    @Test
    fun acceptWithMixedPaths() {
        // nested path where one segment starts with dot
        assertFalse(
            DefaultLocalIgnoreFileFilter.accept(
                configuration.global.localStatePath.toFile(),
                "dir/.hidden/file"
            )
        )
        assertFalse(DefaultLocalIgnoreFileFilter.accept(configuration.global.localStatePath.toFile(), "a/.b/c"))
        // ensure similar-looking names that do NOT start with dot are accepted
        assertTrue(
            DefaultLocalIgnoreFileFilter.accept(
                configuration.global.activeStatePath.toFile(),
                "dir/hidden/file"
            )
        )
        assertTrue(DefaultLocalIgnoreFileFilter.accept(configuration.global.activeStatePath.toFile(), "a/b.c/c"))
    }

    @Test
    fun acceptWithNullParentDirectoryAndNullFileName(@TempDir dir: File) {
        assertFalse(DefaultLocalIgnoreFileFilter.accept(dir, null))
        assertFalse(DefaultLocalIgnoreFileFilter.accept(null, "Non-existentFile.txt"))
        val p: File? = null
        val attrs: String? = null
        assertFalse(DefaultLocalIgnoreFileFilter.accept(p, attrs))
    }

}