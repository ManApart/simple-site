import org.jsoup.Jsoup
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import simpleSite.codeBlock.formatCodeBlock

/*
Highlight syntax in code blocks. So a naive version of higlightjs, but at generation time instead of runtime
https://highlightjs.org/
 */
class CodeBlockSyntaxTest {
    @Test
    fun topLevel() {
        val source = "<code></code>"
        val expected = "<code class=\"hljs\"></code>"

        val actual = source.formatCodeBlocks().unwrap()
        assertEquals(expected, actual)
    }

    @Test
    fun singleToken() {
        val source = """
            <code>
            class Thingy(){}
            </code>
        """
        val expected = """
            <code class="hljs">
            <span class="hljs-keyword">class</span> Thingy(){}
            </code>""".trimIndent().replace("\n", "")

        val actual = source.formatCodeBlocks().unwrap()
        assertEquals(expected, actual)
    }

    @Test
    fun doubleToken() {
        val source = """
            <code>
            class Thingy(){}
            class OtherThingy(){}
            </code>
        """
        val expected = """
            <code class="hljs">
            <span class="hljs-keyword">class</span> Thingy(){} 
            <span class="hljs-keyword">class</span> OtherThingy(){}
            </code>
        """.trimIndent().replace("\n", "")

        val actual = source.formatCodeBlocks().unwrap()
        assertEquals(expected, actual)
    }

    @Test
    fun simpleCap() {
        val source = """
            <code>
            @Annotation
            </code>
        """
        val expected = """
            <code class="hljs">
            <span class="hljs-meta">@Annotation</span>
            </code>""".trimIndent().replace("\n", "")

        val actual = source.formatCodeBlocks().unwrap()
        assertEquals(expected, actual)
    }

    @Test
    fun doubleCap() {
        val source = """
            <code>
            @Annotation
            @AnotherMeta
            </code>
        """
        val expected = """
            <code class="hljs">
            <span class="hljs-meta">@Annotation</span> 
            <span class="hljs-meta">@AnotherMeta</span>
            </code>""".trimIndent().replace("\n", "")

        val actual = source.formatCodeBlocks().unwrap()
        assertEquals(expected, actual)
    }

    @Test
    fun simpleString() {
        val source = """
            <code>
            JsModule("localforage")
            </code>
        """
        val expected = """
            <code class="hljs">
            JsModule("<span class="hljs-string">localforage</span>")
            </code>""".trimIndent().replace("\n", "")

        val actual = source.formatCodeBlocks().unwrap()
        assertEquals(expected, actual)
    }

    @Test
    fun typeWithGeneric() {
        val source = """
            <code>
            inputs: List&lt;Any?&gt;
            </code>
        """
        val expected = """
            <code class="hljs">
            inputs: <span class="hljs-type">List&lt;Any?&gt;</span>
            </code>""".trimIndent().replace("\n", "")

        val actual = source.formatCodeBlocks().unwrap()
        assertEquals(expected, actual)
    }

    @Test
    fun nestedStylePicksWidest() {
        val source = """
            <code>
            js("Object.keys(obj)") 
            </code>
        """
        val expected = """
            <code class="hljs">
            js("<span class="hljs-string">Object.keys(obj)</span>")
            </code>""".trimIndent().replace("\n", "")

        val actual = source.formatCodeBlocks().unwrap()
        assertEquals(expected, actual)
    }
}

fun String.formatCodeBlocks(): String {
    val doc = Jsoup.parse(this)
    doc.select("code").forEach { formatCodeBlock(it) }

    return doc.html()
}