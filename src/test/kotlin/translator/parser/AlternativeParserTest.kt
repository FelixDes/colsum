package translator.parser

import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import translator.nodes.NumberNode
import translator.parser.Parser.*
import translator.tokenization.TokenType
import translator.tokenization.TokenType.*
import kotlin.test.assertEquals

class AlternativeParserTest {
    companion object {
        @JvmStatic
        fun tokenSequence() = listOf(
            Arguments.of(
                listOf(NUMBER to "13"),
                NumberNode.buildNumber(13.0)
            ),
            Arguments.of(
                listOf(NUMBER_NONE to "none"),
                NumberNode.buildNone()
            )
        )
    }

    @ParameterizedTest
    @MethodSource("tokenSequence")
    fun consume_correct(tokens: List<Pair<TokenType, String>>, node: NumberNode) {
        // given
        val parser = AlternativeParser(
            tokens, listOf(
                NumberParser(tokens), NoneParser(tokens)
            )
        )
        // when
        val parserResult = parser.consume(0)
        // then
        assertAll({ assertEquals(1, parserResult.posOffset) }, {
            assertEquals(
                node.compute(),
                parserResult.nodeList[0].compute()
            )
        })
    }
}