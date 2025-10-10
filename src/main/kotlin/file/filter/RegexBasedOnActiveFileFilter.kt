package com.an5on.file.filter

import com.an5on.states.active.ActiveUtils.toActive
import org.apache.commons.io.filefilter.IOFileFilter
import java.io.File

class RegexBasedOnActiveFileFilter(
    val regex: Regex,
) : IOFileFilter {
    override fun accept(file: File?): Boolean {
        if (file == null) {
            return false
        }

        return file.toActive().fold({ false }, {
            regex.matches(it.path)
        })
    }

    override fun accept(dir: File?, name: String?): Boolean {
        return if (dir != null && name != null) {
            accept(File(dir, name))
        } else {
            false
        }
    }
}