package parser

import ASTNode
import translator.ParseException
import translator.Parser
import translator.TokenType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class WhileSeparatorParserTest {
    private val tokenSequence = listOf(
        Pair(TokenType.FUN_NAME, "rgb"),
        Pair(TokenType.PARENTHESIS_OPEN, "("),
        Pair(TokenType.NUMBER, "1"),
        Pair(TokenType.SEPARATOR, ", "),
        Pair(TokenType.NUMBER, "2"),
        Pair(TokenType.SEPARATOR, ", "),
        Pair(TokenType.NUMBER, "3"),
        Pair(TokenType.PARENTHESIS_CLOSE, ")"),
    )

    @Test
    fun consumeColor_correct() {
        // given
        val tokens = tokenSequence
        val parser = Parser.WhileSeparatorParser(
            tokens,
            Parser.DoubleParser(tokens),
            Parser.SingleTokenParser(tokens, TokenType.SEPARATOR)
        )
        for (tokenPair in tokens) {
            // when
            val parserResult = parser.consumeDelegate(2)
            // then
            assertAll({ assertEquals(5, parserResult.posOffset) }, {
                assertEquals(
                    listOf(
                        ASTNode.NumberNode.DoubleNode("1").compute(),
                        ASTNode.NumberNode.DoubleNode("2").compute(),
                        ASTNode.NumberNode.DoubleNode("3").compute(),
                    ), parserResult.nodeList.map { o -> o.compute() }
                )
            })
        }
    }

    @Test
    fun consumeColor_incorrect() {
        // given
        val tokens = tokenSequence
        val parser = Parser.WhileSeparatorParser(tokens, Parser.EmptyParser, Parser.EmptyParser)
        // when + then
        assertFailsWith<ParseException> { parser.consume(-1) }
    }
}