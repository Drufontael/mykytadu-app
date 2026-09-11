# ADR-002 — Isolar o domínio dos contratos AniList

> **Estado:** Aprovado
> **Registro:** retrospectivo
> **Sprints relacionadas:** S4 e S5

## Contexto

A AniList expõe GraphQL, DTOs, enums e nulabilidade próprios. Permitir que esses
contratos atravessem a camada de dados acoplaria UI e regras do produto à API
externa.

## Decisão

DTOs, GraphQL, Ktor, `NetworkResult` e demais tipos remotos permanecem na camada
de dados. Mapeadores explícitos convertem a resposta em modelos e resultados de
domínio. Consumidores acessam comunicação externa por repositories.

`AniListAnimeId` mantém significado distinto de IDs locais e futuros IDs do
backend.

## Consequências

- mudanças remotas ficam concentradas em DTOs e mapeadores;
- enums desconhecidos podem ser preservados como `UNKNOWN`;
- nulabilidade e coleções vazias são representadas sem texto de interface;
- UI não depende de protocolo ou fornecedor externo.

## Evidências

A Sprint 5 implementou e testou modelos, paginação, falhas, mapeadores e
`AnimeRepository` sem dependências de infraestrutura na API do domínio.

## Critérios de revisão

Uma troca de provedor pode adicionar adapters, mas não deve remover a fronteira
sem evidência de que o domínio deixou de ter significado próprio.
