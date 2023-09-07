package translator.parser

import translator.nodes.ASTNode.*
import translator.tokenization.TokenType.*
import translator.nodes.ASTNode
import translator.nodes.Calculable
import translator.nodes.ColorNode
import translator.nodes.NumberNode
import translator.tokenization.TokenType

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
        validate(pos)
        return consumeDelegate(pos)
    }

    private fun validate(pos: Int) {
        if (pos !in tokens.indices) {
            throw ParseException("Invalid position: $pos")
        }
    }

    abstract fun consumeDelegate(pos: Int): ParseResult<ResT>

    // READY TESTED
    data object EmptyParser : Parser<EmptyNode>(tokens = listOf()) {
        override fun consume(pos: Int): ParseResult<EmptyNode> {
            return consumeDelegate(pos)
        }

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
    private class HexColorParser(
        tokens: List<Pair<TokenType, String>>,
    ) : Parser<ColorNode>(tokens) {
        override fun consumeDelegate(pos: Int): ParseResult<ColorNode> {
            if (tokens[pos].first == HEX_COLOR) {
                return ParseResult(1, listOf(ColorNode(tokens[pos].second)))
            }
            throw ParseException("No CssColor at pos: $pos")
        }
    }

    class ColorParser(
        tokens: List<Pair<TokenType, String>>,
    ) : Parser<ColorNode>(tokens) {
        override fun consumeDelegate(pos: Int): ParseResult<ColorNode> {
            return AlternativeParser(tokens, listOf(
                HexColorParser(tokens) as Parsable<ColorNode>,
                FunctionParser.ColorFunctionParser(tokens)
            )).consume(pos)
        }
    }

    // READY TESTED
    open class NumberParser(
        tokens: List<Pair<TokenType, String>>,
    ) : Parser<NumberNode>(tokens) {
        override fun consumeDelegate(pos: Int): ParseResult<NumberNode> {
            return when (tokens[pos].first) {
                NUMBER -> ParseResult(1, listOf(NumberNode.buildNumber(tokens[pos].second)))

                NUMBER_PERCENT -> ParseResult(1, listOf(NumberNode.buildPercent(tokens[pos].second)))

                in setOf(NUMBER_EXP, NUMBER_PI, NUMBER_NEG_INF, NUMBER_POS_INF, NUMBER_NAN) ->
                    ParseResult(1, listOf(NumberNode.buildSpecific(tokens[pos].first)))

                else -> throw ParseException("No number at pos: $pos")
            }
        }
    }

    // READY TESTED
    class NoneParser(
        tokens: List<Pair<TokenType, String>>,
    ) : Parser<NumberNode>(tokens) {
        override fun consumeDelegate(pos: Int): ParseResult<NumberNode> {
            if (tokens[pos].first == NUMBER_NONE) {
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

    // READY TESTED
    class ParenthesisWrapperParser<ResT : ASTNode<*>>(
        tokens: List<Pair<TokenType, String>>, private val innerParser: Parsable<ResT>
    ) : Parser<ResT>(tokens) {
        override fun consumeDelegate(pos: Int): ParseResult<ResT> {
            var localPos = 0

            val openParenthesisResult = SingleTokenParser(
                tokens, PARENTHESIS_OPEN
            ).consume(pos + localPos)
            localPos += openParenthesisResult.posOffset

            val innerParserResult = innerParser.consume(pos + localPos)
            val resultNodes = innerParserResult.nodeList
            localPos += innerParserResult.posOffset

            val closeParenthesisResult = SingleTokenParser(
                tokens, PARENTHESIS_CLOSE
            ).consume(pos + localPos)
            localPos += closeParenthesisResult.posOffset

            return ParseResult(localPos, resultNodes)
        }
    }

    //
    internal sealed class FunctionParser<ResT : ASTNode<*>, ArgT : ASTNode<*>>(
        tokens: List<Pair<TokenType, String>>
    ) : Parser<ResT>(tokens) {

        internal class ArgsParser<FuncArgT : ASTNode<*>>(
            tokens: List<Pair<TokenType, String>>,
            private val argParser: Parsable<FuncArgT>,
            private val separatorList: List<Parsable<*>>
        ) : Parser<FunctionRepresentationNode<FuncArgT>>(tokens) {
            override fun consumeDelegate(pos: Int): ParseResult<FunctionRepresentationNode<FuncArgT>> {
                val functionNameRes = LexemeByTokenParser(tokens, FUN_NAME).consume(pos)
                var localPos = functionNameRes.posOffset
                val name: String = functionNameRes.nodeList[0].compute()

                val argsResult: ParseResult<FuncArgT> = ParenthesisWrapperParser(
                    tokens, ArgSeparatorListParser(
                        tokens, argParser, separatorList
                    )
                ).consume(pos + localPos)
                localPos += argsResult.posOffset

                val argNodes: List<FuncArgT> = argsResult.nodeList

                return ParseResult(
                    localPos, listOf(
                        FunctionRepresentationNode(
                            FunctionRepresentationNode.FunctionRepresentation(
                                name,
                                argNodes,
                                localPos
                            )
                        )
                    )
                )
            }
        }

        // READY TESTED
        class CalcFunctionParser(tokens: List<Pair<TokenType, String>>) : FunctionParser<NumberNode, NumberNode>(
            tokens
        ) {
            override fun consumeDelegate(pos: Int): ParseResult<NumberNode> {
                val rep = ArgsParser(
                    tokens,
                    ExpressionParser(
                        tokens, AlternativeParser(
                            tokens, listOf(
                                CalcFunctionParser(tokens),
                                NumberParser(tokens),
                            )
                        )
                    ),
                    listOf()
                ).consume(pos).nodeList[0].compute()
                if (rep.name == "calc") {
                    return ParseResult(rep.posOffset, rep.argNodes)
                } else {
                    throw ParseException("`calc` function expected but was `$rep.name`")
                }
            }
        }

        // HSL - 0.3turn?
        class ColorFunctionParser(tokens: List<Pair<TokenType, String>>) :
            FunctionParser<ColorNode, NumberNode>(tokens) {

            override fun consumeDelegate(pos: Int): ParseResult<ColorNode> {
                val possibleSeparatorPatterns = listOf(
                    listOf(
                        SingleTokenParser(tokens, COMMA_SEPARATOR),
                        SingleTokenParser(tokens, COMMA_SEPARATOR),
                        SingleTokenParser(tokens, COMMA_SEPARATOR),
                    ),
                    listOf(
                        EmptyParser,
                        EmptyParser,
                        SingleTokenParser(tokens, SLASH_SEPARATOR)
                    ),
                    listOf(
                        SingleTokenParser(tokens, COMMA_SEPARATOR),
                        SingleTokenParser(tokens, COMMA_SEPARATOR),
                    ),
                    listOf(
                        EmptyParser,
                        EmptyParser
                    ),
                )
                val funcArgParsers = possibleSeparatorPatterns.map {
                    ArgsParser(
                        tokens,
                        AlternativeParser(
                            tokens, listOf(
                                NumberParser(tokens),
                                NoneParser(tokens),
                                CalcFunctionParser(tokens),
                            )
                        ),
                        it
                    )
                }

                val rep = AlternativeParser(
                    tokens,
                    funcArgParsers
                ).consume(pos).nodeList[0].compute()

                val func = ColorNode(rep.name, rep.argNodes)
                return ParseResult(rep.posOffset, listOf(func))
            }
        }
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
                            SingleTokenParser(tokens, OPERATOR_MUL),
                            SingleTokenParser(tokens, OPERATOR_DIV),
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
                        SingleTokenParser(tokens, OPERATOR_PLUS),
                        SingleTokenParser(tokens, OPERATOR_MINUS),
                    )
                ).consumeFailSafe(pos + localPos)
                if (opRes.code == ParseResult.ResultCode.FAIL) {
                    return ParseResult(localPos, listOf(termNode))
                }
                localPos += opRes.posOffset
                val newTermRes = TermParser(tokens).consume(pos + localPos)
                localPos += newTermRes.posOffset
                termNode = CalculatingNode(
                    opRes.nodeList[0].compute(), termNode, newTermRes.nodeList[0]
                ).compute()
            }
        }
    }
}

