package file

import BaseTestWithTestConfiguration
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.file.DefaultLocalIgnore
import org.junit.jupiter.api.Assertions.assertFalse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultLocalIgnoreTest : BaseTestWithTestConfiguration() {

    @Test
    fun ignorePatternsEqualsToLocalIgnoreFiles() {
        // assume default set contains at least ".*"
        assertEquals(DefaultLocalIgnore.ignorePatterns, configuration.global.ignoredLocalFiles)
    }

    @Test
    fun matchesWithHiddenLocalFile() {
        // hidden file directly
        val p1 = configuration.global.localStatePath.resolve(".gitignore")
        assertTrue(DefaultLocalIgnore.ignorePathMatchers.any { it.matches(p1) })
        // file under dot-directory
        val p2 = configuration.global.localStatePath.resolve(".config/file.txt")
        assertTrue(DefaultLocalIgnore.ignorePathMatchers.any { it.matches(p2) })
    }

    @Test
    fun matchesWithNormalLocalFile() {
        val ok = configuration.global.localStatePath.resolve("normal.txt")
        assertFalse(DefaultLocalIgnore.ignorePathMatchers.any { it.matches(ok) })
    }


    @Test
    fun matchesWithGlobalRecursivePattern() {
        // ensure patterns include a recursive dot-dir pattern like ".*/**"
        val nested = configuration.global.localStatePath.resolve(".hidden/inner/file.txt")
        assertTrue(
            DefaultLocalIgnore.ignorePathMatchers.any { it.matches(nested) },
            "expected recursive pattern to match $nested"
        )
    }

    @Test
    fun matchesWithDefaultLocalIgnore() {
        assertTrue {
            DefaultLocalIgnore.ignorePathMatchers.any {
                it.matches(
                    configuration.global.localStatePath.resolve(
                        "dir/.hidden/file"
                    )
                )
            }
        }
    }
}