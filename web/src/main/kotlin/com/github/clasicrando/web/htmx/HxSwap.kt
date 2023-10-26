package com.github.clasicrando.web.htmx

import io.ktor.server.html.Placeholder
import kotlinx.html.FlowContent
import kotlin.time.DurationUnit

private const val HX_SWAP = "hx-swap"
private const val DEFAULT_SWAP_TIME = 0
private const val DEFAULT_SETTLE_TIME = 20

data class HxSwap(
    val swapType: SwapType,
    val swapTime: Int = DEFAULT_SWAP_TIME,
    val swapTimeUnit: DurationUnit = DurationUnit.MILLISECONDS,
    val settleTime: Int = DEFAULT_SETTLE_TIME,
    val settleTimeUnit: DurationUnit = DurationUnit.MILLISECONDS,
) : Placeholder<FlowContent>() {
    override fun toString(): String =
        buildString {
            append(swapType.value)
            if (swapTime != DEFAULT_SWAP_TIME || swapTimeUnit != DurationUnit.MILLISECONDS) {
                val swapUnitName =
                    when (swapTimeUnit) {
                        DurationUnit.MILLISECONDS -> "ms"
                        DurationUnit.SECONDS -> "s"
                        else -> error("Use of DurationUnit other than ms or s")
                    }
                append("$swapType$swapUnitName")
            }
            if (settleTime != DEFAULT_SETTLE_TIME || settleTimeUnit != DurationUnit.MILLISECONDS) {
                val settleUnitName =
                    when (settleTimeUnit) {
                        DurationUnit.MILLISECONDS -> "ms"
                        DurationUnit.SECONDS -> "s"
                        else -> error("Use of DurationUnit other than ms or s")
                    }
                append("$settleTime$settleUnitName")
            }
        }

    init {
        this {
            attributes[HX_SWAP] = this@HxSwap.toString()
        }
    }
}

fun FlowContent.hxSwap(hxSwap: HxSwap?) {
    hxSwap?.apply(this)
}

fun FlowContent.hxSwap(
    swapType: SwapType,
    swapTime: Int = 0,
    swapTimeUnit: DurationUnit = DurationUnit.MILLISECONDS,
    settleTime: Int = 20,
    settleTimeUnit: DurationUnit = DurationUnit.MILLISECONDS,
) {
    hxSwap(HxSwap(swapType, swapTime, swapTimeUnit, settleTime, settleTimeUnit))
}

enum class SwapType(val value: String) {
    InnerHtml("innerHTML"),
    OuterHtml("outerHTML"),
    BeforeBegin("beforebegin"),
    AfterBegin("afterbegin"),
    BeforeEnd("beforeend"),
    AfterEnd("afterend"),
    Delete("delete"),
    None("none"),
}
