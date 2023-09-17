package translator.nodes

import color.CssColor
import translator.nodes.NumberNode.*
import translator.nodes.SemanticException.CODE.ALPHA_ARGUMENT_ERROR
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
                    val red: Int = parseNoneDoublePercentNumberArg(args[0])
                    val green: Int = parseNoneDoublePercentNumberArg(args[1])
                    val blue: Int = parseNoneDoublePercentNumberArg(args[2])
                    val alpha: Double = if (args.size == 4) parseAlpha(args[3]) else 1.0

                    CssColor.fromRGBA(red, green, blue, alpha)
                }
//                "hsl", "hsla" -> {
//                    val h: Int = parse_None_Double_Percent_numberArg(args[0])
//                    val s: Int = parse_None_Double_Percent_numberArg(args[1])
//                    val l: Int = parse_None_Double_Percent_numberArg(args[2])
//                    val a: Int = if (args.size == 4) parseAlpha(args[3]) else 0
//
//                    color.CssColor.fromHSLA(red, green, blue, alpha)
//                }
                else -> throw UNKNOWN_FUNCTION.get(name)
            }

        private fun parseNoneDoublePercentNumberArg(arg: NumberNode) = when (arg) {
            is NoneNode -> arg.compute().roundToInt()
            is DoubleNode -> arg.compute().roundToInt()
            is DoublePercentNode -> (arg.compute() * 2.55).roundToInt()
        }

//            private fun parseAngle(arg: NumberNode, percentCoefficient: Double = 1.0) = when (arg) {
//                is DoubleNode -> arg.compute().toInt()
//                is DoublePercentNode -> (arg.compute() * percentCoefficient).toInt()
//                else -> throw ALPHA_ARGUMENT_ERROR.get(arg.toString())
//            }

        private fun parseAlpha(arg: NumberNode) = when (arg) {
            is NoneNode -> arg.compute()
            is DoubleNode -> arg.compute()
            is DoublePercentNode -> arg.compute() * 0.01
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