package com.github.clasicrando.web.session

import io.github.crackthecodeabhi.kreds.connection.KredsClient
import io.ktor.server.sessions.SessionStorage
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class RedisSessionStorage(override val di: DI) : DIAware, SessionStorage {
    private val client: KredsClient by di.instance()

    override suspend fun invalidate(id: String) {
        client.del(id)
    }

    override suspend fun read(id: String): String {
        return client.get(id) ?: throw NoSuchElementException("Session $id not found")
    }

    override suspend fun write(
        id: String,
        value: String,
    ) {
        client.set(id, value)
    }
}
