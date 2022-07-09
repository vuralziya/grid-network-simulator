package com.fastned.gridnetworksimulator.application.query

import com.fastned.gridnetworksimulator.application.exception.NotFoundException
import com.fastned.gridnetworksimulator.domain.solargrid.SolarGridNetworkId
import com.fastned.gridnetworksimulator.domain.solargrid.SolarGridNetworkRepository
import com.fastned.gridnetworksimulator.infrastructure.Constants.Companion.DAYS
import com.fastned.gridnetworksimulator.infrastructure.Constants.Companion.NETWORK_TEXT
import com.trendyol.kediatr.AsyncQueryHandler
import com.trendyol.kediatr.Query
import org.springframework.stereotype.Service

data class GetTotalNetwork(val userId: String, val elapsedTimeInDays: Long) : Query<GetTotalNetworkResult>

@Service
class GetTotalNetworkQueryHandler(private val solarGridNetworkRepository: SolarGridNetworkRepository) :
    AsyncQueryHandler<GetTotalNetwork, GetTotalNetworkResult> {

    override suspend fun handleAsync(query: GetTotalNetwork): GetTotalNetworkResult {
        val id = SolarGridNetworkId.create(userId = query.userId).value
        val gridNetwork = solarGridNetworkRepository.load(id) ?: throw NotFoundException("solar.grid.not.found")
        val solarGridOutputs = gridNetwork.solarGrids.map {
            "${it.name} $NETWORK_TEXT ${it.calculateAgeWithElapsedDays(query.elapsedTimeInDays)} $DAYS"
        }
        return GetTotalNetworkResult(solarGridOutputs)
    }
}

data class GetTotalNetworkResult(val network: List<String>)