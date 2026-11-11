# CI pendiente de subir (`#PP6`)

El CI de la Fase 7 está escrito y auditado (`docs/plans/2026-11-11-f7-ci.md`, `docs/plans/2026-11-11-f7-git-flow.md`),
pero GitHub rechaza crear ficheros en `.github/workflows/` con un token sin el permiso `workflow`. Vive aquí hasta que
quien decide conceda el permiso:

```bash
gh auth refresh -h github.com -s workflow
```

Después, en una rama nueva:

```bash
git mv config/ci-pendiente/workflows .github/workflows
git mv config/ci-pendiente/actions .github/actions
```

y se borra este README en el mismo commit. El primer PR que los suba es la medición pendiente de `#PP6`: los jobs
`alcance`, `android`, `calidad` e `ios` en verde con los verdes ejecutados (`corrio.sh`), y la Xcode de `macos-latest`
(paso `xcodebuild -version`).
