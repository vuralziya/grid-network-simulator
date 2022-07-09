package com.fastned.gridnetworksimulator.domain

interface DomainRepository<TId, TDocument> {
    suspend fun load(id: String): TDocument?
    suspend fun save(document: TDocument)
    suspend fun delete(id: TId)
}
