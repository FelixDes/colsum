package translator

import CssColor
import java.math.BigDecimal
import kotlin.math.roundToInt

class SemanticException(message: String) : Exception(message)

interface Calculable<ResT> {
    operator fun plus(other: ResT): ResT
    operator fun minus(other: ResT): ResT
    operator fun div(other: ResT): ResT
    operator fun times(other: ResT): ResT
}

sealed class ASTNode<ResT> {
    abstract fun compute(): ResT

    data object EmptyNode : ASTNode<Unit>() {

        override fun compute() = Unit
    }

    class TokenNode(private val tokenType: TokenType) : ASTNode<TokenType>() {

        override fun compute() = tokenType
    }

    class LexemeNode(private val lexeme: String) : ASTNode<String>() {

        override fun compute() = lexeme
    }

//    companion object {
//        inline fun <reified ResT, reified ArgT> functionFactory(
//            name: String,
//            args: List<Int>
//        ): ASTNode<ResT> {
//            return when {
//                ResT::class == CssColor::class && ArgT::class == Int::class -> ColorFunctionNode(
//                    name,
//                    args
//                ) as ASTNode<ResT>
//
//                else -> throw Exception("Incorrect result/argument types for '$name' function")
//            }
//        }
//    }

    class CalculatingNode<ResT : Calculable<ResT>>(
        private val operation: TokenType,
        private val leftNode: ResT,
        private val rightNode: ResT,
    ) : ASTNode<ResT>() {
        override fun compute(): ResT {
            return when (operation) {
                TokenType.OPERATOR_PLUS -> leftNode + rightNode
                TokenType.OPERATOR_MINUS -> leftNode - rightNode
                TokenType.OPERATOR_MUL -> leftNode * rightNode
                TokenType.OPERATOR_DIV -> leftNode / rightNode
                else -> throw ParseException("Unknown operation: $operation")
            }
        }
    }

    sealed class NumberNode private constructor(
        protected val value: Double = 0.0
    ) : ASTNode<Double>(), Calculable<NumberNode> {

        override fun compute(): Double {
            return value
        }

        data object NoneNode : NumberNode(0.0) {
            override fun plus(other: NumberNode): NumberNode {
                throw SemanticException("Cannot do calculation on `none`")
            }

            override fun minus(other: NumberNode): NumberNode {
                throw SemanticException("Cannot do calculation on `none`")
            }

            override fun div(other: NumberNode): NumberNode {
                throw SemanticException("Cannot do calculation on `none`")
            }

            override fun times(other: NumberNode): NumberNode {
                throw SemanticException("Cannot do calculation on `none`")
            }
        }

        class DoubleNode(value: Double) : NumberNode(value) {

            override fun plus(other: NumberNode): NumberNode {
                if (other !is DoubleNode) throw SemanticException("Impossible cast")
                return DoubleNode(value + other.value)
            }

            override fun minus(other: NumberNode): NumberNode {
                if (other !is DoubleNode) throw SemanticException("Impossible cast")
                return DoubleNode(value - other.value)
            }

            override fun div(other: NumberNode): NumberNode {
                if (other !is DoubleNode) throw SemanticException("Impossible cast")
                return DoubleNode(value / other.value)
            }

            override fun times(other: NumberNode): NumberNode {
                if (other !is DoubleNode) throw SemanticException("Impossible cast")
                return DoubleNode(value * other.value)
            }
        }

//        class AngleNode(value: Double) : NumberNode(value) {
//
//            override fun plus(other: NumberNode): NumberNode {
//                if (other !is AngleNode) throw SemanticException("Impossible cast")
//                return AngleNode(value + other.value)
//            }
//
//            override fun minus(other: NumberNode): NumberNode {
//                if (other !is AngleNode) throw SemanticException("Impossible cast")
//                return AngleNode(value - other.value)
//            }
//
//            override fun div(other: NumberNode): NumberNode {
//                if (other !is AngleNode) throw SemanticException("Impossible cast")
//                return AngleNode(value / other.value)
//            }
//
//            override fun times(other: NumberNode): NumberNode {
//                if (other !is AngleNode) throw SemanticException("Impossible cast")
//                return AngleNode(value * other.value)
//            }
//        }

        class DoublePercentNode(value: Double) : NumberNode(value) {

            override fun plus(other: NumberNode): NumberNode {
                if (other !is DoublePercentNode) throw SemanticException("Impossible cast")
                return DoublePercentNode(value + other.value)
            }

            override fun minus(other: NumberNode): NumberNode {
                if (other !is DoublePercentNode) throw SemanticException("Impossible cast")
                return DoublePercentNode(value - other.value)
            }

            override fun div(other: NumberNode): NumberNode {
                if (other !is DoublePercentNode) throw SemanticException("Impossible cast")
                return DoublePercentNode(value / other.value)
            }

            override fun times(other: NumberNode): NumberNode {
                if (other !is DoublePercentNode) throw SemanticException("Impossible cast")
                return DoublePercentNode(value * other.value)
            }

        }

        companion object {
            fun buildNone(): NumberNode {
                return NoneNode
            }

            fun buildPercent(value: Double): NumberNode {
                return DoublePercentNode(value)
            }

            fun buildPercent(value: String): NumberNode {
                return buildPercent(BigDecimal(value.substringBefore('%')).toDouble())
            }

            fun buildNumber(value: Double): NumberNode {
                return DoubleNode(value)
            }

            fun buildNumber(value: String): NumberNode {
                return buildNumber(BigDecimal(value).toDouble())
            }

            fun buildSpecific(value: TokenType): NumberNode {
                return when (value) {
                    TokenType.NUMBER_EXP -> buildNumber(Math.E)
                    TokenType.NUMBER_PI -> buildNumber(Math.PI)
                    TokenType.NUMBER_NEG_INF -> buildNumber(Double.NEGATIVE_INFINITY)
                    TokenType.NUMBER_POS_INF -> buildNumber(Double.POSITIVE_INFINITY)
                    TokenType.NUMBER_NAN -> buildNumber(Double.NaN)
                    else -> throw SemanticException("Unknown specific number token: $value")
                }
            }
        }
    }


    // Parse percent/double
    data class ColorFunctionNode(
        private val name: String, private val args: List<NumberNode>
    ) : ASTNode<CssColor>() {
        override fun compute(): CssColor {
            return when (name) {
                "rgb", "rgba" -> {
                    val red: Int = parse_None_Double_Percent_numberArg(args[0])
                    val green: Int = parse_None_Double_Percent_numberArg(args[1])
                    val blue: Int = parse_None_Double_Percent_numberArg(args[2])
                    val alpha: Double = if (args.size == 4) parseAlpha(args[3]) else 0.0

                    CssColor.fromRGBA(red, green, blue, alpha)
                }
//                "hsl", "hsla" -> {
//                    val h: Int = parse_None_Double_Percent_numberArg(args[0])
//                    val s: Int = parse_None_Double_Percent_numberArg(args[1])
//                    val l: Int = parse_None_Double_Percent_numberArg(args[2])
//                    val a: Int = if (args.size == 4) parseAlpha(args[3]) else 0
//
//                    CssColor.fromHSLA(red, green, blue, alpha)
//                }
                else -> throw SemanticException("Unknown function: $name")
            }
        }

        companion object {
            private fun parse_None_Double_Percent_numberArg(arg: NumberNode) =
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
    }

    class FunctionRepresentationNode<FunctionArgT>(private val rep: FunctionRepresentation<FunctionArgT>) :
        ASTNode<FunctionRepresentationNode.FunctionRepresentation<FunctionArgT>>() {
        data class FunctionRepresentation<ResT>(
            val name: String,
            val argNodes: List<ResT>,
            val posOffset: Int
        )

        override fun compute(): FunctionRepresentation<FunctionArgT> {
            return rep
        }
    }

    // READY
    data class HexColorNode(private val hex: String) : ASTNode<CssColor>() {
        override fun compute(): CssColor = CssColor.fromHEX(hex.substring(1))
    }
}