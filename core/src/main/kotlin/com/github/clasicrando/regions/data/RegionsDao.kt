package com.github.clasicrando.regions.data

import com.github.clasicrando.regions.model.Country
import com.github.clasicrando.regions.model.Province

interface RegionsDao {
    suspend fun getCountryCodes(): List<Country>

    suspend fun getProvinceCodes(countryCode: String): List<Province>
}
