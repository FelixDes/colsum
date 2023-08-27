package translator.parser

import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import translator.ASTNode
import translator.Parser
import translator.TokenType
import kotlin.test.assertEquals

class NumberParserTest {
    companion object {
        @JvmStatic
        fun tokenSequence() = listOf(
            Arguments.of(
                listOf(Pair(TokenType.NUMBER, "1")),
                ASTNode.NumberNode.buildNumber("1")
            ),
            Arguments.of(
                listOf(Pair(TokenType.NUMBER_PERCENT, "1%")),
                ASTNode.NumberNode.buildPercent("1%")
            ),
            Arguments.of(
                listOf(Pair(TokenType.NUMBER, "-1")),
                ASTNode.NumberNode.buildNumber("-1")
            ),
            Arguments.of(
                listOf(Pair(TokenType.NUMBER_PERCENT, "-1%")),
                ASTNode.NumberNode.buildPercent("-1%")
            ),
            Arguments.of(
                listOf(Pair(TokenType.NUMBER, "-2.01")),
                ASTNode.NumberNode.buildNumber("-2.01")
            ),
            Arguments.of(
                listOf(Pair(TokenType.NUMBER_PERCENT, "-2.01%")),
                ASTNode.NumberNode.buildPercent("-2.01%")
            ),
            Arguments.of(
                listOf(Pair(TokenType.NUMBER, "1.6e-5")),
                ASTNode.NumberNode.buildNumber("1.6e-5")
            ),
            Arguments.of(
                listOf(Pair(TokenType.NUMBER_PERCENT, "1.6e-5%")),
                ASTNode.NumberNode.buildPercent("1.6e-5%")
            ),
            Arguments.of(
                listOf(Pair(TokenType.NUMBER_PI, "pi")),
                ASTNode.NumberNode.buildSpecific(TokenType.NUMBER_PI)
            ),
            Arguments.of(
                listOf(Pair(TokenType.NUMBER_EXP, "e")),
                ASTNode.NumberNode.buildSpecific(TokenType.NUMBER_EXP)
            ),
            Arguments.of(
                listOf(Pair(TokenType.NUMBER_NAN, "NaN")),
                ASTNode.NumberNode.buildSpecific(TokenType.NUMBER_NAN)
            ),
            Arguments.of(
                listOf(Pair(TokenType.NUMBER_POS_INF, "infinity")),
                ASTNode.NumberNode.buildSpecific(TokenType.NUMBER_POS_INF)
            ),
            Arguments.of(
                listOf(Pair(TokenType.NUMBER_NEG_INF, "-infinity")),
                ASTNode.NumberNode.buildSpecific(TokenType.NUMBER_NEG_INF)
            ),
        )
    }

    @ParameterizedTest
    @MethodSource("tokenSequence")
    fun consume_correct(tokens: List<Pair<TokenType, String>>, res: ASTNode.NumberNode) {
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