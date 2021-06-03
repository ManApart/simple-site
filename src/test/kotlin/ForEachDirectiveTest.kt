import directives.ForEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ForEachDirectiveTest {

    @Test
    fun simpleLoop() {
        val source = "<for i=\"pet\" src=\"pets\">Looped</for>"
        val directive = ForEach(0, source.length, "pet", "pets", "Looped")
        val data = mapOf(
            "pets" to listOf(
                mapOf("name" to "Smudge"),
                mapOf("name" to "Ollie")
            )
        )
        val actual = directive.compute(source, data)
        val expected = "LoopedLooped"
        assertEquals(expected, actual)
    }


}