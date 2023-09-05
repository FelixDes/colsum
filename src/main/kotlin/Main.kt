import translator.ASTNode
import translator.Parser
import translator.Tokenizer

fun main(args: Array<String>) {
//    val parser = ArgParser("colsum")
//    val inputString by parser.option(ArgType.String, shortName = "i", description = "String for computation").required()
//    parser.parse(args)
//

    val inputString = "calc(calc(2 + 3 / 4) - 32)"
    val tokens = Tokenizer.tokenize(inputString)
    println(Parser.FunctionParser.CalcFunctionParser(tokens).consume(0).nodeList[0].compute())
}
