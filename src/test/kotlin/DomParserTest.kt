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

    @Test
    fun parseSelfClosingElement() {
        val source = "<include src=\"credits.html\"/>"
        val parser = DomParser("include")
        val expected = Element(0, source.length, mapOf("src" to "credits.html"), "")

        val actual = parser.find(source)
        assertEquals(expected, actual)
    }

    @Test
    fun parseSelfClosingElementLooksForward() {
        val source = "<br/><include src=\"credits.html\"/>"
        val parser = DomParser("include")
        val expected = Element(5, source.length, mapOf("src" to "credits.html"), "")

        val actual = parser.find(source)
        assertEquals(expected, actual)
    }

    @Test
    fun parseSelfClosingElementIgnoresNext() {
        val source = "<include src=\"credits.html\"/><br/>"
        val parser = DomParser("include")
        val expected = Element(0, source.length-5, mapOf("src" to "credits.html"), "")

        val actual = parser.find(source)
        assertEquals(expected, actual)
    }

    @Test
    fun parseElementIgnoresNext() {
        val source = "<include src=\"credits.html\"></include><include></include>"
        val parser = DomParser("include")
        val expected = Element(0, "<include src=\"credits.html\"></include>".length, mapOf("src" to "credits.html"), "")

        val actual = parser.find(source)
        assertEquals(expected, actual)
    }

}