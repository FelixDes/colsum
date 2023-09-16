package translator.parser

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import translator.nodes.NumberNode
import translator.tokenization.TokenType
import translator.tokenization.TokenType.*

class ArgSeparatorListParserTest : FunSpec({

    data class TestData(
        val tokens: List<Pair<TokenType, String>>,
        val separators: List<TokenType>,
        val expectedOffset: Int,
        val expectedNodes: List<NumberNode>
    )

    withData(
        listOf(
            TestData(
                listOf(
                    FUN_NAME to "function",
                    PARENTHESIS_OPEN to "(",
                    NUMBER to "2",
                    COMMA_SEPARATOR to ", ",
                    NUMBER to "3",
                    SLASH_SEPARATOR to " / ",
                    NUMBER to "5",
                    PARENTHESIS_CLOSE to ")",
                ),
                listOf(
                    COMMA_SEPARATOR,
                    SLASH_SEPARATOR
                ),
                5,
                listOf(
                    NumberNode.buildNumber(2.0),
                    NumberNode.buildNumber(3.0),
                    NumberNode.buildNumber(5.0),
                )
            )
        )
    ) { (tokens, separators, offset, nodes) ->
        // given
        val parser = Parser.ArgSeparatorListParser(
            tokens, Parser.NumberParser(tokens),
            separators.map { Parser.SingleTokenParser(tokens, it) }
        )
        // when
        val parserResult = parser.consume(2)
        // then
        assertSoftly {
            parserResult.posOffset shouldBe offset
            parserResult.nodeList.map { it.compute() } shouldBe nodes.map { it.compute() }
        }
    }
})