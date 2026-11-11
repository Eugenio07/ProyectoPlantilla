# Un 200 con cuerpo malformado acaba en `AppError.Unknown`.

- **Síntoma:** Un 200 con cuerpo malformado acaba en `AppError.Unknown`.
- **Causa:** Ktor no lanza `SerializationException` al deserializar: lanza `JsonConvertException` (`ContentConvertException`).
- **Regla:** CLAUDE.md §5 «Mapea `ContentConvertException` y `SerializationException` a `Serialization`».
