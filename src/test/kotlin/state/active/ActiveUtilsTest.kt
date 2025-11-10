package state.active

//import com.an5on.states.active.ActiveUtils.dotReplacementPrefix
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.states.active.ActiveUtils.toActive
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.pathString
import kotlin.test.Test
import kotlin.test.assertEquals

class ActiveUtilsTest {

    @Test
    fun pathToActiveWithActivePathForWin() {
        val subject = configuration.global.activeStatePath.resolve(".test\\active\\file.txt")

        assertEquals(subject, subject)
    }

//    @Test
//    fun pathToActiveWithRelativePathForWin() {
//        val subject = Path(configuration.global.dotReplacementPrefix + "test\\active\\file.txt")
//        val result = configuration.global.activeStatePath.resolve(".test\\active\\file.txt")
//
//        assertEquals(result,subject.toActive() )
//    }

    @Test
    fun pathToActiveWithLocalPathForWin() {
        val subject =
            configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix + "test\\active\\file.txt")
        val result = configuration.global.activeStatePath.resolve(".test\\active\\file.txt")

        assertEquals(result, subject.toActive())
    }

//    @Test
//    fun pathToActiveWithAbsoluteNonLocalPath() {
//
//        val subject = Path("C:\\ProgramFiles\\.test\\active\\file.txt")
//        val
//
//    }
    @Test
    fun fileToActiveWithPathForWin() {
        val subject = File(configuration.global.activeStatePath.resolve(".test\\active\\file.txt").pathString)

        assertEquals(subject, subject)
    }
}