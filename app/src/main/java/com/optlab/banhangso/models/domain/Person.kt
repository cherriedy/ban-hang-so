package com.optlab.banhangso.models.domain

import java.util.Date

open class Person(
    open var id: String? = null,
    open var name: String? = null,
    open var email: String? = null,
    open var imageUrl: String? = null,
    open var phone: String? = null,
    open var createdAt: Date? = null,
    open var updatedAt: Date? = null,
)
