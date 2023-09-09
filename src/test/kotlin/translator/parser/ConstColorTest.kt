package translator.parser

import color.CssColor
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import translator.tokenization.TokenType
import kotlin.test.assertEquals

class ConstColorTest {
    companion object {
        @JvmStatic
        fun tokenSequence() = listOf(
            Arguments.of(
                listOf(Pair(TokenType.COLOR_CONST, "aqua")),
                CssColor.fromConstant("aqua")
            ),
            Arguments.of(
                listOf(Pair(TokenType.COLOR_CONST, "wheat")),
                CssColor.fromConstant("wheat")
            )
        )
    }

    @ParameterizedTest
    @MethodSource("tokenSequence")
    fun consume_correct(tokens: List<Pair<TokenType, String>>, color: CssColor) {
        // given
        val parser = Parser.ColorParser(tokens)
        // when
        val parserResult = parser.consume(0)
        // then
        assertAll({ assertEquals(1, parserResult.posOffset) }, {
            assertEquals(
                color,
                parserResult.nodeList[0].compute()
            )
        })
    }
}