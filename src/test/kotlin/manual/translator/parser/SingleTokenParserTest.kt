package manual.translator.parser

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import translator.nodes.ASTNode
import translator.parser.ParseException
import translator.parser.Parser
import translator.tokenization.TokenType
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SingleTokenParserTest {
    private val tokenSequence = listOf(Pair(TokenType.PARENTHESIS_OPEN, "("))

    @Test
    fun consume_correct() {
        // given
        val tokens = tokenSequence
        val parser = Parser.SingleTokenParser(tokens, TokenType.PARENTHESIS_OPEN)
        // when
        val parserResult = parser.consume(0)
        // then
        assertAll({ assertEquals(1, parserResult.posOffset) },
            { assertEquals(ASTNode.TokenNode(tokenSequence[0].first).compute(), parserResult.nodeList[0].compute()) })
    }

    @Test
    fun consume_incorrect() {
        // given
        val tokens = tokenSequence
        val parser = Parser.SingleTokenParser(tokens, TokenType.PARENTHESIS_OPEN)
        // when + then
        assertFailsWith<ParseException> { parser.consume(-1) }
    }
}