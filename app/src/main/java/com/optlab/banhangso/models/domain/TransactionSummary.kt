package com.optlab.banhangso.models.domain

import java.util.Date

data class TransactionSummary(
    val id: String,
    val customerName: String,
    val staffName: String,
    val price: Double,
    val createdAt: Date,
)
