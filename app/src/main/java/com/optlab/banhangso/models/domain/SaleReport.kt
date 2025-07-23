package com.optlab.banhangso.models.domain

import java.util.Date

data class SaleReport(
    val currency: String,
    val granularity: String,
    val dateRange: DateRange,
    val revenue: Double,
    val cost: Double,
    val profit: Double,
    val revenueByDate: RevenueByDate,
    val transactionsByDate: TransactionsByDate,
    val summary: Summary,
) {
    data class DateRange(val start: Date, val end: Date)

    data class RevenueByDate(val unit: String, val data: List<Data>) {
        data class Data(val date: Date, val value: Double)
    }

    data class TransactionsByDate(val data: List<Data>) {
        data class Data(val date: Date, val value: Int)
    }

    data class Summary(
        val averageRevenue: Double,
        val maxRevenue: Double,
        val totalTransactions: Int,
        val unit: String,
    )
}
