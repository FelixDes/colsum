package translator.parser

import color.CssColor
import io.kotest.assertions.assertSoftly
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import translator.tokenization.TokenType
import translator.tokenization.TokenType.COLOR_CONST

@OptIn(ExperimentalKotest::class)
class ConstColorTest : BehaviorSpec({
    data class TestData(val tokens: List<Pair<TokenType, String>>, val expected: CssColor)

    withData(
        TestData(
            listOf(COLOR_CONST to "aqua"),
            CssColor.fromConstant("aqua")
        ),
        TestData(
            listOf(COLOR_CONST to "wheat"),
            CssColor.fromConstant("wheat")
        )
    ) { (tokens, color) ->
        given("Parser") {
            val parser = Parser.ColorParser(tokens)
            `when`("Consume") {
                val parserResult = parser.consume(0)
                then("Check") {
                    assertSoftly {
                        parserResult.posOffset shouldBe tokens.size
                        parserResult.nodeList[0].compute() shouldBe color
                    }
                }
            }
        }
    }
})