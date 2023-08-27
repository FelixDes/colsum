import translator.Token
import translator.Tokenizer

fun analyser(tokens: List<Pair<Token, String>>) {
//    val r = Parser.RootParser<>(tokens)
}

fun main(args: Array<String>) {
//    val test_string = "calc(calc(2 + 3% / 4) - 32)"
    val test_string = "3% - 2% + 10%"
    val tokens = Tokenizer.tokenize(test_string)


//    analyser(tokens)
}
