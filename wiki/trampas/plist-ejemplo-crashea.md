# iOS crashea al arrancar con el `GoogleService-Info.plist.example`.

- **Síntoma:** iOS crashea al arrancar con el `GoogleService-Info.plist.example`.
- **Causa:** FirebaseInstallations valida el formato de la clave; la del ejemplo no lo tiene.
- **Regla:** CLAUDE.md §8 «El `.example` es forma, no se usa para probar».
