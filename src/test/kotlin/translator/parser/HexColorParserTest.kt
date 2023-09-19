package translator.parser

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import translator.nodes.ColorNode
import translator.tokenization.TokenType
import translator.tokenization.TokenType.COLOR_HEX
import kotlin.test.assertFailsWith

class HexColorParserTest : AnnotationSpec() {
    private val tokenSequence: List<Pair<TokenType, String>> = listOf(
        COLOR_HEX to "#009",
        COLOR_HEX to "#0099",
        COLOR_HEX to "#009900",
        COLOR_HEX to "#00990011"
    )

    @Test
    fun consumeColor_correct() {
        // given
        val tokens = tokenSequence
        val parser = Parser.ColorParser(tokens)
        for ((i, tokenPair) in tokens.withIndex()) {
            // when
            val parserResult = parser.consume(i)
            // then
            assertSoftly {
                parserResult.posOffset shouldBe 1
                ColorNode.nodeForHex(tokenPair.second).compute() shouldBe parserResult.nodeList[0].compute()
            }
        }

        @Test
        fun consumeColor_incorrect() {
            assertFailsWith<ParsingException> { Parser.ColorParser(tokenSequence).consume(-1) }
        }
    }
}