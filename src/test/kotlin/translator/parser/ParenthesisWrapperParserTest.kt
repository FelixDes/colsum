package translator.parser

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import translator.ParseException
import translator.Parser
import translator.TokenType
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ParenthesisWrapperParserTest {
    private val tokenSequence = listOf(
        Pair(TokenType.PARENTHESIS_OPEN, "("),
        Pair(TokenType.NUMBER, "2"),
        Pair(TokenType.COMMA_SEPARATOR, ","),
        Pair(TokenType.NUMBER, "3"),
        Pair(TokenType.COMMA_SEPARATOR, ","),
        Pair(TokenType.NUMBER, "4"),
        Pair(TokenType.PARENTHESIS_CLOSE, ")"),
    )

    @Test
    fun consume_correct() {
        // given
        val tokens = tokenSequence
        val innerParser = Parser.WhileSeparatorParser(
            tokens,
            Parser.NumberParser(tokens),
            Parser.SingleTokenParser(tokens, TokenType.COMMA_SEPARATOR)
        )
        val parser = Parser.ParenthesisWrapperParser(tokens, innerParser)
        // when
        val parserResult = parser.consume(0)
        // then
        assertAll(
            { assertEquals(7, parserResult.posOffset) },
            { assertEquals(listOf(2.0, 3.0, 4.0), parserResult.nodeList.map { it.compute() }) }
        )
    }

    @Test
    fun consume_incorrect() {
        // given
        val tokens = tokenSequence.subList(1, tokenSequence.size)
        val innerParser = Parser.WhileSeparatorParser(
            tokens,
            Parser.NumberParser(tokens),
            Parser.SingleTokenParser(tokens, TokenType.COMMA_SEPARATOR)
        )
        val parser = Parser.ParenthesisWrapperParser(tokens, innerParser)

        // when then
        assertFailsWith<ParseException> { parser.consume(0) }
    }
}