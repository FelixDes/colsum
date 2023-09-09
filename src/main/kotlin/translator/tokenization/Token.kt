package translator.tokenization

enum class TokenType {
    FUN_NAME,
    COMMA_SEPARATOR, SLASH_SEPARATOR,
    COLOR_HEX, COLOR_CONST,
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

    Token(TokenType.FUN_NAME, """^calc""".toRegex(), "calc"),
    Token(TokenType.FUN_NAME, """^rgba?""".toRegex(), "rgb/rgba"),
    Token(TokenType.FUN_NAME, """^hsla?""".toRegex(), "hsl/hsla"),
    Token(TokenType.FUN_NAME, """^(ok)?hwb""".toRegex(), "okhwb/hwb"),
    Token(TokenType.FUN_NAME, """^(ok)?lab""".toRegex(), "oklab/lab"),

    Token(TokenType.COMMA_SEPARATOR, """^, *""".toRegex()),
    Token(TokenType.SLASH_SEPARATOR, """^, *""".toRegex()),

    Token(TokenType.NUMBER_NONE, """^none""".toRegex(), "none"),
    Token(TokenType.NUMBER_EXP, """^e""".toRegex(), "exp"),
    Token(TokenType.NUMBER_PI, """^pi""".toRegex(), "pi"),
    Token(TokenType.NUMBER_NEG_INF, """^-infinity""".toRegex(), "neg_infinity"),
    Token(TokenType.NUMBER_POS_INF, """^infinity""".toRegex(), "pos_infinity"),
    Token(TokenType.NUMBER_NAN, """^NaN""".toRegex(), "NaN"),
    Token(TokenType.NUMBER_PERCENT, """^[+\-]?(?:0|[1-9]\d*)(?:\.\d+)?(?:[eE][+\-]?\d+)?%""".toRegex(), "percentage"),
    Token(TokenType.NUMBER, """^[+\-]?(?:0|[1-9]\d*)(?:\.\d+)?(?:[eE][+\-]?\d+)?""".toRegex(), "number"),

    Token(TokenType.OPERATOR_PLUS, """^\+""".toRegex(), "+"),
    Token(TokenType.OPERATOR_MINUS, """^-""".toRegex(), "-"),
    Token(TokenType.OPERATOR_MUL, """^\*""".toRegex(), "*"),
    Token(TokenType.OPERATOR_DIV, """^/""".toRegex(), "/"),

    Token(
        TokenType.COLOR_CONST,
        """aliceblue|antiquewhite|aqua|aquamarine|azure|beige|bisque|black|blanchedalmond|blue|blueviolet|brown|burlywood|cadetblue|chartreuse|chocolate|coral|cornflowerblue|cornsilk|crimson|cyan|darkblue|darkcyan|darkgoldenrod|darkgray|darkgreen|darkgrey|darkkhaki|darkmagenta|darkolivegreen|darkorange|darkorchid|darkred|darksalmon|darkseagreen|darkslateblue|darkslategray|darkslategrey|darkturquoise|darkviolet|deeppink|deepskyblue|dimgray|dimgrey|dodgerblue|firebrick|floralwhite|forestgreen|fuchsia|gainsboro|ghostwhite|goldenrod|gold|gray|green|greenyellow|grey|honeydew|hotpink|indianred|indigo|ivory|khaki|lavenderblush|lavender|lawngreen|lemonchiffon|lightblue|lightcoral|lightcyan|lightgoldenrodyellow|lightgray|lightgreen|lightgrey|lightpink|lightsalmon|lightseagreen|lightskyblue|lightslategray|lightslategrey|lightsteelblue|lightyellow|lime|limegreen|linen|magenta|maroon|mediumaquamarine|mediumblue|mediumorchid|mediumpurple|mediumseagreen|mediumslateblue|mediumspringgreen|mediumturquoise|mediumvioletred|midnightblue|mintcream|mistyrose|moccasin|navajowhite|navy|oldlace|olive|olivedrab|orange|orangered|orchid|palegoldenrod|palegreen|paleturquoise|palevioletred|papayawhip|peachpuff|peru|pink|plum|powderblue|purple|rebeccapurple|red|rosybrown|royalblue|saddlebrown|salmon|sandybrown|seagreen|seashell|sienna|silver|skyblue|slateblue|slategray|slategrey|snow|springgreen|steelblue|tan|teal|thistle|tomato|turquoise|violet|wheat|white|whitesmoke|yellow|yellowgreen"""
            .toRegex(
            RegexOption.IGNORE_CASE
        ),
        "constant colors"
    ),

    Token(
        TokenType.COLOR_HEX,
        """^#[0-9a-f]{6}([0-9a-f]{2})?|#[0-9a-f]{3}[0-9a-f]?""".toRegex(RegexOption.IGNORE_CASE)
    ),
)