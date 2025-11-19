package file

import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.config.Configuration
import com.an5on.config.GlobalConfiguration
import com.an5on.file.DefaultLocalIgnore
import java.nio.file.Files
import kotlin.test.Test
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