package com.theapache64.supergithub.utils

object StringUtils {
    private val nonDigits = "\\D+".toRegex()
    fun parseInt(input: String): Int? {
        return try {
            input.replace(nonDigits, "").trim().toInt()
        }catch (e: NumberFormatException){
            null
        }
    }
}