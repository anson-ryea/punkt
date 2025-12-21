package git

import BaseTestWithTestConfiguration
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.git.AddOperation
import com.an5on.type.BooleanWithAuto
import org.eclipse.jgit.api.Git
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.relativeTo

class AddOperationTest : BaseTestWithTestConfiguration() {

    companion object {
        @JvmStatic
        @BeforeAll
        fun setupLogDir() {
            System.setProperty("log.dir", Files.createTempDirectory("test-logs").toString())
        }
    }

    @Test
    fun operateWithBundledWithLocalDirectory() {

        val git = Git.init().setDirectory(configuration.global.localStatePath.toFile()).call()

        val fileDir = Files.createTempDirectory(configuration.global.localStatePath, ".dir")
        val file = fileDir.resolve("file.txt").toFile().apply { writeText("hello") }
        val filePath = Path(file.toString().replace("\\", "/"))

        val op = AddOperation(
            BooleanWithAuto.TRUE,
            repositoryPath = configuration.global.localStatePath,
            targetPath = filePath
        )

        val result = op.operateWithBundled()
        assertTrue(result.isRight())

        val status = git.status().call()
        val expectedPath = filePath.relativeTo(configuration.global.localStatePath).toString().replace("\\", "/")
        println("added: ${status.added}")
        println("changed: ${status.changed}")
        println("untracked: ${status.untracked}")
        assertTrue(status.added.contains(expectedPath), "expected $expectedPath to be staged, got ${status.added}")

    }

    @Test
    fun operateWithSystemWithLocalDirectory() {

        // init repo
        val git = Git.init().setDirectory(configuration.global.localStatePath.toFile()).call()

        // create file
//        should fix since the add operation doesn't allow to have pathString starts with "."
        val fileDir = configuration.global.localStatePath.resolve(".dir")
        Files.createDirectories(fileDir)
        val file = fileDir.resolve("file.txt").toFile().apply { writeText("hello") }.toPath()

        // Instantiate AddOperation with bundled = False
        val op = AddOperation(
            BooleanWithAuto.FALSE,
            repositoryPath = configuration.global.localStatePath,
            targetPath = file
        )

        val result = op.operateWithSystem()
        assertTrue(result.isRight())

        // verify staged
        val status = git.status().call()
        val expectedPath = file.relativeTo(configuration.global.localStatePath).toString()
        println("added: ${status.added}")
        println("changed: ${status.changed}")
        println("untracked: ${status.untracked}")
        assertTrue(status.added.contains(expectedPath), "expected $expectedPath to be staged, got ${status.added}")

    }

}