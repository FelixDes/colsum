package translator.tokenizer

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import translator.tokenization.TokenType
import translator.tokenization.TokenType.*
import translator.tokenization.Tokenizer

data class TokenizerTestData(val expression: String, val tokensExpected: List<Pair<TokenType, String>>)


class TokenizerTest : FunSpec({
    withData(
        TokenizerTestData(
            "rgba(123, 3%, 2, 1.3/4) + hsl(123 2 * 2 4)-#009900",
            listOf(
                FUN_NAME to "rgba",
                PARENTHESIS_OPEN to "(",
                NUMBER to "123",
                COMMA_SEPARATOR to ", ",
                NUMBER_PERCENT to "3%",
                COMMA_SEPARATOR to ", ",
                NUMBER to "2",
                COMMA_SEPARATOR to ", ",
                NUMBER to "1.3",
                OPERATOR_DIV to "/",
                NUMBER to "4",
                PARENTHESIS_CLOSE to ")",
                OPERATOR_PLUS to "+",
                FUN_NAME to "hsl",
                PARENTHESIS_OPEN to "(",
                NUMBER to "123",
                NUMBER to "2",
                OPERATOR_MUL to "*",
                NUMBER to "2",
                NUMBER to "4",
                PARENTHESIS_CLOSE to ")",
                OPERATOR_MINUS to "-",
                COLOR_HEX to "#009900",
            )
        ),
        TokenizerTestData(
            "calc(calc(2 + 3% / 4) - 32)",
            listOf(
                FUN_NAME to "calc",
                PARENTHESIS_OPEN to "(",
                FUN_NAME to "calc",
                PARENTHESIS_OPEN to "(",
                NUMBER to "2",
                OPERATOR_PLUS to "+",
                NUMBER_PERCENT to "3%",
                OPERATOR_DIV to "/",
                NUMBER to "4",
                PARENTHESIS_CLOSE to ")",
                OPERATOR_MINUS to "-",
                NUMBER to "32",
                PARENTHESIS_CLOSE to ")",
            )
        ),
        TokenizerTestData(
            "(((1 * 3% + 3 / 1) - (4 + 5 * 7))) - e / pi * none - infinity + -infinity",
            listOf(
                PARENTHESIS_OPEN to "(",
                PARENTHESIS_OPEN to "(",
                PARENTHESIS_OPEN to "(",
                NUMBER to "1",
                OPERATOR_MUL to "*",
                NUMBER_PERCENT to "3%",
                OPERATOR_PLUS to "+",
                NUMBER to "3",
                OPERATOR_DIV to "/",
                NUMBER to "1",
                PARENTHESIS_CLOSE to ")",
                OPERATOR_MINUS to "-",
                PARENTHESIS_OPEN to "(",
                NUMBER to "4",
                OPERATOR_PLUS to "+",
                NUMBER to "5",
                OPERATOR_MUL to "*",
                NUMBER to "7",
                PARENTHESIS_CLOSE to ")",
                PARENTHESIS_CLOSE to ")",
                PARENTHESIS_CLOSE to ")",
                OPERATOR_MINUS to "-",
                NUMBER_EXP to "e",
                OPERATOR_DIV to "/",
                NUMBER_PI to "pi",
                OPERATOR_MUL to "*",
                NUMBER_NONE to "none",
                OPERATOR_MINUS to "-",
                NUMBER_POS_INF to "infinity",
                OPERATOR_PLUS to "+",
                NUMBER_NEG_INF to "-infinity",
            )
        )
    ) { (expression, expected) ->
        // when
        val actualTokens = Tokenizer.tokenize(expression)
        // then
        actualTokens shouldBe expected
    }
})
