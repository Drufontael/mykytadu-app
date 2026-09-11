# ADR-004 — Integrar Navigation 3 ao histórico com fragmentos

> **Estado:** Aprovado
> **Registro:** retrospectivo
> **Sprint relacionada:** W1

## Contexto

Navigation 3 funciona no código compartilhado, mas não sincroniza por si só a
pilha com URL, reload e Back/Forward do navegador. A distribuição atual não
possui fallback de paths para uma SPA.

## Decisão

Preservar `AppRoute`, `NavDisplay` e o back stack em `commonMain`. Uma fronteira
Web pequena converte rotas em fragmentos e sincroniza Navigation 3 com History
API, `popstate` e `hashchange`.

Fragment routing é usado para permitir reload e deep links em hospedagem HTTP
estática sem rewrite de paths.

## Consequências

- APIs do navegador não atravessam para `commonMain`;
- navegação interna e histórico são sincronizados sem criar um segundo router;
- codecs precisam acompanhar alterações nos contratos de rota;
- URLs por path, SEO e SSR permanecem fora da solução atual.

## Evidências

Canonicalização, Back, Forward, reload e deep links foram validados em JS e
WasmJS. Eventos duplicados são deduplicados e restaurações não criam novo push.

## Critérios de revisão

Reavaliar se a hospedagem oferecer fallback de SPA e houver necessidade concreta
de URLs por path, SEO ou renderização no servidor.
