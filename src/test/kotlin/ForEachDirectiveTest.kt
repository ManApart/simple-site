import simpleSite.directives.ForEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import simpleSite.Context
import simpleSite.DomParser

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
        val actual = directive.compute(source, Context(data))
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
        val actual = ForEach(parser.find(source)!!).compute(source, Context(data))
        val expected = "LoopedInner LoopInner LoopLoopedInner LoopInner Loop"
        assertEquals(expected, actual)
    }

    @Test
    fun nestedMultipleScopes() {
        val source = "<for i=\"pet\" src=\"pets\">{{pet.name}}<for i=\"toy\" src=\"pet.toys\">{{toy.name}}</for></for>"
        val data = mapOf(
            "pets" to listOf(
                mapOf("name" to "Smudge", "toys" to listOf(mapOf("name" to "chewy"), mapOf("name" to "mouse"))),
                mapOf("name" to "Ollie", "toys" to listOf(mapOf("name" to "bone")))
            )
        )
        val parser = DomParser("for")
        val actual = ForEach(parser.find(source)!!).compute(source, Context(data))
        val expected = "SmudgechewymouseOlliebone"
        assertEquals(expected, actual)
    }

    @Test
    fun interpolatedLoop() {
        val source = "<for i=\"pet\" src=\"pets\">{{pet.name}} </for>"
        val directive = ForEach(0, source.length, "pet", "pets", "{{pet.name}} ")
        val data = mapOf(
            "pets" to listOf(
                mapOf("name" to "Smudge"),
                mapOf("name" to "Ollie")
            )
        )
        val actual = directive.compute(source, Context(data))
        val expected = "Smudge Ollie "
        assertEquals(expected, actual)
    }

    @Test
    fun rawList() {
        val source = "<for i=\"name\" src=\"names\">{{name}}</for>"
        val directive = ForEach(0, source.length, "name", "names", "{{name}}")
        val data = mapOf(
            "names" to listOf("Smudge", "Ollie")
        )
        val actual = directive.compute(source, Context(data))
        val expected = "SmudgeOllie"
        assertEquals(expected, actual)
    }

    @Test
    fun loopWithIfNotNull() {
        val source = "<for i=\"pet\" src=\"pets\"><ifnotnull src=\"pet.name\">Here</ifnotnull></for>"
        val directive = ForEach(0, source.length, "pet", "pets", "<ifnotnull src=\"pet.name\">Here</ifnotnull>")
        val data = mapOf(
            "pets" to listOf(
                mapOf("name" to "Smudge"),
                mapOf("name" to "Ollie")
            )
        )
        val actual = directive.compute(source, Context(data))
        val expected = "HereHere"
        assertEquals(expected, actual)
    }

    @Test
    fun loopWithIfNull() {
        val source = "<for i=\"pet\" src=\"pets\"><ifnotnull src=\"pet.age\">Bob</ifnull></for>"
        val directive = ForEach(0, source.length, "pet", "pets", "<ifnull src=\"pet.age\">Bob</ifnull>")
        val data = mapOf(
            "pets" to listOf(
                mapOf("name" to "Smudge"),
                mapOf("name" to "Ollie")
            )
        )
        val actual = directive.compute(source, Context(data))
        val expected = "BobBob"
        assertEquals(expected, actual)
    }

    @Test
    fun sequentialLoop() {
        val source = "<for i=\"pet\" src=\"pets\">Looped</for><for i=\"pet\" src=\"pets\">Looped</for>"
        val directive = ForEach(0, source.length, "pet", "pets", "Looped")
        val data = mapOf(
            "pets" to listOf(
                mapOf("name" to "Smudge"),
                mapOf("name" to "Ollie")
            )
        )
        val actual = directive.compute(source, Context(data))
        val expected = "LoopedLoopedLoopedLooped"
        assertEquals(expected, actual)
    }

}