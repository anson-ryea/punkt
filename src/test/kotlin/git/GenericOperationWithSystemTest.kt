package git

import BaseTestWithTestConfiguration
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.git.GenericOperationWithSystem
import org.eclipse.jgit.api.Git
import org.junit.jupiter.api.BeforeAll
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertTrue

class GenericOperationWithSystemTest : BaseTestWithTestConfiguration() {


    companion object {
        @JvmStatic
        @BeforeAll
        fun setupLogDir() {
            System.setProperty("log.dir", Files.createTempDirectory("test-logs").toString())
        }
    }

    @Test
    fun operateWithSystemWithRepo() {
        // init repo
        val git = Git.init().setDirectory(configuration.global.localStatePath.toFile()).call()

        // Add a file to be committed
        val testFile = configuration.global.localStatePath.resolve("test.txt").toFile()
        testFile.writeText("hello world")
        git.add().addFilepattern("test.txt").call()

        val op = GenericOperationWithSystem(listOf("commit", "-m", "It is a test message"))

        val result = op.operateWithSystem()
        assertTrue(result.isRight())

        val commitMessages = git.log().call().map { it.fullMessage }
        assertTrue(commitMessages.any { it.trim() == "It is a test message" })
    }

}