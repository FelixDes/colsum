package color

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import utils.ResourcePaths.CONSTANT_COLORS
import java.util.regex.Pattern
import java.util.regex.Pattern.CASE_INSENSITIVE


object ConstantColorsService {
    private val colors: Map<String, CssColor> =
        Json.decodeFromString<Map<String, String>>(CONSTANT_COLORS.getResource())
            .mapValues { CssColor.fromHEX(it.value) }

    fun getNamesRegex() = Pattern.compile(colors.keys.joinToString(separator = "|"), CASE_INSENSITIVE)!!.toRegex()
    operator fun get(name: String): CssColor = colors[name]!!
}
