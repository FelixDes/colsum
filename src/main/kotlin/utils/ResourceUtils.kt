package utils

import java.io.InputStream


class ResourceException(path: String) : RuntimeException("Cannot find the resource: $path")

private fun InputStream.getStringContent() = this.bufferedReader().use { it.readText() }

enum class ResourcePaths(private val path: String) {
    CONSTANT_COLORS("/colors/constant_color_names.json");

    fun getResource() =
        object {}.javaClass.getResourceAsStream(path)?.getStringContent() ?: throw ResourceException(path)
}