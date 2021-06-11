import directives.Delete
import directives.ForEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

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
        val expected = Element(0, source.length - 5, mapOf("src" to "credits.html"), "")

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

    @Test
    fun repeatedElement() {
        val source = "<div>one</div><div>two</div>"
        val parser = DomParser("div")
        val loop1 = Delete(parser.find(source)!!).compute(source)
        val loop2 = Delete(parser.find(loop1)!!).compute(loop1)
        assertEquals("onetwo", loop2)
    }

    @Test
    fun nested() {
        val source = "<div>Looped<div>Inner Loop</div></div>"
        val parser = DomParser("div")
        val actual = parser.find(source)

        val expected = "Looped<div>Inner Loop</div>"
        assertEquals(expected, actual?.content)
    }

    @Test
    fun nestedNoEnd() {
        val source = "<div>Looped<div>Inner Loop</div>"
        val parser = DomParser("div")

        assertThrows<Exception> {
            parser.find(source)
        }
    }

    @Test
    fun falsePositiveNoEnd() {
        val source = "<div>Parent<div>a</div><div>b</div></div>"
        val parser = DomParser("div")
        val actual = parser.find(source)

        val expected = "Parent<div>a</div><div>b</div>"
        assertEquals(expected, actual?.content)
    }
}