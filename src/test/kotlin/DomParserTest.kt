import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import websiteBuilder.DomParser
import websiteBuilder.Element
import websiteBuilder.getBetween

class DomParserTest {
//
//    @Test
//    fun getBetween() {
//        val source = "<include src=\"home.html\">Content</include>"
//        val parser = DomParser("include", listOf("src"))
//        val expected = Element(0, source.length, mapOf("src" to "home.html"), "Content")
//
//        val actual = parser.find(source)
//        assertEquals(expected, actual)
//    }

    @Test
    fun getBetweenNoMatch() {
        val source = "<div>"
        val parser = DomParser("include", listOf("src"))
        val actual = parser.find(source)
        assertNull(actual)
    }

    //TODO - self closing

}