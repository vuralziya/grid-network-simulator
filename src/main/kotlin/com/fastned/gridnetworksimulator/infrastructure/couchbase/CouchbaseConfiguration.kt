package com.fastned.gridnetworksimulator.infrastructure.couchbase

import com.couchbase.client.core.env.PasswordAuthenticator
import com.couchbase.client.core.env.TimeoutConfig
import com.couchbase.client.java.ClusterOptions
import com.couchbase.client.java.ReactiveBucket
import com.couchbase.client.java.ReactiveCluster
import com.couchbase.client.java.ReactiveCollection
import com.couchbase.client.java.codec.JacksonJsonSerializer
import com.couchbase.client.java.env.ClusterEnvironment
import com.fastned.gridnetworksimulator.infrastructure.binding.ObjectMapperConfig
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.time.Duration


@Configuration
@EnableConfigurationProperties(CouchbaseProperties::class)
class CouchbaseConfiguration(
    private val couchbaseProperties: CouchbaseProperties
) {

    @Primary
    @Bean(destroyMethod = "shutdown")
    fun clusterEnvironment(): ClusterEnvironment {
        val cbSerializer = JacksonJsonSerializer.create(ObjectMapperConfig.createObjectMapperForCouchbase())
        return ClusterEnvironment
            .builder()
            .timeoutConfig(
                TimeoutConfig.builder()
                    .kvTimeout(Duration.ofMillis(couchbaseProperties.kvTimeout))
                    .connectTimeout(Duration.ofMillis(couchbaseProperties.connectTimeout))
                    .queryTimeout(Duration.ofMillis(couchbaseProperties.queryTimeout))
                    .viewTimeout(Duration.ofMillis(couchbaseProperties.viewTimeout))
            )
            .jsonSerializer(cbSerializer)
            .build()
    }

    @Primary
    @Bean(destroyMethod = "disconnect")
    fun cluster(clusterEnvironment: ClusterEnvironment): ReactiveCluster {
        val authenticator = PasswordAuthenticator.builder().username(couchbaseProperties.username)
            .password(couchbaseProperties.password) // enables only the PLAIN authentication mechanism, used with LDAP
            .enablePlainSaslMechanism()
            .build()
        return ReactiveCluster.connect(
            couchbaseProperties.hosts.joinToString(","),
            ClusterOptions.clusterOptions(authenticator)
        )
    }

    @Primary
    @Bean
    fun bucket(cluster: ReactiveCluster): ReactiveBucket {
        return cluster.bucket(couchbaseProperties.bucketName)
    }

    @Primary
    @Bean
    fun collection(bucket: ReactiveBucket): ReactiveCollection {
        return bucket.defaultCollection()
    }

}
