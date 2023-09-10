package translator.parser

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import translator.nodes.NumberNode
import translator.tokenization.TokenType.NUMBER_NONE
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NoneParserTest {
    private val tokenSequence = listOf(
        NUMBER_NONE to "none",
    )

    @Test
    fun consume_correct() {
        // given
        val tokens = tokenSequence
        val parser = Parser.NoneParser(tokens)
        // when
        val parserResult = parser.consume(0)
        // then
        assertAll({ assertEquals(1, parserResult.posOffset) }, {
            assertEquals(
                NumberNode.buildNone().compute(), parserResult.nodeList[0].compute()
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