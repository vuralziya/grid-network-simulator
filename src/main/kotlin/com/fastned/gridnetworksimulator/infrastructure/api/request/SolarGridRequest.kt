package com.fastned.gridnetworksimulator.infrastructure.api.request

import com.fastned.gridnetworksimulator.domain.solargrid.SolarGrid

data class SolarGridRequest(val name: String, val age: Int) {

    fun toSolarGrid(): SolarGrid {
        return SolarGrid.create(name, age.toLong())
    }
}