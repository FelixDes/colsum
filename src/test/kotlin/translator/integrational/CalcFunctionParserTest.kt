package translator.integrational

import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import translator.parser.FunctionParser
import translator.tokenization.TokenType
import translator.tokenization.TokenType.*
import kotlin.test.assertEquals

class CalcFunctionParserTest {
    companion object {
        @JvmStatic
        fun tokenSequence() = listOf(
            Arguments.of(
                listOf(
                    FUN_NAME to "calc",
                    PARENTHESIS_OPEN to "(",
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
                7.0
            ),
            Arguments.of(
                listOf(
                    FUN_NAME to "calc",
                    PARENTHESIS_OPEN to "(",
                    PARENTHESIS_OPEN to "(",
                    NUMBER_PERCENT to "2",
                    OPERATOR_PLUS to "+",
                    NUMBER_PERCENT to "3",
                    OPERATOR_MUL to "*",
                    NUMBER_PERCENT to "4",
                    PARENTHESIS_CLOSE to ")",
                    OPERATOR_DIV to "/",
                    NUMBER_PERCENT to "2",
                    PARENTHESIS_CLOSE to ")",
                ),
                7.0
            ),
            Arguments.of(
                listOf(
                    FUN_NAME to "calc",
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
                    PARENTHESIS_CLOSE to ")"
                ),
                9.0
            ),
            Arguments.of(
                listOf(
                    FUN_NAME to "calc",
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
                    OPERATOR_PLUS to "+",
                    NUMBER_EXP to "e",
                    PARENTHESIS_CLOSE to ")"
                ),
                84.7 + Math.E
            ),
            Arguments.of(
                listOf(
                    FUN_NAME to "calc",
                    PARENTHESIS_OPEN to "(",
                    FUN_NAME to "calc",
                    PARENTHESIS_OPEN to "(",
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
                    OPERATOR_PLUS to "+",
                    FUN_NAME to "calc",
                    PARENTHESIS_OPEN to "(",
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
                    PARENTHESIS_CLOSE to ")",
                ),
                14.0
            ),
        )
    }

    @ParameterizedTest
    @MethodSource("tokenSequence")
    fun `consume correct`(tokens: List<Pair<TokenType, String>>, res: Double) {
        // given
        val parser = FunctionParser.CalcFunctionParser(tokens)
        // when
        val parserResult = parser.consume(0)
        // then
        assertAll(
            { assertEquals(tokens.size, parserResult.posOffset) },
            { assertEquals(res, parserResult.nodeList[0].compute()) }
        )
    }
}
