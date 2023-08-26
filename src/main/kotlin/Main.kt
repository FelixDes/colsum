import translator.ParseException
import translator.Token
import translator.TokenType
import translator.tokenizer
import java.math.BigDecimal

//data class ParseResult<T : ASTNode<T>> private constructor(
//    val resultCode: ResultCode,
//    val posOffset: Int,
//    val nodeList: List<T>
//) {
//    enum class ResultCode { OK, FAIL }
//
//    companion object {
//        fun <T : ASTNode<T>> ok(posOffset: Int, nodeList: List<T>): ParseResult<T> =
//            ParseResult(ResultCode.OK, posOffset, nodeList)
//
////        fun <T : ASTNode<T>> fail(): ParseResult<T> = ParseResult(ResultCode.FAIL, 0, listOf())
//    }
//}

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
        protected val value: Double = 0.0,
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

        class DoublePercentNode(value: Double) : NumberNode(value) {

            override fun plus(other: NumberNode): NumberNode {
                if (other !is DoublePercentNode) throw SemanticException("Impossible cast")
                return DoublePercentNode(value + other.value)
            }

            override fun minus(other: NumberNode): NumberNode {
                if (other !is DoublePercentNode) throw SemanticException("Impossible cast")
                return DoublePercentNode(value + other.value)
            }

            override fun div(other: NumberNode): NumberNode {
                if (other !is DoublePercentNode) throw SemanticException("Impossible cast")
                return DoublePercentNode(value + other.value)
            }

            override fun times(other: NumberNode): NumberNode {
                if (other !is DoublePercentNode) throw SemanticException("Impossible cast")
                return DoublePercentNode(value + other.value)
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
        }
    }
}

// USABLE (only rgb/rgba)
data class ColorFunctionNode(
    private val name: String, private val args: List<NumberNode>
) : ASTNode<CssColor>() {
    override fun compute(): CssColor = getParseFunctionForName(name)(args.map { it.compute() })

    private fun getParseFunctionForName(name: String): (List<Double>) -> CssColor {
        val functions = mutableMapOf<String, (List<Double>) -> CssColor>(
            "rgb" to CssColor::fromRGBList, "rgba" to CssColor::fromRGBList
            // ...
        )

        if (name in functions) {
            return functions[name]!!
        } else throw ParseException("In function with name: $name")
    }
}

// READY
data class HexColorNode(private val hex: String) : ASTNode<CssColor>() {
    override fun compute(): CssColor = CssColor.fromHEX(hex.substring(1))
}


fun analyser(tokens: List<Pair<Token, String>>) {
//    val r = Parser.RootParser<>(tokens)
}

fun main(args: Array<String>) {
//    val test_string = "rgba(123, 3%, 2, 1.3/4) + hsl(123 2 * 2 4)-#009900"
    val test_string = "(((1*3+3/1)*8-(4+5*7)))"
    val tokens = tokenizer(test_string)


//    analyser(tokens)
}
