package com.genesys.cloud.messenger.transport.core

import com.genesys.cloud.messenger.transport.util.TokenStore

expect class DefaultTokenStore(storeKey: String) : TokenStore