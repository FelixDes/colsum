import java.awt.Color

class CssColor constructor(red: Double, green: Double, blue: Double, alpha: Double) : Color(red.toFloat(), green.toFloat(), blue.toFloat(), alpha.toFloat()) {
    companion object {
        fun fromHEX(hex: String): CssColor {
            TODO()
        }

        fun fromRGBList(components: List<Double>): CssColor {
            return CssColor(
                components[0],
                components[1],
                components[2],
                if (components.size == 4) components[3] else 0.0
            )
        }
//        fun fromCssString(cssString: String): CssColor {
//            val _cssString = cssString.trim()
//            when {
//                // HEX
//                _cssString.startsWith('#') -> {
//                    val decoded: Color = decode(_cssString.substring(1))
//                    return CssColor(
//                        decoded.red,
//                        decoded.green,
//                        decoded.blue,
//                        decoded.alpha,
//                    )
//                }
//                // RGB/RGBA
//                _cssString.matches(
//                    """
//                    ^rgba?\(+\)\$
//                """.trimIndent().toRegex()
//                ) -> {
//                    val args = _cssString.substringAfter("(").substringBeforeLast(")").spl
//                }
//            }
//            TODO("HSL/HSLA HWB LAB LCH OKLAB OKLCH")
//        }
    }

    operator fun plus(color: CssColor) {
//        a01 = (1 - a0)·a1 + a0
//
//        r01 = ((1 - a0)·a1·r1 + a0·r0) / a01
//
//        g01 = ((1 - a0)·a1·g1 + a0·g0) / a01
//
//        b01 = ((1 - a0)·a1·b1 + a0·b0) / a01
    }
}

//fun Color.plus() {
//    this.
//}