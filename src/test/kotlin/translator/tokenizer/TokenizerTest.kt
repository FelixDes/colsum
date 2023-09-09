package translator.tokenizer

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import translator.tokenization.TokenType
import translator.tokenization.Tokenizer
import kotlin.test.assertEquals

class TokenizerTest {
    companion object {
        @JvmStatic
        fun tokenDataSource(): List<Arguments> {
            return listOf<Arguments>(
                Arguments.of(
                    "rgba(123, 3%, 2, 1.3/4) + hsl(123 2 * 2 4)-#009900",
                    listOf(
                        Pair(TokenType.FUN_NAME, "rgba"),
                        Pair(TokenType.PARENTHESIS_OPEN, "("),
                        Pair(TokenType.NUMBER, "123"),
                        Pair(TokenType.COMMA_SEPARATOR, ", "),
                        Pair(TokenType.NUMBER_PERCENT, "3%"),
                        Pair(TokenType.COMMA_SEPARATOR, ", "),
                        Pair(TokenType.NUMBER, "2"),
                        Pair(TokenType.COMMA_SEPARATOR, ", "),
                        Pair(TokenType.NUMBER, "1.3"),
                        Pair(TokenType.OPERATOR_DIV, "/"),
                        Pair(TokenType.NUMBER, "4"),
                        Pair(TokenType.PARENTHESIS_CLOSE, ")"),
                        Pair(TokenType.OPERATOR_PLUS, "+"),
                        Pair(TokenType.FUN_NAME, "hsl"),
                        Pair(TokenType.PARENTHESIS_OPEN, "("),
                        Pair(TokenType.NUMBER, "123"),
                        Pair(TokenType.NUMBER, "2"),
                        Pair(TokenType.OPERATOR_MUL, "*"),
                        Pair(TokenType.NUMBER, "2"),
                        Pair(TokenType.NUMBER, "4"),
                        Pair(TokenType.PARENTHESIS_CLOSE, ")"),
                        Pair(TokenType.OPERATOR_MINUS, "-"),
                        Pair(TokenType.COLOR_HEX, "#009900"),
                    )
                ),
                Arguments.of(
                    "calc(calc(2 + 3% / 4) - 32)",
                    listOf(
                        Pair(TokenType.FUN_NAME, "calc"),
                        Pair(TokenType.PARENTHESIS_OPEN, "("),
                        Pair(TokenType.FUN_NAME, "calc"),
                        Pair(TokenType.PARENTHESIS_OPEN, "("),
                        Pair(TokenType.NUMBER, "2"),
                        Pair(TokenType.OPERATOR_PLUS, "+"),
                        Pair(TokenType.NUMBER_PERCENT, "3%"),
                        Pair(TokenType.OPERATOR_DIV, "/"),
                        Pair(TokenType.NUMBER, "4"),
                        Pair(TokenType.PARENTHESIS_CLOSE, ")"),
                        Pair(TokenType.OPERATOR_MINUS, "-"),
                        Pair(TokenType.NUMBER, "32"),
                        Pair(TokenType.PARENTHESIS_CLOSE, ")"),
                    )
                ),
                Arguments.of(
                    "(((1 * 3% + 3 / 1) - (4 + 5 * 7))) - e / pi * none - infinity + -infinity",
                    listOf(
                        Pair(TokenType.PARENTHESIS_OPEN, "("),
                        Pair(TokenType.PARENTHESIS_OPEN, "("),
                        Pair(TokenType.PARENTHESIS_OPEN, "("),
                        Pair(TokenType.NUMBER, "1"),
                        Pair(TokenType.OPERATOR_MUL, "*"),
                        Pair(TokenType.NUMBER_PERCENT, "3%"),
                        Pair(TokenType.OPERATOR_PLUS, "+"),
                        Pair(TokenType.NUMBER, "3"),
                        Pair(TokenType.OPERATOR_DIV, "/"),
                        Pair(TokenType.NUMBER, "1"),
                        Pair(TokenType.PARENTHESIS_CLOSE, ")"),
                        Pair(TokenType.OPERATOR_MINUS, "-"),
                        Pair(TokenType.PARENTHESIS_OPEN, "("),
                        Pair(TokenType.NUMBER, "4"),
                        Pair(TokenType.OPERATOR_PLUS, "+"),
                        Pair(TokenType.NUMBER, "5"),
                        Pair(TokenType.OPERATOR_MUL, "*"),
                        Pair(TokenType.NUMBER, "7"),
                        Pair(TokenType.PARENTHESIS_CLOSE, ")"),
                        Pair(TokenType.PARENTHESIS_CLOSE, ")"),
                        Pair(TokenType.PARENTHESIS_CLOSE, ")"),
                        Pair(TokenType.OPERATOR_MINUS, "-"),
                        Pair(TokenType.NUMBER_EXP, "e"),
                        Pair(TokenType.OPERATOR_DIV, "/"),
                        Pair(TokenType.NUMBER_PI, "pi"),
                        Pair(TokenType.OPERATOR_MUL, "*"),
                        Pair(TokenType.NUMBER_NONE, "none"),
                        Pair(TokenType.OPERATOR_MINUS, "-"),
                        Pair(TokenType.NUMBER_POS_INF, "infinity"),
                        Pair(TokenType.OPERATOR_PLUS, "+"),
                        Pair(TokenType.NUMBER_NEG_INF, "-infinity"),
                    )
                )
            )
        }
    }

    @ParameterizedTest
    @MethodSource("tokenDataSource")
    fun tokenize_correct(input: String, expectedTokens: List<Pair<TokenType, String>>) {
        // when
        val actualTokens = Tokenizer.tokenize(input)
        // then
        assertEquals(expectedTokens, actualTokens)
    }
}