package translator

import ASTNode
import ASTNode.*
import Calculable
import HexColorNode

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
    open class NumberParser(
        tokens: List<Pair<TokenType, String>>,
    ) : Parser<NumberNode>(tokens) {
        override fun consumeDelegate(pos: Int): ParseResult<NumberNode> {
            if (tokens[pos].first == TokenType.NUMBER) {
                return ParseResult(1, listOf(NumberNode.buildNumber(tokens[pos].second)))
            } else if (tokens[pos].first == TokenType.NUMBER_PERCENT) {
                return ParseResult(1, listOf(NumberNode.buildPercent(tokens[pos].second)))
            }
            throw ParseException("No number at pos: $pos")
        }
    }

    // READY TESTED
    class NoneParser(
        tokens: List<Pair<TokenType, String>>,
    ) : Parser<NumberNode>(tokens) {
        override fun consumeDelegate(pos: Int): ParseResult<NumberNode> {
            if (tokens[pos].first == TokenType.NUMBER_NONE) {
                return ParseResult(1, listOf(NumberNode.buildNone()))
            }
            throw ParseException("No `none` at pos: $pos")
        }
    }

    // READY TESTED
    class AlternativeParser<ResT : ASTNode<*>>(
        tokens: List<Pair<TokenType, String>>, private val parsers: List<Parsable<ResT>>
    ) : Parser<ResT>(tokens), FailSafeParsable<ResT> {
        override fun consumeDelegate(pos: Int): ParseResult<ResT> {
            for (parser in parsers) {
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

    class ArgSeparatorListParser<ResT : ASTNode<*>>(
        tokens: List<Pair<TokenType, String>>,
        private val parser: Parsable<ResT>,
        private val separators: List<Parsable<*>>
    ) : Parser<ResT>(tokens) {
        override fun consumeDelegate(pos: Int): ParseResult<ResT> {
            var localPos = 0
            val nodeList = ArrayList<ResT>()

            val firstArgRes: ParseResult<ResT> = parser.consume(pos + localPos)
            localPos += firstArgRes.posOffset
            nodeList.addAll(firstArgRes.nodeList)

            for (sep in separators) {
                val sepResult = sep.consume(pos + localPos)
                localPos += sepResult.posOffset

                val argResult: ParseResult<ResT> = parser.consume(pos + localPos)
                localPos += argResult.posOffset
                nodeList.addAll(argResult.nodeList)
            }

            return ParseResult(localPos, nodeList)
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
        tokens: List<Pair<TokenType, String>>, private val separatorList: List<Parsable<*>>
    ) : Parser<ResT>(tokens) {
        internal fun getArgs(pos: Int, argParser: Parsable<ArgT>): Triple<String, List<ArgT>, Int> {
            var localPos = 0

            val functionNameRes = LexemeByTokenParser(tokens, TokenType.FUN_NAME).consume(pos + localPos)
            localPos += functionNameRes.posOffset
            val name: String = functionNameRes.nodeList[0].compute()

            val argsResult: ParseResult<ArgT> = ParenthesisWrapperParser(
                tokens, ArgSeparatorListParser(
                    tokens, argParser, separatorList
                )
            ).consume(pos + localPos)
            localPos += argsResult.posOffset

            val argNodes: List<ArgT> = argsResult.nodeList

            return Triple(name, argNodes, localPos)
        }

        // READY TESTED
        class CalcFunctionParser(tokens: List<Pair<TokenType, String>>) : FunctionParser<NumberNode, NumberNode>(
            tokens, listOf()
        ) {
            override fun consumeDelegate(pos: Int): ParseResult<NumberNode> {
                val (name, args, localPos) = getArgs(pos, ExpressionParser(tokens, NumberParser(tokens)))
                if (name == "calc") {
                    return ParseResult(localPos, args)
                } else {
                    throw ParseException("`calc` function expected but was `$name`")
                }
            }
        }

//        sealed class ColorFunction(tokens: List<Pair<TokenType, String>>) :
//            FunctionParser<ColorFunctionNode, NumberNode>(
//                tokens, listOf(
//                    SingleTokenParser(tokens, TokenType.SEPARATOR), EmptyParser
//                )
//            ) {
//
//            protected abstract val argParser: Parsable<NumberNode>
//
//            class ColorFunctionArgs(tokens: List<Pair<TokenType, String>>) : ColorFunction(tokens) {
//                override val argParser: Parsable<NumberNode>
//                    get() = AlternativeParser(
//                        tokens, listOf(
//                            NoneParser(tokens) as Parsable<NumberNode>,
//                            ExpressionParser(tokens, DoubleParser(tokens)) as Parsable<NumberNode>
//                        )
//                    )
//            }
//
////            class ColorFunctionWithPercentArgs(
////                tokens: List<Pair<TokenType, String>>
////            ) : ColorFunction(
////                tokens
////            )
//
//            class ColorFunctionArgParser(tokens: List<Pair<TokenType, String>>, argParser: Parsable<NumberNode>) :
//                ArgParser<NumberNode>(tokens) {
//                override fun consumeDelegate(pos: Int): ParseResult<NumberNode> {
//                    TODO("Not yet implemented")
//                }
//            }
//
//            override fun consumeDelegate(pos: Int): ParseResult<ColorFunctionNode> {
//                val (name, args, localPos) = this.getArgs(pos, ColorFunctionArgParser(tokens, this.argParser))
//                val func = ColorFunctionNode(name, args)
//                return ParseResult(localPos, listOf(func))
//            }
//        }
    }

    class ExpressionParser<ResT>(
        tokens: List<Pair<TokenType, String>>, private val structuralNodeParser: Parsable<ResT>
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
                        opRes.nodeList[0].compute(), termNode, newTermNode
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
                    opRes.nodeList[0].compute(), termNode!!, newTermRes.nodeList[0]
                ).compute()
            }
        }
    }
}

