package com.fastned.gridnetworksimulator.application.command

import com.fastned.gridnetworksimulator.application.exception.NotFoundException
import com.fastned.gridnetworksimulator.domain.solargrid.SolarGrid
import com.fastned.gridnetworksimulator.domain.solargrid.SolarGridNetworkId
import com.fastned.gridnetworksimulator.domain.solargrid.SolarGridNetworkRepository
import com.trendyol.kediatr.AsyncCommandHandler
import com.trendyol.kediatr.Command
import org.springframework.stereotype.Service

data class ResetSolarGridNetwork(val userId: String, val solarGrids: List<SolarGrid>) : Command

@Service
class ResetSolarGridNetworkCommandHandler(
    private val solarGridNetworkRepository: SolarGridNetworkRepository
) : AsyncCommandHandler<ResetSolarGridNetwork> {

    override suspend fun handleAsync(command: ResetSolarGridNetwork) {
        with(command) {
            val solarGridNetworkId = SolarGridNetworkId.create(userId)
            val existing = solarGridNetworkRepository.load(solarGridNetworkId.value)
                ?: throw NotFoundException("solar.grid.network.not.found.for.user")
            existing.updateSolarGrids(solarGrids = solarGrids.toMutableList())
            solarGridNetworkRepository.save(existing)
        }
    }
}
