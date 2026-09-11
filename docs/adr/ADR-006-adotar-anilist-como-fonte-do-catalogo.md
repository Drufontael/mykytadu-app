# ADR-006 — Adotar a AniList como fonte do catálogo

> **Estado:** Aprovado
> **Data:** 2026-09-02
> **Registro:** retrospectivo
> **Sprint relacionada:** S4

## Contexto

Pesquisa e detalhes precisam de uma fonte externa de metadados de anime. O
provedor deve oferecer paginação, títulos alternativos, imagens, estúdios,
trailer, relações e acesso compatível com o cliente Ktor.

## Opções consideradas

- **AniList GraphQL API v2:** campos necessários, consultas seletivas,
  paginação e acesso público sem autenticação;
- **Jikan:** cobertura útil, porém API não oficial do MyAnimeList e com
  limitações diferentes de atualização e uso;
- **Kitsu:** contrato e cobertura menos alinhados aos detalhes necessários;
- **MyAnimeList oficial:** exige autenticação e não trouxe vantagem necessária
  ao escopo inicial.

## Decisão

Usar a AniList GraphQL API v2 como fonte externa inicial do catálogo. `Media.id`
é o identificador externo principal e `Media.idMal` permanece auxiliar e
opcional.

A AniList fornece metadados; não é a persistência da biblioteca pessoal nem a
fonte de identidade do MykytaDu.

## Consequências

- operações usam GraphQL sobre `POST`;
- falhas HTTP e GraphQL precisam ser tratadas separadamente;
- DTOs e enums externos são convertidos antes do domínio;
- campos opcionais e coleções vazias permanecem legítimos;
- rate limits e termos do provedor precisam ser observados;
- uma troca futura de provedor ocorre atrás de repositories e mapeadores.

## Evidências

As operações de pesquisa e detalhes foram inspecionadas e testadas. A camada de
comunicação, os mapeadores e o `AnimeRepository` usam os contratos documentados
em [`api-externa-anilist.md`](../api-externa-anilist.md).

## Critérios de revisão

Reavaliar se termos, disponibilidade, limites, campos essenciais ou custo de
manutenção se tornarem incompatíveis com o produto.
