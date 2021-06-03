import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import directives.Include

class IncludeDirectiveTest {

    @Test
    fun find() {
        val source = "<include src=\"home.html\"/>"
        val actual = Include.find(source)
        val expected = Include(0, source.length, "home.html")
        assertEquals(expected, actual)
    }

    @Test
    fun findNoMatch() {
        val source = "<jimbo src=\"home.html\">"
        val actual = Include.find(source)
        assertNull(actual)
    }

    @Test
    fun findMissingEndSlash() {
        val source = "<include src=\"home.html\">"
        assertThrows<Exception> {
            Include.find(source)
        }
    }

    @Test
    fun findNoSrc() {
        val source = "<include />"
        assertThrows<Exception> {
            Include.find(source)
        }
    }


}