import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import websiteBuilder.directives.Interpolation

class InterpolationDirectiveTest {

    @Test
    fun find() {
        val source = "{{cat.name}}"
        val actual = Interpolation.find(source)
        val expected = Interpolation(0, source.length, "cat.name")
        assertNotNull(actual)
        assertEquals(expected.start, actual?.start)
        assertEquals(expected.end, actual?.end)
        assertEquals(expected.keyPath, actual?.keyPath)
    }

    @Test
    fun findNoMatch() {
        val source = "<jimbo src=\"home.html\">"
        val actual = Interpolation.find(source)
        assertNull(actual)
    }

    @Test
    fun findMissingEnd() {
        val source = "{{cat.name"
        assertThrows<Exception> {
            Interpolation.find(source)
        }
    }

    @Test
    fun findNoSrc() {
        val source = "{{}}"
        val actual = Interpolation.find(source)
        assertNull(actual)
    }

    @Test
    fun computeSimplestCase() {
        val source = "{{cat.name}}"
        val data = mapOf<String, Any>(
            "cat" to mapOf<String, Any>(
                "name" to "smudge"
            )
        )
        val interpolation = Interpolation(0, source.length, "cat.name")
        val actual = interpolation.compute(source, data)
        assertEquals("smudge", actual)
    }

    @Test
    fun computeNested() {
        val source = "{{cat.owner.name}}"
        val data = mapOf<String, Any>(
            "cat" to mapOf<String, Any>(
                "owner" to mapOf<String, Any>(
                    "name" to "smudge"
                )
            )
        )
        val interpolation = Interpolation(0, source.length, "cat.owner.name")
        val actual = interpolation.compute(source, data)
        assertEquals("smudge", actual)
    }

    @Test
    fun computeErrorsOnArrays() {
        val source = "{{cat.owner.name}}"
        val data = mapOf<String, Any>(
            "cat" to mapOf<String, Any>(
                "owner" to listOf(
                    "smudge"
                )
            )
        )
        val interpolation = Interpolation(0, source.length, "cat.owner.name")
        assertThrows<IllegalStateException> {
            interpolation.compute(source, data)
        }
    }


}