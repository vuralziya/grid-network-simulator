package com.fastned.gridnetworksimulator.application.command

import com.fastned.gridnetworksimulator.application.exception.NotFoundException
import com.fastned.gridnetworksimulator.domain.solargrid.SolarGrid
import com.fastned.gridnetworksimulator.domain.solargrid.SolarGridNetworkId
import com.fastned.gridnetworksimulator.domain.solargrid.SolarGridNetworkRepository
import com.trendyol.kediatr.AsyncCommandHandler
import com.trendyol.kediatr.Command
import org.springframework.stereotype.Service

data class AddSolarGrid(val userId: String, val solarGrid: SolarGrid) : Command

@Service
class AddSolarGridCommandHandler(private val solarGridNetworkRepository: SolarGridNetworkRepository) :
    AsyncCommandHandler<AddSolarGrid> {

    override suspend fun handleAsync(command: AddSolarGrid) {
        with(command) {
            val solarGridNetworkId = SolarGridNetworkId.create(userId)
            val solarGridNetwork = solarGridNetworkRepository.load(solarGridNetworkId.value)
                ?: throw NotFoundException("solar.grid.network.not.found.for.user")
            solarGridNetwork.addSolarGridToNetwork(solarGrid)
            solarGridNetworkRepository.save(solarGridNetwork)
        }
    }
}