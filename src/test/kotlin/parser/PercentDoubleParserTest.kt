package parser

import ASTNode
import translator.ParseException
import translator.Parser
import translator.TokenType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PercentDoubleParserTest {
    private val tokenSequence = listOf(
        Pair(TokenType.NUMBER_PERCENT, "1%"),
        Pair(TokenType.NUMBER_PERCENT, "-1%"),
        Pair(TokenType.NUMBER_PERCENT, "-2.01%"),
        Pair(TokenType.NUMBER_PERCENT, "1.6e-5%"),
    )

    @Test
    fun consumeColor_correct() {
        // given
        val tokens = tokenSequence
        val parser = Parser.PercentDoubleParser(tokens)
        for ((i, tokenPair) in tokens.withIndex()) {
            // when
            val parserResult = parser.consume(i)
            // then
            assertAll(
                { assertEquals(1, parserResult.posOffset) },
                {
                    assertEquals(
                        ASTNode.NumberNode.PercentDoubleNode(tokenPair.second).compute(),
                        parserResult.nodeList[0].compute()
                    )
                }
            )
        }
    }

    @Test
    fun consumeColor_incorrect() {
        // given
        val tokens = tokenSequence
        val parser = Parser.PercentDoubleParser(tokens)
        // when + then
        assertFailsWith<ParseException> { parser.consume(-1) }
    }
}