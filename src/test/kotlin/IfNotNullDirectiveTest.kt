import directives.IfNotNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IfNotNullDirectiveTest {

    @Test
    fun trueCase() {
        val source = "<ifnotnull src=\"pets\">content</ifnotnull>"
        val directive = IfNotNull(0, source.length, "pets", "content")
        val data = mapOf(
            "pets" to "notnull"
        )
        val actual = directive.compute(source, data)
        val expected = "content"
        assertEquals(expected, actual)
    }

    @Test
    fun falseCase() {
        val source = "<ifnotnull src=\"pets\">content</ifnotnull>"
        val directive = IfNotNull(0, source.length, "pets", "content")
        val data = mapOf<String, Any>()
        val actual = directive.compute(source, data)
        val expected = ""
        assertEquals(expected, actual)
    }


}