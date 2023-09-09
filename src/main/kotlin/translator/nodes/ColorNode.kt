package translator.nodes

import color.CssColor
import kotlin.math.roundToInt

class ColorNode private constructor(private var color: Lazy<CssColor>) : ASTNode<CssColor>(),
    Calculable<ColorNode> {
    override fun compute(): CssColor = color.value

    companion object {
        fun nodeForHex(hex: String) = ColorNode(lazy { CssColor.fromHEX(hex) })
        fun nodeForConst(const: String) = ColorNode(lazy { CssColor.fromConstant(const) })
        fun nodeForFunction(name: String, args: List<NumberNode>) = ColorNode(lazy { fromFunction(name, args) })

        private fun fromFunction(name: String, args: List<NumberNode>): CssColor {
            return when (name) {
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
                else -> throw SemanticException("Unknown function: $name")
            }
        }

        private fun parseNoneDoublePercentNumberArg(arg: NumberNode) =
            when (arg) {
                is NumberNode.NoneNode -> arg.compute().roundToInt()
                is NumberNode.DoubleNode -> arg.compute().roundToInt()
                is NumberNode.DoublePercentNode -> (arg.compute() * 2.55).roundToInt()
            }

//            private fun parseAngle(arg: NumberNode, percentCoefficient: Double = 1.0) = when (arg) {
//                is NumberNode.DoubleNode -> arg.compute().toInt()
//                is NumberNode.DoublePercentNode -> (arg.compute() * percentCoefficient).toInt()
//                else -> throw SemanticException("Alpha argument error: $arg")
//            }

        private fun parseAlpha(arg: NumberNode) = when (arg) {
            is NumberNode.DoubleNode -> arg.compute()
            is NumberNode.DoublePercentNode -> arg.compute() * 0.01
            else -> throw SemanticException("Alpha argument error: $arg")
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