package com.an5on.command

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.terminal.YesNoPrompt
import kotlin.enums.enumEntries

object CommandUtils {
    private const val ECHO_CONTENT_INDENTATION = "    " // 4 spaces
    fun String.indented() = prependIndent(ECHO_CONTENT_INDENTATION)

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
                prompt.indented()
            )
        ),
        terminal
    )
}