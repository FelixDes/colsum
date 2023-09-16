//package translator.parser
//
//import io.kotest.assertions.assertSoftly
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.datatest.withData
//import io.kotest.matchers.shouldBe
//import translator.nodes.ASTNode.EmptyNode
//import translator.nodes.NumberNode
//import translator.tokenization.TokenType
//import translator.tokenization.TokenType.*
//
//class WhileSeparatorParserTest : FunSpec({
//
//    data class TestData(
//        val tokens: List<Pair<TokenType, String>>,
//        val separatorParser: Parser,
//        val expectedNodes: List<NumberNode>
//    )
//
//    withData(
//        TestData(
//            listOf(
//                FUN_NAME to "rgb",
//                PARENTHESIS_OPEN to "(",
//                NUMBER to "1",
//                COMMA_SEPARATOR to ", ",
//                NUMBER to "2",
//                COMMA_SEPARATOR to ", ",
//                NUMBER to "3",
//                PARENTHESIS_CLOSE to ")",
//            ),
//            COMMA_SEPARATOR,
//            listOf(
//                NumberNode.buildNumber(1.0),
//                NumberNode.buildNumber(2.0),
//                NumberNode.buildNumber(3.0),
//            )
//        ),
//        TestData(
//            listOf(
//                FUN_NAME to "rgb",
//                PARENTHESIS_OPEN to "(",
//                NUMBER to "1",
//                NUMBER to "2",
//                NUMBER to "3",
//                PARENTHESIS_CLOSE to ")",
//            ),
//            Parser.EmptyParser,
//            listOf(
//                NumberNode.buildNumber(1.0),
//                NumberNode.buildNumber(2.0),
//                NumberNode.buildNumber(3.0),
//            )
//        )
//    ) { (tokens, separator, res) ->
//        // given
//        val parser = Parser.WhileSeparatorParser(
//            tokens, Parser.NumberParser(tokens),
//        )
//        // when
//        val parserResult = parser.consumeDelegate(2)
//        // then
//        assertSoftly {
//            parserResult.posOffset shouldBe tokens.size
//        }
//    }
//})