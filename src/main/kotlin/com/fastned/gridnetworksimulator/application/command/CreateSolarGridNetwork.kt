package com.fastned.gridnetworksimulator.application.command

import com.fastned.gridnetworksimulator.domain.solargrid.SolarGrid
import com.fastned.gridnetworksimulator.domain.solargrid.SolarGridNetwork
import com.fastned.gridnetworksimulator.domain.solargrid.SolarGridNetworkId
import com.fastned.gridnetworksimulator.domain.solargrid.SolarGridNetworkRepository
import com.fastned.gridnetworksimulator.infrastructure.Constants.Companion.DAYS
import com.fastned.gridnetworksimulator.infrastructure.Constants.Companion.NETWORK_TEXT
import com.trendyol.kediatr.AsyncCommandWithResultHandler
import com.trendyol.kediatr.CommandWithResult
import org.springframework.stereotype.Service

data class CreateSolarGridNetwork(val userId: String, val solarGrids: List<SolarGrid>, val elapsedTimeInDays: Long) :
    CommandWithResult<GridNetworkOutput>

data class GridNetworkOutput(
    val produced: String,
    val network: List<String>
)

@Service
class CreateSolarGridNetworkCommandHandler(
    private val solarGridNetworkRepository: SolarGridNetworkRepository
) : AsyncCommandWithResultHandler<CreateSolarGridNetwork, GridNetworkOutput> {

    override suspend fun handleAsync(command: CreateSolarGridNetwork): GridNetworkOutput {
        with(command) {
            val solarGridNetworkId = SolarGridNetworkId.create(userId)
            val existing = solarGridNetworkRepository.load(solarGridNetworkId.value)
            val solarGridNetwork = if (existing != null) {
                existing
            } else {
                val newSolarGridNetwork = SolarGridNetwork.create(solarGridNetworkId, userId, solarGrids)
                solarGridNetworkRepository.save(newSolarGridNetwork)
                newSolarGridNetwork
            }
            val totalEnergy = solarGridNetwork.calculateTotalEnergy(elapsedTimeInDays)
            val produced = "$totalEnergy kWh"
            val network = solarGrids.map {
                "${it.name} $NETWORK_TEXT ${it.calculateAge()} $DAYS"
            }
            return GridNetworkOutput(produced, network)
        }
    }
}
