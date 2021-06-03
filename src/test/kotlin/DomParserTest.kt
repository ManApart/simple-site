import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class DomParserTest {

    @Test
    fun parseElement() {
        val source = "<include src=\"home.html\">Content</include>"
        val parser = DomParser("include")
        val expected = Element(0, source.length, mapOf("src" to "home.html"), "Content")

        val actual = parser.find(source)
        assertEquals(expected, actual)
    }

    @Test
    fun noElementFound() {
        val source = "<div>"
        val parser = DomParser("include")
        val actual = parser.find(source)
        assertNull(actual)
    }

    //TODO - self closing

}