package translator.parser

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import translator.nodes.ColorNode
import translator.tokenization.TokenType
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class HexColorParserTest {
    private fun tokenSequence() = listOf(
        Pair(TokenType.COLOR_HEX, "#009"),
        Pair(TokenType.COLOR_HEX, "#0099"),
        Pair(TokenType.COLOR_HEX, "#009900"),
        Pair(TokenType.COLOR_HEX, "#00990011")
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
                { assertEquals(ColorNode.nodeForHex(tokenPair.second).compute(), parserResult.nodeList[0].compute()) }
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