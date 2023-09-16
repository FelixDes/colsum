package translator.parser

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import translator.tokenization.TokenType.*
import kotlin.test.assertFailsWith

class ParenthesisWrapperParserTest : AnnotationSpec() {
    private val tokenSequence = listOf(
        PARENTHESIS_OPEN to "(",
        NUMBER to "2",
        COMMA_SEPARATOR to ",",
        NUMBER to "3",
        COMMA_SEPARATOR to ",",
        NUMBER to "4",
        PARENTHESIS_CLOSE to ")",
    )

    @Test
    fun consume_correct() {
        // given
        val tokens = tokenSequence
        val innerParser = Parser.WhileSeparatorParser(
            tokens, Parser.NumberParser(tokens), Parser.SingleTokenParser(tokens, COMMA_SEPARATOR)
        )
        val parser = Parser.ParenthesisWrapperParser(tokens, innerParser)
        // when
        val parserResult = parser.consume(0)
        // then
        assertSoftly {
            parserResult.posOffset shouldBe tokenSequence.size
            parserResult.nodeList.map { it.compute() } shouldBe listOf(2.0, 3.0, 4.0)
        }

    }

    @Test
    fun consume_incorrect() {
        // given
        val tokens = tokenSequence.subList(1, tokenSequence.size)
        val innerParser = Parser.WhileSeparatorParser(
            tokens,
            Parser.NumberParser(tokens),
            Parser.SingleTokenParser(tokens, COMMA_SEPARATOR)
        )
        val parser = Parser.ParenthesisWrapperParser(tokens, innerParser)

        // when then
        assertFailsWith<ParseException> { parser.consume(0) }
    }
}