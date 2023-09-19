package translator.integrational

import color.CssColor
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import translator.translate

class TranslatorTest : FunSpec({

    data class TestData(val expression: String, val background: String, val expected: CssColor)

    withData(
        listOf(
            TestData(
                "#000 + #111",
                "#FFF",
                CssColor.fromHEX("#111")
            ),
            TestData(
                "aqua + rgb(105,99,114, 0.7)",
                "white",
                CssColor.fromRGBA(73, 145, 156, 1.0)
            ),
            TestData(
                "hsl(855, 47%, 13%, 0.7) + rgb(55, 25, 0, 0.5)",
                "white",
                CssColor.fromRGBA(71, 67, 46, 1.0)
            ),
        )
    ) { (mainExpression, backgroundExpression, resColor) ->
        // when
        val parserResult = translate(mainExpression, backgroundExpression)
        // then
        parserResult shouldBe resColor
    }
})
