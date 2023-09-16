package translator.parser

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.assertAll
import translator.nodes.ASTNode
import kotlin.test.assertEquals

class EmptyParserTest : AnnotationSpec() {
    @Test
    fun consume_correct() {
        // when
        val parserResult = Parser.EmptyParser.consume(0)
        // then
        assertAll({ 0 shouldBe parserResult.posOffset }, {
            assertEquals(
                ASTNode.EmptyNode, parserResult.nodeList[0]
            )
        })
    }
}