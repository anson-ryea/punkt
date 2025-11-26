package file.filter

import BaseTestWithTestConfiguration
import com.an5on.file.filter.DefaultActiveIgnoreFileFilter
import com.an5on.system.OsType
import com.an5on.system.SystemUtils.osType
import com.an5on.system.SystemUtils.resetOsType
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultActiveIgnoreFileFilterTest : BaseTestWithTestConfiguration() {

    @Test
    fun acceptWithValidFile(@TempDir tempDir: File) {
        assertTrue(DefaultActiveIgnoreFileFilter.accept(File("test.txt")))
        assertTrue(DefaultActiveIgnoreFileFilter.accept(tempDir, "test.txt"))
    }

    @Test
    fun acceptWithInValidFileForWin(@TempDir tempDir: File) {
        osType = OsType.WINDOWS

        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, "Thumbs.db"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, "ehthumbs.db"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, "ehthumbs_vista.db"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, "test.stackdump"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, "Desktop.ini"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, "desktop.ini"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, "test.cab"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, "test.msi"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, "test.msix"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, "test.msm"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, "test.msp"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, "test.lnk"))

        // also test File overloads (name-only, no dir)
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File("Thumbs.db")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File("ehthumbs.db")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File("ehthumbs_vista.db")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File("test.stackdump")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File("Desktop.ini")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File("desktop.ini")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File("test.cab")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File("test.msi")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File("test.msix")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File("test.msm")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File("test.msp")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File("test.lnk")))

        resetOsType()
    }

    @Test
    fun acceptWithInValidFileForDarwin(@TempDir tempDir: File) {
        osType = OsType.DARWIN

        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, ".DS_Store"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, ".localized"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, ".AppleDouble"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, "__MACOSX"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, ".LSOverride"))

        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, "._foo"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, "._bar.txt"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, "Icon"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, ".DocumentRevisions-V100"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, ".fseventsd"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, ".Spotlight-V100"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, ".TemporaryItems"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, ".Trashes"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, ".VolumeIcon.icns"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, ".com.apple.timemachine.donotpresent"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, ".AppleDB"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, ".AppleDesktop"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, "Network Trash Folder"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, "Temporary Items"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, ".apdisk"))

        // also test File overloads (name-only, no dir)
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File(".DS_Store")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File(".localized")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File(".AppleDouble")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File("__MACOSX")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File(".LSOverride")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File("._foo")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File("Icon")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File(".DocumentRevisions-V100")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File(".fseventsd")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File(".Spotlight-V100")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File(".TemporaryItems")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File(".Trashes")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File(".VolumeIcon.icns")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File(".com.apple.timemachine.donotpresent")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File(".AppleDB")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File(".AppleDesktop")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File("Network Trash Folder")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File("Temporary Items")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File(".apdisk")))

        resetOsType()
    }

    @Test
    fun acceptWithInValidFileForLinux(@TempDir tempDir: File) {
        osType = OsType.LINUX

        // swap files
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, ".foo.swp"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, ".foo.swo"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, ".foo.swx"))

        // simple names
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, ".directory"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, "nohup.out"))

        // backup suffix
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, "file~"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, "notes.txt~"))

        // fuse/nfs patterns
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, ".fuse_hidden123"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, ".nfs0001"))

        // Trash and cache recursive globs (.Trash-*/**, .cache/**, .local/share/Trash/**)
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, ".Trash-1000/info"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, ".Trash-1000/files/somefile"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, ".cache/someapp/cachefile"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, ".cache/someapp/subdir/file"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, ".local/share/Trash/info"))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(tempDir, ".local/share/Trash/files/removed"))

        // Also test File overloads (name-only)
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File(".foo.swp")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File(".foo.swo")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File(".foo.swx")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File(".directory")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File("nohup.out")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File("file~")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File(".fuse_hidden123")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File(".nfs0001")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File(".Trash-1000/files/somefile")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File(".cache/someapp/file")))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(File(".local/share/Trash/files/removed")))

        resetOsType()
    }


    @Test
    fun acceptWithNullParentDirectoryAndNullFileName(@TempDir dir: File) {
        assertFalse(DefaultActiveIgnoreFileFilter.accept(dir, null))
        assertFalse(DefaultActiveIgnoreFileFilter.accept(null, "Non-existentFile.txt"))
        val p: File? = null
        val attrs: String? = null
        assertFalse(DefaultActiveIgnoreFileFilter.accept(p, attrs))
    }
}