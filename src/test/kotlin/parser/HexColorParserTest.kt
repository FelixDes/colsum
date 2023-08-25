package parser

import ASTNode
import translator.ParseException
import translator.Parser
import translator.TokenType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
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
        val parser = Parser.HexColorParser(tokens)
        for ((i, tokenPair) in tokens.withIndex()) {
            // when
            val parserResult = parser.consume(i)
            // then
            assertAll(
                { assertEquals(1, parserResult.posOffset) },
                { assertEquals(ASTNode.HexColorNode(tokenPair.second), parserResult.nodeList[0]) }
            )
        }
    }

    @Test
    fun consumeColor_incorrect() {
        // given
        val tokens = tokenSequence()
        val parser = Parser.HexColorParser(tokens)
        // when + then
        assertFailsWith<ParseException> { parser.consume(-1) }
    }
}