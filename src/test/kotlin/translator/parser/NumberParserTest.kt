package translator.parser

import io.kotest.assertions.assertSoftly
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import translator.nodes.NumberNode
import translator.tokenization.TokenType
import translator.tokenization.TokenType.*

@OptIn(ExperimentalKotest::class)
class NumberParserTest : BehaviorSpec({

    data class TestData(val tokens: List<Pair<TokenType, String>>, val expected: NumberNode)


    withData(
        TestData(
            listOf(NUMBER to "1"),
            NumberNode.buildNumber("1")
        ),
        TestData(
            listOf(NUMBER_PERCENT to "1%"),
            NumberNode.buildPercent("1%")
        ),
        TestData(
            listOf(NUMBER to "-1"),
            NumberNode.buildNumber("-1")
        ),
        TestData(
            listOf(NUMBER_PERCENT to "-1%"),
            NumberNode.buildPercent("-1%")
        ),
        TestData(
            listOf(NUMBER to "-2.01"),
            NumberNode.buildNumber("-2.01")
        ),
        TestData(
            listOf(NUMBER_PERCENT to "-2.01%"),
            NumberNode.buildPercent("-2.01%")
        ),
        TestData(
            listOf(NUMBER to "1.6e-5"),
            NumberNode.buildNumber("1.6e-5")
        ),
        TestData(
            listOf(NUMBER_PERCENT to "1.6e-5%"),
            NumberNode.buildPercent("1.6e-5%")
        ),
        TestData(
            listOf(NUMBER_PI to "pi"),
            NumberNode.buildSpecific(NUMBER_PI)
        ),
        TestData(
            listOf(NUMBER_EXP to "e"),
            NumberNode.buildSpecific(NUMBER_EXP)
        ),
        TestData(
            listOf(NUMBER_NAN to "NaN"),
            NumberNode.buildSpecific(NUMBER_NAN)
        ),
        TestData(
            listOf(NUMBER_POS_INF to "infinity"),
            NumberNode.buildSpecific(NUMBER_POS_INF)
        ),
        TestData(
            listOf(NUMBER_NEG_INF to "-infinity"),
            NumberNode.buildSpecific(NUMBER_NEG_INF)
        )
    ) { (tokens, res) ->
        given("Parser") {
            val parser = Parser.NumberParser(tokens)
            `when`("Consume") {
                val parserResult = parser.consume(0)
                then("Check") {
                    assertSoftly {
                        parserResult.posOffset shouldBe tokens.size
                        parserResult.nodeList[0].compute() shouldBe res.compute()
                    }
                }
            }
        }
    }
})