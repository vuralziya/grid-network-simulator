package com.fastned.gridnetworksimulator.application.command

import com.fastned.gridnetworksimulator.application.exception.NotFoundException
import com.fastned.gridnetworksimulator.domain.solargrid.SolarGrid
import com.fastned.gridnetworksimulator.domain.solargrid.SolarGridNetworkId
import com.fastned.gridnetworksimulator.domain.solargrid.SolarGridNetworkRepository
import com.trendyol.kediatr.AsyncCommandHandler
import com.trendyol.kediatr.Command
import org.springframework.stereotype.Service

data class RemoveSolarGrid(val userId: String, val solarGrid: SolarGrid) : Command

@Service
class RemoveSolarGridCommandHandler(private val solarGridNetworkRepository: SolarGridNetworkRepository) :
    AsyncCommandHandler<RemoveSolarGrid> {

    override suspend fun handleAsync(command: RemoveSolarGrid) {
        with(command) {
            val solarGridNetworkId = SolarGridNetworkId.create(userId)
            val solarGridNetwork = solarGridNetworkRepository.load(solarGridNetworkId.value) ?: return
            solarGridNetwork.removeSolarGridFromNetwork(solarGrid)
            solarGridNetworkRepository.save(solarGridNetwork)
        }
    }
}