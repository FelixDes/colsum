import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import translator.translate

fun main(args: Array<String>) {
    val parser = ArgParser("colsum")
    val mainExpression by parser.option(
        ArgType.String,
        shortName = "e",
        fullName = "expression",
        description = "String for computation"
    ).required()
    val backgroundColor by parser.option(
        ArgType.String,
        shortName = "b",
        fullName = "background",
        description = "background color"
    ).default("#FFF")
    parser.parse(args)

    println(translate(mainExpression, backgroundColor).toString())
}
