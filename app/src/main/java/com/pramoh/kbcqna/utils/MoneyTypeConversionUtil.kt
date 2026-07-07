package com.pramoh.kbcqna.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class MoneyTypeConversionUtil {

    companion object {
        var prefix: String = ""
        var suffix: String = ""

        fun convertToString(
            money: Int,
            prefix: String = Companion.prefix,
            suffix: String = Companion.suffix
        ): String {
            val isNegative = money < 0
            val absMoney = kotlin.math.abs(money.toLong())

            if (absMoney == 0L) {
                return "${prefix}0${suffix}"
            }

            val formatted = if (absMoney >= 10_000_000L) {
                val crores = absMoney.toDouble() / 10_000_000.0
                val symbols = DecimalFormatSymbols(Locale.US)
                val df = DecimalFormat("#.##", symbols)
                val croresStr = df.format(crores)
                val croreSuffix = if (croresStr == "1") " Crore" else " Crores"
                "$croresStr$croreSuffix"
            } else {
                val numStr = absMoney.toString()
                if (numStr.length <= 3) {
                    numStr
                } else {
                    val lastThree = numStr.substring(numStr.length - 3)
                    val remaining = numStr.substring(0, numStr.length - 3)
                    val groupedRemaining = remaining.reversed()
                        .chunked(2)
                        .joinToString(",")
                        .reversed()
                    "$groupedRemaining,$lastThree"
                }
            }

            val sign = if (isNegative) "-" else ""
            return "$sign$prefix$formatted$suffix"
        }
    }
}