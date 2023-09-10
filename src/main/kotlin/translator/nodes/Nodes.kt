package translator.nodes

import translator.parser.ParseException
import translator.tokenization.TokenType
import translator.tokenization.TokenType.*

class SemanticException private constructor(val code: CODE?, e: Throwable?, msg: String?) :
    Exception() {
    private constructor(code: CODE?, msg: String?) : this(code, null, msg)

    enum class CODE(private val description: String) {
        NONE_CALCULATION("Cannot do calculation on `none`"),
        IMPOSSIBLE_CAST("Impossible cast"),
        UNKNOWN_NUMBER_TOKEN("Unknown specific number token"),
        UNKNOWN_FUNCTION("Unknown function"),
        ALPHA_ARGUMENT_ERROR("Alpha argument error"),
        ;

        fun get(): SemanticException = SemanticException(this, description)
        fun get(msg: String): SemanticException = SemanticException(this, "$description: $msg")
        fun get(e: Throwable): SemanticException = SemanticException(this, e, description)
        fun get(e: Throwable, msg: String): SemanticException =
            SemanticException(this, e, "$description: $msg")
    }
}

interface Calculable<ResT> {
    operator fun plus(other: ResT): ResT
    operator fun minus(other: ResT): ResT
    operator fun div(other: ResT): ResT
    operator fun times(other: ResT): ResT
}

sealed class ASTNode<ResT> {
    abstract fun compute(): ResT

    data object EmptyNode : ASTNode<Unit>() {

        override fun compute() = Unit
    }

    class TokenNode(private val tokenType: TokenType) : ASTNode<TokenType>() {

        override fun compute() = tokenType
    }

    class LexemeNode(private val lexeme: String) : ASTNode<String>() {

        override fun compute() = lexeme
    }

    class CalculatingNode<ResT : Calculable<ResT>>(
        private val operation: TokenType,
        private val leftNode: ResT,
        private val rightNode: ResT,
    ) : ASTNode<ResT>() {
        override fun compute(): ResT = when (operation) {
            OPERATOR_PLUS -> leftNode + rightNode
            OPERATOR_MINUS -> leftNode - rightNode
            OPERATOR_MUL -> leftNode * rightNode
            OPERATOR_DIV -> leftNode / rightNode
            else -> throw ParseException("Unknown operation: $operation")
        }
    }

    class FunctionRepresentationNode<FunctionArgT>(private val rep: FunctionRepresentation<FunctionArgT>) :
        ASTNode<FunctionRepresentationNode.FunctionRepresentation<FunctionArgT>>() {
        data class FunctionRepresentation<ResT>(
            val name: String,
            val argNodes: List<ResT>,
            val posOffset: Int
        )

        override fun compute(): FunctionRepresentation<FunctionArgT> = rep
    }
}