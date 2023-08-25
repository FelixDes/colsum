package translator

import ASTNode
import ASTNode.*
import ASTNode.NumberNode.DoubleNode
import ASTNode.NumberNode.PercentDoubleNode
import Calculable

open class ParseException(message: String) : Exception(message)

data class ParseResult<T : ASTNode<*>>(
    val code: ResultCode = ResultCode.OK, val posOffset: Int, val nodeList: List<T>
) {
    constructor(posOffset: Int, nodeList: List<T>) : this(ResultCode.OK, posOffset, nodeList)

    enum class ResultCode { OK, FAIL }

}

interface Parsable<ResT : ASTNode<*>> {

    @Throws(ParseException::class)
    fun consume(pos: Int): ParseResult<ResT>
}

interface FailSafeParsable<ResT : ASTNode<*>> {

    @Throws(ParseException::class)
    fun consumeFailSafe(pos: Int): ParseResult<ResT>
}


sealed class Parser<ResT : ASTNode<*>>(protected val tokens: List<Pair<TokenType, String>>) : Parsable<ResT> {

    override fun consume(pos: Int): ParseResult<ResT> {
//        println("trying $this from $pos")
        validate(pos)
        return consumeDelegate(pos)
    }

    private fun validate(pos: Int) {
        if (pos !in tokens.indices) {
            throw ParseException("Invalid position: $pos")
        }
    }

    abstract fun consumeDelegate(pos: Int): ParseResult<ResT>

    data object EmptyParser : Parser<EmptyNode>(tokens = listOf()) {
        override fun consumeDelegate(pos: Int): ParseResult<EmptyNode> {
            return ParseResult(0, listOf(EmptyNode))
        }
    }

    // READY TESTED
    class SingleTokenParser(
        tokens: List<Pair<TokenType, String>>, private val tokenType: TokenType
    ) : Parser<TokenNode>(tokens) {
        override fun consumeDelegate(pos: Int): ParseResult<TokenNode> {
            if (tokens[pos].first == tokenType) {
                return ParseResult(1, listOf(TokenNode(tokenType)))
            }
            throw ParseException("No $tokenType token at pos: $pos")
        }
    }

    // READY TESTED
    class LexemeByTokenParser(
        tokens: List<Pair<TokenType, String>>, private val tokenType: TokenType
    ) : Parser<LexemeNode>(tokens) {
        override fun consumeDelegate(pos: Int): ParseResult<LexemeNode> {
            if (tokens[pos].first == tokenType) {
                return ParseResult(1, listOf(LexemeNode(tokens[pos].second)))
            }
            throw ParseException("No $tokenType token at pos: $pos")
        }
    }

    // READY TESTED
    class HexColorParser(
        tokens: List<Pair<TokenType, String>>,
    ) : Parser<HexColorNode>(tokens) {
        override fun consumeDelegate(pos: Int): ParseResult<HexColorNode> {
            if (tokens[pos].first == TokenType.HEX_COLOR) {
                return ParseResult(1, listOf(HexColorNode(tokens[pos].second)))
            }
            throw ParseException("No CssColor at pos: $pos")
        }
    }

    // READY TESTED
    open class DoubleParser(
        tokens: List<Pair<TokenType, String>>,
    ) : Parser<DoubleNode>(tokens) {
        override fun consumeDelegate(pos: Int): ParseResult<DoubleNode> {
            if (tokens[pos].first == TokenType.NUMBER) {
                return ParseResult(1, listOf(DoubleNode(tokens[pos].second)))
            }
            throw ParseException("No number at pos: $pos")
        }
    }

    // READY TESTED
    class NoneParser(
        tokens: List<Pair<TokenType, String>>,
    ) : Parser<NumberNode.NoneNode>(tokens) {
        override fun consumeDelegate(pos: Int): ParseResult<NumberNode.NoneNode> {
            if (tokens[pos].first == TokenType.NUMBER_NONE) {
                return ParseResult(1, listOf(NumberNode.NoneNode))
            }
            throw ParseException("No `none` at pos: $pos")
        }
    }

    // READY TESTED
    class PercentDoubleParser(
        tokens: List<Pair<TokenType, String>>,
    ) : Parser<PercentDoubleNode>(tokens) {
        override fun consumeDelegate(pos: Int): ParseResult<PercentDoubleNode> {
            if (tokens[pos].first == TokenType.NUMBER_PERCENT) {
                return ParseResult(
                    1, listOf(PercentDoubleNode(tokens[pos].second))
                )
            }
            throw ParseException("No percentage number at pos: $pos")
        }
    }

    // READY TESTED
    class AlternativeParser<ResT : ASTNode<*>>(
        tokens: List<Pair<TokenType, String>>, private val parsers: List<Parsable<ResT>>
    ) : Parser<ResT>(tokens), FailSafeParsable<ResT> {
        override fun consumeDelegate(pos: Int): ParseResult<ResT> {
            for (parser in parsers) {
//                println("~~~~ALT~~~~ $parser")
                var r: ParseResult<ResT>? = null
                try {
                    r = parser.consume(pos)
                } catch (_: ParseException) {
                }
                if (r != null) {
                    return r
                }
            }
            throw ParseException("'AlternativeParser': No parsers: $parsers can be applied to pos: $pos")
        }

        override fun consumeFailSafe(pos: Int): ParseResult<ResT> {
            return try {
                consumeDelegate(pos)
            } catch (_: ParseException) {
                ParseResult(ParseResult.ResultCode.FAIL, 0, listOf())
            }
        }
    }

    // READY TESTED
    class WhileSeparatorParser<ResT : ASTNode<*>>(
        tokens: List<Pair<TokenType, String>>, private val parser: Parsable<ResT>, private val separator: Parsable<*>
    ) : Parser<ResT>(tokens) {
        override fun consumeDelegate(pos: Int): ParseResult<ResT> {
            var localPos = 0
            val nodeList = ArrayList<ResT>()

            var firstVisited = false
            while (true) {
                try {
                    val argResult: ParseResult<ResT> = parser.consume(pos + localPos)
                    localPos += argResult.posOffset
                    nodeList.addAll(argResult.nodeList)
                } catch (_: ParseException) {
                    if (!firstVisited) {
                        return ParseResult(localPos, nodeList)
                    } else {
                        throw ParseException("'WhileSeparatorParser': Failed arg consume after separator at pos: ${pos + localPos}")
                    }
                }
                firstVisited = true

                try {
                    val sepResult = separator.consume(pos + localPos)
                    localPos += sepResult.posOffset
                } catch (_: ParseException) {
                    return ParseResult(localPos, nodeList)
                }
            }
        }
    }

//    class SequenceParser<ResT : ASTNode<*>>(
//        tokens: List<Pair<TokenType, String>>,
//        private val parserSequence: List<Parsable<*>>,
//        private val nodeFilterPredicate: (node: ASTNode<*>) -> Boolean
//    ) : Parser<ResT>(tokens) {
//
//        constructor(
//            tokens: List<Pair<TokenType, String>>,
//            parserSequence: List<Parsable<*>>
//        ) : this(tokens, parserSequence, { true })
//
//        override fun consumeDelegate(pos: Int): ParseResult<ResT> {
//            var localPos = 0
//            val nodeList = ArrayList<ASTNode<*>>()
//
//            for (parser in parserSequence) {
//                val parserRes = parser.consume(pos + localPos)
//                localPos += parserRes.posOffset
//                nodeList.addAll(parserRes.nodeList.filter { n -> nodeFilterPredicate(n) })
//            }
//
//            return ParseResult(localPos, nodeList as List<ResT>)
//        }
//    }


    // READY TESTED
    class ParenthesisWrapperParser<ResT : ASTNode<*>>(
        tokens: List<Pair<TokenType, String>>, private val innerParser: Parsable<ResT>
    ) : Parser<ResT>(tokens) {
        override fun consumeDelegate(pos: Int): ParseResult<ResT> {
            var localPos = 0

            val openParenthesisResult = SingleTokenParser(
                tokens, TokenType.PARENTHESIS_OPEN
            ).consume(pos + localPos)
            localPos += openParenthesisResult.posOffset // скобка

            val innerParserResult = innerParser.consume(pos + localPos)
            val resultNodes = innerParserResult.nodeList
            localPos += innerParserResult.posOffset

            val closeParenthesisResult = SingleTokenParser(
                tokens, TokenType.PARENTHESIS_CLOSE
            ).consume(pos + localPos)
            localPos += closeParenthesisResult.posOffset // скобка

            return ParseResult(localPos, resultNodes)
        }
    }

    //
    internal sealed class FunctionParser<ResT : ASTNode<*>, ArgT : ASTNode<*>>(
        tokens: List<Pair<TokenType, String>>, private val separatorParsers: List<Parser<*>>
    ) : Parser<ResT>(tokens) {
        abstract class ArgParser<ResT : ASTNode<*>>(tokens: List<Pair<TokenType, String>>) : Parser<ResT>(tokens)

        internal fun getArgs(pos: Int, argParser: ArgParser<ArgT>): Triple<String, List<ArgT>, Int> {
            var localPos = 0

            val functionNameRes = LexemeByTokenParser(tokens, TokenType.FUN_NAME).consume(pos + localPos)
            localPos += functionNameRes.posOffset
            val name: String = functionNameRes.nodeList[0].compute()

            val parsers = separatorParsers.map {
                WhileSeparatorParser(
                    tokens, argParser, it
                )
            }

            val argsResult: ParseResult<ArgT> =
                ParenthesisWrapperParser(tokens, AlternativeParser(tokens, parsers)).consume(pos + localPos)
            localPos += argsResult.posOffset

            val argNodes: List<ArgT> = argsResult.nodeList

            return Triple(name, argNodes, localPos)
        }

        sealed class ColorFunction(tokens: List<Pair<TokenType, String>>) :
            FunctionParser<ColorFunctionNode, DoubleNode>(
                tokens, listOf(
                    SingleTokenParser(tokens, TokenType.SEPARATOR), EmptyParser
                )
            ) {

            protected abstract val argParser: Parsable<DoubleNode>

            class ColorFunctionWithNumberArgs(tokens: List<Pair<TokenType, String>>) : ColorFunction(tokens) {
                override val argParser: Parsable<DoubleNode>
                    get() = AlternativeParser<DoubleNode>(
                        tokens, listOf(
//                            NoneParser(tokens),
                            ExpressionParser(tokens, DoubleParser(tokens))
                        )
                    )
            }

//            class ColorFunctionWithPercentArgs(
//                tokens: List<Pair<TokenType, String>>
//            ) : ColorFunction(
//                tokens
//            )

            class ColorFunctionArgParser(tokens: List<Pair<TokenType, String>>, argParser: Parsable<DoubleNode>) :
                ArgParser<DoubleNode>(tokens) {
                override fun consumeDelegate(pos: Int): ParseResult<DoubleNode> {
                    TODO("Not yet implemented")
                }
            }

            override fun consumeDelegate(pos: Int): ParseResult<ColorFunctionNode> {
                val (name, args, localPos) = this.getArgs(pos, ColorFunctionArgParser(tokens, this.argParser))
                val func = ColorFunctionNode(name, args)
                return ParseResult(localPos, listOf(func))
            }
        }
    }

    class ExpressionParser<ResT>(
        tokens: List<Pair<TokenType, String>>,
        private val structuralNodeParser: Parsable<ResT>
    ) : Parser<ResT>(tokens) where ResT : ASTNode<*>, ResT : Calculable<ResT> {
        internal inner class FactorParser(
            tokens: List<Pair<TokenType, String>>
        ) : Parser<ResT>(tokens) {
            override fun consumeDelegate(pos: Int): ParseResult<ResT> {
                val res = AlternativeParser(
                    tokens, listOf(
                        structuralNodeParser,
                        ParenthesisWrapperParser(tokens, ExpressionParser(tokens, structuralNodeParser))
                    )
                ).consume(pos)

                return ParseResult(res.posOffset, res.nodeList)
            }
        }

        internal inner class TermParser(
            tokens: List<Pair<TokenType, String>>
        ) : Parser<ResT>(tokens) {
            override fun consumeDelegate(pos: Int): ParseResult<ResT> {
                val termRes = FactorParser(tokens).consume(pos)
                var localPos = termRes.posOffset
                var termNode = termRes.nodeList[0]

                while (true) {
                    val opRes = AlternativeParser(
                        tokens, listOf(
                            SingleTokenParser(tokens, TokenType.OPERATOR_MUL),
                            SingleTokenParser(tokens, TokenType.OPERATOR_DIV),
                        )
                    ).consumeFailSafe(pos + localPos)
                    if (opRes.code == ParseResult.ResultCode.FAIL) {
                        return ParseResult(localPos, listOf(termNode))
                    }
                    localPos += opRes.posOffset
                    val newTermRes = FactorParser(tokens).consume(pos + localPos)
                    localPos += newTermRes.posOffset
                    val newTermNode = newTermRes.nodeList[0]
                    termNode = CalculatingNode(
                        opRes.nodeList[0].compute(),
                        termNode,
                        newTermNode
                    ).compute()
                }
            }
        }

        override fun consumeDelegate(pos: Int): ParseResult<ResT> {
            val termRes = TermParser(tokens).consume(pos)
            var localPos = termRes.posOffset
            var termNode = termRes.nodeList[0]

            while (true) {
                val opRes = AlternativeParser(
                    tokens, listOf(
                        SingleTokenParser(tokens, TokenType.OPERATOR_PLUS),
                        SingleTokenParser(tokens, TokenType.OPERATOR_MINUS),
                    )
                ).consumeFailSafe(pos + localPos)
                if (opRes.code == ParseResult.ResultCode.FAIL) {
                    return ParseResult(localPos, listOf(termNode))
                }
                localPos += opRes.posOffset
                val newTermRes = TermParser(tokens).consume(pos + localPos)
                localPos += newTermRes.posOffset
                termNode = CalculatingNode(
                    opRes.nodeList[0].compute(),
                    termNode!!,
                    newTermRes.nodeList[0]
                ).compute()
            }
        }
    }
}

