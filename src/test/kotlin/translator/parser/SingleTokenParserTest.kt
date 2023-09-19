package translator.parser

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import translator.nodes.ASTNode
import translator.tokenization.TokenType.PARENTHESIS_OPEN
import kotlin.test.assertFailsWith

class SingleTokenParserTest : AnnotationSpec() {
    private val tokenSequence = listOf(
        PARENTHESIS_OPEN to "("
    )

    @Test
    fun consume_correct() {
        // given
        val parser = Parser.SingleTokenParser(tokenSequence, PARENTHESIS_OPEN)
        // when
        val parserResult = parser.consume(0)
        // then
        assertSoftly {
            parserResult.posOffset shouldBe tokenSequence.size
            ASTNode.TokenNode(tokenSequence[0].first).compute() shouldBe parserResult.nodeList[0].compute()
        }
    }

    @Test
    fun consume_incorrect() {
        // given
        val parser = Parser.SingleTokenParser(tokenSequence, PARENTHESIS_OPEN)
        // when + then
        assertFailsWith<ParsingException> { parser.consume(-1) }
    }
}