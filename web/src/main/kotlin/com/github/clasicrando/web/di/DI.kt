package com.github.clasicrando.web.di

import com.github.clasicrando.web.session.RedisSessionStorage
import io.github.crackthecodeabhi.kreds.connection.Endpoint
import io.github.crackthecodeabhi.kreds.connection.newClient
import io.ktor.server.sessions.SessionStorage
import org.kodein.di.DI
import org.kodein.di.bindEagerSingleton
import org.kodein.di.bindProvider

fun DI.MainBuilder.bindRedisSessionComponent() {
    bindEagerSingleton {
        val redisHost =
            System.getenv("EM_REDIS_URL")
                ?: error("Could not find EM_REDIS_URL environment variable")
        newClient(Endpoint.from(redisHost))
    }
    bindProvider<SessionStorage> {
        RedisSessionStorage(di)
    }
}
