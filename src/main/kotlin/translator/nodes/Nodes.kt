package translator.nodes

import translator.parser.ParseException
import translator.tokenization.TokenType

class SemanticException(message: String) : Exception(message)

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
        override fun compute(): ResT {
            return when (operation) {
                TokenType.OPERATOR_PLUS -> leftNode + rightNode
                TokenType.OPERATOR_MINUS -> leftNode - rightNode
                TokenType.OPERATOR_MUL -> leftNode * rightNode
                TokenType.OPERATOR_DIV -> leftNode / rightNode
                else -> throw ParseException("Unknown operation: $operation")
            }
        }
    }

    class FunctionRepresentationNode<FunctionArgT>(private val rep: FunctionRepresentation<FunctionArgT>) :
        ASTNode<FunctionRepresentationNode.FunctionRepresentation<FunctionArgT>>() {
        data class FunctionRepresentation<ResT>(
            val name: String,
            val argNodes: List<ResT>,
            val posOffset: Int
        )

        override fun compute(): FunctionRepresentation<FunctionArgT> {
            return rep
        }
    }
}