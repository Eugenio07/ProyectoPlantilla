package org.example.proyecto_plantilla

import io.ktor.client.engine.HttpClientEngine

/**
 * Variante release: sin stub. El grafo usa OkHttp.
 *
 * Excepción deliberada a `FunctionOnlyReturningConstant`: es la variante release de una función con dos variantes; la
 * de debug devuelve el stub.
 */
@Suppress("FunctionOnlyReturningConstant")
fun provideConfigEngine(): HttpClientEngine? = null
