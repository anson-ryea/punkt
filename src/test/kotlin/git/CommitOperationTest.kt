package git

import BaseTestWithTestConfiguration
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.GitError
import com.an5on.git.CommitOperation
import com.an5on.git.CommitOperation.Companion.substituteCommitMessage
import com.an5on.type.BooleanWithAuto
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.EmptyCommitException
import org.eclipse.jgit.errors.RepositoryNotFoundException
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeAll
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class CommitOperationTest : BaseTestWithTestConfiguration() {

    companion object {
        @JvmStatic
        @BeforeAll
        fun setupLogDir() {
            System.setProperty("log.dir", Files.createTempDirectory("test-logs").toString())
        }
    }

    @Test
    fun operateWithBundledWithRepo() {
        // init repo
        val git = Git.init().setDirectory(configuration.global.localStatePath.toFile()).call()

        // Add a file to be committed
        val testFile = configuration.global.localStatePath.resolve("test.txt").toFile()
        testFile.writeText("hello world")
        git.add().addFilepattern("test.txt").call()

        val op = CommitOperation(
            useBundledGitOption = BooleanWithAuto.TRUE,
            repositoryPath = configuration.global.localStatePath,
            message = "It is a test message",
        )

        val result = op.operateWithBundled()
        assertTrue(result.isRight())

        val commitMessages = git.log().call().map { it.fullMessage }
        assertTrue(commitMessages.any { it.trim() == "It is a test message" })
    }

    @Test
    fun operateWithSystemWithRepo() {
        // init repo
        val git = Git.init().setDirectory(configuration.global.localStatePath.toFile()).call()

        // Add a file to be committed
        val testFile = configuration.global.localStatePath.resolve("test.txt").toFile()
        testFile.writeText("hello world")
        git.add().addFilepattern("test.txt").call()

        val op = CommitOperation(
            useBundledGitOption = BooleanWithAuto.FALSE,
            repositoryPath = configuration.global.localStatePath,
            message = "It is a test message",
        )

        val result = op.operateWithSystem()
        assertTrue(result.isRight())

        val commitMessages = git.log().call().map { it.fullMessage }
        assertTrue(commitMessages.any { it.trim() == "It is a test message" })
    }


    @Test
    fun operationWithBundledWithUnchangedRepository() {
        // init repo and make one commit
        val git = Git.init().setDirectory(configuration.global.localStatePath.toFile()).call()
        val testFile = configuration.global.localStatePath.resolve("test.txt").toFile()
        testFile.writeText("initial content")
        git.add().addFilepattern("test.txt").call()
        git.commit().setMessage("Initial commit").call()

        // try to commit again with no changes
        val op = CommitOperation(
            useBundledGitOption = BooleanWithAuto.TRUE,
            repositoryPath = configuration.global.localStatePath,
            message = "This should fail",
        )

        val result = op.operateWithBundled()
        result.onLeft {
            assertIs<GitError.BundledGitOperationFailed>(it)
            assertIs<EmptyCommitException>(it.cause)
        }
    }

    @Test
    fun operateWithBundledWithFileDirectory() {
        // Don't init a repo, just use a plain directory
        val invalidRepoPath = Files.createTempDirectory("not-a-repo")
        val op = CommitOperation(
            useBundledGitOption = BooleanWithAuto.TRUE,
            repositoryPath = invalidRepoPath,
            message = "This should fail",
        )

        val expectation = assertThrows(RepositoryNotFoundException::class.java) {
            op.operateWithBundled()
        }

        println(expectation.message)
        assertTrue(expectation.message!!.contains("repository not found"))
    }

    @Test
    fun operationWithBundledWithNullMessage() {

        Git.init().setDirectory(configuration.global.localStatePath.toFile()).call()

        val op = CommitOperation(
            useBundledGitOption = BooleanWithAuto.TRUE,
            repositoryPath = configuration.global.localStatePath,
            message = "",
        )

        val result = op.operateWithBundled()
        assertTrue(result.isRight())

    }

    @Test
    fun substituteCommitMessageWithValidMessage() {
        val userName = System.getProperty("user.name")
        val message = "User \${sys:user.name} performed \${op}"
        val operationName = "unsync"
        val result = substituteCommitMessage(message, operationName)
        assertEquals("User $userName performed unsync", result)
    }

    @Test
    fun substituteCommitMessageWithEmptyMessage() {
        val message = ""
        val operationName = "sync"
        val result = substituteCommitMessage(message, operationName)
        assertEquals("", result)

    }
}