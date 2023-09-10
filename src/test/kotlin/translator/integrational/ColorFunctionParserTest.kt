package translator.integrational

import color.CssColor
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import translator.parser.FunctionParser
import translator.tokenization.TokenType
import translator.tokenization.TokenType.*
import kotlin.test.assertEquals

class ColorFunctionParserTest {
    companion object {
        @JvmStatic
        fun tokenSequence() = listOf(
            Arguments.of(
                listOf(
                    FUN_NAME to "rgb",
                    PARENTHESIS_OPEN to "(",
                    NUMBER_PERCENT to "2%",
                    NUMBER_PERCENT to "3%",
                    NUMBER_PERCENT to "4%",
                    SLASH_SEPARATOR to " / ",
                    NUMBER to "0.5",
                    PARENTHESIS_CLOSE to ")",
                ),
                CssColor.fromHEX("#05080A7F")
            ),
            Arguments.of(
                listOf(
                    FUN_NAME to "rgb",
                    PARENTHESIS_OPEN to "(",
                    NUMBER_PERCENT to "2%",
                    NUMBER_NONE to "none",
                    NUMBER_PERCENT to "15%",
                    SLASH_SEPARATOR to " / ",
                    NUMBER to "0.5",
                    PARENTHESIS_CLOSE to ")",
                ),
                CssColor.fromHEX("#0500267F")
            ),
            Arguments.of(
                listOf(
                    FUN_NAME to "rgb",
                    PARENTHESIS_OPEN to "(",
                    NUMBER to "120",
                    NUMBER to "16",
                    NUMBER to "199",
                    SLASH_SEPARATOR to " / ",
                    NUMBER to "0.5",
                    PARENTHESIS_CLOSE to ")",
                ),
                CssColor.fromHEX("#7810C77F")
            ),
            Arguments.of(
                listOf(
                    FUN_NAME to "rgb",
                    PARENTHESIS_OPEN to "(",
                    NUMBER_NONE to "none",
                    NUMBER to "16",
                    NUMBER to "199",
                    SLASH_SEPARATOR to " / ",
                    NUMBER to "0.5",
                    PARENTHESIS_CLOSE to ")",
                ),
                CssColor.fromHEX("#0010C77F")
            ),
            Arguments.of(
                listOf(
                    FUN_NAME to "rgb",
                    PARENTHESIS_OPEN to "(",
                    NUMBER_NONE to "none",
                    NUMBER to "16",
                    NUMBER to "199",
                    SLASH_SEPARATOR to " / ",
                    NUMBER_PERCENT to "50%",
                    PARENTHESIS_CLOSE to ")",
                ),
                CssColor.fromHEX("#0010C77F")
            ),
            Arguments.of(
                listOf(
                    FUN_NAME to "rgb",
                    PARENTHESIS_OPEN to "(",
                    NUMBER to "16",
                    COMMA_SEPARATOR to ", ",
                    NUMBER to "199",
                    COMMA_SEPARATOR to ", ",
                    NUMBER to "255",
                    COMMA_SEPARATOR to ", ",
                    NUMBER to "0.5",
                    PARENTHESIS_CLOSE to ")",
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