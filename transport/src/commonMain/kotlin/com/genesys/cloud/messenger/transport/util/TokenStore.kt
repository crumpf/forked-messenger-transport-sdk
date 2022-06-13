package com.genesys.cloud.messenger.transport.util

internal const val TOKEN_KEY = "token"

interface TokenStore {
    val token: String
}
