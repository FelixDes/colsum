package translator.parser

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import translator.nodes.NumberNode
import translator.tokenization.TokenType.NUMBER_NONE

class NoneParserTest : BehaviorSpec({
    given("Token sequence and parser") {
        val tokenSequence = listOf(NUMBER_NONE to "none")
        val parser = Parser.NoneParser(tokenSequence)
        `when`("Consume") {
            val parserResult = parser.consume(0)
            then("Offset is good and node is none") {
                assertSoftly {
                    parserResult.posOffset shouldBe 1
                    NumberNode.NoneNode shouldBe parserResult.nodeList[0]
                }
            }
        }
    }
})