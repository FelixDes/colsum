package translator.tokenization

import color.ConstantColorsService
import translator.tokenization.TokenType.*

enum class TokenType {
    FUN_NAME,
    COMMA_SEPARATOR, SLASH_SEPARATOR,
    COLOR_HEX, COLOR_CONST,
    NUMBER, NUMBER_PERCENT, NUMBER_NONE, NUMBER_EXP, NUMBER_PI, NUMBER_NEG_INF, NUMBER_POS_INF, NUMBER_NAN,
    NUMBER_ANGLE_DEG, NUMBER_ANGLE_GRAD, NUMBER_ANGLE_TURN, NUMBER_ANGLE_RAD,
    OPERATOR_PLUS, OPERATOR_MINUS, OPERATOR_MUL, OPERATOR_DIV,
    PARENTHESIS_OPEN, PARENTHESIS_CLOSE;
}

data class Token(
    val tokenType: TokenType,
    val regex: Regex,
    val comment: String = tokenType.toString()
)

const val numberRegex = """^[+\-]?(?:0|[1-9]\d*)(?:\.\d+)?(?:[eE][+\-]?\d+)?"""

val CSS_EXPRESSION_TOKENS: List<Token> = listOf(
    Token(PARENTHESIS_OPEN, """^\(""".toRegex(), "("),
    Token(PARENTHESIS_CLOSE, """^\)""".toRegex(), ")"),

    Token(FUN_NAME, """^calc""".toRegex(), "calc"),
    Token(FUN_NAME, """^rgba?""".toRegex(), "rgb/rgba"),
    Token(FUN_NAME, """^hsla?""".toRegex(), "hsl/hsla"),
    Token(FUN_NAME, """^(ok)?hwb""".toRegex(), "okhwb/hwb"),
    Token(FUN_NAME, """^(ok)?lab""".toRegex(), "oklab/lab"),

    Token(NUMBER_NONE, """^none""".toRegex(), "none"),
    Token(NUMBER_EXP, """^e""".toRegex(), "exp"),
    Token(NUMBER_PI, """^pi""".toRegex(), "pi"),
    Token(NUMBER_NEG_INF, """^-infinity""".toRegex(), "neg_infinity"),
    Token(NUMBER_POS_INF, """^infinity""".toRegex(), "pos_infinity"),
    Token(NUMBER_NAN, """^NaN""".toRegex(), "NaN"),

    Token(NUMBER_ANGLE_DEG, """${numberRegex}deg""".toRegex(), "angle in degrees"),
    Token(NUMBER_ANGLE_GRAD, """${numberRegex}grad""".toRegex(), "angle in grads"),
    Token(NUMBER_ANGLE_TURN, """${numberRegex}turn""".toRegex(), "angle in 'turns'"),
    Token(NUMBER_ANGLE_RAD, """${numberRegex}rad""".toRegex(), "angle in radians"),

    Token(NUMBER_PERCENT, """$numberRegex%""".toRegex(), "percentage"),
    Token(NUMBER, numberRegex.toRegex(), "number"),

    Token(OPERATOR_PLUS, """^\+""".toRegex(), "+"),
    Token(OPERATOR_MINUS, """^-""".toRegex(), "-"),
    Token(OPERATOR_MUL, """^\*""".toRegex(), "*"),
    Token(OPERATOR_DIV, """^/""".toRegex(), "/"),

    Token(COMMA_SEPARATOR, """^, *""".toRegex()),
    Token(SLASH_SEPARATOR, """^/ *""".toRegex()),

    Token(COLOR_CONST, ConstantColorsService.getNamesRegex(), "constant colors"),

    Token(COLOR_HEX, """^#[0-9a-f]{6}([0-9a-f]{2})?|#[0-9a-f]{3}[0-9a-f]?""".toRegex(RegexOption.IGNORE_CASE)),
)