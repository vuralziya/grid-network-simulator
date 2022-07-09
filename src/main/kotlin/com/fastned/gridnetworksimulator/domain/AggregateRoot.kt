package com.fastned.gridnetworksimulator.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

abstract class AggregateRoot<T : AggregateRoot<T>> protected constructor(
    val id: String,
    version: Long = 0
) {
    @JsonIgnore
    var cas: Long? = null
    abstract val type: String
    var version: Long = version
        private set

    @JsonIgnore
    private var _domainEvents: MutableList<DomainEvent> = mutableListOf()
    private val events: MutableList<DomainEvent>
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) get() = this._domainEvents

    protected fun <T : DomainEvent> registerEvent(event: T): T {
        version++
        event.version = version
        this.events.add(event)
        return event
    }

    fun clearDomainEvents() = this.events.clear()

    fun domainEvents(): List<DomainEvent> = this.events

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AggregateRoot<*>

        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
