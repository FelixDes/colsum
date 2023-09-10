package translator.parser

import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import translator.nodes.NumberNode
import translator.tokenization.TokenType
import translator.tokenization.TokenType.*
import kotlin.test.assertEquals

class ArgSeparatorListParserTest {
    companion object {
        @JvmStatic
        fun tokenSequence() = listOf(
            Arguments.of(
                listOf(
                    FUN_NAME to "function",
                    PARENTHESIS_OPEN to "(",
                    NUMBER to "2",
                    COMMA_SEPARATOR to ", ",
                    NUMBER to "3",
                    SLASH_SEPARATOR to " / ",
                    NUMBER to "5",
                    PARENTHESIS_CLOSE to ")",
                ),
                listOf(
                    NumberNode.buildNumber(2.0),
                    NumberNode.buildNumber(3.0),
                    NumberNode.buildNumber(5.0),
                )
            )
        )
    }

    @ParameterizedTest
    @MethodSource("tokenSequence")
    fun consume_correct(tokens: List<Pair<TokenType, String>>, nodes: List<NumberNode>) {
        // given
        val parser = Parser.ArgSeparatorListParser(
            tokens, Parser.NumberParser(tokens), listOf(
                Parser.SingleTokenParser(tokens, COMMA_SEPARATOR),
                Parser.SingleTokenParser(tokens, SLASH_SEPARATOR),
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