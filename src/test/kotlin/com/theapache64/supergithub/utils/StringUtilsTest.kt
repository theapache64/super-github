package com.theapache64.supergithub.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class StringUtilsTest {
    @Test
    fun parseInt() {
        assertEquals(100, StringUtils.parseInt("100%"))
        assertEquals(0, StringUtils.parseInt("0%"))
        assertEquals(null, StringUtils.parseInt(""))
    }
}