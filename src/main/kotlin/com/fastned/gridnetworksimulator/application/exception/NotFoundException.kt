package com.fastned.gridnetworksimulator.application.exception

class NotFoundException : RuntimeException {
    val messageArgs: Array<out Any>

    constructor(messageCode: String) : super(messageCode) {
        this.messageArgs = arrayOf()
    }

    constructor(messageCode: String, vararg messageArgs: Any) : super(messageCode) {
        this.messageArgs = messageArgs
    }
}