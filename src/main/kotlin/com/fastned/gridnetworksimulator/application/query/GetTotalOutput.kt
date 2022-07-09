package com.fastned.gridnetworksimulator.application.query

import com.fastned.gridnetworksimulator.application.exception.NotFoundException
import com.fastned.gridnetworksimulator.domain.solargrid.SolarGridNetworkId
import com.fastned.gridnetworksimulator.domain.solargrid.SolarGridNetworkRepository
import com.trendyol.kediatr.AsyncQueryHandler
import com.trendyol.kediatr.Query
import org.springframework.stereotype.Service
import java.math.BigDecimal

data class GetTotalOutput(val userId: String, val elapsedTimeInDays: Long) : Query<GetTotalOutputResult>

@Service
class GetTotalOutputQueryHandler(val solarGridNetworkRepository: SolarGridNetworkRepository) :
    AsyncQueryHandler<GetTotalOutput, GetTotalOutputResult> {

    override suspend fun handleAsync(query: GetTotalOutput): GetTotalOutputResult {
        val id = SolarGridNetworkId.create(userId = query.userId).value
        val gridNetwork = solarGridNetworkRepository.load(id) ?: throw NotFoundException("solar.grid.not.found")
        val totalEnergy = gridNetwork.calculateTotalEnergy(query.elapsedTimeInDays)
        return GetTotalOutputResult(totalEnergy)
    }
}

data class GetTotalOutputResult(val totalOutputInKwh: BigDecimal)
