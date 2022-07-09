package com.fastned.gridnetworksimulator.infrastructure.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fastned.gridnetworksimulator.application.command.*
import com.fastned.gridnetworksimulator.application.query.GetTotalNetwork
import com.fastned.gridnetworksimulator.application.query.GetTotalNetworkResult
import com.fastned.gridnetworksimulator.application.query.GetTotalOutput
import com.fastned.gridnetworksimulator.application.query.GetTotalOutputResult
import com.fastned.gridnetworksimulator.infrastructure.Constants
import com.fastned.gridnetworksimulator.infrastructure.api.request.SolarGridRequest
import com.fastned.gridnetworksimulator.infrastructure.extensions.toBytes
import com.trendyol.kediatr.CommandBus
import org.springframework.http.HttpStatus
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/networks")
class SolarGridNetworkController(private val commandBus: CommandBus, private val objectMapper: ObjectMapper) {

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun addNetwork(
        @RequestPart(value = "file") file: FilePart,
        @RequestParam userId: String,
        @RequestParam elapsedTimeInDays: Long
    ): GridNetworkOutput {
        val inputStreamFlux = file.toBytes()
        val solarGridNetwork = objectMapper.readValue<List<SolarGridRequest>>(
            inputStreamFlux,
            objectMapper.typeFactory.constructCollectionType(List::class.java, SolarGridRequest::class.java)
        )
        val grids = solarGridNetwork.map { it.toSolarGrid() }
        return commandBus.executeCommandAsync(CreateSolarGridNetwork(userId, grids, elapsedTimeInDays))
    }

    @PostMapping("/load")
    @ResponseStatus(HttpStatus.RESET_CONTENT)
    suspend fun resetNetwork(
        @RequestParam userId: String,
        @RequestBody solarGrids: List<SolarGridRequest>
    ) {
        val grids = solarGrids.map { it.toSolarGrid() }
        return commandBus.executeCommandAsync(ResetSolarGridNetwork(userId, grids))
    }

    @PostMapping("/add-solar-grid")
    @ResponseStatus(HttpStatus.OK)
    suspend fun addSolarGrid(
        @RequestParam userId: String,
        @RequestBody solarGridRequest: SolarGridRequest
    ) {
        return commandBus.executeCommandAsync(AddSolarGrid(userId, solarGridRequest.toSolarGrid()))
    }

    @DeleteMapping("/remove-solar-grid")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun removeSolarGrid(
        @RequestParam userId: String,
        @RequestBody solarGridName: String
    ) {
        return commandBus.executeCommandAsync(RemoveSolarGrid(userId, solarGridName))
    }

    @GetMapping("/output")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getOutput(
        @RequestParam userId: String,
        @RequestParam elapsedTimeInDays: Long
    ): GetTotalOutputResult {
        return commandBus.executeQueryAsync(GetTotalOutput(userId, elapsedTimeInDays))
    }

    @GetMapping("/output/years")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getOutputByYears(
        @RequestParam userId: String,
        @RequestParam elapsedTimeInYears: Long
    ): GetTotalOutputResult {
        return commandBus.executeQueryAsync(GetTotalOutput(userId, elapsedTimeInYears * Constants.DAY_COUNT_IN_A_YEAR))
    }

    @GetMapping("/network")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getNetwork(
        @RequestParam userId: String,
        @RequestParam elapsedTimeInDays: Long
    ): GetTotalNetworkResult {
        return commandBus.executeQueryAsync(GetTotalNetwork(userId, elapsedTimeInDays))
    }
}