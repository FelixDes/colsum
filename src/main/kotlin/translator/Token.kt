package translator

enum class TokenType {
    FUN_NAME, SEPARATOR,
    HEX_COLOR,
    NUMBER, NUMBER_PERCENT, NUMBER_NONE, NUMBER_EXP, NUMBER_PI, NUMBER_NEG_INF, NUMBER_POS_INF, NUMBER_NAN,
    OPERATOR_PLUS, OPERATOR_MINUS, OPERATOR_MUL, OPERATOR_DIV,
    PARENTHESIS_OPEN, PARENTHESIS_CLOSE;
}

data class Token(
    val tokenType: TokenType,
    val regex: Regex,
    val comment: String = tokenType.toString()
)

val CSS_EXPRESSION_TOKENS: List<Token> = listOf(
    Token(TokenType.PARENTHESIS_OPEN, """^\(""".toRegex(), "("),
    Token(TokenType.PARENTHESIS_CLOSE, """^\)""".toRegex(), ")"),

    Token(TokenType.OPERATOR_PLUS, """^ \+ """.toRegex(), "+"),
    Token(TokenType.OPERATOR_MINUS, """^ - """.toRegex(), "-"),
    Token(TokenType.OPERATOR_MUL, """^ \* """.toRegex(), "*"),
    Token(TokenType.OPERATOR_DIV, """^ / """.toRegex(), "/"),

    Token(TokenType.FUN_NAME, """^rgba?""".toRegex(), "rgb/rgba"),
    Token(TokenType.FUN_NAME, """^hsla?""".toRegex(), "hsl/hsla"),
    Token(TokenType.FUN_NAME, """^(ok)?hwb""".toRegex(), "okhwb/hwb"),
    Token(TokenType.FUN_NAME, """^(ok)?lab""".toRegex(), "oklab/lab"),

    Token(TokenType.SEPARATOR, """^, *""".toRegex()),

    Token(TokenType.NUMBER, """^[+\-]?(?:0|[1-9]\d*)(?:\.\d+)?(?:[eE][+\-]?\d+)?""".toRegex(), "number"),
    Token(TokenType.NUMBER_PERCENT, """^[+\-]?(?:0|[1-9]\d*)(?:\.\d+)?(?:[eE][+\-]?\d+)?%""".toRegex(), "percentage"),
    Token(TokenType.NUMBER_NONE, """^none""".toRegex(), "none"),
    Token(TokenType.NUMBER_EXP, """^e""".toRegex(), "exp"),
    Token(TokenType.NUMBER_PI, """^pi""".toRegex(), "pi"),
    Token(TokenType.NUMBER_NEG_INF, """^-infinity""".toRegex(), "neg_infinity"),
    Token(TokenType.NUMBER_POS_INF, """^infinity""".toRegex(), "pos_infinity"),
    Token(TokenType.NUMBER_NAN, """^NaN""".toRegex(), "NaN"),

    Token(
        TokenType.HEX_COLOR,
        """^#[0-9a-f]{6}([0-9a-f]{2})?|#[0-9a-f]{3}[0-9a-f]?""".trimIndent().toRegex()
    ),
)