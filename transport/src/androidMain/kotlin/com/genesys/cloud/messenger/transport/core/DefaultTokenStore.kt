package com.genesys.cloud.messenger.transport.core

import android.content.Context
import android.content.SharedPreferences
import com.genesys.cloud.messenger.transport.util.TOKEN_KEY
import com.genesys.cloud.messenger.transport.util.TokenStore
import java.util.UUID

actual class DefaultTokenStore actual constructor(storeKey: String) : TokenStore {
    private val sharedPreferences: SharedPreferences
    init {
        if (context == null) {
            throw IllegalStateException("Must set DefaultTokenStore.context before instantiating")
        }
        sharedPreferences = context!!.getSharedPreferences(storeKey, Context.MODE_PRIVATE)
    }

    override val token: String
        get() = sharedPreferences.getString(TOKEN_KEY, null) ?: UUID.randomUUID().toString().also {
            store(it)
        }

    private fun store(value: String) {
        with(sharedPreferences.edit()) {
            putString(TOKEN_KEY, value)
            apply()
        }
    }

    companion object {
        var context: Context? = null
            set(value) {
                field = value?.applicationContext
            }
    }
}
