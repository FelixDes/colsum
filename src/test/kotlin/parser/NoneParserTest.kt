package parser

import ASTNode
import translator.ParseException
import translator.Parser
import translator.TokenType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NoneParserTest {
    private val tokenSequence = listOf(
            Pair(TokenType.NUMBER_NONE, "none"),
        )

    @Test
    fun consumeColor_correct() {
        // given
        val tokens = tokenSequence
        val parser = Parser.NoneParser(tokens)
        // when
        val parserResult = parser.consume(0)
        // then
        assertAll({ assertEquals(1, parserResult.posOffset) }, {
            assertEquals(
                ASTNode.NumberNode.DoubleNode("0").compute(), parserResult.nodeList[0].compute()
            )
        })
    }

    @Test
    fun consumeColor_incorrect() {
        // given
        val tokens = tokenSequence
        val parser = Parser.NoneParser(tokens)
        // when + then
        assertFailsWith<ParseException> { parser.consume(-1) }
    }
}