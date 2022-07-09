package com.fastned.gridnetworksimulator.application.exception

class ApplicationException : RuntimeException {
    val messageArgs: Array<out Any>

    constructor(messageCode: String, vararg messageArgs: Any) : super(messageCode) {
        this.messageArgs = messageArgs
    }
}