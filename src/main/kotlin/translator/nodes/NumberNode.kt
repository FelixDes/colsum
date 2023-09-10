package translator.nodes

import translator.nodes.SemanticException.CODE.*
import translator.tokenization.TokenType
import translator.tokenization.TokenType.*
import java.math.BigDecimal

sealed class NumberNode private constructor(
    protected val value: Double = 0.0
) : ASTNode<Double>(), Calculable<NumberNode> {

    override fun compute(): Double = value

    protected abstract fun validateNode(other: NumberNode)

    data object NoneNode : NumberNode(0.0) {

        override fun plus(other: NumberNode): NumberNode {
            throw NONE_CALCULATION.get()
        }

        override fun minus(other: NumberNode): NumberNode {
            throw NONE_CALCULATION.get()
        }

        override fun div(other: NumberNode): NumberNode {
            throw NONE_CALCULATION.get()
        }

        override fun times(other: NumberNode): NumberNode {
            throw NONE_CALCULATION.get()
        }

        override fun validateNode(other: NumberNode) {
            if (other !is NoneNode) throw IMPOSSIBLE_CAST.get()
        }
    }

    class DoubleNode(value: Double) : NumberNode(value) {

        override fun plus(other: NumberNode): NumberNode {
            validateNode(other)
            return DoubleNode(value + other.value)
        }

        override fun minus(other: NumberNode): NumberNode {
            validateNode(other)
            return DoubleNode(value - other.value)
        }

        override fun div(other: NumberNode): NumberNode {
            validateNode(other)
            return DoubleNode(value / other.value)
        }

        override fun times(other: NumberNode): NumberNode {
            validateNode(other)
            return DoubleNode(value * other.value)
        }

        override fun validateNode(other: NumberNode) {
            if (other !is DoubleNode) throw IMPOSSIBLE_CAST.get()
        }
    }

//    class AngleNode(value: Double) : NumberNode(value) {
//
//        override fun plus(other: NumberNode): NumberNode {
//            validateNode(other)
//            return AngleNode(value + other.value)
//        }
//
//        override fun minus(other: NumberNode): NumberNode {
//            validateNode(other)
//            return AngleNode(value - other.value)
//        }
//
//        override fun div(other: NumberNode): NumberNode {
//            validateNode(other)
//            return AngleNode(value / other.value)
//        }
//
//        override fun times(other: NumberNode): NumberNode {
//            validateNode(other)
//            return AngleNode(value * other.value)
//        }
//
//        override fun validateNode(other: NumberNode) {
//            if (other !is AngleNode) throw IMPOSSIBLE_CAST.get()
//        }
//    }

    class DoublePercentNode(value: Double) : NumberNode(value) {

        override fun plus(other: NumberNode): NumberNode {
            validateNode(other)
            return DoublePercentNode(value + other.value)
        }

        override fun minus(other: NumberNode): NumberNode {
            validateNode(other)
            return DoublePercentNode(value - other.value)
        }

        override fun div(other: NumberNode): NumberNode {
            validateNode(other)
            return DoublePercentNode(value / other.value)
        }

        override fun times(other: NumberNode): NumberNode {
            validateNode(other)
            return DoublePercentNode(value * other.value)
        }

        override fun validateNode(other: NumberNode) {
            if (other !is DoublePercentNode) throw IMPOSSIBLE_CAST.get()
        }
    }

    companion object {
        fun buildNone(): NumberNode = NoneNode

        fun buildPercent(value: Double): NumberNode = DoublePercentNode(value)

        fun buildPercent(value: String): NumberNode =
            buildPercent(BigDecimal(value.substringBefore('%')).toDouble())


        fun buildNumber(value: Double): NumberNode = DoubleNode(value)

        fun buildNumber(value: String): NumberNode = buildNumber(BigDecimal(value).toDouble())

        fun buildSpecific(value: TokenType): NumberNode = when (value) {
            NUMBER_EXP -> buildNumber(Math.E)
            NUMBER_PI -> buildNumber(Math.PI)
            NUMBER_NEG_INF -> buildNumber(Double.NEGATIVE_INFINITY)
            NUMBER_POS_INF -> buildNumber(Double.POSITIVE_INFINITY)
            NUMBER_NAN -> buildNumber(Double.NaN)
            else -> throw UNKNOWN_NUMBER_TOKEN.get(value.toString())
        }
    }
}