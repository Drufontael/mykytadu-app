# ADR-001 — Compartilhar a aplicação em Kotlin Multiplatform

> **Estado:** Aprovado
> **Registro:** retrospectivo
> **Sprints relacionadas:** S1, S3 e W1

## Contexto

O MykytaDu atende Android, Desktop, iOS, JavaScript e WasmJS. UI, domínio,
repositories e navegação não possuem, em sua maior parte, dependência legítima
de uma plataforma específica.

## Decisão

Manter em `commonMain` tudo que puder ser compartilhado com segurança. Código de
plataforma fica no source set mais estreito; `webMain` reúne o que é comum a JS e
WasmJS. `expect`/`actual` é reservado a fronteiras reais.

Não criar frontends paralelos por plataforma sem uma decisão arquitetural que
demonstre a necessidade.

## Consequências

- comportamento e Design System evoluem em uma base comum;
- cada nova dependência precisa resolver para todos os targets consumidores;
- limitações específicas devem ser isoladas sem contaminar o domínio;
- regressões exigem validação proporcional nos targets afetados.

## Evidências

O mesmo `App`, Navigation 3, domínio e repositories foram compilados e validados
nos targets atuais. Os shells JS e WasmJS renderizam a aplicação compartilhada.

## Critérios de revisão

Reavaliar somente diante de limitação comprovada de plataforma, custo de build
ou requisito de produto que torne o compartilhamento inviável.
