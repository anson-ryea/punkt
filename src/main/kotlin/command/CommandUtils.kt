package com.an5on.command

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.terminal.YesNoPrompt
import kotlin.enums.enumEntries

object CommandUtils {
    private const val CONTENT_INDENTATION = "    " // 4 spaces
    private const val PROMPT_INDENTATION = " :? "
    private const val WARNING_INDENTATION = " :! "
    private const val ERROR_INDENTATION = " :< "
    private const val SUCCESS_INDENTATION = " :> "
    private const val STAGE_INDENTATION = "~~> "
    fun String.indented() = prependIndent(CONTENT_INDENTATION)
    fun String.prependPrompt() = prependIndent(PROMPT_INDENTATION)
    fun String.prependWaring() = prependIndent(WARNING_INDENTATION)
    fun String.prependError() = prependIndent(ERROR_INDENTATION)
    fun String.prependSuccess() = prependIndent(TextColors.green(SUCCESS_INDENTATION))
    fun String.prependStage() = prependIndent(TextColors.cyan(STAGE_INDENTATION))

    inline fun <reified T : Enum<T>> Enum.Companion.toChoices() =
        enumEntries<T>()
            .map { it.name.lowercase().replace("_", "-") }
            .toTypedArray()

    inline fun <reified T : Enum<T>> Enum.Companion.enumEntryOf(choice: String) =
        enumValueOf<T>(choice.uppercase().replace("-", "_"))

    fun punktYesNoPrompt(
        prompt: String,
        terminal: Terminal
    ) = YesNoPrompt(
        TextStyles.bold(
            TextColors.yellow(
                prompt.prependPrompt()
            )
        ),
        terminal
    )
}