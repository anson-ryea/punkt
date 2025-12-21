import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.config.Configuration
import com.an5on.config.GitConfiguration
import com.an5on.config.GlobalConfiguration
import org.junit.jupiter.api.BeforeAll
import java.nio.file.Files

abstract class BaseTestWithTestConfiguration {
    private val testConfiguration = Configuration(
        GlobalConfiguration(
            localStatePath = Files.createTempDirectory("test-local"),
            activeStatePath = Files.createTempDirectory("test-active"),
        ),
        GitConfiguration(
            bundledGitName = "test123" ,
            bundledGitEmail = "",
        )

        @BeforeAll
        @JvmStatic
        protected fun setupTestConfiguration() {
            configuration = testConfiguration
        }
    }
}