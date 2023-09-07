package translator.parser

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import translator.nodes.ColorNode
import translator.tokenization.TokenType
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class HexColorParserTest {
    private fun tokenSequence() = listOf(
        Pair(TokenType.HEX_COLOR, "#009"),
        Pair(TokenType.HEX_COLOR, "#0099"),
        Pair(TokenType.HEX_COLOR, "#009900"),
        Pair(TokenType.HEX_COLOR, "#00990011")
    )

    @Test
    fun consumeColor_correct() {
        // given
        val tokens = tokenSequence()
        val parser = Parser.ColorParser(tokens)
        for ((i, tokenPair) in tokens.withIndex()) {
            // when
            val parserResult = parser.consume(i)
            // then
            assertAll(
                { assertEquals(1, parserResult.posOffset) },
                { assertEquals(ColorNode(tokenPair.second).compute(), parserResult.nodeList[0].compute()) }
            )
        }
    }

    @Test
    fun consumeColor_incorrect() {
        // given
        val tokens = tokenSequence()
        val parser = Parser.ColorParser(tokens)
        // when + then
        assertFailsWith<ParseException> { parser.consume(-1) }
    }
}