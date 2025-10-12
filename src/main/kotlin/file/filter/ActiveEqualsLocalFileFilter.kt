package com.an5on.file.filter

import com.an5on.states.active.ActiveUtils.contentEqualsActive
import com.an5on.states.active.ActiveUtils.toActive
import com.an5on.states.local.LocalUtils.contentEqualsLocal
import com.an5on.states.local.LocalUtils.isLocal
import com.an5on.states.local.LocalUtils.toLocal
import org.apache.commons.io.filefilter.IOFileFilter
import java.io.File

object ActiveEqualsLocalFileFilter : IOFileFilter {
    override fun accept(file: File?): Boolean {
        if (file == null) {
            return false
        }

        return if (file.isLocal()) {
            if (file.isDirectory) {
                file.toActive().exists()
            }
            file.contentEqualsActive()
        } else {
            if (file.isDirectory) {
                file.toLocal().exists()
            }
            file.contentEqualsLocal()
        }
    }

    override fun accept(dir: File?, name: String?): Boolean {
        return if (dir != null && name != null) {
            accept(File(dir, name))
        } else {
            false
        }
    }
}