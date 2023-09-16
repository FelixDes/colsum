package utils

class ResourceException(path: String) : RuntimeException("Cannot find the resource: $path")

enum class ResourcePaths(private val path: String) {
    CONSTANT_COLORS("/colors/constant_color_names.json");

    fun getResourceUri() = this::class.java.getResource(path)?.toURI() ?: throw ResourceException(path)
}