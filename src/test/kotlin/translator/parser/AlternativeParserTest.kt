package translator.parser

import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import translator.ASTNode
import translator.Parser.*
import translator.TokenType
import kotlin.test.assertEquals

class AlternativeParserTest {
    companion object {
        @JvmStatic
        fun tokenSequence() = listOf(
            Arguments.of(
                listOf(Pair(TokenType.NUMBER, "13")),
                ASTNode.NumberNode.buildNumber(13.0)
            ),
            Arguments.of(
                listOf(Pair(TokenType.NUMBER_NONE, "none")),
                ASTNode.NumberNode.buildNone()
            )
        )
    }

    @ParameterizedTest
    @MethodSource("tokenSequence")
    fun consumeColor_correct(tokens: List<Pair<TokenType, String>>, node: ASTNode.NumberNode) {
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