package translator.parser

import translator.nodes.ASTNode
import translator.nodes.ColorNode
import translator.nodes.NumberNode
import translator.tokenization.TokenType
import translator.tokenization.TokenType.*

internal sealed class FunctionParser<ResT : ASTNode<*>, ArgT : ASTNode<*>>(
    tokens: List<Pair<TokenType, String>>
) : Parser<ResT>(tokens) {

    internal class ArgsParser<FuncArgT : ASTNode<*>>(
        tokens: List<Pair<TokenType, String>>,
        private val argParser: Parsable<FuncArgT>,
        private val separatorList: List<Parsable<*>>
    ) : Parser<ASTNode.FunctionRepresentationNode<FuncArgT>>(tokens) {
        override fun consumeDelegate(pos: Int): ParseResult<ASTNode.FunctionRepresentationNode<FuncArgT>> {
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
                    ASTNode.FunctionRepresentationNode(
                        ASTNode.FunctionRepresentationNode.FunctionRepresentation(
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
                throw ParseException("`calc` function expected but was `${rep.name}`")
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

            val func = ColorNode.nodeForFunction(rep.name, rep.argNodes)
            return ParseResult(rep.posOffset, listOf(func))
        }
    }
}