package color

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import utils.ResourcePaths.CONSTANT_COLORS
import java.io.File


object ConstantColors {
    private val json = lazyOf(
        Json.decodeFromString<Map<String, String>>(File(CONSTANT_COLORS.getResourceUri()).readText())
    )

    operator fun get(name: String) = CssColor.fromHEX(json.value[name].toString())
}
