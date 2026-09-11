# ADR-005 — Consumir a AniList diretamente no Web

> **Estado:** Aprovado
> **Registro:** retrospectivo
> **Sprint relacionada:** W1

## Contexto

O navegador pode acessar a AniList diretamente apenas enquanto CORS e o
transporte utilizado forem compatíveis. A primeira engine Web testada, CIO,
tentava usar `node:net` e falhava antes da requisição.

## Decisão

Usar explicitamente a engine Ktor `Js`, baseada em Fetch, para JS e WasmJS.
Manter a chamada direta à AniList enquanto CORS continuar comprovadamente
viável. Não introduzir proxy sem necessidade demonstrada.

Logging HTTP permanece desabilitado no Web, e a aplicação não envia
`Authorization` à AniList para as operações públicas atuais.

## Consequências

- o fluxo permanece `UI → Repository → API → Ktor → AniList`;
- disponibilidade e política CORS externas continuam sendo dependências;
- eventual bloqueio futuro pode exigir proxy no `mykytadu-api`;
- a decisão precisa de smoke tests reais no navegador, não apenas testes JVM.

## Evidências

JS e WasmJS concluíram preflight e `POST` com sucesso, converteram a resposta em
modelos de domínio e carregaram uma capa. Nenhum header `Authorization` foi
observado.

## Critérios de revisão

Reavaliar diante de mudança de CORS, necessidade de segredo, rate limiting
centralizado, composição de dados ou disponibilidade controlada pelo backend.
