package com.fastned.gridnetworksimulator.domain.events

import com.fastned.gridnetworksimulator.domain.DomainEvent

data class SolarGridNetworkCreated(
    val userId: String,
    val solarGridNetworkId: String
) : DomainEvent() {
    override val type: EventType
        get() = EventType.SOLAR_GRID_NETWORK_CREATED
}

data class SolarGridNetworkUpdated(
    val userId: String,
    val solarGridNetworkId: String
) : DomainEvent() {
    override val type: EventType
        get() = EventType.SOLAR_GRID_NETWORK_UPDATED
}

data class SolarGridAdded(
    val name: String
) : DomainEvent() {
    override val type: EventType
        get() = EventType.SOLAR_GRID_ADDED
}

data class SolarGridRemoved(
    val name: String
) : DomainEvent() {
    override val type: EventType
        get() = EventType.SOLAR_GRID_REMOVED
}
