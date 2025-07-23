package com.optlab.banhangso.models.application

import com.optlab.banhangso.R
import com.optlab.banhangso.models.application.Payment.Method.CASH
import com.optlab.banhangso.models.application.Payment.Method.MOBILE_BANKING

@ConsistentCopyVisibility
data class Payment
    private constructor(private val _name: Int, private val _value: String, val image: Int) :
    BaseFilter<String>(_name, _value) {
        enum class Method {
            CASH,
            MOBILE_BANKING,
            UNKNOWN,
        }

        companion object {
            private val UNKNOWN = Payment(-1, "UNKNOWN", -1)

            private val _methods by lazy {
                linkedMapOf(
                    CASH to Payment(R.string.cash, "CASH", R.drawable.ic_cash),
                    MOBILE_BANKING to
                        Payment(R.string.mobile_banking, "MOBILE_BANKING", R.drawable.ic_mobile_banking),
                )
            }

            @JvmStatic
            val methods: List<Payment>
                get() = _methods.values.toList()

            @JvmStatic fun getMethod(method: Method): Payment = _methods[method]!!

            @JvmStatic
            fun getType(payment: Payment): Method = _methods.entries.firstOrNull { it.value == payment }?.key ?: Method.UNKNOWN

            @JvmStatic
            fun getMethod(type: String?): Payment = type?.let { getMethod(Method.valueOf(it)) } ?: UNKNOWN
        }
    }
