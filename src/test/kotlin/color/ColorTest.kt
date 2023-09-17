package color

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import kotlin.test.assertFailsWith


class ColorTest : FunSpec({
    data class TestData(val colors: List<CssColor>, val resColor: CssColor)

    context("Operations") {

        test("minus") {
            assertFailsWith<UnsupportedOperationException> { CssColor.fromHEX("#213") - CssColor.fromRGBA(34, 17, 51) }
        }

        test("div") {
            assertFailsWith<UnsupportedOperationException> { CssColor.fromHEX("#213") / CssColor.fromRGBA(34, 17, 51) }
        }

        test("times") {
            assertFailsWith<UnsupportedOperationException> { CssColor.fromHEX("#213") * CssColor.fromRGBA(34, 17, 51) }
        }

        test("plus") {
            this@context.withData(
                TestData(
                    listOf(CssColor.fromHEX("#213")),
                    CssColor.fromRGBA(34, 17, 51)
                ),
                TestData(
                    listOf(CssColor.fromHEX("#0F3A")),
                    CssColor.fromRGBA(0, 255, 51, 0.67)
                ),
                TestData(
                    listOf(CssColor.fromHEX("#8E98E9")),
                    CssColor.fromRGBA(142, 152, 233)
                ),
                TestData(
                    listOf(CssColor.fromHEX("#d2ec9659")),
                    CssColor.fromRGBA(210, 236, 150, 0.35)
                ),
                TestData(
                    listOf(CssColor.fromHSLA(78, 69, 76)),
                    CssColor.fromRGBA(211, 236, 152),
                ),
                TestData(
                    listOf(CssColor.fromHSLA(438, 69, 76)),
                    CssColor.fromRGBA(211, 236, 152),
                ),
                TestData(
                    listOf(CssColor.fromHSLA(130, 70, 70, 0.5)),
                    CssColor.fromRGBA(125, 232, 143, 0.5),
                ),
                TestData(
                    listOf(CssColor.fromHSLA(-282, 69, 76)),
                    CssColor.fromRGBA(211, 236, 152),
                ),
                TestData(
                    listOf(CssColor.fromHSLA(116, 97, 45)),
                    CssColor.fromRGBA(18, 226, 3)
                ),
                TestData(
                    listOf(
                        CssColor.fromRGBA(255, 255, 255),
                        CssColor.fromRGBA(0, 0, 0),
                        CssColor.fromRGBA(0, 0, 0),
                    ),
                    CssColor.fromRGBA(0, 0, 0)
                ),
                TestData(
                    listOf(
                        CssColor.fromRGBA(255, 255, 255),
                        CssColor.fromRGBA(255, 0, 0, 0.5),
                        CssColor.fromRGBA(0, 255, 0, 0.5),
                    ),
                    CssColor.fromRGBA(127, 191, 63, 1.0)
                ),
                TestData(
                    listOf(
                        CssColor.fromRGBA(255, 255, 255),
                        CssColor.fromRGBA(210, 236, 150, 0.35),
                        CssColor.fromRGBA(145, 0, 0, 0.32),
                    ),
                    CssColor.fromRGBA(208, 168, 148, 1.0)
                ),
            ) { (colors, resColor) ->
                colors.sum() shouldBe resColor
            }
        }
    }
})