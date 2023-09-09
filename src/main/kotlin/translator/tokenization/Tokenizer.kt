package translator.tokenization

import kotlin.math.min

class TokenizerException(message: String) : Exception(message)

class Tokenizer {
    companion object {
        fun tokenize(inputStr: String): List<Pair<TokenType, String>> {
            var input = inputStr
            val resulTokens = ArrayList<Pair<TokenType, String>>()

            while (input.isNotBlank()) {
                var match: MatchResult? = null
                for (token in CSS_EXPRESSION_TOKENS) {
                    match = token.regex.find(input.trim())
                    if (match != null) {
                        resulTokens.add(token.tokenType to match.value)
                        input = input.substring(match.range.last() + countSpacesInStart(input) + 1)
                        break
                    }
                }
                if (match == null) {
                    throw TokenizerException(
                        "Unknown token sequence for: ${input.substring(0, min(input.length, 10))}"
                    )
                }
            }
            return resulTokens
        }

        private fun countSpacesInStart(str: String): Int {
            var spaces = 0
            for (c in str) {
                if (c == ' ') spaces++
                else break
            }
            return spaces
        }
    }
}

