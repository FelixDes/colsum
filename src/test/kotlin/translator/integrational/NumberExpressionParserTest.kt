package translator.integrational

import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import translator.parser.Parser
import translator.tokenization.TokenType
import translator.tokenization.TokenType.*
import kotlin.test.assertEquals

class NumberExpressionParserTest {
    companion object {
        @JvmStatic
        fun tokenSequence() = listOf(
            Arguments.of(
                listOf(
                    PARENTHESIS_OPEN to "(",
                    NUMBER to "2",
                    OPERATOR_PLUS to "+",
                    NUMBER to "3",
                    OPERATOR_MUL to "*",
                    NUMBER to "4",
                    PARENTHESIS_CLOSE to ")",
                    OPERATOR_DIV to "/",
                    NUMBER to "2",
                ),
                7.0
            ),
            Arguments.of(
                listOf(
                    PARENTHESIS_OPEN to "(",
                    PARENTHESIS_OPEN to "(",
                    PARENTHESIS_OPEN to "(",
                    NUMBER to "1",
                    OPERATOR_MUL to "*",
                    NUMBER to "3",
                    OPERATOR_PLUS to "+",
                    NUMBER to "3",
                    OPERATOR_DIV to "/",
                    NUMBER to "1",
                    PARENTHESIS_CLOSE to ")",
                    OPERATOR_MUL to "*",
                    NUMBER to "8",
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
                ),
                9.0
            ),
            Arguments.of(
                listOf(
                    PARENTHESIS_OPEN to "(",

                    PARENTHESIS_OPEN to "(",
                    NUMBER to "12.1",
                    PARENTHESIS_CLOSE to ")",
                    OPERATOR_MUL to "*",

                    PARENTHESIS_OPEN to "(",
                    NUMBER to "2",
                    OPERATOR_PLUS to "+",
                    NUMBER to "3",

                    OPERATOR_MUL to "*",
                    NUMBER to "4",
                    PARENTHESIS_CLOSE to ")",
                    OPERATOR_DIV to "/",

                    NUMBER to "2",
                    PARENTHESIS_CLOSE to ")",
                ),
                84.7
            ),
            Arguments.of(
                listOf(
                    NUMBER_PERCENT to "3%",
                    OPERATOR_MINUS to "-",
                    NUMBER_PERCENT to "2%",
                    OPERATOR_PLUS to "+",
                    NUMBER_PERCENT to "10%",
                ),
                11.0
            ),
        )
    }

    @ParameterizedTest
    @MethodSource("tokenSequence")
    fun consume_correct(tokens: List<Pair<TokenType, String>>, res: Double) {
        // given
        val parser = Parser.ExpressionParser(
            tokens,
            Parser.NumberParser(tokens)
        )
        // when
        val parserResult = parser.consume(0)
        // then
        assertAll(
            { assertEquals(tokens.size, parserResult.posOffset) },
            { assertEquals(res, parserResult.nodeList[0].compute()) }
        )
    }
}