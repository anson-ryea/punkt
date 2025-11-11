package file

import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.file.FileUtils.expandTildeWithHomePathname
import com.an5on.file.FileUtils.toStringInPathStyle
import com.an5on.system.SystemUtils.homePath
import com.an5on.type.PathStyle
import kotlin.io.path.Path
import kotlin.io.path.pathString
import kotlin.test.Test
import kotlin.test.assertEquals

class FileUtilsTest {

    @Test
    fun stringExpandTildeWithHomePathNameForWin(){
        val subject = "~\\.test\\file\\test.txt"
        val result = homePath.pathString + "\\.test\\file\\test.txt"

        assertEquals(result, subject.expandTildeWithHomePathname())
    }

    @Test
    fun pathToStringInPathStyleAbsoluteWithAbsoluteLocalPathForWin(){
        val subject = configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix +"test\\file\\test.txt")
        val result = configuration.global.activeStatePath.resolve(".test\\file\\test.txt").pathString

        assertEquals(result, subject.toStringInPathStyle(PathStyle.ABSOLUTE))
    }

    @Test
    fun pathToStringInPathStyleRelativeWithAbsoluteLocalPathForWin(){
        val subject = configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix +"test\\file\\test.txt")
        val result = Path(".test\\file\\test.txt").pathString

        assertEquals(result, subject.toStringInPathStyle(PathStyle.RELATIVE))
    }

    @Test
    fun pathToStringInPathStyleLocalAbsoluteWithAbsoluteActivePathForWin(){
        val subject = configuration.global.activeStatePath.resolve(".test\\file\\test.txt")
        val result = configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix +"test\\file\\test.txt").pathString

        assertEquals(result, subject.toStringInPathStyle(PathStyle.LOCAL_ABSOLUTE))
    }

    @Test
    fun pathToStringInPathStyleLocalRelativeWithAbsoluteActivePathForWin(){
        val subject = configuration.global.activeStatePath.resolve(".test\\file\\test.txt")
        val result = Path(configuration.global.dotReplacementPrefix +"test\\file\\test.txt").pathString

        assertEquals(result, subject.toStringInPathStyle(PathStyle.LOCAL_RELATIVE))
    }

    @Test
    fun pathToStringInPathStyleLocalAbsoluteWithAbsolutePathForWin(){
        val subject = Path("C:\\.test\\file\\test.txt")
        val result = Path("C:\\" + configuration.global.dotReplacementPrefix +"test\\file\\test.txt").pathString

        assertEquals(result, subject.toStringInPathStyle(PathStyle.LOCAL_ABSOLUTE))
    }
//current the app does not support syncing files outside the home directory.
//    @Test
//    fun pathToStringInPathStyleLocalRelativeWithAbsolutePathForWin() {
//        val subject = Path("C:\\.test\\file\\test.txt")
//        val result = Path(configuration.global.dotReplacementPrefix + "test\\file\\test.txt").pathString
//
//        assertEquals(result, subject.toStringInPathStyle(PathStyle.LOCAL_RELATIVE))
//    }

    @Test
    fun collectionPathToStringPathStyleWithListOfPathsForWin(){
        val subject = listOf(configuration.global.activeStatePath.resolve(".test\\file\\test1.txt"),
            configuration.global.activeStatePath.resolve(".test\\file\\test2.txt"),
            configuration.global.activeStatePath.resolve(".test\\file\\test3.txt"))
        val result = Path(configuration.global.dotReplacementPrefix +"test\\file\\test1.txt").pathString + "\n" + Path(configuration.global.dotReplacementPrefix +"test\\file\\test2.txt").pathString + "\n" + Path(configuration.global.dotReplacementPrefix +"test\\file\\test3.txt").pathString

        assertEquals(result, subject.toStringInPathStyle(PathStyle.LOCAL_RELATIVE))

    }

//    @Test
//    fun pathToStringInPathStyleAbsoluteWithRelativeLocalPathForWin(){
//        val subject = Path(configuration.global.dotReplacementPrefix +"test\\file\\test.txt")
//        val result = configuration.global.activeStatePath.resolve(".test\\file\\test.txt").pathString
//
//        assertEquals(result, subject.toStringInPathStyle(PathStyle.ABSOLUTE))
//    }

    @Test
    fun stringExpandTildeWithHomePathNameForUnix(){
        val subject = "~/.test/file/test.txt"
        val result = homePath.pathString + "/.test/file/test.txt"

        assertEquals(result, subject.expandTildeWithHomePathname())
    }

    @Test
    fun pathToStringInPathStyleAbsoluteWithAbsoluteLocalPathForUnix(){
        val subject = configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix +"test/file/test.txt")
        val result = configuration.global.activeStatePath.resolve(".test/file/test.txt").pathString

        assertEquals(result, subject.toStringInPathStyle(PathStyle.ABSOLUTE))
    }

    @Test
    fun pathToStringInPathStyleRelativeWithAbsoluteLocalPathForUnix(){
        val subject = configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix +"test/file/test.txt")
        val result = Path(".test/file/test.txt").pathString

        assertEquals(result, subject.toStringInPathStyle(PathStyle.RELATIVE))
    }

    @Test
    fun pathToStringInPathStyleLocalAbsoluteWithAbsoluteActivePathForUnix(){
        val subject = configuration.global.activeStatePath.resolve(".test/file/test.txt")
        val result = configuration.global.localStatePath.resolve(configuration.global.dotReplacementPrefix +"test/file/test.txt").pathString

        assertEquals(result, subject.toStringInPathStyle(PathStyle.LOCAL_ABSOLUTE))
    }

    @Test
    fun pathToStringInPathStyleLocalRelativeWithAbsoluteActivePathForUnix(){
        val subject = configuration.global.activeStatePath.resolve(".test/file/test.txt")
        val result = Path(configuration.global.dotReplacementPrefix +"test/file/test.txt").pathString

        assertEquals(result, subject.toStringInPathStyle(PathStyle.LOCAL_RELATIVE))
    }

    @Test
    fun pathToStringInPathStyleLocalAbsoluteWithAbsolutePathForUnix(){
        val subject = Path("/.test/file/test.txt")
        val result = Path("/" + configuration.global.dotReplacementPrefix +"test\\file\\test.txt").pathString

        assertEquals(result, subject.toStringInPathStyle(PathStyle.LOCAL_ABSOLUTE))
    }

    @Test
    fun collectionPathToStringPathStyleWithListOfPathsForUnix(){
        val subject = listOf(configuration.global.activeStatePath.resolve(".test/file/test1.txt"),
            configuration.global.activeStatePath.resolve(".test/file/test2.txt"),
            configuration.global.activeStatePath.resolve(".test/file/test3.txt"))
        val result = Path(configuration.global.dotReplacementPrefix +"test/file/test1.txt").pathString + "\n" + Path(configuration.global.dotReplacementPrefix +"test/file/test2.txt").pathString + "\n" + Path(configuration.global.dotReplacementPrefix +"test/file/test3.txt").pathString

        assertEquals(result, subject.toStringInPathStyle(PathStyle.LOCAL_RELATIVE))

    }
}