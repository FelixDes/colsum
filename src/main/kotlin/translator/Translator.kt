package translator

import color.CssColor
import translator.parser.Parser
import translator.tokenization.Tokenizer

fun translate(mainExpression: String, backgroundExpression: String): CssColor {
    val mainTokens = Tokenizer.tokenize(mainExpression)

    val backgroundColor = Parser.ColorParser(Tokenizer.tokenize(backgroundExpression)).consume(0).nodeList[0].compute()
    val color = Parser.ExpressionParser(mainTokens, Parser.ColorParser(mainTokens)).consume(0).nodeList[0].compute()

    return backgroundColor + color
}