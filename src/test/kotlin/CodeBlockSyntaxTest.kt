import org.jsoup.Jsoup
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import simpleSite.codeBlock.formatCodeBlocks

/*
Highlight syntax in code blocks. So a naive version of higlightjs, but at generation time instead of runtime
https://highlightjs.org/
 */
class CodeBlockSyntaxTest {
    private val source = """
    <code>
    object JsonObject {
        fun keys(obj: Any): List&lt;String&gt; {
            val raw = js("Object.keys(obj)") as Array&lt;*&gt;
            return raw.map { it as String }
        }
      }
    </code>
""".trimIndent()

    private val expected = """
        <code class="hljs">
        <span class="hljs-meta">@JsModule(<span class="hljs-string">"localforage"</span>)</span>
        <span class="hljs-meta">@JsNonModule</span>
        <span class="hljs-keyword">external</span> <span class="hljs-keyword">object</span> LocalForage {
            <span class="hljs-function"><span class="hljs-keyword">fun</span> <span class="hljs-title">setItem</span><span class="hljs-params">(key: <span class="hljs-type">String</span>, value: <span class="hljs-type">Any</span>)</span></span>: Promise&lt;*&gt;
            <span class="hljs-function"><span class="hljs-keyword">fun</span> <span class="hljs-title">getItem</span><span class="hljs-params">(key: <span class="hljs-type">String</span>)</span></span>: Promise&lt;Any?&gt;
        }
        </code>
    """.trimIndent().replace("\n", "")

    @Test
    fun topLevel() {
        val source = "<code></code>"
        val expected = "<code class=\"hljs\"></code>"

        val actual = formatCodeBlocks(source).unwrap()
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

        val actual = formatCodeBlocks(source).unwrap()
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

        val actual = formatCodeBlocks(source).unwrap()
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

        val actual = formatCodeBlocks(source).unwrap()
        assertEquals(expected, actual)
    }

    @Test
    fun appendCodeStyles() {
        val actual = formatCodeBlocks(source).unwrap()
        assertEquals(expected, actual)
    }

    private fun String.unwrap(): String {
        return Jsoup.parse(this).body().html()
    }
}