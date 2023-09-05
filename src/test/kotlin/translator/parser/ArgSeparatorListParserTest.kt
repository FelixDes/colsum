package translator.parser

import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import translator.ASTNode
import translator.Parser
import translator.TokenType
import kotlin.test.assertEquals

class ArgSeparatorListParserTest {
    companion object {
        @JvmStatic
        fun tokenSequence() = listOf(
            Arguments.of(
                listOf(
                    Pair(TokenType.FUN_NAME, "function"),
                    Pair(TokenType.PARENTHESIS_OPEN, "("),
                    Pair(TokenType.NUMBER, "2"),
                    Pair(TokenType.COMMA_SEPARATOR, ", "),
                    Pair(TokenType.NUMBER, "3"),
                    Pair(TokenType.SLASH_SEPARATOR, " / "),
                    Pair(TokenType.NUMBER, "5"),
                    Pair(TokenType.PARENTHESIS_CLOSE, ")"),
                ),
                listOf(
                    ASTNode.NumberNode.buildNumber(2.0),
                    ASTNode.NumberNode.buildNumber(3.0),
                    ASTNode.NumberNode.buildNumber(5.0),
                )
            )
        )
    }

    @ParameterizedTest
    @MethodSource("tokenSequence")
    fun consumeColor_correct(tokens: List<Pair<TokenType, String>>, nodes: List<ASTNode.NumberNode>) {
        // given
        val parser = Parser.ArgSeparatorListParser(
            tokens, Parser.NumberParser(tokens), listOf(
                Parser.SingleTokenParser(tokens, TokenType.COMMA_SEPARATOR),
                Parser.SingleTokenParser(tokens, TokenType.SLASH_SEPARATOR),
            )
        )
        // when
        val parserResult = parser.consume(2)
        // then
        assertAll({ assertEquals(5, parserResult.posOffset) }, {
            assertEquals(
                nodes.map { it.compute() },
                parserResult.nodeList.map { it.compute() }
            )
        })
    }
}