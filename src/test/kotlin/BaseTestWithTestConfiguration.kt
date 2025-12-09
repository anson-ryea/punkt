import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.config.Configuration
import com.an5on.config.GitConfiguration
import com.an5on.config.GlobalConfiguration
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
    )

    init {
        configuration = testConfiguration
    }
}