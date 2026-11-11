package org.example.proyecto_plantilla.core.result

import org.example.proyecto_plantilla.core.error.AppError

/** Resultado de una operación que puede fallar. Los repositorios lo devuelven y nunca lanzan. Kotlin puro. */
sealed interface Resource<out T> {
    data class Success<out T>(
        val data: T,
    ) : Resource<T>

    data class Error(
        val error: AppError,
    ) : Resource<Nothing>
}

inline fun <T, R> Resource<T>.map(transform: (T) -> R): Resource<R> =
    when (this) {
        is Resource.Success -> Resource.Success(transform(data))
        is Resource.Error -> this
    }

inline fun <T, R> Resource<T>.fold(
    onSuccess: (T) -> R,
    onError: (AppError) -> R,
): R =
    when (this) {
        is Resource.Success -> onSuccess(data)
        is Resource.Error -> onError(error)
    }

inline fun <T> Resource<T>.onSuccess(action: (T) -> Unit): Resource<T> {
    if (this is Resource.Success) action(data)
    return this
}

inline fun <T> Resource<T>.onError(action: (AppError) -> Unit): Resource<T> {
    if (this is Resource.Error) action(error)
    return this
}
