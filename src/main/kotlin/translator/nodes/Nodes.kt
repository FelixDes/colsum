package translator.nodes

import CssColor
import translator.parser.ParseException
import translator.tokenization.TokenType
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

    class ColorNode private constructor(private var color: Lazy<CssColor>) : ASTNode<CssColor>(),
        Calculable<ColorNode> {

        constructor(name: String, args: List<NumberNode>) : this(lazy { fromFunction(name, args) })
        constructor(hex: String) : this(lazy { fromHex(hex) })

        override fun compute(): CssColor {
            return color.value
        }

        companion object {
            private fun fromHex(hex: String) = CssColor.fromHEX(hex)

            private fun fromFunction(name: String, args: List<NumberNode>): CssColor {
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
}