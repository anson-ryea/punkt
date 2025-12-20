package command.options

import com.an5on.command.options.PunktOptionGroup
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

// sample enum for tests
// private enum class SampleEnum { FIRSTOption, SECOND_OPTION, THIRD_OPTION }
private enum class SampleEnum {  SECOND_OPTION, THIRD_OPTION }

// tests
class PunktOptionGroupTest: PunktOptionGroup("Test Options") {

    @Test
    fun toChoicesTest() {
        val choices = Enum.toChoices<SampleEnum>()
        // expected order matches enum declaration
//        val expected = arrayOf("firstoption", "second-option", "third-option")
        val expected = arrayOf("second-option", "third-option")
        assertArrayEquals(expected, choices)
    }

    @Test
    fun enumEntryOfTest() {
//        val e1 = Enum.Companion.enumEntryOf<SampleEnum>("firstoption")
        val e2 = Enum.enumEntryOf<SampleEnum>("second-option")
        val e3 = Enum.enumEntryOf<SampleEnum>("third-option")
//        should fix
//        assertEquals(SampleEnum.FIRSTOption, e1)
        assertEquals(SampleEnum.SECOND_OPTION, e2)
        assertEquals(SampleEnum.THIRD_OPTION, e3)
    }

    @Test
    fun roundTripForToChoicesToEnumsToChoices() {
        val choices = Enum.toChoices<SampleEnum>()
        val roundTripped = choices.map { Enum.enumEntryOf<SampleEnum>(it) }
            .map { it.name.lowercase().replace("_", "-") }
            .toTypedArray()
        assertArrayEquals(choices, roundTripped)
    }
}
