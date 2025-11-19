package file

import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.config.Configuration
import com.an5on.config.GlobalConfiguration
import com.an5on.file.DefaultLocalIgnore
import org.junit.jupiter.api.Assertions.assertFalse
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultLocalIgnoreTest {
    private val testConfiguration = Configuration(
        GlobalConfiguration(
            localStatePath = Files.createTempDirectory("test-share"),
            activeStatePath = Files.createTempDirectory("test-home"),
        )
    )

    init {
        configuration = testConfiguration
    }

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
        val p2 = configuration.global.localStatePath.resolve(".config").resolve("file.txt")
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
        val nested = configuration.global.localStatePath.resolve(".trash").resolve("files").resolve("a.txt")
        assertTrue(DefaultLocalIgnore.ignorePathMatchers.any { it.matches(nested) }, "expected recursive pattern to match ${nested}")
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