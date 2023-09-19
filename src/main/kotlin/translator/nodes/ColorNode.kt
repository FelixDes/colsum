package translator.nodes

import color.CssColor
import translator.nodes.NumberNode.*
import translator.nodes.SemanticException.CODE.ILLEGAL_ARGUMENT
import translator.nodes.SemanticException.CODE.UNKNOWN_FUNCTION
import kotlin.math.roundToInt

class ColorNode private constructor(private var color: Lazy<CssColor>) : ASTNode<CssColor>(),
    Calculable<ColorNode> {
    override fun compute(): CssColor = color.value

    companion object {
        fun nodeForHex(hex: String) = ColorNode(lazy { CssColor.fromHEX(hex) })
        fun nodeForConst(const: String) = ColorNode(lazy { CssColor.fromConstant(const) })
        fun nodeForFunction(name: String, args: List<NumberNode>) = ColorNode(lazy { fromFunction(name, args) })

        private fun fromFunction(name: String, args: List<NumberNode>): CssColor =
            when (name) {
                "rgb", "rgba" -> {
                    val red: Int = parseRgbArg(args[0])
                    val green: Int = parseRgbArg(args[1])
                    val blue: Int = parseRgbArg(args[2])
                    val alpha: Double = if (args.size == 4) parseAlpha(args[3]) else 1.0

                    CssColor.fromRGBA(red, green, blue, alpha)
                }

                "hsl", "hsla" -> {
                    val h: Int = parseHue(args[0])
                    val s: Int = parseHslArg(args[1])
                    val l: Int = parseHslArg(args[2])
                    val a: Double = if (args.size == 4) parseAlpha(args[3]) else 1.0

                    CssColor.fromHSLA(h, s, l, a)
                }

                else -> throw UNKNOWN_FUNCTION.get(name)
            }

        private fun parseRgbArg(arg: NumberNode) = when (arg) {
            is DoubleNode -> arg.compute().roundToInt()
            is DoublePercentNode -> (arg.compute() * 2.55).roundToInt()
            is NoneNode -> arg.compute().roundToInt()
            is AngleNode -> throw ILLEGAL_ARGUMENT.get("angle")
        }

        private fun parseHue(arg: NumberNode) = when (arg) {
            is NoneNode -> arg.compute().roundToInt()
            is DoubleNode -> arg.compute().roundToInt()
            is AngleNode -> arg.compute().roundToInt()
            is DoublePercentNode -> throw ILLEGAL_ARGUMENT.get("percent")
        }

        private fun parseHslArg(arg: NumberNode) = when (arg) {
            is NoneNode -> arg.compute().roundToInt()
            is DoublePercentNode -> arg.compute().roundToInt()
            is AngleNode -> throw ILLEGAL_ARGUMENT.get("angle")
            is DoubleNode -> throw ILLEGAL_ARGUMENT.get("number")
        }

        private fun parseAlpha(arg: NumberNode) = when (arg) {
            is NoneNode -> throw ILLEGAL_ARGUMENT.get("none")
            is DoubleNode -> arg.compute()
            is DoublePercentNode -> arg.compute() * 0.01
            is AngleNode -> throw ILLEGAL_ARGUMENT.get("angle")
        }
    }

    override fun plus(other: ColorNode): ColorNode {
        return ColorNode(lazy { this.color.value + other.color.value })
    }

    override fun minus(other: ColorNode): ColorNode {
        TODO("Not yet implemented")
    }

    override fun div(other: ColorNode): ColorNode {
        TODO("Not yet implemented")
    }

    override fun times(other: ColorNode): ColorNode {
        TODO("Not yet implemented")
    }
}