package websiteBuilder

data class Element(
    val start: Int,
    val end: Int,
    val attributes: Map<String, String>,
    val content: String
)