package com.an5on.file.filter

import com.an5on.states.active.ActiveUtils.existsInActive
import com.an5on.states.local.LocalUtils.existsInLocal
import com.an5on.states.local.LocalUtils.isLocal
import org.apache.commons.io.filefilter.IOFileFilter
import java.io.File

object ExistsInBothActiveAndLocalFileFilter : IOFileFilter {
    override fun accept(file: File?): Boolean {
        if (file == null) {
            return false
        }

        return if (file.isLocal()) {
            file.existsInActive()
        } else {
            file.existsInLocal()
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