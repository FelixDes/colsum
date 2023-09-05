import kotlin.math.abs
import kotlin.math.round

// TODO("HWB LAB LCH OKLAB OKLCH")

data class CssColor(
    private val red: Int, private val green: Int, private val blue: Int, private val alpha: Double = 1.0
) {

    companion object {

        fun fromHEX(hex: String): CssColor {
            // Remove '#' symbol from the hex string, if present
            val cleanHex = if (hex.startsWith("#")) {
                hex.substring(1)
            } else throw IllegalArgumentException("Invalid hex color code: $hex")

            if (cleanHex.length !in listOf(3, 4, 6, 8)) {
                throw IllegalArgumentException("Invalid hex color code: $hex")
            }

            try {
                val red = when (cleanHex.length) {
                    3, 4 -> Integer.parseInt(cleanHex.substring(0, 1).repeat(2), 16)
                    else -> Integer.parseInt(cleanHex.substring(0, 2), 16)
                }

                val green = when (cleanHex.length) {
                    3, 4 -> Integer.parseInt(cleanHex.substring(1, 2).repeat(2), 16)
                    else -> Integer.parseInt(cleanHex.substring(2, 4), 16)
                }

                val blue = when (cleanHex.length) {
                    3, 4 -> Integer.parseInt(cleanHex.substring(2, 3).repeat(2), 16)
                    else -> Integer.parseInt(cleanHex.substring(4, 6), 16)
                }

                val alpha = when (cleanHex.length) {
                    4 -> Integer.parseInt(cleanHex.substring(3, 4).repeat(2), 16).toDouble() / 255
                    8 -> Integer.parseInt(cleanHex.substring(6, 8), 16).toDouble() / 255
                    else -> 1.0 // Default alpha value if not provided
                }

                return CssColor(red, green, blue, round(alpha * 100) / 100)
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Invalid hex color code: $hex", e)
            }
        }

        fun fromRGBA(
            red: Int, green: Int, blue: Int, alpha: Double = 1.0
        ): CssColor {
            return CssColor(red, green, blue, alpha)
        }

        fun fromHSLA(
            hue_: Int, saturation_: Int, lightness_: Int, alpha: Double = 1.0
        ): CssColor {
            require(saturation_ in 0..100)
            require(lightness_ in 0..100)
            require(alpha in 0.0..1.0)

            val saturation = saturation_.toDouble() / 100
            val lightness = lightness_.toDouble() / 100

            val hue = when {
                hue_ >= 0 -> hue_ % 360
                else -> 360 - hue_ % 360
            }

            val c = saturation * (1 - abs(2 * lightness - 1))
            val x = c * (1 - abs((hue.toDouble() / 60) % 2 - 1))
            val m = lightness - c / 2

            fun res(red: Double, green: Double, blue: Double): CssColor {
                return fromRGBA(
                    ((red + m) * 255).toInt(),
                    ((green + m) * 255).toInt(),
                    ((blue + m) * 255).toInt(),
                    alpha
                )
            }

            return when (hue) {
                in 0..<60 -> res(c, x, 0.0)
                in 60..<120 -> res(x, c, 0.0)
                in 120..<180 -> res(0.0, c, x)
                in 180..<240 -> res(0.0, x, c)
                in 240..<300 -> res(x, 0.0, c)
                in 300..<360 -> res(c, 0.0, x)
                else -> throw IllegalStateException("Incorrect hue: $hue_")
            }
        }
    }


    operator fun plus(color: CssColor): CssColor {
        val bgAlpha = (color.alpha)
        val bgRed = (color.red).toDouble() / 255
        val bgGreen = (color.green).toDouble() / 255
        val bgBlue = (color.blue).toDouble() / 255

        val addingAlpha = (this.alpha)
        val addingRed = (this.red).toDouble() / 255
        val addingGreen = (this.green).toDouble() / 255
        val addingBlue = (this.blue).toDouble() / 255

        val resAlpha = bgAlpha * (1 - addingAlpha) + addingAlpha
        val resRed = bgRed * bgAlpha * (1 - addingAlpha) + addingRed * addingAlpha
        val resGreen = bgGreen * bgAlpha * (1 - addingAlpha) + addingGreen * addingAlpha
        val resBlue = bgBlue * bgAlpha * (1 - addingAlpha) + addingBlue * addingAlpha

        return fromRGBA(
            (resRed * 255).toInt(), (resGreen * 255).toInt(), (resBlue * 255).toInt(), resAlpha
        )
    }

    override fun toString(): String {
        return "rgba($red, $green, $blue, $alpha)"
    }
}