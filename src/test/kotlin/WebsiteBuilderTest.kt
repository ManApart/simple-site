import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import websiteBuilder.getBetween

class WebsiteBuilderTest {

    @Test
    fun getBetween() {
        val source = "<include=\"home.html\">"
        val actual = getBetween("<include=\"", "\">", source)
        assertEquals("home.html", actual)
    }

    @Test
    fun getBetweenNoMatch() {
        val source = "<include=\"home.html\">"
        val actual = getBetween("<jimbo", "\">", source)
        assertNull(actual)
    }

    @Test
    fun getBetweenDisordered() {
        val source = "<include=\"home.html\">"
        val actual = getBetween("\">", "<include=\"", source)
        assertNull(actual)
    }

    @Test
    fun getBetweenGetsLast() {
        val source = "<include=\"home.html\"><include=\"credits.html\">"
        val actual = getBetween("<include=\"", "\">", source)
        assertEquals("credits.html", actual)
    }

}