import directives.IfNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IfNotNullDirectiveTest {

    @Test
    fun trueCase() {
        val source = "<ifnotnull src=\"pets\">content</ifnotnull>"
        val directive = IfNull(0, source.length, "pets", "content", false)
        val data = mapOf(
            "pets" to "notnull"
        )
        val actual = directive.compute(source, Context(data))
        val expected = "content"
        assertEquals(expected, actual)
    }

    @Test
    fun falseCase() {
        val source = "<ifnotnull src=\"pets\">content</ifnotnull>"
        val directive = IfNull(0, source.length, "pets", "content", false)
        val data = mapOf<String, Any>()
        val actual = directive.compute(source, Context(data))
        val expected = ""
        assertEquals(expected, actual)
    }

    @Test
    fun nullTrueCase() {
        val source = "<ifnull src=\"pets\">content</ifnull>"
        val directive = IfNull(0, source.length, "pets", "content", true)
        val data = mapOf(
            "pets" to "notnull"
        )
        val actual = directive.compute(source, Context(data))
        val expected = ""
        assertEquals(expected, actual)
    }

    @Test
    fun nullFalseCase() {
        val source = "<ifnull src=\"pets\">content</ifnull>"
        val directive = IfNull(0, source.length, "pets", "content", true)
        val data = mapOf<String, Any>()
        val actual = directive.compute(source, Context(data))

        val expected = "content"
        assertEquals(expected, actual)
    }


}