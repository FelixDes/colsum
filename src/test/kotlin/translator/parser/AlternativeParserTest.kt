package translator.parser

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import translator.nodes.NumberNode
import translator.parser.Parser.*
import translator.tokenization.TokenType
import translator.tokenization.TokenType.NUMBER
import translator.tokenization.TokenType.NUMBER_NONE

class AlternativeParserTest : FunSpec({
    data class TestData(val tokens: List<Pair<TokenType, String>>, val expected: NumberNode)

    withData(
        TestData(listOf(NUMBER to "13"), NumberNode.buildNumber(13.0)),
        TestData(listOf(NUMBER_NONE to "none"), NumberNode.buildNone())
    ) { (tokens, node) ->
        // given
        val parser = AlternativeParser(
            tokens, listOf(
                NumberParser(tokens), NoneParser(tokens)
            )
        )
        // when
        val parserResult = parser.consume(0)
        // then
        assertSoftly {
            parserResult.posOffset shouldBe 1
            parserResult.nodeList[0].compute() shouldBe node.compute()
        }
    }
})