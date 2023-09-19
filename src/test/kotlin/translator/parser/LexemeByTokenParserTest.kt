package translator.parser

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import translator.tokenization.TokenType.*
import kotlin.test.assertFailsWith

class LexemeByTokenParserTest : AnnotationSpec() {
    private val tokenSequence = listOf(NUMBER to "2", FUN_NAME to "func")

    @Test
    fun consume_correct() {
        // given
        val tokens = tokenSequence
        for ((index, tokenPair) in tokens.withIndex()) {
            // when
            val parser = Parser.LexemeByTokenParser(tokens, tokenPair.first)
            val parserResult = parser.consumeDelegate(index)
            // then
            assertSoftly {
                parserResult.posOffset shouldBe 1
                parserResult.nodeList[0].compute() shouldBe tokenPair.second
            }
        }
    }

    @Test
    fun consume_incorrect() {
        // given
        val parser = Parser.LexemeByTokenParser(tokenSequence, PARENTHESIS_OPEN)
        // when + then
        assertFailsWith<ParsingException> { parser.consume(-1) }
    }
}