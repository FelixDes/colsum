package translator.integrational

import color.CssColor
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import translator.parser.FunctionParser
import translator.tokenization.TokenType
import kotlin.test.assertEquals

class ColorFunctionParserTest {
    companion object {
        @JvmStatic
        fun tokenSequence() = listOf(
            Arguments.of(
                listOf(
                    Pair(TokenType.FUN_NAME, "rgb"),
                    Pair(TokenType.PARENTHESIS_OPEN, "("),
                    Pair(TokenType.NUMBER_PERCENT, "2%"),
                    Pair(TokenType.NUMBER_PERCENT, "3%"),
                    Pair(TokenType.NUMBER_PERCENT, "4%"),
                    Pair(TokenType.SLASH_SEPARATOR, " / "),
                    Pair(TokenType.NUMBER, "0.5"),
                    Pair(TokenType.PARENTHESIS_CLOSE, ")"),
                ),
                CssColor.fromHEX("#05080A7F")
            ),
            Arguments.of(
                listOf(
                    Pair(TokenType.FUN_NAME, "rgb"),
                    Pair(TokenType.PARENTHESIS_OPEN, "("),
                    Pair(TokenType.NUMBER_PERCENT, "2%"),
                    Pair(TokenType.NUMBER_NONE, "none"),
                    Pair(TokenType.NUMBER_PERCENT, "15%"),
                    Pair(TokenType.SLASH_SEPARATOR, " / "),
                    Pair(TokenType.NUMBER, "0.5"),
                    Pair(TokenType.PARENTHESIS_CLOSE, ")"),
                ),
                CssColor.fromHEX("#0500267F")
            ),
            Arguments.of(
                listOf(
                    Pair(TokenType.FUN_NAME, "rgb"),
                    Pair(TokenType.PARENTHESIS_OPEN, "("),
                    Pair(TokenType.NUMBER, "120"),
                    Pair(TokenType.NUMBER, "16"),
                    Pair(TokenType.NUMBER, "199"),
                    Pair(TokenType.SLASH_SEPARATOR, " / "),
                    Pair(TokenType.NUMBER, "0.5"),
                    Pair(TokenType.PARENTHESIS_CLOSE, ")"),
                ),
                CssColor.fromHEX("#7810C77F")
            ),
            Arguments.of(
                listOf(
                    Pair(TokenType.FUN_NAME, "rgb"),
                    Pair(TokenType.PARENTHESIS_OPEN, "("),
                    Pair(TokenType.NUMBER_NONE, "none"),
                    Pair(TokenType.NUMBER, "16"),
                    Pair(TokenType.NUMBER, "199"),
                    Pair(TokenType.SLASH_SEPARATOR, " / "),
                    Pair(TokenType.NUMBER, "0.5"),
                    Pair(TokenType.PARENTHESIS_CLOSE, ")"),
                ),
                CssColor.fromHEX("#0010C77F")
            ),
            Arguments.of(
                listOf(
                    Pair(TokenType.FUN_NAME, "rgb"),
                    Pair(TokenType.PARENTHESIS_OPEN, "("),
                    Pair(TokenType.NUMBER_NONE, "none"),
                    Pair(TokenType.NUMBER, "16"),
                    Pair(TokenType.NUMBER, "199"),
                    Pair(TokenType.SLASH_SEPARATOR, " / "),
                    Pair(TokenType.NUMBER_PERCENT, "50%"),
                    Pair(TokenType.PARENTHESIS_CLOSE, ")"),
                ),
                CssColor.fromHEX("#0010C77F")
            ),
            Arguments.of(
                listOf(
                    Pair(TokenType.FUN_NAME, "rgb"),
                    Pair(TokenType.PARENTHESIS_OPEN, "("),
                    Pair(TokenType.NUMBER, "16"),
                    Pair(TokenType.COMMA_SEPARATOR, ", "),
                    Pair(TokenType.NUMBER, "199"),
                    Pair(TokenType.COMMA_SEPARATOR, ", "),
                    Pair(TokenType.NUMBER, "255"),
                    Pair(TokenType.COMMA_SEPARATOR, ", "),
                    Pair(TokenType.NUMBER, "0.5"),
                    Pair(TokenType.PARENTHESIS_CLOSE, ")"),
                ),
                CssColor.fromRGBA(16, 199, 255, 0.5)
            ),
        )
    }

    @ParameterizedTest
    @MethodSource("tokenSequence")
    fun consume_correct(tokens: List<Pair<TokenType, String>>, res: CssColor) {
        // given
        val parser = FunctionParser.ColorFunctionParser(tokens)
        // when
        val parserResult = parser.consume(0)
        // then
        assertAll(
            { assertEquals(tokens.size, parserResult.posOffset) },
            { assertEquals(res, parserResult.nodeList[0].compute()) }
        )
    }
}