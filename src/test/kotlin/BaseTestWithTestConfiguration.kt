import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.config.Configuration
import com.an5on.config.GlobalConfiguration
import org.junit.jupiter.api.BeforeAll
import java.nio.file.Files

abstract class BaseTestWithTestConfiguration {
    companion object {
        private val testConfiguration = Configuration(
            GlobalConfiguration(
                localStatePath = Files.createTempDirectory("test-local"),
                activeStatePath = Files.createTempDirectory("test-active"),
            )
        )

        @BeforeAll
        @JvmStatic
        protected fun setupTestConfiguration() {
            configuration = testConfiguration
        }
    }
}