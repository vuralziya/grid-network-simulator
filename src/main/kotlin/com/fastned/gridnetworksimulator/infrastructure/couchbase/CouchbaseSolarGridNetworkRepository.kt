package com.fastned.gridnetworksimulator.infrastructure.couchbase

import com.couchbase.client.java.ReactiveCluster
import com.couchbase.client.java.ReactiveCollection
import com.couchbase.client.java.json.JsonObject
import com.couchbase.client.java.kv.GetResult
import com.fastned.gridnetworksimulator.domain.solargrid.SolarGrid
import com.fastned.gridnetworksimulator.domain.solargrid.SolarGridNetwork
import com.fastned.gridnetworksimulator.domain.solargrid.SolarGridNetworkId
import com.fastned.gridnetworksimulator.domain.solargrid.SolarGridNetworkRepository
import org.springframework.stereotype.Repository

@Repository
class CouchbaseSolarGridNetworkRepository(
    cluster: ReactiveCluster,
    collection: ReactiveCollection
) : BaseRepositoryImpl<SolarGridNetworkId, SolarGridNetwork>(cluster, collection, SolarGridNetwork::class.java),
    SolarGridNetworkRepository {

    override suspend fun map(getResult: GetResult): SolarGridNetwork {
        val jsonObject = getResult.contentAsObject()
        val userId = jsonObject.getString("userId")
        val jsonArray = jsonObject.getArray("solarGrids")
        val solarGrids = mutableListOf<SolarGrid>()
        for (i in 0 until jsonArray.size()) {
            val gridJsonObject: JsonObject = jsonArray.getObject(i)
            val name = gridJsonObject.getString("name")
            val birthDate = gridJsonObject.getLong("birthDate")
            solarGrids.add(SolarGrid(name, birthDate))
        }
        val solarGridNetwork = SolarGridNetwork.create(SolarGridNetworkId.create(userId), userId, solarGrids)
        solarGridNetwork.cas = getResult.cas()
        return solarGridNetwork
    }
}