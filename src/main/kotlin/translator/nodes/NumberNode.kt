package translator.nodes

import translator.tokenization.TokenType
import java.math.BigDecimal

sealed class NumberNode private constructor(
    protected val value: Double = 0.0
) : ASTNode<Double>(), Calculable<NumberNode> {

    override fun compute(): Double {
        return value
    }

    data object NoneNode : NumberNode(0.0) {
        override fun plus(other: NumberNode): NumberNode {
            throw SemanticException("Cannot do calculation on `none`")
        }

        override fun minus(other: NumberNode): NumberNode {
            throw SemanticException("Cannot do calculation on `none`")
        }

        override fun div(other: NumberNode): NumberNode {
            throw SemanticException("Cannot do calculation on `none`")
        }

        override fun times(other: NumberNode): NumberNode {
            throw SemanticException("Cannot do calculation on `none`")
        }
    }

    class DoubleNode(value: Double) : NumberNode(value) {

        override fun plus(other: NumberNode): NumberNode {
            if (other !is DoubleNode) throw SemanticException("Impossible cast")
            return DoubleNode(value + other.value)
        }

        override fun minus(other: NumberNode): NumberNode {
            if (other !is DoubleNode) throw SemanticException("Impossible cast")
            return DoubleNode(value - other.value)
        }

        override fun div(other: NumberNode): NumberNode {
            if (other !is DoubleNode) throw SemanticException("Impossible cast")
            return DoubleNode(value / other.value)
        }

        override fun times(other: NumberNode): NumberNode {
            if (other !is DoubleNode) throw SemanticException("Impossible cast")
            return DoubleNode(value * other.value)
        }
    }

//        class AngleNode(value: Double) : NumberNode(value) {
//
//            override fun plus(other: NumberNode): NumberNode {
//                if (other !is AngleNode) throw SemanticException("Impossible cast")
//                return AngleNode(value + other.value)
//            }
//
//            override fun minus(other: NumberNode): NumberNode {
//                if (other !is AngleNode) throw SemanticException("Impossible cast")
//                return AngleNode(value - other.value)
//            }
//
//            override fun div(other: NumberNode): NumberNode {
//                if (other !is AngleNode) throw SemanticException("Impossible cast")
//                return AngleNode(value / other.value)
//            }
//
//            override fun times(other: NumberNode): NumberNode {
//                if (other !is AngleNode) throw SemanticException("Impossible cast")
//                return AngleNode(value * other.value)
//            }
//        }

    class DoublePercentNode(value: Double) : NumberNode(value) {

        override fun plus(other: NumberNode): NumberNode {
            if (other !is DoublePercentNode) throw SemanticException("Impossible cast")
            return DoublePercentNode(value + other.value)
        }

        override fun minus(other: NumberNode): NumberNode {
            if (other !is DoublePercentNode) throw SemanticException("Impossible cast")
            return DoublePercentNode(value - other.value)
        }

        override fun div(other: NumberNode): NumberNode {
            if (other !is DoublePercentNode) throw SemanticException("Impossible cast")
            return DoublePercentNode(value / other.value)
        }

        override fun times(other: NumberNode): NumberNode {
            if (other !is DoublePercentNode) throw SemanticException("Impossible cast")
            return DoublePercentNode(value * other.value)
        }

    }

    companion object {
        fun buildNone(): NumberNode {
            return NoneNode
        }

        fun buildPercent(value: Double): NumberNode {
            return DoublePercentNode(value)
        }

        fun buildPercent(value: String): NumberNode {
            return buildPercent(BigDecimal(value.substringBefore('%')).toDouble())
        }

        fun buildNumber(value: Double): NumberNode {
            return DoubleNode(value)
        }

        fun buildNumber(value: String): NumberNode {
            return buildNumber(BigDecimal(value).toDouble())
        }

        fun buildSpecific(value: TokenType): NumberNode {
            return when (value) {
                TokenType.NUMBER_EXP -> buildNumber(Math.E)
                TokenType.NUMBER_PI -> buildNumber(Math.PI)
                TokenType.NUMBER_NEG_INF -> buildNumber(Double.NEGATIVE_INFINITY)
                TokenType.NUMBER_POS_INF -> buildNumber(Double.POSITIVE_INFINITY)
                TokenType.NUMBER_NAN -> buildNumber(Double.NaN)
                else -> throw SemanticException("Unknown specific number token: $value")
            }
        }
    }
}