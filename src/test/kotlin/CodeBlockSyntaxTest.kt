import org.jsoup.Jsoup
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import simpleSite.codeBlock.formatCodeBlocks

/*
Highlight syntax in code blocks. So a naive version of higlightjs, but at generation time instead of runtime
https://highlightjs.org/
 */
class CodeBlockSyntaxTest {
    //TODO - do one with Promise<Any?>
    private val source = """
    <code>
    @JsModule("localforage")
    @JsNonModule
    external object LocalForage {
        fun setItem(key: String, value: Any): Promise&lt;*&gt;
        fun getItem(key: String): Promise&lt;Any?&gt;
    }
    </code>
    """.trimIndent()

    private val expected = """
        <code class="hljs"><span class="hljs-meta">@JsModule</span>("<span class="hljs-string">localforage</span>") 
        <span class="hljs-meta">@JsNonModule</span>
        <span class="hljs-keyword">external</span> <span class="hljs-keyword">object</span> LocalForage {
            <span class="hljs-function">fun</span> <span class="hljs-title">setItem</span>(key: <span class="hljs-type">String</span>, value: <span class="hljs-type">Any</span>)</span>: <span class="hljs-type">Promise&lt;*&gt;</span>
            <span class="hljs-function">fun</span> <span class="hljs-title">getItem</span>(key: <span class="hljs-type">String</span>)</span>: <span class="hljs-type">Promise&lt;Any?&gt;</span>
        }</code>
    """.unwrap()

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

        val actual = formatCodeBlocks(source).unwrap()
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

        val actual = formatCodeBlocks(source).unwrap()
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

        val actual = formatCodeBlocks(source).unwrap()
        assertEquals(expected, actual)
    }

    @Test
    fun appendCodeStyles() {
        val actual = formatCodeBlocks(source).unwrap().replace("\n", "")
        assertEquals(expected, actual)
    }

    private fun String.unwrap(): String {
        return Jsoup.parse(this).body().html().replace("\n", "")
    }
}