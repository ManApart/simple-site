import org.jsoup.Jsoup
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CodeBlockSyntaxTest {
    private val source = """
    <code class="language-kotlin">
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
    """.trimIndent()

    @Test
    fun appendCodeStyles() {
        val doc = Jsoup.parse(source)
        val code = doc.select("code")
        println(code.html())
        val actual = ""
        assertEquals(expected, actual)
    }

}