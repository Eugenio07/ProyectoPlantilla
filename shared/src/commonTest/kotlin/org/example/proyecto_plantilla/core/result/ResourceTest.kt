package org.example.proyecto_plantilla.core.result

import org.example.proyecto_plantilla.core.error.AppError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class ResourceTest {
    private val error = AppError.Network

    @Test
    fun mapTransformsSuccessValue() {
        assertEquals(Resource.Success(4), Resource.Success(2).map { it * 2 })
    }

    @Test
    fun mapKeepsErrorUntouched() {
        val original: Resource<Int> = Resource.Error(error)
        assertSame(original, original.map { it * 2 })
    }

    @Test
    fun foldPicksSuccessBranch() {
        assertEquals("ok:1", Resource.Success(1).fold(onSuccess = { "ok:$it" }, onError = { "ko" }))
    }

    @Test
    fun foldPicksErrorBranch() {
        val r: Resource<Int> = Resource.Error(AppError.Http(503))
        assertEquals("ko:503", r.fold(onSuccess = { "ok" }, onError = { e -> "ko:" + (e as? AppError.Http)?.status }))
    }

    @Test
    fun onSuccessRunsOnlyForSuccess() {
        var seen: Int? = null
        Resource.Success(7).onSuccess { seen = it }
        assertEquals(7, seen)
        seen = null
        (Resource.Error(error) as Resource<Int>).onSuccess { seen = it }
        assertNull(seen)
    }

    @Test
    fun onErrorRunsOnlyForError() {
        var seen: AppError? = null
        Resource.Error(error).onError { seen = it }
        assertEquals(error, seen)
        seen = null
        Resource.Success(1).onError { seen = it }
        assertNull(seen)
    }
}
