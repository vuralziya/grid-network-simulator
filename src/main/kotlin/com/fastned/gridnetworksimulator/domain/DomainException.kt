package com.fastned.gridnetworksimulator.domain

open class DomainException : RuntimeException {
    val messageArgs: Array<out Any>

    constructor(messageCode: String) : super(messageCode) {
        this.messageArgs = arrayOf()
    }

    constructor(messageCode: String, vararg messageArgs: Any) : super(messageCode) {
        this.messageArgs = messageArgs
    }

    constructor(cause: Throwable?, messageCode: String, vararg messageArgs: Any) : super(messageCode, cause) {
        this.messageArgs = messageArgs
    }
}
