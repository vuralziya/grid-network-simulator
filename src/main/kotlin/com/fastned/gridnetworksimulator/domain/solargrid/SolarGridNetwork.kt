package com.fastned.gridnetworksimulator.domain.solargrid

import com.fastned.gridnetworksimulator.domain.AggregateRoot
import com.fastned.gridnetworksimulator.domain.events.SolarGridAdded
import com.fastned.gridnetworksimulator.domain.events.SolarGridNetworkCreated
import com.fastned.gridnetworksimulator.domain.events.SolarGridNetworkUpdated
import com.fastned.gridnetworksimulator.domain.events.SolarGridRemoved
import com.fastned.gridnetworksimulator.infrastructure.Constants.Companion.DAY_COUNT_IN_A_YEAR
import com.fastned.gridnetworksimulator.infrastructure.Constants.Companion.DURATION_UNTIL_GRID_IS_READY
import com.fastned.gridnetworksimulator.infrastructure.Constants.Companion.FULL_SUN_HOURS_PER_YEAR
import com.fastned.gridnetworksimulator.infrastructure.Constants.Companion.MAX_YEAR_OF_GRID
import java.math.BigDecimal
import java.math.RoundingMode

class SolarGridNetwork private constructor(
    id: String,
    val userId: String,
    version: Long = 0
) : AggregateRoot<SolarGridNetwork>(id, version) {

    var solarGrids = mutableListOf<SolarGrid>()
        private set

    override val type: String
        get() = TYPE

    companion object {
        const val TYPE = "SolarGridNetwork"
        fun create(
            solarGridNetworkId: SolarGridNetworkId,
            userId: String,
            solarGrids: List<SolarGrid>
        ): SolarGridNetwork {
            val solarGridNetworkIdValue = solarGridNetworkId.value
            val solarGridNetwork = SolarGridNetwork(id = solarGridNetworkIdValue, userId = userId)
            solarGridNetwork.solarGrids.addAll(solarGrids)
            solarGridNetwork.registerEvent(SolarGridNetworkCreated(userId, solarGridNetworkIdValue))
            return solarGridNetwork
        }
    }

    fun calculateTotalEnergy(elapsedDays: Long): BigDecimal {
        val sumOf = solarGrids.sumOf {
            calculateEnergyByDay(it.calculateAge(), elapsedDays)
        }
        return sumOf.setScale(2, RoundingMode.HALF_UP)
    }

    fun updateSolarGrids(solarGrids: MutableList<SolarGrid>) {
        this.solarGrids = solarGrids
        registerEvent(SolarGridNetworkUpdated(userId, this.id))
    }

    private fun calculateEnergyByDay(ageAsDays: Long, elapsedDays: Long): BigDecimal {
        if (ageAsDays + elapsedDays <= DURATION_UNTIL_GRID_IS_READY) {
            return BigDecimal.ZERO;
        }
        if (getAgeAsYears(ageAsDays) >= MAX_YEAR_OF_GRID) {
            return BigDecimal.ZERO;
        }
        if (getAgeAsYears(ageAsDays + elapsedDays) >= MAX_YEAR_OF_GRID) {
            return calculateEnergy(ageAsDays, DAY_COUNT_IN_A_YEAR * MAX_YEAR_OF_GRID)
        }
        val start = ageAsDays - DURATION_UNTIL_GRID_IS_READY
        val end = start + elapsedDays
        return calculateEnergy(start, end)
    }

    private fun getAgeAsYears(ageAsDays: Long) = ageAsDays / DAY_COUNT_IN_A_YEAR

    private fun calculateEnergy(ageAtStartDate: Long, ageAtEndDate: Long): BigDecimal {
        var sum = BigDecimal.ZERO
        val sunHoursPerDay = BigDecimal.valueOf(FULL_SUN_HOURS_PER_YEAR) / BigDecimal.valueOf(DAY_COUNT_IN_A_YEAR)
        for (index in ageAtStartDate until ageAtEndDate) {
            sum += BigDecimal.valueOf(20) *
                    (BigDecimal.ONE - (BigDecimal.valueOf(index)
                        .divide(BigDecimal.valueOf(365), 9, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(0.005)))) * sunHoursPerDay
        }
        return sum
    }

    fun addSolarGridToNetwork(solarGrid: SolarGrid) {
        if (solarGrids.contains(solarGrid)) {
            return
        }
        solarGrids.add(solarGrid)
        registerEvent(SolarGridAdded(solarGrid.name))
    }

    fun removeSolarGridFromNetwork(solarGrid: SolarGrid) {
        if (!solarGrids.contains(solarGrid)) {
            return
        }
        solarGrids.remove(solarGrid)
        registerEvent(SolarGridRemoved(solarGrid.name))
    }
}