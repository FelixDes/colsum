package color

import translator.nodes.Calculable
import kotlin.math.round
import kotlin.math.roundToInt

// TODO("HWB LAB LCH OKLAB OKLCH")

data class CssColor(
    private val red: Int,
    private val green: Int,
    private val blue: Int,
    private val alpha: Double = 1.0
) : Calculable<CssColor> {
    companion object {
        fun fromHEX(hex: String): CssColor {
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
                    else -> 1.0
                }

                return CssColor(red, green, blue, round(alpha * 100) / 100)
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Invalid hex color code: $hex", e)
            }
        }

        fun fromRGBA(
            red: Int,
            green: Int,
            blue: Int,
            alpha: Double = 1.0
        ): CssColor = CssColor(red, green, blue, alpha)

        fun fromHSLA(hue: Int, saturation: Int, lightness: Int, alpha: Double = 1.0): CssColor {
            require(saturation in 0..100)
            require(lightness in 0..100)
            require(alpha in 0.0..1.0)

            val s = saturation.toDouble() / 100
            val l = lightness.toDouble() / 100

            val h = when {
                hue >= 0 -> hue % 360
                else -> 360 + hue % 360
            }.toDouble() / 360

            val r: Double
            val g: Double
            val b: Double
            if (s == 0.0) {
                b = l
                g = b
                r = g
            } else {
                val q = if (l < 0.5) l * (1 + s) else l + s - l * s
                val p = 2 * l - q
                r = hueToRgb(p, q, h + 1.0 / 3.0)
                g = hueToRgb(p, q, h)
                b = hueToRgb(p, q, h - 1.0 / 3.0)
            }
            return fromRGBA((r * 255).roundToInt(), (g * 255).roundToInt(), (b * 255).roundToInt(), alpha)
        }

        private fun hueToRgb(p: Double, q: Double, t: Double): Double {
            var t = t
            if (t < 0f) t += 1f
            if (t > 1f) t -= 1f
            if (t < 1f / 6f) return p + (q - p) * 6f * t
            if (t < 1f / 2f) return q
            return if (t < 2f / 3f) p + (q - p) * (2f / 3f - t) * 6f else p
        }

        fun fromConstant(constant: String): CssColor = ConstantColorsService[constant.lowercase()]
    }


    override fun plus(other: CssColor): CssColor {
        val bgAlpha = this.alpha
        val bgRed = this.red.toDouble() / 255
        val bgGreen = this.green.toDouble() / 255
        val bgBlue = this.blue.toDouble() / 255

        val addingAlpha = other.alpha
        val addingRed = other.red.toDouble() / 255
        val addingGreen = other.green.toDouble() / 255
        val addingBlue = other.blue.toDouble() / 255

        val resAlpha = bgAlpha * (1 - addingAlpha) + addingAlpha
        val resRed = bgRed * bgAlpha * (1 - addingAlpha) + addingRed * addingAlpha
        val resGreen = bgGreen * bgAlpha * (1 - addingAlpha) + addingGreen * addingAlpha
        val resBlue = bgBlue * bgAlpha * (1 - addingAlpha) + addingBlue * addingAlpha

        return fromRGBA(
            (resRed * 255).toInt(),
            (resGreen * 255).toInt(),
            (resBlue * 255).toInt(),
            resAlpha
        )
    }

    override fun minus(other: CssColor): CssColor = throw UnsupportedOperationException()

    override fun div(other: CssColor): CssColor = throw UnsupportedOperationException()

    override fun times(other: CssColor): CssColor = throw UnsupportedOperationException()

    override fun toString(): String = "rgba($red, $green, $blue, $alpha)"
}

fun List<CssColor>.sum(): CssColor = this.reduce { c1, c2 -> c1 + c2 }
