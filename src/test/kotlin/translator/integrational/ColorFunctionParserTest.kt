package translator.integrational

import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import translator.Parser
import translator.TokenType
import kotlin.test.assertEquals

class ColorFunctionParserTest {
    companion object {
        @JvmStatic
        fun tokenSequence() = listOf(
            Arguments.of(
                listOf(
                    Pair(TokenType.FUN_NAME, "rgb"),
                    Pair(TokenType.PARENTHESIS_OPEN, "("),
                    Pair(TokenType.NUMBER, "2"),
                    Pair(TokenType.NUMBER, "3"),
                    Pair(TokenType.NUMBER, "4"),
                    Pair(TokenType.SLASH_SEPARATOR, " / "),
                    Pair(TokenType.NUMBER, "5"),
                    Pair(TokenType.PARENTHESIS_CLOSE, ")"),
                ),
                7.0
            ),
        )
    }

    @ParameterizedTest
    @MethodSource("tokenSequence")
    fun consume_correct(tokens: List<Pair<TokenType, String>>, res: Double) {
        // given
        val parser = Parser.FunctionParser.ColorFunctionParser(tokens)
        // when
        val parserResult = parser.consume(0)
        val color = parserResult.nodeList[0].compute()
        // then
        assertAll(
            { assertEquals(tokens.size, parserResult.posOffset) },
//            { assertEquals(res, parserResult.nodeList[0].compute()) }
        )
    }
}