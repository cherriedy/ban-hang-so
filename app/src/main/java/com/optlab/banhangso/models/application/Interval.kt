package com.optlab.banhangso.models.application

import com.optlab.banhangso.R
import com.optlab.banhangso.internal.utilities.DateTimeUtils
import com.optlab.banhangso.models.application.Interval.Date.LAST_MONTH
import com.optlab.banhangso.models.application.Interval.Date.THIS_MONTH
import com.optlab.banhangso.models.application.Interval.Date.THIS_YEAR
import com.optlab.banhangso.models.application.Interval.Date.TODAY
import com.optlab.banhangso.models.application.Interval.Date.YESTERDAY

@ConsistentCopyVisibility
data class Interval private constructor(private val _name: Int, private val _value: String) :
    BaseFilter<String>(_name, _value) {
        enum class Date {
            TODAY,
            YESTERDAY,
            THIS_MONTH,
            LAST_MONTH,
            THIS_YEAR,
        }

        companion object {
            private val _intervals by lazy {
                linkedMapOf(
                    TODAY to Interval(R.string.today, DateTimeUtils.getToday()),
                    YESTERDAY to Interval(R.string.yesterday, DateTimeUtils.getYesterday()),
                    THIS_MONTH to Interval(R.string.this_month, DateTimeUtils.getThisMonth()),
                    LAST_MONTH to Interval(R.string.last_month, DateTimeUtils.getLastMonth()),
                    THIS_YEAR to Interval(R.string.this_year, DateTimeUtils.getThisYear()),
                )
            }

            @JvmStatic
            val intervals: List<Interval>
                get() = _intervals.values.toList()

            @JvmStatic
            fun getDate(interval: Interval): Date = _intervals.entries.first { it.value == interval }.key

            @JvmStatic
            fun getInterval(date: Date): Interval = _intervals.entries.first { it.key == date }.value
        }
    }
