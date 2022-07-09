package com.fastned.gridnetworksimulator.domain

import com.fastned.gridnetworksimulator.domain.events.EventType

abstract class DomainEvent {
    abstract val type: EventType
    var version: Long = 0
}
