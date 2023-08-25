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

    class CalculatingNode<ResT : Calculable<*>>(
        private val operation: TokenType,
        private val leftNode: ResT,
        private val rightNode: ResT,
    ) : ASTNode<ResT>() {
        override fun compute(): ResT {
            return when (operation) {
                TokenType.OPERATOR_PLUS -> leftNode as Calculable<ResT> + rightNode
                TokenType.OPERATOR_MINUS -> leftNode as Calculable<ResT> - rightNode
                TokenType.OPERATOR_MUL -> leftNode as Calculable<ResT> * rightNode
                TokenType.OPERATOR_DIV -> leftNode as Calculable<ResT> / rightNode
                else -> throw ParseException("Unknown operation: $operation")
            }
        }
    }

    sealed class NumberNode : ASTNode<Double>() {
        data object NoneNode : NumberNode(), Calculable<NoneNode> {
            override fun compute(): Double {
                return 0.0
            }

            override fun plus(other: NoneNode): NoneNode {
                throw SemanticException("Cannot do calculation on `none`")
            }

            override fun minus(other: NoneNode): NoneNode {
                throw SemanticException("Cannot do calculation on `none`")
            }

            override fun div(other: NoneNode): NoneNode {
                throw SemanticException("Cannot do calculation on `none`")
            }

            override fun times(other: NoneNode): NoneNode {
                throw SemanticException("Cannot do calculation on `none`")
            }
        }

        data class DoubleNode(private val value: Double) : NumberNode(), Calculable<DoubleNode> {

            constructor(value: String) : this(BigDecimal(value).toDouble())

            override fun compute(): Double {
                return value
            }

            override fun plus(other: DoubleNode): DoubleNode {
                return DoubleNode(this.value + other.value)
            }

            override fun minus(other: DoubleNode): DoubleNode {
                return DoubleNode(this.value - other.value)
            }

            override fun times(other: DoubleNode): DoubleNode {
                return DoubleNode(this.value * other.value)
            }

            override fun div(other: DoubleNode): DoubleNode {
                return DoubleNode(this.value / other.value)
            }
        }

        data class PercentDoubleNode(private val value: Double) : NumberNode(), Calculable<PercentDoubleNode> {
            constructor(value: String) : this(BigDecimal(value.substring(0, value.length - 1)).toDouble())

            override fun compute(): Double {
                return value
            }

            override fun plus(other: PercentDoubleNode): PercentDoubleNode {
                return PercentDoubleNode(this.value + other.value)
            }

            override fun minus(other: PercentDoubleNode): PercentDoubleNode {
                return PercentDoubleNode(this.value - other.value)
            }

            override fun times(other: PercentDoubleNode): PercentDoubleNode {
                return PercentDoubleNode(this.value * other.value)
            }

            override fun div(other: PercentDoubleNode): PercentDoubleNode {
                return PercentDoubleNode(this.value / other.value)
            }
        }
    }

    // USABLE (only rgb/rgba)
    data class ColorFunctionNode(
        private val name: String, private val args: List<NumberNode.DoubleNode>
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
