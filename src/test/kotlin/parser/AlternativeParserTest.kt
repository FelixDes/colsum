package parser

import ASTNode
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import translator.Parsable
import translator.ParseException
import translator.Parser
import translator.Parser.*
import translator.TokenType
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AlternativeParserTest {
    private val tokenSequence = listOf(Pair(TokenType.NUMBER, "13"), Pair(TokenType.NUMBER_NONE, "none"))
    private val tokenSequenceDoubleValues = listOf("13", "0")

    @Test
    fun consumeColor_correct() {
        // given
        val tokens = tokenSequence
        val parser = AlternativeParser(
            tokens, listOf(
                DoubleParser(tokens) as Parsable<ASTNode.NumberNode>, NoneParser(tokens) as Parsable<ASTNode.NumberNode>
            )
        )
        for (i in tokens.indices) {
            // when
            val parserResult = parser.consume(i)
            // then
            assertAll({ assertEquals(1, parserResult.posOffset) }, {
                assertEquals(
                    ASTNode.NumberNode.DoubleNode(tokenSequenceDoubleValues[i]).compute(),
                    parserResult.nodeList[0].compute()
                )
            })
        }
    }

    @Test
    fun consumeColor_incorrect() {
        // given
        val tokens = tokenSequence
        val parser = Parser.HexColorParser(tokens)
        // when + then
        assertFailsWith<ParseException> { parser.consume(-1) }
    }
}