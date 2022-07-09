package com.fastned.gridnetworksimulator.domain

import com.fastned.gridnetworksimulator.domain.solargrid.SolarGrid
import com.fastned.gridnetworksimulator.domain.solargrid.SolarGridNetwork
import com.fastned.gridnetworksimulator.domain.solargrid.SolarGridNetworkId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.ZoneOffset

internal class SolarGridNetworkTest {

    @Test
    fun it_should_calculated_output_as_zero() {
        //given
        val userId = "user1"
        val solarGridNetworkId = SolarGridNetworkId.create(userId)

        val birthDate1 = LocalDateTime.now().minusDays(19).toEpochSecond(ZoneOffset.UTC)
        val solarGrid1 = SolarGrid("Amsterdam", birthDate1)
        val birthDate2 = LocalDateTime.now().minusDays(29).toEpochSecond(ZoneOffset.UTC)
        val solarGrid2 = SolarGrid("Groningen", birthDate2)
        val birthDate3 = LocalDateTime.now().minusDays(39).toEpochSecond(ZoneOffset.UTC)
        val solarGrid3 = SolarGrid("Maastricht", birthDate3)
        val solarGridNetwork =
            SolarGridNetwork.create(solarGridNetworkId, userId, listOf(solarGrid1, solarGrid2, solarGrid3))

        //when
        val calculateTotalEnergy = solarGridNetwork.calculateTotalEnergy(20)

        //then
        assertThat(calculateTotalEnergy).isEqualTo(BigDecimal.valueOf(0.00).setScale(2, RoundingMode.HALF_UP))
    }

    @Test
    fun it_should_calculated_output_as_max_energy_when_grid_age_is_gt_25() {
        //given
        val userId = "user1"
        val solarGridNetworkId = SolarGridNetworkId.create(userId)

        val birthDate1 = LocalDateTime.now().minusDays(0).toEpochSecond(ZoneOffset.UTC)
        val solarGrid1 = SolarGrid("Amsterdam", birthDate1)
        val solarGridNetwork =
            SolarGridNetwork.create(solarGridNetworkId, userId, listOf(solarGrid1))

        //when
        val calculateTotalEnergy = solarGridNetwork.calculateTotalEnergy(30 * 365)

        //then
        assertThat(calculateTotalEnergy).isEqualTo(BigDecimal.valueOf(513285).setScale(2, RoundingMode.HALF_UP))
    }

    @Test
    fun it_should_calculated_output_as_bakalim() {
        //given
        val userId = "user1"
        val solarGridNetworkId = SolarGridNetworkId.create(userId)

        val birthDate1 = LocalDateTime.now().minusDays(854).toEpochSecond(ZoneOffset.UTC)
        val solarGrid1 = SolarGrid("Amsterdam", birthDate1)
        val birthDate2 = LocalDateTime.now().minusDays(473).toEpochSecond(ZoneOffset.UTC)
        val solarGrid2 = SolarGrid("Groningen", birthDate2)
        val birthDate3 = LocalDateTime.now().minusDays(253).toEpochSecond(ZoneOffset.UTC)
        val solarGrid3 = SolarGrid("Maastricht", birthDate3)
        val solarGridNetwork =
            SolarGridNetwork.create(solarGridNetworkId, userId, listOf(solarGrid1, solarGrid2, solarGrid3))

        //when
        val calculateTotalEnergy = solarGridNetwork.calculateTotalEnergy(20)

        //then
        assertThat(calculateTotalEnergy).isEqualTo(BigDecimal.valueOf(3576.52))
    }
}