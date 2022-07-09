package com.fastned.gridnetworksimulator.infrastructure.api

import com.fastned.gridnetworksimulator.application.query.GetTotalOutput
import com.fastned.gridnetworksimulator.application.query.GetTotalOutputResult
import com.trendyol.kediatr.CommandBus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import java.math.BigDecimal

@ExtendWith(SpringExtension::class)
@WebFluxTest(SolarGridNetworkController::class)
internal class SolarGridNetworkControllerTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockBean
    lateinit var commandBus: CommandBus

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun itShouldGetOutput() = runBlockingTest {
        val userId = "123test"
        val elapsedTimeInDays = 5L

        Mockito.`when`(
            commandBus.executeQueryAsync(
                GetTotalOutput(
                    userId, elapsedTimeInDays
                )
            )
        ).thenReturn(GetTotalOutputResult(BigDecimal.valueOf(1234)))

        webTestClient
            .get()
            .uri {
                it.path("/networks/output")
                it.queryParam("userId", userId)
                it.queryParam("elapsedTimeInDays", elapsedTimeInDays)
                it.build()
            }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk

        BDDMockito.verify(commandBus).executeQueryAsync(
            GetTotalOutput(
                userId, elapsedTimeInDays
            )
        )
    }
}