import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import directives.Interpolation

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
    fun findTakesStart() {
        val source = "{{cat.name}}{{toy.color}}"
        val start = "{{cat.name}}".length
        val actual = Interpolation.find(source, start)
        val expected = Interpolation(start, source.length, "toy.color")
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
        val actual = interpolation.compute(source, Context(data))
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
        val actual = interpolation.compute(source, Context(data))
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
            interpolation.compute(source, Context(data))
        }
    }

    @Test
    fun computeScopedData() {
        val source = "{{cat.name}}"
        val data = mapOf<String, Any>(
            "cat" to mapOf<String, Any>(
                "name" to "smudge"
            )
        )
        val scopedData = mapOf<String, Any>(
            "pet" to mapOf<String, Any>(
                "name" to "gus"
            )
        )
        val interpolation = Interpolation(0, source.length, "pet.name")
        val actual = interpolation.compute(source, Context(data, scopedData = scopedData))
        assertEquals("gus", actual)
    }

    @Test
    fun computeScopedDataJustString() {
        val source = "{{cat.name}}"
        val data = mapOf<String, Any>(
            "cat" to mapOf<String, Any>(
                "name" to "smudge"
            )
        )
        val scopedData = mapOf<String, Any>(
            "pet" to "frank"
        )
        val interpolation = Interpolation(0, source.length, "pet")
        val actual = interpolation.compute(source, Context(data, scopedData = scopedData))
        assertEquals("frank", actual)
    }

    @Test
    fun ignoreValuesWeDontKnowYet() {
        val source = "{{cat.name}}"
        val interpolation = Interpolation(0, source.length, "cat.name")
        val actual = interpolation.compute(source, Context(mapOf()))
        assertEquals("{{cat.name}}", actual)
    }

    @Test
    fun findDoesNotInfiniteLoop() {
        val source = "{{cat.name}}{{builder.tool}}"
        val actual = source.interpolate(Context(mapOf()))
        assertEquals("{{cat.name}}{{builder.tool}}", actual)
    }

}