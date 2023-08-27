import java.awt.Color
import kotlin.math.abs

// TODO("HWB LAB LCH OKLAB OKLCH")

class CssColor private constructor(
    private val c: Color
) {
    constructor(
        red: Int,
        green: Int,
        blue: Int,
        alpha: Int = 0
    ) : this(Color(red, green, blue, alpha))

    companion object {
        fun fromHEX(hex: String): CssColor {
            return CssColor(Color.decode(hex)!!)
        }

        fun fromRGBA(
            red: Int,
            green: Int,
            blue: Int,
            alpha: Int = 0
        ): CssColor {
            return CssColor(red, green, blue, alpha)
        }

        fun fromHSLA(
            hue_: Int,
            saturation: Int,
            lightness: Int,
            alpha: Int = 0
        ): CssColor {
            val hue = when {
                hue_ >= 0 -> hue_ % 360
                else -> 360 - hue_ % 360
            }

            require(saturation in 0..100)
            require(lightness in 0..100)
            require(alpha in 0..100)

            val c = saturation * (1 - abs(2 * lightness - 1))
            val x = c * (1 - abs((hue / 60) % 2 - 1))
            val m = lightness - c / 2

            fun res(red: Int, green: Int, blue: Int): CssColor {
                return CssColor(
                    (red + m) * 255,
                    (green + m) * 255,
                    (blue + m) * 255,
                    alpha
                )
            }

            return when (hue) {
                in 0..<60 -> res(c, x, 0)
                in 60..<120 -> res(x, c, 0)
                in 120..<180 -> res(0, c, x)
                in 180..<240 -> res(0, x, c)
                in 240..<300 -> res(x, 0, c)
                in 300..<360 -> res(c, 0, x)
                else -> throw IllegalStateException("Incorrect hue: $hue_")
            }
        }
    }

    operator fun plus(color: CssColor): CssColor {
        val a0 = c.alpha
        val r0 = c.red
        val g0 = c.green
        val b0 = c.blue

        val a1 = color.c.alpha
        val r1 = color.c.red
        val g1 = color.c.green
        val b1 = color.c.blue

        val alpha = (1 - a0) * a1 + a0
        val red = ((1 - a0) * a1 * r1 + a0 * r0) / alpha
        val green = ((1 - a0) * a1 * g1 + a0 * g0) / alpha
        val blue = ((1 - a0) * a1 * b1 + a0 * b0) / alpha

        return CssColor(red, green, blue, alpha)
    }
}