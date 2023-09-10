package translator.parser

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import translator.tokenization.TokenType.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LexemeByTokenParserTest {
    private val tokenSequence = listOf(
        NUMBER to "2",
        FUN_NAME to "func",
    )

    @Test
    fun consume_correct() {
        // given
        val tokens = tokenSequence
        for ((index, tokenPair) in tokens.withIndex()) {
            // when
            val parser = Parser.LexemeByTokenParser(tokens, tokenPair.first)
            val parserResult = parser.consumeDelegate(index)
            // then
            assertAll({ assertEquals(1, parserResult.posOffset) }, {
                assertEquals(tokenPair.second, parserResult.nodeList[0].compute())
            })
        }
    }

    @Test
    fun consume_incorrect() {
        // given
        val tokens = tokenSequence
        val parser = Parser.LexemeByTokenParser(tokens, PARENTHESIS_OPEN)
        // when + then
        assertFailsWith<ParseException> { parser.consume(-1) }
    }
}