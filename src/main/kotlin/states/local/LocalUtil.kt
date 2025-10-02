package com.an5on.states.local

import com.an5on.config.Configuration.dotReplacementString
import com.an5on.config.Configuration.localDirAbsPath

object LocalUtil {
    fun getLocalRelPathname(relPathname: String): String = relPathname.replace(Regex("^\\.(?!/)|(?<=/)\\."), dotReplacementString)

    fun getLocalAbsPathname(relPathname: String): String = localDirAbsPath + "/" + getLocalRelPathname(relPathname)
}