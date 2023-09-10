package translator.tokenization

import translator.tokenization.TokenType.*

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
    Token(PARENTHESIS_OPEN, """^\(""".toRegex(), "("),
    Token(PARENTHESIS_CLOSE, """^\)""".toRegex(), ")"),

    Token(FUN_NAME, """^calc""".toRegex(), "calc"),
    Token(FUN_NAME, """^rgba?""".toRegex(), "rgb/rgba"),
    Token(FUN_NAME, """^hsla?""".toRegex(), "hsl/hsla"),
    Token(FUN_NAME, """^(ok)?hwb""".toRegex(), "okhwb/hwb"),
    Token(FUN_NAME, """^(ok)?lab""".toRegex(), "oklab/lab"),

    Token(COMMA_SEPARATOR, """^, *""".toRegex()),
    Token(SLASH_SEPARATOR, """^, *""".toRegex()),

    Token(NUMBER_NONE, """^none""".toRegex(), "none"),
    Token(NUMBER_EXP, """^e""".toRegex(), "exp"),
    Token(NUMBER_PI, """^pi""".toRegex(), "pi"),
    Token(NUMBER_NEG_INF, """^-infinity""".toRegex(), "neg_infinity"),
    Token(NUMBER_POS_INF, """^infinity""".toRegex(), "pos_infinity"),
    Token(NUMBER_NAN, """^NaN""".toRegex(), "NaN"),
    Token(NUMBER_PERCENT, """^[+\-]?(?:0|[1-9]\d*)(?:\.\d+)?(?:[eE][+\-]?\d+)?%""".toRegex(), "percentage"),
    Token(NUMBER, """^[+\-]?(?:0|[1-9]\d*)(?:\.\d+)?(?:[eE][+\-]?\d+)?""".toRegex(), "number"),

    Token(OPERATOR_PLUS, """^\+""".toRegex(), "+"),
    Token(OPERATOR_MINUS, """^-""".toRegex(), "-"),
    Token(OPERATOR_MUL, """^\*""".toRegex(), "*"),
    Token(OPERATOR_DIV, """^/""".toRegex(), "/"),

    Token(
        COLOR_CONST,
        """aliceblue|antiquewhite|aqua|aquamarine|azure|beige|bisque|black|blanchedalmond|blue|blueviolet|brown|burlywood|cadetblue|chartreuse|chocolate|coral|cornflowerblue|cornsilk|crimson|cyan|darkblue|darkcyan|darkgoldenrod|darkgray|darkgreen|darkgrey|darkkhaki|darkmagenta|darkolivegreen|darkorange|darkorchid|darkred|darksalmon|darkseagreen|darkslateblue|darkslategray|darkslategrey|darkturquoise|darkviolet|deeppink|deepskyblue|dimgray|dimgrey|dodgerblue|firebrick|floralwhite|forestgreen|fuchsia|gainsboro|ghostwhite|goldenrod|gold|gray|green|greenyellow|grey|honeydew|hotpink|indianred|indigo|ivory|khaki|lavenderblush|lavender|lawngreen|lemonchiffon|lightblue|lightcoral|lightcyan|lightgoldenrodyellow|lightgray|lightgreen|lightgrey|lightpink|lightsalmon|lightseagreen|lightskyblue|lightslategray|lightslategrey|lightsteelblue|lightyellow|lime|limegreen|linen|magenta|maroon|mediumaquamarine|mediumblue|mediumorchid|mediumpurple|mediumseagreen|mediumslateblue|mediumspringgreen|mediumturquoise|mediumvioletred|midnightblue|mintcream|mistyrose|moccasin|navajowhite|navy|oldlace|olive|olivedrab|orange|orangered|orchid|palegoldenrod|palegreen|paleturquoise|palevioletred|papayawhip|peachpuff|peru|pink|plum|powderblue|purple|rebeccapurple|red|rosybrown|royalblue|saddlebrown|salmon|sandybrown|seagreen|seashell|sienna|silver|skyblue|slateblue|slategray|slategrey|snow|springgreen|steelblue|tan|teal|thistle|tomato|turquoise|violet|wheat|white|whitesmoke|yellow|yellowgreen"""
            .toRegex(
                RegexOption.IGNORE_CASE
            ),
        "constant colors"
    ),

    Token(
        COLOR_HEX,
        """^#[0-9a-f]{6}([0-9a-f]{2})?|#[0-9a-f]{3}[0-9a-f]?""".toRegex(RegexOption.IGNORE_CASE)
    ),
)