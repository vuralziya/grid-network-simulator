package com.fastned.gridnetworksimulator.infrastructure.couchbase

import com.couchbase.client.core.error.DocumentNotFoundException
import com.couchbase.client.java.ReactiveCluster
import com.couchbase.client.java.ReactiveCollection
import com.couchbase.client.java.kv.GetResult
import com.couchbase.client.java.kv.ReplaceOptions
import com.couchbase.client.java.query.ReactiveQueryResult
import com.fastned.gridnetworksimulator.domain.AggregateRoot
import com.fastned.gridnetworksimulator.domain.DomainRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration


abstract class BaseRepositoryImpl<TId, TDocument : AggregateRoot<TDocument>>(
    private val cluster: ReactiveCluster,
    private val collection: ReactiveCollection,
    private val clazz: Class<TDocument>,
) : DomainRepository<TId, TDocument> {


    abstract suspend fun map(getResult: GetResult): TDocument
    private val logger: Logger = LoggerFactory.getLogger(BaseRepositoryImpl::class.java)

    override suspend fun load(id: String): TDocument? {
        try {
            val result: GetResult = collection
                .get(id)
                .retryWhen(Retry.backoff(3, Duration.ofMillis(10)).filter { it !is DocumentNotFoundException })
                .onErrorResume {
                    if (it !is DocumentNotFoundException) {
                        logger.error("couchbase replica read occur documentId {}", id, it)
                        return@onErrorResume collection.getAnyReplica(id)
                    }
                    return@onErrorResume Mono.error(it)
                }
                .awaitSingle()
            logger.info("class = $clazz")
            return map(result)
        } catch (ex: DocumentNotFoundException) {
            return null
        }
    }

    override suspend fun save(document: TDocument) {
        if (document.domainEvents().isEmpty()) {
            return
        }
        val isNew = document.cas == null
        val id = document.id

        if (isNew) {
            collection.insert(id, document)
                .onErrorResume {
                    logger.error("error occurred to insert documentId {} document {}", id, document, it)
                    return@onErrorResume Mono.error { it }
                }
                .awaitFirstOrNull()
        } else {
            collection.replace(id, document, ReplaceOptions.replaceOptions().cas(document.cas!!))
                .awaitFirstOrNull()
        }
        //TODO kafkaDomainEventPublisher.publishForAggregate(document.domainEvents(), id)
    }

    override suspend fun delete(id: TId) {
        try {
            collection.remove(id.toString())
        } catch (_: DocumentNotFoundException) {
        }
    }

    protected suspend fun executeQuery(query: String): ReactiveQueryResult {
        return cluster.query(query).awaitSingle()
    }

}
