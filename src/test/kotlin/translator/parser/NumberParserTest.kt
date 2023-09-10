package translator.parser

import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import translator.nodes.NumberNode
import translator.tokenization.TokenType
import translator.tokenization.TokenType.*
import kotlin.test.assertEquals

class NumberParserTest {
    companion object {
        @JvmStatic
        fun tokenSequence() = listOf(
            Arguments.of(
                listOf(NUMBER to "1"),
                NumberNode.buildNumber("1")
            ),
            Arguments.of(
                listOf(NUMBER_PERCENT to "1%"),
                NumberNode.buildPercent("1%")
            ),
            Arguments.of(
                listOf(NUMBER to "-1"),
                NumberNode.buildNumber("-1")
            ),
            Arguments.of(
                listOf(NUMBER_PERCENT to "-1%"),
                NumberNode.buildPercent("-1%")
            ),
            Arguments.of(
                listOf(NUMBER to "-2.01"),
                NumberNode.buildNumber("-2.01")
            ),
            Arguments.of(
                listOf(NUMBER_PERCENT to "-2.01%"),
                NumberNode.buildPercent("-2.01%")
            ),
            Arguments.of(
                listOf(NUMBER to "1.6e-5"),
                NumberNode.buildNumber("1.6e-5")
            ),
            Arguments.of(
                listOf(NUMBER_PERCENT to "1.6e-5%"),
                NumberNode.buildPercent("1.6e-5%")
            ),
            Arguments.of(
                listOf(NUMBER_PI to "pi"),
                NumberNode.buildSpecific(NUMBER_PI)
            ),
            Arguments.of(
                listOf(NUMBER_EXP to "e"),
                NumberNode.buildSpecific(NUMBER_EXP)
            ),
            Arguments.of(
                listOf(NUMBER_NAN to "NaN"),
                NumberNode.buildSpecific(NUMBER_NAN)
            ),
            Arguments.of(
                listOf(NUMBER_POS_INF to "infinity"),
                NumberNode.buildSpecific(NUMBER_POS_INF)
            ),
            Arguments.of(
                listOf(NUMBER_NEG_INF to "-infinity"),
                NumberNode.buildSpecific(NUMBER_NEG_INF)
            ),
        )
    }

    @ParameterizedTest
    @MethodSource("tokenSequence")
    fun consume_correct(tokens: List<Pair<TokenType, String>>, res: NumberNode) {
        // given
        val parser = Parser.NumberParser(tokens)
        // when
        val parserResult = parser.consume(0)
        // then
        assertAll({ assertEquals(1, parserResult.posOffset) }, {
            assertEquals(
                res.compute(), parserResult.nodeList[0].compute()
            )
        })
    }
}