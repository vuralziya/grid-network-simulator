package com.fastned.gridnetworksimulator.domain.events

enum class EventType(val value: String) {
    SOLAR_GRID_NETWORK_CREATED("solar.grid.network.created"),
    SOLAR_GRID_NETWORK_UPDATED("solar.grid.network.updated"),
    SOLAR_GRID_ADDED("solar.grid.added"),
    SOLAR_GRID_REMOVED("solar.grid.removed");

    override fun toString(): String = this.value
}
