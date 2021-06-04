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

    @Test
    fun nestedLoop() {
        val source = "<for i=\"pet\" src=\"pets\">Looped<for i=\"pet\" src=\"pets\">Inner Loop</for></for>"
        val data = mapOf(
            "pets" to listOf(
                mapOf("name" to "Smudge"),
                mapOf("name" to "Ollie")
            )
        )
        val parser = DomParser("for")
        val loop1 = ForEach(parser.find(source)!!).compute(source, data)
        val loop2 = ForEach(parser.find(loop1)!!).compute(loop1, data)
        val loop3 = ForEach(parser.find(loop2)!!).compute(loop2, data)

        val expected = "LoopedInner LoopInner LoopLoopedInner LoopInner Loop"
        assertEquals(expected, loop3)
    }


}