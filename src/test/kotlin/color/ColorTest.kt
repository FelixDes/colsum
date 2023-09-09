package color

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class ColorTest {
    companion object {
        @JvmStatic
        fun colorProvider(): List<Arguments> {
            return listOf(
                Arguments.of(
                    CssColor.fromHEX("#213"),
                    CssColor.fromRGBA(34, 17, 51)
                ),
                Arguments.of(
                    CssColor.fromHEX("#0F3A"),
                    CssColor.fromRGBA(0, 255, 51, 0.67)
                ),
                Arguments.of(
                    CssColor.fromHEX("#8E98E9"),
                    CssColor.fromRGBA(142, 152, 233)
                ),
                Arguments.of(
                    CssColor.fromHEX("#d2ec9659"),
                    CssColor.fromRGBA(210, 236, 150, 0.35)
                ),
                Arguments.of(
                    CssColor.fromHSLA(78, 69, 76),
                    CssColor.fromRGBA(210, 236, 151),
                ),
                Arguments.of(
                    CssColor.fromHSLA(116, 97, 45),
                    CssColor.fromRGBA(18, 226, 3)
                ),
            )
        }

        @JvmStatic
        fun colorCompositionProvider(): List<Arguments> {
            return listOf(
                Arguments.of(
                    listOf(
                        CssColor.fromRGBA(255, 255, 255),
                        CssColor.fromRGBA(0, 0, 0),
                        CssColor.fromRGBA(0, 0, 0),
                    ),
                    CssColor.fromRGBA(0, 0, 0)
                ),
                Arguments.of(
                    listOf(
                        CssColor.fromRGBA(255, 255, 255),
                        CssColor.fromRGBA(255, 0, 0, 0.5),
                        CssColor.fromRGBA(0, 255, 0, 0.5),
                    ),
                    CssColor.fromRGBA(127, 191, 63, 1.0)
                ),
                Arguments.of(
                    listOf(
                        CssColor.fromRGBA(255, 255, 255),
                        CssColor.fromRGBA(210, 236, 150, 0.35),
                        CssColor.fromRGBA(145, 0, 0, 0.32),
                    ),
                    CssColor.fromRGBA(208, 168, 148, 1.0)
                ),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("colorProvider")
    fun colorConstructionTest(color: CssColor, res: CssColor) {
        assertEquals(res, color)
    }

    @ParameterizedTest
    @MethodSource("colorCompositionProvider")
    fun colorCompositionTest(colors: List<CssColor>, res: CssColor) {
        assertEquals(res, colors.reduce { c1, c2 -> c1 + c2 })
    }
}