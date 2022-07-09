package com.fastned.gridnetworksimulator.domain.solargrid

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

data class SolarGrid(
    val name: String,
    val birthDate: Long
) {
    companion object {
        fun create(name: String, age: Long): SolarGrid {
            val birthDate = LocalDateTime.now().minusDays(age).toEpochSecond(ZoneOffset.UTC)
            return SolarGrid(name = name, birthDate = birthDate)
        }
    }

    fun calculateAge() =
        LocalDate.now().toEpochDay() - LocalDateTime.ofEpochSecond(this.birthDate, 0, ZoneOffset.UTC).toLocalDate()
            .toEpochDay()

    fun calculateAgeWithElapsedDays(elapsedTimeInDays: Long): Long {
        val age = calculateAge()
        return age.plus(elapsedTimeInDays)
    }
}