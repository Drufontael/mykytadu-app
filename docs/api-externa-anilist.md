# MykytaDu — Escolha da API Externa de Animes

> Registro da decisão arquitetural da Sprint 4.2 — Seleção e documentação da API pública de animes.

## 1. Status da decisão

| Item | Valor |
|---|---|
| Status | Aprovada |
| Data | 2026-09-02 |
| Provedor | AniList |
| API | AniList GraphQL API v2 |
| Endpoint | `https://graphql.anilist.co` |
| Protocolo | GraphQL sobre HTTP |
| Método | `POST` |
| Formato | JSON |
| Autenticação para catálogo público | Não necessária |
| Identificador externo principal | `Media.id` |
| Identificador externo auxiliar | `Media.idMal` |

## 2. Contexto

O MykytaDu necessita de uma fonte externa de catálogo para sustentar inicialmente:

- Sprint 8 — pesquisa paginada de animes;
- Sprint 9 — exibição dos detalhes de um anime.

A biblioteca pessoal, a autenticação do MykytaDu e a tradução de conteúdo permanecem responsabilidades próprias do projeto. A AniList será utilizada como provedora de metadados de catálogo, não como persistência primária dos dados do usuário.

## 3. Decisão

A **AniList GraphQL API v2** foi escolhida como fonte principal de catálogo.

A decisão foi baseada em:

- cobertura dos campos previstos nas Sprints 8 e 9;
- possibilidade de solicitar apenas os campos necessários;
- obtenção de dados relacionados em uma única operação;
- suporte nativo a paginação;
- disponibilidade de títulos em romaji, inglês e idioma nativo;
- disponibilidade de capas, banners, estúdios, trailer e relações;
- ausência de autenticação para consultas públicas de catálogo;
- integração possível com o Ktor Client já existente.

Foram realizados testes manuais no Apollo Studio com as duas operações inicialmente necessárias. Os resultados confirmaram a disponibilidade dos campos esperados e também demonstraram que diversos campos podem ser nulos ou vazios.

## 4. Alternativas consideradas

### 4.1 Jikan

A Jikan oferece uma API REST não oficial baseada em dados públicos do MyAnimeList.

Vantagens consideradas:

- consumo REST simples;
- ausência de autenticação;
- respostas diretas e documentação ampla;
- grande cobertura de dados do MyAnimeList.

Motivos para não ser escolhida como principal:

- uma tela detalhada pode exigir várias requisições;
- dependência indireta do MyAnimeList e de sua disponibilidade;
- limites por segundo mais relevantes para carregamentos compostos;
- menor controle sobre os campos devolvidos;
- ausência de benefício decisivo diante da flexibilidade da AniList.

### 4.2 Kitsu

A Kitsu oferece uma API REST baseada no padrão JSON:API.

Motivos para não ser escolhida:

- respostas e relacionamentos mais verbosos;
- paginação normalmente limitada a grupos pequenos;
- documentação distribuída entre versões e formatos;
- ausência de vantagem funcional clara sobre a AniList.

### 4.3 MyAnimeList API oficial

A API oficial do MyAnimeList exige registro de aplicação e credenciais, além de OAuth para operações relacionadas ao usuário.

Motivos para não ser escolhida neste momento:

- introdução prematura de credenciais;
- maior acoplamento com contas MyAnimeList;
- conflito com o planejamento de uma biblioteca e autenticação próprias;
- nenhuma vantagem necessária para as Sprints 8 e 9.

Ela poderá ser reavaliada futuramente como integração opcional de importação ou sincronização.

## 5. Escopo inicial da integração

Foram definidas duas operações:

```text
SearchAnime(search, page, perPage)
    → página de resultados resumidos

GetAnimeDetails(id)
    → informações completas do anime selecionado
```

Operações de tendências, temporada atual, lançamentos futuros ou calendário não fazem parte desta decisão inicial. Elas poderão ser adicionadas quando a Home possuir requisitos consolidados.

## 6. Operação de pesquisa

### 6.1 Objetivo

Pesquisar títulos de anime e devolver uma página de resultados com informações suficientes para identificação e exibição em lista ou grade.

### 6.2 Query validada

```graphql
query SearchAnime(
    $search: String!
    $page: Int!
    $perPage: Int!
) {
    Page(page: $page, perPage: $perPage) {
        pageInfo {
            currentPage
            lastPage
            hasNextPage
            perPage
            total
        }

        media(
            search: $search
            type: ANIME
            sort: SEARCH_MATCH
        ) {
            id
            idMal

            title {
                romaji
                english
                native
            }

            coverImage {
                large
                color
            }

            format
            status
            episodes
            season
            seasonYear
            averageScore
        }
    }
}
```

### 6.3 Variables utilizadas no teste

```json
{
  "search": "Naruto",
  "page": 1,
  "perPage": 10
}
```

### 6.4 Estrutura observada da resposta

```json
{
  "data": {
    "Page": {
      "pageInfo": {
        "currentPage": 1,
        "lastPage": 500,
        "hasNextPage": true,
        "perPage": 10,
        "total": 5000
      },
      "media": [
        {
          "id": 20,
          "idMal": 20,
          "title": {
            "romaji": "NARUTO",
            "english": "Naruto",
            "native": "NARUTO -ナルト-"
          },
          "coverImage": {
            "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx20-dE6UHbFFg1A5.jpg",
            "color": "#e47850"
          },
          "format": "TV",
          "status": "FINISHED",
          "episodes": 220,
          "season": "FALL",
          "seasonYear": 2002,
          "averageScore": 80
        },
        {
          "id": 162561,
          "idMal": 54688,
          "title": {
            "romaji": "NARUTO (2026)",
            "english": null,
            "native": "NARUTO -ナルト- (2026)"
          },
          "coverImage": {
            "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx162561-Uk4eZAwcmcWk.jpg",
            "color": "#43bbe4"
          },
          "format": "TV",
          "status": "NOT_YET_RELEASED",
          "episodes": 4,
          "season": null,
          "seasonYear": null,
          "averageScore": null
        }
      ]
    }
  }
}
```

O teste completo retornou dez resultados, incluindo séries, filmes, especiais e OVA. O recorte acima preserva os casos relevantes para documentar o contrato e sua nulabilidade.

### 6.5 Decisões da pesquisa

- `Media.id` será usado para abrir a consulta de detalhes.
- `idMal` será preservado como referência externa opcional.
- os três formatos de título serão mantidos no DTO remoto;
- `description` não será solicitada na pesquisa inicial;
- `sort: SEARCH_MATCH` prioriza a relevância textual;
- a paginação será orientada por `pageInfo.hasNextPage`;
- `page` começa em `1`;
- o tamanho inicial testado foi `10`, mas o valor de produção deverá ser centralizado;
- a pesquisa não deve inferir existência de próxima página pelo tamanho da lista;
- strings vazias não devem gerar requisição.

### 6.6 Nulabilidade observada

O teste comprovou que podem ser nulos:

- `title.english`;
- `season`;
- `seasonYear`;
- `averageScore`.

Outros campos também deverão respeitar a nulabilidade declarada pelo schema da AniList. DTOs remotos não devem tornar obrigatórios campos que a API declara ou demonstra como opcionais.

## 7. Operação de detalhes

### 7.1 Objetivo

Obter pelo identificador AniList os dados necessários para a tela de detalhes prevista na Sprint 9.

### 7.2 Query validada

```graphql
query GetAnimeDetails($id: Int!) {
    Media(id: $id, type: ANIME) {
        id
        idMal

        title {
            romaji
            english
            native
        }

        synonyms
        description(asHtml: false)

        coverImage {
            extraLarge
            large
            color
        }

        bannerImage
        format
        status
        source
        episodes
        duration
        season
        seasonYear
        countryOfOrigin
        isAdult

        startDate {
            year
            month
            day
        }

        endDate {
            year
            month
            day
        }

        genres
        averageScore
        meanScore
        popularity
        favourites

        studios(isMain: true) {
            nodes {
                id
                name
                isAnimationStudio
            }
        }

        trailer {
            id
            site
            thumbnail
        }

        nextAiringEpisode {
            episode
            airingAt
            timeUntilAiring
        }

        relations {
            edges {
                relationType

                node {
                    id
                    type
                    format
                    status

                    title {
                        romaji
                        english
                        native
                    }

                    coverImage {
                        medium
                    }
                }
            }
        }

        externalLinks {
            id
            site
            url
            type
        }
    }
}
```

### 7.3 Variables utilizadas no teste

```json
{
  "id": 21579
}
```

### 7.4 Resultado observado

O teste retornou o especial `BORUTO: NARUTO THE MOVIE - Naruto ga Hokage ni Natta Hi`.

| Grupo | Resultado observado |
|---|---|
| Identificadores | `id: 21579`, `idMal: 32365` |
| Títulos | romaji, inglês e nativo preenchidos |
| Sinônimos | lista vazia |
| Descrição | preenchida |
| Capa | `extraLarge`, `large` e cor preenchidos |
| Banner | preenchido |
| Formato | `SPECIAL` |
| Status | `FINISHED` |
| Fonte | `MANGA` |
| Episódios | `1` |
| Duração | `10` minutos |
| Temporada | `SUMMER 2016` |
| País de origem | `JP` |
| Conteúdo adulto | `false` |
| Datas | início e fim completos |
| Gêneros | `Action`, `Comedy` |
| Nota | `averageScore: 70`, `meanScore: 70` |
| Popularidade | `30687` |
| Favoritos | `313` |
| Estúdios principais | lista vazia |
| Trailer | `null` |
| Próximo episódio | `null` |
| Relações | sequência, prequela, adaptação e alternativa |
| Links externos | lista vazia |

### 7.5 Exemplo das relações retornadas

```json
{
  "relations": {
    "edges": [
      {
        "relationType": "SEQUEL",
        "node": {
          "id": 21220,
          "type": "ANIME",
          "format": "MOVIE",
          "status": "FINISHED",
          "title": {
            "romaji": "BORUTO: NARUTO THE MOVIE",
            "english": "Boruto: Naruto the Movie",
            "native": "BORUTO -NARUTO THE MOVIE-"
          }
        }
      },
      {
        "relationType": "PREQUEL",
        "node": {
          "id": 1735,
          "type": "ANIME",
          "format": "TV",
          "status": "FINISHED",
          "title": {
            "romaji": "NARUTO: Shippuuden",
            "english": "Naruto: Shippuden",
            "native": "NARUTO -ナルト- 疾風伝"
          }
        }
      },
      {
        "relationType": "ADAPTATION",
        "node": {
          "id": 87402,
          "type": "MANGA",
          "format": "ONE_SHOT",
          "status": "FINISHED"
        }
      },
      {
        "relationType": "ALTERNATIVE",
        "node": {
          "id": 97938,
          "type": "ANIME",
          "format": "TV",
          "status": "FINISHED"
        }
      }
    ]
  }
}
```

### 7.6 Nulabilidade e coleções vazias

O resultado demonstra que uma resposta válida pode conter:

- `trailer: null`;
- `nextAiringEpisode: null`;
- `studios.nodes: []`;
- `externalLinks: []`;
- `synonyms: []`.

Essas situações não devem ser tratadas automaticamente como erro de comunicação. A futura UI deverá omitir ou adaptar seções sem conteúdo.

Datas da AniList também podem ser parciais. `year`, `month` e `day` deverão permanecer opcionais no DTO remoto.

## 8. Correspondência com a Sprint 9

| Requisito | Campo AniList | Observação |
|---|---|---|
| Capa | `coverImage.extraLarge` | Pode utilizar `large` como alternativa |
| Banner | `bannerImage` | Deve ser opcional |
| Sinopse | `description(asHtml: false)` | Pode exigir normalização posterior |
| Gêneros | `genres` | Lista de strings |
| Nota | `averageScore` | Pode ser nula |
| Episódios | `episodes` | Pode ser nulo para obras futuras ou incompletas |
| Estúdio | `studios(isMain: true).nodes` | Pode retornar lista vazia ou múltiplos itens |
| Temporada | `season`, `seasonYear` | Ambos podem ser nulos |
| Trailer | `trailer` | Pode ser nulo; `site` não deve ser presumido |

## 9. Títulos

Os três títulos serão preservados no contrato remoto:

- `romaji`;
- `english`;
- `native`.

A seleção do título exibido não deve acontecer no DTO da API. Uma regra inicial possível é:

```text
english
    ↓
romaji
    ↓
native
```

A regra definitiva deverá considerar futuramente a preferência de idioma do usuário.

## 10. Descrição e tradução

A busca inicial não solicitará `description`.

A operação de detalhes usará:

```graphql
description(asHtml: false)
```

O texto pode conter marcação própria da AniList, quebras de linha, spoilers ou conteúdo somente em inglês. A normalização visual e a tradução não pertencem à Sprint 4.

A tradução permanece planejada para a Sprint 15 e não deve alterar o contrato externo original.

## 11. Envelope GraphQL e erros

As respostas GraphQL podem possuir:

```json
{
  "data": {},
  "errors": []
}
```

O cliente deverá futuramente diferenciar:

- falha HTTP;
- timeout;
- indisponibilidade de rede;
- resposta HTTP `429`;
- falha de serialização;
- `errors` GraphQL;
- resposta parcial com `data` e `errors`;
- ausência de `Page` ou `Media` dentro de `data`.

Uma resposta HTTP bem-sucedida não garante que a operação GraphQL tenha sido concluída sem erros.

A política inicial proposta é:

- rejeitar pesquisa parcial quando `Page` estiver ausente;
- rejeitar detalhes quando `Media` estiver ausente;
- não transformar campos opcionais nulos ou coleções vazias em erro;
- preservar mensagens GraphQL para diagnóstico sem expor detalhes técnicos diretamente à UI.

A implementação do resultado padronizado pertence às tasks posteriores da Sprint 4.

## 12. Paginação

A paginação seguirá `PageInfo`:

- primeira página: `1`;
- próxima página somente quando `hasNextPage` for `true`;
- `perPage` deverá ser centralizado;
- `total` e `lastPage` serão preservados;
- uma nova pesquisa reinicia a página para `1`;
- não será inferida próxima página apenas pelo tamanho da lista.

Debounce, cancelamento de pesquisa anterior e estados da interface pertencem às Sprints 7 e 8.

## 13. Autenticação

Consultas públicas de catálogo não exigem autenticação.

OAuth somente será considerado se o projeto passar a acessar dados particulares ou executar mutações em nome de uma conta AniList. Essa possibilidade não faz parte do escopo atual, pois o MykytaDu terá autenticação e biblioteca próprias.

A preparação genérica do cliente para autenticação futura não deve introduzir uma dependência obrigatória de OAuth da AniList.

## 14. Limites e uso responsável

A documentação da AniList informa um limite regular de requisições e prevê redução temporária durante instabilidade.

A integração deverá:

- interpretar headers de limite quando disponíveis;
- tratar HTTP `429`;
- respeitar `Retry-After`, quando fornecido;
- evitar consultas repetidas desnecessárias;
- usar cache futuro apenas como otimização;
- não replicar integralmente a base da AniList;
- revisar os termos antes de qualquer monetização ou mudança relevante de uso.

Referências:

- [AniList API Docs](https://docs.anilist.co/)
- [Terms of Use](https://docs.anilist.co/guide/terms-of-use)
- [Rate Limiting](https://docs.anilist.co/guide/rate-limiting)
- [GraphQL Reference](https://docs.anilist.co/reference/)

## 15. Impacto arquitetural

A escolha da AniList implica:

- requisições `POST` para um único endpoint;
- envelope contendo `query` e `variables`;
- DTO genérico para resposta GraphQL;
- tipos remotos específicos para pesquisa e detalhes;
- tratamento simultâneo de erros HTTP e GraphQL;
- enums externos que não devem vazar diretamente para o domínio;
- separação entre DTO remoto e modelo de domínio nas sprints posteriores;
- preservação da configuração compartilhada do Ktor em `commonMain`;
- nenhuma necessidade imediata de uma biblioteca GraphQL dedicada.

O `AnimeApi` deverá encapsular GraphQL. Repositories, domínio e UI não deverão conhecer queries, variables ou o envelope da AniList.

## 16. Fora do escopo desta decisão

- implementação do `AnimeApi`;
- criação definitiva dos DTOs Kotlin;
- modelos de domínio;
- repositories;
- ViewModels;
- estados de UI;
- cache;
- tradução;
- OAuth AniList;
- sincronização com listas AniList;
- fallback para outro provedor;
- integração simultânea com Jikan.

## 17. Riscos aceitos

| Risco | Impacto | Mitigação planejada |
|---|---|---|
| Dependência de serviço externo | Indisponibilidade afeta pesquisa e detalhes | Tratamento padronizado de falhas e cache futuro |
| Rate limit | Requisições podem receber `429` | Headers, backoff e redução de chamadas repetidas |
| Campos opcionais | Seções podem não possuir dados | DTOs anuláveis e UI adaptativa |
| Mudanças no schema | Desserialização ou queries podem falhar | Testes de contrato e queries centralizadas |
| Erros GraphQL com HTTP `200` | Sucesso HTTP pode mascarar falha | Validar `errors` e presença do dado principal |
| Conteúdo textual com marcação | Sinopse pode exigir limpeza | Normalização em camada apropriada |
| Uso indevido como base própria | Possível violação dos termos | Cache limitado e revisão periódica dos termos |

## 18. Critérios de aceite da Sprint 4.2

- [x] Provedor escolhido.
- [x] Endpoint e protocolo identificados.
- [x] Necessidade de autenticação compreendida.
- [x] Operação de pesquisa definida e testada.
- [x] Operação de detalhes definida e testada.
- [x] Paginação definida.
- [x] Campos das Sprints 8 e 9 localizados.
- [x] Nulabilidade observada registrada.
- [x] Particularidades de erros GraphQL identificadas.
- [x] Restrições de uso e cache reconhecidas.
- [x] Alternativas avaliadas e justificadamente rejeitadas.

## 19. Condições para reavaliação

A escolha deverá ser reavaliada se:

- os termos da AniList se tornarem incompatíveis com o projeto;
- a API deixar de fornecer campos essenciais;
- houver instabilidade recorrente incompatível com o produto;
- os limites impedirem o fluxo normal mesmo com cache responsável;
- o projeto passar a exigir sincronização nativa com outro provedor;
- a monetização ultrapassar as condições de uso permitidas;
- o custo de manutenção do GraphQL superar seus benefícios observados.

## 20. Próximo passo

Com a escolha consolidada, a próxima task deverá configurar a comunicação operacional necessária para GraphQL:

- headers comuns;
- timeouts;
- logging seguro;
- envelope de requisição;
- envelope de resposta;
- tratamento de erros HTTP e GraphQL;
- testes determinísticos com engine mockado.

Não implementar as funcionalidades completas das Sprints 8 e 9 durante a configuração da camada de comunicação.
