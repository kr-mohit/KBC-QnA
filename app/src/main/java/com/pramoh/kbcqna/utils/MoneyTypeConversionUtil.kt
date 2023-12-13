package com.pramoh.kbcqna.utils

class MoneyTypeConversionUtil {

    companion object {

        fun convertToString(money: Int): String {
            return when (money) {
                100000000 -> "Rs. 10 Crores"
                30000000 -> "Rs. 3 Crores"
                10000000 -> "Rs. 1 Crore"
                5000000 -> "Rs. 50,00,000"
                2500000 -> "Rs. 25,00,000"
                1250000 -> "Rs. 12,50,000"
                640000 -> "Rs. 6,40,000"
                320000 -> "Rs. 3,20,000"
                160000 -> "Rs. 1,60,000"
                80000 -> "Rs. 80,000"
                40000 -> "Rs. 40,000"
                20000 -> "Rs. 20,000"
                10000 -> "Rs. 10,000"
                5000 -> "Rs. 5,000"
                1000 -> "Rs. 1,000"
                else -> "Rs. 0"
            }
        }
    }
}