package translator.integrational

import color.CssColor
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import translator.parser.FunctionParser
import translator.tokenization.TokenType
import translator.tokenization.TokenType.*

class ColorFunctionParserTest : FunSpec({
    data class TestData(val tokens: List<Pair<TokenType, String>>, val expected: CssColor)

    context("RGBA") {
        withData(
            TestData(
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
            TestData(
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
            TestData(
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
            TestData(
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
            TestData(
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
            TestData(
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
        ) { (tokens, res) ->
            // given
            val parser = FunctionParser.ColorFunctionParser(tokens)
            // when
            val parserResult = parser.consume(0)
            // then
            assertSoftly {
                tokens.size shouldBe parserResult.posOffset
                parserResult.nodeList[0].compute() shouldBe res
            }
        }
    }

    context("HSLA") {
        withData(
            TestData(
                listOf(
                    FUN_NAME to "hsla",
                    PARENTHESIS_OPEN to "(",
                    NUMBER to "204",
                    NUMBER_PERCENT to "33%",
                    NUMBER_PERCENT to "3%",
                    SLASH_SEPARATOR to " / ",
                    NUMBER to "0.5",
                    PARENTHESIS_CLOSE to ")",
                ),
                CssColor.fromHEX("#05080A7F")
            ),
            TestData(
                listOf(
                    FUN_NAME to "hsla",
                    PARENTHESIS_OPEN to "(",
                    NUMBER_NONE to "none",
                    NUMBER_NONE to "none",
                    NUMBER_PERCENT to "50%",
                    PARENTHESIS_CLOSE to ")",
                ),
                CssColor.fromHEX("#808080")
            ),
            TestData(
                listOf(
                    FUN_NAME to "hsla",
                    PARENTHESIS_OPEN to "(",
                    NUMBER to "634",
                    NUMBER_PERCENT to "85%",
                    NUMBER_PERCENT to "42%",
                    SLASH_SEPARATOR to " / ",
                    NUMBER_PERCENT to "50%",
                    PARENTHESIS_CLOSE to ")",
                ),
                CssColor.fromRGBA(119, 16, 198, 0.5)
            ),
            TestData(
                listOf(
                    FUN_NAME to "hsl",
                    PARENTHESIS_OPEN to "(",
                    NUMBER to "204",
                    NUMBER_PERCENT to "33%",
                    NUMBER_PERCENT to "3%",
                    SLASH_SEPARATOR to " / ",
                    NUMBER to "1.0",
                    PARENTHESIS_CLOSE to ")",
                ),
                CssColor.fromHEX("#05080A")
            ),
        ) { (tokens, res) ->
            // given
            val parser = FunctionParser.ColorFunctionParser(tokens)
            // when
            val parserResult = parser.consume(0)
            // then
            assertSoftly {
                tokens.size shouldBe parserResult.posOffset
                parserResult.nodeList[0].compute() shouldBe res
            }
        }
    }
})