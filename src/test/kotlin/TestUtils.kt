import translator.TokenType

internal object TestUtils {
    fun generateTokenSequence(): List<Pair<TokenType, String>> {
        return listOf(
            Pair(TokenType.FUN_NAME, "rgba"),
            Pair(TokenType.PARENTHESIS_OPEN, "("),
            Pair(TokenType.NUMBER, "123"),
            Pair(TokenType.SEPARATOR, ", "),
            Pair(TokenType.NUMBER, "3%"),
            Pair(TokenType.SEPARATOR, ", "),
            Pair(TokenType.NUMBER, "2"),
            Pair(TokenType.SEPARATOR, ", "),
            Pair(TokenType.NUMBER, "1.3"),
            Pair(TokenType.OPERATOR_DIV, "/"),
            Pair(TokenType.NUMBER, "4"),
            Pair(TokenType.PARENTHESIS_CLOSE, ")"),
            Pair(TokenType.OPERATOR_PLUS, "+"),
            Pair(TokenType.FUN_NAME, "hsl"),
            Pair(TokenType.PARENTHESIS_OPEN, "("),
            Pair(TokenType.NUMBER, "123"),
            Pair(TokenType.NUMBER, "2"),
            Pair(TokenType.OPERATOR_MUL, "*"),
            Pair(TokenType.NUMBER, "2"),
            Pair(TokenType.NUMBER, "4"),
            Pair(TokenType.PARENTHESIS_CLOSE, ")"),
            Pair(TokenType.OPERATOR_MINUS, "-"),
            Pair(TokenType.HEX_COLOR, "#009900"),
        )
    }
}