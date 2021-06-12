data class Context(
    val data: Map<String, Any>,
    val files: Map<String, String>  = mapOf(),
    val scopedData: Map<String, Any> = mapOf<String, String>()
)