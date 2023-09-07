package translator

import CssColor
import translator.parser.Parser
import translator.tokenization.Tokenizer


fun translate(mainExpression: String, backgroundExpression: String): CssColor {
    val mainTokens = Tokenizer.tokenize(mainExpression)
    val backgroundTokens = Tokenizer.tokenize(backgroundExpression)

    val backgroundColor = Parser.ColorParser(backgroundTokens).consume(0).nodeList[0].compute()
    val color = Parser.ExpressionParser(mainTokens, Parser.ColorParser(mainTokens)).consume(0).nodeList[0].compute()

    return backgroundColor + color
}