package com.fastned.gridnetworksimulator.domain.solargrid

data class SolarGridNetworkId(val value: String) {

    companion object {
        fun create(userId: String): SolarGridNetworkId {
            val value = "${SolarGridNetwork.TYPE}:$userId"
            return SolarGridNetworkId(value)
        }
    }
}