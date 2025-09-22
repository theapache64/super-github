package com.theapache64.supergithub.utils

import kotlin.js.Date
import kotlin.test.assertEquals

class TimeUtilsTest {


    @kotlin.test.Test
    fun parseGood() {
        val today = Date("2020-01-20T16:00:00Z") // 04:00:00 PM, Jan 20, 2020
        TimeUtils.apply {

            val dateMap = mapOf(
                "2020-01-20T15:59:58Z" to "just now", // 03:59:58 PM, Jan 20, 2020
                "2020-01-20T15:59:00Z" to "a minute ago", // 03:59:00 PM, Jan 20, 2020
                "2020-01-20T15:30:00Z" to "30 minutes ago", // 03:30:00 PM, Jan 20, 2020
                "2020-01-20T15:01:00Z" to "59 minutes ago", // 03:01:00 PM, Jan 20, 2020
                "2020-01-20T15:00:00Z" to "an hour ago", // 03:00:00 PM, Jan 20, 2020
                "2020-01-20T14:00:00Z" to "2 hours ago", // 02:00:00 PM, Jan 20, 2020
                "2020-01-20T04:00:00Z" to "12 hours ago", // 04:00:00 AM, Jan 20, 2020
                "2020-01-20T01:00:00Z" to "15 hours ago", // 01:00:00 AM, Jan 20, 2020
                "2020-01-19T17:00:00Z" to "23 hours ago", // 05:00:00 PM, Jan 19, 2020
                "2020-01-19T16:01:00Z" to "23 hours ago", // 04:01:00 PM, Jan 19, 2020
                "2020-01-19T16:00:00Z" to "yesterday", // 04:00:00 PM, Jan 19, 2020
                "2020-01-18T00:00:00Z" to "2 days ago", // 00:00:00 PM, Jan 18, 2020
                "2020-01-01T00:00:00Z" to "19 days ago", // 00:00:00 PM, Jan 1, 2020
                "2019-12-20T00:00:00Z" to "a month ago", // 00:00:00 PM, Dec 20, 2019
                "2019-11-20T00:00:00Z" to "2 months ago", // 00:00:00 PM, Nov 20, 2019
                "2019-02-20T00:00:00Z" to "11 months ago", // 00:00:00 PM, Feb 20, 2019
                "2019-01-01T00:00:00Z" to "a year ago", // 00:00:00 PM, Jan 01, 2019
                "2018-01-01T00:00:00Z" to "2 years ago", // 00:00:00 PM, Jan 01, 2018
                "2010-01-01T00:00:00Z" to "10 years ago" // 00:00:00 PM, Jan 01, 2010
            )

            for ((input, output) in dateMap) {
                val inputDate = Date(input)
                assertEquals(output, getRelativeTime(today, inputDate))
            }

        }
    }
}