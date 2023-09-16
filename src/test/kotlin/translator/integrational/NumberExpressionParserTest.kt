package translator.integrational

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import translator.parser.Parser
import translator.tokenization.TokenType
import translator.tokenization.TokenType.*


class NumberExpressionParserTest : FunSpec({
    data class TestData(val tokens: List<Pair<TokenType, String>>, val expected: Double)

    withData(
        TestData(
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
        TestData(
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
        TestData(
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
        TestData(
            listOf(
                NUMBER_PERCENT to "3%",
                OPERATOR_MINUS to "-",
                NUMBER_PERCENT to "2%",
                OPERATOR_PLUS to "+",
                NUMBER_PERCENT to "10%",
            ),
            11.0
        ),
    ) { (tokens, res) ->
        // given
        val parser = Parser.ExpressionParser(
            tokens,
            Parser.NumberParser(tokens)
        )
        // when
        val parserResult = parser.consume(0)
        // then
        assertSoftly {
            tokens.size shouldBe parserResult.posOffset
            parserResult.nodeList[0].compute() shouldBe res
        }
    }
})