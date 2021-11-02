package com.theapache64.supergithub.utils

import kotlin.js.Date
import kotlin.math.floor
import kotlin.math.round

object TimeUtils {

    private const val SECOND_MILLIS = 1000L
    private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private const val DAY_MILLIS = 24 * HOUR_MILLIS
    private const val MONTH_MILLIS = 30 * DAY_MILLIS
    private const val YEAR_MILLIS = 12 * MONTH_MILLIS


    fun getRelativeTime(presentDate: Date, pastDate: Date): String? {

        val past = pastDate.getTime()
        val present = presentDate.getTime()
        val diff = present - past

        if (diff < 0) {
            return null
        }

        return when {

            diff < MINUTE_MILLIS -> {
                "just now";
            }

            diff < 2 * MINUTE_MILLIS -> {
                "a minute ago";
            }

            diff < 60 * MINUTE_MILLIS -> {
                "${floor(diff / MINUTE_MILLIS)} minutes ago";
            }

            diff < 120 * MINUTE_MILLIS -> {
                "an hour ago";
            }

            diff < 24 * HOUR_MILLIS -> {
                "${floor(diff / HOUR_MILLIS)} hours ago";
            }

            diff < 48 * HOUR_MILLIS -> {
                "yesterday";
            }

            diff < 30 * DAY_MILLIS -> {
                "${floor(diff / DAY_MILLIS)} days ago"
            }

            diff < 2 * MONTH_MILLIS -> {
                "a month ago"
            }


            diff < 12 * MONTH_MILLIS -> {
                "${floor(diff / MONTH_MILLIS)} months ago"
            }

            diff < 2 * YEAR_MILLIS -> {
                "a year ago"
            }

            else -> {
                "${round(diff / YEAR_MILLIS)} years ago";
            }
        }
    }

}