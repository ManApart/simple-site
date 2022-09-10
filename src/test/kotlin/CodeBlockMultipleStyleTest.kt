import org.jsoup.Jsoup
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import simpleSite.codeBlock.formatCodeBlocks

class CodeBlockMultipleStyleTest {

    @Test
    fun appendCodeStyles() {
        val source = """
        <code>
        @JsModule("localforage")
        @JsNonModule
        external object LocalForage {
            fun setItem(key: String, value: Any): Promise&lt;*&gt;
            fun getItem(key: String): Promise&lt;Any?&gt;
        }
        </code>
        """.trimIndent()

        val expected = """
        <code class="hljs"><span class="hljs-meta">@JsModule</span>("<span class="hljs-string">localforage</span>") 
        <span class="hljs-meta">@JsNonModule</span>
        <span class="hljs-keyword">external</span> <span class="hljs-keyword">object</span> LocalForage {
            <span class="hljs-function">fun</span> <span class="hljs-title">setItem</span>(key: <span class="hljs-type">String</span>, value: <span class="hljs-type">Any</span>)</span>: <span class="hljs-type">Promise&lt;*&gt;</span>
            <span class="hljs-function">fun</span> <span class="hljs-title">getItem</span>(key: <span class="hljs-type">String</span>)</span>: <span class="hljs-type">Promise&lt;Any?&gt;</span>
        }</code>
        """.unwrap()

        val actual = source.formatCodeBlocks().unwrap().replace("\n", "")
        assertEquals(expected, actual)
    }

    @Test
    fun appendCodeStyles2() {
        val source = """
        <code class="hljs">
        object JsonObject {
            fun keys(obj: Any): List&lt;String&gt; {
                val raw = js("Object.keys(obj)") as Array&lt;*&gt;
                return raw.map { it as String }
            }
        }
        }</code>
        """.trimIndent()

        val expected = """
        <code class="hljs"><span class="hljs-keyword">object</span> JsonObject {
            <span class="hljs-function">fun</span> <span class="hljs-title">keys</span>(obj: <span class="hljs-type">Any</span>): <span class="hljs-type">List&lt;String&gt;</span> {
                <span class="hljs-keyword">val</span> raw = js("<span class="hljs-string">Object.keys(obj)</span>") <span class="hljs-keyword">as</span> <span class="hljs-type">Array&lt;*&gt;</span>
                <span class="hljs-keyword">return</span> raw.map { it <span class="hljs-keyword">as</span> <span class="hljs-type">String</span> }
            }
        }
        </code>
        """.unwrap()

        val actual = source.formatCodeBlocks().unwrap().replace("\n", "")
        assertEquals(expected, actual)
    }
}

fun String.unwrap(): String {
    return Jsoup.parse(this).body().html().replace("\n", "")
}