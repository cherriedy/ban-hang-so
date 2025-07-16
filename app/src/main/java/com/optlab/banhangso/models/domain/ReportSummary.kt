package com.optlab.banhangso.models.domain

import java.util.Date

data class ReportSummary(
    val revenue: Double,
    val transactions: Int,
    val customers: Int,
    val date: Date,
)
