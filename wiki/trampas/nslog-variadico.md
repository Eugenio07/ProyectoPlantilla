# En iOS la app muere al registrar un aviso (`EXC_BAD_ACCESS` dentro de `NSLog`).

- **Síntoma:** En iOS la app muere al registrar un aviso (`EXC_BAD_ACCESS` dentro de `NSLog`).
- **Causa:** Un `String` de Kotlin pasado como argumento variádico `%@` de Objective-C.
- **Regla:** CLAUDE.md §6 «En iOS no se registra con `NSLog` y argumentos variádicos».
