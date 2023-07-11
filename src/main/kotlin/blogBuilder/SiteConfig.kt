package blogBuilder

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class SiteConfig(
    val blogs: String,
    val tabTitle: String = "Home",
    val toc: Boolean = false,
    val singlePageToc: Boolean = false,
    val tocTitle: String = "Table of Contents",
    val homeLink: String? = null,
) {
    var sourceFolder = ""
}
