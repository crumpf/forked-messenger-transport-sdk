package com.genesys.cloud.messenger.transport.core

import com.genesys.cloud.messenger.transport.shyrka.receive.DeploymentConfig
import com.genesys.cloud.messenger.transport.util.TokenStore

class MessengerTransport(configuration: Configuration, private val tokenStore: TokenStore) {

    constructor(configuration: Configuration, storeKey: String) : this(configuration, DefaultTokenStore(storeKey))

    private val messengerTransportImpl: MessengerTransportImpl = MessengerTransportImpl(configuration)

    /**
     * Creates an instance of [MessagingClient] based on the provided configuration.
     */
    fun createMessagingClient(): MessagingClient {
        return messengerTransportImpl.createMessagingClient(tokenStore.token)
    }

    /**
     *  Fetch deployment configuration based on deployment id and domain.
     *
     * @throws Exception
     */
    @Throws(Exception::class)
    suspend fun fetchDeploymentConfig(): DeploymentConfig {
        return messengerTransportImpl.fetchDeploymentConfig()
    }
}