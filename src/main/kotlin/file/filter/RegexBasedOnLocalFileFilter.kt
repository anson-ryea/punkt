package com.an5on.file.filter

import com.an5on.file.FileUtils.toLocal
import org.apache.commons.io.filefilter.IOFileFilter
import java.io.File

/**
 * An [IOFileFilter] that accepts files whose local path fully matches a supplied regular expression.
 *
 * The "local" path is obtained by transforming the given file to its local-state equivalent via
 * [toLocal], then taking its platform path string (i.e. [File.path]). Matching uses [Regex.matches],
 * which requires the entire local path to match the pattern. If you want a substring/contains
 * match, wrap your pattern with `.*` (e.g. `".* /docs/.*".toRegex()`) or construct the [Regex]
 * with an appropriate pattern up front.
 *
 * Notes:
 * - Matching is case-sensitive by default. Supply [RegexOption.IGNORE_CASE] when constructing
 *   [regex] if you need case-insensitive behaviour.
 * - Directories are treated the same as files; the decision is based solely on the local path string.
 * - A `null` file is not accepted.
 *
 * @property regex The regular expression tested against the local path. It is applied as a full match
 * (via [Regex.matches]); ensure your pattern includes anchors or wildcards as needed.
 * @see IOFileFilter
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
class RegexBasedOnLocalFileFilter(
    val regex: Regex
) : IOFileFilter {
    /**
     * Determines whether the given file is accepted by testing its local path against [regex].
     *
     * Uses a full-string match; see class KDoc for details.
     *
     * @param file The file to test.
     * @return `true` if the file's local path fully matches [regex], otherwise `false`.
     */
    override fun accept(file: File?): Boolean {
        return file?.toLocal()?.path?.matches(regex) ?: false
    }

    /**
     * Determines acceptance by constructing a [File] from the provided parent directory and name,
     * then delegating to [accept(file)].
     *
     * @param dir The parent directory of the file.
     * @param name The file name.
     * @return `true` if the constructed file's local path fully matches [regex], otherwise `false`.
     */
    override fun accept(dir: File?, name: String?): Boolean {
        return if (dir != null && name != null) {
            accept(File(dir, name))
        } else {
            false
        }
    }
}