package translator.parser

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import translator.nodes.ASTNode
import kotlin.test.assertEquals

class EmptyParserTest {
    @Test
    fun consume_correct() {
        // when
        val parserResult = Parser.EmptyParser.consume(0)
        // then
        assertAll({ assertEquals(0, parserResult.posOffset) }, {
            assertEquals(
                ASTNode.EmptyNode, parserResult.nodeList[0]
            )
        })
    }
}